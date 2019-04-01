package com.google.codeu.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.codeu.data.QuickstartSample;

/**
 * Runs QuickstartSample class to demonstrate the text-to-speech API
 */
@WebServlet("/quickstart")
public class QuickstartServlet extends HttpServlet {
  
  private QuickstartSample quickstartSample;

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
      quickstartSample = new QuickstartSample();
      quickstartSample.main();
    } catch (Exception e) {
      System.out.println("An exception was thrown.");
    }
    response.setContentType("application/json");

    response.getWriter().println("Have printed the file");
  }
 
}