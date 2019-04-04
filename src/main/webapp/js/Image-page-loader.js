// Get ?user=XYZ parameter value
const urlParams = new URLSearchParams(window.location.search);
const parameterUsername = urlParams.get('user');

// URL must include ?user=XYZ parameter. If not, redirect to homepage.
if (!parameterUsername) {
    window.location.replace('/');
}

/** Sets the user name based on the URL parameter username. */
function setUserName() {
    let atIdx = parameterUsername.indexOf('@');
    const userName = parameterUsername.substring(0, atIdx);

    document.getElementById('username').innerText = "Chat: "  + userName;
    document.title = parameterUsername + ' Home Page';
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