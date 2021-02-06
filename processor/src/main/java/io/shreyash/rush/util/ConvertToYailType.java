package io.shreyash.rush.util;

import java.util.ArrayList;
import java.util.List;

public class ConvertToYailType {
  public static String convert(String type) throws IllegalStateException {
    if (type.startsWith(("java.util.List"))) {
      return "list";
    } else if (componentTypes().contains(type)) {
      return "component";
    }
    switch (type) {
      case "float":
      case "int":
      case "double":
      case "byte":
      case "long":
      case "short":
        return "number";

      case "java.lang.String":
        return "text";

      case "boolean":
        return type;

      case "com.google.appinventor.components.runtime.util.YailList":
        return "list";

      case "com.google.appinventor.components.runtime.util.YailDictionary":
        return "dictionary";

      case "com.google.appinventor.components.runtime.util.YailObject":
        return "yailobject";

      case "java.util.Calendar":
        return "InstantInTime";

      case "java.lang.Object":
        return "any";

      default:
        throw new IllegalStateException();
    }
  }

  private static List<String> componentTypes() {
    ArrayList<String> list = new ArrayList<>();
    list.add("com.google.appinventor.components.runtime.AccelerometerSensor");
    list.add("com.google.appinventor.components.runtime.ActivityStarter");
    list.add("com.google.appinventor.components.runtime.AndroidNonvisibleComponent");
    list.add("com.google.appinventor.components.runtime.AndroidViewComponent");
    list.add("com.google.appinventor.components.runtime.Ball");
    list.add("com.google.appinventor.components.runtime.BarcodeScanner");
    list.add("com.google.appinventor.components.runtime.Barometer");
    list.add("com.google.appinventor.components.runtime.BluetoothClient");
    list.add("com.google.appinventor.components.runtime.BluetoothConnectionBase");
    list.add("com.google.appinventor.components.runtime.BluetoothServer");
    list.add("com.google.appinventor.components.runtime.BufferedSingleValueSensor");
    list.add("com.google.appinventor.components.runtime.Button");
    list.add("com.google.appinventor.components.runtime.ButtonBase");
    list.add("com.google.appinventor.components.runtime.Camcorder");
    list.add("com.google.appinventor.components.runtime.Camera");
    list.add("com.google.appinventor.components.runtime.Canvas");
    list.add("com.google.appinventor.components.runtime.CheckBox");
    list.add("com.google.appinventor.components.runtime.Circle");
    list.add("com.google.appinventor.components.runtime.Clock");
    list.add("com.google.appinventor.components.runtime.Component");
    list.add("com.google.appinventor.components.runtime.ContactPicker");
    list.add("com.google.appinventor.components.runtime.DatePicker");
    list.add("com.google.appinventor.components.runtime.EmailPicker");
    list.add("com.google.appinventor.components.runtime.Ev3ColorSensor");
    list.add("com.google.appinventor.components.runtime.Ev3Commands");
    list.add("com.google.appinventor.components.runtime.Ev3GyroSensor");
    list.add("com.google.appinventor.components.runtime.Ev3Motors");
    list.add("com.google.appinventor.components.runtime.Ev3Sound");
    list.add("com.google.appinventor.components.runtime.Ev3TouchSensor");
    list.add("com.google.appinventor.components.runtime.Ev3UI");
    list.add("com.google.appinventor.components.runtime.Ev3UltrasonicSensor");
    list.add("com.google.appinventor.components.runtime.FeatureCollection");
    list.add("com.google.appinventor.components.runtime.File");
    list.add("com.google.appinventor.components.runtime.FirebaseDB");
    list.add("com.google.appinventor.components.runtime.Form");
    list.add("com.google.appinventor.components.runtime.FusiontablesControl");
    list.add("com.google.appinventor.components.runtime.GameClient");
    list.add("com.google.appinventor.components.runtime.GyroscopeSensor");
    list.add("com.google.appinventor.components.runtime.HorizontalArrangement");
    list.add("com.google.appinventor.components.runtime.HorizontalScrollArrangement");
    list.add("com.google.appinventor.components.runtime.HVArrangement");
    list.add("com.google.appinventor.components.runtime.Hygrometer");
    list.add("com.google.appinventor.components.runtime.Image");
    list.add("com.google.appinventor.components.runtime.ImagePicker");
    list.add("com.google.appinventor.components.runtime.ImageSprite");
    list.add("com.google.appinventor.components.runtime.Label");
    list.add("com.google.appinventor.components.runtime.LegoMindstormsEv3Base");
    list.add("com.google.appinventor.components.runtime.LegoMindstormsEv3Sensor");
    list.add("com.google.appinventor.components.runtime.LegoMindstormsNxtBase");
    list.add("com.google.appinventor.components.runtime.LegoMindstormsNxtSensor");
    list.add("com.google.appinventor.components.runtime.LightSensor");
    list.add("com.google.appinventor.components.runtime.LinearLayout");
    list.add("com.google.appinventor.components.runtime.LineString");
    list.add("com.google.appinventor.components.runtime.ListPicker");
    list.add("com.google.appinventor.components.runtime.ListView");
    list.add("com.google.appinventor.components.runtime.LocationSensor");
    list.add("com.google.appinventor.components.runtime.MagneticFieldSensor");
    list.add("com.google.appinventor.components.runtime.Map");
    list.add("com.google.appinventor.components.runtime.MapFeatureBase");
    list.add("com.google.appinventor.components.runtime.MapFeatureBaseWithFill");
    list.add("com.google.appinventor.components.runtime.MapFeatureContainerBase");
    list.add("com.google.appinventor.components.runtime.Marker");
    list.add("com.google.appinventor.components.runtime.MediaStore");
    list.add("com.google.appinventor.components.runtime.Navigation");
    list.add("com.google.appinventor.components.runtime.NearField");
    list.add("com.google.appinventor.components.runtime.Notifier");
    list.add("com.google.appinventor.components.runtime.NxtColorSensor");
    list.add("com.google.appinventor.components.runtime.NxtDirectCommands");
    list.add("com.google.appinventor.components.runtime.NxtDrive");
    list.add("com.google.appinventor.components.runtime.NxtLightSensor");
    list.add("com.google.appinventor.components.runtime.NxtSoundSensor");
    list.add("com.google.appinventor.components.runtime.NxtTouchSensor");
    list.add("com.google.appinventor.components.runtime.NxtUltrasonicSensor");
    list.add("com.google.appinventor.components.runtime.OrientationSensor");
    list.add("com.google.appinventor.components.runtime.PasswordTextBox");
    list.add("com.google.appinventor.components.runtime.Pedometer");
    list.add("com.google.appinventor.components.runtime.PhoneCall");
    list.add("com.google.appinventor.components.runtime.PhoneNumberPicker");
    list.add("com.google.appinventor.components.runtime.PhoneStatus");
    list.add("com.google.appinventor.components.runtime.Picker");
    list.add("com.google.appinventor.components.runtime.Player");
    list.add("com.google.appinventor.components.runtime.Polygon");
    list.add("com.google.appinventor.components.runtime.PolygonBase");
    list.add("com.google.appinventor.components.runtime.ProximitySensor");
    list.add("com.google.appinventor.components.runtime.Rectangle");
    list.add("com.google.appinventor.components.runtime.SensorComponent");
    list.add("com.google.appinventor.components.runtime.Serial");
    list.add("com.google.appinventor.components.runtime.Sharing");
    list.add("com.google.appinventor.components.runtime.SingleValueSensor");
    list.add("com.google.appinventor.components.runtime.Slider");
    list.add("com.google.appinventor.components.runtime.Sound");
    list.add("com.google.appinventor.components.runtime.SoundRecorder");
    list.add("com.google.appinventor.components.runtime.SpeechRecognizer");
    list.add("com.google.appinventor.components.runtime.Spinner");
    list.add("com.google.appinventor.components.runtime.Sprite");
    list.add("com.google.appinventor.components.runtime.Switch");
    list.add("com.google.appinventor.components.runtime.TableArrangement");
    list.add("com.google.appinventor.components.runtime.TableLayout");
    list.add("com.google.appinventor.components.runtime.TextBox");
    list.add("com.google.appinventor.components.runtime.TextBoxBase");
    list.add("com.google.appinventor.components.runtime.Texting");
    list.add("com.google.appinventor.components.runtime.TextToSpeech");
    list.add("com.google.appinventor.components.runtime.Thermometer");
    list.add("com.google.appinventor.components.runtime.TimePicker");
    list.add("com.google.appinventor.components.runtime.TinyDB");
    list.add("com.google.appinventor.components.runtime.TinyWebDB");
    list.add("com.google.appinventor.components.runtime.ToggleBase");
    list.add("com.google.appinventor.components.runtime.Twitter");
    list.add("com.google.appinventor.components.runtime.VerticalArrangement");
    list.add("com.google.appinventor.components.runtime.VerticalScrollArrangement");
    list.add("com.google.appinventor.components.runtime.VideoPlayer");
    list.add("com.google.appinventor.components.runtime.VisibleComponent");
    list.add("com.google.appinventor.components.runtime.Voting");
    list.add("com.google.appinventor.components.runtime.Web");
    list.add("com.google.appinventor.components.runtime.WebViewer");
    list.add("com.google.appinventor.components.runtime.YandexTranslate");

    return list;
  }

}
