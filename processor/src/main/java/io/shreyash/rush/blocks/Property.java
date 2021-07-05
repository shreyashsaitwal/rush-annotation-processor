package io.shreyash.rush.blocks;

import com.google.appinventor.components.annotations.SimpleProperty;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;

import io.shreyash.rush.util.ConvertToYailType;
import shaded.org.json.JSONObject;

public class Property extends Block {
  private final BlockStore store = BlockStore.getInstance();

  private final ExecutableElement element;
  private final Messager messager;

  public Property(Element element, Messager messager) {
    super(element);
    this.element = (ExecutableElement) element;
    this.messager = messager;
    validate();
  }

  /**
   * @return If this is a setter type property, the type of the value
   * it accepts, else if it is a getter, it's return type.
   */
  @Override
  public String returnType() {
    final String returnType = this.element.getReturnType().toString();

    // If the property is of type setter, the JSON property "type" is
    // equal to the type of parameter the setter expects.
    if (returnType.equals("void")) {
      final String type = this.element.getParameters().get(0).asType().toString();
      return ConvertToYailType.convert(type);
    }

    return ConvertToYailType.convert(returnType);
  }

  @Override
  String description() {
    return element.getAnnotation(SimpleProperty.class).description();
  }

  @Override
  void validate() {
    final boolean isSetter = this.element.getReturnType().toString().equals("void");
    final int noOfParams = this.element.getParameters().size();

    // Total numbers of parameters for setters must be 1 and for
    // getter must be 0.
    if (isSetter && noOfParams != 1) {
      this.messager.printMessage(Diagnostic.Kind.ERROR,
          "The total number of parameters allowed on the setter type simple property \"" + this.name() + "\" is: 1");
    } else if (!isSetter && noOfParams != 0) {
      this.messager.printMessage(Diagnostic.Kind.ERROR,
          "The total number of parameters allowed on the getter type simple property \"" + this.name() + "\" is: 0");
    }

    // Return types of getters and setters must match
    final Property partnerProp = partnerProp();
    if (partnerProp != null && !partnerProp.returnType().equals(this.returnType())) {
      this.messager.printMessage(Diagnostic.Kind.ERROR,
          "Inconsistent types across getter and setter for simple property \"" + this.name() + "\".");
    }
  }

  /**
   * JSON structure:
   * {
   *  "rw": "read-only",
   * 	"deprecated": "false",
   * 	"name": "Foo",
   * 	"description": "",
   * 	"type": "any"
   * },
   */
  @Override
  public JSONObject asJsonObject() {
    final JSONObject propJson = new JSONObject();

    propJson.put("name", this.name());
    propJson.put("description", this.description());
    propJson.put("deprecated", Boolean.toString(this.element.getAnnotation(Deprecated.class) != null));
    propJson.put("rw", this.accessType());
    propJson.put("type", this.returnType());

    return propJson;
  }

  public String accessType() {
    final boolean invisible = !this.element.getAnnotation(SimpleProperty.class).userVisible();
    if (invisible) {
      return PropertyAccessType.INVISIBLE;
    }

    String accessType;

    if (this.element.getReturnType().toString().equals("void")) {
      accessType = PropertyAccessType.WRITE;
    } else {
      accessType = PropertyAccessType.READ;
    }

    final Property partnerProp = partnerProp();

    // If the partner prop exists and is not invisible, then it means that
    // both getter and setter exists for this prop. In that case, we set the
    // access type to read-write which tells AI2 to render two blocks -- one
    // getter and one setter.
    if (partnerProp != null && !partnerProp.accessType().equals(PropertyAccessType.INVISIBLE)) {
      accessType = PropertyAccessType.READ_WRITE;
    }

    // Remove the partner prop from the store. This is necessary because
    // AI2 doesn't expects getter and setter to be defined separately. It
    // checks the access type to decide whether to generate getter (read-only),
    // setter (write-only), both (read-write) or none (invisible).
    store.getAllProperties().remove(partnerProp);
    return accessType;
  }

  /**
   * @return If the property that's being processed currently is has it's corresponding
   * getter or setter, then it is returned, otherwise `null`.
   */
  private Property partnerProp() {
    return store.getAllProperties()
        .stream()
        .filter(el -> el.name().equals(this.name()) && el != this)
        .findFirst()
        .orElse(null);
  }
}
