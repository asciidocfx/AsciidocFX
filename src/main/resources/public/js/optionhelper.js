function getOption(opt) {

    var jsonObject = JSON.parse(opt);

    var attrs = jsonObject.attributes;
    var attrKeys = Object.keys(attrs);
    var fixAttr = [];

    fixAttr.push("lang=" + getDefaultLanguage());

    attrKeys.forEach(function (key, index) {
        fixAttr.push(key + "=" + attrs[key]);
    });

    jsonObject.attributes = fixAttr;

    return Opal.hash2(Object.keys(jsonObject), jsonObject);
};


function getDefaultLanguage() {
    var defaultLanguage = "en";

    if ((typeof afx) != "undefined") {
        var languages = afx.getEditorConfigBean().getDefaultLanguage();
        if (languages.size() > 0) {
            defaultLanguage = languages.get(0);
        }
    }
    return defaultLanguage;
}