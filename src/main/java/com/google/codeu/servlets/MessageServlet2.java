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
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
// Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/a11y/tts")
public class MessageServlet2 extends HttpServlet {

  // private TextToSpeechClient textToSpeechClient;

  // @Override
  // public void init() {
  //   ttsClient = TextToSpeechClient.create();
  // }

    // try {
    //   quickstartSample = new QuickstartSample();
    //   quickstartSample.main();
    // } catch (Exception e) {
    //   System.out.println("An exception was thrown.");
    // }

  /** Responses with a bytestream from the Google Cloud
  /* Text-to-Speech API
  **/
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }
    // To get clean messsages, removed dangerous things, html
    String userMessage = Jsoup.clean(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Whitelist.none());

    // // m: do the api translation, with input set to userMessage
     try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
       // Set the text input to be synthesized
       SynthesisInput input = SynthesisInput.newBuilder()
                                            .setText(userMessage)
                                            .build();

       // Build the voice request, select the language code ("en-US") and the ssml voice gender
       // ("neutral")
       VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
          .setLanguageCode("en-US")
          // Try experimenting with the different voices
          .setSsmlGender(SsmlVoiceGender.NEUTRAL) // Try experimenting with t
          .build();

       // Select the type of audio file you want returned
       AudioConfig audioConfig = AudioConfig.newBuilder()
          .setAudioEncoding(AudioEncoding.MP3)
          .build();

       // Perform the text-to-speech request on the text input with the selected voice parameters and
       // audio file type
       SynthesizeSpeechResponse apiResponse = textToSpeechClient.synthesizeSpeech(input, voice,
          audioConfig);

       // Get the audio contents from the response
       ByteString audioContents = apiResponse.getAudioContent();

       response.setContentType("audio/mpeg");

       // How to send the response back?

       // Write the response to the output file.
       try (
          ServletOutputStream output = response.getOutputStream();
          InputStream input_ = getServletContext().getResourceAsStream(audioContents.toString()); // Placeholder!
       ){
         byte[] buffer = new byte[2048];
         int bytesRead;
         while ((bytesRead = input_.read(buffer)) != -1) {
           output.write(buffer, 0, bytesRead);
         }
       }
     }

    //
    // response.getOutputStream().println(userMessage);
//    response.setContentType("text/html");
//    response.getWriter().println("done!");


    // This basically read audioContents as a stream...
    // since you do not know how long the stream is, you just keep reading till you are done
    // try (
    //   ServletOutputStream output = response.getOutputStream();
    //   InputStream input = getServletContext().getResourceAsStream(audioContents); // Placeholder!
    // ) { 
    //   byte[] buffer = new byte[2048];
    //   int bytesRead;    
    //     while ((bytesRead = input.read(buffer)) != -1) {
    //       output.write(buffer, 0, bytesRead);
    //     }
    // }
  }
}