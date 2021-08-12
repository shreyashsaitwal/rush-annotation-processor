package io.shreyash.rush.migrator.util

import java.lang.annotation.Annotation as JAnnotation
import com.google.appinventor.components.annotations.androidmanifest.ActionElement
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement
import com.google.appinventor.components.annotations.androidmanifest.CategoryElement
import com.google.appinventor.components.annotations.androidmanifest.DataElement
import com.google.appinventor.components.annotations.androidmanifest.GrantUriPermissionElement
import com.google.appinventor.components.annotations.androidmanifest.IntentFilterElement
import com.google.appinventor.components.annotations.androidmanifest.MetaDataElement
import com.google.appinventor.components.annotations.androidmanifest.PathPermissionElement
import com.google.appinventor.components.annotations.androidmanifest.ProviderElement
import com.google.appinventor.components.annotations.androidmanifest.ReceiverElement
import com.google.appinventor.components.annotations.androidmanifest.ServiceElement
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.lang.reflect.InvocationTargetException

class XmlUtil {
    /**
     * Creates a Android manifest XML element equivalent to [annotation].
     *
     * @param annotation   the annotation who's equivalent XML element is to be created
     * @param doc          the XML document
     * @param namespaceUri namespace URI for this XML document
     * @return an Android manifest XML element.
     */
    fun manifestElementForAnnotation(
        annotation: JAnnotation,
        doc: Document,
        namespaceUri: String?
    ): Element? {
        val xmlElement = createElement(annotation as Annotation, doc) ?: return null

        val annotationMethods = annotation.annotationType().declaredMethods
        for (method in annotationMethods) {
            try {
                // Invoke the method in order to obtain it's value.
                val result = method.invoke(annotation.annotationType().cast(annotation))

                // Check whether the result is an list of some annotation or not. If it is, then it
                // means that this method defines the sub-elements of this element. For e.g., an
                // `IntentFilterElement` can be a sub-element of a `ServiceElement`.
                // If its not an array, then it means that this method defines an attribute of this
                // element.
                if (result is Array<*>) {
                    result.forEach {
                        val el = manifestElementForAnnotation(it as JAnnotation, doc, namespaceUri)
                        xmlElement.appendChild(el)
                    }
                } else {
                    val attrValue = result.toString()
                    if (attrValue != "") {
                        val attr = doc.createAttributeNS(namespaceUri, "android:" + method.name)
                        attr.value = attrValue
                        xmlElement.setAttributeNode(attr)
                    }
                }
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
        }

        return xmlElement
    }

    /**
     * Creates an Android manifest XML element in [doc] equivalent to [doc].
     *
     * @param annotation the annotation who's equivalent element is to be created
     * @return an Android manifest XML element
     */
    private fun createElement(annotation: Annotation, doc: Document): Element? {
        val name = when (annotation) {
            is ActionElement -> "action"
            is ActivityElement -> "activity"
            is CategoryElement -> "category"
            is DataElement -> "data"
            is GrantUriPermissionElement -> "grant-uri-permission"
            is IntentFilterElement -> "intent-filter"
            is MetaDataElement -> "meta-data"
            is PathPermissionElement -> "path-permission"
            is ProviderElement -> "provider"
            is ReceiverElement -> "receiver"
            is ServiceElement -> "service"
            else -> return null
        }

        return doc.createElement(name)
    }
}
