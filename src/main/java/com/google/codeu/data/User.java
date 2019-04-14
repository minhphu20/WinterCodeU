package com.google.codeu.data;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

    private String email;
    private String aboutMe;
    private HashSet<String> likes;
    private HashSet<String> notLikes;

    public User(String email, String aboutMe, HashSet<String> likes, HashSet<String> notLikes) {
        this.email = email;
        this.aboutMe = aboutMe;
        this.likes = likes;
        this.notLikes = notLikes;
    }

    public String getEmail() {
        return email;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public HashSet<String> getLikes() {
        return likes;
    }

    public HashSet<String> getNotLikes() {
        return notLikes;
    }

    public void setAboutMe(String about) {
        this.aboutMe = about;
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

    //TODO: add check such as can't add a user into both likes and notLikes
}