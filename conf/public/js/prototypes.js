function cachedImageUri(content) {
    var md5 = CryptoJS.MD5(content);

    return "/afx/cache/" + md5 + ".png";
};

function lineNumber(ifExist) {
    var node = this;
    if (node && node.$source_location) {
        if (node.$source_location().lineno) {
            var nodeName = node.node_name;
            var lineno = node.$source_location().lineno;
            ifExist(lineno);
            return lineno;
        }
    }
};

function setAttribute(name, value) {
    var node = this;
    if (node && node.$attributes) {
        node.$attributes()['$[]='](name, value);
        return true;
    }

    return false;

};

function isNil(name) {
    var obj = this; // e.g document
    if (obj && obj.$attr) {
        return obj.$attr(name)['$nil?']();
    }
    return true;
};

function toString() {
    var obj = this;

    var result = {};
    var limit = 100;

    function iterate_keys(obj) {
        limit--;
        if (limit <= 0) {
            return;
        }
        var keys = [];

        try{
            keys= Object.keys(obj);
        }
        catch (e){

        }
        keys.forEach(function (key) {
            var value = obj[key];

            console.log(key+":"+value);


            if (value) {
                if (value.constructor != Function) {
                    result[key] = value;
                    iterate_keys(value);
                }
            }

        });
    }

    iterate_keys(obj);

    return JSON.stringify(result);

};




