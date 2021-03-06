/** Redirect to homepage if the user is not logged in. */
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
                document.getElementById('prospect_email').innerText = user['email'];
                if (user['imgUrl']) {
                    document.getElementById('prospect_image').src = user['imgUrl'];
                } else {
                    // We have a very cute placeholder for the image if the user has not uploaded an image.
                    document.getElementById('prospect_image').src = "images/doge.jpg";
                }
                if (user['aboutMe']) {
                    document.getElementById('prospect_aboutMe').innerText = user['aboutMe'];
                } else {
                    document.getElementById('prospect_aboutMe').innerText = "I have not add an about me ;)";
                }

            } else {
                // TODO: add hidden class to properly hide things
                document.getElementById('prospect_email').innerText = "You have seen all users."
                document.getElementById('prospect_image').src = "images/profEample.jpg";
                document.getElementById('prospect_aboutMe').innerText = "";
                document.getElementById('buttons').innerText = "";
            }
        })
}

/** Posts like. */
function postLike() {
    console.log("liking...");
    let targetEmail = document.getElementById('prospect_email').innerHTML;
    fetch("/like?email=" + targetEmail + "&status=like", { method: "POST" })
            // Hey CINDY :) This is where you can start modifying
            // stuffs to make the chat happen!!
        .then((response) => {
            return response.text();
        })
        .then((answer) => {
            console.log("answer is: ", answer);
            if (answer === "yes") {
                // Hey Cindy, you can open chat page here.
                console.log("Chat page should open!");

                // A confirm window for opening the chatroom pops up
                var startChat = confirm("Start a chat with " + targetEmail)
                if(startChat === true) {
                    window.location.replace("chat.html?user=" + targetEmail);
                } else {
                    fetchProspect();
                }
            } else {
                console.log("No chat page!");
                fetchProspect();
            }
        })
}

/** Posts not like. */
function postNotLike() {
    console.log("not liking...");
    let targetEmail = document.getElementById('prospect_email').innerHTML;
    fetch("/like?email=" + targetEmail + "&status=notlike", { method: "POST" })
        .then((response) => {
            return response.text();
        })
        .then((answer) => {
            fetchProspect();
        })
}

/** Loads the image page with image fetched from /prospect. */
function loadProspect() {
    showMessageFormIfLoggedIn();
    fetchProspect();
}