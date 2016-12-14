package Communication;

import java.io.Serializable;

/**
 * This class describes a word card message used in the network transition.
 */
public class WordCardMessage implements Serializable {
  /**
   * id is the primary key to distinguish each word card message.
   */
  private int id;

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

  public WordCardMessage(int id, String sender, String receiver, String content) {
    this.id = id;
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
