var Ajax = {};
Ajax.extractParameters = function (params) {
    if (!params) {
        return null;
    }

    return Object
        .keys(params)
        .map(function (key) {
            return key + "=" + encodeURIComponent(params[key])
        })
        .join("&")
};

Ajax.getFile = function (path, params) {
    var data = "";
    var status = -1;

    try {
        var request = new XMLHttpRequest();
        request.open("get", path, false);
        request.overrideMimeType('text/plain');
        request.send(Ajax.extractParameters(params));
        data = request.responseText;
    }
    catch (e) {
        status = 0;
    }

    if (status > 400 || (status == 0 && data == "")) {
        console.log("No such file or directory: " + path);
    }

    return data;
};

Ajax.postFile = function (path, params) {
    var data = "";
    var status = -1;

    try {
        var request = new XMLHttpRequest();
        request.open("post", path, false);
        request.overrideMimeType('text/plain');
        request.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        request.send(Ajax.extractParameters(params));
        data = request.responseText;
    }
    catch (e) {
        status = 0;
    }

    if (status > 400 || (status == 0 && data == "")) {
        console.log("No such file or directory: " + path);
    }

    return data;
};