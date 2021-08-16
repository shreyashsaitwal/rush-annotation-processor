package io.shreyash.rush.processor

import com.google.appinventor.components.annotations.SimpleEvent
import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.annotations.SimpleProperty
import com.google.auto.service.AutoService
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import io.shreyash.rush.processor.block.DesignerProperty
import io.shreyash.rush.processor.block.Event
import io.shreyash.rush.processor.block.Method
import io.shreyash.rush.processor.block.Property

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(
    "com.google.appinventor.components.annotations.SimpleEvent",
    "com.google.appinventor.components.annotations.SimpleFunction",
    "com.google.appinventor.components.annotations.SimpleProperty",
    "com.google.appinventor.components.annotations.DesignerProperty"
)
class ExtensionProcessor : AbstractProcessor() {
    private val store = BlockStore.instance
    private var isFirstRound = true

    private lateinit var messager: Messager
    private lateinit var org: String
    private lateinit var extName: String

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
    }

    override fun process(annotations: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        if (!this.isFirstRound || this.processingEnv.options["root"] == null) {
            return true
        }

        this.isFirstRound = false
        this.org = processingEnv.options["org"]!!
        this.extName = processingEnv.options["extName"]!!

        // Process all SimpleEvents
        roundEnv.getElementsAnnotatedWith(SimpleEvent::class.java)
            .filter {
                isInRightParent(it, "@SimpleEvent") && isPublic(it, "@SimpleEvent")
            }.map {
                store.putEvent(Event(it, messager))
            }

        // Process all SimpleFunctions
        roundEnv.getElementsAnnotatedWith(SimpleFunction::class.java)
            .filter {
                isInRightParent(it, "@SimpleFunction") && isPublic(it, "@SimpleFunction")
            }.map {
                store.putMethod(Method(it, messager))
            }

        // Process all SimpleProps
        roundEnv.getElementsAnnotatedWith(SimpleProperty::class.java)
            .filter {
                isInRightParent(it, "@SimpleProperty") && isPublic(it, "@SimpleProperty")
            }.map {
                store.putProperty(Property(it, messager))
            }

        // Process all DesignerProps
        roundEnv.getElementsAnnotatedWith(com.google.appinventor.components.annotations.DesignerProperty::class.java)
            .filter {
                isInRightParent(it, "@DesignerProperty") && isPublic(it, "@DesignerProperty")
            }.map {
                store.putDesignerProperty(DesignerProperty(it, messager))
            }

        generateInfoFiles()
        return false
    }

    /**
     * Generates the component info files (JSON).
     */
    private fun generateInfoFiles() {
        val root = processingEnv.options["root"]!!
        val version = processingEnv.options["version"]!!
        val output = processingEnv.options["output"]!!
        val type = "${this.org}.${this.extName}"

        val generator = InfoFilesGenerator(root, version, type, output)
        try {
            generator.generateComponentsJson()
            generator.generateBuildInfoJson()
        } catch (e: Throwable) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.message ?: e.stackTraceToString())
        }
    }

    /**
     * Returns `true` if the [element]'s parent class is [extName].
     */
    private fun isInRightParent(element: Element, annotationName: String): Boolean {
        val res = element.enclosingElement.simpleName.toString() == this.extName
        if (!res) {
            this.messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Annotation" + annotationName + "can't be used on element \"" + element.simpleName
                        + "\". It can only be used on members of class \"" + this.org + "." +
                        this.extName + "\"."
            )
        }
        return res
    }

    /**
     * Returns `true` if [element] is a public element.
     */
    private fun isPublic(element: Element, annotationName: String): Boolean {
        val isPublic = element.modifiers.contains(Modifier.PUBLIC)
        if (!isPublic) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Private element \"" + element.simpleName + "\" can't be annotated with" + annotationName + " ."
            )
        }
        return isPublic
    }
}
