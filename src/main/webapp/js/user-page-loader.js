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
 * Shows the message form if the user is logged in.
 */
function showMessageFormIfLoggedIn() {
  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn) {
          fetchImageUploadUrlAndShowForm();
        }
      });
}

function fetchOngoing() {
  const url = '/ongoing?user=' + parameterUsername;
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((ongoingChats) => {
        
        if(!ongoingChats.length == 0) {
          ongoingChats.forEach((ongoing) => {
            alert("You have a new opened chat!");
          });        
        }
        else {
          console.log("there is no opened chat");
        }
      });
}

function buildEmptyChatDiv(user) {
  // console.log("Inside empty div");
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('chat-header');

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('chat-body');
  bodyDiv.innerHTML = '<p>Start chating now!</p>';

  const chatDiv = document.createElement('div');
  chatDiv.classList.add('chat-div');
  chatDiv.appendChild(headerDiv);
  chatDiv.appendChild(bodyDiv);

  // headerDiv.appendChild(document.createTextNode('target email'));
  headerDiv.appendChild(document.createTextNode(user));

  return chatDiv;
}

/** Fetches messages and add them to the page. */
function fetchMessages() {
  const url = '/messages?user=' + parameterUsername;
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        const messagesContainer = document.getElementById('message-container');
        if (messages.length == 0) {
          messagesContainer.innerHTML = '<p>This user has no posts yet.</p>';
        } else {
          messagesContainer.innerHTML = '';
        }
        messages.forEach((message) => {
          const messageDiv = buildMessageDiv(message);
          messagesContainer.appendChild(messageDiv);
        });
      });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildMessageDiv(message) {
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

  if (message.imageUrl) {
    bodyDiv.innerHTML += '<br/>';
    bodyDiv.innerHTML += '<img src="' + message.imageUrl + '" />';
  }

  if(message.imageLabels) {
    bodyDiv.innerHTML += '<br/>';
    bodyDiv.innerHTML += message.imageLabels;
  }

  return messageDiv;
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

/**
 * Fetches the login status of the user first.
 * When the image upload URL returns, it sets the action
 * attribute of the form and shows it.
 * @return {[type]} [description]
 */
function fetchImageUploadUrlAndShowForm() {
  fetch('/image-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('message-form');
        messageForm.action = imageUploadUrl;
        messageForm.classList.remove('hidden');
        document.getElementById('recipientInput').value = parameterUsername;
        const chat = document.getElementById('chat');
        chat.setAttribute("href", "/chat.html?user="+parameterUsername);
        chat.classList.remove('hidden');
      });
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  setPageTitle();
  showMessageFormIfLoggedIn();
  fetchOngoing();
  fetchMessages();
  const config = {removePlugins: [ 'ImageUpload' ]};
  ClassicEditor.create(document.getElementById('message-input'), config);
}
