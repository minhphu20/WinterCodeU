/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Adds all links to the page, with some links depending on whether the user is
 * already logged in.
 */
function addNavigation() {
  const navigationElement = document.getElementById('navigation');
  if (!navigationElement) {
    console.warn('Navigation element not found!');
    return;
  }

  navigationElement.appendChild(
    createListItem(createLink('/', 'Home')));

  navigationElement.appendChild(
    createListItem(createLink('/stats.html', 'Stats')));

  navigationElement.appendChild(
    createListItem(createLink('/map.html', 'Map')));

  navigationElement.appendChild(
    createListItem(createLink('/feed.html', 'Feed')));

  navigationElement.appendChild(
    createListItem(createLink('/chart.html', 'Chart')));
  
  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn) {

          navigationElement.appendChild(
            createListItem(createLink('/user-page.html?user=' + loginStatus.username, 'Your Page')));

          navigationElement.appendChild(
            createListItem(createLink('/image-page.html', 'Swipe')));

          navigationElement.appendChild(
            createListItem(createLink('/chat-list.html', 'Your Chats')));

          navigationElement.appendChild(
              createListItem(createLink('/logout', 'Logout')));

        } else {
          navigationElement.appendChild(
              createListItem(createLink('/login', 'Login')));
        }
      });
}

/**
 * Creates an li element.
 * @param {Element} childElement
 * @return {Element} li element
 */
function createListItem(childElement) {
  const listItemElement = document.createElement('li');
  listItemElement.appendChild(childElement);
  return listItemElement;
}

/**
 * Creates an anchor element.
 * @param {string} url
 * @param {string} text
 * @return {Element} Anchor element
 */
function createLink(url, text) {
  const linkElement = document.createElement('a');
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}
