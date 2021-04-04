package io.shreyash.rush.migrator;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesActivities;
import com.google.appinventor.components.annotations.UsesActivityMetadata;
import com.google.appinventor.components.annotations.UsesApplicationMetadata;
import com.google.appinventor.components.annotations.UsesAssets;
import com.google.appinventor.components.annotations.UsesBroadcastReceivers;
import com.google.appinventor.components.annotations.UsesContentProviders;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.annotations.UsesServices;
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement;
import com.google.appinventor.components.annotations.androidmanifest.MetaDataElement;
import com.google.appinventor.components.annotations.androidmanifest.ProviderElement;
import com.google.appinventor.components.annotations.androidmanifest.ReceiverElement;
import com.google.appinventor.components.annotations.androidmanifest.ServiceElement;
import com.google.auto.service.AutoService;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import io.shreyash.rush.migrator.util.XmlUtil;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "com.google.appinventor.components.annotations.DesignerComponent",
    "com.google.appinventor.components.annotations.SimpleObject",
    "com.google.appinventor.components.annotations.UsesActivities",
    "com.google.appinventor.components.annotations.UsesActivityMetadata",
    "com.google.appinventor.components.annotations.UsesApplicationMetadata",
    "com.google.appinventor.components.annotations.UsesAssets",
    "com.google.appinventor.components.annotations.UsesBroadcastReceivers",
    "com.google.appinventor.components.annotations.UsesContentProviders",
    "com.google.appinventor.components.annotations.UsesLibraries",
    "com.google.appinventor.components.annotations.UsesNativeLibraries",
    "com.google.appinventor.components.annotations.UsesPermissions",
    "com.google.appinventor.components.annotations.UsesServices",
    "com.google.appinventor.components.annotations.androidmanifest.ActionElement",
    "com.google.appinventor.components.annotations.androidmanifest.ActivityElement",
    "com.google.appinventor.components.annotations.androidmanifest.CategoryElement",
    "com.google.appinventor.components.annotations.androidmanifest.DataElement",
    "com.google.appinventor.components.annotations.androidmanifest.GrantUriPermissionElement",
    "com.google.appinventor.components.annotations.androidmanifest.IntentFilterElement",
    "com.google.appinventor.components.annotations.androidmanifest.MetaDataElement",
    "com.google.appinventor.components.annotations.androidmanifest.PathPermissionElement",
    "com.google.appinventor.components.annotations.androidmanifest.ProviderElement",
    "com.google.appinventor.components.annotations.androidmanifest.ReceiverElement",
    "com.google.appinventor.components.annotations.androidmanifest.ServiceElement"
})
public class Migrator extends AbstractProcessor {
  @Override
  public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {
    final String manifestPath = processingEnv.getOptions().get("manifestPath");
    final String extName = processingEnv.getOptions().get("extName");
    final String rushYmlPath = processingEnv.getOptions().get("rushYmlPath");

    final Messager messager = processingEnv.getMessager();

    for (Element el : roundEnv.getElementsAnnotatedWith(DesignerComponent.class)) {
      SimpleObject so = el.getAnnotation(SimpleObject.class);
      if (so.external()) {
        messager.printMessage(Diagnostic.Kind.NOTE, "External component class named \"" +
            el.getSimpleName().toString() + "\" detected.");
        try {
          generateAndroidManifest(el, manifestPath);
          generateRushYml(el, extName, rushYmlPath);
        } catch (TransformerException | ParserConfigurationException | IOException e) {
          e.printStackTrace();
        }
      }
    }

    return false;
  }

  /**
   * Generates rush.yml for {@param comp}.
   *
   * @param comp        the element for which rush.yml is to be produced
   * @param extName     the name of the extension which is to be produced
   * @param rushYmlPath the path where the generated rush.yml is to be stored
   */
  private void generateRushYml(Element comp, String extName, String rushYmlPath) throws IOException {
    final String moreInfo = "# For a detailed info on this file and supported fields, check out this" +
        "\n# link: https://github.com/ShreyashSaitwal/rush-cli/wiki/Metadata-File\n";
    final String optimizeComment = "# Un-comment the below field if you wish to apply ProGuard while" +
        " building\n# a release build ('-r') of your extension:\n# release:\n#   optimize: true\n";

    final DesignerComponent dc = comp.getAnnotation(DesignerComponent.class);

    // The reason behind using a string builder here instead of the YAML library that's being used
    // for the processor is that that library provides only immutable builders to construct YAML,
    // and you cannot add fields to it after it has been instantiated.
    // This is sort of a work-around. It'd be much better to just use some other YAML parsing library
    // for the whole project.
    final StringBuilder content = new StringBuilder();
    content.append(moreInfo + "\n---\n")
        .append("name: " + extName + "\n");

    content.append("description: ")
        .append(!dc.description().equals("") ? dc.description()
            : "Extension component for " + extName + ". Built with Rush.")
        .append("\n\n");

    content.append("version: \n")
        .append("  number: " + dc.version() + "\n")
        .append("  name: ")
        .append(!dc.versionName().equals("") ? dc.versionName() : dc.version())
        .append("\n\n");

    content.append(optimizeComment + "\n");
    content.append("min_sdk: " + dc.androidMinSdk() + "\n\n");

    content.append("assets: \n")
        .append("  icon: ")
        .append(!dc.iconName().equals("") ? dc.iconName() : "icon.png")
        .append("\n");
    final UsesAssets ua = comp.getAnnotation(UsesAssets.class);
    if (ua != null) {
      content.append("  other:\n");
      for (final String file : ua.fileNames().split(",")) {
        content.append("    - " + file.trim() + "\n");
      }
    }
    content.append("\n");

    final UsesLibraries ul = comp.getAnnotation(UsesLibraries.class);
    if (ul != null) {
      content.append("deps:\n");
      for (final String lib : ul.libraries().split(",")) {
        content.append("  - " + lib.trim() + "\n");
      }
      content.append("\n");
    }

    final FileWriter writer = new FileWriter(Paths.get(rushYmlPath).toFile());
    writer.write(content.toString());
    writer.flush();
    writer.close();
  }

  /**
   * Generates AndroidManifest.xml for {@param comp}
   *
   * @param comp         the element for which AndroidManifest.xml is to be generated
   * @param manifestPath the path to where the generated manifest file is to br stored.
   */
  private void generateAndroidManifest(Element comp, String manifestPath) throws TransformerException, ParserConfigurationException {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

    final Document doc = documentBuilder.newDocument();
    doc.setXmlVersion("1.0");

    final String namespaceUri = "http://schemas.android.com/apk/res/android";

    final org.w3c.dom.Element root = doc.createElement("manifest");
    doc.appendChild(root);

    final Attr vc = doc.createAttributeNS(namespaceUri, "android:versionCode");
    final Attr vn = doc.createAttributeNS(namespaceUri, "android:versionName");
    vc.setValue("1");
    vn.setValue("1.0");
    root.setAttributeNode(vc);
    root.setAttributeNode(vn);

    final org.w3c.dom.Element applicationElement = doc.createElement("application");
    root.appendChild(applicationElement);

    final UsesPermissions usesPermissions = comp.getAnnotation(UsesPermissions.class);
    if (usesPermissions != null) {
      final String[] permissions = usesPermissions.permissionNames().split(",");
      for (String permission : permissions) {
        final org.w3c.dom.Element permissionEl = doc.createElement("uses-permission");
        final Attr attr = doc.createAttributeNS(namespaceUri, "android:name");
        attr.setValue(permission);
        permissionEl.setAttributeNode(attr);
        root.appendChild(permissionEl);
      }
    }

    final XmlUtil xmlUtil = new XmlUtil();

    final UsesActivities usesActivities = comp.getAnnotation(UsesActivities.class);
    if (usesActivities != null) {
      for (ActivityElement activityElement : usesActivities.activities()) {
        xmlUtil.appendElement(activityElement, doc, applicationElement, namespaceUri);
      }
    }

    final UsesActivityMetadata usesActivityMetadata = comp.getAnnotation(UsesActivityMetadata.class);
    if (usesActivityMetadata != null) {
      for (MetaDataElement metaDataElement : usesActivityMetadata.metaDataElements()) {
        xmlUtil.appendElement(metaDataElement, doc, applicationElement, namespaceUri);
      }
    }

    final UsesApplicationMetadata usesApplicationMetadata = comp.getAnnotation(UsesApplicationMetadata.class);
    if (usesApplicationMetadata != null) {
      for (MetaDataElement metaDataElement : usesApplicationMetadata.metaDataElements()) {
        xmlUtil.appendElement(metaDataElement, doc, applicationElement, namespaceUri);
      }
    }

    final UsesBroadcastReceivers usesBroadcastReceivers = comp.getAnnotation(UsesBroadcastReceivers.class);
    if (usesBroadcastReceivers != null) {
      for (ReceiverElement receiverElement : usesBroadcastReceivers.receivers()) {
        xmlUtil.appendElement(receiverElement, doc, applicationElement, namespaceUri);
      }
    }

    final UsesContentProviders usesContentProviders = comp.getAnnotation(UsesContentProviders.class);
    if (usesContentProviders != null) {
      for (ProviderElement providerElement : usesContentProviders.providers()) {
        xmlUtil.appendElement(providerElement, doc, applicationElement, namespaceUri);
      }
    }

    final UsesServices usesServices = comp.getAnnotation(UsesServices.class);
    if (usesServices != null) {
      for (ServiceElement serviceElement : usesServices.services()) {
        xmlUtil.appendElement(serviceElement, doc, applicationElement, namespaceUri);
      }
    }

    final DOMSource domSource = new DOMSource(doc);
    final StreamResult streamResult = new StreamResult(Paths.get(manifestPath).toFile());

    final TransformerFactory tf = TransformerFactory.newInstance();
    final Transformer transformer = tf.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
    transformer.transform(domSource, streamResult);
  }
}