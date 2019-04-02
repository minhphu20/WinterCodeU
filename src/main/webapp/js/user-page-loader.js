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

// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace('/');
}

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
  document.getElementById('page-title').innerText = parameterUsername;
  document.title = parameterUsername + ' - User Page';
}

/**
 * Shows the message form if the user is logged in and viewing their own page.
 */
function showMessageFormIfViewingSelf() {
  document.getElementById('about-me-form').classList.remove('hidden');

  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn &&
            loginStatus.username == parameterUsername) {
          const messageForm = document.getElementById('message-form');
          messageForm.action = '/messages?recipient=' + parameterUsername;
          messageForm.classList.remove('hidden');
        }
      });
}

/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = '/messages?user=' + parameterUsername;
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        console.log(messages);
        const messagesContainer = document.getElementById('message-container');
        if (messages.length == 0) {
          messagesContainer.innerHTML = '<p>This user has no posts yet.</p>';
        } else {
          messagesContainer.innerHTML = '';
        }
        messages.forEach(async (message) => {
          const messageDiv = await buildMessageDiv(message);
          messagesContainer.appendChild(messageDiv);
        });
      });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
async function buildMessageDiv(message) {
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('message-header');
  headerDiv.appendChild(document.createTextNode(
    message.user + ' - ' +
    new Date(message.timestamp) +
    ' [' + message.sentimentScore + ']'));

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('message-body');
  bodyDiv.innerHTML = convertInput(message.text);

  const messageDiv = document.createElement('div');
  messageDiv.classList.add('message-div');
  messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);
//  audio = await createAudioTag(message.text);
//  messageDiv.appendChild(audio);
  return messageDiv;
}

/**
  * Creates audio tag.
  */
async function createAudioTag(messageText) {
  // if (messageText === "") {
  //   console.log("empty messageText...")
  //   return;
  //   // Do nothing; consider showing a simple error to the user.
  // }

  // try {
  //   let resp = await fetch("/a11y/tts", {
  //     method: "POST",
  //     body: messageText,
  //     headers: {
  //       "Content-Type": "text/plain"
  //     },
  //   })
   
  //   // let audio = await resp.blob();

  //   var sound      = document.createElement('audio');
  //   sound.controls = 'controls';
  //   sound.src      = 'audio/output.mp3';
  //   sound.type     = 'audio/mpeg';
  //   console.log("got sound...")
  //   return sound;    
   
  // } catch (err) {
  //   console.log("error...")
  //   throw new Error(`Unable to call the Text to Speech API: {err}`)
  // }

  var sound      = document.createElement('audio');
  sound.controls = 'controls';
  sound.src      = 'audio/output.mp3';
  sound.type     = 'audio/mpeg';
  return sound; 
}

/**
  * Fetches about me data from user's input and adds it to the page. 
  */
function fetchAboutMe() {
  const url = '/about?user=' + parameterUsername;
  fetch(url)
      .then((response) => {
        return response.text();
      })
      .then((aboutMe) => {
        const aboutMeContainer = document.getElementById('about-me-container');
        if (aboutMe == '') {
          aboutMe = 'This user has not entered any information yet.';
        }

        aboutMeContainer.innerHTML = convertInput(aboutMe);
      });
}

/**
 * Converts user input with showdown markdown library.
 * @param {String} input
 * @return {Element}
 */
function convertInput(input) {
  let converter = new showdown.Converter(),
  html = converter.makeHtml(input);
  return html
}

async function sendAMessage(){
   try {
     let resp = await fetch("/a11y/tts", {
       method: "POST",
       body: "Hello you are pretty!",
       headers: {
         "Content-Type": "text/plain"
       },
     })

     let audio = await resp.blob();

     var objectURL = URL.createObjectURL(audio);
     var sound      = document.createElement('audio');
     sound.controls = 'controls';
     sound.src      = objectURL;
     sound.type     = 'audio/mpeg';

     const messagesContainer = document.getElementById('audio-source');

     messagesContainer.appendChild(sound);


   } catch (err) {
     console.log("error...")
     throw new Error(`Unable to call the Text to Speech API: {err}`)
   }
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  showMessageFormIfViewingSelf();
  fetchMessages();
  fetchAboutMe();
  const config = {removePlugins: [ 'ImageUpload' ]};
  ClassicEditor.create(document.getElementById('message-input'), config );
  sendAMessage();
}
