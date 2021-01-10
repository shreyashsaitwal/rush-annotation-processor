package io.shreyash.rush;

import com.google.appinventor.components.annotations.*;
import com.google.auto.service.AutoService;
import io.shreyash.rush.model.*;
import io.shreyash.rush.util.CheckName;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("com.google.appinventor.components.annotations.*")
public class ExtensionProcessor extends AbstractProcessor {

  private ExtensionInfo extensionInfo;
  private int pass = 0;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.extensionInfo = new ExtensionInfo();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    if (pass > 0) {
      return true;
    }
    pass++;

    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleEvent.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the simple event \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeEvent");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Event event = new Event(el).build();
        extensionInfo.addEvent(event);
      }
    }

    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleFunction.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the simple function \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeFunction");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        Function func = new Function(el).build();
        extensionInfo.addFunction(func);
      }
    }

    for (Element el : roundEnv.getElementsAnnotatedWith(SimpleProperty.class)) {
      if (!CheckName.isPascalCase(el)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "The name of the simple property \"" + el.getSimpleName() + "\" should be in Pascal case.\nExample: MyAwesomeProperty");
      }

      if (!el.getModifiers().contains(Modifier.PRIVATE)) {
        BlockProperty prop = null;
        try {
          prop = new BlockProperty(el, extensionInfo).build();
        } catch (IllegalAccessException e) {
          this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
          assert prop != null;
          if (prop.getName() != null) {
            extensionInfo.addBlockProp(prop);
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
          prop = new Property(el, extensionInfo).build();
        } catch (IllegalAccessException e) {
          this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        } finally {
          assert prop != null;
          if (prop.getName() != null) {
            extensionInfo.addProp(prop);
          }
        }
      }
    }

    try {
      FileObject compJson = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "extInfo.json");
      Writer writer = compJson.openWriter();
      writer.write(extensionInfo.getJson());
      writer.close();
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
    }

    return false;
  }
}
