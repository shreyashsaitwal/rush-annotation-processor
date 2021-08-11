package com.google.appinventor.components.annotations.androidmanifest

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Defines the path and required permissions for a specific subset of data within a
 * <provider>. This element can be specified multiple times to supply multiple paths.
 *
 * Note: Most of this documentation is adapted from the Android framework specification
 * linked below. That documentation is licensed under the
 * [][<a href=]//creativecommons.org/licenses/by/2.5/">">&lt;a href=&quot;https://creativecommons.org/licenses/by/2.5/&quot;&gt;.
 *
 * See [][<a href=]//developer.android.com/guide/topics/manifest/path-permission-element">">&lt;a href=&quot;https://developer.android.com/guide/topics/manifest/path-permission-element&quot;&gt;.
 *
 * @author https://github.com/ShreyashSaitwal (Shreyash Saitwal)
</provider> */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class PathPermissionElement(
    /**
     * A complete URI path for a subset of content provider data. Permission can be granted only
     * to the particular data identified by this path. When used to provide search suggestion
     * content, it must be appended with "/search_suggest_query".
     *
     * @return  the path permission path attribute
     */
    val path: String = "",
    /**
     * The initial part of a URI path for a subset of content provider data. Permission can be
     * granted to all data subsets with paths that share this initial part.
     *
     * @return  the path permission pathPrefix attribute
     */
    val pathPrefix: String = "",
    /**
     * A complete URI path for a subset of content provider data, but one that can use the following
     * wildcards:
     * - An asterisk ('*'). This matches a sequence of 0 to many occurrences of the immediately
     * preceding character.
     * - A period followed by an asterisk (".*"). This matches any sequence of 0 or more characters.
     *
     * Because '\' is used as an escape character when the string is read from XML (before it is parsed
     * as a pattern), you will need to double-escape. For example, a literal '*' would be written as "\\*"
     * and a literal '\' would be written as "\\". This is basically the same as what you would need to
     * write if constructing the string in Java code.
     *
     * @return  the path permission pathPattern attribute
     */
    val pathPattern: String = "",
    /**
     * The name of a permission that clients must have in order to read or write the content provider's
     * data. This attribute is a convenient way of setting a single permission for both reading and
     * writing. However, the [.readPermission] and [.writePermission] attributes take
     * precedence over this one.
     *
     * @return  the path permission permission attribute
     */
    val permission: String = "",
    /**
     * A permission that clients must have in order to query the content provider.
     *
     * @return  the path permission readPermission attribute
     */
    val readPermission: String = "",
    /**
     * A permission that clients must have in order to make changes to the data controlled by the
     * content provider.
     *
     * @return  the path permission writePermission attribute
     */
    val writePermission: String = ""
)
