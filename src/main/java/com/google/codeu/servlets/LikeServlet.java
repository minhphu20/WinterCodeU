package com.google.codeu.servlets;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import com.google.appengine.api.images.ImagesServiceFailureException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.codeu.data.User;

/** Handles fetching and saving likes. */
@WebServlet("/like")
public class LikeServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    // TODO: check that email is there else will crash the server

    String email = request.getParameter("email");

    User user = datastore.getUser(email);
    ServletContext context = getServletContext( );
    context.log(user.getAboutMe());

    User a_new = new User("aladin.com", "I am Jasmine's fan", new HashSet<String>(), new HashSet<String>());
    a_new.addLike("rihana"); //TODO: check that can only add like of a real user
    datastore.storeUser(a_new);

    List<User> users = datastore.getAllUsers();
    context.log("Size of list: " + Integer.toString(users.size()));
    context.log("Likes of first users: " + users.get(2).getLikes());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("posting like in servlet...");
    // The user must log in to swipe
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }
    // Get who the user like
    String targetEmail = "haha";//request.getParameter("email");
    // Get the current user and add the target user
    if (userService.getCurrentUser() != null) {
      System.out.println("Current user is not null");
    }

    String userEmail = userService.getCurrentUser().getEmail();
    System.out.println("Email of current: " + userEmail);
    // TODO: sometimes the current user is not stored in the datastore!
    User user = datastore.getUser(userEmail);
    if (user == null) {
      System.out.println("user is null");
      user = new User(userEmail, null, new HashSet<String>(), new HashSet<String>());
    }
    System.out.println("User is: " + user.toString());
    // The user must be not null!
    user.addLike(targetEmail);
    System.out.println("done adding like");
    datastore.storeUser(user);
    System.out.println("done storing user");
    // after store like, should do check to see if the other person also like
    // if the other person also like, then allow chat
    // if not, display the next user
    // place holder: redirect to the user page
    response.sendRedirect("/user-page.html?user=" + userEmail);
  }
}