package io.shreyash.rush;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.auto.service.AutoService;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.xml.parsers.ParserConfigurationException;

import io.shreyash.rush.blocks.BlockStore;
import io.shreyash.rush.blocks.DesignerProperty;
import io.shreyash.rush.blocks.Event;
import io.shreyash.rush.blocks.Method;
import io.shreyash.rush.blocks.Property;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "com.google.appinventor.components.annotations.SimpleEvent",
    "com.google.appinventor.components.annotations.SimpleFunction",
    "com.google.appinventor.components.annotations.SimpleProperty",
    "com.google.appinventor.components.annotations.DesignerProperty"
})
public class ExtensionProcessor extends AbstractProcessor {
  private final BlockStore store = BlockStore.getInstance();

  private boolean isFirstRound = true;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!this.isFirstRound || this.processingEnv.getOptions().get("root") == null) {
      return true;
    }
    this.isFirstRound = false;

    final String org = this.processingEnv.getOptions().get("org");
    final String extName = this.processingEnv.getOptions().get("extName");

    // Process all SimpleEvents
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleEvent.class)) {
      if (!isInRightParent(el, extName, org, "@SimpleEvent")) {
        continue;
      }

      if (isPublic(el, "@SimpleEvent")) {
        Event event = new Event(el, this.messager);
        this.store.putEvent(event);
      }
    }

    // Process all SimpleFunctions
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleFunction.class)) {
      if (!isInRightParent(el, extName, org, "@SimpleFunction")) {
        continue;
      }

      if (isPublic(el, "@SimpleFunction")) {
        Method method = new Method(el, this.messager);
        this.store.putMethod(method);
      }
    }

    // Process all SimpleProps
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleProperty.class)) {
      if (!isInRightParent(el, extName, org, "@SimpleProperty")) {
        continue;
      }

      if (isPublic(el, "@SimpleProperty")) {
        Property prop = new Property(el, this.messager);
        this.store.putProperty(prop);
      }
    }

    // Process all DesignerProps
    for (Element el : roundEnv.getElementsAnnotatedWith(com.google.appinventor.components.annotations.DesignerProperty.class)) {
      if (!isInRightParent(el, extName, org, "@DesignerProperty")) {
        continue;
      }

      if (isPublic(el, "@DesignerProperty")) {
        DesignerProperty prop = new DesignerProperty(el, this.messager);
        this.store.putDesignerProperty(prop);
      }
    }

    generateInfoFiles(extName, org);

    return false;
  }

  private void generateInfoFiles(String extName, String org) {
    String root = this.processingEnv.getOptions().get("root");
    String version = this.processingEnv.getOptions().get("version");
    String type = org + "." + extName;
    String output = this.processingEnv.getOptions().get("output");

    InfoFilesGenerator generator = new InfoFilesGenerator(root, version, type, output);
    try {
      generator.generateComponentsJson();
      generator.generateBuildInfoJson();
    } catch (IOException | ParserConfigurationException | SAXException e) {
      this.messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }
  }

  private boolean isInRightParent(Element el, String extName, String org, String annotationName) {
    final boolean res = el.getEnclosingElement().getSimpleName().toString().equals(extName);

    if (!res) {
      this.messager.printMessage(Diagnostic.Kind.ERROR,
          "Annotation" + annotationName + "can't be used on element \"" + el.getSimpleName()
              + "\". It can only be used on members of class \"" + org + "." + extName + "\".");
    }

    return res;
  }

  private boolean isPublic(Element el, String annotationName) {
    final boolean isPublic = el.getModifiers().contains(Modifier.PUBLIC);

    if (!isPublic) {
      messager.printMessage(Diagnostic.Kind.ERROR,
          "Private element \"" + el.getSimpleName() + "\" can't be annotated with" + annotationName + " .");
    }

    return isPublic;
  }
}
