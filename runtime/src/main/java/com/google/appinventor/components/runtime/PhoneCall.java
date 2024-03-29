// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2019 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telephony.TelephonyManager;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.util.BulkPermissionRequest;
import com.google.appinventor.components.runtime.util.PhoneCallUtil;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * ![PhoneCall component icon](images/phonecall.png)
 *
 * A non-visible component that makes a phone call to the number specified in the
 * {@link #PhoneNumber()} property, which can be set either in the Designer or Blocks Editor.
 * The component has a {@link #MakePhoneCall()} method, enabling the program to launch a phone call.
 * You may also use {@link #MakePhoneCallDirect()} to directly initiate a phone call without user
 * interaction. However, apps using this block may require further review by Google if submitted
 * to the Play Store so it is advised to use {@link #MakePhoneCall()} instead.
 *
 * Often, this component is used with the {@link ContactPicker} component, which lets the user
 * select a contact from the ones stored on the phone and sets the
 * {@link #PhoneNumber()} property to {@link ContactPicker#PhoneNumber()} property.
 *
 * To directly specify the phone number (e.g., 650-555-1212), set the {@link #PhoneNumber()}
 * property to a Text with the specified digits (e.g., "6505551212"). Dashes, dots, and parentheses
 * may be included (e.g., "(650)-555-1212") but will be ignored; spaces may not be included.
 *
 * @internaldoc
 *
 * TODO(markf): Note that the initial carrier for Android phones only supports 3 participants
 *              in a conference call, so that's all that the current implementation of this
 *              component supports.  In the future we can generalize this to more participants.
 *
 * @author markf@google.com (Mark Friedman)
 * @author rekygx@gmail.com (Xian Gao)
 */

public class PhoneCall extends AndroidNonvisibleComponent implements Component, OnDestroyListener,
    ActivityResultListener {

  /**
   * Magic number "PHON" used to report when a phone call has been initiated
   */
  private static final int PHONECALL_REQUEST_CODE = 0x50484F4E;
  private String phoneNumber;
  private final Context context;
  private final CallStateReceiver callStateReceiver;
  private boolean havePermission = false;
  private boolean didRegisterReceiver = false;

  /**
   * Creates a Phone Call component.
   *
   * @param container container, component will be placed in
   */
  public PhoneCall(ComponentContainer container) {
    super(container.$form());
    context = container.$context();
    form.registerForOnDestroy(this);
    form.registerForActivityResult(this, PHONECALL_REQUEST_CODE);
    PhoneNumber("");
    callStateReceiver = new CallStateReceiver();
  }

  @SuppressWarnings({"unused"})
  public void Initialize() {
    if (form.doesAppDeclarePermission(READ_CALL_LOG)) {
      form.askPermission(new BulkPermissionRequest(this, "Initialize",
          PROCESS_OUTGOING_CALLS, READ_PHONE_STATE, READ_CALL_LOG) {
        @Override
        public void onGranted() {
          registerCallStateMonitor();
        }
      });
    }
  }

  /**
   * PhoneNumber property getter method.
   */
  @SimpleProperty()
  public String PhoneNumber() {
    return phoneNumber;
  }

  /**
   * Specifies the phone number to call.
   *
   * @param phoneNumber a phone number to call
   */
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING)
  @SimpleProperty
  public void PhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * Launches the default dialer app set to start a phone call using the number in the
   * {@link #PhoneNumber()} property.
   */
  @SimpleFunction(description = "Launches the default dialer app set to start a phone call using"
      + "the number in the PhoneNumber property.")
  public void MakePhoneCall() {
    Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", this.phoneNumber, null));
    if (i.resolveActivity(form.getPackageManager()) != null) {
      form.startActivityForResult(i, PHONECALL_REQUEST_CODE);
    }
  }

  /**
   * Directly initiates a phone call using the number in the {@link #PhoneNumber()} property,
   * bypassing user interaction to start the call. **Most apps should use
   * {@link #MakePhoneCall()} instead, which requires no permissions.**
   */
  @SimpleFunction(description = "Directly initiates a phone call using the number in the "
      + "PhoneNumber property.")
  public void MakePhoneCallDirect() {
    // Check that we have permission and ask for it if we don't
    if (!havePermission) {
      form.askPermission(CALL_PHONE,
        new PermissionResultHandler() {
          @Override
          public void HandlePermissionResponse(String permission, boolean granted) {
            if (granted) {
              PhoneCall.this.havePermission = true;
              PhoneCall.this.MakePhoneCallDirect();
            } else {
              form.dispatchPermissionDeniedEvent(PhoneCall.this, "MakePhoneCall", CALL_PHONE);
            }
          }
        });
    } else {
      PhoneCallUtil.makePhoneCall(context, phoneNumber);
    }
  }

  /**
   * Event indicating that a phone call has started. The `status`{:.variable.block} can be any of:
   *
   *   - `1`: Incoming call is ringing
   *   - `2`: Outgoing call is dialled
   *
   * @param status 1:incoming call is ringing; 2:outgoing call is dialled.
   * @param phoneNumber incoming/outgoing call phone number
   */
  @SimpleEvent(
      description =
          "Event indicating that a phonecall has started." +
              " If status is 1, incoming call is ringing; " +
              "if status is 2, outgoing call is dialled. " +
              "phoneNumber is the incoming/outgoing phone number.")
  public void PhoneCallStarted(int status, String phoneNumber) {
    // invoke the application's "PhoneCallStarted" event handler.
    EventDispatcher.dispatchEvent(this, "PhoneCallStarted", status, phoneNumber);
  }

  /**
   * Event indicating that a phone call has ended. The `status`{:.variable.block} can be any of:
   *
   *   - `1`: Incoming call was missed or rejected
   *   - `2`: Incoming call was answered and hung up
   *   - `3`: Outgoing call was hung up.
   *
   * @param status 1:incoming call is missed or rejected; 2:incoming call is answered before
   *               hanging up; 3:Outgoing call is hung up.
   * @param phoneNumber ended call phone number
   */
  @SimpleEvent(
      description =
          "Event indicating that a phone call has ended. " +
              "If status is 1, incoming call is missed or rejected; " +
              "if status is 2, incoming call is answered before hanging up; " +
              "if status is 3, outgoing call is hung up. " +
              "phoneNumber is the ended call phone number.")
  public void PhoneCallEnded(int status, String phoneNumber) {
    // invoke the application's "PhoneCallEnded" event handler.
    EventDispatcher.dispatchEvent(this, "PhoneCallEnded", status, phoneNumber);
  }

  /**
   * Event indicating that an incoming phone call is answered. `phoneNumber`{:.variable.block} is
   * the incoming call phone number.
   *
   * @param phoneNumber incoming call phone number
   */
  @SimpleEvent(
      description =
          "Event indicating that an incoming phone call is answered. " +
              "phoneNumber is the incoming call phone number.")
  public void IncomingCallAnswered(String phoneNumber) {
    // invoke the application's "IncomingCallAnswered" event handler.
    EventDispatcher.dispatchEvent(this, "IncomingCallAnswered", phoneNumber);
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    if (requestCode == PHONECALL_REQUEST_CODE) {
      PhoneCallStarted(2, "");
    }
  }

  /**
   * BroadcastReceiver for incomming/outgoing phonecall state changes
   *
   */
  private class CallStateReceiver extends BroadcastReceiver {
    private int status; // 0:undetermined, 1:incoming ringed, 2:outgoing dialled,
                        // 3: incoming answered
    private String number; // phone call number
    public CallStateReceiver() {
      status = 0;
      number = "";
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if(TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)){
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        if(TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
          // Incoming call rings
          status = 1;
          number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
          if (number == null) {
            // This gets called first with null, then with the actual number.
            // Ignore the first invocation.
            return;
          }
          PhoneCallStarted(1, number);
        }else if(TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)){
          // Call off-hook
          if(status == 1){
            // Incoming call answered
            status = 3;
            IncomingCallAnswered(number);
          }
        }else if(TelephonyManager.EXTRA_STATE_IDLE.equals(state)){
          // Incomming/Outgoing Call ends
          if(status == 1){
            // Incoming Missed or Rejected
            PhoneCallEnded(1, number);
          }else if(status == 3){
            // Incoming Answer Ended
            PhoneCallEnded(2, number);
          }else if(status == 2){
            // Outgoing Ended
            PhoneCallEnded(3, number);
          }
          status = 0;
          number = "";
        }
      }else if(Intent.ACTION_NEW_OUTGOING_CALL.equals(action)){
        // Outgoing call dialled
        status = 2;
        number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        PhoneCallStarted(2, number);
      }
    }
  }

  /**
   * Registers phonecall state monitor
   */
  private void registerCallStateMonitor(){
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
    intentFilter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
    context.registerReceiver(callStateReceiver, intentFilter);
    didRegisterReceiver = true;
  }

  /**
   * Unregisters phonecall state monitor
   */
  private void unregisterCallStateMonitor() {
    if (didRegisterReceiver) {
      context.unregisterReceiver(callStateReceiver);
      didRegisterReceiver = false;
    }
  }

  @Override
  public void onDestroy() {
    unregisterCallStateMonitor();
  }
}
