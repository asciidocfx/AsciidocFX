(function e(t, n, r) {
    function s(o, u) {
        if (!n[o]) {
            if (!t[o]) {
                var a = typeof require == "function" && require;
                if (!u && a)return a(o, !0);
                if (i)return i(o, !0);
                throw new Error("Cannot find module '" + o + "'")
            }
            var f = n[o] = {exports: {}};
            t[o][0].call(f.exports, function (e) {
                var n = t[o][1][e];
                return s(n ? n : e)
            }, f, f.exports, e, t, n, r)
        }
        return n[o].exports
    }

    var i = typeof require == "function" && require;
    for (var o = 0; o < r.length; o++)s(r[o]);
    return s
})({
    1: [function (require, module, exports) {

        /* **********************************************
         Begin prism-core.js
         ********************************************** */

        var _self = (typeof window !== 'undefined')
            ? window   // if in browser
            : (
            (typeof WorkerGlobalScope !== 'undefined' && self instanceof WorkerGlobalScope)
                ? self // if in worker
                : {}   // if in node js
        );

        /**
         * Prism: Lightweight, robust, elegant syntax highlighting
         * MIT license http://www.opensource.org/licenses/mit-license.php/
         * @author Lea Verou http://lea.verou.me
         */

        var Prism = (function () {

// Private helper vars
            var lang = /\blang(?:uage)?-(?!\*)(\w+)\b/i;

            var _ = _self.Prism = {
                util: {
                    encode: function (tokens) {
                        if (tokens instanceof Token) {
                            return new Token(tokens.type, _.util.encode(tokens.content), tokens.alias);
                        } else if (_.util.type(tokens) === 'Array') {
                            return tokens.map(_.util.encode);
                        } else {
                            return tokens.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/\u00a0/g, ' ');
                        }
                    },

                    type: function (o) {
                        return Object.prototype.toString.call(o).match(/\[object (\w+)\]/)[1];
                    },

                    // Deep clone a language definition (e.g. to extend it)
                    clone: function (o) {
                        var type = _.util.type(o);

                        switch (type) {
                            case 'Object':
                                var clone = {};

                                for (var key in o) {
                                    if (o.hasOwnProperty(key)) {
                                        clone[key] = _.util.clone(o[key]);
                                    }
                                }

                                return clone;

                            case 'Array':
                                // Check for existence for IE8
                                return o.map && o.map(function (v) {
                                        return _.util.clone(v);
                                    });
                        }

                        return o;
                    }
                },

                languages: {
                    extend: function (id, redef) {
                        var lang = _.util.clone(_.languages[id]);

                        for (var key in redef) {
                            lang[key] = redef[key];
                        }

                        return lang;
                    },

                    /**
                     * Insert a token before another token in a language literal
                     * As this needs to recreate the object (we cannot actually insert before keys in object literals),
                     * we cannot just provide an object, we need anobject and a key.
                     * @param inside The key (or language id) of the parent
                     * @param before The key to insert before. If not provided, the function appends instead.
                     * @param insert Object with the key/value pairs to insert
                     * @param root The object that contains `inside`. If equal to Prism.languages, it can be omitted.
                     */
                    insertBefore: function (inside, before, insert, root) {
                        root = root || _.languages;
                        var grammar = root[inside];

                        if (arguments.length == 2) {
                            insert = arguments[1];

                            for (var newToken in insert) {
                                if (insert.hasOwnProperty(newToken)) {
                                    grammar[newToken] = insert[newToken];
                                }
                            }

                            return grammar;
                        }

                        var ret = {};

                        for (var token in grammar) {

                            if (grammar.hasOwnProperty(token)) {

                                if (token == before) {

                                    for (var newToken in insert) {

                                        if (insert.hasOwnProperty(newToken)) {
                                            ret[newToken] = insert[newToken];
                                        }
                                    }
                                }

                                ret[token] = grammar[token];
                            }
                        }

                        // Update references in other language definitions
                        _.languages.DFS(_.languages, function (key, value) {
                            if (value === root[inside] && key != inside) {
                                this[key] = ret;
                            }
                        });

                        return root[inside] = ret;
                    },

                    // Traverse a language definition with Depth First Search
                    DFS: function (o, callback, type) {
                        for (var i in o) {
                            if (o.hasOwnProperty(i)) {
                                callback.call(o, i, o[i], type || i);

                                if (_.util.type(o[i]) === 'Object') {
                                    _.languages.DFS(o[i], callback);
                                }
                                else if (_.util.type(o[i]) === 'Array') {
                                    _.languages.DFS(o[i], callback, i);
                                }
                            }
                        }
                    }
                },

                highlightAll: function (async, callback) {
                    var elements = document.querySelectorAll('code[class*="language-"], [class*="language-"] code, code[class*="lang-"], [class*="lang-"] code');

                    for (var i = 0, element; element = elements[i++];) {
                        _.highlightElement(element, async === true, callback);
                    }
                },

                highlightElement: function (element, async, callback) {
                    // Find language
                    var language, grammar, parent = element;

                    while (parent && !lang.test(parent.className)) {
                        parent = parent.parentNode;
                    }

                    if (parent) {
                        language = (parent.className.match(lang) || [, ''])[1];
                        grammar = _.languages[language];
                    }

                    // Set language on the element, if not present
                    element.className = element.className.replace(lang, '').replace(/\s+/g, ' ') + ' language-' + language;

                    // Set language on the parent, for styling
                    parent = element.parentNode;

                    if (/pre/i.test(parent.nodeName)) {
                        parent.className = parent.className.replace(lang, '').replace(/\s+/g, ' ') + ' language-' + language;
                    }

                    var code = element.textContent;

                    var env = {
                        element: element,
                        language: language,
                        grammar: grammar,
                        code: code
                    };

                    if (code) {
                        env.code = code.replace(/^(?:\r?\n|\r)/, '');
                    }

                    if (!code || !grammar) {
                        _.hooks.run('complete', env);
                        return;
                    }

                    _.hooks.run('before-highlight', env);

                    if (async && _self.Worker) {
                        var worker = new Worker(_.filename);

                        worker.onmessage = function (evt) {
                            env.highlightedCode = Token.stringify(JSON.parse(evt.data), language);

                            _.hooks.run('before-insert', env);

                            env.element.innerHTML = env.highlightedCode;

                            callback && callback.call(env.element);
                            _.hooks.run('after-highlight', env);
                            _.hooks.run('complete', env);
                        };

                        worker.postMessage(JSON.stringify({
                            language: env.language,
                            code: env.code
                        }));
                    }
                    else {
                        env.highlightedCode = _.highlight(env.code, env.grammar, env.language);

                        _.hooks.run('before-insert', env);

                        env.element.innerHTML = env.highlightedCode;

                        callback && callback.call(element);

                        _.hooks.run('after-highlight', env);
                        _.hooks.run('complete', env);
                    }
                },

                highlight: function (text, grammar, language) {
                    var tokens = _.tokenize(text, grammar);
                    return Token.stringify(_.util.encode(tokens), language);
                },

                tokenize: function (text, grammar, language) {
                    var Token = _.Token;

                    var strarr = [text];

                    var rest = grammar.rest;

                    if (rest) {
                        for (var token in rest) {
                            grammar[token] = rest[token];
                        }

                        delete grammar.rest;
                    }

                    tokenloop: for (var token in grammar) {
                        if (!grammar.hasOwnProperty(token) || !grammar[token]) {
                            continue;
                        }

                        var patterns = grammar[token];
                        patterns = (_.util.type(patterns) === "Array") ? patterns : [patterns];

                        for (var j = 0; j < patterns.length; ++j) {
                            var pattern = patterns[j],
                                inside = pattern.inside,
                                lookbehind = !!pattern.lookbehind,
                                lookbehindLength = 0,
                                alias = pattern.alias;

                            pattern = pattern.pattern || pattern;

                            for (var i = 0; i < strarr.length; i++) { // Don’t cache length as it changes during the loop

                                var str = strarr[i];

                                if (strarr.length > text.length) {
                                    // Something went terribly wrong, ABORT, ABORT!
                                    break tokenloop;
                                }

                                if (str instanceof Token) {
                                    continue;
                                }

                                pattern.lastIndex = 0;

                                var match = pattern.exec(str);

                                if (match) {
                                    if (lookbehind) {
                                        lookbehindLength = match[1].length;
                                    }

                                    var from = match.index - 1 + lookbehindLength,
                                        match = match[0].slice(lookbehindLength),
                                        len = match.length,
                                        to = from + len,
                                        before = str.slice(0, from + 1),
                                        after = str.slice(to + 1);

                                    var args = [i, 1];

                                    if (before) {
                                        args.push(before);
                                    }

                                    var wrapped = new Token(token, inside ? _.tokenize(match, inside) : match, alias);

                                    args.push(wrapped);

                                    if (after) {
                                        args.push(after);
                                    }

                                    Array.prototype.splice.apply(strarr, args);
                                }
                            }
                        }
                    }

                    return strarr;
                },

                hooks: {
                    all: {},

                    add: function (name, callback) {
                        var hooks = _.hooks.all;

                        hooks[name] = hooks[name] || [];

                        hooks[name].push(callback);
                    },

                    run: function (name, env) {
                        var callbacks = _.hooks.all[name];

                        if (!callbacks || !callbacks.length) {
                            return;
                        }

                        for (var i = 0, callback; callback = callbacks[i++];) {
                            callback(env);
                        }
                    }
                }
            };

            var Token = _.Token = function (type, content, alias) {
                this.type = type;
                this.content = content;
                this.alias = alias;
            };

            Token.stringify = function (o, language, parent) {
                if (typeof o == 'string') {
                    return o;
                }

                if (_.util.type(o) === 'Array') {
                    return o.map(function (element) {
                        return Token.stringify(element, language, o);
                    }).join('');
                }

                var env = {
                    type: o.type,
                    content: Token.stringify(o.content, language, parent),
                    tag: 'span',
                    classes: ['token', o.type],
                    attributes: {},
                    language: language,
                    parent: parent
                };

                if (env.type == 'comment') {
                    env.attributes['spellcheck'] = 'true';
                }

                if (o.alias) {
                    var aliases = _.util.type(o.alias) === 'Array' ? o.alias : [o.alias];
                    Array.prototype.push.apply(env.classes, aliases);
                }

                _.hooks.run('wrap', env);

                var attributes = '';

                for (var name in env.attributes) {
                    attributes += name + '="' + (env.attributes[name] || '') + '"';
                }

                return '<' + env.tag + ' class="' + env.classes.join(' ') + '" ' + attributes + '>' + env.content + '</' + env.tag + '>';

            };

            if (!_self.document) {
                if (!_self.addEventListener) {
                    // in Node.js
                    return _self.Prism;
                }
                // In worker
                _self.addEventListener('message', function (evt) {
                    var message = JSON.parse(evt.data),
                        lang = message.language,
                        code = message.code;

                    _self.postMessage(JSON.stringify(_.util.encode(_.tokenize(code, _.languages[lang]))));
                    _self.close();
                }, false);

                return _self.Prism;
            }

// Get current script and highlight
            var script = document.getElementsByTagName('script');

            script = script[script.length - 1];

            if (script) {
                _.filename = script.src;

                if (document.addEventListener && !script.hasAttribute('data-manual')) {
                    document.addEventListener('DOMContentLoaded', _.highlightAll);
                }
            }

            return _self.Prism;

        })();

        if (typeof module !== 'undefined' && module.exports) {
            module.exports = Prism;
        }


        /* **********************************************
         Begin prism-markup.js
         ********************************************** */

        Prism.languages.markup = {
            'comment': /<!--[\w\W]*?-->/,
            'prolog': /<\?[\w\W]+?\?>/,
            'doctype': /<!DOCTYPE[\w\W]+?>/,
            'cdata': /<!\[CDATA\[[\w\W]*?]]>/i,
            'tag': {
                pattern: /<\/?[^\s>\/]+(?:\s+[^\s>\/=]+(?:=(?:("|')(?:\\\1|\\?(?!\1)[\w\W])*\1|[^\s'">=]+))?)*\s*\/?>/i,
                inside: {
                    'tag': {
                        pattern: /^<\/?[^\s>\/]+/i,
                        inside: {
                            'punctuation': /^<\/?/,
                            'namespace': /^[^\s>\/:]+:/
                        }
                    },
                    'attr-value': {
                        pattern: /=(?:('|")[\w\W]*?(\1)|[^\s>]+)/i,
                        inside: {
                            'punctuation': /[=>"']/
                        }
                    },
                    'punctuation': /\/?>/,
                    'attr-name': {
                        pattern: /[^\s>\/]+/,
                        inside: {
                            'namespace': /^[^\s>\/:]+:/
                        }
                    }

                }
            },
            'entity': /&#?[\da-z]{1,8};/i
        };

// Plugin to make entity title show the real entity, idea by Roman Komarov
        Prism.hooks.add('wrap', function (env) {

            if (env.type === 'entity') {
                env.attributes['title'] = env.content.replace(/&amp;/, '&');
            }
        });


        /* **********************************************
         Begin prism-css.js
         ********************************************** */

        Prism.languages.css = {
            'comment': /\/\*[\w\W]*?\*\//,
            'atrule': {
                pattern: /@[\w-]+?.*?(;|(?=\s*\{))/i,
                inside: {
                    'rule': /@[\w-]+/
                    // See rest below
                }
            },
            'url': /url\((?:(["'])(\\(?:\r\n|[\w\W])|(?!\1)[^\\\r\n])*\1|.*?)\)/i,
            'selector': /[^\{\}\s][^\{\};]*?(?=\s*\{)/,
            'string': /("|')(\\(?:\r\n|[\w\W])|(?!\1)[^\\\r\n])*\1/,
            'property': /(\b|\B)[\w-]+(?=\s*:)/i,
            'important': /\B!important\b/i,
            'function': /[-a-z0-9]+(?=\()/i,
            'punctuation': /[(){};:]/
        };

        Prism.languages.css['atrule'].inside.rest = Prism.util.clone(Prism.languages.css);

        if (Prism.languages.markup) {
            Prism.languages.insertBefore('markup', 'tag', {
                'style': {
                    pattern: /<style[\w\W]*?>[\w\W]*?<\/style>/i,
                    inside: {
                        'tag': {
                            pattern: /<style[\w\W]*?>|<\/style>/i,
                            inside: Prism.languages.markup.tag.inside
                        },
                        rest: Prism.languages.css
                    },
                    alias: 'language-css'
                }
            });

            Prism.languages.insertBefore('inside', 'attr-value', {
                'style-attr': {
                    pattern: /\s*style=("|').*?\1/i,
                    inside: {
                        'attr-name': {
                            pattern: /^\s*style/i,
                            inside: Prism.languages.markup.tag.inside
                        },
                        'punctuation': /^\s*=\s*['"]|['"]\s*$/,
                        'attr-value': {
                            pattern: /.+/i,
                            inside: Prism.languages.css
                        }
                    },
                    alias: 'language-css'
                }
            }, Prism.languages.markup.tag);
        }

        /* **********************************************
         Begin prism-clike.js
         ********************************************** */

        Prism.languages.clike = {
            'comment': [
                {
                    pattern: /(^|[^\\])\/\*[\w\W]*?\*\//,
                    lookbehind: true
                },
                {
                    pattern: /(^|[^\\:])\/\/.*/,
                    lookbehind: true
                }
            ],
            'string': /("|')(\\(?:\r\n|[\s\S])|(?!\1)[^\\\r\n])*\1/,
            'class-name': {
                pattern: /((?:(?:class|interface|extends|implements|trait|instanceof|new)\s+)|(?:catch\s+\())[a-z0-9_\.\\]+/i,
                lookbehind: true,
                inside: {
                    punctuation: /(\.|\\)/
                }
            },
            'keyword': /\b(if|else|while|do|for|return|in|instanceof|function|new|try|throw|catch|finally|null|break|continue)\b/,
            'boolean': /\b(true|false)\b/,
            'function': /[a-z0-9_]+(?=\()/i,
            'number': /\b-?(0x[\dA-Fa-f]+|\d*\.?\d+([Ee]-?\d+)?)\b/,
            'operator': /[-+]{1,2}|!|<=?|>=?|={1,3}|&{1,2}|\|?\||\?|\*|\/|~|\^|%/,
            'punctuation': /[{}[\];(),.:]/
        };


        /* **********************************************
         Begin prism-javascript.js
         ********************************************** */

        Prism.languages.javascript = Prism.languages.extend('clike', {
            'keyword': /\b(as|async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|enum|export|extends|false|finally|for|from|function|get|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|set|static|super|switch|this|throw|true|try|typeof|var|void|while|with|yield)\b/,
            'number': /\b-?(0x[\dA-Fa-f]+|0b[01]+|0o[0-7]+|\d*\.?\d+([Ee][+-]?\d+)?|NaN|Infinity)\b/,
            'function': /(?!\d)[a-z0-9_$]+(?=\()/i
        });

        Prism.languages.insertBefore('javascript', 'keyword', {
            'regex': {
                pattern: /(^|[^/])\/(?!\/)(\[.+?]|\\.|[^/\\\r\n])+\/[gimyu]{0,5}(?=\s*($|[\r\n,.;})]))/,
                lookbehind: true
            }
        });

        Prism.languages.insertBefore('javascript', 'class-name', {
            'template-string': {
                pattern: /`(?:\\`|\\?[^`])*`/,
                inside: {
                    'interpolation': {
                        pattern: /\$\{[^}]+\}/,
                        inside: {
                            'interpolation-punctuation': {
                                pattern: /^\$\{|\}$/,
                                alias: 'punctuation'
                            },
                            rest: Prism.languages.javascript
                        }
                    },
                    'string': /[\s\S]+/
                }
            }
        });

        if (Prism.languages.markup) {
            Prism.languages.insertBefore('markup', 'tag', {
                'script': {
                    pattern: /<script[\w\W]*?>[\w\W]*?<\/script>/i,
                    inside: {
                        'tag': {
                            pattern: /<script[\w\W]*?>|<\/script>/i,
                            inside: Prism.languages.markup.tag.inside
                        },
                        rest: Prism.languages.javascript
                    },
                    alias: 'language-javascript'
                }
            });
        }


        /* **********************************************
         Begin prism-file-highlight.js
         ********************************************** */

        (function () {
            if (!self.Prism || !self.document || !document.querySelector) {
                return;
            }

            self.Prism.fileHighlight = function () {

                var Extensions = {
                    'js': 'javascript',
                    'html': 'markup',
                    'svg': 'markup',
                    'xml': 'markup',
                    'py': 'python',
                    'rb': 'ruby',
                    'ps1': 'powershell',
                    'psm1': 'powershell'
                };

                if (Array.prototype.forEach) { // Check to prevent error in IE8
                    Array.prototype.slice.call(document.querySelectorAll('pre[data-src]')).forEach(function (pre) {
                        var src = pre.getAttribute('data-src');

                        var language, parent = pre;
                        var lang = /\blang(?:uage)?-(?!\*)(\w+)\b/i;
                        while (parent && !lang.test(parent.className)) {
                            parent = parent.parentNode;
                        }

                        if (parent) {
                            language = (pre.className.match(lang) || [, ''])[1];
                        }

                        if (!language) {
                            var extension = (src.match(/\.(\w+)$/) || [, ''])[1];
                            language = Extensions[extension] || extension;
                        }

                        var code = document.createElement('code');
                        code.className = 'language-' + language;

                        pre.textContent = '';

                        code.textContent = 'Loading…';

                        pre.appendChild(code);

                        var xhr = new XMLHttpRequest();

                        xhr.open('GET', src, true);

                        xhr.onreadystatechange = function () {
                            if (xhr.readyState == 4) {

                                if (xhr.status < 400 && xhr.responseText) {
                                    code.textContent = xhr.responseText;

                                    Prism.highlightElement(code);
                                }
                                else if (xhr.status >= 400) {
                                    code.textContent = '✖ Error ' + xhr.status + ' while fetching file: ' + xhr.statusText;
                                }
                                else {
                                    code.textContent = '✖ Error: File does not exist or is empty';
                                }
                            }
                        };

                        xhr.send(null);
                    });
                }

            };

            self.Prism.fileHighlight();

        })();

    }, {}], 2: [function (require, module, exports) {
        module.exports = function () {
            return function (deck) {
                var backdrops;

                function createBackdropForSlide(slide) {
                    var backdropAttribute = slide.getAttribute('data-bespoke-backdrop');

                    if (backdropAttribute) {
                        var backdrop = document.createElement('div');
                        backdrop.className = backdropAttribute;
                        backdrop.classList.add('bespoke-backdrop');
                        deck.parent.appendChild(backdrop);
                        return backdrop;
                    }
                }

                function updateClasses(el) {
                    if (el) {
                        var index = backdrops.indexOf(el),
                            currentIndex = deck.slide();

                        removeClass(el, 'active');
                        removeClass(el, 'inactive');
                        removeClass(el, 'before');
                        removeClass(el, 'after');

                        if (index !== currentIndex) {
                            addClass(el, 'inactive');
                            addClass(el, index < currentIndex ? 'before' : 'after');
                        } else {
                            addClass(el, 'active');
                        }
                    }
                }

                function removeClass(el, className) {
                    el.classList.remove('bespoke-backdrop-' + className);
                }

                function addClass(el, className) {
                    el.classList.add('bespoke-backdrop-' + className);
                }

                backdrops = deck.slides
                    .map(createBackdropForSlide);

                deck.on('activate', function () {
                    backdrops.forEach(updateClasses);
                });
            };
        };

    }, {}], 3: [function (require, module, exports) {
        module.exports = function (options) {
            return function (deck) {
                var activeSlideIndex,
                    activeBulletIndex,

                    bullets = deck.slides.map(function (slide) {
                        return [].slice.call(slide.querySelectorAll((typeof options === 'string' ? options : '[data-bespoke-bullet]')), 0);
                    }),

                    next = function () {
                        var nextSlideIndex = activeSlideIndex + 1;

                        if (activeSlideHasBulletByOffset(1)) {
                            activateBullet(activeSlideIndex, activeBulletIndex + 1);
                            return false;
                        } else if (bullets[nextSlideIndex]) {
                            activateBullet(nextSlideIndex, 0);
                        }
                    },

                    prev = function () {
                        var prevSlideIndex = activeSlideIndex - 1;

                        if (activeSlideHasBulletByOffset(-1)) {
                            activateBullet(activeSlideIndex, activeBulletIndex - 1);
                            return false;
                        } else if (bullets[prevSlideIndex]) {
                            activateBullet(prevSlideIndex, bullets[prevSlideIndex].length - 1);
                        }
                    },

                    activateBullet = function (slideIndex, bulletIndex) {
                        activeSlideIndex = slideIndex;
                        activeBulletIndex = bulletIndex;

                        bullets.forEach(function (slide, s) {
                            slide.forEach(function (bullet, b) {
                                bullet.classList.add('bespoke-bullet');

                                if (s < slideIndex || s === slideIndex && b <= bulletIndex) {
                                    bullet.classList.add('bespoke-bullet-active');
                                    bullet.classList.remove('bespoke-bullet-inactive');
                                } else {
                                    bullet.classList.add('bespoke-bullet-inactive');
                                    bullet.classList.remove('bespoke-bullet-active');
                                }

                                if (s === slideIndex && b === bulletIndex) {
                                    bullet.classList.add('bespoke-bullet-current');
                                } else {
                                    bullet.classList.remove('bespoke-bullet-current');
                                }
                            });
                        });
                    },

                    activeSlideHasBulletByOffset = function (offset) {
                        return bullets[activeSlideIndex][activeBulletIndex + offset] !== undefined;
                    };

                deck.on('next', next);
                deck.on('prev', prev);

                deck.on('slide', function (e) {
                    activateBullet(e.index, 0);
                });

                activateBullet(0, 0);
            };
        };

    }, {}], 4: [function (require, module, exports) {
        module.exports = function () {
            return function (deck) {
                deck.slides.forEach(function (slide) {
                    slide.addEventListener('keydown', function (e) {
                        if (/INPUT|TEXTAREA|SELECT/.test(e.target.nodeName) || e.target.contentEditable === 'true') {
                            e.stopPropagation();
                        }
                    });
                });
            };
        };

    }, {}], 5: [function (require, module, exports) {
        module.exports = function () {
            return function (deck) {
                var parseHash = function () {
                    var hash = window.location.hash.slice(1),
                        slideNumberOrName = parseInt(hash, 10);

                    if (hash) {
                        if (slideNumberOrName) {
                            activateSlide(slideNumberOrName - 1);
                        } else {
                            deck.slides.forEach(function (slide, i) {
                                if (slide.getAttribute('data-bespoke-hash') === hash) {
                                    activateSlide(i);
                                }
                            });
                        }
                    }
                };

                var activateSlide = function (index) {
                    var indexToActivate = -1 < index && index < deck.slides.length ? index : 0;
                    if (indexToActivate !== deck.slide()) {
                        deck.slide(indexToActivate);
                    }
                };

                setTimeout(function () {
                    parseHash();

                    deck.on('activate', function (e) {
                        var slideName = e.slide.getAttribute('data-bespoke-hash');
                        window.location.hash = slideName || e.index + 1;
                    });

                    window.addEventListener('hashchange', parseHash);
                }, 0);
            };
        };

    }, {}], 6: [function (require, module, exports) {
        module.exports = function (options) {
            return function (deck) {
                var isHorizontal = options !== 'vertical';

                document.addEventListener('keydown', function (e) {
                    if (e.which == 34 || // PAGE DOWN
                        (e.which == 32 && !e.shiftKey) || // SPACE WITHOUT SHIFT
                        (isHorizontal && e.which == 39) || // RIGHT
                        (!isHorizontal && e.which == 40) // DOWN
                    ) {
                        deck.next();
                    }

                    if (e.which == 33 || // PAGE UP
                        (e.which == 32 && e.shiftKey) || // SPACE + SHIFT
                        (isHorizontal && e.which == 37) || // LEFT
                        (!isHorizontal && e.which == 38) // UP
                    ) {
                        deck.prev();
                    }
                });
            };
        };

    }, {}], 7: [function (require, module, exports) {
        module.exports = function (options) {
            return function (deck) {
                var progressParent = document.createElement('div'),
                    progressBar = document.createElement('div'),
                    prop = options === 'vertical' ? 'height' : 'width';

                progressParent.className = 'bespoke-progress-parent';
                progressBar.className = 'bespoke-progress-bar';
                progressParent.appendChild(progressBar);
                deck.parent.appendChild(progressParent);

                deck.on('activate', function (e) {
                    progressBar.style[prop] = (e.index * 100 / (deck.slides.length - 1)) + '%';
                });
            };
        };

    }, {}], 8: [function (require, module, exports) {
        module.exports = function (options) {
            return function (deck) {
                var parent = deck.parent,
                    firstSlide = deck.slides[0],
                    slideHeight = firstSlide.offsetHeight,
                    slideWidth = firstSlide.offsetWidth,
                    useZoom = options === 'zoom' || ('zoom' in parent.style && options !== 'transform'),

                    wrap = function (element) {
                        var wrapper = document.createElement('div');
                        wrapper.className = 'bespoke-scale-parent';
                        element.parentNode.insertBefore(wrapper, element);
                        wrapper.appendChild(element);
                        return wrapper;
                    },

                    elements = useZoom ? deck.slides : deck.slides.map(wrap),

                    transformProperty = (function (property) {
                        var prefixes = 'Moz Webkit O ms'.split(' ');
                        return prefixes.reduce(function (currentProperty, prefix) {
                            return prefix + property in parent.style ? prefix + property : currentProperty;
                        }, property.toLowerCase());
                    }('Transform')),

                    scale = useZoom ?
                        function (ratio, element) {
                            element.style.zoom = ratio;
                        } :
                        function (ratio, element) {
                            element.style[transformProperty] = 'scale(' + ratio + ')';
                        },

                    scaleAll = function () {
                        var xScale = parent.offsetWidth / slideWidth,
                            yScale = parent.offsetHeight / slideHeight;

                        elements.forEach(scale.bind(null, Math.min(xScale, yScale)));
                    };

                window.addEventListener('resize', scaleAll);
                scaleAll();
            };

        };

    }, {}], 9: [function (require, module, exports) {
        (function (global) {
            /*!
             * bespoke-theme-cube v2.0.1
             *
             * Copyright 2014, Mark Dalgleish
             * This content is released under the MIT license
             * http://mit-license.org/markdalgleish
             */

            !function (e) {
                if ("object" == typeof exports)module.exports = e(); else if ("function" == typeof define && define.amd)define(e); else {
                    var o;
                    "undefined" != typeof window ? o = window : "undefined" != typeof global ? o = global : "undefined" != typeof self && (o = self);
                    var f = o;
                    f = f.bespoke || (f.bespoke = {}), f = f.themes || (f.themes = {}), f.cube = e()
                }
            }(function () {
                var define, module, exports;
                return (function e(t, n, r) {
                    function s(o, u) {
                        if (!n[o]) {
                            if (!t[o]) {
                                var a = typeof require == "function" && require;
                                if (!u && a)return a(o, !0);
                                if (i)return i(o, !0);
                                throw new Error("Cannot find module '" + o + "'")
                            }
                            var f = n[o] = {exports: {}};
                            t[o][0].call(f.exports, function (e) {
                                var n = t[o][1][e];
                                return s(n ? n : e)
                            }, f, f.exports, e, t, n, r)
                        }
                        return n[o].exports
                    }

                    var i = typeof require == "function" && require;
                    for (var o = 0; o < r.length; o++)s(r[o]);
                    return s
                })({
                    1: [function (_dereq_, module, exports) {

                        var classes = _dereq_('bespoke-classes');
                        var insertCss = _dereq_('insert-css');

                        module.exports = function () {
                            var css = "*{-moz-box-sizing:border-box;box-sizing:border-box;margin:0;padding:0}@media print{*{-webkit-print-color-adjust:exact}}@page{size:landscape;margin:0}.bespoke-parent{-webkit-transition:background .6s ease;transition:background .6s ease;position:absolute;top:0;bottom:0;left:0;right:0;overflow:hidden}@media print{.bespoke-parent{overflow:visible;position:static}}.bespoke-theme-cube-slide-parent{position:absolute;top:0;left:0;right:0;bottom:0;-webkit-perspective:600px;perspective:600px;pointer-events:none}.bespoke-slide{pointer-events:auto;-webkit-transition:-webkit-transform .6s ease,opacity .6s ease,background .6s ease;transition:transform .6s ease,opacity .6s ease,background .6s ease;-webkit-transform-origin:50% 50% 0;transform-origin:50% 50% 0;-webkit-backface-visibility:hidden;backface-visibility:hidden;display:-webkit-box;display:-webkit-flex;display:-ms-flexbox;display:flex;-webkit-box-orient:vertical;-webkit-box-direction:normal;-webkit-flex-direction:column;-ms-flex-direction:column;flex-direction:column;-webkit-box-pack:center;-webkit-justify-content:center;-ms-flex-pack:center;justify-content:center;-webkit-box-align:center;-webkit-align-items:center;-ms-flex-align:center;align-items:center;text-align:center;width:640px;height:480px;position:absolute;top:50%;margin-top:-240px;left:50%;margin-left:-320px;background:#eaeaea;padding:40px;border-radius:0}@media print{.bespoke-slide{zoom:1!important;height:743px;width:100%;page-break-before:always;position:static;margin:0;-webkit-transition:none;transition:none}}.bespoke-before{-webkit-transform:translateX(100px)translateX(-320px)rotateY(-90deg)translateX(-320px);transform:translateX(100px)translateX(-320px)rotateY(-90deg)translateX(-320px)}@media print{.bespoke-before{-webkit-transform:none;transform:none}}.bespoke-after{-webkit-transform:translateX(-100px)translateX(320px)rotateY(90deg)translateX(320px);transform:translateX(-100px)translateX(320px)rotateY(90deg)translateX(320px)}@media print{.bespoke-after{-webkit-transform:none;transform:none}}.bespoke-inactive{opacity:0;pointer-events:none}@media print{.bespoke-inactive{opacity:1}}.bespoke-active{opacity:1}.bespoke-bullet{-webkit-transition:all .3s ease;transition:all .3s ease}@media print{.bespoke-bullet{-webkit-transition:none;transition:none}}.bespoke-bullet-inactive{opacity:0}li.bespoke-bullet-inactive{-webkit-transform:translateX(16px);transform:translateX(16px)}@media print{li.bespoke-bullet-inactive{-webkit-transform:none;transform:none}}@media print{.bespoke-bullet-inactive{opacity:1}}.bespoke-bullet-active{opacity:1}.bespoke-scale-parent{-webkit-perspective:600px;perspective:600px;position:absolute;top:0;left:0;right:0;bottom:0;pointer-events:none}.bespoke-scale-parent .bespoke-active{pointer-events:auto}@media print{.bespoke-scale-parent{-webkit-transform:none!important;transform:none!important}}.bespoke-progress-parent{position:absolute;top:0;left:0;right:0;height:2px}@media only screen and (min-width:1366px){.bespoke-progress-parent{height:4px}}@media print{.bespoke-progress-parent{display:none}}.bespoke-progress-bar{-webkit-transition:width .6s ease;transition:width .6s ease;position:absolute;height:100%;background:#0089f3;border-radius:0 4px 4px 0}.emphatic{background:#eaeaea}.bespoke-backdrop{position:absolute;top:0;left:0;right:0;bottom:0;-webkit-transform:translateZ(0);transform:translateZ(0);-webkit-transition:opacity .6s ease;transition:opacity .6s ease;opacity:0;z-index:-1}.bespoke-backdrop-active{opacity:1}pre{padding:26px!important;border-radius:8px}body{font-family:helvetica,arial,sans-serif;font-size:18px;color:#404040}h1{font-size:72px;line-height:82px;letter-spacing:-2px;margin-bottom:16px}h2{font-size:42px;letter-spacing:-1px;margin-bottom:8px}h3{font-size:24px;font-weight:400;margin-bottom:24px;color:#606060}hr{visibility:hidden;height:20px}ul{list-style:none}li{margin-bottom:12px}p{margin:0 100px 12px;line-height:22px}a{color:#0089f3;text-decoration:none}";
                            insertCss(css, {prepend: true});

                            return function (deck) {
                                classes()(deck);

                                var wrap = function (element) {
                                    var wrapper = document.createElement('div');
                                    wrapper.className = 'bespoke-theme-cube-slide-parent';
                                    element.parentNode.insertBefore(wrapper, element);
                                    wrapper.appendChild(element);
                                };

                                deck.slides.forEach(wrap);
                            };
                        };

                    }, {"bespoke-classes": 2, "insert-css": 3}], 2: [function (_dereq_, module, exports) {
                        module.exports = function () {
                            return function (deck) {
                                var addClass = function (el, cls) {
                                        el.classList.add('bespoke-' + cls);
                                    },

                                    removeClass = function (el, cls) {
                                        el.className = el.className
                                            .replace(new RegExp('bespoke-' + cls + '(\\s|$)', 'g'), ' ')
                                            .trim();
                                    },

                                    deactivate = function (el, index) {
                                        var activeSlide = deck.slides[deck.slide()],
                                            offset = index - deck.slide(),
                                            offsetClass = offset > 0 ? 'after' : 'before';

                                        ['before(-\\d+)?', 'after(-\\d+)?', 'active', 'inactive'].map(removeClass.bind(null, el));

                                        if (el !== activeSlide) {
                                            ['inactive', offsetClass, offsetClass + '-' + Math.abs(offset)].map(addClass.bind(null, el));
                                        }
                                    };

                                addClass(deck.parent, 'parent');
                                deck.slides.map(function (el) {
                                    addClass(el, 'slide');
                                });

                                deck.on('activate', function (e) {
                                    deck.slides.map(deactivate);
                                    addClass(e.slide, 'active');
                                    removeClass(e.slide, 'inactive');
                                });
                            };
                        };

                    }, {}], 3: [function (_dereq_, module, exports) {
                        var inserted = {};

                        module.exports = function (css, options) {
                            if (inserted[css]) return;
                            inserted[css] = true;

                            var elem = document.createElement('style');
                            elem.setAttribute('type', 'text/css');

                            if ('textContent' in elem) {
                                elem.textContent = css;
                            } else {
                                elem.styleSheet.cssText = css;
                            }

                            var head = document.getElementsByTagName('head')[0];
                            if (options && options.prepend) {
                                head.insertBefore(elem, head.childNodes[0]);
                            } else {
                                head.appendChild(elem);
                            }
                        };

                    }, {}]
                }, {}, [1])
                (1)
            });
        }).call(this, typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : {})
    }, {}], 10: [function (require, module, exports) {
        module.exports = function (options) {
            return function (deck) {
                var axis = options == 'vertical' ? 'Y' : 'X',
                    startPosition,
                    delta;

                deck.parent.addEventListener('touchstart', function (e) {
                    if (e.touches.length == 1) {
                        startPosition = e.touches[0]['page' + axis];
                        delta = 0;
                    }
                });

                deck.parent.addEventListener('touchmove', function (e) {
                    if (e.touches.length == 1) {
                        e.preventDefault();
                        delta = e.touches[0]['page' + axis] - startPosition;
                    }
                });

                deck.parent.addEventListener('touchend', function () {
                    if (Math.abs(delta) > 50) {
                        deck[delta > 0 ? 'prev' : 'next']();
                    }
                });
            };
        };

    }, {}], 11: [function (require, module, exports) {
        var from = function (selectorOrElement, plugins) {
            var parent = selectorOrElement.nodeType === 1 ? selectorOrElement : document.querySelector(selectorOrElement),
                slides = [].filter.call(parent.children, function (el) {
                    return el.nodeName !== 'SCRIPT';
                }),
                activeSlide = slides[0],
                listeners = {},

                activate = function (index, customData) {
                    if (!slides[index]) {
                        return;
                    }

                    fire('deactivate', createEventData(activeSlide, customData));
                    activeSlide = slides[index];
                    fire('activate', createEventData(activeSlide, customData));
                },

                slide = function (index, customData) {
                    if (arguments.length) {
                        fire('slide', createEventData(slides[index], customData)) && activate(index, customData);
                    } else {
                        return slides.indexOf(activeSlide);
                    }
                },

                step = function (offset, customData) {
                    var slideIndex = slides.indexOf(activeSlide) + offset;

                    fire(offset > 0 ? 'next' : 'prev', createEventData(activeSlide, customData)) && activate(slideIndex, customData);
                },

                on = function (eventName, callback) {
                    (listeners[eventName] || (listeners[eventName] = [])).push(callback);

                    return function () {
                        listeners[eventName] = listeners[eventName].filter(function (listener) {
                            return listener !== callback;
                        });
                    };
                },

                fire = function (eventName, eventData) {
                    return (listeners[eventName] || [])
                        .reduce(function (notCancelled, callback) {
                            return notCancelled && callback(eventData) !== false;
                        }, true);
                },

                createEventData = function (el, eventData) {
                    eventData = eventData || {};
                    eventData.index = slides.indexOf(el);
                    eventData.slide = el;
                    return eventData;
                },

                deck = {
                    on: on,
                    fire: fire,
                    slide: slide,
                    next: step.bind(null, 1),
                    prev: step.bind(null, -1),
                    parent: parent,
                    slides: slides
                };

            (plugins || []).forEach(function (plugin) {
                plugin(deck);
            });

            activate(0);

            return deck;
        };

        module.exports = {
            from: from
        };

    }, {}], 12: [function (require, module, exports) {
// Require Node modules in the browser thanks to Browserify: http://browserify.org
        var bespoke = require('bespoke'),
            cube = require('bespoke-theme-cube'),
            keys = require('bespoke-keys'),
            touch = require('bespoke-touch'),
            bullets = require('bespoke-bullets'),
            backdrop = require('bespoke-backdrop'),
            scale = require('bespoke-scale'),
            hash = require('bespoke-hash'),
            progress = require('bespoke-progress'),
            forms = require('bespoke-forms');

// Bespoke.js
        bespoke.from('article', [
            cube(),
            keys(),
            touch(),
            bullets('li, .bullet'),
            backdrop(),
            scale(),
            hash(),
            progress(),
            forms()
        ]);

// Prism syntax highlighting
// This is actually loaded from "bower_components" thanks to
// debowerify: https://github.com/eugeneware/debowerify
        require("./..\\..\\bower_components\\prism\\prism.js");


    }, {
        "./..\\..\\bower_components\\prism\\prism.js": 1,
        "bespoke": 11,
        "bespoke-backdrop": 2,
        "bespoke-bullets": 3,
        "bespoke-forms": 4,
        "bespoke-hash": 5,
        "bespoke-keys": 6,
        "bespoke-progress": 7,
        "bespoke-scale": 8,
        "bespoke-theme-cube": 9,
        "bespoke-touch": 10
    }]
}, {}, [12])
//# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIkM6XFxVc2Vyc1xcdXN0YVxcRHJvcGJveFxcQXNjaWlkb2NGWFxcYmVza29cXG5vZGVfbW9kdWxlc1xcZ3VscC1icm93c2VyaWZ5XFxub2RlX21vZHVsZXNcXGJyb3dzZXJpZnlcXG5vZGVfbW9kdWxlc1xcYnJvd3Nlci1wYWNrXFxfcHJlbHVkZS5qcyIsIkM6L1VzZXJzL3VzdGEvRHJvcGJveC9Bc2NpaWRvY0ZYL2Jlc2tvL2Jvd2VyX2NvbXBvbmVudHMvcHJpc20vcHJpc20uanMiLCJDOi9Vc2Vycy91c3RhL0Ryb3Bib3gvQXNjaWlkb2NGWC9iZXNrby9ub2RlX21vZHVsZXMvYmVzcG9rZS1iYWNrZHJvcC9saWIvYmVzcG9rZS1iYWNrZHJvcC5qcyIsIkM6L1VzZXJzL3VzdGEvRHJvcGJveC9Bc2NpaWRvY0ZYL2Jlc2tvL25vZGVfbW9kdWxlcy9iZXNwb2tlLWJ1bGxldHMvbGliL2Jlc3Bva2UtYnVsbGV0cy5qcyIsIkM6L1VzZXJzL3VzdGEvRHJvcGJveC9Bc2NpaWRvY0ZYL2Jlc2tvL25vZGVfbW9kdWxlcy9iZXNwb2tlLWZvcm1zL2xpYi9iZXNwb2tlLWZvcm1zLmpzIiwiQzovVXNlcnMvdXN0YS9Ecm9wYm94L0FzY2lpZG9jRlgvYmVza28vbm9kZV9tb2R1bGVzL2Jlc3Bva2UtaGFzaC9saWIvYmVzcG9rZS1oYXNoLmpzIiwiQzovVXNlcnMvdXN0YS9Ecm9wYm94L0FzY2lpZG9jRlgvYmVza28vbm9kZV9tb2R1bGVzL2Jlc3Bva2Uta2V5cy9saWIvYmVzcG9rZS1rZXlzLmpzIiwiQzovVXNlcnMvdXN0YS9Ecm9wYm94L0FzY2lpZG9jRlgvYmVza28vbm9kZV9tb2R1bGVzL2Jlc3Bva2UtcHJvZ3Jlc3MvbGliL2Jlc3Bva2UtcHJvZ3Jlc3MuanMiLCJDOi9Vc2Vycy91c3RhL0Ryb3Bib3gvQXNjaWlkb2NGWC9iZXNrby9ub2RlX21vZHVsZXMvYmVzcG9rZS1zY2FsZS9saWIvYmVzcG9rZS1zY2FsZS5qcyIsIkM6L1VzZXJzL3VzdGEvRHJvcGJveC9Bc2NpaWRvY0ZYL2Jlc2tvL25vZGVfbW9kdWxlcy9iZXNwb2tlLXRoZW1lLWN1YmUvZGlzdC9iZXNwb2tlLXRoZW1lLWN1YmUuanMiLCJDOi9Vc2Vycy91c3RhL0Ryb3Bib3gvQXNjaWlkb2NGWC9iZXNrby9ub2RlX21vZHVsZXMvYmVzcG9rZS10b3VjaC9saWIvYmVzcG9rZS10b3VjaC5qcyIsIkM6L1VzZXJzL3VzdGEvRHJvcGJveC9Bc2NpaWRvY0ZYL2Jlc2tvL25vZGVfbW9kdWxlcy9iZXNwb2tlL2xpYi9iZXNwb2tlLmpzIiwiQzovVXNlcnMvdXN0YS9Ecm9wYm94L0FzY2lpZG9jRlgvYmVza28vc3JjL3NjcmlwdHMvZmFrZV8zNTljZDY5My5qcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFBQTtBQ0FBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQ2pzQkE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDbkRBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDdEVBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTs7QUNYQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDdENBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDbkJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDaEJBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzdDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FDL0ZBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzNCQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBOztBQzVFQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQSIsImZpbGUiOiJnZW5lcmF0ZWQuanMiLCJzb3VyY2VSb290IjoiIiwic291cmNlc0NvbnRlbnQiOlsiKGZ1bmN0aW9uIGUodCxuLHIpe2Z1bmN0aW9uIHMobyx1KXtpZighbltvXSl7aWYoIXRbb10pe3ZhciBhPXR5cGVvZiByZXF1aXJlPT1cImZ1bmN0aW9uXCImJnJlcXVpcmU7aWYoIXUmJmEpcmV0dXJuIGEobywhMCk7aWYoaSlyZXR1cm4gaShvLCEwKTt0aHJvdyBuZXcgRXJyb3IoXCJDYW5ub3QgZmluZCBtb2R1bGUgJ1wiK28rXCInXCIpfXZhciBmPW5bb109e2V4cG9ydHM6e319O3Rbb11bMF0uY2FsbChmLmV4cG9ydHMsZnVuY3Rpb24oZSl7dmFyIG49dFtvXVsxXVtlXTtyZXR1cm4gcyhuP246ZSl9LGYsZi5leHBvcnRzLGUsdCxuLHIpfXJldHVybiBuW29dLmV4cG9ydHN9dmFyIGk9dHlwZW9mIHJlcXVpcmU9PVwiZnVuY3Rpb25cIiYmcmVxdWlyZTtmb3IodmFyIG89MDtvPHIubGVuZ3RoO28rKylzKHJbb10pO3JldHVybiBzfSkiLCJcclxuLyogKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKlxyXG4gICAgIEJlZ2luIHByaXNtLWNvcmUuanNcclxuKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiAqL1xyXG5cclxudmFyIF9zZWxmID0gKHR5cGVvZiB3aW5kb3cgIT09ICd1bmRlZmluZWQnKVxyXG5cdD8gd2luZG93ICAgLy8gaWYgaW4gYnJvd3NlclxyXG5cdDogKFxyXG5cdFx0KHR5cGVvZiBXb3JrZXJHbG9iYWxTY29wZSAhPT0gJ3VuZGVmaW5lZCcgJiYgc2VsZiBpbnN0YW5jZW9mIFdvcmtlckdsb2JhbFNjb3BlKVxyXG5cdFx0PyBzZWxmIC8vIGlmIGluIHdvcmtlclxyXG5cdFx0OiB7fSAgIC8vIGlmIGluIG5vZGUganNcclxuXHQpO1xyXG5cclxuLyoqXHJcbiAqIFByaXNtOiBMaWdodHdlaWdodCwgcm9idXN0LCBlbGVnYW50IHN5bnRheCBoaWdobGlnaHRpbmdcclxuICogTUlUIGxpY2Vuc2UgaHR0cDovL3d3dy5vcGVuc291cmNlLm9yZy9saWNlbnNlcy9taXQtbGljZW5zZS5waHAvXHJcbiAqIEBhdXRob3IgTGVhIFZlcm91IGh0dHA6Ly9sZWEudmVyb3UubWVcclxuICovXHJcblxyXG52YXIgUHJpc20gPSAoZnVuY3Rpb24oKXtcclxuXHJcbi8vIFByaXZhdGUgaGVscGVyIHZhcnNcclxudmFyIGxhbmcgPSAvXFxibGFuZyg/OnVhZ2UpPy0oPyFcXCopKFxcdyspXFxiL2k7XHJcblxyXG52YXIgXyA9IF9zZWxmLlByaXNtID0ge1xyXG5cdHV0aWw6IHtcclxuXHRcdGVuY29kZTogZnVuY3Rpb24gKHRva2Vucykge1xyXG5cdFx0XHRpZiAodG9rZW5zIGluc3RhbmNlb2YgVG9rZW4pIHtcclxuXHRcdFx0XHRyZXR1cm4gbmV3IFRva2VuKHRva2Vucy50eXBlLCBfLnV0aWwuZW5jb2RlKHRva2Vucy5jb250ZW50KSwgdG9rZW5zLmFsaWFzKTtcclxuXHRcdFx0fSBlbHNlIGlmIChfLnV0aWwudHlwZSh0b2tlbnMpID09PSAnQXJyYXknKSB7XHJcblx0XHRcdFx0cmV0dXJuIHRva2Vucy5tYXAoXy51dGlsLmVuY29kZSk7XHJcblx0XHRcdH0gZWxzZSB7XHJcblx0XHRcdFx0cmV0dXJuIHRva2Vucy5yZXBsYWNlKC8mL2csICcmYW1wOycpLnJlcGxhY2UoLzwvZywgJyZsdDsnKS5yZXBsYWNlKC9cXHUwMGEwL2csICcgJyk7XHJcblx0XHRcdH1cclxuXHRcdH0sXHJcblxyXG5cdFx0dHlwZTogZnVuY3Rpb24gKG8pIHtcclxuXHRcdFx0cmV0dXJuIE9iamVjdC5wcm90b3R5cGUudG9TdHJpbmcuY2FsbChvKS5tYXRjaCgvXFxbb2JqZWN0IChcXHcrKVxcXS8pWzFdO1xyXG5cdFx0fSxcclxuXHJcblx0XHQvLyBEZWVwIGNsb25lIGEgbGFuZ3VhZ2UgZGVmaW5pdGlvbiAoZS5nLiB0byBleHRlbmQgaXQpXHJcblx0XHRjbG9uZTogZnVuY3Rpb24gKG8pIHtcclxuXHRcdFx0dmFyIHR5cGUgPSBfLnV0aWwudHlwZShvKTtcclxuXHJcblx0XHRcdHN3aXRjaCAodHlwZSkge1xyXG5cdFx0XHRcdGNhc2UgJ09iamVjdCc6XHJcblx0XHRcdFx0XHR2YXIgY2xvbmUgPSB7fTtcclxuXHJcblx0XHRcdFx0XHRmb3IgKHZhciBrZXkgaW4gbykge1xyXG5cdFx0XHRcdFx0XHRpZiAoby5oYXNPd25Qcm9wZXJ0eShrZXkpKSB7XHJcblx0XHRcdFx0XHRcdFx0Y2xvbmVba2V5XSA9IF8udXRpbC5jbG9uZShvW2tleV0pO1xyXG5cdFx0XHRcdFx0XHR9XHJcblx0XHRcdFx0XHR9XHJcblxyXG5cdFx0XHRcdFx0cmV0dXJuIGNsb25lO1xyXG5cclxuXHRcdFx0XHRjYXNlICdBcnJheSc6XHJcblx0XHRcdFx0XHQvLyBDaGVjayBmb3IgZXhpc3RlbmNlIGZvciBJRThcclxuXHRcdFx0XHRcdHJldHVybiBvLm1hcCAmJiBvLm1hcChmdW5jdGlvbih2KSB7IHJldHVybiBfLnV0aWwuY2xvbmUodik7IH0pO1xyXG5cdFx0XHR9XHJcblxyXG5cdFx0XHRyZXR1cm4gbztcclxuXHRcdH1cclxuXHR9LFxyXG5cclxuXHRsYW5ndWFnZXM6IHtcclxuXHRcdGV4dGVuZDogZnVuY3Rpb24gKGlkLCByZWRlZikge1xyXG5cdFx0XHR2YXIgbGFuZyA9IF8udXRpbC5jbG9uZShfLmxhbmd1YWdlc1tpZF0pO1xyXG5cclxuXHRcdFx0Zm9yICh2YXIga2V5IGluIHJlZGVmKSB7XHJcblx0XHRcdFx0bGFuZ1trZXldID0gcmVkZWZba2V5XTtcclxuXHRcdFx0fVxyXG5cclxuXHRcdFx0cmV0dXJuIGxhbmc7XHJcblx0XHR9LFxyXG5cclxuXHRcdC8qKlxyXG5cdFx0ICogSW5zZXJ0IGEgdG9rZW4gYmVmb3JlIGFub3RoZXIgdG9rZW4gaW4gYSBsYW5ndWFnZSBsaXRlcmFsXHJcblx0XHQgKiBBcyB0aGlzIG5lZWRzIHRvIHJlY3JlYXRlIHRoZSBvYmplY3QgKHdlIGNhbm5vdCBhY3R1YWxseSBpbnNlcnQgYmVmb3JlIGtleXMgaW4gb2JqZWN0IGxpdGVyYWxzKSxcclxuXHRcdCAqIHdlIGNhbm5vdCBqdXN0IHByb3ZpZGUgYW4gb2JqZWN0LCB3ZSBuZWVkIGFub2JqZWN0IGFuZCBhIGtleS5cclxuXHRcdCAqIEBwYXJhbSBpbnNpZGUgVGhlIGtleSAob3IgbGFuZ3VhZ2UgaWQpIG9mIHRoZSBwYXJlbnRcclxuXHRcdCAqIEBwYXJhbSBiZWZvcmUgVGhlIGtleSB0byBpbnNlcnQgYmVmb3JlLiBJZiBub3QgcHJvdmlkZWQsIHRoZSBmdW5jdGlvbiBhcHBlbmRzIGluc3RlYWQuXHJcblx0XHQgKiBAcGFyYW0gaW5zZXJ0IE9iamVjdCB3aXRoIHRoZSBrZXkvdmFsdWUgcGFpcnMgdG8gaW5zZXJ0XHJcblx0XHQgKiBAcGFyYW0gcm9vdCBUaGUgb2JqZWN0IHRoYXQgY29udGFpbnMgYGluc2lkZWAuIElmIGVxdWFsIHRvIFByaXNtLmxhbmd1YWdlcywgaXQgY2FuIGJlIG9taXR0ZWQuXHJcblx0XHQgKi9cclxuXHRcdGluc2VydEJlZm9yZTogZnVuY3Rpb24gKGluc2lkZSwgYmVmb3JlLCBpbnNlcnQsIHJvb3QpIHtcclxuXHRcdFx0cm9vdCA9IHJvb3QgfHwgXy5sYW5ndWFnZXM7XHJcblx0XHRcdHZhciBncmFtbWFyID0gcm9vdFtpbnNpZGVdO1xyXG5cdFx0XHRcclxuXHRcdFx0aWYgKGFyZ3VtZW50cy5sZW5ndGggPT0gMikge1xyXG5cdFx0XHRcdGluc2VydCA9IGFyZ3VtZW50c1sxXTtcclxuXHRcdFx0XHRcclxuXHRcdFx0XHRmb3IgKHZhciBuZXdUb2tlbiBpbiBpbnNlcnQpIHtcclxuXHRcdFx0XHRcdGlmIChpbnNlcnQuaGFzT3duUHJvcGVydHkobmV3VG9rZW4pKSB7XHJcblx0XHRcdFx0XHRcdGdyYW1tYXJbbmV3VG9rZW5dID0gaW5zZXJ0W25ld1Rva2VuXTtcclxuXHRcdFx0XHRcdH1cclxuXHRcdFx0XHR9XHJcblx0XHRcdFx0XHJcblx0XHRcdFx0cmV0dXJuIGdyYW1tYXI7XHJcblx0XHRcdH1cclxuXHRcdFx0XHJcblx0XHRcdHZhciByZXQgPSB7fTtcclxuXHJcblx0XHRcdGZvciAodmFyIHRva2VuIGluIGdyYW1tYXIpIHtcclxuXHJcblx0XHRcdFx0aWYgKGdyYW1tYXIuaGFzT3duUHJvcGVydHkodG9rZW4pKSB7XHJcblxyXG5cdFx0XHRcdFx0aWYgKHRva2VuID09IGJlZm9yZSkge1xyXG5cclxuXHRcdFx0XHRcdFx0Zm9yICh2YXIgbmV3VG9rZW4gaW4gaW5zZXJ0KSB7XHJcblxyXG5cdFx0XHRcdFx0XHRcdGlmIChpbnNlcnQuaGFzT3duUHJvcGVydHkobmV3VG9rZW4pKSB7XHJcblx0XHRcdFx0XHRcdFx0XHRyZXRbbmV3VG9rZW5dID0gaW5zZXJ0W25ld1Rva2VuXTtcclxuXHRcdFx0XHRcdFx0XHR9XHJcblx0XHRcdFx0XHRcdH1cclxuXHRcdFx0XHRcdH1cclxuXHJcblx0XHRcdFx0XHRyZXRbdG9rZW5dID0gZ3JhbW1hclt0b2tlbl07XHJcblx0XHRcdFx0fVxyXG5cdFx0XHR9XHJcblx0XHRcdFxyXG5cdFx0XHQvLyBVcGRhdGUgcmVmZXJlbmNlcyBpbiBvdGhlciBsYW5ndWFnZSBkZWZpbml0aW9uc1xyXG5cdFx0XHRfLmxhbmd1YWdlcy5ERlMoXy5sYW5ndWFnZXMsIGZ1bmN0aW9uKGtleSwgdmFsdWUpIHtcclxuXHRcdFx0XHRpZiAodmFsdWUgPT09IHJvb3RbaW5zaWRlXSAmJiBrZXkgIT0gaW5zaWRlKSB7XHJcblx0XHRcdFx0XHR0aGlzW2tleV0gPSByZXQ7XHJcblx0XHRcdFx0fVxyXG5cdFx0XHR9KTtcclxuXHJcblx0XHRcdHJldHVybiByb290W2luc2lkZV0gPSByZXQ7XHJcblx0XHR9LFxyXG5cclxuXHRcdC8vIFRyYXZlcnNlIGEgbGFuZ3VhZ2UgZGVmaW5pdGlvbiB3aXRoIERlcHRoIEZpcnN0IFNlYXJjaFxyXG5cdFx0REZTOiBmdW5jdGlvbihvLCBjYWxsYmFjaywgdHlwZSkge1xyXG5cdFx0XHRmb3IgKHZhciBpIGluIG8pIHtcclxuXHRcdFx0XHRpZiAoby5oYXNPd25Qcm9wZXJ0eShpKSkge1xyXG5cdFx0XHRcdFx0Y2FsbGJhY2suY2FsbChvLCBpLCBvW2ldLCB0eXBlIHx8IGkpO1xyXG5cclxuXHRcdFx0XHRcdGlmIChfLnV0aWwudHlwZShvW2ldKSA9PT0gJ09iamVjdCcpIHtcclxuXHRcdFx0XHRcdFx0Xy5sYW5ndWFnZXMuREZTKG9baV0sIGNhbGxiYWNrKTtcclxuXHRcdFx0XHRcdH1cclxuXHRcdFx0XHRcdGVsc2UgaWYgKF8udXRpbC50eXBlKG9baV0pID09PSAnQXJyYXknKSB7XHJcblx0XHRcdFx0XHRcdF8ubGFuZ3VhZ2VzLkRGUyhvW2ldLCBjYWxsYmFjaywgaSk7XHJcblx0XHRcdFx0XHR9XHJcblx0XHRcdFx0fVxyXG5cdFx0XHR9XHJcblx0XHR9XHJcblx0fSxcclxuXHJcblx0aGlnaGxpZ2h0QWxsOiBmdW5jdGlvbihhc3luYywgY2FsbGJhY2spIHtcclxuXHRcdHZhciBlbGVtZW50cyA9IGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3JBbGwoJ2NvZGVbY2xhc3MqPVwibGFuZ3VhZ2UtXCJdLCBbY2xhc3MqPVwibGFuZ3VhZ2UtXCJdIGNvZGUsIGNvZGVbY2xhc3MqPVwibGFuZy1cIl0sIFtjbGFzcyo9XCJsYW5nLVwiXSBjb2RlJyk7XHJcblxyXG5cdFx0Zm9yICh2YXIgaT0wLCBlbGVtZW50OyBlbGVtZW50ID0gZWxlbWVudHNbaSsrXTspIHtcclxuXHRcdFx0Xy5oaWdobGlnaHRFbGVtZW50KGVsZW1lbnQsIGFzeW5jID09PSB0cnVlLCBjYWxsYmFjayk7XHJcblx0XHR9XHJcblx0fSxcclxuXHJcblx0aGlnaGxpZ2h0RWxlbWVudDogZnVuY3Rpb24oZWxlbWVudCwgYXN5bmMsIGNhbGxiYWNrKSB7XHJcblx0XHQvLyBGaW5kIGxhbmd1YWdlXHJcblx0XHR2YXIgbGFuZ3VhZ2UsIGdyYW1tYXIsIHBhcmVudCA9IGVsZW1lbnQ7XHJcblxyXG5cdFx0d2hpbGUgKHBhcmVudCAmJiAhbGFuZy50ZXN0KHBhcmVudC5jbGFzc05hbWUpKSB7XHJcblx0XHRcdHBhcmVudCA9IHBhcmVudC5wYXJlbnROb2RlO1xyXG5cdFx0fVxyXG5cclxuXHRcdGlmIChwYXJlbnQpIHtcclxuXHRcdFx0bGFuZ3VhZ2UgPSAocGFyZW50LmNsYXNzTmFtZS5tYXRjaChsYW5nKSB8fCBbLCcnXSlbMV07XHJcblx0XHRcdGdyYW1tYXIgPSBfLmxhbmd1YWdlc1tsYW5ndWFnZV07XHJcblx0XHR9XHJcblxyXG5cdFx0Ly8gU2V0IGxhbmd1YWdlIG9uIHRoZSBlbGVtZW50LCBpZiBub3QgcHJlc2VudFxyXG5cdFx0ZWxlbWVudC5jbGFzc05hbWUgPSBlbGVtZW50LmNsYXNzTmFtZS5yZXBsYWNlKGxhbmcsICcnKS5yZXBsYWNlKC9cXHMrL2csICcgJykgKyAnIGxhbmd1YWdlLScgKyBsYW5ndWFnZTtcclxuXHJcblx0XHQvLyBTZXQgbGFuZ3VhZ2Ugb24gdGhlIHBhcmVudCwgZm9yIHN0eWxpbmdcclxuXHRcdHBhcmVudCA9IGVsZW1lbnQucGFyZW50Tm9kZTtcclxuXHJcblx0XHRpZiAoL3ByZS9pLnRlc3QocGFyZW50Lm5vZGVOYW1lKSkge1xyXG5cdFx0XHRwYXJlbnQuY2xhc3NOYW1lID0gcGFyZW50LmNsYXNzTmFtZS5yZXBsYWNlKGxhbmcsICcnKS5yZXBsYWNlKC9cXHMrL2csICcgJykgKyAnIGxhbmd1YWdlLScgKyBsYW5ndWFnZTtcclxuXHRcdH1cclxuXHJcblx0XHR2YXIgY29kZSA9IGVsZW1lbnQudGV4dENvbnRlbnQ7XHJcblxyXG5cdFx0dmFyIGVudiA9IHtcclxuXHRcdFx0ZWxlbWVudDogZWxlbWVudCxcclxuXHRcdFx0bGFuZ3VhZ2U6IGxhbmd1YWdlLFxyXG5cdFx0XHRncmFtbWFyOiBncmFtbWFyLFxyXG5cdFx0XHRjb2RlOiBjb2RlXHJcblx0XHR9O1xyXG5cclxuXHRcdGlmKGNvZGUpIHtcclxuXHRcdFx0ZW52LmNvZGUgPSBjb2RlLnJlcGxhY2UoL14oPzpcXHI/XFxufFxccikvLCAnJyk7XHJcblx0XHR9XHJcblxyXG5cdFx0aWYgKCFjb2RlIHx8ICFncmFtbWFyKSB7XHJcblx0XHRcdF8uaG9va3MucnVuKCdjb21wbGV0ZScsIGVudik7XHJcblx0XHRcdHJldHVybjtcclxuXHRcdH1cclxuXHJcblx0XHRfLmhvb2tzLnJ1bignYmVmb3JlLWhpZ2hsaWdodCcsIGVudik7XHJcblxyXG5cdFx0aWYgKGFzeW5jICYmIF9zZWxmLldvcmtlcikge1xyXG5cdFx0XHR2YXIgd29ya2VyID0gbmV3IFdvcmtlcihfLmZpbGVuYW1lKTtcclxuXHJcblx0XHRcdHdvcmtlci5vbm1lc3NhZ2UgPSBmdW5jdGlvbihldnQpIHtcclxuXHRcdFx0XHRlbnYuaGlnaGxpZ2h0ZWRDb2RlID0gVG9rZW4uc3RyaW5naWZ5KEpTT04ucGFyc2UoZXZ0LmRhdGEpLCBsYW5ndWFnZSk7XHJcblxyXG5cdFx0XHRcdF8uaG9va3MucnVuKCdiZWZvcmUtaW5zZXJ0JywgZW52KTtcclxuXHJcblx0XHRcdFx0ZW52LmVsZW1lbnQuaW5uZXJIVE1MID0gZW52LmhpZ2hsaWdodGVkQ29kZTtcclxuXHJcblx0XHRcdFx0Y2FsbGJhY2sgJiYgY2FsbGJhY2suY2FsbChlbnYuZWxlbWVudCk7XHJcblx0XHRcdFx0Xy5ob29rcy5ydW4oJ2FmdGVyLWhpZ2hsaWdodCcsIGVudik7XHJcblx0XHRcdFx0Xy5ob29rcy5ydW4oJ2NvbXBsZXRlJywgZW52KTtcclxuXHRcdFx0fTtcclxuXHJcblx0XHRcdHdvcmtlci5wb3N0TWVzc2FnZShKU09OLnN0cmluZ2lmeSh7XHJcblx0XHRcdFx0bGFuZ3VhZ2U6IGVudi5sYW5ndWFnZSxcclxuXHRcdFx0XHRjb2RlOiBlbnYuY29kZVxyXG5cdFx0XHR9KSk7XHJcblx0XHR9XHJcblx0XHRlbHNlIHtcclxuXHRcdFx0ZW52LmhpZ2hsaWdodGVkQ29kZSA9IF8uaGlnaGxpZ2h0KGVudi5jb2RlLCBlbnYuZ3JhbW1hciwgZW52Lmxhbmd1YWdlKTtcclxuXHJcblx0XHRcdF8uaG9va3MucnVuKCdiZWZvcmUtaW5zZXJ0JywgZW52KTtcclxuXHJcblx0XHRcdGVudi5lbGVtZW50LmlubmVySFRNTCA9IGVudi5oaWdobGlnaHRlZENvZGU7XHJcblxyXG5cdFx0XHRjYWxsYmFjayAmJiBjYWxsYmFjay5jYWxsKGVsZW1lbnQpO1xyXG5cclxuXHRcdFx0Xy5ob29rcy5ydW4oJ2FmdGVyLWhpZ2hsaWdodCcsIGVudik7XHJcblx0XHRcdF8uaG9va3MucnVuKCdjb21wbGV0ZScsIGVudik7XHJcblx0XHR9XHJcblx0fSxcclxuXHJcblx0aGlnaGxpZ2h0OiBmdW5jdGlvbiAodGV4dCwgZ3JhbW1hciwgbGFuZ3VhZ2UpIHtcclxuXHRcdHZhciB0b2tlbnMgPSBfLnRva2VuaXplKHRleHQsIGdyYW1tYXIpO1xyXG5cdFx0cmV0dXJuIFRva2VuLnN0cmluZ2lmeShfLnV0aWwuZW5jb2RlKHRva2VucyksIGxhbmd1YWdlKTtcclxuXHR9LFxyXG5cclxuXHR0b2tlbml6ZTogZnVuY3Rpb24odGV4dCwgZ3JhbW1hciwgbGFuZ3VhZ2UpIHtcclxuXHRcdHZhciBUb2tlbiA9IF8uVG9rZW47XHJcblxyXG5cdFx0dmFyIHN0cmFyciA9IFt0ZXh0XTtcclxuXHJcblx0XHR2YXIgcmVzdCA9IGdyYW1tYXIucmVzdDtcclxuXHJcblx0XHRpZiAocmVzdCkge1xyXG5cdFx0XHRmb3IgKHZhciB0b2tlbiBpbiByZXN0KSB7XHJcblx0XHRcdFx0Z3JhbW1hclt0b2tlbl0gPSByZXN0W3Rva2VuXTtcclxuXHRcdFx0fVxyXG5cclxuXHRcdFx0ZGVsZXRlIGdyYW1tYXIucmVzdDtcclxuXHRcdH1cclxuXHJcblx0XHR0b2tlbmxvb3A6IGZvciAodmFyIHRva2VuIGluIGdyYW1tYXIpIHtcclxuXHRcdFx0aWYoIWdyYW1tYXIuaGFzT3duUHJvcGVydHkodG9rZW4pIHx8ICFncmFtbWFyW3Rva2VuXSkge1xyXG5cdFx0XHRcdGNvbnRpbnVlO1xyXG5cdFx0XHR9XHJcblxyXG5cdFx0XHR2YXIgcGF0dGVybnMgPSBncmFtbWFyW3Rva2VuXTtcclxuXHRcdFx0cGF0dGVybnMgPSAoXy51dGlsLnR5cGUocGF0dGVybnMpID09PSBcIkFycmF5XCIpID8gcGF0dGVybnMgOiBbcGF0dGVybnNdO1xyXG5cclxuXHRcdFx0Zm9yICh2YXIgaiA9IDA7IGogPCBwYXR0ZXJucy5sZW5ndGg7ICsraikge1xyXG5cdFx0XHRcdHZhciBwYXR0ZXJuID0gcGF0dGVybnNbal0sXHJcblx0XHRcdFx0XHRpbnNpZGUgPSBwYXR0ZXJuLmluc2lkZSxcclxuXHRcdFx0XHRcdGxvb2tiZWhpbmQgPSAhIXBhdHRlcm4ubG9va2JlaGluZCxcclxuXHRcdFx0XHRcdGxvb2tiZWhpbmRMZW5ndGggPSAwLFxyXG5cdFx0XHRcdFx0YWxpYXMgPSBwYXR0ZXJuLmFsaWFzO1xyXG5cclxuXHRcdFx0XHRwYXR0ZXJuID0gcGF0dGVybi5wYXR0ZXJuIHx8IHBhdHRlcm47XHJcblxyXG5cdFx0XHRcdGZvciAodmFyIGk9MDsgaTxzdHJhcnIubGVuZ3RoOyBpKyspIHsgLy8gRG9u4oCZdCBjYWNoZSBsZW5ndGggYXMgaXQgY2hhbmdlcyBkdXJpbmcgdGhlIGxvb3BcclxuXHJcblx0XHRcdFx0XHR2YXIgc3RyID0gc3RyYXJyW2ldO1xyXG5cclxuXHRcdFx0XHRcdGlmIChzdHJhcnIubGVuZ3RoID4gdGV4dC5sZW5ndGgpIHtcclxuXHRcdFx0XHRcdFx0Ly8gU29tZXRoaW5nIHdlbnQgdGVycmlibHkgd3JvbmcsIEFCT1JULCBBQk9SVCFcclxuXHRcdFx0XHRcdFx0YnJlYWsgdG9rZW5sb29wO1xyXG5cdFx0XHRcdFx0fVxyXG5cclxuXHRcdFx0XHRcdGlmIChzdHIgaW5zdGFuY2VvZiBUb2tlbikge1xyXG5cdFx0XHRcdFx0XHRjb250aW51ZTtcclxuXHRcdFx0XHRcdH1cclxuXHJcblx0XHRcdFx0XHRwYXR0ZXJuLmxhc3RJbmRleCA9IDA7XHJcblxyXG5cdFx0XHRcdFx0dmFyIG1hdGNoID0gcGF0dGVybi5leGVjKHN0cik7XHJcblxyXG5cdFx0XHRcdFx0aWYgKG1hdGNoKSB7XHJcblx0XHRcdFx0XHRcdGlmKGxvb2tiZWhpbmQpIHtcclxuXHRcdFx0XHRcdFx0XHRsb29rYmVoaW5kTGVuZ3RoID0gbWF0Y2hbMV0ubGVuZ3RoO1xyXG5cdFx0XHRcdFx0XHR9XHJcblxyXG5cdFx0XHRcdFx0XHR2YXIgZnJvbSA9IG1hdGNoLmluZGV4IC0gMSArIGxvb2tiZWhpbmRMZW5ndGgsXHJcblx0XHRcdFx0XHRcdFx0bWF0Y2ggPSBtYXRjaFswXS5zbGljZShsb29rYmVoaW5kTGVuZ3RoKSxcclxuXHRcdFx0XHRcdFx0XHRsZW4gPSBtYXRjaC5sZW5ndGgsXHJcblx0XHRcdFx0XHRcdFx0dG8gPSBmcm9tICsgbGVuLFxyXG5cdFx0XHRcdFx0XHRcdGJlZm9yZSA9IHN0ci5zbGljZSgwLCBmcm9tICsgMSksXHJcblx0XHRcdFx0XHRcdFx0YWZ0ZXIgPSBzdHIuc2xpY2UodG8gKyAxKTtcclxuXHJcblx0XHRcdFx0XHRcdHZhciBhcmdzID0gW2ksIDFdO1xyXG5cclxuXHRcdFx0XHRcdFx0aWYgKGJlZm9yZSkge1xyXG5cdFx0XHRcdFx0XHRcdGFyZ3MucHVzaChiZWZvcmUpO1xyXG5cdFx0XHRcdFx0XHR9XHJcblxyXG5cdFx0XHRcdFx0XHR2YXIgd3JhcHBlZCA9IG5ldyBUb2tlbih0b2tlbiwgaW5zaWRlPyBfLnRva2VuaXplKG1hdGNoLCBpbnNpZGUpIDogbWF0Y2gsIGFsaWFzKTtcclxuXHJcblx0XHRcdFx0XHRcdGFyZ3MucHVzaCh3cmFwcGVkKTtcclxuXHJcblx0XHRcdFx0XHRcdGlmIChhZnRlcikge1xyXG5cdFx0XHRcdFx0XHRcdGFyZ3MucHVzaChhZnRlcik7XHJcblx0XHRcdFx0XHRcdH1cclxuXHJcblx0XHRcdFx0XHRcdEFycmF5LnByb3RvdHlwZS5zcGxpY2UuYXBwbHkoc3RyYXJyLCBhcmdzKTtcclxuXHRcdFx0XHRcdH1cclxuXHRcdFx0XHR9XHJcblx0XHRcdH1cclxuXHRcdH1cclxuXHJcblx0XHRyZXR1cm4gc3RyYXJyO1xyXG5cdH0sXHJcblxyXG5cdGhvb2tzOiB7XHJcblx0XHRhbGw6IHt9LFxyXG5cclxuXHRcdGFkZDogZnVuY3Rpb24gKG5hbWUsIGNhbGxiYWNrKSB7XHJcblx0XHRcdHZhciBob29rcyA9IF8uaG9va3MuYWxsO1xyXG5cclxuXHRcdFx0aG9va3NbbmFtZV0gPSBob29rc1tuYW1lXSB8fCBbXTtcclxuXHJcblx0XHRcdGhvb2tzW25hbWVdLnB1c2goY2FsbGJhY2spO1xyXG5cdFx0fSxcclxuXHJcblx0XHRydW46IGZ1bmN0aW9uIChuYW1lLCBlbnYpIHtcclxuXHRcdFx0dmFyIGNhbGxiYWNrcyA9IF8uaG9va3MuYWxsW25hbWVdO1xyXG5cclxuXHRcdFx0aWYgKCFjYWxsYmFja3MgfHwgIWNhbGxiYWNrcy5sZW5ndGgpIHtcclxuXHRcdFx0XHRyZXR1cm47XHJcblx0XHRcdH1cclxuXHJcblx0XHRcdGZvciAodmFyIGk9MCwgY2FsbGJhY2s7IGNhbGxiYWNrID0gY2FsbGJhY2tzW2krK107KSB7XHJcblx0XHRcdFx0Y2FsbGJhY2soZW52KTtcclxuXHRcdFx0fVxyXG5cdFx0fVxyXG5cdH1cclxufTtcclxuXHJcbnZhciBUb2tlbiA9IF8uVG9rZW4gPSBmdW5jdGlvbih0eXBlLCBjb250ZW50LCBhbGlhcykge1xyXG5cdHRoaXMudHlwZSA9IHR5cGU7XHJcblx0dGhpcy5jb250ZW50ID0gY29udGVudDtcclxuXHR0aGlzLmFsaWFzID0gYWxpYXM7XHJcbn07XHJcblxyXG5Ub2tlbi5zdHJpbmdpZnkgPSBmdW5jdGlvbihvLCBsYW5ndWFnZSwgcGFyZW50KSB7XHJcblx0aWYgKHR5cGVvZiBvID09ICdzdHJpbmcnKSB7XHJcblx0XHRyZXR1cm4gbztcclxuXHR9XHJcblxyXG5cdGlmIChfLnV0aWwudHlwZShvKSA9PT0gJ0FycmF5Jykge1xyXG5cdFx0cmV0dXJuIG8ubWFwKGZ1bmN0aW9uKGVsZW1lbnQpIHtcclxuXHRcdFx0cmV0dXJuIFRva2VuLnN0cmluZ2lmeShlbGVtZW50LCBsYW5ndWFnZSwgbyk7XHJcblx0XHR9KS5qb2luKCcnKTtcclxuXHR9XHJcblxyXG5cdHZhciBlbnYgPSB7XHJcblx0XHR0eXBlOiBvLnR5cGUsXHJcblx0XHRjb250ZW50OiBUb2tlbi5zdHJpbmdpZnkoby5jb250ZW50LCBsYW5ndWFnZSwgcGFyZW50KSxcclxuXHRcdHRhZzogJ3NwYW4nLFxyXG5cdFx0Y2xhc3NlczogWyd0b2tlbicsIG8udHlwZV0sXHJcblx0XHRhdHRyaWJ1dGVzOiB7fSxcclxuXHRcdGxhbmd1YWdlOiBsYW5ndWFnZSxcclxuXHRcdHBhcmVudDogcGFyZW50XHJcblx0fTtcclxuXHJcblx0aWYgKGVudi50eXBlID09ICdjb21tZW50Jykge1xyXG5cdFx0ZW52LmF0dHJpYnV0ZXNbJ3NwZWxsY2hlY2snXSA9ICd0cnVlJztcclxuXHR9XHJcblxyXG5cdGlmIChvLmFsaWFzKSB7XHJcblx0XHR2YXIgYWxpYXNlcyA9IF8udXRpbC50eXBlKG8uYWxpYXMpID09PSAnQXJyYXknID8gby5hbGlhcyA6IFtvLmFsaWFzXTtcclxuXHRcdEFycmF5LnByb3RvdHlwZS5wdXNoLmFwcGx5KGVudi5jbGFzc2VzLCBhbGlhc2VzKTtcclxuXHR9XHJcblxyXG5cdF8uaG9va3MucnVuKCd3cmFwJywgZW52KTtcclxuXHJcblx0dmFyIGF0dHJpYnV0ZXMgPSAnJztcclxuXHJcblx0Zm9yICh2YXIgbmFtZSBpbiBlbnYuYXR0cmlidXRlcykge1xyXG5cdFx0YXR0cmlidXRlcyArPSBuYW1lICsgJz1cIicgKyAoZW52LmF0dHJpYnV0ZXNbbmFtZV0gfHwgJycpICsgJ1wiJztcclxuXHR9XHJcblxyXG5cdHJldHVybiAnPCcgKyBlbnYudGFnICsgJyBjbGFzcz1cIicgKyBlbnYuY2xhc3Nlcy5qb2luKCcgJykgKyAnXCIgJyArIGF0dHJpYnV0ZXMgKyAnPicgKyBlbnYuY29udGVudCArICc8LycgKyBlbnYudGFnICsgJz4nO1xyXG5cclxufTtcclxuXHJcbmlmICghX3NlbGYuZG9jdW1lbnQpIHtcclxuXHRpZiAoIV9zZWxmLmFkZEV2ZW50TGlzdGVuZXIpIHtcclxuXHRcdC8vIGluIE5vZGUuanNcclxuXHRcdHJldHVybiBfc2VsZi5QcmlzbTtcclxuXHR9XHJcbiBcdC8vIEluIHdvcmtlclxyXG5cdF9zZWxmLmFkZEV2ZW50TGlzdGVuZXIoJ21lc3NhZ2UnLCBmdW5jdGlvbihldnQpIHtcclxuXHRcdHZhciBtZXNzYWdlID0gSlNPTi5wYXJzZShldnQuZGF0YSksXHJcblx0XHQgICAgbGFuZyA9IG1lc3NhZ2UubGFuZ3VhZ2UsXHJcblx0XHQgICAgY29kZSA9IG1lc3NhZ2UuY29kZTtcclxuXHJcblx0XHRfc2VsZi5wb3N0TWVzc2FnZShKU09OLnN0cmluZ2lmeShfLnV0aWwuZW5jb2RlKF8udG9rZW5pemUoY29kZSwgXy5sYW5ndWFnZXNbbGFuZ10pKSkpO1xyXG5cdFx0X3NlbGYuY2xvc2UoKTtcclxuXHR9LCBmYWxzZSk7XHJcblxyXG5cdHJldHVybiBfc2VsZi5QcmlzbTtcclxufVxyXG5cclxuLy8gR2V0IGN1cnJlbnQgc2NyaXB0IGFuZCBoaWdobGlnaHRcclxudmFyIHNjcmlwdCA9IGRvY3VtZW50LmdldEVsZW1lbnRzQnlUYWdOYW1lKCdzY3JpcHQnKTtcclxuXHJcbnNjcmlwdCA9IHNjcmlwdFtzY3JpcHQubGVuZ3RoIC0gMV07XHJcblxyXG5pZiAoc2NyaXB0KSB7XHJcblx0Xy5maWxlbmFtZSA9IHNjcmlwdC5zcmM7XHJcblxyXG5cdGlmIChkb2N1bWVudC5hZGRFdmVudExpc3RlbmVyICYmICFzY3JpcHQuaGFzQXR0cmlidXRlKCdkYXRhLW1hbnVhbCcpKSB7XHJcblx0XHRkb2N1bWVudC5hZGRFdmVudExpc3RlbmVyKCdET01Db250ZW50TG9hZGVkJywgXy5oaWdobGlnaHRBbGwpO1xyXG5cdH1cclxufVxyXG5cclxucmV0dXJuIF9zZWxmLlByaXNtO1xyXG5cclxufSkoKTtcclxuXHJcbmlmICh0eXBlb2YgbW9kdWxlICE9PSAndW5kZWZpbmVkJyAmJiBtb2R1bGUuZXhwb3J0cykge1xyXG5cdG1vZHVsZS5leHBvcnRzID0gUHJpc207XHJcbn1cclxuXHJcblxyXG4vKiAqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqXHJcbiAgICAgQmVnaW4gcHJpc20tbWFya3VwLmpzXHJcbioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiogKi9cclxuXHJcblByaXNtLmxhbmd1YWdlcy5tYXJrdXAgPSB7XHJcblx0J2NvbW1lbnQnOiAvPCEtLVtcXHdcXFddKj8tLT4vLFxyXG5cdCdwcm9sb2cnOiAvPFxcP1tcXHdcXFddKz9cXD8+LyxcclxuXHQnZG9jdHlwZSc6IC88IURPQ1RZUEVbXFx3XFxXXSs/Pi8sXHJcblx0J2NkYXRhJzogLzwhXFxbQ0RBVEFcXFtbXFx3XFxXXSo/XV0+L2ksXHJcblx0J3RhZyc6IHtcclxuXHRcdHBhdHRlcm46IC88XFwvP1teXFxzPlxcL10rKD86XFxzK1teXFxzPlxcLz1dKyg/Oj0oPzooXCJ8JykoPzpcXFxcXFwxfFxcXFw/KD8hXFwxKVtcXHdcXFddKSpcXDF8W15cXHMnXCI+PV0rKSk/KSpcXHMqXFwvPz4vaSxcclxuXHRcdGluc2lkZToge1xyXG5cdFx0XHQndGFnJzoge1xyXG5cdFx0XHRcdHBhdHRlcm46IC9ePFxcLz9bXlxccz5cXC9dKy9pLFxyXG5cdFx0XHRcdGluc2lkZToge1xyXG5cdFx0XHRcdFx0J3B1bmN0dWF0aW9uJzogL148XFwvPy8sXHJcblx0XHRcdFx0XHQnbmFtZXNwYWNlJzogL15bXlxccz5cXC86XSs6L1xyXG5cdFx0XHRcdH1cclxuXHRcdFx0fSxcclxuXHRcdFx0J2F0dHItdmFsdWUnOiB7XHJcblx0XHRcdFx0cGF0dGVybjogLz0oPzooJ3xcIilbXFx3XFxXXSo/KFxcMSl8W15cXHM+XSspL2ksXHJcblx0XHRcdFx0aW5zaWRlOiB7XHJcblx0XHRcdFx0XHQncHVuY3R1YXRpb24nOiAvWz0+XCInXS9cclxuXHRcdFx0XHR9XHJcblx0XHRcdH0sXHJcblx0XHRcdCdwdW5jdHVhdGlvbic6IC9cXC8/Pi8sXHJcblx0XHRcdCdhdHRyLW5hbWUnOiB7XHJcblx0XHRcdFx0cGF0dGVybjogL1teXFxzPlxcL10rLyxcclxuXHRcdFx0XHRpbnNpZGU6IHtcclxuXHRcdFx0XHRcdCduYW1lc3BhY2UnOiAvXlteXFxzPlxcLzpdKzovXHJcblx0XHRcdFx0fVxyXG5cdFx0XHR9XHJcblxyXG5cdFx0fVxyXG5cdH0sXHJcblx0J2VudGl0eSc6IC8mIz9bXFxkYS16XXsxLDh9Oy9pXHJcbn07XHJcblxyXG4vLyBQbHVnaW4gdG8gbWFrZSBlbnRpdHkgdGl0bGUgc2hvdyB0aGUgcmVhbCBlbnRpdHksIGlkZWEgYnkgUm9tYW4gS29tYXJvdlxyXG5QcmlzbS5ob29rcy5hZGQoJ3dyYXAnLCBmdW5jdGlvbihlbnYpIHtcclxuXHJcblx0aWYgKGVudi50eXBlID09PSAnZW50aXR5Jykge1xyXG5cdFx0ZW52LmF0dHJpYnV0ZXNbJ3RpdGxlJ10gPSBlbnYuY29udGVudC5yZXBsYWNlKC8mYW1wOy8sICcmJyk7XHJcblx0fVxyXG59KTtcclxuXHJcblxyXG4vKiAqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqXHJcbiAgICAgQmVnaW4gcHJpc20tY3NzLmpzXHJcbioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiogKi9cclxuXHJcblByaXNtLmxhbmd1YWdlcy5jc3MgPSB7XHJcblx0J2NvbW1lbnQnOiAvXFwvXFwqW1xcd1xcV10qP1xcKlxcLy8sXHJcblx0J2F0cnVsZSc6IHtcclxuXHRcdHBhdHRlcm46IC9AW1xcdy1dKz8uKj8oO3woPz1cXHMqXFx7KSkvaSxcclxuXHRcdGluc2lkZToge1xyXG5cdFx0XHQncnVsZSc6IC9AW1xcdy1dKy9cclxuXHRcdFx0Ly8gU2VlIHJlc3QgYmVsb3dcclxuXHRcdH1cclxuXHR9LFxyXG5cdCd1cmwnOiAvdXJsXFwoKD86KFtcIiddKShcXFxcKD86XFxyXFxufFtcXHdcXFddKXwoPyFcXDEpW15cXFxcXFxyXFxuXSkqXFwxfC4qPylcXCkvaSxcclxuXHQnc2VsZWN0b3InOiAvW15cXHtcXH1cXHNdW15cXHtcXH07XSo/KD89XFxzKlxceykvLFxyXG5cdCdzdHJpbmcnOiAvKFwifCcpKFxcXFwoPzpcXHJcXG58W1xcd1xcV10pfCg/IVxcMSlbXlxcXFxcXHJcXG5dKSpcXDEvLFxyXG5cdCdwcm9wZXJ0eSc6IC8oXFxifFxcQilbXFx3LV0rKD89XFxzKjopL2ksXHJcblx0J2ltcG9ydGFudCc6IC9cXEIhaW1wb3J0YW50XFxiL2ksXHJcblx0J2Z1bmN0aW9uJzogL1stYS16MC05XSsoPz1cXCgpL2ksXHJcblx0J3B1bmN0dWF0aW9uJzogL1soKXt9OzpdL1xyXG59O1xyXG5cclxuUHJpc20ubGFuZ3VhZ2VzLmNzc1snYXRydWxlJ10uaW5zaWRlLnJlc3QgPSBQcmlzbS51dGlsLmNsb25lKFByaXNtLmxhbmd1YWdlcy5jc3MpO1xyXG5cclxuaWYgKFByaXNtLmxhbmd1YWdlcy5tYXJrdXApIHtcclxuXHRQcmlzbS5sYW5ndWFnZXMuaW5zZXJ0QmVmb3JlKCdtYXJrdXAnLCAndGFnJywge1xyXG5cdFx0J3N0eWxlJzoge1xyXG5cdFx0XHRwYXR0ZXJuOiAvPHN0eWxlW1xcd1xcV10qPz5bXFx3XFxXXSo/PFxcL3N0eWxlPi9pLFxyXG5cdFx0XHRpbnNpZGU6IHtcclxuXHRcdFx0XHQndGFnJzoge1xyXG5cdFx0XHRcdFx0cGF0dGVybjogLzxzdHlsZVtcXHdcXFddKj8+fDxcXC9zdHlsZT4vaSxcclxuXHRcdFx0XHRcdGluc2lkZTogUHJpc20ubGFuZ3VhZ2VzLm1hcmt1cC50YWcuaW5zaWRlXHJcblx0XHRcdFx0fSxcclxuXHRcdFx0XHRyZXN0OiBQcmlzbS5sYW5ndWFnZXMuY3NzXHJcblx0XHRcdH0sXHJcblx0XHRcdGFsaWFzOiAnbGFuZ3VhZ2UtY3NzJ1xyXG5cdFx0fVxyXG5cdH0pO1xyXG5cdFxyXG5cdFByaXNtLmxhbmd1YWdlcy5pbnNlcnRCZWZvcmUoJ2luc2lkZScsICdhdHRyLXZhbHVlJywge1xyXG5cdFx0J3N0eWxlLWF0dHInOiB7XHJcblx0XHRcdHBhdHRlcm46IC9cXHMqc3R5bGU9KFwifCcpLio/XFwxL2ksXHJcblx0XHRcdGluc2lkZToge1xyXG5cdFx0XHRcdCdhdHRyLW5hbWUnOiB7XHJcblx0XHRcdFx0XHRwYXR0ZXJuOiAvXlxccypzdHlsZS9pLFxyXG5cdFx0XHRcdFx0aW5zaWRlOiBQcmlzbS5sYW5ndWFnZXMubWFya3VwLnRhZy5pbnNpZGVcclxuXHRcdFx0XHR9LFxyXG5cdFx0XHRcdCdwdW5jdHVhdGlvbic6IC9eXFxzKj1cXHMqWydcIl18WydcIl1cXHMqJC8sXHJcblx0XHRcdFx0J2F0dHItdmFsdWUnOiB7XHJcblx0XHRcdFx0XHRwYXR0ZXJuOiAvLisvaSxcclxuXHRcdFx0XHRcdGluc2lkZTogUHJpc20ubGFuZ3VhZ2VzLmNzc1xyXG5cdFx0XHRcdH1cclxuXHRcdFx0fSxcclxuXHRcdFx0YWxpYXM6ICdsYW5ndWFnZS1jc3MnXHJcblx0XHR9XHJcblx0fSwgUHJpc20ubGFuZ3VhZ2VzLm1hcmt1cC50YWcpO1xyXG59XHJcblxyXG4vKiAqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqXHJcbiAgICAgQmVnaW4gcHJpc20tY2xpa2UuanNcclxuKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiAqL1xyXG5cclxuUHJpc20ubGFuZ3VhZ2VzLmNsaWtlID0ge1xyXG5cdCdjb21tZW50JzogW1xyXG5cdFx0e1xyXG5cdFx0XHRwYXR0ZXJuOiAvKF58W15cXFxcXSlcXC9cXCpbXFx3XFxXXSo/XFwqXFwvLyxcclxuXHRcdFx0bG9va2JlaGluZDogdHJ1ZVxyXG5cdFx0fSxcclxuXHRcdHtcclxuXHRcdFx0cGF0dGVybjogLyhefFteXFxcXDpdKVxcL1xcLy4qLyxcclxuXHRcdFx0bG9va2JlaGluZDogdHJ1ZVxyXG5cdFx0fVxyXG5cdF0sXHJcblx0J3N0cmluZyc6IC8oXCJ8JykoXFxcXCg/OlxcclxcbnxbXFxzXFxTXSl8KD8hXFwxKVteXFxcXFxcclxcbl0pKlxcMS8sXHJcblx0J2NsYXNzLW5hbWUnOiB7XHJcblx0XHRwYXR0ZXJuOiAvKCg/Oig/OmNsYXNzfGludGVyZmFjZXxleHRlbmRzfGltcGxlbWVudHN8dHJhaXR8aW5zdGFuY2VvZnxuZXcpXFxzKyl8KD86Y2F0Y2hcXHMrXFwoKSlbYS16MC05X1xcLlxcXFxdKy9pLFxyXG5cdFx0bG9va2JlaGluZDogdHJ1ZSxcclxuXHRcdGluc2lkZToge1xyXG5cdFx0XHRwdW5jdHVhdGlvbjogLyhcXC58XFxcXCkvXHJcblx0XHR9XHJcblx0fSxcclxuXHQna2V5d29yZCc6IC9cXGIoaWZ8ZWxzZXx3aGlsZXxkb3xmb3J8cmV0dXJufGlufGluc3RhbmNlb2Z8ZnVuY3Rpb258bmV3fHRyeXx0aHJvd3xjYXRjaHxmaW5hbGx5fG51bGx8YnJlYWt8Y29udGludWUpXFxiLyxcclxuXHQnYm9vbGVhbic6IC9cXGIodHJ1ZXxmYWxzZSlcXGIvLFxyXG5cdCdmdW5jdGlvbic6IC9bYS16MC05X10rKD89XFwoKS9pLFxyXG5cdCdudW1iZXInOiAvXFxiLT8oMHhbXFxkQS1GYS1mXSt8XFxkKlxcLj9cXGQrKFtFZV0tP1xcZCspPylcXGIvLFxyXG5cdCdvcGVyYXRvcic6IC9bLStdezEsMn18IXw8PT98Pj0/fD17MSwzfXwmezEsMn18XFx8P1xcfHxcXD98XFwqfFxcL3x+fFxcXnwlLyxcclxuXHQncHVuY3R1YXRpb24nOiAvW3t9W1xcXTsoKSwuOl0vXHJcbn07XHJcblxyXG5cclxuLyogKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKlxyXG4gICAgIEJlZ2luIHByaXNtLWphdmFzY3JpcHQuanNcclxuKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKiAqL1xyXG5cclxuUHJpc20ubGFuZ3VhZ2VzLmphdmFzY3JpcHQgPSBQcmlzbS5sYW5ndWFnZXMuZXh0ZW5kKCdjbGlrZScsIHtcclxuXHQna2V5d29yZCc6IC9cXGIoYXN8YXN5bmN8YXdhaXR8YnJlYWt8Y2FzZXxjYXRjaHxjbGFzc3xjb25zdHxjb250aW51ZXxkZWJ1Z2dlcnxkZWZhdWx0fGRlbGV0ZXxkb3xlbHNlfGVudW18ZXhwb3J0fGV4dGVuZHN8ZmFsc2V8ZmluYWxseXxmb3J8ZnJvbXxmdW5jdGlvbnxnZXR8aWZ8aW1wbGVtZW50c3xpbXBvcnR8aW58aW5zdGFuY2VvZnxpbnRlcmZhY2V8bGV0fG5ld3xudWxsfG9mfHBhY2thZ2V8cHJpdmF0ZXxwcm90ZWN0ZWR8cHVibGljfHJldHVybnxzZXR8c3RhdGljfHN1cGVyfHN3aXRjaHx0aGlzfHRocm93fHRydWV8dHJ5fHR5cGVvZnx2YXJ8dm9pZHx3aGlsZXx3aXRofHlpZWxkKVxcYi8sXHJcblx0J251bWJlcic6IC9cXGItPygweFtcXGRBLUZhLWZdK3wwYlswMV0rfDBvWzAtN10rfFxcZCpcXC4/XFxkKyhbRWVdWystXT9cXGQrKT98TmFOfEluZmluaXR5KVxcYi8sXHJcblx0J2Z1bmN0aW9uJzogLyg/IVxcZClbYS16MC05XyRdKyg/PVxcKCkvaVxyXG59KTtcclxuXHJcblByaXNtLmxhbmd1YWdlcy5pbnNlcnRCZWZvcmUoJ2phdmFzY3JpcHQnLCAna2V5d29yZCcsIHtcclxuXHQncmVnZXgnOiB7XHJcblx0XHRwYXR0ZXJuOiAvKF58W14vXSlcXC8oPyFcXC8pKFxcWy4rP118XFxcXC58W14vXFxcXFxcclxcbl0pK1xcL1tnaW15dV17MCw1fSg/PVxccyooJHxbXFxyXFxuLC47fSldKSkvLFxyXG5cdFx0bG9va2JlaGluZDogdHJ1ZVxyXG5cdH1cclxufSk7XHJcblxyXG5QcmlzbS5sYW5ndWFnZXMuaW5zZXJ0QmVmb3JlKCdqYXZhc2NyaXB0JywgJ2NsYXNzLW5hbWUnLCB7XHJcblx0J3RlbXBsYXRlLXN0cmluZyc6IHtcclxuXHRcdHBhdHRlcm46IC9gKD86XFxcXGB8XFxcXD9bXmBdKSpgLyxcclxuXHRcdGluc2lkZToge1xyXG5cdFx0XHQnaW50ZXJwb2xhdGlvbic6IHtcclxuXHRcdFx0XHRwYXR0ZXJuOiAvXFwkXFx7W159XStcXH0vLFxyXG5cdFx0XHRcdGluc2lkZToge1xyXG5cdFx0XHRcdFx0J2ludGVycG9sYXRpb24tcHVuY3R1YXRpb24nOiB7XHJcblx0XHRcdFx0XHRcdHBhdHRlcm46IC9eXFwkXFx7fFxcfSQvLFxyXG5cdFx0XHRcdFx0XHRhbGlhczogJ3B1bmN0dWF0aW9uJ1xyXG5cdFx0XHRcdFx0fSxcclxuXHRcdFx0XHRcdHJlc3Q6IFByaXNtLmxhbmd1YWdlcy5qYXZhc2NyaXB0XHJcblx0XHRcdFx0fVxyXG5cdFx0XHR9LFxyXG5cdFx0XHQnc3RyaW5nJzogL1tcXHNcXFNdKy9cclxuXHRcdH1cclxuXHR9XHJcbn0pO1xyXG5cclxuaWYgKFByaXNtLmxhbmd1YWdlcy5tYXJrdXApIHtcclxuXHRQcmlzbS5sYW5ndWFnZXMuaW5zZXJ0QmVmb3JlKCdtYXJrdXAnLCAndGFnJywge1xyXG5cdFx0J3NjcmlwdCc6IHtcclxuXHRcdFx0cGF0dGVybjogLzxzY3JpcHRbXFx3XFxXXSo/PltcXHdcXFddKj88XFwvc2NyaXB0Pi9pLFxyXG5cdFx0XHRpbnNpZGU6IHtcclxuXHRcdFx0XHQndGFnJzoge1xyXG5cdFx0XHRcdFx0cGF0dGVybjogLzxzY3JpcHRbXFx3XFxXXSo/Pnw8XFwvc2NyaXB0Pi9pLFxyXG5cdFx0XHRcdFx0aW5zaWRlOiBQcmlzbS5sYW5ndWFnZXMubWFya3VwLnRhZy5pbnNpZGVcclxuXHRcdFx0XHR9LFxyXG5cdFx0XHRcdHJlc3Q6IFByaXNtLmxhbmd1YWdlcy5qYXZhc2NyaXB0XHJcblx0XHRcdH0sXHJcblx0XHRcdGFsaWFzOiAnbGFuZ3VhZ2UtamF2YXNjcmlwdCdcclxuXHRcdH1cclxuXHR9KTtcclxufVxyXG5cclxuXHJcbi8qICoqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKipcclxuICAgICBCZWdpbiBwcmlzbS1maWxlLWhpZ2hsaWdodC5qc1xyXG4qKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqKioqICovXHJcblxyXG4oZnVuY3Rpb24gKCkge1xyXG5cdGlmICghc2VsZi5QcmlzbSB8fCAhc2VsZi5kb2N1bWVudCB8fCAhZG9jdW1lbnQucXVlcnlTZWxlY3Rvcikge1xyXG5cdFx0cmV0dXJuO1xyXG5cdH1cclxuXHJcblx0c2VsZi5QcmlzbS5maWxlSGlnaGxpZ2h0ID0gZnVuY3Rpb24oKSB7XHJcblxyXG5cdFx0dmFyIEV4dGVuc2lvbnMgPSB7XHJcblx0XHRcdCdqcyc6ICdqYXZhc2NyaXB0JyxcclxuXHRcdFx0J2h0bWwnOiAnbWFya3VwJyxcclxuXHRcdFx0J3N2Zyc6ICdtYXJrdXAnLFxyXG5cdFx0XHQneG1sJzogJ21hcmt1cCcsXHJcblx0XHRcdCdweSc6ICdweXRob24nLFxyXG5cdFx0XHQncmInOiAncnVieScsXHJcblx0XHRcdCdwczEnOiAncG93ZXJzaGVsbCcsXHJcblx0XHRcdCdwc20xJzogJ3Bvd2Vyc2hlbGwnXHJcblx0XHR9O1xyXG5cclxuXHRcdGlmKEFycmF5LnByb3RvdHlwZS5mb3JFYWNoKSB7IC8vIENoZWNrIHRvIHByZXZlbnQgZXJyb3IgaW4gSUU4XHJcblx0XHRcdEFycmF5LnByb3RvdHlwZS5zbGljZS5jYWxsKGRvY3VtZW50LnF1ZXJ5U2VsZWN0b3JBbGwoJ3ByZVtkYXRhLXNyY10nKSkuZm9yRWFjaChmdW5jdGlvbiAocHJlKSB7XHJcblx0XHRcdFx0dmFyIHNyYyA9IHByZS5nZXRBdHRyaWJ1dGUoJ2RhdGEtc3JjJyk7XHJcblxyXG5cdFx0XHRcdHZhciBsYW5ndWFnZSwgcGFyZW50ID0gcHJlO1xyXG5cdFx0XHRcdHZhciBsYW5nID0gL1xcYmxhbmcoPzp1YWdlKT8tKD8hXFwqKShcXHcrKVxcYi9pO1xyXG5cdFx0XHRcdHdoaWxlIChwYXJlbnQgJiYgIWxhbmcudGVzdChwYXJlbnQuY2xhc3NOYW1lKSkge1xyXG5cdFx0XHRcdFx0cGFyZW50ID0gcGFyZW50LnBhcmVudE5vZGU7XHJcblx0XHRcdFx0fVxyXG5cclxuXHRcdFx0XHRpZiAocGFyZW50KSB7XHJcblx0XHRcdFx0XHRsYW5ndWFnZSA9IChwcmUuY2xhc3NOYW1lLm1hdGNoKGxhbmcpIHx8IFssICcnXSlbMV07XHJcblx0XHRcdFx0fVxyXG5cclxuXHRcdFx0XHRpZiAoIWxhbmd1YWdlKSB7XHJcblx0XHRcdFx0XHR2YXIgZXh0ZW5zaW9uID0gKHNyYy5tYXRjaCgvXFwuKFxcdyspJC8pIHx8IFssICcnXSlbMV07XHJcblx0XHRcdFx0XHRsYW5ndWFnZSA9IEV4dGVuc2lvbnNbZXh0ZW5zaW9uXSB8fCBleHRlbnNpb247XHJcblx0XHRcdFx0fVxyXG5cclxuXHRcdFx0XHR2YXIgY29kZSA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ2NvZGUnKTtcclxuXHRcdFx0XHRjb2RlLmNsYXNzTmFtZSA9ICdsYW5ndWFnZS0nICsgbGFuZ3VhZ2U7XHJcblxyXG5cdFx0XHRcdHByZS50ZXh0Q29udGVudCA9ICcnO1xyXG5cclxuXHRcdFx0XHRjb2RlLnRleHRDb250ZW50ID0gJ0xvYWRpbmfigKYnO1xyXG5cclxuXHRcdFx0XHRwcmUuYXBwZW5kQ2hpbGQoY29kZSk7XHJcblxyXG5cdFx0XHRcdHZhciB4aHIgPSBuZXcgWE1MSHR0cFJlcXVlc3QoKTtcclxuXHJcblx0XHRcdFx0eGhyLm9wZW4oJ0dFVCcsIHNyYywgdHJ1ZSk7XHJcblxyXG5cdFx0XHRcdHhoci5vbnJlYWR5c3RhdGVjaGFuZ2UgPSBmdW5jdGlvbiAoKSB7XHJcblx0XHRcdFx0XHRpZiAoeGhyLnJlYWR5U3RhdGUgPT0gNCkge1xyXG5cclxuXHRcdFx0XHRcdFx0aWYgKHhoci5zdGF0dXMgPCA0MDAgJiYgeGhyLnJlc3BvbnNlVGV4dCkge1xyXG5cdFx0XHRcdFx0XHRcdGNvZGUudGV4dENvbnRlbnQgPSB4aHIucmVzcG9uc2VUZXh0O1xyXG5cclxuXHRcdFx0XHRcdFx0XHRQcmlzbS5oaWdobGlnaHRFbGVtZW50KGNvZGUpO1xyXG5cdFx0XHRcdFx0XHR9XHJcblx0XHRcdFx0XHRcdGVsc2UgaWYgKHhoci5zdGF0dXMgPj0gNDAwKSB7XHJcblx0XHRcdFx0XHRcdFx0Y29kZS50ZXh0Q29udGVudCA9ICfinJYgRXJyb3IgJyArIHhoci5zdGF0dXMgKyAnIHdoaWxlIGZldGNoaW5nIGZpbGU6ICcgKyB4aHIuc3RhdHVzVGV4dDtcclxuXHRcdFx0XHRcdFx0fVxyXG5cdFx0XHRcdFx0XHRlbHNlIHtcclxuXHRcdFx0XHRcdFx0XHRjb2RlLnRleHRDb250ZW50ID0gJ+KcliBFcnJvcjogRmlsZSBkb2VzIG5vdCBleGlzdCBvciBpcyBlbXB0eSc7XHJcblx0XHRcdFx0XHRcdH1cclxuXHRcdFx0XHRcdH1cclxuXHRcdFx0XHR9O1xyXG5cclxuXHRcdFx0XHR4aHIuc2VuZChudWxsKTtcclxuXHRcdFx0fSk7XHJcblx0XHR9XHJcblxyXG5cdH07XHJcblxyXG5cdHNlbGYuUHJpc20uZmlsZUhpZ2hsaWdodCgpO1xyXG5cclxufSkoKTtcclxuIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbigpIHtcbiAgcmV0dXJuIGZ1bmN0aW9uKGRlY2spIHtcbiAgICB2YXIgYmFja2Ryb3BzO1xuXG4gICAgZnVuY3Rpb24gY3JlYXRlQmFja2Ryb3BGb3JTbGlkZShzbGlkZSkge1xuICAgICAgdmFyIGJhY2tkcm9wQXR0cmlidXRlID0gc2xpZGUuZ2V0QXR0cmlidXRlKCdkYXRhLWJlc3Bva2UtYmFja2Ryb3AnKTtcblxuICAgICAgaWYgKGJhY2tkcm9wQXR0cmlidXRlKSB7XG4gICAgICAgIHZhciBiYWNrZHJvcCA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ2RpdicpO1xuICAgICAgICBiYWNrZHJvcC5jbGFzc05hbWUgPSBiYWNrZHJvcEF0dHJpYnV0ZTtcbiAgICAgICAgYmFja2Ryb3AuY2xhc3NMaXN0LmFkZCgnYmVzcG9rZS1iYWNrZHJvcCcpO1xuICAgICAgICBkZWNrLnBhcmVudC5hcHBlbmRDaGlsZChiYWNrZHJvcCk7XG4gICAgICAgIHJldHVybiBiYWNrZHJvcDtcbiAgICAgIH1cbiAgICB9XG5cbiAgICBmdW5jdGlvbiB1cGRhdGVDbGFzc2VzKGVsKSB7XG4gICAgICBpZiAoZWwpIHtcbiAgICAgICAgdmFyIGluZGV4ID0gYmFja2Ryb3BzLmluZGV4T2YoZWwpLFxuICAgICAgICAgIGN1cnJlbnRJbmRleCA9IGRlY2suc2xpZGUoKTtcblxuICAgICAgICByZW1vdmVDbGFzcyhlbCwgJ2FjdGl2ZScpO1xuICAgICAgICByZW1vdmVDbGFzcyhlbCwgJ2luYWN0aXZlJyk7XG4gICAgICAgIHJlbW92ZUNsYXNzKGVsLCAnYmVmb3JlJyk7XG4gICAgICAgIHJlbW92ZUNsYXNzKGVsLCAnYWZ0ZXInKTtcblxuICAgICAgICBpZiAoaW5kZXggIT09IGN1cnJlbnRJbmRleCkge1xuICAgICAgICAgIGFkZENsYXNzKGVsLCAnaW5hY3RpdmUnKTtcbiAgICAgICAgICBhZGRDbGFzcyhlbCwgaW5kZXggPCBjdXJyZW50SW5kZXggPyAnYmVmb3JlJyA6ICdhZnRlcicpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgIGFkZENsYXNzKGVsLCAnYWN0aXZlJyk7XG4gICAgICAgIH1cbiAgICAgIH1cbiAgICB9XG5cbiAgICBmdW5jdGlvbiByZW1vdmVDbGFzcyhlbCwgY2xhc3NOYW1lKSB7XG4gICAgICBlbC5jbGFzc0xpc3QucmVtb3ZlKCdiZXNwb2tlLWJhY2tkcm9wLScgKyBjbGFzc05hbWUpO1xuICAgIH1cblxuICAgIGZ1bmN0aW9uIGFkZENsYXNzKGVsLCBjbGFzc05hbWUpIHtcbiAgICAgIGVsLmNsYXNzTGlzdC5hZGQoJ2Jlc3Bva2UtYmFja2Ryb3AtJyArIGNsYXNzTmFtZSk7XG4gICAgfVxuXG4gICAgYmFja2Ryb3BzID0gZGVjay5zbGlkZXNcbiAgICAgIC5tYXAoY3JlYXRlQmFja2Ryb3BGb3JTbGlkZSk7XG5cbiAgICBkZWNrLm9uKCdhY3RpdmF0ZScsIGZ1bmN0aW9uKCkge1xuICAgICAgYmFja2Ryb3BzLmZvckVhY2godXBkYXRlQ2xhc3Nlcyk7XG4gICAgfSk7XG4gIH07XG59O1xuIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihvcHRpb25zKSB7XG4gIHJldHVybiBmdW5jdGlvbihkZWNrKSB7XG4gICAgdmFyIGFjdGl2ZVNsaWRlSW5kZXgsXG4gICAgICBhY3RpdmVCdWxsZXRJbmRleCxcblxuICAgICAgYnVsbGV0cyA9IGRlY2suc2xpZGVzLm1hcChmdW5jdGlvbihzbGlkZSkge1xuICAgICAgICByZXR1cm4gW10uc2xpY2UuY2FsbChzbGlkZS5xdWVyeVNlbGVjdG9yQWxsKCh0eXBlb2Ygb3B0aW9ucyA9PT0gJ3N0cmluZycgPyBvcHRpb25zIDogJ1tkYXRhLWJlc3Bva2UtYnVsbGV0XScpKSwgMCk7XG4gICAgICB9KSxcblxuICAgICAgbmV4dCA9IGZ1bmN0aW9uKCkge1xuICAgICAgICB2YXIgbmV4dFNsaWRlSW5kZXggPSBhY3RpdmVTbGlkZUluZGV4ICsgMTtcblxuICAgICAgICBpZiAoYWN0aXZlU2xpZGVIYXNCdWxsZXRCeU9mZnNldCgxKSkge1xuICAgICAgICAgIGFjdGl2YXRlQnVsbGV0KGFjdGl2ZVNsaWRlSW5kZXgsIGFjdGl2ZUJ1bGxldEluZGV4ICsgMSk7XG4gICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9IGVsc2UgaWYgKGJ1bGxldHNbbmV4dFNsaWRlSW5kZXhdKSB7XG4gICAgICAgICAgYWN0aXZhdGVCdWxsZXQobmV4dFNsaWRlSW5kZXgsIDApO1xuICAgICAgICB9XG4gICAgICB9LFxuXG4gICAgICBwcmV2ID0gZnVuY3Rpb24oKSB7XG4gICAgICAgIHZhciBwcmV2U2xpZGVJbmRleCA9IGFjdGl2ZVNsaWRlSW5kZXggLSAxO1xuXG4gICAgICAgIGlmIChhY3RpdmVTbGlkZUhhc0J1bGxldEJ5T2Zmc2V0KC0xKSkge1xuICAgICAgICAgIGFjdGl2YXRlQnVsbGV0KGFjdGl2ZVNsaWRlSW5kZXgsIGFjdGl2ZUJ1bGxldEluZGV4IC0gMSk7XG4gICAgICAgICAgcmV0dXJuIGZhbHNlO1xuICAgICAgICB9IGVsc2UgaWYgKGJ1bGxldHNbcHJldlNsaWRlSW5kZXhdKSB7XG4gICAgICAgICAgYWN0aXZhdGVCdWxsZXQocHJldlNsaWRlSW5kZXgsIGJ1bGxldHNbcHJldlNsaWRlSW5kZXhdLmxlbmd0aCAtIDEpO1xuICAgICAgICB9XG4gICAgICB9LFxuXG4gICAgICBhY3RpdmF0ZUJ1bGxldCA9IGZ1bmN0aW9uKHNsaWRlSW5kZXgsIGJ1bGxldEluZGV4KSB7XG4gICAgICAgIGFjdGl2ZVNsaWRlSW5kZXggPSBzbGlkZUluZGV4O1xuICAgICAgICBhY3RpdmVCdWxsZXRJbmRleCA9IGJ1bGxldEluZGV4O1xuXG4gICAgICAgIGJ1bGxldHMuZm9yRWFjaChmdW5jdGlvbihzbGlkZSwgcykge1xuICAgICAgICAgIHNsaWRlLmZvckVhY2goZnVuY3Rpb24oYnVsbGV0LCBiKSB7XG4gICAgICAgICAgICBidWxsZXQuY2xhc3NMaXN0LmFkZCgnYmVzcG9rZS1idWxsZXQnKTtcblxuICAgICAgICAgICAgaWYgKHMgPCBzbGlkZUluZGV4IHx8IHMgPT09IHNsaWRlSW5kZXggJiYgYiA8PSBidWxsZXRJbmRleCkge1xuICAgICAgICAgICAgICBidWxsZXQuY2xhc3NMaXN0LmFkZCgnYmVzcG9rZS1idWxsZXQtYWN0aXZlJyk7XG4gICAgICAgICAgICAgIGJ1bGxldC5jbGFzc0xpc3QucmVtb3ZlKCdiZXNwb2tlLWJ1bGxldC1pbmFjdGl2ZScpO1xuICAgICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgICAgYnVsbGV0LmNsYXNzTGlzdC5hZGQoJ2Jlc3Bva2UtYnVsbGV0LWluYWN0aXZlJyk7XG4gICAgICAgICAgICAgIGJ1bGxldC5jbGFzc0xpc3QucmVtb3ZlKCdiZXNwb2tlLWJ1bGxldC1hY3RpdmUnKTtcbiAgICAgICAgICAgIH1cblxuICAgICAgICAgICAgaWYgKHMgPT09IHNsaWRlSW5kZXggJiYgYiA9PT0gYnVsbGV0SW5kZXgpIHtcbiAgICAgICAgICAgICAgYnVsbGV0LmNsYXNzTGlzdC5hZGQoJ2Jlc3Bva2UtYnVsbGV0LWN1cnJlbnQnKTtcbiAgICAgICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgICAgIGJ1bGxldC5jbGFzc0xpc3QucmVtb3ZlKCdiZXNwb2tlLWJ1bGxldC1jdXJyZW50Jyk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgfSk7XG4gICAgICAgIH0pO1xuICAgICAgfSxcblxuICAgICAgYWN0aXZlU2xpZGVIYXNCdWxsZXRCeU9mZnNldCA9IGZ1bmN0aW9uKG9mZnNldCkge1xuICAgICAgICByZXR1cm4gYnVsbGV0c1thY3RpdmVTbGlkZUluZGV4XVthY3RpdmVCdWxsZXRJbmRleCArIG9mZnNldF0gIT09IHVuZGVmaW5lZDtcbiAgICAgIH07XG5cbiAgICBkZWNrLm9uKCduZXh0JywgbmV4dCk7XG4gICAgZGVjay5vbigncHJldicsIHByZXYpO1xuXG4gICAgZGVjay5vbignc2xpZGUnLCBmdW5jdGlvbihlKSB7XG4gICAgICBhY3RpdmF0ZUJ1bGxldChlLmluZGV4LCAwKTtcbiAgICB9KTtcblxuICAgIGFjdGl2YXRlQnVsbGV0KDAsIDApO1xuICB9O1xufTtcbiIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oKSB7XG4gIHJldHVybiBmdW5jdGlvbihkZWNrKSB7XG4gICAgZGVjay5zbGlkZXMuZm9yRWFjaChmdW5jdGlvbihzbGlkZSkge1xuICAgICAgc2xpZGUuYWRkRXZlbnRMaXN0ZW5lcigna2V5ZG93bicsIGZ1bmN0aW9uKGUpIHtcbiAgICAgICAgaWYgKC9JTlBVVHxURVhUQVJFQXxTRUxFQ1QvLnRlc3QoZS50YXJnZXQubm9kZU5hbWUpIHx8IGUudGFyZ2V0LmNvbnRlbnRFZGl0YWJsZSA9PT0gJ3RydWUnKSB7XG4gICAgICAgICAgZS5zdG9wUHJvcGFnYXRpb24oKTtcbiAgICAgICAgfVxuICAgICAgfSk7XG4gICAgfSk7XG4gIH07XG59O1xuIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbigpIHtcbiAgcmV0dXJuIGZ1bmN0aW9uKGRlY2spIHtcbiAgICB2YXIgcGFyc2VIYXNoID0gZnVuY3Rpb24oKSB7XG4gICAgICB2YXIgaGFzaCA9IHdpbmRvdy5sb2NhdGlvbi5oYXNoLnNsaWNlKDEpLFxuICAgICAgICBzbGlkZU51bWJlck9yTmFtZSA9IHBhcnNlSW50KGhhc2gsIDEwKTtcblxuICAgICAgaWYgKGhhc2gpIHtcbiAgICAgICAgaWYgKHNsaWRlTnVtYmVyT3JOYW1lKSB7XG4gICAgICAgICAgYWN0aXZhdGVTbGlkZShzbGlkZU51bWJlck9yTmFtZSAtIDEpO1xuICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgIGRlY2suc2xpZGVzLmZvckVhY2goZnVuY3Rpb24oc2xpZGUsIGkpIHtcbiAgICAgICAgICAgIGlmIChzbGlkZS5nZXRBdHRyaWJ1dGUoJ2RhdGEtYmVzcG9rZS1oYXNoJykgPT09IGhhc2gpIHtcbiAgICAgICAgICAgICAgYWN0aXZhdGVTbGlkZShpKTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICB9KTtcbiAgICAgICAgfVxuICAgICAgfVxuICAgIH07XG5cbiAgICB2YXIgYWN0aXZhdGVTbGlkZSA9IGZ1bmN0aW9uKGluZGV4KSB7XG4gICAgICB2YXIgaW5kZXhUb0FjdGl2YXRlID0gLTEgPCBpbmRleCAmJiBpbmRleCA8IGRlY2suc2xpZGVzLmxlbmd0aCA/IGluZGV4IDogMDtcbiAgICAgIGlmIChpbmRleFRvQWN0aXZhdGUgIT09IGRlY2suc2xpZGUoKSkge1xuICAgICAgICBkZWNrLnNsaWRlKGluZGV4VG9BY3RpdmF0ZSk7XG4gICAgICB9XG4gICAgfTtcblxuICAgIHNldFRpbWVvdXQoZnVuY3Rpb24oKSB7XG4gICAgICBwYXJzZUhhc2goKTtcblxuICAgICAgZGVjay5vbignYWN0aXZhdGUnLCBmdW5jdGlvbihlKSB7XG4gICAgICAgIHZhciBzbGlkZU5hbWUgPSBlLnNsaWRlLmdldEF0dHJpYnV0ZSgnZGF0YS1iZXNwb2tlLWhhc2gnKTtcbiAgICAgICAgd2luZG93LmxvY2F0aW9uLmhhc2ggPSBzbGlkZU5hbWUgfHwgZS5pbmRleCArIDE7XG4gICAgICB9KTtcblxuICAgICAgd2luZG93LmFkZEV2ZW50TGlzdGVuZXIoJ2hhc2hjaGFuZ2UnLCBwYXJzZUhhc2gpO1xuICAgIH0sIDApO1xuICB9O1xufTtcbiIsIm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24ob3B0aW9ucykge1xuICByZXR1cm4gZnVuY3Rpb24oZGVjaykge1xuICAgIHZhciBpc0hvcml6b250YWwgPSBvcHRpb25zICE9PSAndmVydGljYWwnO1xuXG4gICAgZG9jdW1lbnQuYWRkRXZlbnRMaXN0ZW5lcigna2V5ZG93bicsIGZ1bmN0aW9uKGUpIHtcbiAgICAgIGlmIChlLndoaWNoID09IDM0IHx8IC8vIFBBR0UgRE9XTlxuICAgICAgICAoZS53aGljaCA9PSAzMiAmJiAhZS5zaGlmdEtleSkgfHwgLy8gU1BBQ0UgV0lUSE9VVCBTSElGVFxuICAgICAgICAoaXNIb3Jpem9udGFsICYmIGUud2hpY2ggPT0gMzkpIHx8IC8vIFJJR0hUXG4gICAgICAgICghaXNIb3Jpem9udGFsICYmIGUud2hpY2ggPT0gNDApIC8vIERPV05cbiAgICAgICkgeyBkZWNrLm5leHQoKTsgfVxuXG4gICAgICBpZiAoZS53aGljaCA9PSAzMyB8fCAvLyBQQUdFIFVQXG4gICAgICAgIChlLndoaWNoID09IDMyICYmIGUuc2hpZnRLZXkpIHx8IC8vIFNQQUNFICsgU0hJRlRcbiAgICAgICAgKGlzSG9yaXpvbnRhbCAmJiBlLndoaWNoID09IDM3KSB8fCAvLyBMRUZUXG4gICAgICAgICghaXNIb3Jpem9udGFsICYmIGUud2hpY2ggPT0gMzgpIC8vIFVQXG4gICAgICApIHsgZGVjay5wcmV2KCk7IH1cbiAgICB9KTtcbiAgfTtcbn07XG4iLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKG9wdGlvbnMpIHtcbiAgcmV0dXJuIGZ1bmN0aW9uIChkZWNrKSB7XG4gICAgdmFyIHByb2dyZXNzUGFyZW50ID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2JyksXG4gICAgICBwcm9ncmVzc0JhciA9IGRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ2RpdicpLFxuICAgICAgcHJvcCA9IG9wdGlvbnMgPT09ICd2ZXJ0aWNhbCcgPyAnaGVpZ2h0JyA6ICd3aWR0aCc7XG5cbiAgICBwcm9ncmVzc1BhcmVudC5jbGFzc05hbWUgPSAnYmVzcG9rZS1wcm9ncmVzcy1wYXJlbnQnO1xuICAgIHByb2dyZXNzQmFyLmNsYXNzTmFtZSA9ICdiZXNwb2tlLXByb2dyZXNzLWJhcic7XG4gICAgcHJvZ3Jlc3NQYXJlbnQuYXBwZW5kQ2hpbGQocHJvZ3Jlc3NCYXIpO1xuICAgIGRlY2sucGFyZW50LmFwcGVuZENoaWxkKHByb2dyZXNzUGFyZW50KTtcblxuICAgIGRlY2sub24oJ2FjdGl2YXRlJywgZnVuY3Rpb24oZSkge1xuICAgICAgcHJvZ3Jlc3NCYXIuc3R5bGVbcHJvcF0gPSAoZS5pbmRleCAqIDEwMCAvIChkZWNrLnNsaWRlcy5sZW5ndGggLSAxKSkgKyAnJSc7XG4gICAgfSk7XG4gIH07XG59O1xuIiwibW9kdWxlLmV4cG9ydHMgPSBmdW5jdGlvbihvcHRpb25zKSB7XG4gIHJldHVybiBmdW5jdGlvbihkZWNrKSB7XG4gICAgdmFyIHBhcmVudCA9IGRlY2sucGFyZW50LFxuICAgICAgZmlyc3RTbGlkZSA9IGRlY2suc2xpZGVzWzBdLFxuICAgICAgc2xpZGVIZWlnaHQgPSBmaXJzdFNsaWRlLm9mZnNldEhlaWdodCxcbiAgICAgIHNsaWRlV2lkdGggPSBmaXJzdFNsaWRlLm9mZnNldFdpZHRoLFxuICAgICAgdXNlWm9vbSA9IG9wdGlvbnMgPT09ICd6b29tJyB8fCAoJ3pvb20nIGluIHBhcmVudC5zdHlsZSAmJiBvcHRpb25zICE9PSAndHJhbnNmb3JtJyksXG5cbiAgICAgIHdyYXAgPSBmdW5jdGlvbihlbGVtZW50KSB7XG4gICAgICAgIHZhciB3cmFwcGVyID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2Jyk7XG4gICAgICAgIHdyYXBwZXIuY2xhc3NOYW1lID0gJ2Jlc3Bva2Utc2NhbGUtcGFyZW50JztcbiAgICAgICAgZWxlbWVudC5wYXJlbnROb2RlLmluc2VydEJlZm9yZSh3cmFwcGVyLCBlbGVtZW50KTtcbiAgICAgICAgd3JhcHBlci5hcHBlbmRDaGlsZChlbGVtZW50KTtcbiAgICAgICAgcmV0dXJuIHdyYXBwZXI7XG4gICAgICB9LFxuXG4gICAgICBlbGVtZW50cyA9IHVzZVpvb20gPyBkZWNrLnNsaWRlcyA6IGRlY2suc2xpZGVzLm1hcCh3cmFwKSxcblxuICAgICAgdHJhbnNmb3JtUHJvcGVydHkgPSAoZnVuY3Rpb24ocHJvcGVydHkpIHtcbiAgICAgICAgdmFyIHByZWZpeGVzID0gJ01veiBXZWJraXQgTyBtcycuc3BsaXQoJyAnKTtcbiAgICAgICAgcmV0dXJuIHByZWZpeGVzLnJlZHVjZShmdW5jdGlvbihjdXJyZW50UHJvcGVydHksIHByZWZpeCkge1xuICAgICAgICAgICAgcmV0dXJuIHByZWZpeCArIHByb3BlcnR5IGluIHBhcmVudC5zdHlsZSA/IHByZWZpeCArIHByb3BlcnR5IDogY3VycmVudFByb3BlcnR5O1xuICAgICAgICAgIH0sIHByb3BlcnR5LnRvTG93ZXJDYXNlKCkpO1xuICAgICAgfSgnVHJhbnNmb3JtJykpLFxuXG4gICAgICBzY2FsZSA9IHVzZVpvb20gP1xuICAgICAgICBmdW5jdGlvbihyYXRpbywgZWxlbWVudCkge1xuICAgICAgICAgIGVsZW1lbnQuc3R5bGUuem9vbSA9IHJhdGlvO1xuICAgICAgICB9IDpcbiAgICAgICAgZnVuY3Rpb24ocmF0aW8sIGVsZW1lbnQpIHtcbiAgICAgICAgICBlbGVtZW50LnN0eWxlW3RyYW5zZm9ybVByb3BlcnR5XSA9ICdzY2FsZSgnICsgcmF0aW8gKyAnKSc7XG4gICAgICAgIH0sXG5cbiAgICAgIHNjYWxlQWxsID0gZnVuY3Rpb24oKSB7XG4gICAgICAgIHZhciB4U2NhbGUgPSBwYXJlbnQub2Zmc2V0V2lkdGggLyBzbGlkZVdpZHRoLFxuICAgICAgICAgIHlTY2FsZSA9IHBhcmVudC5vZmZzZXRIZWlnaHQgLyBzbGlkZUhlaWdodDtcblxuICAgICAgICBlbGVtZW50cy5mb3JFYWNoKHNjYWxlLmJpbmQobnVsbCwgTWF0aC5taW4oeFNjYWxlLCB5U2NhbGUpKSk7XG4gICAgICB9O1xuXG4gICAgd2luZG93LmFkZEV2ZW50TGlzdGVuZXIoJ3Jlc2l6ZScsIHNjYWxlQWxsKTtcbiAgICBzY2FsZUFsbCgpO1xuICB9O1xuXG59O1xuIiwiKGZ1bmN0aW9uIChnbG9iYWwpe1xuLyohXG4gKiBiZXNwb2tlLXRoZW1lLWN1YmUgdjIuMC4xXG4gKlxuICogQ29weXJpZ2h0IDIwMTQsIE1hcmsgRGFsZ2xlaXNoXG4gKiBUaGlzIGNvbnRlbnQgaXMgcmVsZWFzZWQgdW5kZXIgdGhlIE1JVCBsaWNlbnNlXG4gKiBodHRwOi8vbWl0LWxpY2Vuc2Uub3JnL21hcmtkYWxnbGVpc2hcbiAqL1xuXG4hZnVuY3Rpb24oZSl7aWYoXCJvYmplY3RcIj09dHlwZW9mIGV4cG9ydHMpbW9kdWxlLmV4cG9ydHM9ZSgpO2Vsc2UgaWYoXCJmdW5jdGlvblwiPT10eXBlb2YgZGVmaW5lJiZkZWZpbmUuYW1kKWRlZmluZShlKTtlbHNle3ZhciBvO1widW5kZWZpbmVkXCIhPXR5cGVvZiB3aW5kb3c/bz13aW5kb3c6XCJ1bmRlZmluZWRcIiE9dHlwZW9mIGdsb2JhbD9vPWdsb2JhbDpcInVuZGVmaW5lZFwiIT10eXBlb2Ygc2VsZiYmKG89c2VsZik7dmFyIGY9bztmPWYuYmVzcG9rZXx8KGYuYmVzcG9rZT17fSksZj1mLnRoZW1lc3x8KGYudGhlbWVzPXt9KSxmLmN1YmU9ZSgpfX0oZnVuY3Rpb24oKXt2YXIgZGVmaW5lLG1vZHVsZSxleHBvcnRzO3JldHVybiAoZnVuY3Rpb24gZSh0LG4scil7ZnVuY3Rpb24gcyhvLHUpe2lmKCFuW29dKXtpZighdFtvXSl7dmFyIGE9dHlwZW9mIHJlcXVpcmU9PVwiZnVuY3Rpb25cIiYmcmVxdWlyZTtpZighdSYmYSlyZXR1cm4gYShvLCEwKTtpZihpKXJldHVybiBpKG8sITApO3Rocm93IG5ldyBFcnJvcihcIkNhbm5vdCBmaW5kIG1vZHVsZSAnXCIrbytcIidcIil9dmFyIGY9bltvXT17ZXhwb3J0czp7fX07dFtvXVswXS5jYWxsKGYuZXhwb3J0cyxmdW5jdGlvbihlKXt2YXIgbj10W29dWzFdW2VdO3JldHVybiBzKG4/bjplKX0sZixmLmV4cG9ydHMsZSx0LG4scil9cmV0dXJuIG5bb10uZXhwb3J0c312YXIgaT10eXBlb2YgcmVxdWlyZT09XCJmdW5jdGlvblwiJiZyZXF1aXJlO2Zvcih2YXIgbz0wO288ci5sZW5ndGg7bysrKXMocltvXSk7cmV0dXJuIHN9KSh7MTpbZnVuY3Rpb24oX2RlcmVxXyxtb2R1bGUsZXhwb3J0cyl7XG5cbnZhciBjbGFzc2VzID0gX2RlcmVxXygnYmVzcG9rZS1jbGFzc2VzJyk7XG52YXIgaW5zZXJ0Q3NzID0gX2RlcmVxXygnaW5zZXJ0LWNzcycpO1xuXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKCkge1xuICB2YXIgY3NzID0gXCIqey1tb3otYm94LXNpemluZzpib3JkZXItYm94O2JveC1zaXppbmc6Ym9yZGVyLWJveDttYXJnaW46MDtwYWRkaW5nOjB9QG1lZGlhIHByaW50eyp7LXdlYmtpdC1wcmludC1jb2xvci1hZGp1c3Q6ZXhhY3R9fUBwYWdle3NpemU6bGFuZHNjYXBlO21hcmdpbjowfS5iZXNwb2tlLXBhcmVudHstd2Via2l0LXRyYW5zaXRpb246YmFja2dyb3VuZCAuNnMgZWFzZTt0cmFuc2l0aW9uOmJhY2tncm91bmQgLjZzIGVhc2U7cG9zaXRpb246YWJzb2x1dGU7dG9wOjA7Ym90dG9tOjA7bGVmdDowO3JpZ2h0OjA7b3ZlcmZsb3c6aGlkZGVufUBtZWRpYSBwcmludHsuYmVzcG9rZS1wYXJlbnR7b3ZlcmZsb3c6dmlzaWJsZTtwb3NpdGlvbjpzdGF0aWN9fS5iZXNwb2tlLXRoZW1lLWN1YmUtc2xpZGUtcGFyZW50e3Bvc2l0aW9uOmFic29sdXRlO3RvcDowO2xlZnQ6MDtyaWdodDowO2JvdHRvbTowOy13ZWJraXQtcGVyc3BlY3RpdmU6NjAwcHg7cGVyc3BlY3RpdmU6NjAwcHg7cG9pbnRlci1ldmVudHM6bm9uZX0uYmVzcG9rZS1zbGlkZXtwb2ludGVyLWV2ZW50czphdXRvOy13ZWJraXQtdHJhbnNpdGlvbjotd2Via2l0LXRyYW5zZm9ybSAuNnMgZWFzZSxvcGFjaXR5IC42cyBlYXNlLGJhY2tncm91bmQgLjZzIGVhc2U7dHJhbnNpdGlvbjp0cmFuc2Zvcm0gLjZzIGVhc2Usb3BhY2l0eSAuNnMgZWFzZSxiYWNrZ3JvdW5kIC42cyBlYXNlOy13ZWJraXQtdHJhbnNmb3JtLW9yaWdpbjo1MCUgNTAlIDA7dHJhbnNmb3JtLW9yaWdpbjo1MCUgNTAlIDA7LXdlYmtpdC1iYWNrZmFjZS12aXNpYmlsaXR5OmhpZGRlbjtiYWNrZmFjZS12aXNpYmlsaXR5OmhpZGRlbjtkaXNwbGF5Oi13ZWJraXQtYm94O2Rpc3BsYXk6LXdlYmtpdC1mbGV4O2Rpc3BsYXk6LW1zLWZsZXhib3g7ZGlzcGxheTpmbGV4Oy13ZWJraXQtYm94LW9yaWVudDp2ZXJ0aWNhbDstd2Via2l0LWJveC1kaXJlY3Rpb246bm9ybWFsOy13ZWJraXQtZmxleC1kaXJlY3Rpb246Y29sdW1uOy1tcy1mbGV4LWRpcmVjdGlvbjpjb2x1bW47ZmxleC1kaXJlY3Rpb246Y29sdW1uOy13ZWJraXQtYm94LXBhY2s6Y2VudGVyOy13ZWJraXQtanVzdGlmeS1jb250ZW50OmNlbnRlcjstbXMtZmxleC1wYWNrOmNlbnRlcjtqdXN0aWZ5LWNvbnRlbnQ6Y2VudGVyOy13ZWJraXQtYm94LWFsaWduOmNlbnRlcjstd2Via2l0LWFsaWduLWl0ZW1zOmNlbnRlcjstbXMtZmxleC1hbGlnbjpjZW50ZXI7YWxpZ24taXRlbXM6Y2VudGVyO3RleHQtYWxpZ246Y2VudGVyO3dpZHRoOjY0MHB4O2hlaWdodDo0ODBweDtwb3NpdGlvbjphYnNvbHV0ZTt0b3A6NTAlO21hcmdpbi10b3A6LTI0MHB4O2xlZnQ6NTAlO21hcmdpbi1sZWZ0Oi0zMjBweDtiYWNrZ3JvdW5kOiNlYWVhZWE7cGFkZGluZzo0MHB4O2JvcmRlci1yYWRpdXM6MH1AbWVkaWEgcHJpbnR7LmJlc3Bva2Utc2xpZGV7em9vbToxIWltcG9ydGFudDtoZWlnaHQ6NzQzcHg7d2lkdGg6MTAwJTtwYWdlLWJyZWFrLWJlZm9yZTphbHdheXM7cG9zaXRpb246c3RhdGljO21hcmdpbjowOy13ZWJraXQtdHJhbnNpdGlvbjpub25lO3RyYW5zaXRpb246bm9uZX19LmJlc3Bva2UtYmVmb3Jley13ZWJraXQtdHJhbnNmb3JtOnRyYW5zbGF0ZVgoMTAwcHgpdHJhbnNsYXRlWCgtMzIwcHgpcm90YXRlWSgtOTBkZWcpdHJhbnNsYXRlWCgtMzIwcHgpO3RyYW5zZm9ybTp0cmFuc2xhdGVYKDEwMHB4KXRyYW5zbGF0ZVgoLTMyMHB4KXJvdGF0ZVkoLTkwZGVnKXRyYW5zbGF0ZVgoLTMyMHB4KX1AbWVkaWEgcHJpbnR7LmJlc3Bva2UtYmVmb3Jley13ZWJraXQtdHJhbnNmb3JtOm5vbmU7dHJhbnNmb3JtOm5vbmV9fS5iZXNwb2tlLWFmdGVyey13ZWJraXQtdHJhbnNmb3JtOnRyYW5zbGF0ZVgoLTEwMHB4KXRyYW5zbGF0ZVgoMzIwcHgpcm90YXRlWSg5MGRlZyl0cmFuc2xhdGVYKDMyMHB4KTt0cmFuc2Zvcm06dHJhbnNsYXRlWCgtMTAwcHgpdHJhbnNsYXRlWCgzMjBweClyb3RhdGVZKDkwZGVnKXRyYW5zbGF0ZVgoMzIwcHgpfUBtZWRpYSBwcmludHsuYmVzcG9rZS1hZnRlcnstd2Via2l0LXRyYW5zZm9ybTpub25lO3RyYW5zZm9ybTpub25lfX0uYmVzcG9rZS1pbmFjdGl2ZXtvcGFjaXR5OjA7cG9pbnRlci1ldmVudHM6bm9uZX1AbWVkaWEgcHJpbnR7LmJlc3Bva2UtaW5hY3RpdmV7b3BhY2l0eToxfX0uYmVzcG9rZS1hY3RpdmV7b3BhY2l0eToxfS5iZXNwb2tlLWJ1bGxldHstd2Via2l0LXRyYW5zaXRpb246YWxsIC4zcyBlYXNlO3RyYW5zaXRpb246YWxsIC4zcyBlYXNlfUBtZWRpYSBwcmludHsuYmVzcG9rZS1idWxsZXR7LXdlYmtpdC10cmFuc2l0aW9uOm5vbmU7dHJhbnNpdGlvbjpub25lfX0uYmVzcG9rZS1idWxsZXQtaW5hY3RpdmV7b3BhY2l0eTowfWxpLmJlc3Bva2UtYnVsbGV0LWluYWN0aXZley13ZWJraXQtdHJhbnNmb3JtOnRyYW5zbGF0ZVgoMTZweCk7dHJhbnNmb3JtOnRyYW5zbGF0ZVgoMTZweCl9QG1lZGlhIHByaW50e2xpLmJlc3Bva2UtYnVsbGV0LWluYWN0aXZley13ZWJraXQtdHJhbnNmb3JtOm5vbmU7dHJhbnNmb3JtOm5vbmV9fUBtZWRpYSBwcmludHsuYmVzcG9rZS1idWxsZXQtaW5hY3RpdmV7b3BhY2l0eToxfX0uYmVzcG9rZS1idWxsZXQtYWN0aXZle29wYWNpdHk6MX0uYmVzcG9rZS1zY2FsZS1wYXJlbnR7LXdlYmtpdC1wZXJzcGVjdGl2ZTo2MDBweDtwZXJzcGVjdGl2ZTo2MDBweDtwb3NpdGlvbjphYnNvbHV0ZTt0b3A6MDtsZWZ0OjA7cmlnaHQ6MDtib3R0b206MDtwb2ludGVyLWV2ZW50czpub25lfS5iZXNwb2tlLXNjYWxlLXBhcmVudCAuYmVzcG9rZS1hY3RpdmV7cG9pbnRlci1ldmVudHM6YXV0b31AbWVkaWEgcHJpbnR7LmJlc3Bva2Utc2NhbGUtcGFyZW50ey13ZWJraXQtdHJhbnNmb3JtOm5vbmUhaW1wb3J0YW50O3RyYW5zZm9ybTpub25lIWltcG9ydGFudH19LmJlc3Bva2UtcHJvZ3Jlc3MtcGFyZW50e3Bvc2l0aW9uOmFic29sdXRlO3RvcDowO2xlZnQ6MDtyaWdodDowO2hlaWdodDoycHh9QG1lZGlhIG9ubHkgc2NyZWVuIGFuZCAobWluLXdpZHRoOjEzNjZweCl7LmJlc3Bva2UtcHJvZ3Jlc3MtcGFyZW50e2hlaWdodDo0cHh9fUBtZWRpYSBwcmludHsuYmVzcG9rZS1wcm9ncmVzcy1wYXJlbnR7ZGlzcGxheTpub25lfX0uYmVzcG9rZS1wcm9ncmVzcy1iYXJ7LXdlYmtpdC10cmFuc2l0aW9uOndpZHRoIC42cyBlYXNlO3RyYW5zaXRpb246d2lkdGggLjZzIGVhc2U7cG9zaXRpb246YWJzb2x1dGU7aGVpZ2h0OjEwMCU7YmFja2dyb3VuZDojMDA4OWYzO2JvcmRlci1yYWRpdXM6MCA0cHggNHB4IDB9LmVtcGhhdGlje2JhY2tncm91bmQ6I2VhZWFlYX0uYmVzcG9rZS1iYWNrZHJvcHtwb3NpdGlvbjphYnNvbHV0ZTt0b3A6MDtsZWZ0OjA7cmlnaHQ6MDtib3R0b206MDstd2Via2l0LXRyYW5zZm9ybTp0cmFuc2xhdGVaKDApO3RyYW5zZm9ybTp0cmFuc2xhdGVaKDApOy13ZWJraXQtdHJhbnNpdGlvbjpvcGFjaXR5IC42cyBlYXNlO3RyYW5zaXRpb246b3BhY2l0eSAuNnMgZWFzZTtvcGFjaXR5OjA7ei1pbmRleDotMX0uYmVzcG9rZS1iYWNrZHJvcC1hY3RpdmV7b3BhY2l0eToxfXByZXtwYWRkaW5nOjI2cHghaW1wb3J0YW50O2JvcmRlci1yYWRpdXM6OHB4fWJvZHl7Zm9udC1mYW1pbHk6aGVsdmV0aWNhLGFyaWFsLHNhbnMtc2VyaWY7Zm9udC1zaXplOjE4cHg7Y29sb3I6IzQwNDA0MH1oMXtmb250LXNpemU6NzJweDtsaW5lLWhlaWdodDo4MnB4O2xldHRlci1zcGFjaW5nOi0ycHg7bWFyZ2luLWJvdHRvbToxNnB4fWgye2ZvbnQtc2l6ZTo0MnB4O2xldHRlci1zcGFjaW5nOi0xcHg7bWFyZ2luLWJvdHRvbTo4cHh9aDN7Zm9udC1zaXplOjI0cHg7Zm9udC13ZWlnaHQ6NDAwO21hcmdpbi1ib3R0b206MjRweDtjb2xvcjojNjA2MDYwfWhye3Zpc2liaWxpdHk6aGlkZGVuO2hlaWdodDoyMHB4fXVse2xpc3Qtc3R5bGU6bm9uZX1saXttYXJnaW4tYm90dG9tOjEycHh9cHttYXJnaW46MCAxMDBweCAxMnB4O2xpbmUtaGVpZ2h0OjIycHh9YXtjb2xvcjojMDA4OWYzO3RleHQtZGVjb3JhdGlvbjpub25lfVwiO1xuICBpbnNlcnRDc3MoY3NzLCB7IHByZXBlbmQ6IHRydWUgfSk7XG5cbiAgcmV0dXJuIGZ1bmN0aW9uKGRlY2spIHtcbiAgICBjbGFzc2VzKCkoZGVjayk7XG5cbiAgICB2YXIgd3JhcCA9IGZ1bmN0aW9uKGVsZW1lbnQpIHtcbiAgICAgIHZhciB3cmFwcGVyID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnZGl2Jyk7XG4gICAgICB3cmFwcGVyLmNsYXNzTmFtZSA9ICdiZXNwb2tlLXRoZW1lLWN1YmUtc2xpZGUtcGFyZW50JztcbiAgICAgIGVsZW1lbnQucGFyZW50Tm9kZS5pbnNlcnRCZWZvcmUod3JhcHBlciwgZWxlbWVudCk7XG4gICAgICB3cmFwcGVyLmFwcGVuZENoaWxkKGVsZW1lbnQpO1xuICAgIH07XG5cbiAgICBkZWNrLnNsaWRlcy5mb3JFYWNoKHdyYXApO1xuICB9O1xufTtcblxufSx7XCJiZXNwb2tlLWNsYXNzZXNcIjoyLFwiaW5zZXJ0LWNzc1wiOjN9XSwyOltmdW5jdGlvbihfZGVyZXFfLG1vZHVsZSxleHBvcnRzKXtcbm1vZHVsZS5leHBvcnRzID0gZnVuY3Rpb24oKSB7XG4gIHJldHVybiBmdW5jdGlvbihkZWNrKSB7XG4gICAgdmFyIGFkZENsYXNzID0gZnVuY3Rpb24oZWwsIGNscykge1xuICAgICAgICBlbC5jbGFzc0xpc3QuYWRkKCdiZXNwb2tlLScgKyBjbHMpO1xuICAgICAgfSxcblxuICAgICAgcmVtb3ZlQ2xhc3MgPSBmdW5jdGlvbihlbCwgY2xzKSB7XG4gICAgICAgIGVsLmNsYXNzTmFtZSA9IGVsLmNsYXNzTmFtZVxuICAgICAgICAgIC5yZXBsYWNlKG5ldyBSZWdFeHAoJ2Jlc3Bva2UtJyArIGNscyArJyhcXFxcc3wkKScsICdnJyksICcgJylcbiAgICAgICAgICAudHJpbSgpO1xuICAgICAgfSxcblxuICAgICAgZGVhY3RpdmF0ZSA9IGZ1bmN0aW9uKGVsLCBpbmRleCkge1xuICAgICAgICB2YXIgYWN0aXZlU2xpZGUgPSBkZWNrLnNsaWRlc1tkZWNrLnNsaWRlKCldLFxuICAgICAgICAgIG9mZnNldCA9IGluZGV4IC0gZGVjay5zbGlkZSgpLFxuICAgICAgICAgIG9mZnNldENsYXNzID0gb2Zmc2V0ID4gMCA/ICdhZnRlcicgOiAnYmVmb3JlJztcblxuICAgICAgICBbJ2JlZm9yZSgtXFxcXGQrKT8nLCAnYWZ0ZXIoLVxcXFxkKyk/JywgJ2FjdGl2ZScsICdpbmFjdGl2ZSddLm1hcChyZW1vdmVDbGFzcy5iaW5kKG51bGwsIGVsKSk7XG5cbiAgICAgICAgaWYgKGVsICE9PSBhY3RpdmVTbGlkZSkge1xuICAgICAgICAgIFsnaW5hY3RpdmUnLCBvZmZzZXRDbGFzcywgb2Zmc2V0Q2xhc3MgKyAnLScgKyBNYXRoLmFicyhvZmZzZXQpXS5tYXAoYWRkQ2xhc3MuYmluZChudWxsLCBlbCkpO1xuICAgICAgICB9XG4gICAgICB9O1xuXG4gICAgYWRkQ2xhc3MoZGVjay5wYXJlbnQsICdwYXJlbnQnKTtcbiAgICBkZWNrLnNsaWRlcy5tYXAoZnVuY3Rpb24oZWwpIHsgYWRkQ2xhc3MoZWwsICdzbGlkZScpOyB9KTtcblxuICAgIGRlY2sub24oJ2FjdGl2YXRlJywgZnVuY3Rpb24oZSkge1xuICAgICAgZGVjay5zbGlkZXMubWFwKGRlYWN0aXZhdGUpO1xuICAgICAgYWRkQ2xhc3MoZS5zbGlkZSwgJ2FjdGl2ZScpO1xuICAgICAgcmVtb3ZlQ2xhc3MoZS5zbGlkZSwgJ2luYWN0aXZlJyk7XG4gICAgfSk7XG4gIH07XG59O1xuXG59LHt9XSwzOltmdW5jdGlvbihfZGVyZXFfLG1vZHVsZSxleHBvcnRzKXtcbnZhciBpbnNlcnRlZCA9IHt9O1xuXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uIChjc3MsIG9wdGlvbnMpIHtcbiAgICBpZiAoaW5zZXJ0ZWRbY3NzXSkgcmV0dXJuO1xuICAgIGluc2VydGVkW2Nzc10gPSB0cnVlO1xuICAgIFxuICAgIHZhciBlbGVtID0gZG9jdW1lbnQuY3JlYXRlRWxlbWVudCgnc3R5bGUnKTtcbiAgICBlbGVtLnNldEF0dHJpYnV0ZSgndHlwZScsICd0ZXh0L2NzcycpO1xuXG4gICAgaWYgKCd0ZXh0Q29udGVudCcgaW4gZWxlbSkge1xuICAgICAgZWxlbS50ZXh0Q29udGVudCA9IGNzcztcbiAgICB9IGVsc2Uge1xuICAgICAgZWxlbS5zdHlsZVNoZWV0LmNzc1RleHQgPSBjc3M7XG4gICAgfVxuICAgIFxuICAgIHZhciBoZWFkID0gZG9jdW1lbnQuZ2V0RWxlbWVudHNCeVRhZ05hbWUoJ2hlYWQnKVswXTtcbiAgICBpZiAob3B0aW9ucyAmJiBvcHRpb25zLnByZXBlbmQpIHtcbiAgICAgICAgaGVhZC5pbnNlcnRCZWZvcmUoZWxlbSwgaGVhZC5jaGlsZE5vZGVzWzBdKTtcbiAgICB9IGVsc2Uge1xuICAgICAgICBoZWFkLmFwcGVuZENoaWxkKGVsZW0pO1xuICAgIH1cbn07XG5cbn0se31dfSx7fSxbMV0pXG4oMSlcbn0pO1xufSkuY2FsbCh0aGlzLHR5cGVvZiBzZWxmICE9PSBcInVuZGVmaW5lZFwiID8gc2VsZiA6IHR5cGVvZiB3aW5kb3cgIT09IFwidW5kZWZpbmVkXCIgPyB3aW5kb3cgOiB7fSkiLCJtb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKG9wdGlvbnMpIHtcbiAgcmV0dXJuIGZ1bmN0aW9uKGRlY2spIHtcbiAgICB2YXIgYXhpcyA9IG9wdGlvbnMgPT0gJ3ZlcnRpY2FsJyA/ICdZJyA6ICdYJyxcbiAgICAgIHN0YXJ0UG9zaXRpb24sXG4gICAgICBkZWx0YTtcblxuICAgIGRlY2sucGFyZW50LmFkZEV2ZW50TGlzdGVuZXIoJ3RvdWNoc3RhcnQnLCBmdW5jdGlvbihlKSB7XG4gICAgICBpZiAoZS50b3VjaGVzLmxlbmd0aCA9PSAxKSB7XG4gICAgICAgIHN0YXJ0UG9zaXRpb24gPSBlLnRvdWNoZXNbMF1bJ3BhZ2UnICsgYXhpc107XG4gICAgICAgIGRlbHRhID0gMDtcbiAgICAgIH1cbiAgICB9KTtcblxuICAgIGRlY2sucGFyZW50LmFkZEV2ZW50TGlzdGVuZXIoJ3RvdWNobW92ZScsIGZ1bmN0aW9uKGUpIHtcbiAgICAgIGlmIChlLnRvdWNoZXMubGVuZ3RoID09IDEpIHtcbiAgICAgICAgZS5wcmV2ZW50RGVmYXVsdCgpO1xuICAgICAgICBkZWx0YSA9IGUudG91Y2hlc1swXVsncGFnZScgKyBheGlzXSAtIHN0YXJ0UG9zaXRpb247XG4gICAgICB9XG4gICAgfSk7XG5cbiAgICBkZWNrLnBhcmVudC5hZGRFdmVudExpc3RlbmVyKCd0b3VjaGVuZCcsIGZ1bmN0aW9uKCkge1xuICAgICAgaWYgKE1hdGguYWJzKGRlbHRhKSA+IDUwKSB7XG4gICAgICAgIGRlY2tbZGVsdGEgPiAwID8gJ3ByZXYnIDogJ25leHQnXSgpO1xuICAgICAgfVxuICAgIH0pO1xuICB9O1xufTtcbiIsInZhciBmcm9tID0gZnVuY3Rpb24oc2VsZWN0b3JPckVsZW1lbnQsIHBsdWdpbnMpIHtcbiAgdmFyIHBhcmVudCA9IHNlbGVjdG9yT3JFbGVtZW50Lm5vZGVUeXBlID09PSAxID8gc2VsZWN0b3JPckVsZW1lbnQgOiBkb2N1bWVudC5xdWVyeVNlbGVjdG9yKHNlbGVjdG9yT3JFbGVtZW50KSxcbiAgICBzbGlkZXMgPSBbXS5maWx0ZXIuY2FsbChwYXJlbnQuY2hpbGRyZW4sIGZ1bmN0aW9uKGVsKSB7IHJldHVybiBlbC5ub2RlTmFtZSAhPT0gJ1NDUklQVCc7IH0pLFxuICAgIGFjdGl2ZVNsaWRlID0gc2xpZGVzWzBdLFxuICAgIGxpc3RlbmVycyA9IHt9LFxuXG4gICAgYWN0aXZhdGUgPSBmdW5jdGlvbihpbmRleCwgY3VzdG9tRGF0YSkge1xuICAgICAgaWYgKCFzbGlkZXNbaW5kZXhdKSB7XG4gICAgICAgIHJldHVybjtcbiAgICAgIH1cblxuICAgICAgZmlyZSgnZGVhY3RpdmF0ZScsIGNyZWF0ZUV2ZW50RGF0YShhY3RpdmVTbGlkZSwgY3VzdG9tRGF0YSkpO1xuICAgICAgYWN0aXZlU2xpZGUgPSBzbGlkZXNbaW5kZXhdO1xuICAgICAgZmlyZSgnYWN0aXZhdGUnLCBjcmVhdGVFdmVudERhdGEoYWN0aXZlU2xpZGUsIGN1c3RvbURhdGEpKTtcbiAgICB9LFxuXG4gICAgc2xpZGUgPSBmdW5jdGlvbihpbmRleCwgY3VzdG9tRGF0YSkge1xuICAgICAgaWYgKGFyZ3VtZW50cy5sZW5ndGgpIHtcbiAgICAgICAgZmlyZSgnc2xpZGUnLCBjcmVhdGVFdmVudERhdGEoc2xpZGVzW2luZGV4XSwgY3VzdG9tRGF0YSkpICYmIGFjdGl2YXRlKGluZGV4LCBjdXN0b21EYXRhKTtcbiAgICAgIH0gZWxzZSB7XG4gICAgICAgIHJldHVybiBzbGlkZXMuaW5kZXhPZihhY3RpdmVTbGlkZSk7XG4gICAgICB9XG4gICAgfSxcblxuICAgIHN0ZXAgPSBmdW5jdGlvbihvZmZzZXQsIGN1c3RvbURhdGEpIHtcbiAgICAgIHZhciBzbGlkZUluZGV4ID0gc2xpZGVzLmluZGV4T2YoYWN0aXZlU2xpZGUpICsgb2Zmc2V0O1xuXG4gICAgICBmaXJlKG9mZnNldCA+IDAgPyAnbmV4dCcgOiAncHJldicsIGNyZWF0ZUV2ZW50RGF0YShhY3RpdmVTbGlkZSwgY3VzdG9tRGF0YSkpICYmIGFjdGl2YXRlKHNsaWRlSW5kZXgsIGN1c3RvbURhdGEpO1xuICAgIH0sXG5cbiAgICBvbiA9IGZ1bmN0aW9uKGV2ZW50TmFtZSwgY2FsbGJhY2spIHtcbiAgICAgIChsaXN0ZW5lcnNbZXZlbnROYW1lXSB8fCAobGlzdGVuZXJzW2V2ZW50TmFtZV0gPSBbXSkpLnB1c2goY2FsbGJhY2spO1xuXG4gICAgICByZXR1cm4gZnVuY3Rpb24oKSB7XG4gICAgICAgIGxpc3RlbmVyc1tldmVudE5hbWVdID0gbGlzdGVuZXJzW2V2ZW50TmFtZV0uZmlsdGVyKGZ1bmN0aW9uKGxpc3RlbmVyKSB7XG4gICAgICAgICAgcmV0dXJuIGxpc3RlbmVyICE9PSBjYWxsYmFjaztcbiAgICAgICAgfSk7XG4gICAgICB9O1xuICAgIH0sXG5cbiAgICBmaXJlID0gZnVuY3Rpb24oZXZlbnROYW1lLCBldmVudERhdGEpIHtcbiAgICAgIHJldHVybiAobGlzdGVuZXJzW2V2ZW50TmFtZV0gfHwgW10pXG4gICAgICAgIC5yZWR1Y2UoZnVuY3Rpb24obm90Q2FuY2VsbGVkLCBjYWxsYmFjaykge1xuICAgICAgICAgIHJldHVybiBub3RDYW5jZWxsZWQgJiYgY2FsbGJhY2soZXZlbnREYXRhKSAhPT0gZmFsc2U7XG4gICAgICAgIH0sIHRydWUpO1xuICAgIH0sXG5cbiAgICBjcmVhdGVFdmVudERhdGEgPSBmdW5jdGlvbihlbCwgZXZlbnREYXRhKSB7XG4gICAgICBldmVudERhdGEgPSBldmVudERhdGEgfHwge307XG4gICAgICBldmVudERhdGEuaW5kZXggPSBzbGlkZXMuaW5kZXhPZihlbCk7XG4gICAgICBldmVudERhdGEuc2xpZGUgPSBlbDtcbiAgICAgIHJldHVybiBldmVudERhdGE7XG4gICAgfSxcblxuICAgIGRlY2sgPSB7XG4gICAgICBvbjogb24sXG4gICAgICBmaXJlOiBmaXJlLFxuICAgICAgc2xpZGU6IHNsaWRlLFxuICAgICAgbmV4dDogc3RlcC5iaW5kKG51bGwsIDEpLFxuICAgICAgcHJldjogc3RlcC5iaW5kKG51bGwsIC0xKSxcbiAgICAgIHBhcmVudDogcGFyZW50LFxuICAgICAgc2xpZGVzOiBzbGlkZXNcbiAgICB9O1xuXG4gIChwbHVnaW5zIHx8IFtdKS5mb3JFYWNoKGZ1bmN0aW9uKHBsdWdpbikge1xuICAgIHBsdWdpbihkZWNrKTtcbiAgfSk7XG5cbiAgYWN0aXZhdGUoMCk7XG5cbiAgcmV0dXJuIGRlY2s7XG59O1xuXG5tb2R1bGUuZXhwb3J0cyA9IHtcbiAgZnJvbTogZnJvbVxufTtcbiIsIi8vIFJlcXVpcmUgTm9kZSBtb2R1bGVzIGluIHRoZSBicm93c2VyIHRoYW5rcyB0byBCcm93c2VyaWZ5OiBodHRwOi8vYnJvd3NlcmlmeS5vcmdcbnZhciBiZXNwb2tlID0gcmVxdWlyZSgnYmVzcG9rZScpLFxuICBjdWJlID0gcmVxdWlyZSgnYmVzcG9rZS10aGVtZS1jdWJlJyksXG4gIGtleXMgPSByZXF1aXJlKCdiZXNwb2tlLWtleXMnKSxcbiAgdG91Y2ggPSByZXF1aXJlKCdiZXNwb2tlLXRvdWNoJyksXG4gIGJ1bGxldHMgPSByZXF1aXJlKCdiZXNwb2tlLWJ1bGxldHMnKSxcbiAgYmFja2Ryb3AgPSByZXF1aXJlKCdiZXNwb2tlLWJhY2tkcm9wJyksXG4gIHNjYWxlID0gcmVxdWlyZSgnYmVzcG9rZS1zY2FsZScpLFxuICBoYXNoID0gcmVxdWlyZSgnYmVzcG9rZS1oYXNoJyksXG4gIHByb2dyZXNzID0gcmVxdWlyZSgnYmVzcG9rZS1wcm9ncmVzcycpLFxuICBmb3JtcyA9IHJlcXVpcmUoJ2Jlc3Bva2UtZm9ybXMnKTtcblxuLy8gQmVzcG9rZS5qc1xuYmVzcG9rZS5mcm9tKCdhcnRpY2xlJywgW1xuICBjdWJlKCksXG4gIGtleXMoKSxcbiAgdG91Y2goKSxcbiAgYnVsbGV0cygnbGksIC5idWxsZXQnKSxcbiAgYmFja2Ryb3AoKSxcbiAgc2NhbGUoKSxcbiAgaGFzaCgpLFxuICBwcm9ncmVzcygpLFxuICBmb3JtcygpXG5dKTtcblxuLy8gUHJpc20gc3ludGF4IGhpZ2hsaWdodGluZ1xuLy8gVGhpcyBpcyBhY3R1YWxseSBsb2FkZWQgZnJvbSBcImJvd2VyX2NvbXBvbmVudHNcIiB0aGFua3MgdG9cbi8vIGRlYm93ZXJpZnk6IGh0dHBzOi8vZ2l0aHViLmNvbS9ldWdlbmV3YXJlL2RlYm93ZXJpZnlcbnJlcXVpcmUoXCIuLy4uXFxcXC4uXFxcXGJvd2VyX2NvbXBvbmVudHNcXFxccHJpc21cXFxccHJpc20uanNcIik7XG5cbiJdfQ==
