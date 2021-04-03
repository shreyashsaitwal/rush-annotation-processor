package io.shreyash.rush.migrator;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesActivities;
import com.google.appinventor.components.annotations.UsesActivityMetadata;
import com.google.appinventor.components.annotations.UsesApplicationMetadata;
import com.google.appinventor.components.annotations.UsesBroadcastReceivers;
import com.google.appinventor.components.annotations.UsesContentProviders;
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
    final Messager messager = processingEnv.getMessager();

    for (Element el : roundEnv.getElementsAnnotatedWith(DesignerComponent.class)) {
      SimpleObject so = el.getAnnotation(SimpleObject.class);
      if (so.external()) {
        generateAndroidManifest(el, messager, manifestPath);
        messager.printMessage(Diagnostic.Kind.NOTE, "External component class named \"" + el.getSimpleName().toString() + "\" detected.");
      }
    }

    return false;
  }

  /**
   * Generates AndroidManifest.xml for {@param comp}
   *
   * @param comp         the element for which AndroidManifest.xml is to be generated
   * @param messager     the messager for this processing env
   * @param manifestPath the path to where the generated manifest file is to br stored.
   */
  private void generateAndroidManifest(Element comp, Messager messager, String manifestPath) {
    final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }

    if (documentBuilder == null) {
      messager.printMessage(Diagnostic.Kind.ERROR, "ERR Something went wrong while instantiating document builder.");
    }

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
    try {
      final Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, streamResult);
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }
}