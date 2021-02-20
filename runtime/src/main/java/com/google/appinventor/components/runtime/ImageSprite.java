// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2019 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.MediaUtil;

import java.io.IOException;

/**
 * A 'sprite' that can be placed on a {@link Canvas}, where it can react to touches and drags,
 * interact with other sprites ({@link Ball}s and other `ImageSprite`s) and the edge of the
 * `Canvas`, and move according to its property values. Its appearance is that of the image
 * specified in its {@link #Picture()} property (unless its {@link #Visible()} property is
 * `false`{:.logic.block}.
 *
 * To have an `ImageSprite` move 10 pixels to the left every 1000 milliseconds (one second), for
 * example, you would set the {@link #Speed()} property to 10 [pixels], the {@link #Interval()}
 * property to 1000 [milliseconds], the {@link #Heading()} property to 180 [degrees], and the
 * {@link #Enabled()} property to `true`{:.logic.block}. A sprite whose {@link #Rotates()}
 * property is `true`{:.logic.block} will rotate its image as the sprite's heading changes.
 * *Checking for collisions with a rotated sprite currently checks the sprite's unrotated position
 * so that collision checking will be inaccurate for tall narrow or short wide sprites that are
 * rotated.* Any of the sprite properties can be changed at any time under program control.
 */

public class ImageSprite extends Sprite {
  private final Form form;
  private BitmapDrawable drawable;
  private int widthHint = LENGTH_PREFERRED;
  private int heightHint = LENGTH_PREFERRED;
  private String picturePath = "";  // Picture property
  private boolean rotates;


  /**
   * Constructor for ImageSprite.
   *
   * @param container
   */
  public ImageSprite(ComponentContainer container) {
    super(container);
    form = container.$form();
    rotates = true;
  }

  /**
   * This method uses getWidth and getHeight directly from the bitmap,
   * so we apply corrections for density for coordinates and size.
   * @param canvas the canvas on which to draw
   */
  public void onDraw(android.graphics.Canvas canvas) {
    if (drawable != null && visible) {
      int xinit = (int) (Math.round(xLeft) * form.deviceDensity());
      int yinit = (int) (Math.round(yTop) * form.deviceDensity());
      int w = (int)(Width() * form.deviceDensity());
      int h = (int)(Height() * form.deviceDensity());
      drawable.setBounds(xinit, yinit, xinit + w, yinit + h);
      // If the sprite doesn't rotate, just draw the drawable
      // within the bounds of the sprite rectangle
      if (!rotates) {
        drawable.draw(canvas);
      } else {
        // if the sprite does rotate, draw the sprite on the canvas
        // that has been rotated in the opposite direction
        // Still within those same image bounds.
        canvas.save();
        // rotate the canvas for drawing.  This pivot point of the
        // rotation will be the center of the sprite
        canvas.rotate((float) (- Heading()), xinit + w/2, yinit + h/2);
        drawable.draw(canvas);
        canvas.restore();
      }
    }
  }
 
  /**
   * Returns the path of the sprite's picture
   *
   * @return  the path of the sprite's picture
   */
  @SimpleProperty(
      description = "The picture that determines the ImageSprite's appearance.")
  public String Picture() {
    return picturePath;
  }

  /**
   * Specifies the path of the sprite's picture.
   *
   * @param path  the path of the sprite's picture
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_ASSET,
      defaultValue = "")
  @SimpleProperty
  public void Picture(String path) {
    picturePath = (path == null) ? "" : path;
    try {
      drawable = MediaUtil.getBitmapDrawable(form, picturePath);
    } catch (IOException ioe) {
      Log.e("ImageSprite", "Unable to load " + picturePath);
      drawable = null;
    }
    // note: drawable can be null!
    registerChange();
  }

  // The actual width/height of an ImageSprite whose Width/Height property is set to Automatic or
  // Fill Parent will be the width/height of the image.

  @Override
  @SimpleProperty(description = "The height of the ImageSprite in pixels.")
  public int Height() {
    if (heightHint == LENGTH_PREFERRED || heightHint == LENGTH_FILL_PARENT || heightHint <= LENGTH_PERCENT_TAG) {
      // Drawable.getIntrinsicWidth/Height gives weird values, but Bitmap.getWidth/Height works.
      return drawable == null ? 0 : (int)(drawable.getBitmap().getHeight() / form.deviceDensity());
    }
    return heightHint;
  }

  /**
   * @suppressdoc
   * @param height  height property used by the layout
   */
  @Override
  @SimpleProperty
  public void Height(int height) {
    heightHint = height;
    registerChange();
  }

  @Override
  public void HeightPercent(int pCent) {
    // Ignore
  }

  @Override
  @SimpleProperty(description = "The width of the ImageSprite in pixels.")
  public int Width() {
    if (widthHint == LENGTH_PREFERRED || widthHint == LENGTH_FILL_PARENT || widthHint <= LENGTH_PERCENT_TAG) {
      // Drawable.getIntrinsicWidth/Height gives weird values, but Bitmap.getWidth/Height works.
      return drawable == null ? 0 : (int)(drawable.getBitmap().getWidth() / form.deviceDensity());
    }
    return widthHint;
  }

  /**
   * @suppressdoc
   * @param width  width property used by the layout
   */
  @Override
  @SimpleProperty
  public void Width(int width) {
    widthHint = width;
    registerChange();
  }

  @Override
  public void WidthPercent(int pCent) {
    // Ignore
  }

  /**
   * Rotates property getter method.
   *
   * @return  {@code true} indicates that the image rotates to match the sprite's heading
   * {@code false} indicates that the sprite image doesn't rotate.
   */
  @SimpleProperty(
      description = "Whether the image should rotate to match the ImageSprite's heading. " +
          "The sprite rotates around its centerpoint.")
  public boolean Rotates() {
    return rotates;
  }

  /**
   * If true, the sprite image rotates to match the sprite's heading. If false, the sprite image
   * does not rotate when the sprite changes heading. The sprite rotates around its centerpoint.
   *
   * @param rotates  {@code true} indicates that the image rotates to match the sprite's heading
   * {@code false} indicates that the sprite image doesn't rotate.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
      defaultValue = "True")
  @SimpleProperty
    public void Rotates(boolean rotates) {
    this.rotates = rotates;
    registerChange();
  }

  // We need to override methods defined in the superclass to generate appropriate documentation.

  @SimpleProperty(
      description = "The horizontal coordinate of the left edge of the ImageSprite, " +
          "increasing as the ImageSprite moves right.")
  @Override
  public double X() {
    return super.X();
  }

  @SimpleProperty(
      description = "The vertical coordinate of the top edge of the ImageSprite, " +
          "increasing as the ImageSprite moves down.")
  @Override
  public double Y() {
    return super.Y();
  }

  /**
   * Moves the %type% so that its left top corner is at the specified `x` and `y` coordinates.
   * @param x the x-coordinate
   * @param y the y-coordinate
   */
  @SimpleFunction(
      description = "Moves the ImageSprite so that its left top corner is at " +
          "the specified x and y coordinates.")
  @Override
  public void MoveTo(double x, double y) {
    super.MoveTo(x, y);
  }
}
