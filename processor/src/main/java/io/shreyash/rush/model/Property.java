package io.shreyash.rush.model;

import com.google.appinventor.components.annotations.DesignerProperty;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

public class Property {
  private String name;
  private String defaultVal;
  private String editorType;
  private String[] args;
  private boolean alwaysSend;

  private final Element element;
  private final ExtensionInfo ext;

  public Property(Element element, ExtensionInfo ext) {
    this.element = element;
    this.ext = ext;
  }

  public Property build() throws IllegalAccessException {
    ExecutableElement executableElement = (ExecutableElement) element;
    this.name = executableElement.getSimpleName().toString();

    if (!ext.getBlockProps().containsKey(this.name)) {
      throw new IllegalAccessException("Unable to find corresponding @SimpleProperty annotation for designer property \"" + this.name + "\".");
    } else {
      this.defaultVal = executableElement.getAnnotation(DesignerProperty.class).defaultValue();
      this.editorType = executableElement.getAnnotation(DesignerProperty.class).defaultValue();
      this.args = executableElement.getAnnotation(DesignerProperty.class).editorArgs();
      this.alwaysSend = executableElement.getAnnotation(DesignerProperty.class).alwaysSend();

      if (!this.defaultVal.equals("")) {
        ext.getBlockProps().get(this.name).setDefaultVal(this.defaultVal);
      }

      if (this.alwaysSend) {
        ext.getBlockProps().get(this.name).setAlwaysSend(true);
      }
    }

    return this;
  }

  public String getName() {
    return name;
  }

  public String getDefaultVal() {
    return defaultVal;
  }

  public String getEditorType() {
    return editorType;
  }

  public String[] getArgs() {
    return args;
  }

  public boolean isAlwaysSend() {
    return alwaysSend;
  }
}
