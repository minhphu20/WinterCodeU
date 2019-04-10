package com.google.codeu.data;

public class User {

  private String email;
  private String aboutMe;

  public User(String email, String aboutMe) {
    this.email = email.trim();
    this.aboutMe = aboutMe;
  }

  public String getEmail() {
    return email;
  }

  public String getAboutMe() {
    return aboutMe;
  }
}