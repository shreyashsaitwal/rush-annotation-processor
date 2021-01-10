package io.shreyash.rush.model;

import java.util.HashMap;

public class ExtensionFieldInfo {
  private final HashMap<String, Function> functions = new HashMap<>();
  private final HashMap<String, Event> events = new HashMap<>();
  private final HashMap<String, BlockProperty> blockProps = new HashMap<>();
  private final HashMap<String, Property> props = new HashMap<>();

  public String getJson() {
    StringBuilder sb = new StringBuilder("{");
    sb.append("\"methods\": [");
    if (!functions.isEmpty()) {
      functions.values().forEach(func -> {
        sb.append("{");
        sb.append("\"deprecated:\" : \"" + func.isDeprecated() + "\",");
        sb.append("\"name:\" : \"" + func.getName() + "\",");
        sb.append("\"description:\" : \"" + func.getDescription() + "\",");
        if (func.getReturnType() != null) {
          sb.append("\"returnType:\" : \"" + func.getReturnType() + "\",");
        }
        sb.append("\"params\" : [");
        if (!func.getParams().isEmpty()) {
          func.getParams().forEach(param -> {
            sb.append("{");
            sb.append("\"name\" : \"" + param.getName() + "\",");
            sb.append("\"type\" : \"" + param.getType() + "\",");
            sb.append("},");
          });
        }
        sb.append("],},");
      });
    }
    sb.append("], \"events\" : [");
    if (!events.isEmpty()) {
      events.values().forEach(event -> {
        sb.append("{");
        sb.append("\"deprecated:\" : \"" + event.isDeprecated() + "\",");
        sb.append("\"name:\" : \"" + event.getName() + "\",");
        sb.append("\"description:\" : \"" + event.getDescription() + "\",");
        sb.append("\"params\" : [");
        if (!event.getParams().isEmpty()) {
          event.getParams().forEach(param -> {
            sb.append("{");
            sb.append("\"name\" : \"" + param.getName() + "\",");
            sb.append("\"type\" : \"" + param.getType() + "\",");
            sb.append("},");
          });
        }
        sb.append("],},");
      });
    }
    sb.append("], \"blockProperties\" : [");
    if (!blockProps.isEmpty()) {
      blockProps.values().forEach(prop -> {
        sb.append("{");
        sb.append("\"deprecated:\" : \"" + prop.isDeprecated() + "\",");
        sb.append("\"name:\" : \"" + prop.getName() + "\",");
        sb.append("\"description:\" : \"" + prop.getDescription() + "\",");
        sb.append("\"rw\" : \"");
        if (prop.getAccessType().equals(AccessType.INVISIBLE)) {
          sb.append("invisible\",");
        } else if (prop.getAccessType().equals(AccessType.READ)) {
          sb.append("read-only\",");
        } else if (prop.getAccessType().equals(AccessType.WRITE)) {
          sb.append("write-only\",");
        } else if (prop.getAccessType().equals(AccessType.READ_WRITE)) {
          sb.append("read-write\",");
        }
        if (!prop.getDefaultVal().equals("")) {
          sb.append("\"defaultValue\" : \"" + prop.getDefaultVal() + "\",");
        }
        if (prop.isAlwaysSend()) {
          sb.append("\"alwaysSend\" : \"" + prop.isAlwaysSend() + "\",");
        }
        sb.append("\"type:\" : \"" + prop.getType() + "\",");
        sb.append("},");
      });
    }
    sb.append("], \"properties\" : [");
    props.values().forEach(prop -> {
      sb.append("{");
      sb.append("\"editorType:\" : \"" + prop.getEditorType() + "\",");
      sb.append("\"name:\" : \"" + prop.getName() + "\",");
      sb.append("\"defaultValue:\" : \"" + prop.getDefaultVal() + "\",");
      if (prop.isAlwaysSend()) {
        sb.append("\"alwaysSend\" : \"" + prop.isAlwaysSend() + "\",");
      }
      sb.append("\"editorType:\" : [");
      for (String arg : prop.getArgs()) {
        sb.append("\"" + arg + "\",");
      }
      sb.append("], },");
    });
    sb.append("], }");

    return sb.toString();
  }

  public void addEvent(Event event) {
    events.put(event.getName(), event);
  }

  public void addFunction(Function func) {
    functions.put(func.getName(), func);
  }

  public void addProp(Property prop) {
    props.put(prop.getName(), prop);
  }

  public void addBlockProp(BlockProperty prop) {
    blockProps.put(prop.getName(), prop);
  }

  public HashMap<String, BlockProperty> getBlockProps() {
    return blockProps;
  }

  public void removeBlockProp(String name) {
    this.blockProps.remove(name);
  }
}
