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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/** Provides access to the data stored in Datastore. */
public class Datastore {
  private DatastoreService datastore;

  public Datastore() {
     datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("recipient", message.getRecipient());
    messageEntity.setProperty("sentimentScore", message.getSentimentScore());

    if (message.getImageUrl() != null) {
          messageEntity.setProperty("imageUrl", message.getImageUrl());
    }

    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String recipient) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient))
            .addSort("timestamp", SortDirection.DESCENDING);

    PreparedQuery results = datastore.prepare(query);

    return getMessagesFromQuery(results);
  }

  /**
   * Fetches the messages of all users, or an empty list if there are no users.
   *
   * @return a list of messages posted by all users, or empty list if noone has
   *     ever posted a message. List is sorted by time ascending if ascending is
   *     set to true, else it is sorted by time descending.
   */

  public List<Message> getAllMessages(boolean ascending) {
    Query query = new Query("Message");
    if (ascending == true) {
      query.addSort("timestamp", SortDirection.ASCENDING);
    } else {
      query.addSort("timestamp", SortDirection.DESCENDING);
    }

    PreparedQuery results = datastore.prepare(query);

    return getMessagesFromQuery(results);
  }

  /**
   * Overload getAllMessages(boolean ascending) to make ascending default to false.
   *
   * @return a list of messages posted by all users, or empty list if noone has
   *     ever posted a message. List is sorted by time descending.
   */
  public List<Message> getAllMessages() {
    return getAllMessages(false);
  }

  /**
   * Returns the messages inside PreparedQuery results.
   *
   * @return a list of messages inside PreparedQuery results.
   */
  private List<Message> getMessagesFromQuery(PreparedQuery results) {
    List<Message> messages = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String recipient = (String) entity.getProperty("recipient");
        float sentimentScore =
            entity.getProperty("sentimentScore") == null
                ? (float) 0.0
                : ((Double) entity.getProperty("sentimentScore")).floatValue();

            // Added recipient argument
            Message message = new Message(id, user, text, timestamp, recipient, sentimentScore);
            messages.add(message);
         } catch (Exception e) {
            System.err.println("Error reading message.");
            System.err.println(entity.toString());
            e.printStackTrace();
         }
      }

    return messages;
  }

  /**
   * Gets total message count.
   *
   * @return the total number of messages posted by all users.
   */
  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }

  /**
   * Get markers that user adds.
   */
  public List<UserMarker> getMarkers() {
    List<UserMarker> markers = new ArrayList<>();

    Query query = new Query("UserMarker");
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      try {
        double lat = (double) entity.getProperty("lat");
        double lng = (double) entity.getProperty("lng");
        String content = (String) entity.getProperty("content");

        UserMarker marker = new UserMarker(lat, lng, content);
        markers.add(marker);
      } catch (Exception e) {
        System.err.println("Error reading marker.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return markers;
  }

  /**
   * Store markers to datastore.
   */
  public void storeMarker(UserMarker marker) {
    Entity markerEntity = new Entity("UserMarker");
    markerEntity.setProperty("lat", marker.getLat());
    markerEntity.setProperty("lng", marker.getLng());
    markerEntity.setProperty("content", marker.getContent());
    datastore.put(markerEntity);
  }

  /**
   * Stores the User in datastore.
   */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    userEntity.setProperty("likes", user.getLikes());
    userEntity.setProperty("notLikes", user.getNotLikes());
    datastore.put(userEntity);
  }

  /**
   * Returns the User owned by the email address, or
   * null if no matching User was found.
   */
  public User getUser(String email) {
    Query query = new Query("User")
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();
    if (userEntity == null) {
      return null;
    }
    return this.getUserFromEntity(userEntity);
  }

  /**
   * Gets a list of all users.
   */
  public List<User> getAllUsers() {
    Query query = new Query("User");
    query.addSort("email", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);

    List<User> users = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      User user = this.getUserFromEntity(entity);
      if (user != null) {
        users.add(user);
      }
    }
    return users;
  }

  /**
   * Gets the user inside the entity.
   * @param entity
   * @return User from the entity. In case of error, returns null.
   */
  private User getUserFromEntity(Entity entity) {
    User user = null;
    try {
      String email = (String) entity.getProperty("email");
      String aboutMe = (String) entity.getProperty("aboutMe");
      System.out.println("User: " + email);
      HashSet<String> likes = new HashSet<String>();
      HashSet<String> notLikes = new HashSet<String>();
      if (entity.getProperty("likes") != null) {
        likes = new HashSet<String>((ArrayList<String>) entity.getProperty("likes"));
      }
      if (entity.getProperty("notLikes") != null) {
        notLikes = new HashSet<String>((ArrayList<String>) entity.getProperty("notLikes"));
      }
      user = new User(email, aboutMe, likes, notLikes);
    } catch (Exception e) {
      System.err.println("Error reading user.");
      System.err.println(entity.toString());
      e.printStackTrace();
    }
    return user;
  }

  /**
   * Checks if userA is liked by userB.
   */
  public Boolean isLiked(User userA, User userB) {
    String userAEmail = userA.getEmail();
    System.out.println(userAEmail);
    System.out.println(userB);
    HashSet<String> likedByB = userB.getLikes();
    System.out.println("gettting liked by B done");
    if (likedByB == null) {
      return false;
    }
    System.out.println("Done checking is liked.");
    return likedByB.contains(userAEmail);
  }

  /**
   * Returns a list of users who have not been liked or not liked by user.
   */
  public HashSet<User> notSeenBy(User user){
    List<User> allUsers = this.getAllUsers();
    HashSet<String> liked = user.getLikes();
    HashSet<String> notLiked = user.getLikes();
    HashSet<User> notSeen = new HashSet<User>();
    for (User target : allUsers) {
      if (user.getEmail() != target.getEmail() && !liked.contains(target.getEmail()) && !notLiked.contains(target.getEmail())) {
        notSeen.add(target);
      }
    }
    return notSeen;
  }

}


