package com.google.appinventor.components.annotations

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation to indicate asset files required by components.
 *
 * @author trevorbadams@gmail.com (Trevor Adams)
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class UsesAssets(
    /**
     * The filenames of the required assets separated by commas.
     */
    val fileNames: String = ""
)
