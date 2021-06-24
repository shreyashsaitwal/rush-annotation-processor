package io.shreyash.rush.model;

public class Desugar {
  private boolean enable;
  private boolean desugar_deps;

  public boolean isEnable() {
    return enable;
  }

  public boolean isDesugar_deps() {
    return desugar_deps;
  }

  public void setDesugar_deps(boolean desugar_deps) {
    this.desugar_deps = desugar_deps;
  }

  public boolean getEnable() {
    return enable;
  }

  public void setEnable(boolean enable) {
    this.enable = enable;
  }
}
