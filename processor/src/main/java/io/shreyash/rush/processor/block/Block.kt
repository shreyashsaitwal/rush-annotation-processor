package io.shreyash.rush.processor.block

import com.google.appinventor.components.annotations.Options
import io.shreyash.rush.processor.util.yailTypeOf
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

    /**
     * [Element] from which [Helper] should be created. This is always same as [element] except
     * in the case of [Property] setter block and when the [Options] annotation is used.
     * This is because:
     * - the setter type block is always void, and it's first parameter defines its type, and
     * - when the @Options annotation is used, [element] will be whatever element that annotation
     *   is used on. To create [Helper] definition, we need the element to be the [Options.value].
     */
    var helperElement: Element = element

    fun helper(): Helper? {
        if (returnType == null) return null
        val (helper, newHelperElement) = Helper.tryFrom(helperElement)

        // [newHelperElement] won't always be different; see comment on [helperElement] for more info.
        helperElement = newHelperElement
        return helper
    }

    /** Whether this block is deprecated */
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
    open val returnType: String?
        get() = if (element.returnType.toString() != "void") {
            yailTypeOf(element)
        } else {
            null
        }
}

abstract class ParameterizedBlock(element: Element) : Block(element) {
    /**
     * @return The parameters (or arguments) of this block.
     */
    fun params(): List<BlockParam> {
        return this.element.parameters.map {
            val (helper, _) = Helper.tryFrom(it)
            BlockParam(it.simpleName.toString(), yailTypeOf(it), helper)
        }
    }
}

data class BlockParam(
    val name: String,
    val type: String,
    val helper: Helper?,
)
