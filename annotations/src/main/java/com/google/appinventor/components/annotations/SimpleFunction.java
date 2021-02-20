// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2021 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark Simple functions.
 *
 * <p>Note that the Simple compiler will only recognize Java methods marked
 * with this annotation. All other methods will be ignored.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleFunction {
  /**
   * If non-empty, description to use in user-level documentation in place of
   * Javadoc, which is meant for developers.
   */
  String description() default "";
}
