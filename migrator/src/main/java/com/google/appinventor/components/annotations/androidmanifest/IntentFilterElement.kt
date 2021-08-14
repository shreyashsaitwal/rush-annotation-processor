package com.google.appinventor.components.annotations.androidmanifest

/**
 * Annotation to describe an <intent-filter> element required by an <activity>
 * or a <receiver> element so that it can be added to AndroidManifest.xml.
 * <intent-filter> element attributes that are not set explicitly default
 * to "" or {} and are ignored when the element is created in the manifest.
 * In order for an <intent-filter> element to work properly, it must include
 * at least one <action> element in the [.actionElements] attribute.
 *
 * See [ActionElement] for more information.
 *
 * Note: Some of this documentation is adapted from the Android framework specification
 * linked below. That documentation is licensed under the
 * [][<a href=]//creativecommons.org/licenses/by/2.5/">">&lt;a href=&quot;https://creativecommons.org/licenses/by/2.5/&quot;&gt;.
 *
 * See [][<a href=]//developer.android.com/guide/topics/manifest/intent-filter-element.html">">&lt;a href=&quot;https://developer.android.com/guide/topics/manifest/intent-filter-element.html&quot;&gt;.
 *
 * @author will2596@gmail.com (William Byrne)
</action></intent-filter></intent-filter></receiver></activity></intent-filter> */
annotation class IntentFilterElement(
    /**
     * The array of actions accepted by this <intent-filter>. By construction,
     * <intent-filter> elements must have at least one <action> subelement.
     * Thus, this attribute of @IntentFilterElement is required and has no default.
     *
     * @return  the array of actions accepted by this <intent-filter>
    </intent-filter></action></intent-filter></intent-filter> */
    val actionElements: Array<ActionElement>,
    /**
     * The array of categories accepted by this <intent-filter>. According to
     * the AndroidMainfest.xml specification, these subelements are optional.
     *
     * @return  the array of categories accepted by this <intent-filter>
    </intent-filter></intent-filter> */
    val categoryElements: Array<CategoryElement> = [],
    /**
     * The array of data specifications accepted by this <intent-filter>. According to
     * the AndroidMainfest.xml specification, these subelements are optional.
     *
     * @return  the array of data URIs accepted by this <intent-filter>
    </intent-filter></intent-filter> */
    val dataElements: Array<DataElement> = [],
    /**
     * A reference to a drawable resource representing the parent activity
     * or broadcast receiver when that component is presented to
     * the user as having the capability described by the filter.
     *
     * @return  a reference to the drawable resource for the filter's parent
     */
    val icon: String = "",
    /**
     * A user-readable label for the parent component specified as a reference
     * to a string resource. If this attribute is left unspecified, the label
     * will default to the label set by the parent component
     *
     * @return  a reference to the string resource to be used as a label
     */
    val label: String = "",
    /**
     * The priority that should be given to the parent activity or broadcast
     * receiver with regard to handling intents of the type described by the
     * filter. This must be specified as an integer in the interval
     * (-1000, 1000). If the priority is not set, it will default to 0.
     *
     * @return  the priority of the parent activity/broadcast receiver/service/content provider w.r.t.
     * handling intents described by this filter
     */
    val priority: String = "",
    /**The order in which the filter should be processed when multiple filters
     * match.
     * "order" differs from "priority" in that "priority" applies across apps, while
     * "order" disambiguates multiple matching filters in a single app.
     *
     * When multiple filters could match, use a directed intent instead.
     *
     * The value must be an integer, such as "100". Higher numbers are matched first.
     * The default value is 0.
     *
     * This attribute was introduced in API Level 28.
     *
     * @return the order of the parent activity/broadcast receiver/service/content provider w.r.t.
     * handling intents described by this filter
     */
    val order: String = ""
)
