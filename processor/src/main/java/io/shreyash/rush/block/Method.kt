package io.shreyash.rush.block

import com.google.appinventor.components.annotations.SimpleFunction
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic
import io.shreyash.rush.util.isCamelCase
import io.shreyash.rush.util.isPascalCase
import shaded.org.json.JSONObject

class Method(element: Element, private val messager: Messager) : BlockWithParams(element) {
    private val element = element as ExecutableElement

    init {
        runChecks()
    }

    override fun description() = this.element.getAnnotation(SimpleFunction::class.java).description

    override fun runChecks() {
        // Check method name
        if (!isPascalCase(name())) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Simple function \"" + name() + "\" should follow 'PascalCase' naming convention."
            )
        }

        // Check param names
        params().forEach {
            if (!isCamelCase(it.name)) {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Parameter \"" + it.name + "\" in simple function \"" + name() + "\" should " +
                            "follow 'camelCase' naming convention."
                )
            }
        }
    }

    /**
     * @return JSON representation of this method.
     * {
     *  "name": "Foo",
     *  "description": "This is a description",
     *  "deprecated": "false",
     *  "returnType": "any",
     *  "params": [
     *    { "name": "bar", "type": "number" },
     *  ]
     * }
     */
    override fun asJsonObject(): JSONObject {
        val methodJson = JSONObject()
            .put("name", name())
            .put("description", description())
            .put("deprecated", this.deprecated().toString())

        // Here, null represents the return type is void. Return type for void methods don't need to
        // be specified
        if (returnType() != null) {
            methodJson.put("returnType", returnType())
        }

        val params = params().map {
            JSONObject()
                .put("name", it.name)
                .put("type", it.type)
        }
        methodJson.put("params", params)

        return methodJson
    }
}
