      let map;
      function createMap(){
          map = new google.maps.Map(document.getElementById('map'), {
            center: {lat: 37.422, lng: -122.084},
            zoom: 16
          });

          // Add a marker at Googleplex
          const trexMarker = new google.maps.Marker({
            position: {lat: 37.421903, lng: -122.084674},
            map: map,
            title: 'Stan the T-Rex',
          });
          var trexInfoWindow = new google.maps.InfoWindow({
              content: 'This is Stan, the T-Rex statue.'
          })
          trexMarker.addListener('click', function() {
            trexInfoWindow.open(map, trexMarker);
          });

          // Add a marker for the dog park
          var image = 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png';
          const parkMarker = new google.maps.Marker({
              position: {lat: 37.428352, lng: -122.077574},
              map: map,
              title: 'Dog Park',
              icon: image
          })
          var parkInfoWindow = new google.maps.InfoWindow({
              content: 'This is a park for dogs.'
          })
          parkMarker.addListener('click', function() {
            parkInfoWindow.open(map, parkMarker);
          });
      }