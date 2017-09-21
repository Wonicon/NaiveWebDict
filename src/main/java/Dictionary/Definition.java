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
  private String[] defs;

  public Definition(String pos, String[] defs) {
    this.pos = pos;
    this.defs = defs;
  }

  public String getPos() {
    return pos;
  }

  public String[] getDefs() {
    return defs;
  }
}
