package Dictionary;

/**
 * The definition for a word.
 */
public class Definition {
  /**
   * The part of speech.
   */
  private String pos;

  /**
   * The definition.
   */
  private String def;

  public Definition(String pos, String def) {
    this.pos = pos;
    this.def = def;
  }

  public String getPos() {
    return pos;
  }

  public String getDef() {
    return def;
  }
}
