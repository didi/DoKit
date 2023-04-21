// LiveReload client script
(function() {
  var uri, host, protocol, base;
  uri      = document.location.href;
  protocol = document.location.protocol;
  host     = document.location.host;

  var WebSocket = window.WebSocket || window.MozWebSocket;

  // Create WebSocket
  var socket = new WebSocket('ws' + (protocol == 'https:' ? 's' : '') + '://' + document.location.hostname + ':35729' + '/livereload');

  socket.onopen = function(evt) {
    // socket.send('hello from client');
  };

  socket.onerror = function(error) {
    console.log('WebSocket error', error);
  };

  socket.onmessage = function(evt) {
      document.location.reload();
  };
})();
