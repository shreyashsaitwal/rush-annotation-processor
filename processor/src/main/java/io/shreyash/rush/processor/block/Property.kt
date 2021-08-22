package io.shreyash.rush.processor.block

import com.google.appinventor.components.annotations.SimpleProperty
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.tools.Diagnostic
import io.shreyash.rush.processor.BlockStore
import io.shreyash.rush.processor.util.convert
import io.shreyash.rush.processor.util.isPascalCase
import shaded.org.json.JSONObject

object PropertyAccessType {
    const val READ = "read-only"
    const val WRITE = "write-only"
    const val READ_WRITE = "read-write"
    const val INVISIBLE = "invisible"
}

class Property(element: Element, private val messager: Messager) : Block(element) {
    private val store = BlockStore.instance
    private val element = element as ExecutableElement

    private val accessType: String

    init {
        runChecks()
        accessType = accessType()
    }

    override fun description() = element.getAnnotation(SimpleProperty::class.java).description

    override fun runChecks() {
        if (!isPascalCase(name())) {
            messager.printMessage(
                Diagnostic.Kind.WARNING,
                "Designer property \"" + name() + "\" should follow 'PascalCase' naming convention."
            )
        }

        val isSetter = this.element.returnType.toString() == "void"
        val noOfParams = this.element.parameters.size

        // Total numbers of parameters for setters must be 1 and for getter must be 0.
        if (isSetter && noOfParams != 1) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "The total number of parameters allowed on the setter type simple property \"" +
                        name() + "\" is: 1"
            )
        } else if (!isSetter && noOfParams != 0) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "The total number of parameters allowed on the getter type simple property \"" +
                        name() + "\" is: 0"
            )
        }

        val partnerProp = this.store.properties.firstOrNull {
            it.name() == name() && it !== this
        }
        // Return types of getters and setters must match
        if (partnerProp != null && partnerProp.returnType() != returnType()) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                "Inconsistent types across getter and setter for simple property \"" + name() + "\"."
            )
        }
    }

    /**
     * @return If this is a setter type property, the type of the value it accepts, else if it is a
     * getter, it's return type.
     */
    override fun returnType(): String {
        val returnType = this.element.returnType.toString()

        // If the property is of type setter, the JSON property "type" is equal to the type of
        // parameter the setter expects.
        return if (returnType == "void") {
            val type = this.element.parameters[0].asType().toString()
            convert(type)
        } else {
            convert(returnType)
        }
    }

    /**
     * @return JSON representation of this property.
     * {
     *  "rw": "read-only",
     *  "deprecated": "false",
     *  "name": "Foo",
     *  "description": "",
     *  "type": "any"
     * }
     */
    override fun asJsonObject(): JSONObject = JSONObject()
        .put("name", name())
        .put("description", description())
        .put("deprecated", deprecated().toString())
        .put("type", returnType())
        .put("rw", accessType)

    /**
     * @return The access type of the current property.
     */
    private fun accessType(): String {
        val invisible = !this.element.getAnnotation(SimpleProperty::class.java).userVisible
        if (invisible) {
            return PropertyAccessType.INVISIBLE
        }

        var accessType = if (this.element.returnType.toString() == "void") {
            PropertyAccessType.WRITE
        } else {
            PropertyAccessType.READ
        }

        // If the current property is a setter, this could be a getter and vice versa.
        val partnerProp = this.store.properties.firstOrNull {
            it.name() == name() && it !== this
        }

        // If the partner prop exists and is not invisible, then it means that both getter and setter
        // exists for this prop. In that case, we set the access type to read-write which tells AI2
        // to render two blocks -- one getter and one setter.
        if (partnerProp != null && partnerProp.accessType != PropertyAccessType.INVISIBLE) {
            accessType = PropertyAccessType.READ_WRITE
        }

        // Remove the partner prop from the store. This is necessary because AI2 doesn't expects
        // getter and setter to be defined separately. It checks the access type to decide whether
        // to generate getter (read-only), setter (write-only), both (read-write) or none (invisible).
        this.store.properties.remove(partnerProp)
        return accessType
    }
}
