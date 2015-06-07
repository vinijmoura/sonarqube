define([
  'apps/computation/report'
], function (Report) {

  describe('just checking', function () {

    it('works', function () {
      var report = new Report({ key: 'id' });
      expect(report.id).toBe('id');
    });

  });

});