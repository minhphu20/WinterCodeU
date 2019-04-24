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

import java.util.*;

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

    if (message.getImageUrl() != null) {
          messageEntity.setProperty("imageUrl", message.getImageUrl());
    }
    messageEntity.setProperty("imageLabels", message.getImageLabels());

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
     
    if (sender == "") {
      query =
        new Query("Message")
            .setFilter(new Query.CompositeFilter(Query.CompositeFilterOperator.AND, Arrays.asList(
              new Query.FilterPredicate("recipient", FilterOperator.EQUAL, recipient),
              new Query.FilterPredicate("isDirectMessage", FilterOperator.EQUAL, false))))
            .addSort("timestamp", SortDirection.DESCENDING);
    } else {
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
            .addSort("timestamp", SortDirection.ASCENDING);
    }

    PreparedQuery results = datastore.prepare(query);

    for(Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        String imageUrl = (String) entity.getProperty("imageUrl");
        String imageLabels = (String) entity.getProperty("imageLabels");
        float sentimentScore = entity.getProperty("sentimentScore") == null
                                  ? (float) 0.0
                                  : ((Double) entity.getProperty("sentimentScore")).floatValue();
        boolean isDirectMessage = (boolean) entity.getProperty("isDirectMessage");
        String recipientProperty = (String) entity.getProperty("recipient");
        Message message = new Message(id, user, text, timestamp, recipientProperty, sentimentScore, imageUrl, imageLabels, isDirectMessage);
        messages.add(message);
      } catch(Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }

  /**
   * Gets the most recent private messages that the user recieved from or had sent to another user.
   *
   * @return a list of private messages, or empty list if user has never recieved or sent a private
   *     message. List is sorted by time descending.
   */
  public List<Message> getRecentPrivateMessages(String recipient) {
    List<Message> recentChats = new ArrayList<>();
    
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
        String imageUrl = (String) entity.getProperty("imageUrl");
        float sentimentScore = entity.getProperty("sentimentScore") == null
                                ? (float) 0.0
                                : ((Double) entity.getProperty("sentimentScore")).floatValue();
        boolean isDirectMessage = (boolean) entity.getProperty("isDirectMessage");
        if (!loggedInUser.equals(recipientProperty) && !users.contains(recipientProperty) || loggedInUser.equals(recipientProperty) && !users.contains(user)) {
          if ((loggedInUser.equals(user) && loggedInUser.equals(recipientProperty)) || loggedInUser.equals(user)) {
            users.add(recipientProperty);
            text = "You: " + text;
          } else {
            users.add(user);
          }
          Message message = new Message(id, user, text, timestamp, recipientProperty, sentimentScore, imageUrl, isDirectMessage);
          recentChats.add(message);
        }
      } catch(Exception e) {
        System.err.println("Error getting private messages.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    
    return recentChats;
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

    return getMessages_(results);
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
  private List<Message> getMessages_(PreparedQuery results) {
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
    System.out.println("enter storing user...");
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    userEntity.setProperty("name", user.getName());
    userEntity.setProperty("breed", user.getBreed());
    userEntity.setProperty("gender", user.getGender());
    userEntity.setProperty("birthday", user.getBirthday());
    userEntity.setProperty("weight", user.getWeight());
    userEntity.setProperty("imgUrl", user.getImgUrl());
    System.out.println("done till uploading images in user...");
    if (user.getAddress() != null && user.getAddress().size() == 3) {
      System.out.println("setting address...");
      System.out.println(user.getAddress().toString());
      userEntity.setProperty("city", user.getAddress().get(0));
      userEntity.setProperty("state", user.getAddress().get(1));
      userEntity.setProperty("zip", user.getAddress().get(2));
    } else {
      userEntity.setProperty("city", "");
      userEntity.setProperty("state", "");
      userEntity.setProperty("zip", "");
    }
    userEntity.setProperty("likes", user.getLikes());
    userEntity.setProperty("notLikes", user.getNotLikes());
    datastore.put(userEntity);
    System.out.println("done storing user...");
  }

  public int getTotalMessageCount() {
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withLimit(1000));
  }

  /**
   * Get markers that user adds
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
   * Store markers to datastore
   */
  public void storeMarker(UserMarker marker) {
    Entity markerEntity = new Entity("UserMarker");
    markerEntity.setProperty("lat", marker.getLat());
    markerEntity.setProperty("lng", marker.getLng());
    markerEntity.setProperty("content", marker.getContent());
    datastore.put(markerEntity);
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
   * @param userEntity
   * @return User from the entity. In case of error, returns null.
   */
  private User getUserFromEntity(Entity userEntity) {
    User user = null;
    try {
      String email = (String) userEntity.getProperty("email");
      HashSet<String> likes = new HashSet<String>();
      HashSet<String> notLikes = new HashSet<String>();
      if (userEntity.getProperty("likes") != null) {
        likes = new HashSet<String>((ArrayList<String>) userEntity.getProperty("likes"));
      }
      if (userEntity.getProperty("notLikes") != null) {
        notLikes = new HashSet<String>((ArrayList<String>) userEntity.getProperty("notLikes"));
      }
      String aboutMe = (String) userEntity.getProperty("aboutMe");
      String name = (String) userEntity.getProperty("name");
      String breed = (String) userEntity.getProperty("breed");
      String gender = (String) userEntity.getProperty("gender");
      String birthday = (String) userEntity.getProperty("birthday");
      String weight = (String) userEntity.getProperty("weight");
      String imgUrl = (String) userEntity.getProperty("imgUrl");
      ArrayList<String> address = new ArrayList<String>();
      address.add((String) userEntity.getProperty("city"));
      address.add((String) userEntity.getProperty("state"));
      address.add((String) userEntity.getProperty("zip"));

      user = new User(email, aboutMe, likes, notLikes, name, breed, gender, birthday, weight, address, imgUrl);
    } catch (Exception e) {
      System.err.println("Error reading user.");
      System.err.println(userEntity.toString());
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
    System.out.println("in not seen by user...");
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

  /** Returns empty chat room lists of a user */
  public ArrayList<String> openedChats(User user) {
    // System.out.println("Openedchats");
    user.addChats("uauaua");
    for(String s : user.getOngoing()) {
      System.out.println(s);
    }
    return user.getOngoing();
  }
}

