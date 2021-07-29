package io.shreyash.rush.blocks;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

import shaded.org.json.JSONArray;
import shaded.org.json.JSONObject;

public class DesignerProperty extends Block {
  private final ExecutableElement element;
  private final Messager messager;

  private final BlockStore store = BlockStore.getInstance();

  public DesignerProperty(Element element, Messager messager) {
    super(element);
    this.element = (ExecutableElement) element;
    this.messager = messager;
    validate();
  }

  @Override
  String description() {
    return null;
  }

  @Override
  void validate() {
    // Check if the corresponding setter simple property exists.
    final boolean setterExist = store.getAllProperties().stream()
        .anyMatch(el -> el.name().equals(this.name()));

    if (!setterExist) {
      messager.printMessage(Diagnostic.Kind.ERROR,
          "Unable to find corresponding @SimpleProperty annotation for designer property '"
              + this.name() + "'.");
    }
  }

  /**
   * JSON structure:
   * {
   *  "alwaysSend": "false",
   * 	"defaultValue": "Bar",
   * 	"name": "Foo",
   * 	"editorArgs": ["Bar", "Baz"],
   * 	"editorType": "text"
   * }
   */
  @Override
  public JSONObject asJsonObject() {
    final JSONObject propJson = new JSONObject();
    final com.google.appinventor.components.annotations.DesignerProperty annotation
        = this.element.getAnnotation(com.google.appinventor.components.annotations.DesignerProperty.class);

    propJson.put("name", this.name());
    propJson.put("editorType", annotation.editorType());
    propJson.put("alwaysSend", Boolean.toString(annotation.alwaysSend()));
    propJson.put("defaultValue", annotation.defaultValue());

    final JSONArray args = new JSONArray();
    for (final String arg : annotation.editorArgs()) {
      args.put(arg);
    }
    propJson.put("editorArgs", args);

    return propJson;
  }
}