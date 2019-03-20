/**
 * Put Ufo position on the map
 * Display the state once click on the marker
 */
function createUfoSightingsMap(){
    fetch('/ufo-data').then(function(response) {
      return response.json();
    }).then((ufoSightings) => {
        
      // Construct a map
      const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 35.78613674, lng: -119.4491591},
        zoom:7
      });

      var markers = [];

      ufoSightings.forEach((ufoSighting) => {
        // Construct a marker for each ufoSighting
        const marker = new google.maps.Marker({
          position: {lat: ufoSighting.lat, lng: ufoSighting.lng},
          map: map,
        });
        // Add it into the markers array
        markers.push(marker);

        // Add info windows
        var infoWindow = new google.maps.InfoWindow({
          content: ufoSighting.state
        })

        // Display infoWindow if clicks on the marker
        marker.addListener('click', function() {
          infoWindow.open(map, marker);
        });
      });

      // Add a marker clusterer to manage the markers.
      var markerCluster = new MarkerClusterer(map, markers,
        {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});
    });
}