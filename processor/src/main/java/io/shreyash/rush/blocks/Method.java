package io.shreyash.rush.blocks;

import com.google.appinventor.components.annotations.SimpleFunction;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

import io.shreyash.rush.util.Casing;
import shaded.org.json.JSONArray;
import shaded.org.json.JSONObject;

public class Method extends BlockWithParams {
  private final ExecutableElement element;
  private final Messager messager;

  public Method(Element element, Messager messager) {
    super(element);
    this.element = (ExecutableElement) element;
    this.messager = messager;
    validate();
  }

  @Override
  String description() {
    return this.element.getAnnotation(SimpleFunction.class).description();
  }

  @Override
  void validate() {
    // Check method name
    if (!Casing.isPascalCase(this.name())) {
      messager.printMessage(
          Diagnostic.Kind.WARNING,
          "Simple function \"" + this.name() + "\" should follow 'PascalCase' naming convention."
      );
    }

    // Check param names
    this.params().forEach(el -> {
      if (!Casing.isCamelCase(el.getName())) {
        messager.printMessage(
            Diagnostic.Kind.WARNING,
            "Parameter \"" + el.getName() + "\" in simple function \"" + this.name() + "\" should " +
            "follow 'camelCase' naming convention."
        );
      }
    });
  }

  /**
   * JSON structure:
   * {
   *   "name": "Foo",
   *   "description": "This is a description",
   *   "deprecated": "false",
   *   "returnType": "any",
   *   "params": [
   *      { "name": "bar", "type": "number" },
   *   ]
   *  }
   */
  @Override
  public JSONObject asJsonObject() {
    final JSONObject methodJson = new JSONObject();

    methodJson.put("name", this.name());
    methodJson.put("description", this.description());
    methodJson.put("deprecated", Boolean.toString(this.element.getAnnotation(Deprecated.class) != null));

    // Here, null represents the return type is void. Return type for void
    // methods don't need to be specified
    if (this.returnType() != null) {
      methodJson.put("returnType", this.returnType());
    }

    final JSONArray params = new JSONArray();
    this.params().forEach(el -> {
      final JSONObject paramObj = new JSONObject();
      paramObj.put("name", el.getName());
      paramObj.put("type", el.getType());

      params.put(paramObj);
    });

    methodJson.put("params", params);
    return methodJson;
  }
}
