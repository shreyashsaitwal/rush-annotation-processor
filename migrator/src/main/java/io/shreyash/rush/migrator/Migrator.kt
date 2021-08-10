package io.shreyash.rush.migrator

import java.lang.annotation.Annotation as JAnnotation
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.google.appinventor.components.annotations.DesignerComponent
import com.google.appinventor.components.annotations.SimpleObject
import com.google.appinventor.components.annotations.UsesActivities
import com.google.appinventor.components.annotations.UsesActivityMetadata
import com.google.appinventor.components.annotations.UsesApplicationMetadata
import com.google.appinventor.components.annotations.UsesAssets
import com.google.appinventor.components.annotations.UsesBroadcastReceivers
import com.google.appinventor.components.annotations.UsesContentProviders
import com.google.appinventor.components.annotations.UsesLibraries
import com.google.appinventor.components.annotations.UsesPermissions
import com.google.appinventor.components.annotations.UsesServices
import com.google.auto.service.AutoService
import java.io.IOException
import java.nio.file.Paths
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import io.shreyash.rush.migrator.util.XmlUtil
import io.shreyash.rush.model.Assets
import io.shreyash.rush.model.Build
import io.shreyash.rush.model.Release
import io.shreyash.rush.model.RushYaml
import io.shreyash.rush.model.Version

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.google.appinventor.components.annotations.*")
class Migrator : AbstractProcessor() {
    override fun process(set: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        val outputDir = processingEnv.options["outputDir"]
        val messager = processingEnv.messager

        roundEnv.getElementsAnnotatedWith(DesignerComponent::class.java).forEach {
            val simpleObject = it.getAnnotation(SimpleObject::class.java)
            if (simpleObject.external) {
                messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "External component class \"" + it.simpleName + "\" detected."
                )
                try {
                    generateAndroidManifest(it, outputDir)
                    generateRushYml(it, outputDir)
                } catch (e: TransformerException) {
                    e.printStackTrace()
                } catch (e: ParserConfigurationException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return false
    }

    /**
     * Generates rush.yml for {@param comp}.
     *
     * @param comp      the element for which rush.yml is to be produced
     * @param outputDir the path where the generated rush.yml is to be stored
     */
    @Throws(IOException::class)
    private fun generateRushYml(comp: Element, outputDir: String?) {
        val designerComponent = comp.getAnnotation(DesignerComponent::class.java)

        val version = Version(
            name = designerComponent.versionName,
            number = "auto",
        )

        val otherAssets = comp.getAnnotation(UsesAssets::class.java)?.fileNames
        val assets = Assets(
            icon = designerComponent.iconName,
            other = otherAssets?.split(",")?.map { it.trim() } ?: listOf()
        )

        val extName = comp.simpleName.toString()
        val deps =
            comp.getAnnotation(UsesLibraries::class.java)?.libraries?.split(",")?.map { it.trim() }
        val build = Build(
            release = Release(optimize = true)
        )

        val rushYaml = RushYaml(
            name = extName,
            description = designerComponent.description,
            version = version,
            assets = assets,
            build = build,
            minSdk = designerComponent.androidMinSdk,
            deps = deps ?: listOf()
        )

        val yamlMapper = Yaml(
            configuration = YamlConfiguration(
                encodeDefaults = false,
                breakScalarsAt = 80
            )
        )
        val yamlString = yamlMapper.encodeToString(RushYaml.serializer(), rushYaml)
        Paths
            .get(outputDir!!, "rush-$extName.yml")
            .toFile()
            .writeText(yamlString)
    }

    /**
     * Generates AndroidManifest.xml for {@param comp}
     *
     * @param comp      the element for which AndroidManifest.xml is to be generated
     * @param outputDir the path to where the generated manifest file is to br stored.
     */
    @Throws(TransformerException::class, ParserConfigurationException::class)
    private fun generateAndroidManifest(comp: Element, outputDir: String?) {
        val dbf = DocumentBuilderFactory.newInstance()
        val documentBuilder = dbf.newDocumentBuilder()
        val doc = documentBuilder.newDocument()

        val root = doc.createElement("manifest")
        doc.appendChild(root)

        doc.xmlVersion = "1.0"
        val namespaceUri = "http://schemas.android.com/apk/res/android"

        val versionCodeAttr = doc.createAttributeNS(namespaceUri, "android:versionCode")
        versionCodeAttr.value = "1"
        root.setAttributeNode(versionCodeAttr)

        val versionNameAttr = doc.createAttributeNS(namespaceUri, "android:versionName")
        versionNameAttr.value = "1.0"
        root.setAttributeNode(versionNameAttr)

        val applicationElement = doc.createElement("application")
        root.appendChild(applicationElement)

        val permissions =
            comp.getAnnotation(UsesPermissions::class.java)?.permissionNames?.split(",")
        permissions?.forEach {
            val permissionEl = doc.createElement("uses-permission")
            val attr = doc.createAttributeNS(namespaceUri, "android:name")
            attr.value = it
            permissionEl.setAttributeNode(attr)
            root.appendChild(permissionEl)
        }

        val manifestAnnotations = listOf(
            comp.getAnnotation(UsesServices::class.java)?.services,
            comp.getAnnotation(UsesActivities::class.java)?.activities,
            comp.getAnnotation(UsesContentProviders::class.java)?.providers,
            comp.getAnnotation(UsesBroadcastReceivers::class.java)?.receivers,
            comp.getAnnotation(UsesActivityMetadata::class.java)?.metaDataElements,
            comp.getAnnotation(UsesApplicationMetadata::class.java)?.metaDataElements,
        )

        val xmlUtil = XmlUtil()
        manifestAnnotations.forEach { annotation ->
            annotation?.forEach {
                val el = xmlUtil.manifestElementForAnnotation(it as JAnnotation, doc, namespaceUri)
                applicationElement.appendChild(el)
            }
        }

        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")

        val manifestPath = Paths.get(outputDir!!, "manifest-" + comp.simpleName.toString() + ".xml")
        val streamResult = StreamResult(manifestPath.toFile())
        transformer.transform(DOMSource(doc), streamResult)
    }
}
