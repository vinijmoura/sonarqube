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
define(function () {

  var $ = jQuery;

  return Backbone.Model.extend({

    fetch: function () {
      return $.when(this.fetchMeasures());
    },

    fetchMeasures: function () {
      var that = this,
          url = baseUrl + '/api/resources/index',
          data = {
            resource: this.id,
            metrics: 'quality_gate_details,ncloc,violations,new_violations,sqale_rating,sqale_index,new_technical_debt,coverage,new_coverage,duplicated_lines_density',
            includetrends: true
          };
      return $.get(url, data).done(function (r) {
        var gateDetailsMeasure = _.findWhere(r[0].msr, { key: 'quality_gate_details' });
        if (gateDetailsMeasure) {
          that.set({ gateDetails: JSON.parse(gateDetailsMeasure.data) });
        }

        var nclocMeasure = _.findWhere(r[0].msr, { key: 'ncloc' });
        if (nclocMeasure) {
          that.set({
            ncloc: nclocMeasure.val,
            nclocLeak: nclocMeasure.var3
          });
        }

        var issuesMeasure = _.findWhere(r[0].msr, { key: 'violations' });
        if (issuesMeasure) {
          that.set({
            issues: issuesMeasure.val,
            issuesLeak: issuesMeasure.var3
          });
        }

        var newIssuesMeasure = _.findWhere(r[0].msr, { key: 'new_violations' });
        if (newIssuesMeasure) {
          that.set({
            newIssuesLeak: newIssuesMeasure.var3
          });
        }

        var debtMeasure = _.findWhere(r[0].msr, { key: 'sqale_index' });
        if (debtMeasure) {
          that.set({
            debt: debtMeasure.val,
            debtLeak: debtMeasure.var3
          });
        }

        var ratingMeasure = _.findWhere(r[0].msr, { key: 'sqale_rating' });
        if (ratingMeasure) {
          that.set({
            rating: ratingMeasure.val
          });
        }

        var newDebtMeasure = _.findWhere(r[0].msr, { key: 'new_technical_debt' });
        if (newDebtMeasure) {
          that.set({
            newDebtLeak: newDebtMeasure.var3
          });
        }

        var coverageMeasure = _.findWhere(r[0].msr, { key: 'coverage' });
        if (coverageMeasure) {
          that.set({
            coverage: coverageMeasure.val,
            coverageLeak: coverageMeasure.var3
          });
        }

        var newCoverageMeasure = _.findWhere(r[0].msr, { key: 'new_coverage' });
        if (newCoverageMeasure) {
          that.set({
            newCoverageLeak: newCoverageMeasure.var3
          });
        }

        var duplicationsMeasure = _.findWhere(r[0].msr, { key: 'duplicated_lines_density' });
        if (duplicationsMeasure) {
          that.set({
            duplications: duplicationsMeasure.val,
            duplicationsLeak: duplicationsMeasure.var3
          });
        }
      });
    }

  });

});
