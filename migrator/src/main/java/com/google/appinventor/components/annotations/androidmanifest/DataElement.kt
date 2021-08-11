package com.google.appinventor.components.annotations.androidmanifest

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Adds a data specification to an <intent-filter> as a <data> subelement.
 * The specification can be just a data type [.mimeType], just a URI,
 * or both a data type and a URI. A URI is specified by separate attributes
 * for each of its parts:
 *
 * [.scheme]://[.host]:[.port][[.path]|[.pathPrefix]|[.pathPattern]]
 *
 * When neither the MIME data type nor the data URI is specified, the <data> element
 * is ignored and not added to the manifest.
 *
 * Note: Some of this documentation is adapted from the Android framework specification
 * linked below. That documentation is licensed under the
 * [][<a href=]//creativecommons.org/licenses/by/2.5/">">&lt;a href=&quot;https://creativecommons.org/licenses/by/2.5/&quot;&gt;.
 *
 * See [][<a href=]//developer.android.com/guide/topics/manifest/data-element.html">">&lt;a href=&quot;https://developer.android.com/guide/topics/manifest/data-element.html&quot;&gt;.
 *
 * @author will2596@gmail.com (William Byrne)
</data></data></intent-filter> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DataElement(
    /**
     * The scheme part of a URI. This is the minimal essential attribute for
     * specifying a URI. If this is not specified, all other URI attributes are
     * ignored.
     *
     * Note: Scheme matching is case sensitive in Android.
     *
     * @return  the scheme for the URI
     */
    val scheme: String = "",
    /**
     * The host part of a URI authority. This attribute is meaningless unless
     * a scheme attribute is also specified for the filter. If a host is not
     * specified for the filter, the port attribute and all the path attributes
     * are ignored.
     *
     * Note: Host matching is case sensitive in Android.
     *
     * @return  the host for the URI
     */
    val host: String = "",
    /**
     * The port part of a URI authority. This attribute is meaningful only if the
     * scheme and host attributes are also specified for the filter.
     *
     * @return  the port for the URI
     */
    val port: String = "",
    /**
     * The path attribute specifies a complete path that is matched against the
     * complete path in an Intent object.
     *
     * @return  the complete URI path to the data
     */
    val path: String = "",
    /**
     * The pathPrefix attribute specifies a partial path that is matched against
     * only the initial part of the path in the Intent object.
     *
     * @return  the specified prefix of the complete data URI path
     */
    val pathPrefix: String = "",
    /**
     * The pathPattern attribute specifies a complete path that is matched against
     * the complete path in the Intent object, but it can contain the following
     * wildcards:
     *
     * -> An asterisk ('*') matches a sequence of 0 to many occurrences of the
     * immediately preceding character.
     *
     * -> A period followed by an asterisk (".*") matches any sequence of 0 to
     * many characters.
     *
     * @return  the complete URI path to the data with any of the aforementioned
     * wildcards
     */
    val pathPattern: String = "",
    /**
     * A MIME media type, such as image/jpeg or audio/mpeg4-generic. The subtype
     * can be the asterisk wildcard (*) to indicate that any subtype matches.
     *
     * Note: MIME type matching is case sensitive in Android.
     *
     * @return  the MIME media type
     */
    val mimeType: String = ""
)
