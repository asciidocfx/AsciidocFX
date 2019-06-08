function sendConsole(args, level) {

    args = [].slice.call(args);
    var message = args.map(message => {
        if (message.constructor == Error || message.constructor == TypeError) {
            var stack = message.stack.replace(/^[^\(]+?[\n$]/gm, '')
                .replace(/^\s+at\s+/gm, '')
                .replace(/^Object.<anonymous>\s*\(/gm, '{anonymous}()@')
                .split('\n');
            return (message + '\n' + stack);
        } else {
            return message;
        }
    }).map(message => {
        return message.constructor == String ? message : JSON.stringify(message);
    }).join(" | ");

    self.postMessage(JSON.stringify({
        type: "log",
        level: level,
        message: message,
        taskId: lastTaskId
    }));
}

console = {};

console.log = function () {
    sendConsole(arguments, "log");
};

console.debug = function () {
    sendConsole(arguments, "debug");
};

console.warn = function () {
    sendConsole(arguments, "warn");
};

console.error = function () {
    sendConsole(arguments, "error");
};

console.info = function () {
    sendConsole(arguments, "info");
};