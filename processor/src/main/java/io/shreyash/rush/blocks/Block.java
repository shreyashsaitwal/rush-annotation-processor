package io.shreyash.rush.blocks;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import io.shreyash.rush.util.ConvertToYailType;
import shaded.org.json.JSONObject;

public abstract class Block {
  private final ExecutableElement element;

  protected Block(Element element) {
    this.element = (ExecutableElement) element;
  }

  /**
   * @return The description of this block
   */
  abstract String description();

  /**
   * Validates the use of the appropriate annotation and prints
   * warnings and errors as appropriate.
   */
  abstract void validate();

  /**
   * @return JSON representation of this block that is later used to
   * construct the `components.json` descriptor file.
   */
  public abstract JSONObject asJsonObject();

  /**
   * @return Name of this block.
   */
  public String name() {
    return this.element.getSimpleName().toString();
  }

  /**
   * @return True if this block is deprecated, else false.
   */
  public boolean deprecated() {
    return this.element.getAnnotation(Deprecated.class) != null;
  }

  /**
   * @return YAIL representation of the return type of this block.
   */
  public String returnType() {
    if (!element.getReturnType().toString().equals("void")) {
      return ConvertToYailType.convert(element.getReturnType().toString());
    } else {
      return null;
    }
  }
}
