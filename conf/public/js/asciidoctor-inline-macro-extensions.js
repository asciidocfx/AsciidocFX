function process_inline_macro_extension(obj) {

    var attrs = obj["attrs"];
    var parent = obj["parent"];
    var self = obj["self"];
    var nil = obj["nil"];
    var name = obj["name"];
    var target = obj["target"];

    var title = (attrs.title),
        alt = (attrs.alt),
        caption = (attrs.caption),
        width = (attrs.width),
        height = (attrs.height),
        scale = (attrs.scale),
        align = (attrs.align),
        type = (attrs.type),
        cache = (attrs.cache),
        role = (attrs.role),
        link = (attrs.link),
        float = (attrs.float),
        imagesdir = parent.$document().$attr('imagesdir', '');

    var filename = "";

    //if (!attrs['$[]']("file")["$nil?"]()) {
    //    var extension = attrs['$[]']("extension")["$nil?"]() ? "" : "." + attrs['$[]']("extension");
    //    filename = "" + attrs['$[]']("file") + extension;
    //}

    var parts = target.split(",");


    if (parts.length > 1) {
        var extension = parts.length > 2 ? "." + parts[2] : "";
        filename = "" + parts[1] + extension;
    }

    var normalName = name.toLowerCase();

    var content = "" + parts[0];
    var command = normalName;

    if (filename != "") {
        target = parent.$image_uri(filename);
    } else {
        target = cachedImageUri(content);
        var host = ((typeof location) != "undefined") ? "http://" + location.host : "";
        filename = host + target;
    }

    var stems = getMathExtensionNames();
    if (stems.indexOf(name) != -1) {
        content = parseStems(parent, content, name);
        command = "math";
    }

    var parameters = [content, type, imagesdir, target, normalName].map(function (e) {
        return e + "";
    });

    //afx[command].apply(afx,parameters);

    postMessage(JSON.stringify({
        type: "afx",
        func: command,
        parameters: parameters
    }));

    var attributes = {
        "target": filename,
        "title": title,
        "alt": alt,
        "caption": caption,
        "width": width,
        "height": height,
        "scale": scale,
        "align": align,
        "role": role,
        "link": link,
        "float": float
    };

    var keys = Object.keys(attributes);

    keys.forEach(function (key) {
        if (attributes[key] == null) {
            delete attributes[key];
        }
    });

    attributes.alt = attributes.alt || "alt";
    delete attributes.target;

    return self.createInline(parent, "image", "", {target: filename, attributes: attributes});
}

function registerInlineMacroExtensions(name) {
    asciidoctor.Extensions.register(function () {
        this.inlineMacro(name, function () {
            let self = this;
            self.$match_format('short');
            self.$name_positional_attributes("target", "file", "extension");
            self.process(function (parent, target, attrs) {

                return process_inline_macro_extension({
                    parent: parent,
                    attrs: attrs,
                    self: self,
                    nil: Opal.nil,
                    name: name,
                    target: target
                });
            });
        });
    });
}

// there is a bug for math asciimath:[] generates text + image ascii<img>
getMathExtensionNames()
    .forEach(function (name) {
        registerInlineMacroExtensions(name);
    });
