// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2019 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0
// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)
package com.google.appinventor.components.annotations

/**
 * Annotation to indicate Android permissions required by components.
 *
 * @author markf@google.com (Mark Friedman)
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class UsesPermissions(
    /**
     * The names of the permissions separated by commas.
     *
     * @return  the permission name
     * @see android.Manifest.permission
     */
    val permissionNames: String = "",
    /**
     * The names of the permissions as a list.
     *
     * @return  the permission names
     * @see android.Manifest.permission
     */
    vararg val value: String = []
)
