package com.google.appinventor.components.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ExtensionComponent(
    val name: String,
    val description: String = "",
    val icon: String = "",
)
