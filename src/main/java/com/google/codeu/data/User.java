package com.google.codeu.data;

public class User {

  private String email;
  private String name;
  private String breed;
  private String gender;
  private String birthday;
  private String aboutMe;
  private String weight;

  public User(String email, String aboutMe, String name, String breed, String gender, String birthday, String weight) {
    this.email = email.trim();
    this.aboutMe = aboutMe;
    this.name = name;
    this.breed = breed;
    this.gender = gender;
    this.birthday = birthday;
    this.weight = weight;
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
}