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

/**
 * ![EV3 component icon](images/legoMindstormsEv3.png)
 *
 * A component that provides a high-level interface to a touch sensor on a LEGO
 * MINDSTORMS EV3 robot.
 *
 * @author jerry73204@gmail.com (jerry73204)
 * @author spaded06543@gmail.com (Alvin Chang)
 */

public class Ev3TouchSensor extends LegoMindstormsEv3Sensor implements Deleteable {
  private static final int SENSOR_VALUE_THRESHOLD = 50;
  private static final int SENSOR_TYPE = 16;
  private static final int SENSOR_MODE_TOUCH = 0;
  private static final String SENSOR_MODE_TOUCH_STRING = "touch";
  private static final int DELAY_MILLISECONDS = 50;

  private String modeString = SENSOR_MODE_TOUCH_STRING;
  private int mode = SENSOR_MODE_TOUCH;
  private Handler eventHandler;
  private final Runnable sensorValueChecker;
  private int savedPressedValue = -1;
  private boolean pressedEventEnabled;
  private boolean releasedEventEnabled;

  /**
   * Creates a new Ev3TouchSensor component.
   */
  public Ev3TouchSensor(ComponentContainer container) {
    super(container, "Ev3TouchSensor");

    eventHandler = new Handler();
    sensorValueChecker = new Runnable() {
      public void run() {
        String functionName = "";

        if (bluetooth != null && bluetooth.IsConnected()) {
          int currentPressedValue = getPressedValue(functionName);

          if (savedPressedValue < 0) {
            savedPressedValue = currentPressedValue;
            eventHandler.postDelayed(this, DELAY_MILLISECONDS);
            return;
          }

          if (savedPressedValue < SENSOR_VALUE_THRESHOLD) {
            if (releasedEventEnabled && currentPressedValue >= SENSOR_VALUE_THRESHOLD)
              Pressed();
          } else {
            if (pressedEventEnabled && currentPressedValue < SENSOR_VALUE_THRESHOLD)
              Released();
          }

          savedPressedValue = currentPressedValue;
        }

        eventHandler.postDelayed(this, DELAY_MILLISECONDS);
      }
    };
    eventHandler.post(sensorValueChecker);

    PressedEventEnabled(false);
    ReleasedEventEnabled(false);
  }

  /**
   * Returns true if the touch sensor is pressed.
   */
  @SimpleFunction(description = "Returns true if the touch sensor is pressed.")
  public boolean IsPressed() {
    String functionName = "IsPressed";
    return getPressedValue(functionName) >= SENSOR_VALUE_THRESHOLD;
  }

  /**
   * Specifies whether the Pressed event should fire when the touch sensor is
   * pressed.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
                    defaultValue = "False")
  @SimpleProperty
  public void PressedEventEnabled(boolean enabled) {
    pressedEventEnabled = enabled;
  }

  /**
   * Returns whether the Pressed event should fire when the touch sensor is
   * pressed.
   */
  @SimpleProperty(description = "Whether the Released event should fire when the touch sensor is " +
                                "pressed.")
  public boolean PressedEventEnabled() {
    return pressedEventEnabled;
  }

  /**
   * Called when the touch sensor is pressed.
   */
  @SimpleEvent(description = "Called when the touch sensor is pressed.")
  public void Pressed() {
    EventDispatcher.dispatchEvent(this, "Pressed");
  }

  /**
   * Returns whether the Released event should fire when the touch sensor is
   * released.
   */
  @SimpleProperty(description = "Whether the Released event should fire when the touch sensor is " +
                                "released.")
  public boolean ReleasedEventEnabled() {
    return releasedEventEnabled;
  }

  /**
   * Specifies whether the Released event should fire when the touch sensor is
   * released.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN,
                    defaultValue = "False")
  @SimpleProperty
  public void ReleasedEventEnabled(boolean enabled) {
    releasedEventEnabled = enabled;
  }

  /**
   * Called when the touch sensor is pressed.
   */
  @SimpleEvent(description = "Called when the touch sensor is pressed.")
  public void Released() {
    EventDispatcher.dispatchEvent(this, "Released");
  }

  private int getPressedValue(String functionName) {
    int value =  readInputPercentage(functionName,
                                     0, // assume layer = 0
                                     sensorPortNumber,
                                     SENSOR_TYPE,
                                     mode);
    return value;
  }

  // Deleteable implementation
  @Override
  public void onDelete() {
    eventHandler.removeCallbacks(sensorValueChecker);
    super.onDelete();
  }
}
