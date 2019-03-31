/** Fetches chatrooms and add them to the page. **/
function fetchChatrooms() {
  const url = '/chatroom-list';
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((chatrooms) => {
        const chatroomContainer = document.getElementById('chatroom-container');
        if (chatrooms.length == 0) {
          chatroomContainer.innerHTML = '<p>This user has no ongoing chats yet.</p>';
        } else {
          chatroomContainer.innerHTML = '';
        }

        chatrooms.forEach((chatroom) => {
          const chatroomDiv = buildChatroomDiv(chatroom);
          chatroomContainer.appendChild(chatroomDiv);
        })
      });
}

/**
 * Builds an element that displays the message.
 * @param {Chatroom} message
 * @return {Element}
 */
function buildChatroomDiv(chatroom) {
  console.log("user" + chatroom.user);
  console.log("recipient" + chatroom.recipient);
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('chatroom-header');
  headerDiv.appendChild(document.createTextNode(
    chatroom.recipient + ' - ' +
    new Date(chatroom.timestamp)));

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('chatroom-body');
  bodyDiv.innerHTML = convertInput(chatroom.text);

  const chatroomDiv = document.createElement('div');
  chatroomDiv.classList.add('chatroom-div');
  chatroomDiv.appendChild(headerDiv);
  chatroomDiv.appendChild(bodyDiv);
  chatroomDiv.setAttribute('onclick', "location.href='/chatroom.html?user=" + chatroom.recipient + "'");

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