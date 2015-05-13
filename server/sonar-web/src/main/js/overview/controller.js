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
  './layout',
  './component',
  './gate-view',
  './measures-view'
], function (Layout, Component, GateView, MeasuresView) {

  return Marionette.Controller.extend({

    initialize: function () {
      this.app = this.options.app;
    },

    index: function (id) {
      this.app.component = new Component({ id: id });
      this.app.layout = new Layout({
        el: this.options.el,
        model: this.app.component
      });
      this.app.layout.render();
      this.renderGate();
      this.renderMeasures();
      this.app.component.fetch();
    },

    renderGate: function () {
      this.app.layout.gateRegion.show(new GateView({ model: this.app.component }));
    },

    renderMeasures: function () {
      this.app.layout.measuresRegion.show(new MeasuresView({ model: this.app.component }));
    }

  });

});
