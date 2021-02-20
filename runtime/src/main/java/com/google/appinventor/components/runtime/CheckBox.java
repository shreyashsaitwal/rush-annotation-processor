// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2018 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import annotations.DesignerProperty;
import annotations.SimpleProperty;
import common.PropertyTypeConstants;

/**
 * ![Example of a CheckBox](images/checkbox.png)
 *
 * `CheckBox` components can detect user taps and can change their boolean state in response.
 *
 * A `CheckBox` component raises an event when the user taps it. There are many properties affecting
 * its appearance that can be set in the Designer or Blocks Editor.
 */

public final class CheckBox extends ToggleBase<android.widget.CheckBox> {

  /**
   * Creates a new CheckBox component.
   *
   * @param container  container, component will be placed in
   */
  public CheckBox(ComponentContainer container) {
    super(container);
    view = new android.widget.CheckBox(container.$context());
    Checked(false);
    initToggle();
  }

  /**
   * Set to `true`{:.logic.block} if the box is checked, `false`{:.logic.block} otherwise.
   *
   * @return  {@code true} indicates checked, {@code false} unchecked
   */
  @SimpleProperty(description = "True if the box is checked, false otherwise.")
  public boolean Checked() {
    return view.isChecked();
  }

  /**
   * Checked property setter method.
   *
   * @suppressdoc
   * @param value  {@code true} indicates checked, {@code false} unchecked
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
      defaultValue = "False")
  @SimpleProperty
  public void Checked(boolean value) {
    view.setChecked(value);
    view.invalidate();
  }

}
