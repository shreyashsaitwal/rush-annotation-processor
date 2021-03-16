// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2016 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.util.Ev3BinaryParser;
import com.google.appinventor.components.runtime.util.Ev3Constants;

/**
 * ![EV3 component icon](images/legoMindstormsEv3.png)
 *
 * A component that provides a low-level interface to a LEGO MINDSTORMS EV3
 * robot, with functions to send system or direct commands to EV3 robots.
 *
 * @author jerry73204@gmail.com (jerry73204)
 * @author spaded06543@gmail.com (Alvin Chang)
 */

public class Ev3Commands extends LegoMindstormsEv3Base {

  /**
   * Creates a new Ev3Commands component.
   */
  public Ev3Commands(ComponentContainer container) {
    super(container, "Ev3Commands");
  }

  /**
   * Keep the EV3 brick from shutdown for a period of time.
   */
  @SimpleFunction(description = "Keep the EV3 brick from shutdown for a period of time.")
  public void KeepAlive(int minutes) {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();

    if (minutes < 0 || minutes > 0xff) {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_ILLEGAL_ARGUMENT, functionName);
      return;
    }

    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.KEEP_ALIVE,
                                                         false,
                                                         0,
                                                         0,
                                                         "c",
                                                         (byte) minutes);
    sendCommand(functionName, command, false);

  }

  /**
   * Get the battery voltage.
   */
  @SimpleFunction(description = "Get the battery voltage.")
  public double GetBatteryVoltage() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.UI_READ,
                                                         true,
                                                         4,
                                                         0,
                                                         "cg",
                                                         Ev3Constants.UIReadSubcode.GET_VBATT,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);
    if (reply != null && reply.length == 5 && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] values = Ev3BinaryParser.unpack("xf", reply);
      return (Float) values[0];
    } else {                    // error
      return -1.0;
    }
  }

  /**
   * Get the battery current.
   */
  @SimpleFunction(description = "Get the battery current.")
  public double GetBatteryCurrent() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.UI_READ,
                                                         true,
                                                         4,
                                                         0,
                                                         "cg",
                                                         Ev3Constants.UIReadSubcode.GET_IBATT,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);
    if (reply != null && reply.length == 5 && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] values = Ev3BinaryParser.unpack("xf", reply);
      return (Float) values[0];
    } else {                    // error
      return -1.0;
    }
  }

  /**
   * Get the OS version on EV3.
   */
  @SimpleFunction(description = "Get the OS version on EV3.")
  public String GetOSVersion() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.UI_READ,
                                                         true,
                                                         100,
                                                         0,
                                                         "ccg",
                                                         Ev3Constants.UIReadSubcode.GET_OS_VERS,
                                                         (short) 100,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);
    if (reply != null && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] value = Ev3BinaryParser.unpack("xS", reply);
      return String.valueOf(value[0]);
    } else {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_INVALID_REPLY);
      return null;
    }
  }

  /**
   * Get the OS build on EV3.
   */
  @SimpleFunction(description = "Get the OS build on EV3.")
  public String GetOSBuild() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.UIReadSubcode.GET_OS_VERS,
                                                         true,
                                                         100,
                                                         0,
                                                         "ccg",
                                                         Ev3Constants.UIReadSubcode.GET_OS_BUILD,
                                                         (short) 100,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);

    if (reply != null && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] value = Ev3BinaryParser.unpack("xS", reply);
      return String.valueOf(value[0]);
    } else {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_INVALID_REPLY);
      return null;
    }
  }

  /**
   * Get the firmware version on EV3.
   */
  @SimpleFunction(description = "Get the firmware version on EV3.")
  public String GetFirmwareVersion() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.UI_READ,
                                                         true,
                                                         100,
                                                         0,
                                                         "ccg",
                                                         Ev3Constants.UIReadSubcode.GET_FW_VERS,
                                                         (short) 100,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);

    if (reply != null && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] value = Ev3BinaryParser.unpack("xS", reply);
      return String.valueOf(value[0]);
    } else {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_INVALID_REPLY);
      return null;
    }
  }

  /**
   * Get the firmware build on EV3.
   */
  @SimpleFunction(description = "Get the firmware build on EV3.")
  public String GetFirmwareBuild() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.UI_READ,
                                                         true,
                                                         100,
                                                         0,
                                                         "cg",
                                                         (byte) 127,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);

    if (reply != null && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] value = Ev3BinaryParser.unpack("xS", reply);
      return String.valueOf(value[0]);
    } else {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_INVALID_REPLY);
      return null;
    }
  }

  /**
   * Get the hardware version of EV3.
   */
  @SimpleFunction(description = "Get the hardware version of EV3.")
  public String GetHardwareVersion() {
    String functionName = Thread.currentThread().getStackTrace()[1].getMethodName();
    byte[] command = Ev3BinaryParser.encodeDirectCommand(Ev3Constants.Opcode.UI_READ,
                                                         true,
                                                         100,
                                                         0,
                                                         "ccg",
                                                         Ev3Constants.UIReadSubcode.GET_HW_VERS,
                                                         (short) 100,
                                                         (byte) 0);
    byte[] reply = sendCommand(functionName, command, true);

    if (reply != null && reply[0] == Ev3Constants.DirectReplyType.DIRECT_REPLY) {
      Object[] value = Ev3BinaryParser.unpack("xS", reply);
      return String.valueOf(value[0]);
    } else {
      form.dispatchErrorOccurredEvent(this, functionName, ErrorMessages.ERROR_EV3_INVALID_REPLY);
      return null;
    }
  }
}
