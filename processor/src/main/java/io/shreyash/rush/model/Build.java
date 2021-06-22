package io.shreyash.rush.model;

public class Build {
  private Desugar desugar;
  private Kotlin kotlin;
  private Release release;

  public Desugar getDesugar() {
    if (desugar == null) {
      return new Desugar();
    }
    return desugar;
  }

  public void setDesugar(Desugar desugar) {
    this.desugar = desugar;
  }

  public Kotlin getKotlin() {
    if (kotlin == null) {
      return new Kotlin();
    }
    return kotlin;
  }

  public void setKotlin(Kotlin kotlin) {
    this.kotlin = kotlin;
  }

  public Release getRelease() {
    if (release == null) {
      return new Release();
    }
    return release;
  }

  public void setRelease(Release release) {
    this.release = release;
  }
}
