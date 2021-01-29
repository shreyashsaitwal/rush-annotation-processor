package io.shreyash.rush;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.auto.service.AutoService;

import com.amihaiemil.eoyaml.exceptions.YamlReadingException;
import io.shreyash.rush.model.*;
import io.shreyash.rush.util.CheckName;
import io.shreyash.rush.util.InfoFilesGenerator;
import org.xml.sax.SAXException;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("com.google.appinventor.components.annotations.*")
public class ExtensionProcessor extends AbstractProcessor {

  private ExtensionFieldInfo extensionFieldInfo;
  private boolean isFirstRound = true;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.extensionFieldInfo = new ExtensionFieldInfo();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!isFirstRound) {
      return true;
    }
    isFirstRound = false;

    final Messager messager = processingEnv.getMessager();

    // Process all SimpleEvents
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleEvent.class)) {
      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        if (!CheckName.isPascalCase(el)) {
          messager.printMessage(Diagnostic.Kind.WARNING, "@SimpleEvent " + el.getSimpleName() + "'s name should follow PascalCase naming convention.");
        }
        Event event = new Event(el).build();
        extensionFieldInfo.addEvent(event);
      } else {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Private element " + el.getSimpleName() + " can't be annotated with @SimpleEvent.");
      }
    }

    // Process all SimpleFunctions
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleFunction.class)) {
      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        if (!CheckName.isPascalCase(el)) {
          messager.printMessage(Diagnostic.Kind.WARNING, "@SimpleFunction " + el.getSimpleName() + "'s name should follow PascalCase naming convention.");
        }
        Function func = new Function(el).build();
        extensionFieldInfo.addFunction(func);
      } else {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Private element " + el.getSimpleName() + " can't be annotated with @SimpleFunction.");
      }
    }

    // Process all SimpleProps
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleProperty.class)) {
      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        if (!CheckName.isPascalCase(el)) {
          messager.printMessage(Diagnostic.Kind.WARNING, "@SimpleProperty " + el.getSimpleName() + "'s name should follow PascalCase naming convention.");
        }
        BlockProperty prop = null;
        try {
          prop = new BlockProperty(el, extensionFieldInfo).build();
        } catch (IllegalAccessException e) {
          messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
          assert prop != null;
          if (prop.getName() != null) {
            extensionFieldInfo.addBlockProp(prop);
          }
        }
      } else {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Private element " + el.getSimpleName() + " can't be annotated with @SimpleProperty.");
      }
    }

    // Process all DesignerProps
    for (Element el : roundEnv.getElementsAnnotatedWith(DesignerProperty.class)) {
      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        if (!CheckName.isPascalCase(el)) {
          messager.printMessage(Diagnostic.Kind.WARNING, "@DesignerProperty " + el.getSimpleName() + "'s name should follow PascalCase naming convention.");
        }
        Property prop = null;
        try {
          prop = new Property(el, extensionFieldInfo).build();
        } catch (IllegalAccessException e) {
          messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
          assert prop != null;
          if (prop.getName() != null) {
            extensionFieldInfo.addProp(prop);
          }
        }
      } else {
        messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, "Private element " + el.getSimpleName() + " can't be annotated with @DesignerProperty.");
      }
    }

    String root = processingEnv.getOptions().get("root");
    String version = processingEnv.getOptions().get("version");
    String type = processingEnv.getOptions().get("type");
    String output = processingEnv.getOptions().get("output");

    InfoFilesGenerator generator = new InfoFilesGenerator(root, version, type, extensionFieldInfo, output);
    try {
      generator.generateBuildInfoJson();
      generator.generateSimpleCompJson();
    } catch (IOException | ParserConfigurationException | SAXException | YamlReadingException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }

    return false;
  }
}
