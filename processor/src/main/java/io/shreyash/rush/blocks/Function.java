package io.shreyash.rush.blocks;

import com.google.appinventor.components.annotations.SimpleFunction;
import io.shreyash.rush.util.CheckName;
import io.shreyash.rush.util.ConvertToYailType;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;

public class Function {
  private final ArrayList<FunctionParam> params = new ArrayList<>();
  private final Element element;
  private final Messager messager;
  private boolean deprecated;
  private String name;
  private String description;
  private String returnType;

  public Function(Element element, Messager messager) {
    this.element = element;
    this.messager = messager;
  }

  public Function build() {
    if (!CheckName.isPascalCase(element)) {
      messager.printMessage(Diagnostic.Kind.WARNING, "Function '" + element.getSimpleName() + "' should follow PascalCase naming convention.");
    }
    ExecutableElement executableElement = ((ExecutableElement) element);
    name = executableElement.getSimpleName().toString();
    description = executableElement.getAnnotation(SimpleFunction.class).description();
    deprecated = executableElement.getAnnotation(Deprecated.class) != null;
    returnType = ConvertToYailType.convert(executableElement.getReturnType().toString());

    for (VariableElement param : executableElement.getParameters()) {
      if (!CheckName.isCamelCase(param)) {
        messager.printMessage(Diagnostic.Kind.WARNING, "Parameter '" + param.getSimpleName() + "' of Function '" + element.getSimpleName() + "' should follow camelCase naming convention.");
      }
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

