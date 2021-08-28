package io.shreyash.rush.processor

import com.google.appinventor.components.annotations.Meta
import com.google.appinventor.components.annotations.SimpleEvent
import com.google.appinventor.components.annotations.SimpleFunction
import com.google.appinventor.components.annotations.SimpleProperty
import com.google.auto.service.AutoService
import io.shreyash.rush.processor.block.DesignerProperty
import io.shreyash.rush.processor.block.Event
import io.shreyash.rush.processor.block.Function
import io.shreyash.rush.processor.block.Property
import io.shreyash.rush.processor.model.Extension
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes(
    "com.google.appinventor.components.annotations.Meta",
    "com.google.appinventor.components.annotations.SimpleEvent",
    "com.google.appinventor.components.annotations.SimpleFunction",
    "com.google.appinventor.components.annotations.SimpleProperty",
    "com.google.appinventor.components.annotations.DesignerProperty"
)
class ExtensionProcessor : AbstractProcessor() {
    private var isFirstRound = true

    private lateinit var messager: Messager
    private lateinit var elementUtil: Elements

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        messager = processingEnv.messager
        elementUtil = processingEnv.elementUtils
    }

    override fun process(annotations: Set<TypeElement?>, roundEnv: RoundEnvironment): Boolean {
        if (!this.isFirstRound) {
            return true
        }
        this.isFirstRound = false

        val extensions = roundEnv.getElementsAnnotatedWith(Meta::class.java).map {
            processExtensionElement(it)
        }
        generateInfoFiles(extensions)

        return false
    }

    private fun processExtensionElement(element: Element): Extension {
        // Process simple events
        val events = element.enclosedElements
            .filter {
                it.getAnnotation(SimpleEvent::class.java) != null && isPublic(it)
            }.map { Event(it, this.messager) }

        // Process simple functions
        val functions = element.enclosedElements
            .filter {
                it.getAnnotation(SimpleFunction::class.java) != null && isPublic(it)
            }.map { Function(it, messager) }

        // Process simple properties
        val processedProperties = mutableListOf<Property>()
        val properties = element.enclosedElements
            .filter {
                it.getAnnotation(SimpleProperty::class.java) != null && isPublic(it)
            }.map {
                val property = Property(it, this.messager, processedProperties)
                processedProperties.add(property)
                property
            }

        // Process designer properties
        val designerProperties = element.enclosedElements
            .filter {
                it.getAnnotation(com.google.appinventor.components.annotations.DesignerProperty::class.java) != null
                        && isPublic(it)
            }.map {
                DesignerProperty(it, this.messager, properties)
            }

        val packageName = this.elementUtil.getPackageOf(element).qualifiedName.toString()
        val fqcn = "$packageName.${element.simpleName}"

        return Extension(
            element.getAnnotation(Meta::class.java),
            fqcn,
            events,
            functions,
            properties,
            designerProperties
        )
    }

    /** Generates the component info files (JSON). */
    private fun generateInfoFiles(extensions: List<Extension>) {
        val projectRoot = processingEnv.options["projectRoot"]!!
        val outputDir = processingEnv.options["outputDir"]!!

        val generator = InfoFilesGenerator(projectRoot, outputDir, extensions)
        try {
            generator.generateComponentsJson()
            generator.generateBuildInfoJson()
        } catch (e: Throwable) {
            messager.printMessage(Diagnostic.Kind.ERROR, e.message ?: e.stackTraceToString())
        }
    }

    /** @returns `true` if [element] is a public element. */
    private fun isPublic(element: Element): Boolean {
        val isPublic = element.modifiers.contains(Modifier.PUBLIC)
        if (!isPublic) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Element \"${element.simpleName}\" is private. It should be public to be visible in" +
                        " the blocks editor."
            )
        }
        return isPublic
    }
}
