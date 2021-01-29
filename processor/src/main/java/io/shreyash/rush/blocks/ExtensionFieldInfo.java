package io.shreyash.rush.blocks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class ExtensionFieldInfo {
  private final HashMap<String, Function> functions = new HashMap<>();
  private final HashMap<String, Event> events = new HashMap<>();
  private final HashMap<String, BlockProperty> blockProps = new HashMap<>();
  private final HashMap<String, Property> props = new HashMap<>();

  public JSONArray getFuncJson() {
    JSONArray funcArray = new JSONArray();

    for (Function f : functions.values()) {
      JSONObject obj = new JSONObject();
      obj.put("deprecated", Boolean.toString(f.isDeprecated()));
      obj.put("name", f.getName());
      obj.put("description", f.getDescription());
      if (f.getReturnType() != null) {
        obj.put("returnType", f.getReturnType());
      }

      JSONArray params = new JSONArray();
      for (FunctionParam p : f.getParams()) {
        JSONObject pObj = new JSONObject();
        pObj.put("name", p.getName());
        pObj.put("type", p.getType());
        params.put(pObj);
      }
      obj.put("params", params);

      funcArray.put(obj);
    }

    return funcArray;
  }

  public JSONArray getEventJson() {
    JSONArray eventArray = new JSONArray();

    for (Event e : events.values()) {
      JSONObject obj = new JSONObject();
      obj.put("deprecated", Boolean.toString(e.isDeprecated()));
      obj.put("name", e.getName());
      obj.put("description", e.getDescription());

      JSONArray params = new JSONArray();
      for (EventParam p : e.getParams()) {
        JSONObject pObj = new JSONObject();
        pObj.put("name", p.getName());
        pObj.put("type", p.getType());
        params.put(pObj);
      }
      obj.put("params", params);

      eventArray.put(obj);
    }

    return eventArray;
  }

  public JSONArray getBlockPropsJson() {
    JSONArray propsArray = new JSONArray();

    for (BlockProperty bp : blockProps.values()) {
      JSONObject obj = new JSONObject();
      obj.put("deprecated", Boolean.toString(bp.isDeprecated()));
      obj.put("name", bp.getName());
      obj.put("description", bp.getDescription());
      if (bp.getAccessType().equals(AccessType.INVISIBLE)) {
        obj.put("rw", "invisible");
      } else if (bp.getAccessType().equals(AccessType.READ)) {
        obj.put("rw", "read-only");
      } else if (bp.getAccessType().equals(AccessType.WRITE)) {
        obj.put("rw", "write-only");
      } else if (bp.getAccessType().equals(AccessType.READ_WRITE)) {
        obj.put("rw", "read-write");
      }
      if (!bp.getDefaultVal().equals("")) {
        obj.put("defaultValue", bp.getDefaultVal());
      }
      if (bp.isAlwaysSend()) {
        obj.put("alwaysSend", bp.isAlwaysSend());
      }
      obj.put("type", bp.getType());

      propsArray.put(obj);
    }

    return propsArray;
  }

  public JSONArray getPropsJson() {
    JSONArray propsArray = new JSONArray();

    for (Property p : props.values()) {
      JSONObject obj = new JSONObject();
      obj.put("name", p.getName());
      obj.put("editorType", p.getEditorType());
      obj.put("defaultValue", p.getDefaultVal());
      if (p.isAlwaysSend()) {
        obj.put("alwaysSend", p.isAlwaysSend());
      }

      JSONArray args = new JSONArray();
      for (String arg : p.getArgs()) {
        args.put(arg);
      }
      obj.put("editorArgs", args);

      propsArray.put(obj);
    }

    return propsArray;
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
