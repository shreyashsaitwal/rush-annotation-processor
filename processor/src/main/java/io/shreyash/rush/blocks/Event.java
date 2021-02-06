package io.shreyash.rush.blocks;

import com.google.appinventor.components.annotations.SimpleEvent;
import io.shreyash.rush.util.CheckName;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.ArrayList;

public class Event {
  private final ArrayList<EventParam> params = new ArrayList<>();
  private final Element element;
  private final Messager messager;
  private boolean deprecated;
  private String name;
  private String description;

  public Event(Element element, Messager messager) {
    this.element = element;
    this.messager = messager;
  }

  /**
   * Builds an Event object
   *
   * @return An Event object
   */
  public Event build() {
    if (!CheckName.isPascalCase(element)) {
      messager.printMessage(Diagnostic.Kind.WARNING, "Event '" + element.getSimpleName() + "' should follow PascalCase naming convention.");
    }
    ExecutableElement executableElement = ((ExecutableElement) element);
    name = executableElement.getSimpleName().toString();
    description = executableElement.getAnnotation(SimpleEvent.class).description();
    deprecated = executableElement.getAnnotation(Deprecated.class) != null;

    for (VariableElement param : executableElement.getParameters()) {
      if (!CheckName.isCamelCase(param)) {
        messager.printMessage(Diagnostic.Kind.WARNING, "Parameter '" + param.getSimpleName() + "' of Event '" + element.getSimpleName() + "' should follow camelCase naming convention.");
      }
      params.add(new EventParam(param, messager, name));
    }

    return this;
  }

  public boolean isDeprecated() {
    return this.deprecated;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public ArrayList<EventParam> getParams() {
    return this.params;
  }
}

