package com.google.appinventor.components.annotations

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Meta(
    val name: String,
    val description: String = "",
    val icon: String = "",
)
