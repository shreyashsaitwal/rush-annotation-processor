package io.shreyash.rush.model;

import java.util.ArrayList;

public class Assets {
  private String icon;
  private ArrayList<String> other = new ArrayList<>();

  public String getIcon() {
    return icon;
  }

  public ArrayList<String> getOther() {
    return other;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public void setOther(ArrayList<String> other) {
    this.other = other;
  }
}