package io.shreyash.rush.model;

import com.google.appinventor.components.annotations.SimpleEvent;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;

public class Event {
  private boolean deprecated;
  private String name;
  private String description;
  private final ArrayList<EventParam> params = new ArrayList<>();
  private final Element element;

  public Event(Element element) {
    this.element = element;
  }

  public Event build() {
    ExecutableElement executableElement = ((ExecutableElement) element);
    name = executableElement.getSimpleName().toString();
    description = executableElement.getAnnotation(SimpleEvent.class).description();
    deprecated = executableElement.getAnnotation(Deprecated.class) != null;

    for (VariableElement param : executableElement.getParameters()) {
      params.add(new EventParam(param));
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

