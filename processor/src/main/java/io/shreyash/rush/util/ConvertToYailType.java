package io.shreyash.rush.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class ConvertToYailType {
  public static String convert(String type, Messager messager) {
    if (type.startsWith(("java.util.List"))) {
      return "list";
    }
    switch (type) {
      case "float":
      case "int":
      case "double":
      case "byte":
      case "long":
      case "short":
        return "number";

      case "java.lang.String":
        return "text";

      case "boolean":
        return type;

      case "com.google.appinventor.components.runtime.util.YailList":
        return "list";

      case "com.google.appinventor.components.runtime.util.YailDictionary":
        return "dictionary";

      case "com.google.appinventor.components.runtime.util.YailObject":
        return "yailobject";

      case "java.util.Calendar":
        return "InstantInTime";

      case "java.lang.Object":
        return "any";

      case "com.google.appinventor.components.runtime.Component":
        return "component";

      // TODO: Component type
      default:
        messager.printMessage(Diagnostic.Kind.ERROR, "ERR Cannot to convert Java type '" + type + "' to YAIL type.");
        return null;
    }
  }
}
