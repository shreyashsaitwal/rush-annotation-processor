package com.google.appinventor.components.annotations

import com.google.appinventor.components.annotations.androidmanifest.MetaDataElement
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation to indicate any additional metadata required by
 * a component so that corresponding <meta-data> elements can be added
 * to AndroidManifest.xml.
</meta-data> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class UsesApplicationMetadata(
    /**
     * An array containing each [MetaDataElement]
     * that is required by the component.
     *
     * @return  the array containing the relevant metadata
     */
    val metaDataElements: Array<MetaDataElement>
)
