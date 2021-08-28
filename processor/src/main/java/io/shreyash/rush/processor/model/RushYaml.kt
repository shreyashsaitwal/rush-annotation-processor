package io.shreyash.rush.processor.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RushYaml(
    val version: String,

    val license: String = "",
    val homepage: String = "",

    val assets: List<String> = listOf(),
    val authors: List<String> = listOf(),

    val deps: List<DepEntry> = listOf(),
    val android: Android = Android(),
    val kotlin: Kotlin = Kotlin(false),
    val desugar: Desugar = Desugar(false),
)

@Serializable
data class Android(
    @SerialName("compile_sdk") val compileSdk: Int = 31,
    @SerialName("min_sdk") val minSdk: Int = 7,
)

@Serializable
data class Kotlin(
    val enable: Boolean,
    val version: String = "latest-stable",
)

@Serializable
data class Desugar(
    @SerialName("src_files") val srcFile: Boolean,
    val deps: Boolean = false,
)
