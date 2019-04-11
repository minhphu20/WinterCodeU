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