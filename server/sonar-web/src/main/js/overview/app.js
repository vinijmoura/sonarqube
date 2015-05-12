define([
    './router',
    './controller'
], function (Router, Controller) {

  function getRoot () {
    var OVERVIEW = '/overview',
        path = window.location.pathname,
        pos = path.indexOf(OVERVIEW);
    return path.substr(0, pos + OVERVIEW.length);
  }

  var App = new Marionette.Application();

  App.on('start', function (options) {
    // Controller
    this.controller = new Controller({ app: this, el: options.el });

    // Router
    this.router = new Router({ app: this });
    Backbone.history.start({
      pushState: true,
      root: getRoot()
    });
  });

  return App;

});
