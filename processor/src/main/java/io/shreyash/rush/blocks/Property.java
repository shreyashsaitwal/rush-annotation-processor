package io.shreyash.rush.blocks;

import io.shreyash.rush.util.CheckName;
import io.shreyash.rush.util.ConvertToYailType;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;

public class Property {
  private final Element element;
  private final BlocksDescriptorAdapter ext;
  private final Messager messager;
  private String name;
  private String description;
  private String type;
  private boolean deprecated;
  private AccessType accessType;
  private String defaultVal = "";
  private boolean alwaysSend = false;

  public Property(Element element, BlocksDescriptorAdapter ext, Messager messager) {
    this.element = element;
    this.ext = ext;
    this.messager = messager;
  }

  public Property build() {
    if (!CheckName.isPascalCase(element)) {
      messager.printMessage(Diagnostic.Kind.WARNING,
          "Property '" + element.getSimpleName() + "' should follow PascalCase naming convention.");
    }
    ExecutableElement executableElement = ((ExecutableElement) element);
    int paramSize = executableElement.getParameters().size();

    if (executableElement.getReturnType().getKind() == TypeKind.VOID) {
      if (paramSize != 1) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "@SimpleProperty '" + name + "': The total number of parameters allowed " +
                "on the setter property '" + element.getSimpleName() + "' is: 1.");
      } else {
        accessType = AccessType.WRITE;
        final String paramType = executableElement.getParameters().get(0).asType().toString();
        try {
          type = ConvertToYailType.convert(paramType);
        } catch (IllegalStateException e) {
          messager.printMessage(Diagnostic.Kind.ERROR,
              "@SimpleProperty '" + name + "': Can't convert parameter type '"
                  + paramType + "' (parameter '" + name + "') to YAIL type.");
        }
      }
    } else {
      if (paramSize != 0) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "@SimpleProperty '" + name + "': The total number of parameters allowed " +
                "on the getter property '" + element.getSimpleName() + "' is: 0.");
      } else {
        accessType = AccessType.READ;
        final String returnType = executableElement.getReturnType().toString();
        try {
          type = ConvertToYailType.convert(returnType);
        } catch (IllegalStateException e) {
          messager.printMessage(Diagnostic.Kind.ERROR,
              "@SimpleProperty '" + name + "': Can't convert return type '" + returnType + "' to YAIL type.");
        }
      }
    }

    name = executableElement.getSimpleName().toString();
    description = executableElement.getAnnotation(com.google.appinventor.components.annotations.SimpleProperty.class).description();
    accessType = executableElement.getAnnotation(com.google.appinventor.components.annotations.SimpleProperty.class).userVisible() ? accessType : AccessType.INVISIBLE;
    deprecated = executableElement.getAnnotation(Deprecated.class) != null;

    if (ext.getSimplePropertiesMap().containsKey(executableElement.getSimpleName().toString())) {
      Property priorProp = ext.getSimplePropertiesMap().get(executableElement.getSimpleName().toString());
      if (!priorProp.getType().equals(type)) {
        if (accessType.equals(AccessType.READ)) {
          priorProp.setType(type);
        } else {
          messager.printMessage(Diagnostic.Kind.ERROR,
              "@SimpleProperty '" + name + "': Inconsistent types '" + priorProp.getType()
                  + "' and '" + type + "' for property '" + name + "'.");
        }
      }

      if (priorProp.getDescription().isEmpty() && !getDescription().isEmpty()) {
        priorProp.setDescription(description);
      }

      if (priorProp.getAccessType().equals(AccessType.INVISIBLE) && accessType != AccessType.INVISIBLE) {
        if (paramSize == 1) {
          accessType = AccessType.WRITE;
        } else if (paramSize == 0) {
          accessType = AccessType.READ;
        }
      } else if (!priorProp.getAccessType().equals(AccessType.INVISIBLE) && accessType == AccessType.INVISIBLE) {
        final ExecutableElement el = (ExecutableElement) priorProp.element;
        if (el.getParameters().size() == 1) {
          accessType = AccessType.WRITE;
        } else if (el.getParameters().size() == 0) {
          accessType = AccessType.READ;
        }
      } else if (!priorProp.getAccessType().equals(accessType)) {
        accessType = AccessType.READ_WRITE;
      }

      ext.removeSimpleProperty(name);
    }
    return this;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  public void setDefaultVal(String defaultVal) {
    this.defaultVal = defaultVal;
  }

  public boolean isAlwaysSend() {
    return alwaysSend;
  }

  public void setAlwaysSend(boolean alwaysSend) {
    this.alwaysSend = alwaysSend;
  }

}
