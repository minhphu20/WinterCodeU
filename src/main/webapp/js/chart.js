// TODO: add a new chart based on our team's theme
// TODO: add an about page to explain this feature

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
  // Options to tyle the chart
  var chart_options = {width: 800, height: 400, title: "GREAT BOOKS"};
  chart.draw(book_data, chart_options);
}

/**
 * Build the chart.
 */
function buildChart() {
  google.charts.load('current', {packages: ['corechart']});
  google.charts.setOnLoadCallback(drawDefaultChart);
}