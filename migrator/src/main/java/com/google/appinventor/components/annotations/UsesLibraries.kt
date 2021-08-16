package com.google.appinventor.components.annotations

/**
 * Annotation to indicate library files required by components.
 *
 * @author ralph.morelli@trincoll.edu
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class UsesLibraries(
    /**
     * The names of the libraries separated by commas.
     *
     * @return  the library name
     */
    val libraries: String = "",
    /**
     * The names of the libraries (as an array)
     *
     * @return  the array of library names
     */
    vararg val value: String = []
)
