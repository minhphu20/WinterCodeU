package com.google.codeu.data;

import java.util.ArrayList;
import java.util.HashSet;

public class User {

  private String email;
  private String name;
  private String breed;
  private String gender;
  private String birthday;
  private String aboutMe;
  private ArrayList<String> address;
  private String weight;
  private String imgUrl;
  private HashSet<String> likes;
  private HashSet<String> notLikes;
  private boolean hasUnRead;
  private boolean hasUnopenedCR;

  public User(String email, String aboutMe, HashSet<String> likes, HashSet<String> notLikes, String name, String breed, String gender, String birthday, String weight, ArrayList<String> address, String imgUrl) {
    this.email = email.trim();
    this.aboutMe = aboutMe;
    this.name = name;
    this.breed = breed;
    this.gender = gender;
    this.birthday = birthday;
    this.weight = weight;
    this.address = address;
    this.imgUrl = imgUrl;
    this.likes = likes;
    this.notLikes = notLikes;
  }

  public void setHasUnread(boolean hasUnRead) {
    this.hasUnRead = hasUnRead;
  }

  public void setHasUnopenedCR(boolean hasUnopenedCR) {
    this.hasUnopenedCR = hasUnopenedCR;
  }

  public String getEmail() {
    return email;
  }

  public String getAboutMe() {
    return aboutMe;
  }

  public String getName() {
    return name;
  }

  public String getBreed() {
    return breed;
  }

  public String getGender() {
    return gender;
  }

  public String getBirthday() {
    return birthday;
  }

  public String getWeight() {
    return weight;
  }

  public ArrayList<String> getAddress() {
    return address;
  }

  public String getImgUrl() {
    return imgUrl;
  }

  public void setImgUrl(String imgUrl) {
    this.imgUrl = imgUrl;
  }

  public HashSet<String> getLikes() {
    return likes;
  }

  public HashSet<String> getNotLikes() {
    return notLikes;
  }

  public void addLike(String user) {
    System.out.println("Inside adding likes..." + user);
    if (this.likes == null) {
      this.likes = new HashSet<String>();
    }
    this.likes.add(user);
    System.out.println("Finish inside adding likes");
  }

  public void addNotLike(String user) {
    if (this.notLikes == null) {
      this.notLikes = new HashSet<String>();
    }
    this.notLikes.add(user);
  }

  public void getHasUnread() {
    return hasUnRead;
  }

  public void getHasUnopenedCR() {
    return hasUnopenedCR;
  }

}