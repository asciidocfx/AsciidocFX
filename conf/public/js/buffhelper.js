function BufferedAction() {

    var afterRun;

    this.buff = function (code, delay) {

        if (afterRun)
            clearTimeout(afterRun);

        afterRun = setTimeout(code, delay);
    };
}