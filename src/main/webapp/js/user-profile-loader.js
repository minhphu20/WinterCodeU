/** Fetches user information and populates the form. **/
function fetchProfile() {
  const url = '/profile';
  fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((user) => {
        document.getElementById('name').value = user.name;
        document.getElementById('breed').value = user.breed;
        document.getElementById('bday').value = user.birthday;
        document.getElementById('city').value = user.address[0];
        document.getElementById('state').value = user.address[1];
        document.getElementById('zip').value = user.address[2];
        document.getElementById('weight').value = user.weight;
        document.getElementById('about-me').value = user.aboutMe;
        document.getElementById('gender').value = user.gender;
        document.getElementById('dog-name').innerHTML = user.name;
        document.getElementById('profile-img').src = user.imgUrl;
        console.log(user);
      });
}

/**
 * Fetches the login status of the user first.
 * When the image upload URL returns, it sets the action
 * attribute of the form and shows it.
 * @return {[type]} [description]
 */
function fetchImageUploadUrl() {
  fetch('/profile-image-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const profileForm = document.getElementById('profile-form');
        profileForm.action = imageUploadUrl;
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
      } else {
        fetchImageUploadUrl();
      }
    })
}

/** Fetches data and populates the UI of the page. */
function buildUI() {
  checkLoggedIn();
  fetchProfile();
}