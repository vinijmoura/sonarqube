/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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

  var init = function (options) {
    // Controller
    this.controller = new Controller({ app: this, el: options.el });

    // Router
    this.router = new Router({ app: this });
    Backbone.history.start({
      pushState: true,
      root: getRoot()
    });
  };

  App.on('start', function (options) {
    window.requestMessages().done(function () {
      init.call(App, options);
    });
  });

  return App;

});
