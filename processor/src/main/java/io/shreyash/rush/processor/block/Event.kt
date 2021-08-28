package io.shreyash.rush.processor.block

import com.google.appinventor.components.annotations.SimpleEvent
import io.shreyash.rush.processor.util.isCamelCase
import io.shreyash.rush.processor.util.isPascalCase
import shaded.org.json.JSONObject
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.tools.Diagnostic

class Event(element: Element, private val messager: Messager) : BlockWithParams(element) {
    init {
        runChecks()
    }

    override fun description() = this.element.getAnnotation(SimpleEvent::class.java).description

    override fun runChecks() {
        // Check method name
        if (!isPascalCase(name())) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Simple event \"" + name() + "\" should follow 'PascalCase' naming convention."
            )
        }

        // Check param names
        params().forEach {
            if (!isCamelCase(it.name)) {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Parameter \"" + it.name + "\" in simple event \"" + name() + "\" should " +
                            "follow 'camelCase' naming convention."
                )
            }
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
            .put("deprecated", this.deprecated().toString())
            .put("name", name())
            .put("description", description())

        val params = params().map {
            JSONObject()
                .put("name", it.name)
                .put("type", it.type)
        }
        eventJson.put("params", params)

        return eventJson
    }
}
