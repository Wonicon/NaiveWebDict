package Glossary;

import java.io.FileWriter;
import java.io.IOException;

public class GlossaryCSV {
  private StringBuilder csv = new StringBuilder();

  private boolean isSaved = false;

  public void addEntry(String word, String pos, String def, String source, String sentence) {
    csv.append(String.join(", ", word, pos, def, source, sentence)).append("\n");
    isSaved = false;
  }

  public String export(String filename) {
    try (FileWriter writer = new FileWriter(filename)) {
      writer.write(csv.toString());
      isSaved = true;
      return "Success";
    }
    catch (IOException e) {
      return e.toString();
    }
  }

  public String view() {
    return csv.toString();
  }

  public boolean isSaved() {
    return isSaved;
  }
}
