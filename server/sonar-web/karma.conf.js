// Karma configuration
// Generated on Fri Jun 05 2015 15:17:30 GMT+0200 (CEST)

module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      'src/main/js/libs/translate.js',
      'src/main/js/libs/third-party/jquery.js',
      'src/main/js/libs/third-party/jquery-ui.js',
      'src/main/js/libs/third-party/d3.js',
      'src/main/js/libs/third-party/latinize.js',
      'src/main/js/libs/third-party/underscore.js',
      'src/main/js/libs/third-party/backbone.js',
      'src/main/js/libs/third-party/backbone-super.js',
      'src/main/js/libs/third-party/backbone.marionette.js',
      'src/main/js/libs/third-party/handlebars.js',
      'src/main/js/libs/third-party/underscore.js',
      'src/main/js/libs/third-party/select2.js',
      'src/main/js/libs/third-party/keymaster.js',
      'src/main/js/libs/third-party/moment.js',
      'src/main/js/libs/third-party/numeral.js',
      'src/main/js/libs/third-party/numeral-languages.js',
      'src/main/js/libs/third-party/bootstrap/tooltip.js',
      'src/main/js/libs/third-party/bootstrap/dropdown.js',
      'src/main/js/libs/third-party/md5.js',
      'src/main/js/libs/select2-jquery-ui-fix.js',

      'src/main/js/libs/widgets/base.js',
      'src/main/js/libs/widgets/widget.js',
      'src/main/js/libs/widgets/bubble-chart.js',
      'src/main/js/libs/widgets/timeline.js',
      'src/main/js/libs/widgets/stack-area.js',
      'src/main/js/libs/widgets/pie-chart.js',
      'src/main/js/libs/widgets/histogram.js',
      'src/main/js/libs/widgets/word-cloud.js',
      'src/main/js/libs/widgets/tag-cloud.js',
      'src/main/js/libs/widgets/treemap.js',

      'src/main/js/libs/graphics/pie-chart.js',
      'src/main/js/libs/graphics/barchart.js',
      'src/main/js/libs/sortable.js',

      'src/main/js/libs/inputs.js',
      'src/main/js/components/common/dialogs.js',
      'src/main/js/components/common/processes.js',
      'src/main/js/components/common/jquery-isolated-scroll.js',
      'src/main/js/components/common/handlebars-extensions.js',

      'src/main/js/libs/application.js',
      'src/main/js/libs/csv.js',
      'src/main/js/libs/dashboard.js',
      'src/main/js/libs/recent-history.js',
      'src/main/js/libs/third-party/require.js',
      
      'src/test/unit/**/*.js'
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
      'src/main/js/!(libs)/**/*.js': ['coverage'],
      'src/main/js/libs/*.js': ['coverage'],
      'src/main/js/libs/!(third-party)/**/*.js': ['coverage']
    },


    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress', 'coverage'],


    coverageReporter: {
      type : 'lcov',
      dir : 'target/js-coverage',
      subdir: 'unit'
    },


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: false,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['PhantomJS'],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: true
  });
};
