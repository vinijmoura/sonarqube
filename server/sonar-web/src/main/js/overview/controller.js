define([
  './layout'
], function (Layout) {

  return Marionette.Controller.extend({

    initialize: function () {
      this.app = this.options.app;
    },

    index: function (id) {
      this.app.state = new Backbone.Model({ id: id });
      this.app.layout = new Layout({
        el: this.options.el,
        model: this.app.state
      });
      this.app.layout.render();
    }

  });

});
