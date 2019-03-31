package com.google.codeu.data;

import java.util.UUID;

/** The last text messaged by a user in a chat. */
public class Chat {

  private UUID id;
  private String user;
  private String text;
  private long timestamp;
  private String recipient;

  /**
   * Constructs a new {@link Chat} posted by {@code user} with {@code text} content and {@code recipient}.
   * Generates a random ID and uses the current system time for the creation time.
   */
  public Chat(String user, String text, String recipient) {
    this(UUID.randomUUID(), user, text, System.currentTimeMillis(), recipient);
  }

  /**
   * Constructs a new {@link Chat} posted by {@code id}, {@code user}, {@code text},
   * {@code timestamp} and {@code recipient}
   */
  public Chat(UUID id, String user, String text, long timestamp, String recipient) {
    this.id = id;
    this.user = user;
    this.text = text;
    this.timestamp = timestamp;
    this.recipient = recipient;
  }

  public UUID getId() {
    return id;
  }

  public String getUser() {
    return user;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getRecipient() {
    return recipient;
  }
}
