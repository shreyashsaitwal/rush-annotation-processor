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
 * Physical world component that can measure the ambient air temperature if
 * supported by the hardware.
 */

public class Thermometer extends SingleValueSensor {
  /**
   * Creates a new Thermometer component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public Thermometer(ComponentContainer container) {
    super(container.$form(), Sensor.TYPE_AMBIENT_TEMPERATURE);
  }

  @Override
  protected void onValueChanged(float value) {
    TemperatureChanged(value);
  }

  /**
   * Indicates a change of temperature, provided in degrees Celsius.
   *
   * @param the temperature in degrees Celsius
   */
  @SimpleEvent(description = "Called when a change is detected in the temperature (in degrees Celsius).")
  public void TemperatureChanged(float temperature) {
    EventDispatcher.dispatchEvent(this, "TemperatureChanged", temperature);
  }

  /**
   * Returns the temperature in degrees Celsius.
   * The sensor must be enabled and available
   * to return meaningful values.
   *
   * @return the temperature in degrees Celsius
   */
  @SimpleProperty(description = "The temperature in degrees Celsius, if the sensor is available and enabled")
   public float Temperature() {
    return getValue();
  }
}
