package com.google.appinventor.components.annotations

/**
 * Annotation to change user facing name of block parameters.
 *
 * Usage:
 * `public void Foo(@Rename("bazz") String bar) { ... }`
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE_PARAMETER)
annotation class Rename (
    val name: String = "",
)
