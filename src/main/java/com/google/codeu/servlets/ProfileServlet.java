package com.google.codeu.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesServiceFailureException;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Handles fetching and saving user data.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }
 
 /**
  * Responds with a JSON representation of {@link User} data for a specific user. Responds with
  * an empty array if the user is not provided.
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    UserService userService = UserServiceFactory.getUserService();
    String user = userService.getCurrentUser().getEmail();

    response.setContentType("application/json");
  
    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }
    
    User userData = datastore.getUser(user);
  
    if (userData == null) {
      ArrayList<String> address = new ArrayList<String>();
      address.add("");
      address.add("");
      address.add("");
      userData = new User(user, "", "", "", "", "", "", address, "../images/cooldoge.png");
      datastore.storeUser(userData);
    }

    Gson gson = new Gson();
    String json = gson.toJson(userData);
    response.getWriter().println(json);
  }
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();  
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.relaxed());
    String name = Jsoup.clean(request.getParameter("name"), Whitelist.relaxed());
    String breed = Jsoup.clean(request.getParameter("breed"), Whitelist.relaxed());
    String gender = Jsoup.clean(request.getParameter("gender"), Whitelist.relaxed());
    String birthday = Jsoup.clean(request.getParameter("birthday"), Whitelist.relaxed());
    String weight = Jsoup.clean(request.getParameter("weight"), Whitelist.relaxed());
    ArrayList<String> address = new ArrayList<String>();
    address.add(request.getParameter("city"));
    address.add(request.getParameter("state"));
    address.add(request.getParameter("zip"));

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    String imageUrl = "";
    HashSet<String> likes = new HashSet<String>();
    HashSet<String> notLikes = new HashSet<String>();
    User user = datastore.getUser(userEmail);
    if (user != null) {
      imageUrl = user.getImgUrl();
      likes = user.getLikes();
      notLikes = user.getNotLikes();
    }

    if (blobKeys != null && !blobKeys.isEmpty()) {
      BlobKey blobKey = blobKeys.get(0);
      ImagesService imagesService = ImagesServiceFactory.getImagesService();
      ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
      try {
        imageUrl = imagesService.getServingUrl(options);
        System.out.println(imageUrl);
      } catch (ImagesServiceFailureException unused) {

      }
    }

    user = new User(userEmail, aboutMe, likes, notLikes, name, breed, gender, birthday, weight, address, imageUrl);
    datastore.storeUser(user);
  
    response.sendRedirect("/user-profile.html");
  }
}