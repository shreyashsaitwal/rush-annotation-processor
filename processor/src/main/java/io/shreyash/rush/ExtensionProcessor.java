package io.shreyash.rush;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.auto.service.AutoService;
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
@SupportedSourceVersion(SourceVersion.RELEASE_7)
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

    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleEvent.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the simple event \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeEvent");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Event event = new Event(el).build();
        extensionFieldInfo.addEvent(event);
      }
    }

    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleFunction.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the simple function \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeFunction");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Function func = new Function(el).build();
        extensionFieldInfo.addFunction(func);
      }
    }

    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleProperty.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the simple property \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeProperty");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        BlockProperty prop = null;
        try {
          prop = new BlockProperty(el, extensionFieldInfo).build();
        } catch (IllegalAccessException e) {
          this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
          assert prop != null;
          if (prop.getName() != null) {
            extensionFieldInfo.addBlockProp(prop);
          }
        }
      }
    }

    for (Element el : roundEnv.getElementsAnnotatedWith(DesignerProperty.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the designer property \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeDesignerProperty");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Property prop = null;
        try {
          prop = new Property(el, extensionFieldInfo).build();
        } catch (IllegalAccessException e) {
          this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
          assert prop != null;
          if (prop.getName() != null) {
            extensionFieldInfo.addProp(prop);
          }
        }
      }
    }

    String root = processingEnv.getOptions().get("root");
    String version = processingEnv.getOptions().get("version");
    String org = processingEnv.getOptions().get("org");
    String output = processingEnv.getOptions().get("output");

    InfoFilesGenerator generator = new InfoFilesGenerator(root, version, org, extensionFieldInfo, output);
    try {
      generator.generateBuildInfoJson();
      generator.generateSimpleCompJson();
    } catch (IOException | ParserConfigurationException | SAXException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }

    return false;
  }
}
