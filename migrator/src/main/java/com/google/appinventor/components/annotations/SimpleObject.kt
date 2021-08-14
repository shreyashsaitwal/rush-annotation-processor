package com.google.appinventor.components.annotations

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation to mark Simple objects.
 *
 *
 * Note that the Simple compiler will only recognize Java classes marked
 * with this annotation. All other classes will be ignored.
 *
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class SimpleObject(
    /**
     * True if this component is an external component.
     * Setting to True is mandatory for packing Extensions (aix)
     */
    val external: Boolean = false
)
