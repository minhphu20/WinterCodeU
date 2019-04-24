package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.codeu.data.User;

/** Handles fetching and saving likes and not likes. */
@WebServlet("/like")
public class LikeServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("Posting like in servlet...");

        UserService userService = UserServiceFactory.getUserService();

        String userEmail = userService.getCurrentUser().getEmail();

        User user = datastore.getUser(userEmail);
        // Note: sometimes the current user is not stored in the datastore!
        if (user == null) {
            System.out.println("The current user is not in Datastore. Adding them...");
            user = new User(userEmail, null, new HashSet<String>(), new HashSet<String>(), "", "", "", "", "", new ArrayList<String>() , "");
        }

        // Get who the user likes
        String prospectiveEmail = request.getParameter("email");
        System.out.println("prospective email: " + prospectiveEmail);
        User prospectiveMatch = datastore.getUser(prospectiveEmail);
        // Note: sometimes the prospective user is not stored in the datastore!
        if (prospectiveMatch == null) {
            System.out.println("The prospective user is not in Datastore. Adding them...");
            prospectiveMatch = new User(prospectiveEmail, null, new HashSet<String>(), new HashSet<String>(), "", "", "", "", "", new ArrayList<String>() , "");
        }

        String status = request.getParameter("status");
        System.out.println("status: " + status);
        if ("like".equals(status)) {
            user.addLike(prospectiveEmail);
            prospectiveMatch.addChats(userEmail);
            System.out.println("Done adding like...");
        } else {
            user.addNotLike(prospectiveEmail);
            System.out.println("Done adding not like...");
        }

        datastore.storeUser(user);

        // Hey CINDY :) This is where you can start modifying
        // stuffs to make the chat happen!!

        // yes means 2 users like each other, no means 1 of them does not like the other.
        if (("like".equals(status)) && (datastore.isLiked(user, prospectiveMatch))) {
            System.out.println("It's a match! We will start chatting...");
            // Add current user to prospectiveMatch's ongoing chats

            System.out.println("Add chats " + prospectiveMatch + " " + user);
            prospectiveMatch.addChats(userEmail);
            user.addChats(prospectiveEmail);

            datastore.storeUser(prospectiveMatch);
            datastore.storeUser(user);

            System.out.println("Add " + userEmail + " to " + prospectiveMatch + prospectiveMatch.getName());

            response.setContentType("text/html");
            response.getWriter().print("yes");
            
        } else {
            response.setContentType("text/html");
            response.getWriter().print("no");
        }
    }
}
