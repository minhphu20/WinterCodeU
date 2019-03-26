/** 
 * Styles the message input div to include ability
 * to style text with B, I, U, etc. 
 */
function styleUI() {
  const config = {removePlugins: [ 'ImageUpload' ]};
  ClassicEditor.create(document.getElementById('message-input'), config );
}