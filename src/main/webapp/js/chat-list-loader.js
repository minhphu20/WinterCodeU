/** Fetches chats and add them to the page. **/
function fetchChats() {
  const url = '/chat-list';
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((chats) => {
        const chatContainer = document.getElementById('chat-container');
        if (chats.length == 0) {
          chatContainer.innerHTML = '<p>This user has no ongoing chats yet.</p>';
        } else {
          chatContainer.innerHTML = '';
        }

        chats.forEach((chat) => {
          const chatDiv = buildChatDiv(chat);
          chatContainer.appendChild(chatDiv);
        })
      });
}

/**
 * Builds an element that displays the chat.
 * @param {Chat} chat
 * @return {Element}
 */
function buildChatDiv(chat) {
  console.log("user" + chat.user);
  console.log("recipient" + chat.recipient);
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('chat-header');
  headerDiv.appendChild(document.createTextNode(
    chat.recipient + ' - ' +
    new Date(chat.timestamp)));

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('chat-body');
  bodyDiv.innerHTML = convertInput(chat.text);

  const chatDiv = document.createElement('div');
  chatDiv.classList.add('chat-div');
  chatDiv.appendChild(headerDiv);
  chatDiv.appendChild(bodyDiv);
  chatDiv.setAttribute('onclick', "location.href='/chat.html?user=" + chat.recipient + "'");

  return chatDiv;
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
  fetchChats();
}