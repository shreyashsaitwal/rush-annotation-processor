package io.shreyash.rush.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RushYaml(
    // Required fields
    val name: String,
    val description: String,
    val version: Version,
    val assets: Assets,

    // Optional fields
    @SerialName("min_sdk")
    val minSdk: Int = 7,
    val license: String = "",
    val homepage: String = "",
    val deps: List<String> = listOf(),
    val authors: List<String> = listOf(),
    val build: Build = Build(),

    // Deprecated
    @SerialName("license_url")
    val licenseUrl: String = "",
    val release: Release = Release(),
)

@Serializable
data class Assets(
    val icon: String,
    val other: List<String> = listOf(),
)

@Serializable
data class Version(
    val name: String,
    val number: String
)
