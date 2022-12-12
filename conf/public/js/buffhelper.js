function BufferedAction() {

    var afterRun1;
    var afterRun2;

    // Delays until all events stopped, then runs in timeout
    this.buff = function (code, delay) {

        if (afterRun1)
            clearTimeout(afterRun1);

        afterRun1 = setTimeout(code, delay);
    };

    var codes = [];

    // Runs one time in interval
    this.throttle = function (code, delay) {
        if (code) {
            codes[0] = code;
        }

        if (afterRun2) {
            return;
        }

        let popped = codes.pop();

        if (!popped) {
            return;
        }

        afterRun2 = setTimeout(() => {
            popped();
            afterRun2 = null;
            if (codes.length > 0) {
                this.throttle(null, delay);
            }
        }, delay);
    };
}