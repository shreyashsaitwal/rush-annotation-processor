package io.shreyash.rush.blocks;

import com.google.appinventor.components.annotations.SimpleEvent;

import io.shreyash.rush.util.Casing;
import shaded.org.json.JSONArray;
import shaded.org.json.JSONObject;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

public class Event extends BlockWithParams {
  private final ExecutableElement element;
  private final Messager messager;

  public  Event(Element element, Messager messager) {
    super(element);
    this.element = (ExecutableElement) element;
    this.messager = messager;
    validate();
  }

  @Override
  String description() {
    return this.element.getAnnotation(SimpleEvent.class).description();
  }

  @Override
  void validate() {
    // Check method name
    if (!Casing.isPascalCase(this.name())) {
      messager.printMessage(
          Diagnostic.Kind.WARNING,
          "Simple event \"" + this.name() + "\" should follow 'PascalCase' naming convention."
      );
    }

    // Check param names
    this.params().forEach(el -> {
      if (!Casing.isCamelCase(el.getName())) {
        messager.printMessage(
            Diagnostic.Kind.WARNING,
            "Parameter \"" + el.getName() + "\" in simple event \"" + this.name() + "\" should " +
                "follow 'camelCase' naming convention."
        );
      }
    });
  }

  /**
   * Converts this event to a JSONObject which will later be added
   * the `components.json` descriptor file.
   *
   * JSON:
   * {
   *   "name": "Foo",
   *   "description": "This is a description",
   *   "deprecated": "false",
   *   "params": [
   *      { "name": "bar", "type": "number" },
   *   ]
   *  }
   */
  @Override
  public JSONObject asJsonObject() {
    final JSONObject eventJson = new JSONObject();

    eventJson.put("name", this.name());
    eventJson.put("description", this.description());
    eventJson.put("deprecated", Boolean.toString(this.element.getAnnotation(Deprecated.class) != null));

    final JSONArray params = new JSONArray();
    this.params().forEach(el -> {
      final JSONObject paramObj = new JSONObject();
      paramObj.put("name", el.getName());
      paramObj.put("type", el.getType());

      params.put(paramObj);
    });

    eventJson.put("params", params);
    return eventJson;
  }
}