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
 * Physical world component that can measure the ambient air pressure if
 * supported by the hardware.
 */

public class Barometer extends SingleValueSensor {
  /**
   * Creates a new Barometer component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public Barometer(ComponentContainer container) {
    super(container.$form(), Sensor.TYPE_PRESSURE);
  }

  @Override
  protected void onValueChanged(float value) {
    AirPressureChanged(value);
  }
  
  /**
   * Called when a change is detected in the air pressure (provided in hPa).
   *
   * @param pressure new air pressure in hPa (millibar)
   */
  @SimpleEvent
  public void AirPressureChanged(float pressure) {
    EventDispatcher.dispatchEvent(this, "AirPressureChanged", pressure);
  }

  /**
   * The atmospheric pressure in hPa (millibar), if the sensor is available 
   * and enabled.
   *
   * @return the atmospheric pressure in hPa (millibar)
   */
  @SimpleProperty(description = "The air pressure in hPa (millibar), if the sensor is available " +
      "and enabled.")
   public float AirPressure() {
      return getValue();
  }
}
