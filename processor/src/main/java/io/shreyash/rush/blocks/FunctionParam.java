package io.shreyash.rush.blocks;

import io.shreyash.rush.util.ConvertToYailType;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class FunctionParam {
  private String name;
  private String type;

  public FunctionParam(VariableElement param, Messager messager, String parent) {
    name = param.getSimpleName().toString();
    try {
      type = ConvertToYailType.convert(param.asType().toString());
    } catch (IllegalStateException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, "ERR @SimpleFunction '" + parent + "': Can't convert parameter type '" + param.asType() + "' (parameter '" + name + "') to YAIL type.");
    }
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}
