package io.shreyash.rush.util;

import javax.lang.model.element.Element;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckName {

  public static boolean isPascalCase(Element element) {
    Pattern pattern = Pattern.compile("^[A-Z][a-z]+(?:(\\d|[A-Z][\\w$_\\d]+))*$");
    Matcher matcher = pattern.matcher(element.getSimpleName().toString());
    return matcher.find();
  }

  public static boolean isCamelCase(Element element) {
    Pattern pattern = Pattern.compile("^[a-z]+(?:(\\d|[A-Z][\\w$_\\d]*))*$");
    Matcher matcher = pattern.matcher(element.getSimpleName().toString());
    return matcher.find();
  }
}
