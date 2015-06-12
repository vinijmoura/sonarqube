define([
  './issue'
], function (Issue) {

  return Backbone.Collection.extend({
    model: Issue,

    url: function () {
      return baseUrl + '/api/issues/search';
    },

    parseIssues: function (r) {
      var find = function (source, key, keyField) {
        var searchDict = {};
        searchDict[keyField || 'key'] = key;
        return _.findWhere(source, searchDict) || key;
      };
      return r.issues.map(function (issue, index) {
        var rule = find(r.rules, issue.rule);
        _.extend(issue, { index: index });
        if (rule) {
          _.extend(issue, {
            ruleName: rule.name
          });
        }
        return issue;
      });
    },

    setIndex: function () {
      return this.forEach(function (issue, index) {
        return issue.set({ index: index });
      });
    },

    selectByKeys: function (keys) {
      var that = this;
      keys.forEach(function (key) {
        var issue = that.get(key);
        if (issue) {
          issue.set({ selected: true });
        }
      });
    }
  });

});
