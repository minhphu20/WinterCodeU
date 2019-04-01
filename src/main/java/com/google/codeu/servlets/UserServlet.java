package com.google.codeu.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import java.util.List;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.User;
import javax.servlet.ServletOutputStream;
import java.lang.Exception;

/**
 * Handles fetching and server-side rendering User Page
 * for any user.
 * @param "/user/*"
 */
@WebServlet("/user/*")
public class UserServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    String requestUrl = request.getRequestURI();
    String user = requestUrl.substring("/user/".length());
    request.setAttribute("user", user);

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      request.setAttribute("isLoggedIn", true);
      request.setAttribute("username", userService.getCurrentUser().getEmail());
    } else {
      request.setAttribute("isLoggedIn", false);
    }

    if (!(user == null || user.equals(""))) {
      List<Message> messages = datastore.getMessages(user);
      if (messages != null && messages.size() != 0) {
        Gson gson = new Gson();
        String json = gson.toJson(messages);
        request.setAttribute("messages", json);
      }
    }

    User userData = datastore.getUser(user);
    if (userData != null && userData.getAboutMe() != null) {
      request.setAttribute("aboutMe", userData.getAboutMe());
    }

    request.getRequestDispatcher("/WEB-INF/user.jsp").forward(request,response);
  }
}
