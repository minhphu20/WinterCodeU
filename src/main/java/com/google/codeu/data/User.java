package com.google.codeu.data;

import java.util.ArrayList;

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

  public User(String email, String aboutMe, String name, String breed, String gender, String birthday, String weight, ArrayList<String> address, String imgUrl) {
    this.email = email.trim();
    this.aboutMe = aboutMe;
    this.name = name;
    this.breed = breed;
    this.gender = gender;
    this.birthday = birthday;
    this.weight = weight;
    this.address = address;
    this.imgUrl = imgUrl;
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
}