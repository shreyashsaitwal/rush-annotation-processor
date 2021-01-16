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
  private final ExtensionFieldInfo ext;

  public Property(Element element, ExtensionFieldInfo ext) {
    this.element = element;
    this.ext = ext;
  }

  public Property build() throws IllegalAccessException {
    ExecutableElement executableElement = (ExecutableElement) element;
    name = executableElement.getSimpleName().toString();

    if (!ext.getBlockProps().containsKey(name)) {
      throw new IllegalAccessException("Unable to find corresponding @SimpleProperty annotation for designer property \"" + name + "\".");
    } else {
      defaultVal = executableElement.getAnnotation(DesignerProperty.class).defaultValue();
      editorType = executableElement.getAnnotation(DesignerProperty.class).defaultValue();
      args = executableElement.getAnnotation(DesignerProperty.class).editorArgs();
      alwaysSend = executableElement.getAnnotation(DesignerProperty.class).alwaysSend();

      if (!defaultVal.equals("")) {
        ext.getBlockProps().get(name).setDefaultVal(defaultVal);
      }

      if (alwaysSend) {
        ext.getBlockProps().get(name).setAlwaysSend(true);
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
