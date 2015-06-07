/* jshint jasmine:true */
define(function () {

  describe('Files and Paths', function () {

    it('collapsedDirFromPath()', function () {
      expect(window.collapsedDirFromPath(null)).toBe(null);
      expect(window.collapsedDirFromPath('src/main.js'))
          .toBe('src/');
      expect(window.collapsedDirFromPath('src/main/js/components/state.js'))
          .toBe('src/main/js/components/');
      expect(window.collapsedDirFromPath('src/main/js/components/navigator/app/models/state.js'))
          .toBe('src/.../js/components/navigator/app/models/');
      expect(window.collapsedDirFromPath('src/main/another/js/components/navigator/app/models/state.js'))
          .toBe('src/.../js/components/navigator/app/models/');
    });

    it('fileFromPath()', function () {
      expect(window.fileFromPath(null)).toBe(null);
      expect(window.fileFromPath('state.js')).toBe('state.js');
      expect(window.fileFromPath('src/main/js/components/state.js')).toBe('state.js');
    });

  });


  describe('Format Measures', function () {
    var HOURS_IN_DAY = 8,
        ONE_MINUTE = 1,
        ONE_HOUR = ONE_MINUTE * 60,
        ONE_DAY = HOURS_IN_DAY * ONE_HOUR;

    beforeAll(function () {
      window.messages = {
        'work_duration.x_days': '{0}d',
        'work_duration.x_hours': '{0}h',
        'work_duration.x_minutes': '{0}min'
      };
      window.SS = { hoursInDay: HOURS_IN_DAY };
    });

    it('should format INT', function () {
      expect(window.formatMeasure(0, 'INT')).toBe('0');
      expect(window.formatMeasure(1, 'INT')).toBe('1');
      expect(window.formatMeasure(-5, 'INT')).toBe('-5');
      expect(window.formatMeasure(999, 'INT')).toBe('999');
      expect(window.formatMeasure(1000, 'INT')).toBe('1,000');
      expect(window.formatMeasure(1529, 'INT')).toBe('1,529');
      expect(window.formatMeasure(10000, 'INT')).toBe('10,000');
      expect(window.formatMeasure(1234567890, 'INT')).toBe('1,234,567,890');
    });

    it('should format SHORT_INT', function () {
      expect(window.formatMeasure(0, 'SHORT_INT')).toBe('0');
      expect(window.formatMeasure(1, 'SHORT_INT')).toBe('1');
      expect(window.formatMeasure(999, 'SHORT_INT')).toBe('999');
      expect(window.formatMeasure(1000, 'SHORT_INT')).toBe('1k');
      expect(window.formatMeasure(1529, 'SHORT_INT')).toBe('1.5k');
      expect(window.formatMeasure(10000, 'SHORT_INT')).toBe('10k');
      expect(window.formatMeasure(10678, 'SHORT_INT')).toBe('11k');
      expect(window.formatMeasure(1234567890, 'SHORT_INT')).toBe('1b');
    });

    it('should format FLOAT', function () {
      expect(window.formatMeasure(0.0, 'FLOAT')).toBe('0.0');
      expect(window.formatMeasure(1.0, 'FLOAT')).toBe('1.0');
      expect(window.formatMeasure(1.3, 'FLOAT')).toBe('1.3');
      expect(window.formatMeasure(1.34, 'FLOAT')).toBe('1.3');
      expect(window.formatMeasure(50.89, 'FLOAT')).toBe('50.9');
      expect(window.formatMeasure(100.0, 'FLOAT')).toBe('100.0');
      expect(window.formatMeasure(123.456, 'FLOAT')).toBe('123.5');
      expect(window.formatMeasure(123456.7, 'FLOAT')).toBe('123,456.7');
      expect(window.formatMeasure(1234567890.0, 'FLOAT')).toBe('1,234,567,890.0');
    });

    it('should format PERCENT', function () {
      expect(window.formatMeasure(0.0, 'PERCENT')).toBe('0.0%');
      expect(window.formatMeasure(1.0, 'PERCENT')).toBe('1.0%');
      expect(window.formatMeasure(1.3, 'PERCENT')).toBe('1.3%');
      expect(window.formatMeasure(1.34, 'PERCENT')).toBe('1.3%');
      expect(window.formatMeasure(50.89, 'PERCENT')).toBe('50.9%');
      expect(window.formatMeasure(100.0, 'PERCENT')).toBe('100.0%');
    });

    it('should format WORK_DUR', function () {
      expect(window.formatMeasure(0, 'WORK_DUR')).toBe('0');
      expect(window.formatMeasure(5 * ONE_DAY, 'WORK_DUR')).toBe('5d');
      expect(window.formatMeasure(2 * ONE_HOUR, 'WORK_DUR')).toBe('2h');
      expect(window.formatMeasure(ONE_MINUTE, 'WORK_DUR')).toBe('1min');
      expect(window.formatMeasure(5 * ONE_DAY + 2 * ONE_HOUR, 'WORK_DUR')).toBe('5d 2h');
      expect(window.formatMeasure(2 * ONE_HOUR + ONE_MINUTE, 'WORK_DUR')).toBe('2h 1min');
      expect(window.formatMeasure(5 * ONE_DAY + 2 * ONE_HOUR + ONE_MINUTE, 'WORK_DUR')).toBe('5d 2h');
      expect(window.formatMeasure(15 * ONE_DAY + 2 * ONE_HOUR + ONE_MINUTE, 'WORK_DUR')).toBe('15d');
      expect(window.formatMeasure(-5 * ONE_DAY, 'WORK_DUR')).toBe('-5d');
      expect(window.formatMeasure(-2 * ONE_HOUR, 'WORK_DUR')).toBe('-2h');
      expect(window.formatMeasure(-1 * ONE_MINUTE, 'WORK_DUR')).toBe('-1min');
    });

    it('should format RATING', function () {
      expect(window.formatMeasure(1, 'RATING')).toBe('A');
      expect(window.formatMeasure(2, 'RATING')).toBe('B');
      expect(window.formatMeasure(3, 'RATING')).toBe('C');
      expect(window.formatMeasure(4, 'RATING')).toBe('D');
      expect(window.formatMeasure(5, 'RATING')).toBe('E');
    });

    it('should format RANDOM_TYPE', function () {
      expect(window.formatMeasure('random value', 'RANDOM_TYPE')).toBe('random value');
    });

    it('should format INT variation', function () {
      expect(window.formatMeasureVariation(0, 'INT')).toBe('0');
      expect(window.formatMeasureVariation(1, 'INT')).toBe('+1');
      expect(window.formatMeasureVariation(-1, 'INT')).toBe('-1');
      expect(window.formatMeasureVariation(1529, 'INT')).toBe('+1,529');
      expect(window.formatMeasureVariation(-1529, 'INT')).toBe('-1,529');
    });

    it('should format SHORT_INT variation', function () {
      expect(window.formatMeasureVariation(0, 'SHORT_INT')).toBe('0');
      expect(window.formatMeasureVariation(1, 'SHORT_INT')).toBe('+1');
      expect(window.formatMeasureVariation(-1, 'SHORT_INT')).toBe('-1');
      expect(window.formatMeasureVariation(1529, 'SHORT_INT')).toBe('+1.5k');
      expect(window.formatMeasureVariation(-1529, 'SHORT_INT')).toBe('-1.5k');
      expect(window.formatMeasureVariation(10678, 'SHORT_INT')).toBe('+11k');
      expect(window.formatMeasureVariation(-10678, 'SHORT_INT')).toBe('-11k');
    });

    it('should format FLOAT variation', function () {
      expect(window.formatMeasureVariation(0.0, 'FLOAT')).toBe('0');
      expect(window.formatMeasureVariation(1.0, 'FLOAT')).toBe('+1.0');
      expect(window.formatMeasureVariation(-1.0, 'FLOAT')).toBe('-1.0');
      expect(window.formatMeasureVariation(50.89, 'FLOAT')).toBe('+50.9');
      expect(window.formatMeasureVariation(-50.89, 'FLOAT')).toBe('-50.9');
    });

    it('should format PERCENT variation', function () {
      expect(window.formatMeasureVariation(0.0, 'PERCENT')).toBe('0%');
      expect(window.formatMeasureVariation(1.0, 'PERCENT')).toBe('+1.0%');
      expect(window.formatMeasureVariation(-1.0, 'PERCENT')).toBe('-1.0%');
      expect(window.formatMeasureVariation(50.89, 'PERCENT')).toBe('+50.9%');
      expect(window.formatMeasureVariation(-50.89, 'PERCENT')).toBe('-50.9%');
    });

    it('should format WORK_DUR variation', function () {
      expect(window.formatMeasureVariation(0, 'WORK_DUR')).toBe('0');
      expect(window.formatMeasureVariation(5 * ONE_DAY, 'WORK_DUR')).toBe('+5d');
      expect(window.formatMeasureVariation(2 * ONE_HOUR, 'WORK_DUR')).toBe('+2h');
      expect(window.formatMeasureVariation(ONE_MINUTE, 'WORK_DUR')).toBe('+1min');
      expect(window.formatMeasureVariation(-5 * ONE_DAY, 'WORK_DUR')).toBe('-5d');
      expect(window.formatMeasureVariation(-2 * ONE_HOUR, 'WORK_DUR')).toBe('-2h');
      expect(window.formatMeasureVariation(-1 * ONE_MINUTE, 'WORK_DUR')).toBe('-1min');
    });

    it('should format RANDOM_TYPE variation', function () {
      expect(window.formatMeasureVariation('random value', 'RANDOM_TYPE')).toBe('random value');
    });

  });


  describe('Severity Comparators', function () {

    it('should have severity comparator', function () {
      expect(window.severityComparator('BLOCKER')).toBe(0);
      expect(window.severityComparator('CRITICAL')).toBe(1);
      expect(window.severityComparator('MAJOR')).toBe(2);
      expect(window.severityComparator('MINOR')).toBe(3);
      expect(window.severityComparator('INFO')).toBe(4);
    });

    it('should have severity column comparator', function () {
      expect(window.severityColumnsComparator('BLOCKER')).toBe(0);
      expect(window.severityColumnsComparator('CRITICAL')).toBe(2);
      expect(window.severityColumnsComparator('MAJOR')).toBe(4);
      expect(window.severityColumnsComparator('MINOR')).toBe(1);
      expect(window.severityColumnsComparator('INFO')).toBe(3);
    });

  });


  describe('MD5', function () {

    it('should hash', function () {
      expect(window.getMD5Hash(null)).toBe(null);
      expect(window.getMD5Hash('')).toBe('d41d8cd98f00b204e9800998ecf8427e');
      expect(window.getMD5Hash('abcd')).toBe('e2fc714c4727ee9395f324cd2e7f331f');
      expect(window.getMD5Hash(' abcd')).toBe('e2fc714c4727ee9395f324cd2e7f331f');
      expect(window.getMD5Hash('    abcd   ')).toBe('e2fc714c4727ee9395f324cd2e7f331f');
    });

  });

});