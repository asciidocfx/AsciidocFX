const asciidoctor = require('asciidoctor')()

asciidoctor.Extensions.register(function () {
    this.inlineMacro("extname", function () {
        let self = this;
        self.$match_format('short')
        self.process(function (parent, target, attrs) {
            return self.createInline(parent, "image", "", {target: "http://", attributes: {alt: "alt"}});
        });
    });
});

const content = '==Extension test \n Extension: extname:[hello] ';

const html = asciidoctor.convert(content);

console.log(html);