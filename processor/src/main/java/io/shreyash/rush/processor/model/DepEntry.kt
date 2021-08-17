package io.shreyash.rush.processor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class DepScope(val value: String) {
    IMPLEMENT("runtime"),
    COMPILE_ONLY("compile");

    companion object {
        private val map = DepScope.values().associateBy(DepScope::value)
        fun fromString(string: String) = map[string]
    }
}

@Serializable
data class DepEntry(
    val implement: String? = null,
    @SerialName("compile_only") val compileOnly: String? = null,
    val exclude: List<String> = listOf(),
) {
    init {
        // TODO: Add error message
        require(implement != null || compileOnly != null)
        if (implement != null) require(compileOnly == null)
        if (compileOnly != null) require(implement == null)
    }

    val scope: DepScope
        get() = if (implement != null) {
            DepScope.IMPLEMENT
        } else {
            DepScope.COMPILE_ONLY
        }

    val mvnCoordinate: String
        get() = if (scope == DepScope.COMPILE_ONLY) {
            compileOnly!!
        } else {
            implement!!
        }
}
