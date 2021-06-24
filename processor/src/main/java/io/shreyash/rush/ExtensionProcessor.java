package io.shreyash.rush;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.auto.service.AutoService;

import org.xml.sax.SAXException;

import java.io.IOException;
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

import io.shreyash.rush.blocks.BlocksDescriptorAdapter;
import io.shreyash.rush.blocks.DesignerProperty;
import io.shreyash.rush.blocks.Event;
import io.shreyash.rush.blocks.Function;
import io.shreyash.rush.blocks.Property;
import io.shreyash.rush.util.InfoFilesGenerator;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
    "com.google.appinventor.components.annotations.SimpleEvent",
    "com.google.appinventor.components.annotations.SimpleFunction",
    "com.google.appinventor.components.annotations.SimpleProperty",
    "com.google.appinventor.components.annotations.DesignerProperty"
})
public class ExtensionProcessor extends AbstractProcessor {
  private BlocksDescriptorAdapter blocksDescriptorAdapter;
  private boolean isFirstRound = true;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.blocksDescriptorAdapter = new BlocksDescriptorAdapter();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (!isFirstRound || processingEnv.getOptions().get("root") == null) {
      return true;
    }
    isFirstRound = false;

    final Messager messager = processingEnv.getMessager();
    final String org = processingEnv.getOptions().get("org");
    final String extName = processingEnv.getOptions().get("extName");

    // Process all SimpleEvents
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleEvent.class)) {
      if (!el.getEnclosingElement().getSimpleName().toString().equals(extName)) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Annotation @SimpleEvent can't be used on element '" + el.getSimpleName()
                + "'. It can only be used on members of class '" + org + "." + extName + "'.");
        continue;
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Event event = new Event(el, messager).build();
        blocksDescriptorAdapter.addSimpleEvent(event);
      } else {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Private element '" + el.getSimpleName() + "' can't be annotated with @SimpleEvent.");
      }
    }

    // Process all SimpleFunctions
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleFunction.class)) {
      if (!el.getEnclosingElement().getSimpleName().toString().equals(extName)) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Annotation @SimpleFunction can't be used on element '" + el.getSimpleName()
                + "'. It can only be used on members of class '" + org + "." + extName + "'.");
        continue;
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Function func = new Function(el, messager).build();
        blocksDescriptorAdapter.addSimpleFunction(func);
      } else {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Private element '" + el.getSimpleName() + "' can't be annotated with @SimpleFunction.");
      }
    }

    // Process all SimpleProps
    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleProperty.class)) {
      if (!el.getEnclosingElement().getSimpleName().toString().equals(extName)) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Annotation @SimpleProperty can't be used on element '" + el.getSimpleName()
                + "'. It can only be used on members of class '" + org + "." + extName + "'.");
        continue;
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Property prop = new Property(el, blocksDescriptorAdapter, messager).build();
        if (prop.getName() != null) {
          blocksDescriptorAdapter.addSimpleProperty(prop);
        }
      } else {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Private element '" + el.getSimpleName() + "' can't be annotated with @SimpleProperty.");
      }
    }

    // Process all DesignerProps
    for (Element el : roundEnv.getElementsAnnotatedWith(com.google.appinventor.components.annotations.DesignerProperty.class)) {
      if (!el.getEnclosingElement().getSimpleName().toString().equals(extName)) {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Annotation @DesignerProperty can't be used on element '" + el.getSimpleName()
                + "'. It can only be used on members of class '" + org + "." + extName + "'.");
        continue;
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        DesignerProperty prop = new DesignerProperty(el, blocksDescriptorAdapter, messager).build();
        if (prop.getName() != null) {
          blocksDescriptorAdapter.addDesignerProperty(prop);
        }
      } else {
        messager.printMessage(Diagnostic.Kind.ERROR,
            "Private element '" + el.getSimpleName() + "' can't be annotated with @DesignerProperty.");
      }
    }

    String root = processingEnv.getOptions().get("root");
    String version = processingEnv.getOptions().get("version");
    String type = org + "." + extName;
    String output = processingEnv.getOptions().get("output");

    InfoFilesGenerator generator = new InfoFilesGenerator(root, version, type, blocksDescriptorAdapter, output);
    try {
      generator.generateComponentsJson();
      generator.generateBuildInfoJson();
    } catch (IOException | ParserConfigurationException | SAXException e) {
      messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }

    return false;
  }
}
