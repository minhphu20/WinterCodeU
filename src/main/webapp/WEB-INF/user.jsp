<%@ page import="java.lang.reflect.Type" %>
<%@ page import="com.google.gson.reflect.TypeToken" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.codeu.data.Message" %>

<!DOCTYPE html>
<%
String user = (String) request.getAttribute("user");
boolean isUserLoggedIn = (boolean) request.getAttribute("isLoggedIn");
if (user == null || user.equals("")) {
  response.sendRedirect("/index.html");
}
%>
<html>
  <head>
    <title><%= user %>- Page</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/user-page.css">
    <link rel="icon" href="/images/doge.jpg">
    <link href="https://fonts.googleapis.com/css?family=Crete+Round|Playfair+Display|Satisfy" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/showdown@1.9.0/dist/showdown.min.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
    <script src="/js/user-page-styler.js"></script>
    <script src="http://mvnrepository.com/artifact/com.google.code.gson/gson"></script>
  </head>
  <body onload="styleUI();">
    <div class="box">
      <nav>
        <ul id="navigation">
          <li><a href="/">Home</a></li>
          <li><a href="/stats.html">Stats</a></li>
          <li><a href="/map.html">Map</a></li>
          <li><a href="/feed.html">Feed</a></li>
          <li><a href="/chart.html">Chart</a></li>
        </ul>
      </nav>
      <h1 id="page-title" class="shadow"><%= user %></h1>

      <%
      if (isUserLoggedIn) {
        String aboutMe = (String) request.getAttribute("aboutMe");
        if (aboutMe == "" || aboutMe == null) {
          aboutMe = "This user has not entered any information yet.";
        }
      %>
      <div id="about-me-container"><%= aboutMe %></div>
      <% 
      } 
      %>

      <div id="about-me-form">
        <form action="/about" method="POST">
          <textarea name="about-me" placeholder="Who are you?" rows=4 required></textarea>
          <br/>
          <input type="submit" value="Submit" class="rounded">
        </form>
      </div>

      </br>

      <% 
      if (isUserLoggedIn && user.equals((String) request.getAttribute("username"))) { 
      %>
        <form id="message-form" action=<%= "/messages?recipient=" + request.getAttribute("user") %> method="POST">
          Enter a new message:
          <br/>
          <textarea name="text" id="message-input"></textarea>
          <br/>
          <input type="submit" value="Submit" class="rounded">
        </form>
        <hr/>
      <% 
      } 
      %>

      <div id="message-container">
        <% 
        String json = (String) request.getAttribute("messages");
        if (json != null) {
          Gson gson = new Gson();
          Type type = new TypeToken<List<Message>>(){}.getType();
          List<Message> messages = gson.fromJson(json, type);
          for (Message message : messages) {
        %>
            <div class="message-div">
              <div class="message-header">
                <p><%= message.getUser() %>-<%= new Date(message.getTimestamp()) %>-[<%= message.getSentimentScore() %>]</p>
              </div>
              <div class="message-body">
                <p><%= message.getText() %></p>
              </div>
            </div>             
        <% 
          }
        } else {
        %>
          <p>This user has no posts yet.</p>
        <% 
        } 
        %>
      </div>
      
    </div>
  </body>
</html>