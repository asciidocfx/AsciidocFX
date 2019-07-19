Object.prototype._is_nil = function (name) {
    const obj = this; // e.g document
    if (obj && obj.$attr) {
        return obj.$attr(name)['$nil?']();
    }
    return true;
};

asciidoctor.Extensions.register(function () {
    this.treeProcessor(function () {
        const self = this;
        const selector = JSON.stringify({
        	traverse_documents: true
        });
        self.process(function (parent, reader) {

            if (parent._is_nil("apply-data-line"))
                return parent;

            parent.$find_by(selector).forEach(function (node) {
                if (node && node.$source_location) {
                    if (node.$source_location().lineno) {
                        var nodeName = node.node_name;
                        var lineno = node.$source_location().lineno;
                        node['$add_role']("data-line-" + lineno);
                    }
                }
            });
            return parent;
        });
    });
});
