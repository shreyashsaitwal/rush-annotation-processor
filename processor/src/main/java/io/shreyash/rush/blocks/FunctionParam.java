package io.shreyash.rush.blocks;

import io.shreyash.rush.util.ConvertToYailType;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;

public class FunctionParam {
  private final String name;
  private final String type;

  public FunctionParam(VariableElement param, Messager messager) {
    name = param.getSimpleName().toString();
    type = ConvertToYailType.convert(param.asType().toString(), messager);
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}
