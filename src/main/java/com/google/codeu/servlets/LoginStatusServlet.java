/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.JsonObject;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Returns login data as JSON, e.g. {"isLoggedIn": true, "username": "alovelace@codeustudents.com", "filledForm" : true}
 */
@WebServlet("/login-status")
public class LoginStatusServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    JsonObject jsonObject = new JsonObject();

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      boolean filledForm = false;
      String user = userService.getCurrentUser().getEmail();
      User userData = datastore.getUser(user);
  
      if (userData != null) {
        filledForm = true;
      }

      jsonObject.addProperty("isLoggedIn", true);
      jsonObject.addProperty("username", user);
      jsonObject.addProperty("filledForm", filledForm);
    } else {
      jsonObject.addProperty("isLoggedIn", false);
    }

    response.setContentType("application/json");
    response.getWriter().println(jsonObject.toString());
  }
}
