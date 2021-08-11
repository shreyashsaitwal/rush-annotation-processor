package com.google.appinventor.components.annotations.androidmanifest

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation to describe an <action> element required by an <intent-filter>
 * element so that it can be added to AndroidManifest.xml.
 *
 * Note: Some of this documentation is adapted from the Android framework specification
 * linked below. That documentation is licensed under the
 * [][<a href=]//creativecommons.org/licenses/by/2.5/">">&lt;a href=&quot;https://creativecommons.org/licenses/by/2.5/&quot;&gt;.
 *
 * See [][<a href=]//developer.android.com/guide/topics/manifest/action-element.html">">&lt;a href=&quot;https://developer.android.com/guide/topics/manifest/action-element.html&quot;&gt;.
 *
 * @author will2596@gmail.com (William Byrne)
</intent-filter></action> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class ActionElement(
    /**
     * The fully qualified name of the action. For standard actions defined in
     * the [android.content.Intent] class, prepend "android.intent.action" to
     * the "string" in each ACTION_string constant. For example, to specify
     * ACTION_MAIN the fully qualified name would be "android.intent.action.MAIN".
     * Custom defined actions are conventionally prepended with the package name
     * of their containing class, e.g. "com.example.project.ACTION". The name attribute
     * is required in any @ActionElement annotation and hence has no default value.
     *
     * @return the fully qualified name of the action
     */
    val name: String
)
