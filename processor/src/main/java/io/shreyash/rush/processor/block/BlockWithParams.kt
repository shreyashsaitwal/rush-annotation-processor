package io.shreyash.rush.processor.block

import io.shreyash.rush.processor.util.convert
import javax.lang.model.element.Element

abstract class BlockWithParams protected constructor(element: Element) : Block(element) {
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
