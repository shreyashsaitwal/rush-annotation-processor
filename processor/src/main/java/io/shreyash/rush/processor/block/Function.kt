package io.shreyash.rush.processor.block

import com.google.appinventor.components.annotations.SimpleFunction
import io.shreyash.rush.processor.util.isCamelCase
import io.shreyash.rush.processor.util.isPascalCase
import shaded.org.json.JSONObject
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

class Function(
    element: Element,
    private val messager: Messager,
    private val elementUtils: Elements,
) : BlockWithParams(element) {
    init {
        runChecks()
    }

    override val description: String
        get() {
            val desc = this.element.getAnnotation(SimpleFunction::class.java).description.let {
                if (it.isBlank()) {
                    elementUtils.getDocComment(element) ?: ""
                } else {
                    it
                }
            }
            return desc
        }

    override fun runChecks() {
        // Check method name
        if (!isPascalCase(name)) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Simple function \"$name\" should follow 'PascalCase' naming convention."
            )
        }

        // Check param names
        params().forEach {
            if (!isCamelCase(it.name)) {
                messager.printMessage(
                    Diagnostic.Kind.WARNING,
                    "Parameter \"${it.name}\" in simple function \"$name\" should follow 'camelCase' naming convention."
                )
            }
        }

        if (description.isBlank()) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Simple function \"$name\" is missing a description."
            )
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
            .put("name", name)
            .put("description", description)
            .put("deprecated", deprecated.toString())

        // Here, null represents the return type is void. Return type for void methods don't need to
        // be specified.
        returnType()?.apply {
            methodJson.put("returnType", this)
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
