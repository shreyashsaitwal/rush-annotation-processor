package io.shreyash.rush.migrator.util;

import com.google.appinventor.components.annotations.androidmanifest.ActionElement;
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement;
import com.google.appinventor.components.annotations.androidmanifest.CategoryElement;
import com.google.appinventor.components.annotations.androidmanifest.DataElement;
import com.google.appinventor.components.annotations.androidmanifest.GrantUriPermissionElement;
import com.google.appinventor.components.annotations.androidmanifest.IntentFilterElement;
import com.google.appinventor.components.annotations.androidmanifest.MetaDataElement;
import com.google.appinventor.components.annotations.androidmanifest.PathPermissionElement;
import com.google.appinventor.components.annotations.androidmanifest.ProviderElement;
import com.google.appinventor.components.annotations.androidmanifest.ReceiverElement;
import com.google.appinventor.components.annotations.androidmanifest.ServiceElement;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlUtil {

  /**
   * Infers and creates a XML element from the {@param annotation} and appends it to {@param parent}
   *
   * @param annotation   the annotation who's equivalent XML element is to be created
   * @param doc          the XML document
   * @param parent       the XML element to which the created element is to be appended
   * @param namespaceUri namespace URI for this XML document
   */
  public void appendElement(Annotation annotation, Document doc, Element parent, String namespaceUri) {
    final String name = getElementName(annotation);
    if (name.equals("")) {
      return;
    }

    final Element element = doc.createElement(name);
    final List<Method> methods = new ArrayList<>(Arrays.asList(annotation.annotationType().getDeclaredMethods()));

    for (Method method : methods) {
      try {
        // Invoke the method in order to obtain it's value.
        final Object result = method.invoke(annotation.annotationType().cast(annotation));

        // Check whether the result is an array of some annotation or not. If it is, then it means
        // that this method defines the sub-elements of this element. For eg, an `IntentFilterElement`
        // can be a sub-element of a `ServiceElement`.
        // If its not an array, then it means that this method defines an attribute of this element.
        if (result instanceof Annotation[]) {
          final Annotation[] annotations = (Annotation[]) result;
          for (Annotation an : annotations) {
            appendElement(an, doc, element, namespaceUri);
          }
        } else {
          final String attrValue = result.toString();
          if (!attrValue.equals("")) {
            Attr attr = doc.createAttributeNS(namespaceUri, "android:" + method.getName());
            attr.setValue(attrValue);
            element.setAttributeNode(attr);
          }
        }
      } catch (IllegalAccessException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }

    parent.appendChild(element);
  }

  /**
   * Returns the corresponding XML tag name of {@param annotation}
   *
   * @param annotation the annotation who's corresponding XML tag name is to be obtained
   * @return XML tag name of {@param annotation}
   */
  private String getElementName(Annotation annotation) {
    if (annotation instanceof ActionElement) {
      return "action";
    } else if (annotation instanceof ActivityElement) {
      return "activity";
    } else if (annotation instanceof CategoryElement) {
      return "category";
    } else if (annotation instanceof DataElement) {
      return "data";
    } else if (annotation instanceof GrantUriPermissionElement) {
      return "grant-uri-permission";
    } else if (annotation instanceof IntentFilterElement) {
      return "intent-filter";
    } else if (annotation instanceof MetaDataElement) {
      return "meta-data";
    } else if (annotation instanceof PathPermissionElement) {
      return "path-permission";
    } else if (annotation instanceof ProviderElement) {
      return "provider";
    } else if (annotation instanceof ReceiverElement) {
      return "receiver";
    } else if (annotation instanceof ServiceElement) {
      return "service";
    }
    return "";
  }
}
