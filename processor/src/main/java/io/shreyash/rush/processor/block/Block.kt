package io.shreyash.rush.processor.block

import java.lang.Deprecated
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import kotlin.String
import io.shreyash.rush.processor.util.convert
import shaded.org.json.JSONObject

abstract class Block protected constructor(element: Element) {
    private val element = element as ExecutableElement

    /**
     * Checks that are supposed to be performed on this block.
     */
    abstract fun runChecks()

    /**
     * @return The description of this block
     */
    abstract fun description(): String?

    /**
     * @return JSON representation of this block that is later used to construct the `components.json`
     * descriptor file.
     */
    abstract fun asJsonObject(): JSONObject

    /**
     * @return Name of this block.
     */
    fun name() = element.simpleName.toString()

    /**
     * @return True if this block is deprecated, else false.
     */
    fun deprecated() = element.getAnnotation(Deprecated::class.java) != null

    /**
     * @return YAIL equivalent of the return type of this block.
     */
    open fun returnType() = if (element.returnType.toString() != "void") {
        convert(element.returnType.toString())
    } else {
        null
    }
}
