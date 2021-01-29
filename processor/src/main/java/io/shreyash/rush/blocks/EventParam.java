package io.shreyash.rush.blocks;

import io.shreyash.rush.util.ConvertToYailType;

import javax.lang.model.element.VariableElement;

public class EventParam {
  private final String name;
  private final String type;

  public EventParam(VariableElement param) {
    name = param.getSimpleName().toString();
    type = ConvertToYailType.convert(param.asType().toString());
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}
