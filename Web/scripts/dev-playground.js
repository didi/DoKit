const serveHandler = require('serve-handler');
const open = require('open');
const http = require('http');
const path = require('path');
const livereload = require('livereload');
// Create LiveReload server
const liveReloadServer = livereload.createServer({ port: 35729});
// Watch the 'public' directory for changes
liveReloadServer.watch([path.join(process.cwd(), 'packages/web/dist'), path.join(process.cwd(), 'packages/web-independent/dist')]);

// Create HTTP server
const server = http.createServer((request, response) => {
  return serveHandler(request, response);
});

// Start HTTP server
server.listen(3000, () => {
  console.log('Server listening on port 3000');
  open('http://localhost:3000/playground');
});
