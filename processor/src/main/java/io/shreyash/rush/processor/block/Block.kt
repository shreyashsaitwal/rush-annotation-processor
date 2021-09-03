package io.shreyash.rush.processor.block

import io.shreyash.rush.processor.util.convert
import shaded.org.json.JSONObject
import java.lang.Deprecated
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import kotlin.Boolean
import kotlin.String

abstract class Block(element: Element) {
    val element = element as ExecutableElement

    /** Name of this block. */
    val name: String
        get() = element.simpleName.toString()

    /** The description of this block */
    abstract val description: String?

    /** Whether or not this block is deprecated */
    val deprecated: Boolean
        get() = element.getAnnotation(Deprecated::class.java) != null

    /** Checks that are supposed to be performed on this block */
    abstract fun runChecks()

    /**
     * @return JSON representation of this block that is later used to construct the `components.json`
     * descriptor file.
     */
    abstract fun asJsonObject(): JSONObject

    /**
     * @return YAIL equivalent of the return type of this block.
     */
    open fun returnType() = if (element.returnType.toString() != "void") {
        // TODO Handle the exception
        convert(element.returnType.toString())
    } else {
        null
    }
}
