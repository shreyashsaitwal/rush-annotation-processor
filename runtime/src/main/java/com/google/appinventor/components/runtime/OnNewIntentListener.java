// Copyright 2008 Google Inc. All Rights Reserved.

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.content.Intent;

/**
 * Listener for distributing the Activity onStop() method to interested components.
 *
 * @author markf@google.com (Mark Friedman)
 */

public interface OnNewIntentListener {
  public void onNewIntent(Intent intent);
}
