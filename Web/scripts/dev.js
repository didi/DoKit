const execa = require('execa');
const handler = require('serve-handler');
const http = require('http');
const open = require('open');

run()
async function run() {
  startDev();
  startServer();
  open('http://localhost:3000/playground');
}

async function startDev() {
  execa(
    'rollup',
    [
      '-c',
      '-w'
    ],
    {
      stdio: 'inherit'
    }
  )
}

async function startServer() {
  const server = http.createServer((request, response) => {
    // You pass two more arguments for config and middleware
    // More details here: https://github.com/vercel/serve-handler#options
    return handler(request, response, {
      public: "./",
      rewrites: [
        { "source": "/index.html", "destination": "./__tests__/fixture" },
      ]
    });
  })

  server.listen(3000, () => {
    console.log('Running at http://localhost:3000');
  });
}
