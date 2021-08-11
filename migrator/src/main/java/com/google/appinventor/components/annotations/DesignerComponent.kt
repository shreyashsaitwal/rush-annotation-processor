package com.google.appinventor.components.annotations

import com.google.appinventor.components.common.ComponentCategory
import com.google.appinventor.components.common.ComponentConstants
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotation to mark components for use in the Designer and Blocks Editor.
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class DesignerComponent(
    /**
     * Category within designer.
     */
    val category: ComponentCategory = ComponentCategory.UNINITIALIZED,
    /**
     * If non-empty, description to use in user-level documentation on a static
     * web page in place of Javadoc, which is meant for developers.  This may
     * contain unescaped HTML.
     */
    val description: String = "",
    /**
     * Description to be shown on user request in the Designer.  If this field is
     * empty, the description() field should be used.  This may contain HTML.
     * Internal double-quotes will be converted to single-quotes when this field
     * is displayed in the designer.
     */
    // TODO(user): Add more robust character escaping.
    val designerHelpDescription: String = "",
    /**
     * If False, don't show this component on the palette.  This was added to
     * support the Form/Screen component.
     */
    val showOnPalette: Boolean = true,
    /**
     * If true, component is "non-visible" in the UI; that is, it doesn't need
     * any special handling in the Designer and can be represented by a
     * [com.google.appinventor.client.editor.simple.components.MockNonVisibleComponent].
     */
    val nonVisible: Boolean = false,
    /**
     * The file name of the icon that represents the component in the palette.
     * This should be just the last part of the path name for the file. We'll
     * look for the file in "com/google/appinventor/images/" for
     * statically loaded resources, or in "war/images/" for dynamically loaded
     * components
     *
     * @return The name of the icon file
     */
    val iconName: String = "",
    /**
     * The version of the component.
     */
    // Constants for all component version numbers must be defined in
    // com.google.appinventor.components.common.YaVersion, and specified when the DesignerComponent
    // annotation is used.
    val version: Int // There is no default value.
    ,
    /**
     * Custom help URL for the component (used for extensions).
     */
    val helpUrl: String = "",
    /**
     * The minimum SDK version required for the component. Defaults to
     * the global App Inventor minimum SDK unless otherwise specified.
     */
    val androidMinSdk: Int = ComponentConstants.APP_INVENTOR_MIN_SDK,
    /**
     * A custom version name for the component version. If provided, it
     * will be shown in the component help popup in place of the
     * [.version]. This can be useful for marking beta or release
     * candidate versions of extensions, for example.
     * @return The custom version name, if any.
     */
    val versionName: String = "",
    /**
     * A ISO 8601 datetime string that indicates when the component was
     * built. This information will be shown in the component help popup
     * for extensions. This is automatically populated by
     * [com.google.appinventor.components.scripts.ExternalComponentGenerator].
     * @return An ISO 8601 string containing the compilation time of
     * the component.
     */
    val dateBuilt: String = "",
    /**
     * The file name of the LICENSE file that the component is attributed under.
     * Meant primarily for use by external components which can have a license
     * different from that of this codebase. This string can also be a URL pointing
     * to an external LICENSE file.
     *
     * @return The name of the LICENSE file
     */
    val licenseName: String = ""
)
