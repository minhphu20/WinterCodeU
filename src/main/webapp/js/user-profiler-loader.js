/** Fetches chats and add them to the page. **/
function fetchProfile() {
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

/**
 * Builds an element that displays the chat.
 * @param {Chat} chat
 * @return {Element}
 */
function populateForm{
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

/** Fetches data and populates the UI of the page. */
function buildUI() {
  checkLoggedIn();
  fetchProfile();
}