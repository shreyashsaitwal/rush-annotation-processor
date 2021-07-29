package io.shreyash.rush;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.task.list.items.TaskListItemsExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.shreyash.rush.blocks.BlockStore;
import io.shreyash.rush.model.Assets;
import io.shreyash.rush.model.RushYaml;
import shaded.org.json.JSONArray;
import shaded.org.json.JSONException;
import shaded.org.json.JSONObject;

public class InfoFilesGenerator {
  private final String projectRoot;
  private final String extVersion;
  private final String type;
  private final String outputPath;

  private final BlockStore store = BlockStore.getInstance();

  public InfoFilesGenerator(
      String projectRoot, String extVersion, String type, String outputPath) {
    this.projectRoot = projectRoot;
    this.extVersion = extVersion;
    this.type = type;
    this.outputPath = outputPath;
  }

  /**
   * {@link io.shreyash.rush.ExtensionProcessor} is designed to pick only the classes that declare
   * at least one of the block annotations. So, in case there's no block annotation, the CLI would
   * crash, as there won't be any annotation processor generated info file to further process by
   * the CLI. Therefore, to prevent this, we check if the info files exists and that they are
   * up-to-date. If they aren't, we run this method and generate (new) info files w/o any blocks.
   *
   * @param args 0 -> projectPath
   *             1 -> extVersion
   *             2 -> type
   *             3 -> outputPath
   */
  public static void main(String[] args)
      throws IOException, ParserConfigurationException, SAXException {
    final InfoFilesGenerator generator = new InfoFilesGenerator(args[0], args[1], args[2], args[3]);
    generator.generateComponentsJson();
    generator.generateBuildInfoJson();
  }

  /**
   * Generates the components.json file.
   *
   * @throws IOException
   * @throws JSONException
   */
  public void generateComponentsJson() throws IOException, JSONException {
    JSONArray componentsJson = new JSONArray();

    final RushYaml yaml = getRushYml();
    JSONObject json = new JSONObject();

    if (yaml.getVersion().getName() != null) {
      json.put("versionName", yaml.getVersion().getName());
    } else {
      json.put("versionName", "");
    }

    if (yaml.getHomepage() != null) {
      json.put("helpUrl", yaml.getHomepage());
    } else {
      json.put("helpUrl", "");
    }

    if (yaml.getLicense() != null) {
      json.put("licenseName", yaml.getLicense());
    } else {
      json.put("licenseName", "");
    }

    final List<Extension> extensionList = Arrays.asList(
        AutolinkExtension.create(),
        TaskListItemsExtension.create()
    );

    final Parser parser = new Parser.Builder()
        .extensions(extensionList)
        .build();

    final HtmlRenderer renderer = HtmlRenderer.builder()
        .extensions(extensionList)
        .softbreak("<br>")
        .build();

    json.put("helpString", renderer.render(parser.parse(yaml.getDescription())));

    json.put("type", type);
    json.put("version", extVersion);
    json.put("name", yaml.getName());
    json.put("androidMinSdk", Math.max(yaml.getMin_sdk(), 7));

    final Pattern urlPattern = Pattern.compile("https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_+.~#?&//=]*)");
    final String icon = yaml.getAssets().getIcon();
    if (urlPattern.matcher(icon).find()) {
      json.put("iconName", icon);
    } else {
      json.put("iconName", "aiwebres/" + icon);
    }

    final String time = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    json.put("dateBuilt", time);

    json.put("external", "true");
    json.put("categoryString", "EXTENSION");
    json.put("showOnPalette", "true");
    json.put("nonVisible", "true");

    // Put events
    final JSONArray events = new JSONArray();
    this.store.getAllEvents().forEach(el -> events.put(el.asJsonObject()));
    json.put("events", events);

    // Put methods
    final JSONArray methods = new JSONArray();
    this.store.getAllMethods().forEach(el -> methods.put(el.asJsonObject()));
    json.put("methods", methods);

    // Put properties
    final JSONArray properties = new JSONArray();
    this.store.getAllProperties().forEach(el -> properties.put(el.asJsonObject()));
    json.put("blockProperties", properties);

    // Put designer properties
    final JSONArray designerProperties = new JSONArray();
    this.store.getAllDesignerProperties().forEach(el -> designerProperties.put(el.asJsonObject()));
    json.put("properties", designerProperties);

    componentsJson.put(json);

    FileWriter writer = new FileWriter(Paths.get(outputPath, "components.json").toFile());
    componentsJson.write(writer);
    writer.flush();
    writer.close();
  }

  /**
   * Generate component_build_infos.json file.
   *
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public void generateBuildInfoJson() throws IOException, ParserConfigurationException, SAXException {
    JSONArray buildInfoJson = new JSONArray();
    JSONObject obj = new JSONObject();

    RushYaml yaml = getRushYml();

    // Put assets
    JSONArray assets = new JSONArray();
    Assets ymlAssets = yaml.getAssets();

    if (ymlAssets != null && !ymlAssets.getOther().isEmpty()) {
      ymlAssets.getOther().forEach(it -> assets.put(it.trim()));
    }
    obj.put("assets", assets);

    obj.put("type", type);
    obj.put("androidMinSdk", new JSONArray().put(Math.max(yaml.getMin_sdk(), 7)));

    File manifest = Paths.get(projectRoot, "src", "AndroidManifest.xml").toFile();
    if (manifest.exists()) {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document doc = builder.parse(manifest);

      // Put application elements
      putApplicationElements(obj, doc);

      // Put permissions
      NodeList nodes = doc.getElementsByTagName("uses-permission");
      JSONArray permissions = new JSONArray();
      if (nodes.getLength() != 0) {
        for (int i = 0; i < nodes.getLength(); i++) {
          permissions.put(generateXmlElement(nodes.item(i), "manifest"));
        }
      }
      obj.put("permissions", permissions);
    }
    buildInfoJson.put(obj);

    FileWriter writer = new FileWriter(Paths.get(outputPath, "component_build_infos.json").toFile());
    buildInfoJson.write(writer);
    writer.flush();
    writer.close();
  }

  /**
   * Get rush.yml file's data
   *
   * @return The rush.yml file's data
   * @throws IOException If the input can't be read for some reason.
   */
  private RushYaml getRushYml() throws IOException {
    final Yaml parser = new Yaml(new Constructor(RushYaml.class));

    File rushYml = Paths.get(projectRoot, "rush.yml").toFile();
    if (!rushYml.exists()) {
      if (Paths.get(projectRoot, "rush.yaml").toFile().exists()) {
        rushYml = Paths.get(projectRoot, "rush.yaml").toFile();
      } else {
        throw new FileNotFoundException("ERR Unable to find rush.yml file.");
      }
    }

    return parser.load(new FileInputStream(rushYml));
  }

  /**
   * Returns a JSON array of specific XML elements from the given
   * list of nodes.
   *
   * @param node   A XML node, for eg., <service>
   * @param parent Name of the node who's child nodes we want to
   *               generate. This is required because getElementsByTag()
   *               method returns all the elements that satisfy the
   *               name.
   * @return A JSON array containing XML elements
   */
  private String generateXmlElement(Node node, String parent) {
    if (node.getNodeName().equals("uses-permission")) {
      final Node permission = node.getAttributes().getNamedItem("android:name");
      if (permission != null) {
        return permission.getNodeValue();
      } else {
        throw new DOMException((short) 1, "ERR No android:name attribute found in <uses-permission>");
      }
    }

    final StringBuilder sb = new StringBuilder();
    if (node.getNodeType() == Node.ELEMENT_NODE && node.getParentNode().getNodeName().equals(parent)) {
      final Element element = (Element) node;
      final String tagName = element.getTagName();
      sb.append("<" + tagName + " ");


      if (element.hasAttributes()) {
        final NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
          if (attributes.item(i).getNodeType() == Node.ATTRIBUTE_NODE) {
            final Attr attribute = (Attr) attributes.item(i);
            sb.append(attribute.getNodeName() + " = \"" + attribute.getNodeValue() + "\" ");
          }
        }
      }

      if (element.hasChildNodes()) {
        sb.append(" >\n");
        final NodeList children = element.getChildNodes();

        for (int j = 0; j < children.getLength(); j++) {
          sb.append(generateXmlElement(children.item(j), element.getNodeName()));
        }
        sb.append("</" + tagName + ">\n");
      } else {
        sb.append("/>\n");
      }

    }
    return sb.toString();
  }

  /**
   * Puts all the application level manifest tags buildInfoJson
   *
   * @param buildInfoJson The simple_component_build_info's JSONObject
   * @param doc           The AndroidManifest.xml document
   */
  private void putApplicationElements(JSONObject buildInfoJson, Document doc) {
    final HashMap<String, String> supportedTags = new HashMap<>();
    supportedTags.put("activities", "activity");
    supportedTags.put("broadcastReceivers", "receiver");

    ArrayList<String> otherTags = new ArrayList<>();
    otherTags.add("service");
    otherTags.add("provider");
    otherTags.add("meta-data");
    otherTags.add("activity-alias");
    otherTags.add("uses-library");

    supportedTags.forEach((key, val) -> {
      JSONArray arr = new JSONArray();
      NodeList elements = doc.getElementsByTagName(val);
      if (elements.getLength() != 0) {
        for (int i = 0; i < elements.getLength(); i++) {
          arr.put(generateXmlElement(elements.item(i), "application"));
        }
      }
      buildInfoJson.put(key, arr);
    });

    // This is a sort of hack that allows adding application level
    // manifest tags by adding them to the JSON Arrays of available
    // application level tags in component_build_infos.json
    // AI2's compiler currently do not have any validation checks
    // and adds anything inside those JSON Arrays to the manifest
    // file.
    otherTags.forEach(el -> {
      JSONArray arr = new JSONArray();
      NodeList elements = doc.getElementsByTagName(el);
      if (elements.getLength() != 0) {
        for (int i = 0; i < elements.getLength(); i++) {
          arr.put(generateXmlElement(elements.item(i), "application"));
        }
      }
      arr.forEach(o -> buildInfoJson.getJSONArray("activities").put(o));
    });
  }
}
