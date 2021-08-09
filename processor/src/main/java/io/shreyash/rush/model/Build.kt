package io.shreyash.rush.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Build(
    val desugar: Desugar? = null,
    val kotlin: Kotlin? = null,
    val release: Release? = null,
)

@Serializable
data class Release(
    val optimize: Boolean
)

@Serializable
data class Kotlin(
    val enable: Boolean
)

@Serializable
data class Desugar(
    @SerialName("desugar_deps")
    val desugarDeps: Boolean = false,
    val enable: Boolean
)
