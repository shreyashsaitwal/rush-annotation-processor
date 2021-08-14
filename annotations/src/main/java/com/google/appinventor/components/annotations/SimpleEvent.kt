package com.google.appinventor.components.annotations

/**
 * Annotation to mark Simple event definitions.
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class SimpleEvent(
    /**
     * If non-empty, description to use in user-level documentation in place of
     * Javadoc, which is meant for developers.
     */
    val description: String = ""
)
