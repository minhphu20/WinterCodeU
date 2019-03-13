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
 * Draw the default chart.
 */
function drawDefaultChart(){
  // Create a DataTable to store data
  var book_data = new google.visualization.DataTable();
  book_data.addColumn('string', 'Book Title');
  book_data.addColumn('number', 'Votes');
  book_data.addRows([
    ["The Best We Could Do", 6],
    ["Sing, Unburied, Sing", 10],
    ["The Book of Unknown Americans", 7],
    ["The 57 Bus", 4],
    ["The Handmaid's Tale", 8]
  ]);
  // Create a bar chart
  var chart = new google.visualization.BarChart(document.getElementById('book_chart'));
  var chart_options = {title: "GREAT BOOKS", width: 800, height: 400};
  chart.draw(book_data, chart_options);
}

/**
 * Build the charts.
 */
function buildChart() {
  google.charts.load('current', {
    'packages': ['corechart', 'geochart'],
    // Note: you will need to get a mapsApiKey for your project.
    // See: https://developers.google.com/chart/interactive/docs/basic_load_libs#load-settings
    'mapsApiKey': 'AIzaSyD-9tSrke72PouQMnMX-a7eZSW0jkFMBWY'});
  google.charts.setOnLoadCallback(drawDefaultChart);
  google.charts.setOnLoadCallback(drawRegionChart);
}