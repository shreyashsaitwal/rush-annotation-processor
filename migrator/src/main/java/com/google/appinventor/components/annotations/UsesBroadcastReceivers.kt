package com.google.appinventor.components.annotations

import com.google.appinventor.components.annotations.androidmanifest.ReceiverElement

/**
 * Annotation to indicate any broadcast receivers used by
 * a component so that corresponding <receiver> elements can be
 * created in AndroidManifest.xml.
 *
 * @author will2596@gmail.com (William Byrne)
</receiver> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class UsesBroadcastReceivers(
    /**
     * An array containing each [ReceiverElement]
     * that is required by the component.
     *
     * @return  the array containing the relevant receivers
     */
    val receivers: Array<ReceiverElement>
)
