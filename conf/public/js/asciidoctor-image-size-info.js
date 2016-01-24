/* Generated by Opal 0.8.0 */
Opal.modules["image-size-info-treeprocessor/extension"] = function (Opal) {
    Opal.dynamic_require_severity = "ignore";
    var self = Opal.top, $scope = Opal, nil = Opal.nil, $breaker = Opal.breaker, $slice = Opal.slice, $klass = Opal.klass, $hash2 = Opal.hash2;

    if ($scope.get('RUBY_ENGINE')['$==']("opal")) {
    }
    ;
    self.$include(Opal.get('Asciidoctor'));
    return (function ($base, $super) {
        function $DataLineTreeprocessor() {
        };
        var self = $DataLineTreeprocessor = $klass($base, $super, 'DataLineTreeprocessor', $DataLineTreeprocessor);

        var def = self.$$proto, $scope = self.$$scope;

        return (def.$process = function (document) {
                var self = this;

                if (document.$attr('apply-image-size')['$nil?']())
                    return document;

                var backend = document.$backend();

                if (backend == 'revealjs' || backend == 'deckjs')
                    return document;

                try {
                    var imageNodes = document.$find_by(Opal.hash2(['context'], {"context": "image"}));

                    imageNodes.forEach(function (node) {

                        var declaredWidth = node.$attributes()['$[]']('width') == false;
                        var declaredHeight = node.$attributes()['$[]']('height') == false;

                        if (declaredWidth && declaredHeight) {
                            var info = {};
                            afx.getImageInfo(node.$image_uri(node.$attr('target')), info);

                            if (info.width && info.height) {
                                node.$attributes()['$[]=']("width", info.width);
                                node.$attributes()['$[]=']("height", info.height);
                            }

                        }

                    });
                } catch (e) {
                    console.log(e);
                }

                return document;
            }, nil) && 'process'
    })(self, (($scope.get('Extensions')).$$scope.get('Treeprocessor')));
};
/* Generated by Opal 0.8.0 */
Opal.modules["image-size-info-treeprocessor"] = function (Opal) {
    Opal.dynamic_require_severity = "ignore";
    var $a, $b, TMP_1, self = Opal.top, $scope = Opal, nil = Opal.nil, $breaker = Opal.breaker, $slice = Opal.slice;

    if ($scope.get('RUBY_ENGINE')['$==']("opal")) {
        self.$require("image-size-info-treeprocessor/extension")
    }
    ;
    return ($a = ($b = $scope.get('Extensions')).$register, $a.$$p = (TMP_1 = function () {
        var self = TMP_1.$$s || this;

        return self.$treeprocessor($scope.get('DataLineTreeprocessor'))
    }, TMP_1.$$s = self, TMP_1), $a).call($b);
};

Opal.require('image-size-info-treeprocessor');
Opal.require('image-size-info-treeprocessor/extension');