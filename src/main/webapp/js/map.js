      let map;

      /**
       * Build the map
       */
      function createMap(){
          map = new google.maps.Map(document.getElementById('map'), {
            center: {lat: 37.422, lng: -122.084},
            zoom: 16
          });

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