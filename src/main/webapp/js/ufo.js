/**
 * Put Ufo position on the map
 * Display the state once click on the marker
 */
function createUfoSightingsMap(){
    fetch('/ufo-data').then(function(response) {
      return response.json();
    }).then((ufoSightings) => {
        
      const map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 35.78613674, lng: -119.4491591},
        zoom:7
      });

      ufoSightings.forEach((ufoSighting) => {
        const marker = new google.maps.Marker({
          position: {lat: ufoSighting.lat, lng: ufoSighting.lng},
          map: map,
        });
        var infoWindow = new google.maps.InfoWindow({
          content: ufoSighting.state
        })
        marker.addListener('click', function() {
          infoWindow.open(map, marker);
        });
      });
    });
  }