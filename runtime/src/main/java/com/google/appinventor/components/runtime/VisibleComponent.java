// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.SimpleProperty;

/**
 * Superclass of visible components in the runtime libraries.
 * <p>
 * Defines standard properties and events.
 *
 */
public abstract class VisibleComponent implements Component {
  protected VisibleComponent() {
  }

  /**
   * Width property getter method.
   *
   * @return  width property used by the layout
   */
  @SimpleProperty
  public abstract int Width();

  /**
   * Width property setter method.
   *
   * @param width  width property used by the layout
   */
  @SimpleProperty
  public abstract void Width(int width);

  /**
   * Width property setter method.
   *
   * This version takes a percentage of the parent
   * component's width as its input.
   *
   * @param wPercent width as a percent of its parent
   *
   */
  @SimpleProperty
  public abstract void WidthPercent(int wPercent);

  /**
   * Height property getter method.
   *
   * @return  height property used by the layout
   */
  @SimpleProperty
  public abstract int Height();

  /**
   * Height property setter method.
   *
   * @param height  height property used by the layout
   */
  @SimpleProperty
  public abstract void Height(int height);

  /**
   * Height property setter method.
   *
   * This version takes a percentage of the parent
   * component's height as its input.
   *
   * @param hPercent width as a percent of the height of its parent
   *
   */
  @SimpleProperty
  public abstract void HeightPercent(int hPercent);
}
