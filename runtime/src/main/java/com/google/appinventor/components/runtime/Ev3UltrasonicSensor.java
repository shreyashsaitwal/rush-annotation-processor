// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2016 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.os.Handler;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.ErrorMessages;

/**
 * ![EV3 component icon](images/legoMindstormsEv3.png)
 *
 * A component that provides a high-level interface to an ultrasonic sensor on a LEGO
 * MINDSTORMS EV3 robot.
 *
 * @author jerry73204@gmail.com (jerry73204)
 * @author spaded06543@gmail.com (Alvin Chang)
 */

public class Ev3UltrasonicSensor extends LegoMindstormsEv3Sensor implements Deleteable {
  private static final int SENSOR_TYPE = 30;
  private static final int SENSOR_MODE_CM = 0;
  private static final int SENSOR_MODE_INCH = 1;
  private static final String SENSOR_MODE_CM_STRING = "cm";
  private static final String SENSOR_MODE_INCH_STRING = "inch";

  private static final int DEFAULT_BOTTOM_OF_RANGE = 30;
  private static final int DEFAULT_TOP_OF_RANGE = 90;
  private static final String DEFAULT_SENSOR_MODE_STRING = SENSOR_MODE_CM_STRING;
  private static final int DELAY_MILLISECONDS = 50;

  private String modeString = SENSOR_MODE_CM_STRING;
  private int mode = SENSOR_MODE_CM;
  private Handler eventHandler;
  private final Runnable sensorValueChecker;
  private double previousDistance = -1.0;
  private int bottomOfRange;
  private int topOfRange;
  private boolean belowRangeEventEnabled;
  private boolean withinRangeEventEnabled;
  private boolean aboveRangeEventEnabled;

  /**
   * Creates a new Ev3UltrasonicSensor component.
   */
  public Ev3UltrasonicSensor(ComponentContainer container) {
    super(container, "Ev3UltrasonicSensor");

    eventHandler = new Handler();
    sensorValueChecker = new Runnable() {
      public void run() {
        String functionName = "";

        if (bluetooth != null && bluetooth.IsConnected()) {
          double currentDistance = getDistance(functionName);

          if (previousDistance < 0.0) {
            previousDistance = currentDistance;
            eventHandler.postDelayed(this, DELAY_MILLISECONDS);
            return;
          }

          if (currentDistance < bottomOfRange) {
            if (belowRangeEventEnabled && previousDistance >= bottomOfRange)
              BelowRange();
          } else if (currentDistance > topOfRange) {
            if (aboveRangeEventEnabled && previousDistance <= topOfRange)
              AboveRange();
          } else {
            if (withinRangeEventEnabled && (previousDistance < bottomOfRange || previousDistance > topOfRange))
              WithinRange();
          }

          previousDistance = currentDistance;
        }

        eventHandler.postDelayed(this, DELAY_MILLISECONDS);
      }
    };

    eventHandler.post(sensorValueChecker);

    TopOfRange(DEFAULT_TOP_OF_RANGE);
    BottomOfRange(DEFAULT_BOTTOM_OF_RANGE);
    BelowRangeEventEnabled(false);
    AboveRangeEventEnabled(false);
    WithinRangeEventEnabled(false);
    Unit(DEFAULT_SENSOR_MODE_STRING);
  }

  @SimpleFunction(description = "Returns the current distance in centimeters as a value between " +
                                "0 and 254, or -1 if the distance can not be read.")
  public double GetDistance() {
    String functionName = "GetDistance";
    return getDistance(functionName);
  }

  private double getDistance(String functionName) {
    double distance = readInputSI(functionName, 0, sensorPortNumber, SENSOR_TYPE, mode);
    return distance == 255 ? -1.0 : distance;
  }

  /**
   * Returns the bottom of the range used for the BelowRange, WithinRange,
   * and AboveRange events.
   */
  @SimpleProperty(description = "The bottom of the range used for the BelowRange, WithinRange, " +
                                "and AboveRange events.")
  public int BottomOfRange() {
    return bottomOfRange;
  }

  /**
   * Specifies the bottom of the range used for the BelowRange, WithinRange,
   * and AboveRange events.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
                    defaultValue = "" + DEFAULT_BOTTOM_OF_RANGE)
  @SimpleProperty
  public void BottomOfRange(int bottomOfRange) {
    this.bottomOfRange = bottomOfRange;
  }

  /**
   * Returns the top of the range used for the BelowRange, WithinRange, and
   * AboveRange events.
   */
  @SimpleProperty(description = "The top of the range used for the BelowRange, WithinRange, and " +
                                "AboveRange events.")
  public int TopOfRange() {
    return topOfRange;
  }

  /**
   * Specifies the top of the range used for the BelowRange, WithinRange, and
   * AboveRange events.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER,
                    defaultValue = "" + DEFAULT_TOP_OF_RANGE)
  @SimpleProperty
  public void TopOfRange(int topOfRange) {
    this.topOfRange = topOfRange;
  }

  /**
   * Returns whether the BelowRange event should fire when the distance
   * goes below the BottomOfRange.
   */
  @SimpleProperty(description = "Whether the BelowRange event should fire when the distance " +
                                "goes below the BottomOfRange.")
  public boolean BelowRangeEventEnabled() {
    return belowRangeEventEnabled;
  }

  /**
   * Specifies whether the BelowRange event should fire when the distance
   * goes below the BottomOfRange.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
                    defaultValue = "False")
  @SimpleProperty
  public void BelowRangeEventEnabled(boolean enabled) {
    belowRangeEventEnabled = enabled;
  }

  /**
   * Called when the detected distance has gone below the range.
   */
  @SimpleEvent(description = "Called when the detected distance has gone below the range.")
  public void BelowRange() {
    EventDispatcher.dispatchEvent(this, "BelowRange");
  }

  /**
   * Returns whether the WithinRange event should fire when the distance
   * goes between the BottomOfRange and the TopOfRange.
   */
  @SimpleProperty(description = "Whether the WithinRange event should fire when the distance" +
                                " goes between the BottomOfRange and the TopOfRange.")
  public boolean WithinRangeEventEnabled() {
    return withinRangeEventEnabled;
  }

  /**
   * Specifies whether the WithinRange event should fire when the distance
   * goes between the BottomOfRange and the TopOfRange.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
                    defaultValue = "False")
  @SimpleProperty
  public void WithinRangeEventEnabled(boolean enabled) {
    withinRangeEventEnabled = enabled;
  }

  /**
   * Called when the detected distance has gone within the range.
   */
  @SimpleEvent(description = "Called when the detected distance has gone within the range.")
  public void WithinRange() {
    EventDispatcher.dispatchEvent(this, "WithinRange");
  }

  /**
   * Returns whether the AboveRange event should fire when the distance
   * goes above the TopOfRange.
   */
  @SimpleProperty(description = "Whether the AboveRange event should fire when the distance " +
                                "goes above the TopOfRange.")
  public boolean AboveRangeEventEnabled() {
    return aboveRangeEventEnabled;
  }

  /**
   * Specifies whether the AboveRange event should fire when the distance
   * goes above the TopOfRange.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
                    defaultValue = "False")
  @SimpleProperty
  public void AboveRangeEventEnabled(boolean enabled) {
    aboveRangeEventEnabled = enabled;
  }

  /**
   * Called when the detected distance has gone above the range.
   */
  @SimpleEvent(description = "Called when the detected distance has gone above the range.")
  public void AboveRange() {
    EventDispatcher.dispatchEvent(this, "AboveRange");
  }

  /**
   * Specifies the unit of distance.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LEGO_EV3_ULTRASONIC_SENSOR_MODE,
                    defaultValue = DEFAULT_SENSOR_MODE_STRING)
  @SimpleProperty
  public void Unit(String unitName) {
    String functionName = "Unit";
    try {
      setMode(unitName);
    } catch(IllegalArgumentException e) {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_ILLEGAL_ARGUMENT, functionName);
    }
  }

  /**
   * Returns the unit of distance.
   */
  @SimpleProperty(description = "The distance unit, which can be either \"cm\" or \"inch\".")
  public String Unit() {
    return modeString;
  }

  /**
   * Measure the distance in centimeters.
   */
  @SimpleFunction(description = "Measure the distance in centimeters.")
  public void SetCmUnit() {
    String functionName = "SetCmUnit";
    try {
      setMode(SENSOR_MODE_CM_STRING);
    } catch(IllegalArgumentException e) {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_ILLEGAL_ARGUMENT, functionName);
    }
  }

  /**
   * Measure the distance in inches.
   */
  @SimpleFunction(description = "Measure the distance in inches.")
  public void SetInchUnit() {
    String functionName = "SetInchUnit";
    try {
      setMode(SENSOR_MODE_INCH_STRING);
    } catch(IllegalArgumentException e) {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_ILLEGAL_ARGUMENT, functionName);
    }
  }

  private void setMode(String newModeString) {
    previousDistance = -1.0;

    if (SENSOR_MODE_CM_STRING.equals(newModeString)) {
      mode = SENSOR_MODE_CM;
    }
    else if (SENSOR_MODE_INCH_STRING.equals(newModeString)) {
      mode = SENSOR_MODE_INCH;
    }
    else
      throw new IllegalArgumentException();

    this.modeString = newModeString;
  }

  // Deleteable implementation
  @Override
  public void onDelete() {
    eventHandler.removeCallbacks(sensorValueChecker);
    super.onDelete();
  }
}
