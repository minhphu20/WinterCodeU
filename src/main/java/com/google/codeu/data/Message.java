/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.data;

import java.util.UUID;

/** A single message posted by a user. */
public class Message {

  private UUID id;
  private String user;
  private String text;
  private long timestamp;
  private String recipient;
  private float sentimentScore;
  private boolean isDirectMessage;
  private String imageUrl;
  private String imageLabels;

  /**
   * Constructs a new {@link Message} posted by {@code user} with {@code text} content and {@code recipient}.
   * Generates a random ID and uses the current system time for the creation time.
   */

  public Message(String user, String text, String recipient, float sentimentScore) {
    this(UUID.randomUUID(), user, text, System.currentTimeMillis(), recipient, sentimentScore);
  }

  public Message(String user, String text, String recipient, float sentimentScore, String imageUrl, String imageLabels, boolean isDirectMessage) {
    this(UUID.randomUUID(), user, text, System.currentTimeMillis(), recipient, sentimentScore, imageUrl, imageLabels);

  public Message(String user, String text, String recipient, float sentimentScore, String imageUrl, boolean isDirectMessage) {
    this(UUID.randomUUID(), user, text, System.currentTimeMillis(), recipient, sentimentScore, isDirectMessage);

  }

  /**
   * Constructs a new {@link Message} posted by {@code id}, {@code user}, {@code text},
   * {@code timestamp} and {@code recipient}
   */
  public Message(UUID id, String user, String text, long timestamp, String recipient, float sentimentScore, String imageUrl, boolean isDirectMessage) {
    this.id = id;
    this.user = user;
    this.text = text;
    this.timestamp = timestamp;
    this.recipient = recipient;
    this.sentimentScore = sentimentScore;
    this.imageUrl = imageUrl;
    this.isDirectMessage = isDirectMessage;
  }

  public Message(UUID id, String user, String text, long timestamp, String recipient, float sentimentScore, String imageUrl, String imageLabels, boolean isDirectMessage) {
    this.id = id;
    this.user = user;
    this.text = text;
    this.timestamp = timestamp;
    this.recipient = recipient;
    this.sentimentScore = sentimentScore;
    this.imageUrl = imageUrl;
    this.imageLabels = imageLabels;
    this.isDirectMessage = isDirectMessage;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public void setImageLabels(String imageLabel) {
    this.imageLabels = imageLabel;
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

  public float getSentimentScore() {
    return sentimentScore;
  }

  public String getRecipient() {
    return recipient;
  }

  public boolean getIsDirectMessage() {
    return isDirectMessage;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }

  public String getImageLabels() {
    return imageLabels;
  }

}
