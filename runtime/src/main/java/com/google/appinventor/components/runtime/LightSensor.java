// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2019 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0


// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.hardware.Sensor;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleProperty;

/**
 * Physical world component that can measure the light level.
 *
 * @internaldoc
 * It is implemented using
 * android.hardware.SensorListener
 * (http://developer.android.com/reference/android/hardware/SensorListener.html).
 */

public class LightSensor extends BufferedSingleValueSensor {
  private static final int BUFFER_SIZE = 10;

  /**
   * Creates a new LightSensor component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public LightSensor(ComponentContainer container) {
    super(container.$form(), Sensor.TYPE_LIGHT, BUFFER_SIZE);
  }

  @Override
  protected void onValueChanged(float value) {
    LightChanged(value);
  }
  
  /**
   * Indicates the light level changed.
   *
   * @param lux the new light level in lux
   */
  @SimpleEvent(description = "Called when a change is detected in the light level.")
  public void LightChanged(float lux) {
    EventDispatcher.dispatchEvent(this, "LightChanged", lux);
  }

  /**
   * Returns the last measured brightness in lux.
   * The sensor must be enabled and available to return meaningful values.
   *
   * @return lux
   */
  @SimpleProperty(description = "The most recent light level, in lux, if the sensor is available " +
       "and enabled.")
   public float Lux() {
    return getValue();
  }

  /**
   * Returns the brightness in lux by averaging the previous 10 measured values.
   * The sensor must be enabled and available to return meaningful values.
   *
   * @return lux
   */
  @SimpleProperty(description = "The average of the 10 most recent light levels measured, in lux.")
   public float AverageLux() {
    return getAverageValue();
  }
}
