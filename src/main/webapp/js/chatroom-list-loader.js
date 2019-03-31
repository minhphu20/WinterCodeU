// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
  window.location.replace('/');
}

/** Fetches chatrooms and add them to the page. **/
function fetchChatrooms() {
  const url = '/chatroom-list?user=' + parameterUsername;
  console.log(parameterUsername)
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((messages) => {
        const chatroomContainer = document.getElementById('chatroom-container');
        if (messages.length == 0) {
          chatroomContainer.innerHTML = '<p>This user has no ongoing chats yet.</p>';
        } else {
          chatroomContainer.innerHTML = '';
        }

        messages.forEach((message) => {
          const chatroomDiv = buildChatroomDiv(message);
          chatroomContainer.appendChild(chatroomDiv);
        })
      });
}

/**
 * Builds an element that displays the message.
 * @param {Message} message
 * @return {Element}
 */
function buildChatroomDiv(message) {
  console.log(message.user);
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('chatroom-header');
  headerDiv.appendChild(document.createTextNode(
    message.user + ' - ' +
    new Date(message.timestamp)));

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('chatroom-body');
  bodyDiv.innerHTML = convertInput(message.text);

  const chatroomDiv = document.createElement('div');
  chatroomDiv.classList.add('chatroom-div');
  chatroomDiv.appendChild(headerDiv);
  chatroomDiv.appendChild(bodyDiv);

  return chatroomDiv;
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
  fetchChatrooms();
}