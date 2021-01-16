package io.shreyash.rush.model;

import com.google.appinventor.components.annotations.SimpleFunction;
import io.shreyash.rush.util.ConvertToYailType;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;

public class Function {
  private boolean deprecated;
  private String name;
  private String description;
  private String returnType;
  private final ArrayList<FunctionParam> params = new ArrayList<>();
  private final Element element;

  public Function(Element element) {
    this.element = element;
  }

  public Function build() {
    ExecutableElement executableElement = ((ExecutableElement) element);
    name = executableElement.getSimpleName().toString();
    description = executableElement.getAnnotation(SimpleFunction.class).description();
    deprecated = executableElement.getAnnotation(Deprecated.class) != null;
    returnType = ConvertToYailType.convert(executableElement.getReturnType().toString());

    for (VariableElement param : executableElement.getParameters()) {
      params.add(new FunctionParam(param));
    }

    return this;
  }

  public String getReturnType() {
    return returnType;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public ArrayList<FunctionParam> getParams() {
    return params;
  }
}

