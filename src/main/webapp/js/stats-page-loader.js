/**
 * Fetch stats and displays them in the page.
 * @return {Element}
 */
function fetchStats() {
   const url = '/stats';
   fetch(url).then((response) => {
      return response.json();
   }).then((stats) => {
      const statsContainer = document.getElementById('stats-container');
      statsContainer.innerHTML = '';
      const messageCountElement = buildStatElement('Message count: ' + stats.messageCount);
      statsContainer.appendChild(messageCountElement);
   });
}

/**
 * Builds an element that displays the statistic.
 * @param {Statistic} statistic
 * @return {Element}
 */
function buildStatElement(statString) {
   const statElement = document.createElement('p');
   statElement.appendChild(document.createTextNode(statString));
   return statElement;
}

/**
 * Fetch data and populate the UI of the page.
 * @return {Element}
 */
function buildUI() {
   fetchStats();
}
