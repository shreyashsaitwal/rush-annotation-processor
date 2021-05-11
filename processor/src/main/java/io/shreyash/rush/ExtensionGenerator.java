// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2021 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

// This file has been modified by Shreyash Saitwal to add support for extensions
// built with Rush build tool (https://github.com/ShreyashSaitwal/rush-cli)

package io.shreyash.rush;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import shaded.org.json.JSONArray;
import shaded.org.json.JSONException;
import shaded.org.json.JSONObject;

public class ExtensionGenerator {

  private static final Map<String, List<ExtensionInfo>> externalComponentsByPackage =
      new TreeMap<>();
  private static String rawDir;
  private static String classesDir;
  private static String depsDir;
  private static String rawClassesDir;
  private static boolean useFQCN = false;
  private static String rootDir;

  public static void main(String[] args) {
    String simple_component_json = null;
    String simple_component_build_info_json = null;
    try {
      simple_component_json = readFile(args[0], Charset.defaultCharset());
      simple_component_build_info_json = readFile(args[1], Charset.defaultCharset());
    } catch (IOException e) {
      e.printStackTrace();
    }

    rawDir = args[2];
    classesDir = args[3];
    depsDir = args[4];
    rawClassesDir = args[5];
    useFQCN = Boolean.parseBoolean(args[6]);
    rootDir = args[7];

    JSONArray simpleComponentDescriptors = new JSONArray(simple_component_json);
    JSONArray simpleComponentBuildInfos = new JSONArray(simple_component_build_info_json);
    Map<String, JSONObject> buildInfos = buildInfoAsMap(simpleComponentBuildInfos);

    for (int i = 0; i < simpleComponentDescriptors.length(); i++) {
      JSONObject componentDescriptor = (JSONObject) simpleComponentDescriptors.get(i);
      if (componentDescriptor.get("external").toString().equals("true")) {
        ExtensionInfo info = new ExtensionInfo(componentDescriptor, buildInfos.get(componentDescriptor.getString("type")));
        if (!externalComponentsByPackage.containsKey(info.packageName)) {
          externalComponentsByPackage.put(info.packageName, new ArrayList<>());
        }
        externalComponentsByPackage.get(info.packageName).add(info);
      }
    }

    try {
      generateAllExtensions();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Map<String, JSONObject> buildInfoAsMap(JSONArray buildInfos) throws JSONException {
    Map<String, JSONObject> result = new HashMap<>();
    for (int i = 0; i < buildInfos.length(); i++) {
      JSONObject componentBuildInfo = buildInfos.getJSONObject(i);
      result.put(componentBuildInfo.getString("type"), componentBuildInfo);
    }
    return result;
  }

  private static void generateAllExtensions() throws IOException, JSONException {
    for (Map.Entry<String, List<ExtensionInfo>> entry : externalComponentsByPackage.entrySet()) {
      String name = useFQCN && entry.getValue().size() == 1 ? entry.getValue().get(0).type : entry.getKey();
      generateExternalComponentDescriptors(name, entry.getValue());
      for (ExtensionInfo info : entry.getValue()) {
        copyIcon(name, info.descriptor);
        copyLicense(name, info.descriptor);
        copyAssets(name, info.descriptor);
      }
      generateExternalComponentBuildFiles(name, entry.getValue());
      generateExternalComponentOtherFiles(name);
    }
  }

  private static void generateExternalComponentDescriptors(String packageName, List<ExtensionInfo> infos)
      throws IOException, JSONException {
    StringBuilder sb = new StringBuilder("[");
    boolean first = true;
    for (ExtensionInfo info : infos) {
      if (!first) {
        sb.append(',');
      } else {
        first = false;
      }
      sb.append(info.descriptor.toString(1));
    }
    sb.append(']');
    String components = sb.toString();
    String extensionDirPath = rawDir + File.separator + packageName;
    ensureDirectory(extensionDirPath, "ERR Unable to create build directory for [" + packageName + "].");
    FileWriter jsonWriter = null;
    try {
      jsonWriter = new FileWriter(extensionDirPath + File.separator + "components.json");
      jsonWriter.write(components);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (jsonWriter != null) {
        jsonWriter.close();
      }
    }
    // Write legacy format to transition developers
    try {
      jsonWriter = new FileWriter(extensionDirPath + File.separator + "component.json");
      jsonWriter.write(infos.get(0).descriptor.toString(1));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (jsonWriter != null) {
        jsonWriter.close();
      }
    }
  }

  private static void generateExternalComponentBuildFiles(String packageName, List<ExtensionInfo> extensions) throws IOException {
    String extensionDirPath = rawDir + File.separator + packageName;
    String extensionTempDirPath = rawClassesDir + File.separator + packageName;
    String extensionFileDirPath = extensionDirPath + File.separator + "files";
    copyRelatedExternalClasses(classesDir, extensionTempDirPath);

    JSONArray buildInfos = new JSONArray();
    for (ExtensionInfo info : extensions) {
      JSONObject componentBuildInfo = info.buildInfo;
      try {
        JSONArray librariesNeeded = componentBuildInfo.getJSONArray("libraries");
        for (int j = 0; j < librariesNeeded.length(); ++j) {
          // Copy Library files for Unjar and Jaring
          String library = librariesNeeded.getString(j);

          if (library.equals("kotlin-stdlib.jar")) {
            copyFile(Paths.get(rootDir, ".rush", "dev-deps", "kotlin-stdlib.jar").toAbsolutePath().toString(),
                extensionTempDirPath + File.separator + library);
          } else {
            copyFile(depsDir + File.separator + library,
                extensionTempDirPath + File.separator + library);
          }
        }
        //empty the libraries meta-data to avoid redundancy
        componentBuildInfo.put("libraries", new JSONArray());
      } catch (JSONException e) {
        // bad
        throw new IllegalStateException("ERR An unexpected error occurred while parsing simple_components.json",
            e);
      }
      buildInfos.put(componentBuildInfo);
    }

    // Create component_build_info.json
    ensureDirectory(extensionFileDirPath, "ERR Unable to create path for component_build_info.json");
    FileWriter extensionBuildInfoFile = null;
    try {
      extensionBuildInfoFile = new FileWriter(extensionFileDirPath + File.separator + "component_build_infos.json");
      extensionBuildInfoFile.write(buildInfos.toString());

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (extensionBuildInfoFile != null) {
        extensionBuildInfoFile.flush();
        extensionBuildInfoFile.close();
      }
    }
    // Write out legacy component_build_info.json to transition developers
    try {
      extensionBuildInfoFile = new FileWriter(extensionFileDirPath + File.separator + "component_build_info.json");
      extensionBuildInfoFile.write(buildInfos.get(0).toString());
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    } finally {
      if (extensionBuildInfoFile != null) {
        extensionBuildInfoFile.close();
      }
    }
  }

  private static void copyIcon(String packageName, JSONObject componentDescriptor)
      throws IOException, JSONException {
    String icon = componentDescriptor.getString("iconName");
    if (icon.equals("") || icon.startsWith("http:") || icon.startsWith("https:")) {
      // Icon will be loaded from the web
      return;
    }
    File image = Paths.get(rootDir, "assets", icon.split("aiwebres/")[1]).toFile();
    if (image.exists()) {
      File dstIcon = new File(rawDir + File.separator + packageName + File.separator + icon);
      ensureDirectory(dstIcon.getParent(), "ERR Unable to copy extension icon [" + icon + "] to it's destination directory.");
      copyFile(image.getAbsolutePath(), dstIcon.getAbsolutePath());
    }
  }

  private static void copyLicense(String packageName, JSONObject componentDescriptor)
      throws IOException, JSONException {
    String license = componentDescriptor.getString("licenseName");
    if ("".equals(license) || license.startsWith("http:") || license.startsWith("https:")) {
      // License will be loaded from the web
      return;
    }
    File licenseFile = Paths.get(rootDir, "LICENSE").toFile();
    if (licenseFile.exists()) {
      File destinationLicense = new File(rawDir + File.separator + packageName + File.separator + "aiwebres" + File.separator + "LICENSE");
      ensureDirectory(destinationLicense.getParent(), "ERR Unable to copy LICENSE file to it's destination directory.");
      copyFile(licenseFile.getAbsolutePath(), destinationLicense.getAbsolutePath());
    }
  }

  private static void copyAssets(String packageName, JSONObject componentDescriptor)
      throws IOException, JSONException {
    JSONArray assets = componentDescriptor.optJSONArray("assets");
    if (assets == null) {
      return;
    }

    // Get asset source directory
    File assetSrcDir = new File(rootDir, "assets");
    if (!assetSrcDir.exists() || !assetSrcDir.isDirectory()) {
      return;
    }

    // Get asset dest directory
    File destDir = new File(rawDir + File.separator + packageName + File.separator);
    File assetDestDir = new File(destDir, "assets");
    ensureFreshDirectory(assetDestDir.getPath());

    // Copy assets
    for (int i = 0; i < assets.length(); i++) {
      String asset = assets.getString(i);
      if (!asset.isEmpty()) {
        if (!copyFile(assetSrcDir.getAbsolutePath() + File.separator + asset,
            assetDestDir.getAbsolutePath() + File.separator + asset)) {
          throw new IllegalStateException("ERR Unable to copy asset [" + asset + "] to destination.");
        }
      }
    }
  }

  private static void generateExternalComponentOtherFiles(String packageName) throws IOException {
    String extensionDirPath = rawDir + File.separator + packageName;

    // Create extension.properties
    StringBuilder extensionPropertiesString = new StringBuilder();
    extensionPropertiesString.append("type=external\n");
    FileWriter extensionPropertiesFile = new FileWriter(extensionDirPath + File.separator + "extension.properties");
    try {
      extensionPropertiesFile.write(extensionPropertiesString.toString());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      extensionPropertiesFile.flush();
      extensionPropertiesFile.close();
    }
  }

  /**
   * Read a file and returns its content
   *
   * @param path     the path of the file to be read
   * @param encoding the encoding system
   */
  private static String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  /**
   * Copy one file to another. If destination file does not exist, it is created.
   *
   * @param srcPath absolute path to source file
   * @param dstPath absolute path to destination file
   * @return {@code true} if the copy succeeds, {@code false} otherwise
   */
  private static Boolean copyFile(String srcPath, String dstPath) {
    try {
      FileInputStream in = new FileInputStream(srcPath);
      FileOutputStream out = new FileOutputStream(dstPath);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
      in.close();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Copy compiled classes of all the Java files in src directory
   *
   * @param srcPath  the folder in which to check compiled classes
   * @param destPath where the compiled classes will be copied
   */
  private static void copyRelatedExternalClasses(final String srcPath,
                                                 final String destPath) throws IOException {
    File srcFolder = new File(srcPath);
    File[] files = srcFolder.listFiles();
    if (files == null) {
      return;
    }
    for (File fileEntry : files) {
      if (fileEntry.isFile()) {
        copyFile(fileEntry.getAbsolutePath(), destPath + File.separator + fileEntry.getName());
      } else if (fileEntry.isDirectory()) {
        String newDestPath = destPath + fileEntry.getAbsolutePath().substring(srcFolder.getAbsolutePath().length());
        ensureDirectory(newDestPath, "ERR Unable to create temporary path for extension build.");
        copyRelatedExternalClasses(fileEntry.getAbsolutePath(), newDestPath);
      }
    }
  }

  private static boolean deleteRecursively(File dirOrFile) {
    if (dirOrFile.isFile()) {
      return dirOrFile.delete();
    } else {
      boolean result = true;
      File[] children = dirOrFile.listFiles();
      if (children != null) {
        for (File child : children) {
          result = result && deleteRecursively(child);
        }
      }
      return result && dirOrFile.delete();
    }
  }

  private static void ensureFreshDirectory(String path) throws IOException {
    File file = new File(path);
    if (file.exists() && !deleteRecursively(file)) {
      throw new IOException("ERR Unable to delete the assets directory for the extension.");
    }
    if (!file.mkdirs()) {
      throw new IOException("ERR Unable to delete the assets directory for the extension.");
    }
  }

  private static void ensureDirectory(String path, String errorMessage) throws IOException {
    File file = new File(path);
    if (!file.exists() && !file.mkdirs()) {
      throw new IOException(errorMessage);
    }
  }

  /**
   * Container class to store information about an extension.
   */
  private static class ExtensionInfo {
    private final String type;
    private final String packageName;
    private final JSONObject descriptor;
    private final JSONObject buildInfo;

    ExtensionInfo(JSONObject descriptor, JSONObject buildInfo) {
      this.descriptor = descriptor;
      this.buildInfo = buildInfo;
      this.type = descriptor.optString("type");
      this.packageName = type.substring(0, type.lastIndexOf('.'));
    }
  }
}
