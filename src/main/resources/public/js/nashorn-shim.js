var console = {
    log: print,
    debug: print,
    warn: print,
    error: print,
    info: print
}

var navigator = {
    appVersion: 'JavaFX'
}

if ((typeof setTimeout) == "undefined") {
    setTimeout = function () {
        arguments[0]();
    };
}

if ((typeof clearTimeout) == "undefined") {
    clearTimeout = function () {
    };
}