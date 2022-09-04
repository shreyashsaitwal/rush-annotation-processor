package io.shreyash.rush.processor.block

import com.google.appinventor.components.annotations.SimpleEvent
import io.shreyash.rush.processor.util.isCamelCase
import io.shreyash.rush.processor.util.isPascalCase
import shaded.org.json.JSONObject
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

class Event(
    element: Element,
    private val messager: Messager,
    private val elementUtils: Elements,
) : ParameterizedBlock(element) {
    init {
        runChecks()
    }

    override val description: String
        get() {
            val desc = this.element.getAnnotation(SimpleEvent::class.java).description.let {
                it.ifBlank {
                    elementUtils.getDocComment(element) ?: ""
                }
            }
            return desc
        }

    override fun runChecks() {
        // Check method name
        if (!isPascalCase(name)) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Simple event \"$name\" should follow 'PascalCase' naming convention."
            )
        }

        // Check param names
        params().forEach {
            if (!isCamelCase(it.name)) {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Parameter \"${it.name}\" in simple event \"$name\" should follow 'camelCase' naming convention."
                )
            }
        }

        if (description.isBlank()) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Simple event \"$name\" is missing a description."
            )
        }
    }

    /**
     * @return JSON representation of this event.
     *
     * JSON:
     * {
     *  "name": "Foo",
     *  "description": "This is a description",
     *  "deprecated": "false",
     *  "params": [
     *    { "name": "bar", "type": "number" },
     *  ]
     * }
     */
    override fun asJsonObject(): JSONObject {
        val eventJson = JSONObject()
            .put("deprecated", deprecated.toString())
            .put("name", name)
            .put("description", description)

        val params = params().map {
            val obj = JSONObject()
                .put("name", it.name)
                .put("type", it.type)
            it.helper?.apply {
                obj.put("helper", this.data.toJson())
            }
            obj
        }
        eventJson.put("params", params)

        return eventJson
    }
}
