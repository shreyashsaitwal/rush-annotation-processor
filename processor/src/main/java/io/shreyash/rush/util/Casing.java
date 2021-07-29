package io.shreyash.rush.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Casing {
  public static boolean isCamelCase(String text) {
    final Pattern pattern = Pattern.compile("^[a-z]+(?:(\\d|[A-Z][\\w$_\\d]*))*$");
    return pattern.matcher(text).find();
  }

  public static boolean isPascalCase(String text) {
    final Pattern pattern = Pattern.compile("^[A-Z][a-z]+(?:(\\d|[A-Z][\\w$_\\d]+))*$");
    return pattern.matcher(text).find();
  }
}
