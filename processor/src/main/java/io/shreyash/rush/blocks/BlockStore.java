package io.shreyash.rush.blocks;

import java.util.ArrayList;
import java.util.List;

/**
 * A singleton class that stores all the parsed blocks.
 */
public class BlockStore {
  private static final BlockStore instance = new BlockStore();

  private final List<Method> methods = new ArrayList<>();
  private final List<Event> events = new ArrayList<>();
  private final List<Property> properties = new ArrayList<>();
  private final List<DesignerProperty> designerProperties = new ArrayList<>();

  public static BlockStore getInstance() {
    return instance;
  }

  public List<Method> getAllMethods() {
    return this.methods;
  }
  public void putMethod(Method method) {
    this.methods.add(method);
  }

  public List<Event> getAllEvents() {
    return this.events;
  }
  public void putEvent(Event event) {
    this.events.add(event);
  }

  public List<Property> getAllProperties() {
    return this.properties;
  }
  public void putProperty(Property property) {
    this.properties.add(property);
  }

  public List<DesignerProperty> getAllDesignerProperties() {
    return this.designerProperties;
  }
  public void putDesignerProperty(DesignerProperty designerProperty) {
    this.designerProperties.add(designerProperty);
  }

}
