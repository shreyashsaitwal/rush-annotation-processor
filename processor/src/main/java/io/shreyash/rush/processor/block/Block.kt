package io.shreyash.rush.processor.block

import com.google.appinventor.components.annotations.Asset
import io.shreyash.rush.processor.util.yailTypeOf
import shaded.org.json.JSONObject
import java.lang.Deprecated
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import kotlin.Boolean
import kotlin.String
import kotlin.apply

abstract class Block(element: Element) {
    val element = element as ExecutableElement

    /** Name of this block. */
    val name: String
        get() = element.simpleName.toString()

    /** The description of this block */
    abstract val description: String?

    /**
     * [Element] that should be used to create [Helper] from. This is always same as [element] except
     * in the case of [Property] setter block. This is because the setter type block is always void
     * and its it's first parameter that defines its type.
     */
    var helperElement: Element = element

    fun helper(): Helper? {
        if (returnType == null) return null
        val helperType = HelperType.tryFrom(helperElement)
        return when (helperType) {
            HelperType.ASSET -> {
                Helper(
                    type = helperType,
                    data = AssetData(helperElement.getAnnotation(Asset::class.java).value)
                )
            }

            HelperType.OPTION_LIST -> {
                val optionListEnumName = if (helperElement is ExecutableElement)
                    (helperElement as ExecutableElement).returnType.toString()
                else
                    helperElement.asType().toString()

                val data = if (optionListCache.containsKey(optionListEnumName)) {
                    optionListCache[optionListEnumName]!!
                } else {
                    OptionListData(helperElement).apply {
                        optionListCache.putIfAbsent(optionListEnumName, this)
                    }
                }
                Helper(
                    type = helperType,
                    data = data
                )
            }

            else -> null
        }
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
            val helper = when (val helperType = HelperType.tryFrom(it)) {
                HelperType.ASSET -> {
                    Helper(
                        type = helperType,
                        data = AssetData(it.getAnnotation(Asset::class.java).value)
                    )
                }

                HelperType.OPTION_LIST -> {
                    val optionListEnumName = it.asType().toString()
                    val data = if (optionListCache.containsKey(optionListEnumName)) {
                        optionListCache[optionListEnumName]!!
                    } else {
                        OptionListData(it).apply {
                            optionListCache.putIfAbsent(optionListEnumName, this)
                        }
                    }
                    Helper(
                        type = helperType,
                        data = data
                    )
                }

                else -> null
            }

            BlockParam(it.simpleName.toString(), yailTypeOf(it), helper)
        }
    }
}

data class BlockParam(
    // Name of this parameter
    val name: String,

    // YAIL type of this parameter
    val type: String,

    val helper: Helper?,
)
