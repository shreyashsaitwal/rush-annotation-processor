package com.google.appinventor.components.annotations

import com.google.appinventor.components.annotations.androidmanifest.ActivityElement

/**
 * Annotation to indicate any additional activities required by
 * a component so that corresponding <activity> elements can be added
 * to AndroidManifest.xml.
 *
 * @author will2596@gmail.com (William Byrne)
</activity> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class UsesActivities(
    /**
     * An array containing each [ActivityElement]
     * that is required by the component.
     *
     * @return  the array containing the relevant activities
     */
    val activities: Array<ActivityElement>
)
