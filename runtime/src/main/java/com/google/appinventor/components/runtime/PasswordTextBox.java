// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.appinventor.components.annotations.SimpleProperty;

/**
 * Users enter passwords in a password text box component, which hides the text that has been typed in it.
 *
 * ![Example of a PasswordTextBox](images/passwordtextbox.png)
 *
 * A password text box is the same as the ordinary {@link TextBox} component, except that it does
 * not display the characters typed by the user.
 *
 * You can get or set the value of the text in the box with the {@link #Text(String)} property. If
 * {@link #Text()} is blank, you can use the {@link #Hint(String)} property to provide the user
 * with a suggestion of what to type. The {@link #Hint()} appears as faint text in the box.
 *
 * Password text box components are usually used with a {@link Button} component. The user taps the
 * {@code Button} after entering text.
*/

public final class PasswordTextBox extends TextBoxBase {

  private boolean passwordVisible;

  /**
   * Creates a new PasswordTextBox component.
   *
   * @param container  container, component will be placed in
   */
  public PasswordTextBox(ComponentContainer container) {
    super(container, new EditText(container.$context()));

    // make the box single line
    view.setSingleLine(true);
    // Add a transformation method to hide password text.   This must
    // be done after the SingleLine command
    view.setTransformationMethod(new PasswordTransformationMethod());

    // make sure the done action is Done and not Next.  See comment in Textbox.java
    view.setImeOptions(EditorInfo.IME_ACTION_DONE);

    PasswordVisible(false);

  }

  @SimpleProperty(description = "Visibility of password.")
  public void PasswordVisible(boolean visible){
    passwordVisible=visible;
    if(visible){
      view.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }else{
      view.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }
  }

  /**
   * Specifies whether the password is hidden (default) or shown.
   *
   * @return true if the password should be shown, otherwise false.
   */
  @SimpleProperty(description = "Visibility of password.")
  public boolean PasswordVisible(){
    return passwordVisible;
  }

}
