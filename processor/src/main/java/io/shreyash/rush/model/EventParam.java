package io.shreyash.rush.model;

import io.shreyash.rush.util.ConvertToYailType;

import javax.lang.model.element.VariableElement;

public class EventParam {
  private String name;

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  private String type;

  public EventParam(VariableElement param) {
    this.name = param.getSimpleName().toString();
    this.type = ConvertToYailType.convert(param.asType().toString());
  }
}
