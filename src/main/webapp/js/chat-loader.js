// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace('/');
}

/** Sets the page title based on the URL parameter username. */
function setPageTitle() {
  document.getElementById('page-title').innerText = "Chat: "  + parameterUsername;
  document.title = parameterUsername + ' - Chat';
}

/** Check that the user is logged in. If not, redirect to homepage. */
function checkLoggedIn() {
  fetch('/login-status')
    .then((response) => {
      return response.json();
    })
    .then((loginStatus) => {
      if (!loginStatus.isLoggedIn) {
        window.location.replace('/');
      }
    })
}

/** Fetches messages and add them to the page. **/
function fetchMessages() {
  const url = '/chat?user=' + parameterUsername;
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
  console.log(message);
  const headerDiv = document.createElement('div');/* 
  headerDiv.classList.add('message-header');
  headerDiv.appendChild(document.createTextNode(
    message.user + ' - ' +
    new Date(message.timestamp) +
    ' [' + message.sentimentScore + ']')); */

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('message-body');
  bodyDiv.innerHTML = convertInput(message.text);

  const messageDiv = document.createElement('div');
  messageDiv.classList.add('message-div');
  fetch('/login-status')
    .then((response) => {
      return response.json();
    })
    .then((loginStatus) => {
      if (loginStatus.isLoggedIn) {
        if (loginStatus.username === message.user){
          messageDiv.classList.add('sender-message');
        } else {
          messageDiv.classList.add('recipient-message');
        }
      }
    })
  // messageDiv.appendChild(headerDiv);
  messageDiv.appendChild(bodyDiv);

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

/** Fetches data and populates the UI of the page. */
function buildUI() {
  checkLoggedIn();
  setPageTitle();
  fetchMessages();
  let box = document.getElementsByClassName('box')[0];
  console.log(box.scrollHeight);
  box.scrollTop = box.scrollHeight;
  // box.scrollTo(0, box.scrollHeight);
  const messageForm = document.getElementById('message-form');
  messageForm.action = '/chat?recipient=' + parameterUsername;
  const config = {removePlugins: [ 'ImageUpload' ]};
  ClassicEditor.create(document.getElementById('message-input'), config);
}