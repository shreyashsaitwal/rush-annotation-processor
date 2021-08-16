package com.google.appinventor.components.annotations

import com.google.appinventor.components.annotations.androidmanifest.ProviderElement

/**
 * Annotation to indicate any content providers used by
 * a component so that corresponding <provider> elements can be
 * created in AndroidManifest.xml.
 *
 * @author https://github.com/ShreyashSaitwal (Shreyash Saitwal)
</provider> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class UsesContentProviders(
    /**
     * An array containing each [ProviderElement]
     * that is required by the component.
     *
     * @return  the array containing the relevant providers
     */
    val providers: Array<ProviderElement>
)
