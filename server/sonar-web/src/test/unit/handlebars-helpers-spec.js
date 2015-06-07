define(function () {

  describe('Handlebars Helpers', function () {

    function emptyFn () {
      // do nothing
    }

    function returnX () {
      return 'x';
    }

    function returnY () {
      return 'y';
    }

    function helper (name) {
      var args = Array.prototype.slice.call(arguments, 1);
      args.push({});
      return Handlebars.helpers[name].apply(this, args);
    }

    function blockHelper (name, fn, inverse) {
      var args = Array.prototype.slice.call(arguments, 3);
      args.push({ fn: fn, inverse: inverse });
      return Handlebars.helpers[name].apply(this, args);
    }

    it('capitalize', function () {
      expect(helper('capitalize', '')).toBe('');
      expect(helper('capitalize', 'a')).toBe('A');
      expect(helper('capitalize', 'abcd')).toBe('Abcd');
      expect(helper('capitalize', 'abcd def')).toBe('Abcd def');
    });

    it('gt', function () {
      expect(blockHelper('gt', returnX, returnY, 1, 2)).toBe('y');
      expect(blockHelper('gt', returnX, returnY, 2, 1)).toBe('x');
      expect(blockHelper('gt', returnX, returnY, 1, 1)).toBe('y');
    });

    it('lt', function () {
      expect(blockHelper('lt', returnX, returnY, 1, 2)).toBe('x');
      expect(blockHelper('lt', returnX, returnY, 2, 1)).toBe('y');
      expect(blockHelper('lt', returnX, returnY, 1, 1)).toBe('y');
    });

    it('ifLength', function () {
      expect(blockHelper('ifLength', returnX, returnY, null, 7)).toBe('y');
      expect(blockHelper('ifLength', returnX, returnY, [], 0)).toBe('x');
      expect(blockHelper('ifLength', returnX, returnY, [], 1)).toBe('y');
      expect(blockHelper('ifLength', returnX, returnY, ['a'], 1)).toBe('x');
      expect(blockHelper('ifLength', returnX, returnY, ['a'], 2)).toBe('y');
    });

    it('numberShort', function () {
      expect(helper('numberShort', 0)).toBe('0');
      expect(helper('numberShort', 1)).toBe('1');
      expect(helper('numberShort', 999)).toBe('999');
      expect(helper('numberShort', 1000)).toBe('1,000');
      expect(helper('numberShort', 1529)).toBe('1,529');
      expect(helper('numberShort', 10000)).toBe('10k');
      expect(helper('numberShort', 10678)).toBe('10.7k');
      expect(helper('numberShort', 1234567890)).toBe('1b');
    });

    it('limitString', function () {
      expect(helper('limitString', '')).toBe('');
      expect(helper('limitString', 'abcd')).toBe('abcd');
      expect(helper('limitString', 'aaaa aaaa aaaa aaaa aaaa aaaa ')).toBe('aaaa aaaa aaaa aaaa aaaa aaaa ');
      expect(helper('limitString', 'aaaa aaaa aaaa aaaa aaaa aaaa a')).toBe('aaaa aaaa aaaa aaaa aaaa aaaa ...');
      expect(helper('limitString', 'aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa aaaa '),
          'aaaa aaaa aaaa aaaa aaaa aaaa ...');
    });

    it('withSign', function () {
      expect(helper('withSign', 0)).toBe('+0');
      expect(helper('withSign', 1)).toBe('+1');
      expect(helper('withSign', 2)).toBe('+2');
      expect(helper('withSign', -1)).toBe('-1');
      expect(helper('withSign', -2)).toBe('-2');
    });

    it('formatMeasure', function () {
      expect(helper('formatMeasure', 50.89, 'PERCENT')).toBe('50.9%');
    });

    it('formatMeasureVariation', function () {
      expect(helper('formatMeasureVariation', 50.89, 'PERCENT')).toBe('+50.9%');
    });

    it('repeat', function () {
      expect(blockHelper('repeat', returnX, emptyFn, 3)).toBe('xxx');
    });

    it('eqComponents', function () {
      expect(blockHelper('eqComponents', returnX, returnY, null, null)).toBe('x');
      expect(blockHelper('eqComponents', returnX, returnY, {}, null)).toBe('x');
      expect(blockHelper('eqComponents', returnX, returnY, null, {})).toBe('x');
      expect(blockHelper('eqComponents', returnX, returnY,
          { project: 'A' }, { project: 'A' })).toBe('x');
      expect(blockHelper('eqComponents', returnX, returnY,
          { project: 'A' }, { project: 'B' })).toBe('y');
      expect(blockHelper('eqComponents', returnX, returnY,
          { project: 'A', subProject: 'D' }, { project: 'A', subProject: 'D' })).toBe('x');
      expect(blockHelper('eqComponents', returnX, returnY,
          { project: 'A', subProject: 'D' }, { project: 'A', subProject: 'E' })).toBe('y');
      expect(blockHelper('eqComponents', returnX, returnY,
          { project: 'A', subProject: 'D' }, { project: 'B', subProject: 'D' })).toBe('y');
      expect(blockHelper('eqComponents', returnX, returnY,
          { project: 'A', subProject: 'D' }, { project: 'B', subProject: 'E' })).toBe('y');
    });

  });

});
