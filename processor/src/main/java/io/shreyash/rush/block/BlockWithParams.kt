package io.shreyash.rush.block

import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import io.shreyash.rush.util.convert

abstract class BlockWithParams protected constructor(element: Element) : Block(element) {
    private val element = element as ExecutableElement

    /**
     * @return The parameters (or arguments) of this block.
     */
    fun params(): List<BlockParam> {
        val params = this.element.parameters
        return params.map {
            val annotate = it.getAnnotation(io.shreyash.rush.Rename::class.java)
            val simpleName = it.simpleName.toString()

            val name: String = annotate?.name ?: simpleName
            BlockParam(simpleName, name, convert(it.asType().toString()))
        }
    }
}

data class BlockParam(
    // Original name of the parameter
    val originalName: String,
    // Name of this parameter
    val name: String,
    // YAIL type of this parameter
    val type: String
)
