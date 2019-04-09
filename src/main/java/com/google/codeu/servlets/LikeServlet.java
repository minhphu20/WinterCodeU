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
   * Responds with the emails of users who the logged in user likes.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      System.out.println("The current user is not logged in. Redirecting to main page...");
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();

    User user = datastore.getUser(userEmail);
    if (user == null) {
      System.out.println("The current user is not in Datastore. Adding them...");
      user = new User(userEmail, null, new HashSet<String>(), new HashSet<String>());
      datastore.storeUser(user);
    }

    HashSet<String> likes = user.getLikes();

    Gson gson = new Gson();
    String json = gson.toJson(likes);

    response.setContentType("application/json");
    response.getWriter().println(json);

    ServletContext context = getServletContext( );
    context.log("Likes of current user: " + likes.toString());
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    System.out.println("Posting like in servlet...");

    UserService userService = UserServiceFactory.getUserService();

    if (!userService.isUserLoggedIn()) {
      System.out.println("The current user is not logged in. Redirecting to main page...");
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();

    User user = datastore.getUser(userEmail);
    // Note: sometimes the current user is not stored in the datastore!
    if (user == null) {
      System.out.println("The current user is not in Datastore. Adding them...");
      user = new User(userEmail, null, new HashSet<String>(), new HashSet<String>());
    }

    // Get who the user likes
    String prospectiveEmail = request.getParameter("email");
    User prospectiveMatch = datastore.getUser(prospectiveEmail);
    // Note: sometimes the prospective user is not stored in the datastore!
    if (prospectiveMatch == null) {
      System.out.println("The prospective user is not in Datastore. Adding them...");
      prospectiveMatch = new User(prospectiveEmail, null, new HashSet<String>(), new HashSet<String>());
    }

    user.addLike(prospectiveEmail);
    datastore.storeUser(user);

    System.out.println("Done adding like...");

    // after store like, should do check to see if the other person also like
    // if the other person also like, then allow chat
    // if not, display the next user
    // place holder: redirect to the user page
    if (datastore.isLiked(user, prospectiveMatch)) {
      System.out.println("It's a match! We will start chatting...");
      response.sendRedirect("/index.html");
    } else {
      HashSet<User> notSeen = datastore.notSeenBy(user);
      Object[] arrayItem = notSeen.toArray();
      if (notSeen != null && notSeen.size() > 0) {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(arrayItem[0]);
        response.getWriter().println(json);
        System.out.println("A new user who you have not seen is...");
        System.out.println(json);
      } else {
        System.out.println("There is noone else for you to swipe...");
      }
    }

  }
}