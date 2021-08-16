package com.google.appinventor.components.annotations

/**
 * Annotation to indicate native library files required by components.
 *
 * @author trevorbadams@gmail.com (Trevor Adams)
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class UsesNativeLibraries(
    /**
     * The names of the libraries separated by commas. Filenames of native libraries targeted at
     * Armeabi-v7A must end (after name but before the file extension) with a suffix
     * defined in Compiler.java ("-v7a") the same goes for v8a libraries ("-v8a").
     */
    val libraries: String = "",
    val v7aLibraries: String = "",
    val v8aLibraries: String = "",
    val x86_64Libraries: String = ""
)
