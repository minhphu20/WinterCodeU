const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

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
          console.log(chat);
          const chatDiv = buildChatDiv(chat);
          chatContainer.appendChild(chatDiv);
        })

        // Build empty chat lists for matching users who haven't started chatting yet
        const ongoingurl = '/ongoing?user=' + parameterUsername;
        console.log("outside ongingurl fetching");
        fetch(ongoingurl)
            .then((response) => {
              return response.json();
            })
            .then((users) => {
              users.forEach((user) => {
                console.log(user);
                const chatDiv = buildEmptyChatDiv(user);
                chatContainer.appendChild(chatDiv);
              })
            });
      });
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

/** Build an element that displays an empty chatroom */
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

  headerDiv.appendChild(document.createTextNode(user));
  chatDiv.setAttribute('onclick', "location.href='/chat.html?user=" + user + "'");

  return chatDiv;
}

/**
 * Builds an element that displays the chat.
 * @param {Chat} chat
 * @return {Element}
 */
function buildChatDiv(chat) {
  const headerDiv = document.createElement('div');
  headerDiv.classList.add('chat-header');

  const bodyDiv = document.createElement('div');
  bodyDiv.classList.add('chat-body');
  bodyDiv.innerHTML = convertInput(chat.text);

  const chatDiv = document.createElement('div');
  chatDiv.classList.add('chat-div');
  chatDiv.appendChild(headerDiv);
  chatDiv.appendChild(bodyDiv);

  fetch('/login-status')
    .then((response) => {
      return response.json();
    })
    .then((loginStatus) => {
      if (loginStatus.isLoggedIn) {
        let recipient = "";
        if (loginStatus.username === chat.user){
          recipient = chat.recipient;
        } else {
          recipient = chat.user;
        }
        headerDiv.appendChild(document.createTextNode(
          recipient + ' - ' +
          new Date(chat.timestamp)));
        chatDiv.setAttribute('onclick', "location.href='/chat.html?user=" + recipient + "'");
      }
    })
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
  checkLoggedIn();
  fetchChats();
}