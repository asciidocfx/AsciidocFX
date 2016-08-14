Object.prototype._cached_image_uri = function (content) {
    var md5 = CryptoJS.MD5(content);

    return "/afx/cache/" + md5 + ".png";
};

Object.prototype._line_number = function (ifExist) {
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

Object.prototype._set_attribute = function (name, value) {
    var node = this;
    if (node && node.$attributes) {
        node.$attributes()['$[]='](name, value);
        return true;
    }

    return false;

};

Object.prototype._is_nil = function (name) {
    var obj = this; // e.g document
    if (obj && obj.$attr) {
        return obj.$attr(name)['$nil?']();
    }
    return true;
};

Object.prototype._to_string = function () {
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




