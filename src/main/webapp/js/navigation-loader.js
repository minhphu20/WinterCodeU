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
    createListItem(createLink('/', 'Home', 'home-link')));

  navigationElement.appendChild(
    createListItem(createLink('/stats.html', 'Stats', 'stats-link')));

  navigationElement.appendChild(
    createListItem(createLink('/map.html', 'Map', 'map-link')));

  navigationElement.appendChild(
    createListItem(createLink('/feed.html', 'Feed', 'feed-link')));

  navigationElement.appendChild(
    createListItem(createLink('/chart.html', 'Chart', 'chart-link')));

  fetch('/login-status')
      .then((response) => {
        return response.json();
      })
      .then((loginStatus) => {
        if (loginStatus.isLoggedIn) {

          navigationElement.appendChild(
              createListItem(createLink('/user-profile.html', 'Profile', 'user-profile-link')));
          
          if (loginStatus.filledForm) {
            navigationElement.appendChild(
              createListItem(createLink('/chat-list.html', 'Chats', 'chat-list-link')));
            
            navigationElement.appendChild(
              createListItem(createLink('/user-page.html?user=' + loginStatus.username, 'Page', 'user-page-link')));
    
            navigationElement.appendChild(
                createListItem(createLink('/image-page.html', 'Swipe', 'swipe-link')));
          }

          navigationElement.appendChild(
              createListItem(createLink('/logout', 'Logout', 'logout-link')));

        } else {
          navigationElement.appendChild(
              createListItem(createLink('/login', 'Login', 'login-link')));
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
 * @param {string} id
 * @return {Element} Anchor element
 */
function createLink(url, text, id) {
  const linkElement = document.createElement('a');
  linkElement.setAttribute("id", id);
  linkElement.appendChild(document.createTextNode(text));
  linkElement.href = url;
  return linkElement;
}
