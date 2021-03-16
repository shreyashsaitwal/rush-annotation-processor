// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.SimpleEvent;

/**
 * Button with the ability to detect clicks. Many aspects of its appearance can be changed, as well
 * as whether it is clickable (`Enabled`). Its properties can be changed in the Designer or in the
 * Blocks Editor.
 */

public final class Button extends ButtonBase {

  /**
   * Creates a new Button component.
   *
   * @param container container, component will be placed in
   */
  public Button(ComponentContainer container) {
    super(container);
  }

 @Override
  public void click() {
    // Call the users Click event handler. Note that we distinguish the click() abstract method
    // implementation from the Click() event handler method.
    Click();
  }

  /**
   * Indicates that the user tapped and released the `Button`.
   */
  @SimpleEvent(description = "User tapped and released the button.")
  public void Click() {
    EventDispatcher.dispatchEvent(this, "Click");
  }

  @Override
  public boolean longClick() {
    // Call the users Click event handler. Note that we distinguish the longclick() abstract method
    // implementation from the LongClick() event handler method.
    return LongClick();
  }

  /**
   * Indicates that the user held the `Button` down.
   */
  @SimpleEvent(description = "User held the button down.")
  public boolean LongClick() {
    return EventDispatcher.dispatchEvent(this, "LongClick");
  }
}
