package io.shreyash.rush.processor

import com.charleskorn.kaml.Yaml
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.w3c.dom.Attr
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.SAXException
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import kotlin.io.path.exists
import io.shreyash.rush.processor.model.RushYaml
import shaded.org.json.JSONArray
import shaded.org.json.JSONException
import shaded.org.json.JSONObject

/**
 * [ExtensionProcessor] is designed to pick only the classes that declare at least one of the block
 * annotations. So, in case there's no block annotation, the CLI would crash, as there won't be any
 * annotation processor generated info file to further process by the CLI. Therefore, to prevent
 * this, we check if the info files exists and that they are up-to-date. If they aren't, we run this
 * method and generate (new) info files w/o any blocks.
 */
@Throws(IOException::class, ParserConfigurationException::class, SAXException::class)
fun main(args: Array<String>) {
    val generator = InfoFilesGenerator(
        projectRoot = args[0],
        extVersion = args[1],
        type = args[2],
        outputDir = args[3]
    )
    generator.generateComponentsJson()
    generator.generateBuildInfoJson()
}

class InfoFilesGenerator(
    private val projectRoot: String,
    private val extVersion: String,
    private val type: String,
    private val outputDir: String
) {
    private val store = BlockStore.instance

    /**
     * Generates the components.json file.
     *
     * @throws IOException
     * @throws JSONException
     */
    @Throws(IOException::class, JSONException::class)
    fun generateComponentsJson() {
        val yaml = metadataFile()

        val componentsJsonArray = JSONArray()
        val primaryObj = JSONObject()

        // These are always the same for all extensions.
        primaryObj
            .put("external", "true")
            .put("categoryString", "EXTENSION")
            .put("showOnPalette", "true")
            .put("nonVisible", "true")

        primaryObj
            .put("name", yaml.name)
            .put("type", this.type)
            .put("version", this.extVersion)
            .put("androidMinSdk", yaml.minSdk.coerceAtLeast(7))
            .put("versionName", yaml.version.name)
            .put("helpUrl", yaml.homepage)
            .put("licenseName", yaml.license)
            .put("helpString", parseMdString(yaml.description))

        val urlPattern = Pattern.compile(
            """https?://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()!@:%_+.~#?&//=]*)"""
        )
        val icon = yaml.assets.icon
        if (urlPattern.matcher(icon).find()) {
            primaryObj.put("iconName", icon)
        } else {
            primaryObj.put("iconName", "aiwebres/$icon")
        }

        val time = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
        primaryObj.put("dateBuilt", time)

        // Put events
        val events = this.store.events.map { it.asJsonObject() }
        primaryObj.put("events", events)

        // Put methods
        val methods = this.store.methods.map { it.asJsonObject() }
        primaryObj.put("methods", methods)

        // Put properties
        val properties = this.store.properties.map { it.asJsonObject() }
        primaryObj.put("blockProperties", properties)

        // Put designer properties
        val designerProperties = this.store.designerProperties.map { it.asJsonObject() }
        primaryObj.put("properties", designerProperties)

        componentsJsonArray.put(primaryObj)

        val componentsJsonFile = Paths.get(outputDir, "components.json").toFile()
        componentsJsonFile.writeText(componentsJsonArray.toString())
    }

    /**
     * Generate component_build_infos.json file.
     *
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    @Throws(IOException::class, ParserConfigurationException::class, SAXException::class)
    fun generateBuildInfoJson() {
        val yaml = metadataFile()

        val buildInfoJsonArray = JSONArray()
        val primaryObj = JSONObject()

        primaryObj.put("type", type)
        primaryObj.put("androidMinSdk", listOf(yaml.minSdk.coerceAtLeast(7)))

        // Put assets
        val assets = yaml.assets.other.map { it.trim() }
        primaryObj.put("assets", assets)

        // Before the annotation processor runs, the CLI merges the manifests of all the AAR deps
        // with the extension's main manifest and stores the output at [outputDir]/MergedManifest.xml.
        // So, if the merged manifest is found use it instead of the main manifest.
        val manifest = if (Paths.get(outputDir, "MergedManifest.xml").exists()) {
            Paths.get(outputDir, "MergedManifest.xml").toFile()
        } else {
            Paths.get(projectRoot, "src", "AndroidManifest.xml").toFile()
        }

        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = builder.parse(manifest)

        // Put application elements
        val appElements = applicationElementsXmlString(doc)
        // We put all the elements under the activities tag. This let's us use the tags which aren't
        // yet added to AI2 and don't have a dedicated key in the build info JSON file.
        // The reason why this works is that AI compiler doesn't performs any checks on these
        // manifest arrays in the build info file, and just adds them to the final manifest file.
        primaryObj.put("activities", appElements)

        // Put permissions
        val nodes = doc.getElementsByTagName("uses-permission")
        val permissions = JSONArray()
        if (nodes.length != 0) {
            for (i in 0 until nodes.length) {
                permissions.put(generateXmlString(nodes.item(i), "manifest"))
            }
        }
        primaryObj.put("permissions", permissions)

        buildInfoJsonArray.put(primaryObj)

        val buildInfoJsonFile = Paths.get(outputDir, "component_build_infos.json").toFile()
        buildInfoJsonFile.writeText(buildInfoJsonArray.toString())
    }

    /**
     * Get metadata file
     *
     * @return The rush.yml file's data
     * @throws IOException If the input can't be read for some reason.
     */
    private fun metadataFile(): RushYaml {
        val rushYml = if (Paths.get(projectRoot, "rush.yml").exists()) {
            Paths.get(projectRoot, "rush.yml").toFile()
        } else {
            Paths.get(projectRoot, "rush.yaml").toFile()
        }

        return Yaml.default.decodeFromStream(RushYaml.serializer(), FileInputStream(rushYml))
    }

    /** Parses [markdown] and returns it. */
    private fun parseMdString(markdown: String): String {
        val extensionList = listOf(
            // Adds ability to convert URLs to clickable links
            AutolinkExtension.create(),
            // Adds ability to create task lists.
            TaskListItemsExtension.create()
        )

        val parser = Parser.Builder().extensions(extensionList).build()
        val renderer = HtmlRenderer.builder()
            .extensions(extensionList)
            .softbreak("<br>")
            .build()

        return renderer.render(parser.parse(markdown))
    }

    /**
     * Returns a JSON array of specific XML elements from the given list of nodes.
     *
     * @param node   A XML node, for eg., <service>
     * @param parent Name of the node who's child nodes we want to generate. This is required because
     *               getElementsByTag() method returns all the elements that satisfy the name.
     * @return A JSON array containing XML elements
     */
    private fun generateXmlString(node: Node, parent: String): String {
        // Unlike other elements, permissions aren't stored as XML strings in the build info JSON
        // file. Only the name of the permission (android:name) is stored.
        if (node.nodeName == "uses-permission") {
            val permission = node.attributes.getNamedItem("android:name")
            return if (permission != null) {
                permission.nodeValue
            } else {
                throw DOMException(
                    1.toShort(),
                    "ERR No android:name attribute found in <uses-permission>"
                )
            }
        }

        val sb = StringBuilder()
        if (node.nodeType == Node.ELEMENT_NODE && node.parentNode.nodeName == parent) {
            val element = node as Element
            val tagName = element.tagName
            sb.append("<$tagName ")

            if (element.hasAttributes()) {
                val attributes = element.attributes
                for (i in 0 until attributes.length) {
                    if (attributes.item(i).nodeType == Node.ATTRIBUTE_NODE) {
                        val attribute = attributes.item(i) as Attr
                        // Drop the "tools:sth" attributes.
                        if (!attribute.nodeName.contains("tools:")) {
                            sb.append("${attribute.nodeName} = \"${attribute.nodeValue}\" ")
                        }
                    }
                }
            }

            if (element.hasChildNodes()) {
                sb.append(" >\n")
                val children = element.childNodes
                for (j in 0 until children.length) {
                    sb.append(generateXmlString(children.item(j), element.nodeName))
                }
                sb.append("</$tagName>\n")
            } else {
                sb.append("/>\n")
            }
        }

        return sb.toString()
    }

    /**
     * Stringifies all the XML elements under <application> and returns them as a list.
     *
     * @param doc   The AndroidManifest.xml document
     */
    private fun applicationElementsXmlString(doc: Document): List<String> {
        val validTags = listOf(
            "activity",
            "activity-alias",
            "meta-data",
            "provider",
            "service",
            "receiver",
            "uses-library"
        )

        val xmlStrings = validTags.map {
            val res = mutableListOf<String>()
            val elements = doc.getElementsByTagName(it)
            for (i in 0 until elements.length) {
                res.add(generateXmlString(elements.item(i), "application"))
            }
            res
        }.flatten()

        return xmlStrings.toList()
    }
}
