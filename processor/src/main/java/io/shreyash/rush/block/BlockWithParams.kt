package io.shreyash.rush.block

import io.shreyash.rush.util.convert
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement

abstract class BlockWithParams protected constructor(element: Element) : Block(element) {
    private val element = element as ExecutableElement

    /**
     * @return The parameters (or arguments) of this block.
     */
    fun params(): List<BlockParam> {
        val params = this.element.parameters
        return params.map {
            BlockParam(it.simpleName.toString(), convert(it.asType().toString()))
        }
    }

}

data class BlockParam(
    // Name of this parameter
    val name: String,
    // YAIL type of this parameter
    val type: String
)
