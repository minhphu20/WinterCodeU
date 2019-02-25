package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.codeu.data.Datastore;
import com.google.gson.JsonObject;

/**
 * Handles fetching site statistics.
 */
@WebServlet("/stats")
public class StatsPageServlet extends HttpServlet {

  private Datastore datastore;

  /**
   * Constructor
   */
  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * handles GET requests, requests that access an url
   * @param  request     servlet will “handle” the request
   * @param  response    by returning a response with the return value of doGet()
   * @throws IOException exception
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    int messageCount = datastore.getTotalMessageCount();
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("messageCount", messageCount);
    response.getOutputStream().println(jsonObject.toString());
  }
}
