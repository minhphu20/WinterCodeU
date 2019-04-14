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
    const url = '/prospect';
    fetch(url)
        .then((response) => {
            return response.json();
        })
        .then((user) => {
            if (user['email']) {
                document.getElementById('prospect_name').innerText = user['email'];
                // TODO: add image profile of user -> waiting on Jenny to make profile picture compulsory
                // Right now we have a very cute placeholder for the image
                document.getElementById('prospect_image').src = "images/doge.jpg";
                if (user['aboutMe']) {
                    document.getElementById('prospect_aboutMe').innerText = user['aboutMe'];
                } else {
                    document.getElementById('prospect_aboutMe').innerText = "I have not add an about me ;)";
                }

            } else {
                // TODO: add hidden class to properly hide things
                document.getElementById('prospect_name').innerText = "You have seen all users."
                document.getElementById('prospect_image').src = "images/profEample.jpg";
                document.getElementById('prospect_aboutMe').innerText = "";
                document.getElementById('buttons').innerText = "";
            }
        })
}

/** Load the image page with image fetched from /prospect. */
function loadProspect() {
    showMessageFormIfLoggedIn();
    fetchProspect();
}
