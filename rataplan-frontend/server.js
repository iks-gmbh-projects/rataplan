// server.js
const express = require('express');
var process = require('process');
const app = express();
const path = require('path');

// Redirects all HTTP-Requests to HTTPS if Environment is Production (Heroku)
app.use(function (req, res, next) {
  if ((req.get('X-Forwarded-Proto') != 'https' && !req.secure) && app.get('env') === 'production') {
    res.redirect('https://' + req.get('host') + req.url);
  } else {
    next();
  }
});

// Run the app by serving the static files
// in the dist directory
app.use(express.static(__dirname + '/dist'));

// Start the app by listening on the default
// Heroku port
app.listen(process.env.PORT || 8080);


app.get('/environment', function (req, res) {
  res.json(process.env)
});

// For all GET requests, send back index.html
// so that PathLocationStrategy can be used
app.get('/*', function (req, res) {
  res.sendFile(path.join(__dirname + '/dist/index.html'));
});
