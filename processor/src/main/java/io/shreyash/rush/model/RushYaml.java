package io.shreyash.rush.model;

import java.util.ArrayList;

public class RushYaml {
  private Version version;
  private Assets assets;
  private Build build;
  private Release release;

  private String name;
  private String description;
  private ArrayList<String> authors = new ArrayList<>();
  private ArrayList<String> deps = new ArrayList<>();
  private String homepage;
  private String license;

  // Fields' names need to correspond to the actual rush.yml fields
  private String license_url;
  private int min_sdk;

  public Build getBuild() {
    return build;
  }

  public void setBuild(Build build) {
    this.build = build;
  }

  public Release getRelease() {
    return release;
  }

  public void setRelease(Release release) {
    this.release = release;
  }

  public String getLicense_url() {
    return license_url;
  }

  public void setLicense_url(String license_url) {
    this.license_url = license_url;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ArrayList<String> getAuthors() {
    return authors;
  }

  public void setAuthors(ArrayList<String> authors) {
    this.authors = authors;
  }

  public ArrayList<String> getDeps() {
    return deps;
  }

  public void setDeps(ArrayList<String> deps) {
    this.deps = deps;
  }

  public Version getVersion() {
    return version;
  }

  public void setVersion(Version versionObject) {
    this.version = versionObject;
  }

  public Assets getAssets() {
    return assets;
  }

  public void setAssets(Assets assetsObject) {
    this.assets = assetsObject;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public String getLicense() {
    if (license == null) {
      return "";
    }
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public int getMin_sdk() {
    return min_sdk;
  }

  public void setMin_sdk(int min_sdk) {
    this.min_sdk = min_sdk;
  }
}

