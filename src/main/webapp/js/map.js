let map;

/**
 * Build the map
 */
function createMap(){
    // Locate the user to its current location
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(function(position) {
        var pos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };

        map = new google.maps.Map(document.getElementById('map'), {
          center: {lat: pos.lat, lng: pos.lng},
          zoom: 16
        });

        addMarker('location', pos.lat, pos.lng, null, 'I\'m here', map);

        map.setCenter(pos);
      }, function() {
        handleLocationError(true, infoWindow, map.getCenter());
      });
    } else {

      map = new google.maps.Map(document.getElementById('map'), {
        center: {lat: 37.422, lng: -122.084},
        zoom: 16
      });

      // Browser doesn't support Geolocation
      handleLocationError(false, infoWindow, map.getCenter());
    }

    // Add a marker at Googleplex
    addMarker('Stan the T-Rex', 37.421903, -122.084674, null, 'This is Stan, the T-Rex statue.', map);

    // Add a marker for the dog park
    var image = 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png';
    addMarker('Stan the T-Rex', 37.428352, -122.077574, image, 'This is a park for dogs.', map);
}

/**
 * Add markers to the map
 * @param {*} title title of the marker
 * @param {*} lat latitude of th marker
 * @param {*} lng longitude of the marker
 * @param {*} image image of the marker, null for default image
 * @param {*} content content of the slide info window
 * @param {*} map map
 */
function addMarker(title, lat, lng, image, content, map) {
  const marker = new google.maps.Marker({
    position: {lat: lat, lng: lng},
    map: map,
    title: title,
    icon: image
  });
  var infoWindow = new google.maps.InfoWindow({
      content: content
  })
  marker.addListener('click', function() {
    infoWindow.open(map, marker);
  });
}