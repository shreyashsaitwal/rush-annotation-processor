package io.shreyash.rush.model;

import io.shreyash.rush.util.ConvertToYailType;

import javax.lang.model.element.VariableElement;

public class FunctionParam {
  private String name;
  private String type;

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public FunctionParam(VariableElement param) {
    this.name = param.getSimpleName().toString();
    this.type = ConvertToYailType.convert(param.asType().toString());
  }
}
