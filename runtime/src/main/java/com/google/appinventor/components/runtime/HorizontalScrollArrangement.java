// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2016 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import common.ComponentConstants;

/**
 * A formatting element in which to place components that should be displayed from left to right.
 * If you wish to have components displayed one over another, use {@link VerticalScrollArrangement}
 * instead.
 *
 * This version is scrollable.
 *
 * @author sharon@google.com (Sharon Perl)
 * @author jis@mit.edu (Jeffrey I. Schiller)
 *
 */

public class HorizontalScrollArrangement extends HVArrangement {
  public HorizontalScrollArrangement(ComponentContainer container) {
    super(container, ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL,
      ComponentConstants.SCROLLABLE_ARRANGEMENT);
  }

}
