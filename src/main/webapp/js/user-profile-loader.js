/** Fetches user information and populates the form. **/
function fetchProfile() {
  const url = '/profile';
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((user) => {
        const profileForm = document.getElementById('profile-form');
        /* if (user.length == 0) {
          chatContainer.innerHTML = '<p>This user has no ongoing chats yet.</p>';
        } else {
          chatContainer.innerHTML = '';
        } */
        console.log(user);
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

/** Fetches data and populates the UI of the page. */
function buildUI() {
  checkLoggedIn();
  fetchProfile();
}