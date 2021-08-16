package com.google.appinventor.components.annotations.androidmanifest

/**
 * Annotation to describe a <meta-data> element. A <meta-data> element consists
 * of a name-value pair for an item of additional, arbitrary data that can be
 * supplied to the parent component. For our purposes, the parent component is
 * either an <activity> or a <receiver>. A component element can contain any
 * number of <meta-data> subelements. The values from all of them are collected
 * in a single Bundle object and made available to the component as the
 * [android.content.pm.PackageItemInfo.metaData] field.
 *
 * Ordinary values are specified through the value attribute. However, to
 * assign a resource ID as the value, use the resource attribute instead.
 *
 * When using a MetaDataElement, the [.name] attribute must be specified along
 * with either the [.resource] or the [.value] attribute.
 * <meta-data> element attributes that are not set explicitly default to "" and
 * are ignored when the element is created in the manifest.
 *
 * Note: Some of this documentation is adapted from the Android framework specification
 * linked below. That documentation is licensed under the
 * [][<a href=]//creativecommons.org/licenses/by/2.5/">">&lt;a href=&quot;https://creativecommons.org/licenses/by/2.5/&quot;&gt;.
 *
 * See [][<a href=]//developer.android.com/guide/topics/manifest/meta-data-element.html">">&lt;a href=&quot;https://developer.android.com/guide/topics/manifest/meta-data-element.html&quot;&gt;.
 *
 * @author will2596@gmail.com (William Byrne)
</meta-data></meta-data></receiver></activity></meta-data></meta-data> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class MetaDataElement(
    /**
     * A unique name for the data item. By convention, this name should follow
     * the Java package name format, e.g. "com.example.project.activity.data".
     * The name attribute is required in any @MetaDataElement annotation and
     * hence has no default value.
     *
     * @return  the name of the data item
     */
    val name: String,
    /**
     * A reference to a resource. The ID of the resource is the value assigned
     * to the data item. The ID can be retrieved from the meta-data Bundle by the
     * [android.os.BaseBundle.getInt] method.
     *
     * @return  a reference to the specified resource
     */
    val resource: String = "",
    /**
     * The value assigned to the item. The data types that can be assigned as
     * values and the [android.os.Bundle] methods that components use to
     * retrieve those values are detailed below:
     *
     * -> A String value, using double backslashes (\\) to escape characters,
     * such as "\\n" and "\\uxxxxx" for a Unicode character, can be accessed
     * using [android.os.Bundle.getString].
     *
     * -> An Integer value, such as "100", can be accessed using
     * [android.os.Bundle.getInt].
     *
     * -> A Boolean value, either "true" or "false", can be accessed using
     * [android.os.Bundle.getBoolean].
     *
     * -> A Color value, in the form "#rgb", "#argb", "#rrggbb", or "#aarrggbb",
     * can be accessed using [android.os.Bundle.getInt].
     *
     * -> A Float value, such as "1.23", can be accessed using
     * [android.os.Bundle.getFloat]
     *
     * @return  the value to be assigned to this data item
     */
    val value: String = ""
)
