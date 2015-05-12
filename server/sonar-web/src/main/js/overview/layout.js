define([
  'templates/overview'
], function () {

  var $ = jQuery;

  return Marionette.Layout.extend({
    template: Templates['overview-layout']
  });

});
