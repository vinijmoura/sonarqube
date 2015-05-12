define(function () {

  return Backbone.Router.extend({
    routes: {
      '?id=:id': 'toIndex',
      'index?id=:id': 'index'
    },

    initialize: function (options) {
      this.app = options.app;
    },

    toIndex: function (id) {
      this.navigate('index?id=' + encodeURIComponent(id), { replace: true });
    },

    index: function (id) {
      this.app.controller.index(id);
    }
  });

});
