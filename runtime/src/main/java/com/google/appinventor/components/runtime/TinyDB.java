// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0
// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package com.google.appinventor.components.runtime;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.JsonUtil;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * `TinyDB` is a non-visible component that stores data for an app.
 *
 * Apps created with App Inventor are initialized each time they run. This means that if an app
 * sets the value of a variable and the user then quits the app, the value of that variable will
 * not be remembered the next time the app is run. In contrast, TinyDB is a persistent data store
 * for the app. The data stored in a `TinyDB` will be available each time the app is run. An
 * example might be a game that saves the high score and retrieves it each time the game is played.
 *
 * Data items consist of tags and values. To store a data item, you specify the tag it should be
 * stored under. The tag must be a text block, giving the data a name. Subsequently, you can
 * retrieve the data that was stored under a given tag.
 *
 * You cannot use the `TinyDB` to pass data between two different apps on the phone, although you
 * can use the `TinyDB` to share data between the different screens of a multi-screen app.
 *
 * When you are developing apps using the AI Companion, all the apps using that Companion will
 * share the same `TinyDB`. That sharing will disappear once the apps are packaged and installed on
 * the phone. During development you should be careful to clear the Companion app's data each time
 * you start working on a new app.
 *
 * @author markf@google.com (Mark Friedman)
 */

public class TinyDB extends AndroidNonvisibleComponent implements Component, Deleteable {

  public static final String DEFAULT_NAMESPACE="TinyDB1";

  private SharedPreferences sharedPreferences;
  private String namespace;

  private Context context;  // this was a local in constructor and final not private


  /**
   * Creates a new TinyDB component.
   *
   * @param container the Form that this component is contained in.
   */
  public TinyDB(ComponentContainer container) {
    super(container.$form());
    context = (Context) container.$context();
    Namespace(DEFAULT_NAMESPACE);
  }

  /**
   * Namespace for storing data. All `TinyDB` components in the same app with the same `Namespace`
   * property access the same data.
   *
   *   Each `Namespace` represents a single data store that is shared by the entire app. If you
   * have multiple `TinyDB` components with the same `Namespace` within an app, they use the same
   * data store, even if they are on different screens. If you only need one data store for your
   * app, it's not necessary to set a `Namespace`.
   *
   * @param namespace the alternate namespace to use for the TinyDB
   */
  @SimpleProperty(description = "Namespace for storing data.")
  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING, defaultValue = DEFAULT_NAMESPACE)
  public void Namespace(String namespace) {
    this.namespace = namespace;
    sharedPreferences = context.getSharedPreferences(namespace, Context.MODE_PRIVATE);
  }

  @SimpleProperty(description = "Namespace for storing data.")
  public String Namespace() {
    return namespace;
  }

  /**
   * Store the given `valueToStore`{:.variable.block} under the given `tag`{:.text.block}.
   * The storage persists on the phone when the app is restarted.
   *
   * @param tag The tag to use
   * @param valueToStore The value to store. Can be any type of value (e.g.
   * number, text, boolean or list).
   */
  @SimpleFunction(description = "Store the given value under the given tag.  The storage persists "
      + "on the phone when the app is restarted.")
  public void StoreValue(final String tag, final Object valueToStore) {
    final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    try {
      sharedPrefsEditor.putString(tag, JsonUtil.getJsonRepresentation(valueToStore));
      sharedPrefsEditor.commit();
    } catch (JSONException e) {
      throw new YailRuntimeError("Value failed to convert to JSON.", "JSON Creation Error.");
    }
  }

  /**
   * Retrieve the value stored under the given `tag`{:.text.block}.  If there's no such tag, then
   * return `valueIfTagNotThere`{:.variable.block}.
   *
   * @param tag The tag to use
   * @param valueIfTagNotThere The value returned if tag in not in TinyDB
   * @return The value stored under the tag. Can be any type of value (e.g.
   * number, text, boolean or list).
   */
  @SimpleFunction(description = "Retrieve the value stored under the given tag. If there's no "
      + "such tag, then return valueIfTagNotThere.")
  public Object GetValue(final String tag, final Object valueIfTagNotThere) {
    try {
      String value = sharedPreferences.getString(tag, "");
      // If there's no entry with tag as a key then return the empty string.
      //    was  return (value.length() == 0) ? "" : JsonUtil.getObjectFromJson(value);
      return (value.length() == 0) ? valueIfTagNotThere : JsonUtil.getObjectFromJson(value, true);
    } catch (JSONException e) {
      throw new YailRuntimeError("Value failed to convert from JSON.", "JSON Creation Error.");
    }
  }

   /**
   * Return a list of all the tags in the data store.
   *
   * @return a list of all keys.
   */
  @SimpleFunction(description = "Return a list of all the tags in the data store.")
  public Object GetTags() {
    List<String> keyList = new ArrayList<String>();
    Map<String,?> keyValues = sharedPreferences.getAll();
    // here is the simple way to get keys
    keyList.addAll(keyValues.keySet());
    java.util.Collections.sort(keyList);
    return keyList;
  }

  /**
   * Clear the entire data store.
   *
   */
  @SimpleFunction(description = "Clear the entire data store.")
  public void ClearAll() {
    final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    sharedPrefsEditor.clear();
    sharedPrefsEditor.commit();
  }

  /**
   * Clear the entry with the given `tag`{:.text.block}.
   *
   * @param tag The tag to remove.
   */
  @SimpleFunction(description = "Clear the entry with the given tag.")
  public void ClearTag(final String tag) {
    final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    sharedPrefsEditor.remove(tag);
    sharedPrefsEditor.commit();
  }

  @Override
  public void onDelete() {
    final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    sharedPrefsEditor.clear();
    sharedPrefsEditor.commit();
  }
}
