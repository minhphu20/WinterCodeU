package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashSet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.codeu.data.User;

/** Handles fetching and saving likes. */
@WebServlet("/prospect")
public class ProspectServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }

    /**
     * Returns with a user who the logged in user has not seen.
     * Returns nothing if the logged in user has seen everyone.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserService userService = UserServiceFactory.getUserService();

        String userEmail = userService.getCurrentUser().getEmail();

        User user = datastore.getUser(userEmail);

        if (user == null) {
            System.out.println("The current user is not in Datastore. Adding them...");
            user = new User(userEmail, null, new HashSet<String>(), new HashSet<String>());
            datastore.storeUser(user);
        }

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
            response.getWriter().println("{}");
            System.out.println("You have seen everyone...");
        }
    }
}