package io.shreyash.rush.model;

import java.util.ArrayList;

public class RushYaml {
  private String name;
  private String description;

  private ArrayList<String> authors = new ArrayList<>();
  private ArrayList<String> deps = new ArrayList<>();

  private String homepage;
  private String license;

  // Fields' names need to correspond to the actual rush.yml fields
  private int min_sdk;

  Version version;
  Assets assets;
  Kotlin kotlin;
  Release release;

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ArrayList<String> getAuthors() {
    return authors;
  }

  public ArrayList<String> getDeps() {
    return deps;
  }

  public Version getVersion() {
    return version;
  }

  public Assets getAssets() {
    return assets;
  }

  public String getHomepage() {
    return homepage;
  }

  public Kotlin getKotlin() {
    if (kotlin == null) {
      return new Kotlin();
    }
    return kotlin;
  }

  public String getLicense() {
    if (license == null) {
      return "";
    }
    return license;
  }

  public int getMin_sdk() {
    return min_sdk;
  }

  public Release getRelease() {
    if (release == null) {
      return new Release();
    }
    return release;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setAuthors(ArrayList<String> authors) {
    this.authors = authors;
  }

  public void setDeps(ArrayList<String> deps) {
    this.deps = deps;
  }

  public void setVersion(Version versionObject) {
    this.version = versionObject;
  }

  public void setAssets(Assets assetsObject) {
    this.assets = assetsObject;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public void setKotlin(Kotlin kotlinObject) {
    this.kotlin = kotlinObject;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public void setMin_sdk(int min_sdk) {
    this.min_sdk = min_sdk;
  }

  public void setRelease(Release releaseObject) {
    this.release = releaseObject;
  }
}

