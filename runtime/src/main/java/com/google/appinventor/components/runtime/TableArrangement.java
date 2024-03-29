// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.app.Activity;
import android.view.View;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.ViewUtil;

/**
 * Use a table arrangement component to display a group of components in a tabular fashion.
 *
 * This component is a formatting element in which you place components that should be displayed
 * in tabular form.
 *
 * In a `TableArrangement`, components are arranged in a grid of rows and columns, with not more
 * than one component visible in each cell. **If multiple components occupy the same cell, only the
 * last one will be visible.**
 *
 * Within each row, components are vertically center-aligned.
 *
 * The width of a column is determined by the widest component in that column. When calculating
 * column width, the automatic width is used for components whose {@link #Width()} property is set
 * to `Fill Parent`. **However, each component will always fill the full width of the column that it
 * occupies.**
 *
 * The height of a row is determined by the tallest component in that row whose {@link #Height()}
 * property is not set to `Fill Parent`. If a row contains only components whose {@link #Height()}
 * properties are set to `Fill Parent`, the height of the row is calculated using the automatic
 * heights of the components.
 *
 * @author lizlooney@google.com (Liz Looney)
 */

public class TableArrangement extends AndroidViewComponent
    implements Component, ComponentContainer {
  private final Activity context;

  // Layout
  private final TableLayout viewLayout;

  /**
   * Creates a new TableArrangement component.
   *
   * @param container  container, component will be placed in
  */
  public TableArrangement(ComponentContainer container) {
    super(container);
    context = container.$context();

    viewLayout = new TableLayout(context, 2, 2);

    container.$add(this);
  }

  /**
   * Columns property getter method.
   *
   * @return  number of columns in this layout
   */
  @SimpleProperty(userVisible = false)
  public int Columns() {
    return viewLayout.getNumColumns();
  }

  /**
   * Determines the number of columns in the table.
   *
   * @param numColumns  number of columns in this layout
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
      defaultValue = "2")
  @SimpleProperty(userVisible = false)
  public void Columns(int numColumns) {
    viewLayout.setNumColumns(numColumns);
  }

  /**
   * Rows property getter method.
   *
   * @return  number of rows in this layout
   */
  @SimpleProperty(userVisible = false)
  public int Rows() {
    return viewLayout.getNumRows();
  }

  /**
   * Determines the number of rows in the table.
   *
   * @param numRows  number of rows in this layout
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
      defaultValue = "2")
  @SimpleProperty(userVisible = false)
  public void Rows(int numRows) {
    viewLayout.setNumRows(numRows);
  }

  // ComponentContainer implementation

  @Override
  public Activity $context() {
    return context;
  }

  @Override
  public Form $form() {
    return container.$form();
  }

  @Override
  public void $add(AndroidViewComponent component) {
    viewLayout.add(component);
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {

    System.err.println("TableArrangment.setChildWidth: width = " + width + " component = " + component);
    if (width <= LENGTH_PERCENT_TAG) {

      int cWidth = container.$form().Width();

      if ((cWidth > LENGTH_PERCENT_TAG) && (cWidth <= 0)) {
        // FILL_PARENT OR LENGTH_PREFERRED
        width = LENGTH_PREFERRED;
      } else {
        System.err.println("%%TableArrangement.setChildWidth(): width = " + width + " parent Width = " + cWidth + " child = " + component);
        width = cWidth * (- (width - LENGTH_PERCENT_TAG)) / 100;
      }
    }

    component.setLastWidth(width);

    ViewUtil.setChildWidthForTableLayout(component.getView(), width);
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    if (height <= LENGTH_PERCENT_TAG) {
      int cHeight = container.$form().Height();

      if ((cHeight > LENGTH_PERCENT_TAG) && (cHeight <= 0)) {
        // FILL_PARENT OR LENGTH_PREFERRED
        height = LENGTH_PREFERRED;
      } else {
        height = cHeight * (- (height - LENGTH_PERCENT_TAG)) / 100;
      }
    }

    component.setLastHeight(height);

    ViewUtil.setChildHeightForTableLayout(component.getView(), height);

  }

  // AndroidViewComponent implementation

  @Override
  public View getView() {
    return viewLayout.getLayoutManager();
  }
}
