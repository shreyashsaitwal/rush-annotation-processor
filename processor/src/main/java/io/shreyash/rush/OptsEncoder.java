package io.shreyash.rush;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class OptsEncoder {
  public static void main(String[] args) throws IOException {
    final String input = args[0];
    final String[] entries = input.split(";");

    final ByteArrayOutputStream os = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(os);

    oos.writeInt(entries.length);
    for (String entry : entries) {
      final String[] keyVal = entry.split("=");
      oos.writeUTF(keyVal[0]);
      oos.writeUTF(keyVal[1]);
    }

    oos.flush();

    System.out.println(Base64.getEncoder().encodeToString(os.toByteArray()));
  }
}
