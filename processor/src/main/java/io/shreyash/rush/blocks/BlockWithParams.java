package io.shreyash.rush.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import io.shreyash.rush.util.ConvertToYailType;

public abstract class BlockWithParams extends Block {
  private final ExecutableElement element;

  protected BlockWithParams(Element element) {
    super(element);
    this.element = (ExecutableElement) element;
  }

  /**
   * @return The parameters (or arguments) of this block.
   */
  public List<BlockParam> params() {
    final List<BlockParam> params = new ArrayList<>();

    if (this.element.getParameters() != null) {
      this.element.getParameters().forEach(el -> {
        params.add(new BlockParam(
            el.getSimpleName().toString(), ConvertToYailType.convert(el.asType().toString())
        ));
      });
    }

    return params;
  }
}

class BlockParam {
  // Name of this parameter
  private final String name;

  // YAIL type of this parameter
  private final String type;

  BlockParam(String name, String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}