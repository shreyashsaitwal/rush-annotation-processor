package com.google.appinventor.components.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE)
annotation class Asset(
    val value: Array<String>
)
