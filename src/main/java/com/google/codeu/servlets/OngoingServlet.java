package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.Document.Type;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import com.google.codeu.data.User;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/ongoing")
public class OngoingServlet extends HttpServlet {

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

    // UserService userService = UserServiceFactory.getUserService();
    // String sender = userService.getCurrentUser().getEmail();

    response.setContentType("application/json");

    String user = request.getParameter("user");
    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    User userObject = datastore.getUser(user);
    if(userObject == null) {
        System.out.println("The user is not in the datastore");
    } else {
        System.out.println("Has user");
        System.out.println(userObject + userObject.getName());
    }
    
    ArrayList<String> ongoingChats = datastore.openedChats(userObject);
    // ArrayList<String> ongoingChats = userObject.getOngoing();

    if(ongoingChats == null || ongoingChats.size() == 0) {
      System.out.println("no ongoing " + userObject.getName());
      response.getWriter().println("[]");
      return;
    }

    System.out.println("Has ongoing " + userObject.getName());


    Gson gson = new Gson();
    String json = gson.toJson(ongoingChats);
    System.out.println("Has ongoing json" + json);
    response.getWriter().println(json);
  }
}