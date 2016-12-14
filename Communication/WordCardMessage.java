package Communication;

import java.io.Serializable;

/**
 * This class describes a word card message used in the network transition.
 */
public class WordCardMessage implements Serializable {
  /**
   * Sender of this word card.
   */
  private String sender;

  /**
   * Receiver of this word card.
   */
  private String receiver;

  /**
   * The detailed content of the word card, mainly the word and definitions.
   */
  private String content;

  public WordCardMessage(String sender, String receiver, String content) {
    this.sender = sender;
    this.receiver = receiver;
    this.content = content;
  }

  public String getSender() {
    return sender;
  }

  public String getReceiver() {
    return receiver;
  }

  public String getContent() {
    return content;
  }
}
