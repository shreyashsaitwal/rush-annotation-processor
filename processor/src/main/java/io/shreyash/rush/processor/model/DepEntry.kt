package io.shreyash.rush.processor.model

import kotlinx.serialization.Serializable

enum class DepScope(val value: String) {
    RUNTIME("runtime"),
    COMPILE("compile");

    companion object {
        private val map = DepScope.values().associateBy(DepScope::value)
        fun fromString(string: String) = map[string]
    }
}

@Serializable
data class DepEntry(
    val runtime: String? = null,
    val compile: String? = null,
    val exclude: List<String> = listOf(),
) {
    init {
        // TODO: Add error message
        require(runtime != null || compile != null)
        if (runtime != null) require(compile == null)
        if (compile != null) require(runtime == null)
    }

    val scope: DepScope
        get() = if (runtime != null) {
            DepScope.RUNTIME
        } else {
            DepScope.COMPILE
        }

    val value: String
        get() = if (scope == DepScope.COMPILE) {
            compile!!
        } else {
            runtime!!
        }
}
