let map;
let editMarker;

/**
 * Creates a map
 */
function createMap(){
    map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 38.5949, lng: -94.8923},
    zoom: 4
    });

    map.addListener('click', (event) => {
        createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
    });
}

/**
 * Populates the content in an Info Window that we attach to our marker
 * @param {*} map map
 * @param {*} lat latitude
 * @param {*} lng longitude
 */
function createMarkerForEdit(lat, lng){
    if(editMarker){
        editMarker.setMap(null)
    }

    editMarker = new google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map
    });  
            
    const infoWindow = new google.maps.InfoWindow({
        content: buildInfoWindowInput(lat, lng)
    });
        
    google.maps.event.addListener(infoWindow, 'closeclick', () => {
        editMarker.setMap(null);
    });

    infoWindow.open(map, editMarker);
}

/**
 * Builds a div that contains a textarea and a button.
 * @param {*} map map
 * @param {*} lat latitude
 */
function buildInfoWindowInput(lat, lng){
    const textBox = document.createElement('textarea');
    const button = document.createElement('button');
    button.appendChild(document.createTextNode('Submit'));

    button.onclick = () => {
        createMarkerForDisplay(lat, lng, textBox.value);
        editMarker.setMap(null);
    }
       
    const containerDiv = document.createElement('div');
    containerDiv.appendChild(textBox);
    containerDiv.appendChild(document.createElement('br'));
    containerDiv.appendChild(button);
       
    return containerDiv;
  }

  /**
   * Adds the user's data to the map when they click the Submit button.
   * @param {*} lat latitude
   * @param {*} lng longitude
   * @param {*} content user input form the textbox
   */
  function createMarkerForDisplay(lat, lng, content){
    const marker = new google.maps.Marker({
      position: {lat: lat, lng: lng},
      map: map
    });
                
    var infoWindow = new google.maps.InfoWindow({
      content: content
    });

    marker.addListener('click', () => {
      infoWindow.open(map, marker);
    });
  }