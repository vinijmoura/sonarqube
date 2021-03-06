define([
  'components/common/action-options-view',
  '../templates'
], function (ActionOptionsView) {

  var $ = jQuery;

  return ActionOptionsView.extend({
    template: Templates['issue-assign-form'],
    optionTemplate: Templates['issue-assign-form-option'],

    events: function () {
      return _.extend(ActionOptionsView.prototype.events.apply(this, arguments), {
        'click input': 'onInputClick',
        'keydown input': 'onInputKeydown',
        'keyup input': 'onInputKeyup'
      });
    },

    initialize: function () {
      this._super();
      this.assignees = null;
      this.debouncedSearch = _.debounce(this.search, 250);
    },

    getAssignee: function () {
      return this.model.get('assignee');
    },

    getAssigneeName: function () {
      return this.model.get('assigneeName');
    },

    onRender: function () {
      var that = this;
      this._super();
      this.renderTags();
      setTimeout(function () {
        that.$('input').focus();
      }, 100);
    },

    renderTags: function () {
      this.$('.menu').empty();
      this.getAssignees().forEach(this.renderAssignee, this);
      this.bindUIElements();
      this.selectInitialOption();
    },

    renderAssignee: function (assignee) {
      var html = this.optionTemplate(assignee);
      this.$('.menu').append(html);
    },

    selectOption: function (e) {
      var assignee = $(e.currentTarget).data('value'),
          assigneeName = $(e.currentTarget).data('text');
      this.submit(assignee, assigneeName);
      return this._super(e);
    },

    submit: function (assignee, assigneeName) {
      var that = this;
      var _assignee = this.getAssignee(),
          _assigneeName = this.getAssigneeName();
      if (assignee === _assignee) {
        return;
      }
      if (assignee === '') {
        this.model.set({ assignee: null, assigneeName: null });
      } else {
        this.model.set({ assignee: assignee, assigneeName: assigneeName });
      }
      return $.ajax({
        type: 'POST',
        url: baseUrl + '/api/issues/assign',
        data: {
          issue: this.model.id,
          assignee: assignee
        }
      }).fail(function () {
        that.model.set({ assignee: _assignee, assigneeName: _assigneeName });
      });
    },

    onInputClick: function (e) {
      e.stopPropagation();
    },

    onInputKeydown: function (e) {
      this.query = this.$('input').val();
      if (e.keyCode === 38) {
        this.selectPreviousOption();
      }
      if (e.keyCode === 40) {
        this.selectNextOption();
      }
      if (e.keyCode === 13) {
        this.selectActiveOption();
      }
      if (e.keyCode === 27) {
        this.destroy();
      }
      if ([9, 13, 27, 38, 40].indexOf(e.keyCode) !== -1) {
        return false;
      }
    },

    onInputKeyup: function () {
      var query = this.$('input').val();
      if (query !== this.query) {
        if (query.length < 2) {
          query = '';
        }
        this.query = query;
        this.debouncedSearch(query);
      }
    },

    search: function (query) {
      var that = this;
      if (query.length > 1) {
        $.get(baseUrl + '/api/users/search', { q: query }).done(function (data) {
          that.resetAssignees(data.users);
        });
      } else {
        this.resetAssignees();
      }
    },

    resetAssignees: function (users) {
      if (users) {
        this.assignees = users.map(function (user) {
          return { id: user.login, text: user.name };
        });
      } else {
        this.assignees = null;
      }
      this.renderTags();
    },

    getAssignees: function () {
      if (this.assignees) {
        return this.assignees;
      }
      var assignees = [{ id: '', text: t('unassigned') }],
          currentUser = window.SS.user,
          currentUserName = window.SS.userName;
      assignees.push({ id: currentUser, text: currentUserName });
      if (this.getAssignee()) {
        assignees.push({ id: this.getAssignee(), text: this.getAssigneeName() });
      }
      return this.makeUnique(assignees);
    },

    makeUnique: function (assignees) {
      return _.uniq(assignees, false, function (assignee) {
        return assignee.id;
      });
    }
  });

});
