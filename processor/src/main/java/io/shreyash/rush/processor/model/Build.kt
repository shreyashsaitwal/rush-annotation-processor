package io.shreyash.rush.processor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Build(
    val release: Release? = Release(),
    val kotlin: Kotlin = Kotlin(),
    val desugar: Desugar = Desugar(),
)

@Serializable
data class Release(
    val optimize: Boolean = false
)

@Serializable
data class Kotlin(
    val enable: Boolean = false
)

@Serializable
data class Desugar(
    @SerialName("desugar_deps")
    val desugarDeps: Boolean = false,
    val enable: Boolean = false
)
