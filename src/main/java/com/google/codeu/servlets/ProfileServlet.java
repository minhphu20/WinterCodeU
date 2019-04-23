package com.google.codeu.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesServiceFailureException;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.protobuf.ByteString;
import com.google.cloud.vision.v1.*;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Handles fetching and saving user data.
 */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }
 
 /**
  * Responds with a JSON representation of {@link User} data for a specific user. Responds with
  * an empty array if the user is not provided.
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
  
    UserService userService = UserServiceFactory.getUserService();
    String user = userService.getCurrentUser().getEmail();

    response.setContentType("application/json");
  
    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }
    
    User userData = datastore.getUser(user);
  
    if (userData == null) {
      ArrayList<String> address = new ArrayList<String>();
      address.add("");
      address.add("");
      address.add("");
      userData = new User(user, "", new HashSet<String>(), new HashSet<String>(), "", "", "", "", "", address, "../images/cooldoge.png", "");
      datastore.storeUser(userData);
    }

    Gson gson = new Gson();
    String json = gson.toJson(userData);
    response.getWriter().println(json);
  }
 
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();  
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String userEmail = userService.getCurrentUser().getEmail();
    String aboutMe = Jsoup.clean(request.getParameter("about-me"), Whitelist.relaxed());
    String name = Jsoup.clean(request.getParameter("name"), Whitelist.relaxed());
    String breed = Jsoup.clean(request.getParameter("breed"), Whitelist.relaxed());
    String gender = Jsoup.clean(request.getParameter("gender"), Whitelist.relaxed());
    String birthday = Jsoup.clean(request.getParameter("birthday"), Whitelist.relaxed());
    String weight = Jsoup.clean(request.getParameter("weight"), Whitelist.relaxed());
    ArrayList<String> address = new ArrayList<String>();
    address.add(request.getParameter("city"));
    address.add(request.getParameter("state"));
    address.add(request.getParameter("zip"));

    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get("image");

    String imageUrl = "";
    String imageLabels = "";
    HashSet<String> likes = new HashSet<String>();
    HashSet<String> notLikes = new HashSet<String>();
    User user = datastore.getUser(userEmail);
    if (user != null) {
      imageUrl = user.getImgUrl();
      imageLabels = user.getImageLabels();
      likes = user.getLikes();
      notLikes = user.getNotLikes();
    }

    if (blobKeys != null && !blobKeys.isEmpty()) {
      BlobKey blobKey = blobKeys.get(0);
      ImagesService imagesService = ImagesServiceFactory.getImagesService();
      ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
      try {
        imageUrl = imagesService.getServingUrl(options);
        System.out.println(imageUrl);
        byte[] blobBytes = getBlobBytes(blobstoreService, blobKey);
        imageLabels = getImageLabels(blobBytes);
      } catch (ImagesServiceFailureException unused) {

      }
    }

    user = new User(userEmail, aboutMe, likes, notLikes, name, breed, gender, birthday, weight, address, imageUrl, imageLabels);
    datastore.storeUser(user);
  
    response.sendRedirect("/user-profile.html");
  }

  private String getImageLabels(byte[] imgBytes) throws IOException {
    ByteString byteString = ByteString.copyFrom(imgBytes);
    Image image = Image.newBuilder().setContent(byteString).build();
  
    Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
    AnnotateImageRequest request =
        AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
    List<AnnotateImageRequest> requests = new ArrayList<>();
    requests.add(request);
  
    ImageAnnotatorClient client = ImageAnnotatorClient.create();
    BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
    client.close();
    List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
    AnnotateImageResponse imageResponse = imageResponses.get(0);
  
    if (imageResponse.hasError()) {
      System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
      return null;
    }
  
    String labelsString = imageResponse.getLabelAnnotationsList().stream()
        .map(EntityAnnotation::getDescription)
        .collect(Collectors.joining(", "));
  
    return labelsString;
  }
  
  private byte[] getBlobBytes(BlobstoreService blobstoreService, BlobKey blobKey)
      throws IOException {
  
    ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
  
    int fetchSize = BlobstoreService.MAX_BLOB_FETCH_SIZE;
  
    long currentByteIndex = 0;
    boolean continueReading = true;
    while (continueReading) {
      // end index is inclusive, so we have to subtract 1 to get fetchSize bytes
      byte[] b =
          blobstoreService.fetchData(blobKey, currentByteIndex, currentByteIndex + fetchSize - 1);
      outputBytes.write(b);
  
      // if we read fewer bytes than we requested, then we reached the end
      if (b.length < fetchSize) {
        continueReading = false;
      }
  
      currentByteIndex += fetchSize;
    }
  
    return outputBytes.toByteArray();
  }
}