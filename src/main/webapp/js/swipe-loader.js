/**
 * Redirect to homepage if the user is not logged in.
 */
function showMessageFormIfLoggedIn() {
    fetch('/login-status')
    .then((response) => {
        return response.json();
    })
    .then((loginStatus) => {
        if (!loginStatus.isLoggedIn) {
            window.location.replace('/');
        }
    });
}

showMessageFormIfLoggedIn();