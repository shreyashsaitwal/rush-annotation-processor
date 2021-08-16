package com.google.appinventor.components.annotations

import com.google.appinventor.components.annotations.androidmanifest.ServiceElement

/**
 * Annotation to indicate any services used by
 * a component so that corresponding <service> elements can be
 * created in AndroidManifest.xml.
 *
 * @author https://github.com/ShreyashSaitwal (Shreyash Saitwal)
</service> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class UsesServices(
    /**
     * An array containing each [ServiceElement]
     * that is required by the component.
     *
     * @return  the array containing the relevant services
     */
    val services: Array<ServiceElement>
)
