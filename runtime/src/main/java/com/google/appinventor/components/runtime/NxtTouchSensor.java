// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
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
 * ![NXT component icon](images/legoMindstormsNxt.png)
 *
 * A component that provides a high-level interface to a touch sensor on a LEGO
 * MINDSTORMS NXT robot.
 *
 * @author lizlooney@google.com (Liz Looney)
 */

public class NxtTouchSensor extends LegoMindstormsNxtSensor implements Deleteable {

  private enum State { UNKNOWN, PRESSED, RELEASED }
  private static final String DEFAULT_SENSOR_PORT = "1";

  private Handler handler;
  private State previousState;
  private final Runnable sensorReader;
  private boolean pressedEventEnabled;
  private boolean releasedEventEnabled;

  /**
   * Creates a new NxtTouchSensor component.
   */
  public NxtTouchSensor(ComponentContainer container) {
    super(container, "NxtTouchSensor");
    handler = new Handler();
    previousState = State.UNKNOWN;
    sensorReader = new Runnable() {
      public void run() {
        if (bluetooth != null && bluetooth.IsConnected()) {
          SensorValue<Boolean> sensorValue = getPressedValue("");
          if (sensorValue.valid) {
            State currentState = sensorValue.value ? State.PRESSED : State.RELEASED;

            if (currentState != previousState) {
              if (currentState == State.PRESSED && pressedEventEnabled) {
                Pressed();
              }
              if (currentState == State.RELEASED && releasedEventEnabled) {
                Released();
              }
            }

            previousState = currentState;
          }
        }
        if (isHandlerNeeded()) {
          handler.post(sensorReader);
        }
      }
    };

    SensorPort(DEFAULT_SENSOR_PORT);
    PressedEventEnabled(false);
    ReleasedEventEnabled(false);
  }

  @Override
  protected void initializeSensor(String functionName) {
    setInputMode(functionName, port, SENSOR_TYPE_SWITCH, SENSOR_MODE_BOOLEANMODE);
  }

  /**
   * Specifies the sensor port that the sensor is connected to.
   * **Must be set in the Designer.**
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LEGO_NXT_SENSOR_PORT,
      defaultValue = DEFAULT_SENSOR_PORT)
  @SimpleProperty(userVisible = false)
  public void SensorPort(String sensorPortLetter) {
    setSensorPort(sensorPortLetter);
  }

  @SimpleFunction(description = "Returns true if the touch sensor is pressed.")
  public boolean IsPressed() {
    String functionName = "IsPressed";
    if (!checkBluetooth(functionName)) {
      return false;
    }

    SensorValue<Boolean> sensorValue = getPressedValue(functionName);
    if (sensorValue.valid) {
      return sensorValue.value;
    }

    // invalid response
    return false;
  }

  private SensorValue<Boolean> getPressedValue(String functionName) {
    byte[] returnPackage = getInputValues(functionName, port);
    if (returnPackage != null) {
      boolean valid = getBooleanValueFromBytes(returnPackage, 4);
      if (valid) {
        int scaledValue = getSWORDValueFromBytes(returnPackage, 12);
        return new SensorValue<Boolean>(true, (scaledValue != 0));
      }
    }

    // invalid response
    return new SensorValue<Boolean>(false, null);
  }

  /**
   * Returns whether the Pressed event should fire when the touch sensor is
   * pressed.
   */
  @SimpleProperty(description = "Whether the Pressed event should fire when the touch sensor is" +
      " pressed.")
  public boolean PressedEventEnabled() {
    return pressedEventEnabled;
  }

  /**
   * Specifies whether the Pressed event should fire when the touch sensor is
   * pressed.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
  @SimpleProperty
  public void PressedEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    pressedEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  @SimpleEvent(description = "Touch sensor has been pressed.")
  public void Pressed() {
    EventDispatcher.dispatchEvent(this, "Pressed");
  }

  /**
   * Returns whether the Released event should fire when the touch sensor is
   * released.
   */
  @SimpleProperty(description = "Whether the Released event should fire when the touch sensor is" +
      " released.")
  public boolean ReleasedEventEnabled() {
    return releasedEventEnabled;
  }

  /**
   * Specifies whether the Released event should fire when the touch sensor is
   * released.
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
  @SimpleProperty
  public void ReleasedEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    releasedEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  @SimpleEvent(description = "Touch sensor has been released.")
  public void Released() {
    EventDispatcher.dispatchEvent(this, "Released");
  }

  private boolean isHandlerNeeded() {
    return pressedEventEnabled || releasedEventEnabled;
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    handler.removeCallbacks(sensorReader);
    super.onDelete();
  }
}
