const fs = require('fs');
const Builder = require('opal-compiler').Builder;
// Opal object will be available on the global scope

const builder = Builder.$new();
builder.$append_paths('lib');

let extension_name = "substitutors";

const result = builder.$build(extension_name + '.rb');
fs.writeFileSync(extension_name + '.js', result.$to_s(), 'utf8');