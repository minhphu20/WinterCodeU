let map;
let editMarker;

/**
 * Create a map
 */
function createMap(){
    map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 38.5949, lng: -94.8923},
    zoom: 4
    });

    map.addListener('click', (event) => {
    const clickLatLng = event.latLng;
    console.log(clickLatLng.lat() + ', ' + clickLatLng.lng());
    });

    map.addListener('click', (event) => {
        const clickLatLng = event.latLng
        createMarkerForEdit(clickLatLng.lat(), clickLatLng.lng());
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
        content: buildInfoWindowInput()
    });
        
    infoWindow.open(map, editMarker);

    google.maps.event.addListener(infoWindow, 'closeclick', () => {
        editMarker.setMap(null);
    });
}

/**
 * Builds a div that contains a textarea and a button.
 */
function buildInfoWindowInput(){
    const textBox = document.createElement('textarea');
    const button = document.createElement('button');
    button.appendChild(document.createTextNode('Submit'));
       
    const containerDiv = document.createElement('div');
    containerDiv.appendChild(textBox);
    containerDiv.appendChild(document.createElement('br'));
    containerDiv.appendChild(button);
       
    return containerDiv;
  }