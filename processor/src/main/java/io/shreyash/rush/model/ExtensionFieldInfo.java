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
      int count = 0;
      for (Function func : functions.values()) {
        if (count == 0) {
          sb.append("{");
        } else {
          sb.append(", {");
        }
        count++;
        sb.append("\"deprecated:\" : \"" + func.isDeprecated() + "\",");
        sb.append("\"name:\" : \"" + func.getName() + "\",");
        sb.append("\"description:\" : \"" + func.getDescription() + "\",");
        if (func.getReturnType() != null) {
          sb.append("\"returnType:\" : \"" + func.getReturnType() + "\",");
        }
        sb.append("\"params\" : [");
        if (!func.getParams().isEmpty()) {
          int count2 = 0;
          for (FunctionParam param : func.getParams()) {
            if (count2 == 0) {
              sb.append("{");
            } else {
              sb.append(", {");
            }
            count2++;
            sb.append("\"name\" : \"" + param.getName() + "\",");
            sb.append("\"type\" : \"" + param.getType() + "\"");
            sb.append("}");
          }
        }
        sb.append("] }");
      }
    }

    sb.append("], \"events\" : [");
    if (!events.isEmpty()) {
      int count = 0;
      for (Event event : events.values()) {
        if (count == 0) {
          sb.append("{");
        } else {
          sb.append(", {");
        }
        count++;
        sb.append("\"deprecated:\" : \"" + event.isDeprecated() + "\",");
        sb.append("\"name:\" : \"" + event.getName() + "\",");
        sb.append("\"description:\" : \"" + event.getDescription() + "\",");
        sb.append("\"params\" : [");
        if (!event.getParams().isEmpty()) {
          int count2 = 0;
          for (EventParam param : event.getParams()) {
            if (count2 == 0) {
              sb.append("{");
            } else {
              sb.append(", {");
            }
            count2++;
            sb.append("\"name\" : \"" + param.getName() + "\",");
            sb.append("\"type\" : \"" + param.getType() + "\"");
            sb.append("}");
          }
        }
        sb.append("] }");
      }
    }

    sb.append("], \"blockProperties\" : [");
    if (!blockProps.isEmpty()) {
      int count = 0;
      for (BlockProperty prop : blockProps.values()) {
        if (count == 0) {
          sb.append("{");
        } else {
          sb.append(", {");
        }
        count++;
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
        sb.append("\"type:\" : \"" + prop.getType() + "\"");
        sb.append("}");
      }
    }

    sb.append("], \"properties\" : [");
    if (!props.values().isEmpty()) {
      int count = 0;
      for (Property prop: props.values()) {
        if (count == 0) {
            sb.append("{");
          } else {
            sb.append(", {");
          }
          count++;
        sb.append("\"editorType:\" : \"" + prop.getEditorType() + "\",");
        sb.append("\"name:\" : \"" + prop.getName() + "\",");
        sb.append("\"defaultValue:\" : \"" + prop.getDefaultVal() + "\",");
        if (prop.isAlwaysSend()) {
          sb.append("\"alwaysSend\" : \"" + prop.isAlwaysSend() + "\",");
        }
        sb.append("\"editorArgs:\" : [");
        int count2 = 0;
        for (String arg : prop.getArgs()) {
          if (count2 == 0) {
            sb.append("\"" + arg + "\"");
          } else {
            sb.append(",\"" + arg + "\"");
          }
          count2++;
        }
        sb.append("] }");
      }
    }
    sb.append("] }");

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
