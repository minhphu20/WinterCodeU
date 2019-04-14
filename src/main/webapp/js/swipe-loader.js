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

/** Fetches a prospect and add their info (name, image, aboutMe) to the page. */
function fetchProspect() {
    const url = '/messages?user=' + 'test@example.com';
    fetch(url)
        .then((response) => {
            return response.json();
        })
        .then((user) => {
            if (user) {
                document.getElementById('prospect_name').innerText = "jalala@example.com";
                document.getElementById('prospect_image').src = "images/doge.jpg";
                document.getElementById('prospect_aboutMe').innerText = "I adore running outside.";
            } else {
                document.getElementById('prospect').innerText = "You have seen all our users.";
            }
        })
}

showMessageFormIfLoggedIn();
fetchProspect();