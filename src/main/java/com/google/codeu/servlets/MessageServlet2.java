package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Message;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import java.util.stream.Collectors;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/a11y/tts")
public class MessageServlet2 extends HttpServlet {
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
    // To get clean messages, removed dangerous things, html
    String userMessage = Jsoup.clean(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Whitelist.none());

     try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
       // Set the text input to be synthesized
       SynthesisInput input = SynthesisInput.newBuilder()
                                            .setText(userMessage)
                                            .build();

       // Build the voice request, select the language code ("en-US") and the ssml voice gender
       // ("neutral")
       VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
          .setLanguageCode("en-US")
          .setSsmlGender(SsmlVoiceGender.NEUTRAL)
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
       response.getOutputStream().write(audioContents.toByteArray());
     }
  }
}