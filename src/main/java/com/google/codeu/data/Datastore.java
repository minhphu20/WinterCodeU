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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.ArrayList;
import java.util.Arrays;
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
    messageEntity.setProperty("isDirectMessage", message.getIsDirectMessage());

    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String recipient, String sender) {
    List<Message> messages = new ArrayList<>();
    Query query;
     
    if (sender == ""){
      query =
        new Query("Message")
            .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(
              new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient),
              new Query.FilterPredicate("isDirectMessage", FilterOperator.EQUAL, false))))
            .addSort("timestamp", SortDirection.DESCENDING);
    }else{
      query =
        new Query("Message")
            .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.OR, Arrays.asList(
              new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(
                new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient),
                new Query.FilterPredicate("user", FilterOperator.EQUAL, sender),
                new Query.FilterPredicate("isDirectMessage", FilterOperator.EQUAL, true))),
              new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(
                new Query.FilterPredicate("recipient", FilterOperator.EQUAL, sender),
                new Query.FilterPredicate("user", FilterOperator.EQUAL, recipient),
                new Query.FilterPredicate("isDirectMessage", FilterOperator.EQUAL, true)
              )))))
            .addSort("timestamp", SortDirection.DESCENDING);
    }

    PreparedQuery results = datastore.prepare(query);

    for(Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        float sentimentScore = entity.getProperty("sentimentScore") == null
                                  ? (float) 0.0
                                  : ((Double) entity.getProperty("sentimentScore")).floatValue();
        boolean isDirectMessage = (boolean) entity.getProperty("isDirectMessage");
        String recipientProperty = (String) entity.getProperty("recipient");
        Message message = new Message(id, user, text, timestamp, recipientProperty, sentimentScore, isDirectMessage);
        messages.add(message);
      } catch(Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }

  public List<Chatroom> getRecentPrivateMessages(String recipient) {
    List<Chatroom> chatrooms = new ArrayList<>();
    
    Query query = 
      new Query("Message")
        .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.OR, Arrays.asList(
          new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(
            new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient),
            new Query.FilterPredicate("isDirectMessage", FilterOperator.EQUAL, true))),
          new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(
            new Query.FilterPredicate("user", FilterOperator.EQUAL, recipient),
            new Query.FilterPredicate("isDirectMessage", FilterOperator.EQUAL, true)
          )))))
        .addSort("timestamp", SortDirection.DESCENDING);
    
    PreparedQuery results = datastore.prepare(query);
    List<String> users = new ArrayList<>();
    UserService userService = UserServiceFactory.getUserService();
    String loggedInUser = userService.getCurrentUser().getEmail();
    
    for(Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String recipientProperty = (String) entity.getProperty("recipient");
      
        if (!loggedInUser.equals(recipientProperty) && !users.contains(recipientProperty) || loggedInUser.equals(recipientProperty) && !users.contains(user)) {
          Chatroom chatroom;
          if (loggedInUser.equalsIgnoreCase(user) && loggedInUser.equalsIgnoreCase(recipientProperty)) {
            users.add(loggedInUser);
            chatroom = new Chatroom(id, loggedInUser, "You: " + text, timestamp, recipientProperty);
          }else if (loggedInUser.equalsIgnoreCase(user)) {
            users.add(recipientProperty);
            chatroom = new Chatroom(id, loggedInUser, "You: " + text, timestamp, recipientProperty);
          } else {
            users.add(user);
            chatroom = new Chatroom(id, loggedInUser, user + ": " + text, timestamp, user);
          }
          chatrooms.add(chatroom);/* 
          System.out.println("loggedin " + loggedInUser);
          System.out.println("user " + user);
          System.out.println("recipient " + recipientProperty); */
        }
      } catch(Exception e) {
        System.err.println("Error getting chatrooms.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    
    return chatrooms;
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

    return getMessages(results);
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
  private List<Message> getMessages(PreparedQuery results) {
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

        boolean isDirectMessage = (boolean) entity.getProperty("isDirectMessage");
            // Added recipient argument
            Message message = new Message(id, user, text, timestamp, recipient, sentimentScore, isDirectMessage);
            messages.add(message);
         } catch (Exception e) {
            System.err.println("Error reading message.");
            System.err.println(entity.toString());
            e.printStackTrace();
         }
      }
    return messages;
  }

  /** Stores the User in Datastore. */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
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
    if(userEntity == null) {
      return null;
    }

    String aboutMe = (String) userEntity.getProperty("aboutMe");
    User user = new User(email, aboutMe);

    return user;
  }

  public int getTotalMessageCount(){
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }
}