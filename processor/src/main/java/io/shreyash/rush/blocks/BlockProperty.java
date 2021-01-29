package io.shreyash.rush.blocks;

import com.google.appinventor.components.annotations.SimpleProperty;
import io.shreyash.rush.util.ConvertToYailType;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;

public class BlockProperty {
  private String name;
  private String description;
  private String type;
  private boolean deprecated;
  private AccessType accessType;
  private String defaultVal = "";
  private boolean alwaysSend = false;

  private final Element element;
  private final ExtensionFieldInfo ext;

  public BlockProperty(Element element, ExtensionFieldInfo ext) {
    this.element = element;
    this.ext = ext;
  }

  public BlockProperty build() throws IllegalAccessException {
    ExecutableElement executableElement = ((ExecutableElement) element);
    int paramSize = executableElement.getParameters().size();

    if (executableElement.getReturnType().getKind() == TypeKind.VOID) {
      if (paramSize != 1) {
        throw new IllegalAccessException("The number of parameters allowed on the setter property \"" + element.getSimpleName() + "\" is: 1.");
      } else {
        accessType = AccessType.WRITE;
        type = ConvertToYailType.convert(executableElement.getParameters().get(0).asType().toString());
      }
    } else {
      if (paramSize != 0) {
        throw new IllegalAccessException("The number of parameters allowed on the getter property \"" + element.getSimpleName() + "\" is: 0.");
      } else {
        accessType = AccessType.READ;
        type = ConvertToYailType.convert(executableElement.getReturnType().toString());
      }
    }

    name = executableElement.getSimpleName().toString();
    description = executableElement.getAnnotation(SimpleProperty.class).description();
    accessType = executableElement.getAnnotation(SimpleProperty.class).userVisible() ? accessType : AccessType.INVISIBLE;
    deprecated = executableElement.getAnnotation(Deprecated.class) != null;

    if (ext.getBlockProps().containsKey(executableElement.getSimpleName().toString())) {
      BlockProperty priorProp = ext.getBlockProps().get(executableElement.getSimpleName().toString());
      if (!priorProp.getType().equals(type)) {
        if (accessType.equals(AccessType.READ)) {
          priorProp.setType(type);
        } else {
          throw new IllegalAccessException("Inconsistent types \"" + priorProp.getType() + "\" and \"" + type + "\" for property \"" + name + "\".");
        }
      }

      if (priorProp.getDescription().isEmpty() && !getDescription().isEmpty()) {
        priorProp.setDescription(description);

      }

      if (priorProp.getAccessType().equals(AccessType.INVISIBLE) || accessType.equals(AccessType.INVISIBLE)) {
        accessType = AccessType.INVISIBLE;
      } else if (!priorProp.getAccessType().equals(accessType)) {
        accessType = AccessType.READ_WRITE;
      }

      ext.removeBlockProp(name);
    }
    return this;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }

  public boolean isDeprecated() {
    return deprecated;
  }

  public AccessType getAccessType() {
    return accessType;
  }

  public String getDefaultVal() {
    return defaultVal;
  }

  public boolean isAlwaysSend() {
    return alwaysSend;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setDefaultVal(String defaultVal) {
    this.defaultVal = defaultVal;
  }

  public void setAlwaysSend(boolean alwaysSend) {
    this.alwaysSend = alwaysSend;
  }

}
