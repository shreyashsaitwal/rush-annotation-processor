// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.StringJoiner;

/**
 * These commands post to the Web and get responses that are assumed
 * to be JSON structures: a string, a JSON array, or a JSON object.
 * It's up to the caller of these routines to decide which version
 * to use, and to decode the response.
 *
 * @author halabelson@google.com (Hal Abelson)
 */
public class WebServiceUtil {
  private static final String LOG_TAG = "WebServiceUtil";

  private WebServiceUtil(){
  }

  /**
   * Returns the one <code>WebServiceUtil</code> instance
   * @return the one <code>WebServiceUtil</code> instance
   */
//  public static WebServiceUtil getInstance() {
//    // This needs to be here instead of in the constructor because
//    // it uses classes that are in the AndroidSDK and thus would
//    // cause Stub! errors when running the component descriptor.
//    synchronized(httpClientSynchronizer) {
//      if (httpClient == null) {
//        SchemeRegistry schemeRegistry = new SchemeRegistry();
//        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
//        BasicHttpParams params = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
//        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
//        ConnManagerParams.setMaxTotalConnections(params, 20);
//        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params,
//            schemeRegistry);
//        WebServiceUtil.httpClient = new DefaultHttpClient(manager, params);
//      }
//    }
//    return INSTANCE;
//  }

  /**
   * Make a post command to serviceURL with params and return the
   * response String as a JSON array.
   *
   * @param serviceURL The URL of the server to post to.
   * @param commandName The path to the command.
   * @param params A List of NameValuePairs to send as parameters
   * with the post.
   * @param callback A callback function that accepts a JSON array
   * on success.
   */
  @RequiresApi(api = Build.VERSION_CODES.N)
  public static void postCommandReturningArray(String serviceURL, String commandName,
                                               List<NameValuePair> params, final AsyncCallbackPair<JSONArray> callback) {
    AsyncCallbackPair<String> thisCallback = new AsyncCallbackPair<String>() {
      public void onSuccess(String httpResponseString) {
        try {
          callback.onSuccess(new JSONArray(httpResponseString));
        } catch (JSONException e) {
          callback.onFailure(e.getMessage());
        }
      }
      public void onFailure(String failureMessage) {
        callback.onFailure(failureMessage);
      }
    };
    postCommand(serviceURL, commandName, params, thisCallback);
  }

  /**
   * Make a post command to serviceURL with paramaterss and
   * return the response String as a JSON object.
   *
   * @param serviceURL The URL of the server to post to.
   * @param commandName The path to the command.
   * @param params A List of NameValuePairs to send as parameters
   * with the post.
   * @param callback A callback function that accepts a JSON object
   * on success.
   */
  @RequiresApi(api = Build.VERSION_CODES.N)
  public void postCommandReturningObject(final String serviceURL, final String commandName,
                                         List<NameValuePair> params, final AsyncCallbackPair<JSONObject> callback) {
    AsyncCallbackPair<String> thisCallback = new AsyncCallbackPair<String>() {
    public void onSuccess(String httpResponseString) {
        try {
          callback.onSuccess(new JSONObject(httpResponseString));
        } catch (JSONException e) {
          callback.onFailure(e.getMessage());
        }
      }
      public void onFailure(String failureMessage) {
        callback.onFailure(failureMessage);
      }
    };
    postCommand(serviceURL, commandName, params, thisCallback);
  }

  /**
   * Make a post command to serviceURL with params and return the
   * response String.
   *
   * @param serviceURL The URL of the server to post to.
   * @param commandName The path to the command.
   * @param params A List of NameValuePairs to send as parameters
   * with the post.
   * @param callback A callback function that accepts a String on
   * success.
   */
  @RequiresApi(api = Build.VERSION_CODES.N)
  public static void postCommand(final String serviceURL, final String commandName,
                                 List<NameValuePair> params, AsyncCallbackPair<String> callback) {
    Log.d(LOG_TAG, "Posting " + commandName + " to " + serviceURL + " with arguments " + params);

    if (serviceURL == null || serviceURL.equals("")) {
      callback.onFailure("No service url to post command to.");
    }

    try {
      final URL url = new URL(serviceURL + "/" + commandName);
      HttpURLConnection http = (HttpURLConnection) url.openConnection();
      http.setRequestMethod("POST");
      http.setDoOutput(true);
      http.setRequestProperty("Accept", "application/json");

      StringJoiner sj = new StringJoiner("&");
      for (NameValuePair pair : params) {
        sj.add(
            URLEncoder.encode(pair.getName(), "UTF-8") + "=" +
            URLEncoder.encode(pair.getValue(), "UTF-8")
        );
      }
      byte[] bytes = sj.toString().getBytes();

      http.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
      http.setRequestProperty( "charset", "utf-8");
      http.setRequestProperty( "Content-Length", Integer.toString(bytes.length));
      http.setUseCaches( false );
      try( DataOutputStream wr = new DataOutputStream(http.getOutputStream())) {
        wr.write(bytes);
      }
      http.connect();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
