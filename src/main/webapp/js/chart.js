// TODO: add an about page to explain this feature

/**
 * Draw a map displaying number of pet dogs per country.
 * Data taken from http://www.irishdogs.ie/articles/countries-with-the-most-dogs-worldwide.html
 */
function drawRegionChart(){
  // Create a DataTable to store data
  var data = google.visualization.arrayToDataTable([
    ['Country', 'Pet Dogs Population [Million, 2012]'],
    ['United States', 75.8],
    ['Brazil', 35.7],
    ['China', 27.4],
    ['RU', 15.0],
    ['Japan', 12.0]
  ]);
  // Create a map chart
  var chart = new google.visualization.GeoChart(document.getElementById('region_chart'));
  var options = {title: "Top five countries that love dogs", width: 800, height: 400};
  chart.draw(data, options);
}

/**
 * Draw a generic bar chart from data.
 * @param {DataTable} data
 * @param {String} divName
 */
function drawBarChart(data, divName){
  var chart = new google.visualization.BarChart(document.getElementById(divName));
  var options = {width: 800, height: 400};
  chart.draw(data, options);
}

/**
 * Draw a bar chart of message counts per day.
 */
function drawMessageCountChart() {
  fetch("/messagechart")
    .then((response) => {
      return response.json();
    })
    .then((msgJson) => {
      var msgData = new google.visualization.DataTable();
      msgData.addColumn('date', 'Date');
      msgData.addColumn('number', 'Message Count');

      for (i = 0; i < msgJson.length; i++) {
        msgRow = [];
        var timestampAsDate = new Date (msgJson[i].timestamp);
        var totalMessages = i + 1;
        msgRow.push(timestampAsDate, totalMessages);
        msgData.addRow(msgRow);
      }
      drawBarChart(msgData, 'bar_chart');
    });
}

/**
 * Build the charts.
 */
function buildChart() {
  google.charts.load('current', {
    'packages': ['corechart', 'geochart', 'table'],
    // Note: you will need to get a mapsApiKey for your project.
    // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
    'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY'});
  google.charts.setOnLoadCallback(drawMessageCountChart);
  google.charts.setOnLoadCallback(drawRegionChart);
}