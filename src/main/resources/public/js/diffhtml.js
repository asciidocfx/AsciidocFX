(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.diff = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(_dereq_,module,exports){
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.makeElement = makeElement;

    function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

    var _node = _dereq_('./node');

    var _svg = _dereq_('./svg');

    var svg = _interopRequireWildcard(_svg);

    /**
     * makeElement
     *
     * @param descriptor
     * @return
     */

    function makeElement(descriptor) {
        var element = null;
        var isSvg = false;

        if (descriptor.nodeName === '#text') {
            element = document.createTextNode(descriptor.nodeValue);
        } else {
            if (svg.elements.indexOf(descriptor.nodeName) > -1) {
                isSvg = true;
                element = document.createElementNS(svg.namespace, descriptor.nodeName);
            } else {
                element = document.createElement(descriptor.nodeName);
            }

            if (descriptor.attributes && descriptor.attributes.length) {
                for (var i = 0; i < descriptor.attributes.length; i++) {
                    var attribute = descriptor.attributes[i];
                    if (isSvg) {
                        element.setAttributeNS(null, attribute.name, attribute.value);
                    } else {
                        element.setAttribute(attribute.name, attribute.value);
                    }
                }
            }

            if (descriptor.childNodes && descriptor.childNodes.length) {
                for (var i = 0; i < descriptor.childNodes.length; i++) {
                    element.appendChild(makeElement(descriptor.childNodes[i]));
                }
            }
        }

        // Add to the nodes cache using the designated id.
        _node.makeNode.nodes[descriptor.element] = element;

        return element;
    }

},{"./node":4,"./svg":5}],2:[function(_dereq_,module,exports){
    /**
     * The tran
     *
     * @class
     * @return
     */
    "use strict";

    Object.defineProperty(exports, "__esModule", {
        value: true
    });

    var _get = function get(_x, _x2, _x3) { var _again = true; _function: while (_again) { var object = _x, property = _x2, receiver = _x3; desc = parent = getter = undefined; _again = false; if (object === null) object = Function.prototype; var desc = Object.getOwnPropertyDescriptor(object, property); if (desc === undefined) { var parent = Object.getPrototypeOf(object); if (parent === null) { return undefined; } else { _x = parent; _x2 = property; _x3 = receiver; _again = true; continue _function; } } else if ("value" in desc) { return desc.value; } else { var getter = desc.get; if (getter === undefined) { return undefined; } return getter.call(receiver); } } };

    function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

    function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

    var TransitionStateError = (function (_Error) {
        _inherits(TransitionStateError, _Error);

        function TransitionStateError(message) {
            _classCallCheck(this, TransitionStateError);

            _get(Object.getPrototypeOf(TransitionStateError.prototype), "constructor", this).call(this);

            this.message = message;
        }

        return TransitionStateError;
    })(Error);

    exports.TransitionStateError = TransitionStateError;

},{}],3:[function(_dereq_,module,exports){
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.outerHTML = outerHTML;
    exports.innerHTML = innerHTML;
    exports.element = element;
    exports.addTransitionState = addTransitionState;
    exports.removeTransitionState = removeTransitionState;
    exports.enableProllyfill = enableProllyfill;

    var _node = _dereq_('./node');

    var _transitions = _dereq_('./transitions');

    var _errors = _dereq_('./errors');

    Object.defineProperty(exports, 'TransitionStateError', {
        enumerable: true,
        get: function get() {
            return _errors.TransitionStateError;
        }
    });

    /**
     * Used to diff the outerHTML contents of the passed element with the markup
     * contents.  Very useful for applying a global diff on the
     * `document.documentElement`.
     *
     * @param element
     * @param markup=''
     * @param options={}
     */

    function outerHTML(element) {
        var markup = arguments.length <= 1 || arguments[1] === undefined ? '' : arguments[1];
        var options = arguments.length <= 2 || arguments[2] === undefined ? {} : arguments[2];

        options.inner = false;
        (0, _node.patchNode)(element, markup, options);
    }

    /**
     * Used to diff the innerHTML contents of the passed element with the markup
     * contents.  This is useful with libraries like Backbone that render Views
     * into element container.
     *
     * @param element
     * @param markup=''
     * @param options={}
     * @return
     */

    function innerHTML(element) {
        var markup = arguments.length <= 1 || arguments[1] === undefined ? '' : arguments[1];
        var options = arguments.length <= 2 || arguments[2] === undefined ? {} : arguments[2];

        options.inner = true;
        (0, _node.patchNode)(element, markup, options);
    }

    /**
     * Used to diff two elements.  The `inner` Boolean property can be specified in
     * the options to set innerHTML\outerHTML behavior.  By default it is
     * outerHTML.
     *
     * @param element
     * @param newElement
     * @param options={}
     * @return
     */

    function element(element, newElement) {
        var options = arguments.length <= 2 || arguments[2] === undefined ? {} : arguments[2];

        (0, _node.patchNode)(element, newElement, options);
    }

    /**
     * Adds a global transition listener.  With many elements this could be an
     * expensive operation, so try to limit the amount of listeners added if you're
     * concerned about performance.
     *
     * Since the callback triggers with various elements, most of which you
     * probably don't care about, you'll want to filter.  A good way of filtering
     * is to use the DOM `matches` method.  It's fairly well supported
     * (http://caniuse.com/#feat=matchesselector) and may suit many projects.  If
     * you need backwards compatibility, consider using jQuery's `is`.
     *
     * You can do fun, highly specific, filters:
     *
     * addTransitionState('added', function(element) {
 *   // Fade in the main container after it's added.
 *   if (element.matches('body main.container')) {
 *     $(element).stop(true, true).fadeIn();
 *   }
 * });
     *
     * @param state - String name that matches what's available in the
     * documentation above.
     * @param callback - Function to receive the matching elements.
     */

    function addTransitionState(state, callback) {
        if (!state) {
            throw new _errors.TransitionStateError('Missing transition state name');
        }

        if (!callback) {
            throw new _errors.TransitionStateError('Missing transition state callback');
        }

        // Not a valid state name.
        if (Object.keys(_transitions.transitionStates).indexOf(state) === -1) {
            throw new _errors.TransitionStateError('Invalid state name: ' + state);
        }

        _transitions.transitionStates[state].push(callback);
    }

    /**
     * Removes a global transition listener.
     *
     * When invoked with no arguments, this method will remove all transition
     * callbacks.  When invoked with the name argument it will remove all
     * transition state callbacks matching the name, and so on for the callback.
     *
     * @param state - String name that matches what's available in the
     * documentation above.
     * @param callback - Function to receive the matching elements.
     */

    function removeTransitionState(state, callback) {
        if (!callback && state) {
            _transitions.transitionStates[state].length = 0;
        } else if (state && callback) {
            // Not a valid state name.
            if (Object.keys(_transitions.transitionStates).indexOf(state) === -1) {
                throw new _errors.TransitionStateError('Invalid state name ' + state);
            }

            var index = _transitions.transitionStates[state].indexOf(callback);
            _transitions.transitionStates[state].splice(index, 1);
        } else {
            for (var _state in _transitions.transitionStates) {
                _transitions.transitionStates[_state].length = 0;
            }
        }
    }

    /**
     * enableProllyfill
     *
     * @return
     */

    function enableProllyfill() {
        Object.defineProperty(Element.prototype, 'addTransitionState', {
            configurable: true,

            value: function value(state, callback) {
                addTransitionState(state, callback);
            }
        });

        Object.defineProperty(Element.prototype, 'removeTransitionState', {
            configurable: true,

            value: function value(state, callback) {
                removeTransitionState(state, callback);
            }
        });

        Object.defineProperty(Element.prototype, 'diffInnerHTML', {
            configurable: true,

            set: function set(newHTML) {
                innerHTML(this, newHTML);
            }
        });

        Object.defineProperty(Element.prototype, 'diffOuterHTML', {
            configurable: true,

            set: function set(newHTML) {
                outerHTML(this, newHTML);
            }
        });

        Object.defineProperty(Element.prototype, 'diffElement', {
            configurable: true,

            value: function value(newElement) {
                element(this, newElement);
            }
        });
    }

},{"./errors":2,"./node":4,"./transitions":6}],4:[function(_dereq_,module,exports){
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.syncNode = syncNode;
    exports.makeNode = makeNode;
    exports.patchNode = patchNode;

    function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

    function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

    var _utilBuffers = _dereq_('./util/buffers');

    var buffers = _interopRequireWildcard(_utilBuffers);

    var _utilPools = _dereq_('./util/pools');

    var _utilParser = _dereq_('./util/parser');

    var _utilUuid = _dereq_('./util/uuid');

    var _element = _dereq_('./element');

    var _transitions = _dereq_('./transitions');

    var _worker = _dereq_('./worker');

    var _worker2 = _interopRequireDefault(_worker);

    var pools = _utilPools.pools;
    var poolCount = 10000;
    var nodes = makeNode.nodes = {};

// Initialize with a reasonable amount of objects.
    (0, _utilPools.initializePools)(poolCount);

    var push = Array.prototype.push;
    var slice = Array.prototype.slice;
    var filter = Array.prototype.filter;

    /**
     * syncNode
     *
     * @param virtualNode
     * @param liveNode
     * @return
     */

    function syncNode(virtualNode, liveNode) {
        var patches = this;

        // For now always sync the children.  In the future we'll be smarter about
        // when this is necessary.
        var oldChildNodes = virtualNode.childNodes;
        var oldChildNodesLength = oldChildNodes ? oldChildNodes.length : 0;

        if (!liveNode) {
            patches.push({ __do__: -1, element: virtualNode.element });

            virtualNode.childNodes.splice(0, oldChildNodesLength);
            return;
        }

        var nodeValue = liveNode.nodeValue;

        // Filter down the childNodes to only what we care about.
        var childNodes = liveNode.childNodes;
        var newChildNodesLength = childNodes ? childNodes.length : 0;

        // If the element we're replacing is totally different from the previous
        // replace the entire element, don't bother investigating children.
        if (virtualNode.nodeName !== liveNode.nodeName) {
            return;
        }

        // Replace text node values if they are different.
        if (liveNode.nodeName === '#text' && virtualNode.nodeName === '#text') {
            // Text changed.
            if (virtualNode.nodeValue !== liveNode.nodeValue) {
                virtualNode.nodeValue = liveNode.nodeValue;

                patches.push({
                    __do__: 3,
                    element: virtualNode.element,
                    value: nodeValue
                });
            }

            return;
        }

        // Most common additive elements.
        if (newChildNodesLength > oldChildNodesLength) {
            // Store elements in a DocumentFragment to increase performance and be
            // generally simplier to work with.
            var fragment = pools.array.get();

            for (var i = oldChildNodesLength; i < newChildNodesLength; i++) {
                // Internally add to the tree.
                virtualNode.childNodes.push(childNodes[i]);

                // Add to the document fragment.
                fragment.push(childNodes[i]);
            }

            // Assign the fragment to the patches to be injected.
            patches.push({
                __do__: 1,
                element: virtualNode.element,
                fragment: fragment
            });
        }

        // Replace elements if they are different.
        for (var i = 0; i < newChildNodesLength; i++) {
            if (virtualNode.childNodes[i].nodeName !== childNodes[i].nodeName) {
                // Add to the patches.
                patches.push({
                    __do__: 1,
                    old: virtualNode.childNodes[i],
                    'new': childNodes[i]
                });

                // Replace the internal tree's point of view of this element.
                virtualNode.childNodes[i] = childNodes[i];
            }
        }

        // Remove these elements.
        if (oldChildNodesLength > newChildNodesLength) {
            // Elements to remove.
            var toRemove = slice.call(virtualNode.childNodes, newChildNodesLength, oldChildNodesLength);

            for (var i = 0; i < toRemove.length; i++) {
                // Remove the element, this happens before the splice so that we still
                // have access to the element.
                patches.push({ __do__: 1, old: toRemove[i].element });
            }

            virtualNode.childNodes.splice(newChildNodesLength, oldChildNodesLength - newChildNodesLength);
        }

        // Synchronize attributes
        var attributes = liveNode.attributes;

        if (attributes) {
            var oldLength = virtualNode.attributes.length;
            var newLength = attributes.length;

            // Start with the most common, additive.
            if (newLength > oldLength) {
                var toAdd = slice.call(attributes, oldLength);

                for (var i = 0; i < toAdd.length; i++) {
                    var change = {
                        __do__: 2,
                        element: virtualNode.element,
                        name: toAdd[i].name,
                        value: toAdd[i].value
                    };

                    // Push the change object into into the virtual tree.
                    virtualNode.attributes.push({
                        name: toAdd[i].name,
                        value: toAdd[i].value
                    });

                    // Add the change to the series of patches.
                    patches.push(change);
                }
            }

            // Check for removals.
            if (oldLength > newLength) {
                var toRemove = slice.call(virtualNode.attributes, newLength);

                for (var i = 0; i < toRemove.length; i++) {
                    var change = {
                        __do__: 2,
                        element: virtualNode.element,
                        name: toRemove[i].name,
                        value: undefined
                    };

                    // Remove the attribute from the virtual node.
                    virtualNode.attributes.splice(i, 1);

                    // Add the change to the series of patches.
                    patches.push(change);
                }
            }

            // Check for modifications.
            var toModify = attributes;

            for (var i = 0; i < toModify.length; i++) {
                var oldAttrValue = virtualNode.attributes[i] && virtualNode.attributes[i].value;
                var newAttrValue = attributes[i] && attributes[i].value;

                // Only push in a change if the attribute or value changes.
                if (oldAttrValue !== newAttrValue) {
                    var change = {
                        __do__: 2,
                        element: virtualNode.element,
                        name: toModify[i].name,
                        value: toModify[i].value
                    };

                    // Replace the attribute in the virtual node.
                    virtualNode.attributes[i].name = toModify[i].name;
                    virtualNode.attributes[i].value = toModify[i].value;

                    // Add the change to the series of patches.
                    patches.push(change);
                }
            }
        }

        // Sync each current node.
        for (var i = 0; i < virtualNode.childNodes.length; i++) {
            if (virtualNode.childNodes[i] !== childNodes[i]) {
                syncNode.call(patches, virtualNode.childNodes[i], childNodes[i]);
            }
        }
    }

    /**
     * Converts a live node into a virtual node.
     *
     * @param node
     * @return
     */

    function makeNode(node) {
        // If this node has already been converted, do not attempt to convert again.
        if (node && node.__node__) {
            return node.__node__;
        }

        var nodeType = node.nodeType;
        var nodeValue = node.nodeValue;

        if (!nodeType || nodeType === 2 || nodeType === 4 || nodeType === 8) {
            return false;
        }

        if (nodeType === 3 && !nodeValue.trim()) {
            return false;
        }

        // Virtual representation of a node, containing only the data we wish to
        // diff and patch.
        var entry = {};

        // Cache the element in the ids.
        var id = pools.uuid.get();

        // Add to internal lookup.
        nodes[id] = node;

        // Save a reference to this object.
        node.__node__ = entry;

        entry.element = id;
        entry.nodeName = node.nodeName.toLowerCase();
        entry.nodeValue = nodeValue;
        entry.childNodes = [];
        entry.attributes = [];

        // Collect attributes.
        var attributes = node.attributes;

        // If the element has no attributes, skip over.
        if (attributes) {
            var attributesLength = attributes.length;

            if (attributesLength) {
                for (var i = 0; i < attributesLength; i++) {
                    push.call(entry.attributes, {
                        name: attributes[i].name,
                        value: attributes[i].value
                    });
                }
            }
        }

        // Collect childNodes.
        var childNodes = node.childNodes;
        var childNodesLength = node.childNodes.length;
        var newNode = null;

        // If the element has child nodes, convert them all to virtual nodes.
        if (node.nodeType !== 3 && childNodes) {
            for (var i = 0; i < childNodesLength; i++) {
                newNode = makeNode(childNodes[i]);

                if (newNode) {
                    entry.childNodes.push(newNode);
                }
            }
        }

        return entry;
    }

    var hasWorker = typeof Worker === 'function';

// Set up a WebWorker if available.
    if (hasWorker) {
        // Construct the worker reusing code already organized into modules.
        var workerBlob = new Blob([[
            // Reusable Array methods.
            'var slice = Array.prototype.slice;', 'var filter = Array.prototype.filter;',

            // Add a namespace to attach pool methods to.
            'var pools = {};', 'var nodes = 0;',

            // Adds in a global `uuid` function.
            _utilUuid.uuid,

            // Add in pool manipulation methods.
            _utilPools.createPool, _utilPools.initializePools, 'initializePools(' + poolCount + ');',

            // Add in Node manipulation.
            syncNode,

            // Add in the ability to parseHTML.
            _utilParser.parseHTML,

            // Give the webworker utilities.
            buffers.stringToBuffer, buffers.bufferToString, _utilParser.makeParser, 'var parser = makeParser();',

            // Add in the worker source.
            _worker2['default'],

            // Metaprogramming up this worker call.
            'startup(self);'].join('\n')], { type: 'application/javascript' });

        // Construct the worker and start it up.
        var worker = new Worker(URL.createObjectURL(workerBlob));
    }

    /**
     * getElement
     *
     * @param ref
     * @return
     */
    function getElement(ref) {
        var element = ref.element || ref;

        // Already created.
        if (element in makeNode.nodes) {
            return makeNode.nodes[element];
        }
        // Need to create.
        else {
            return (0, _element.makeElement)(ref);
        }
    }

    /**
     * Processes an Array of patches.
     *
     * @param e
     * @return
     */
    function processPatches(element, e) {
        var patches = e.data;
        var states = _transitions.transitionStates;

        var callCallback = function callCallback(callback) {
            callback(this);
        };

        var attachedCallback = function attachedCallback(elementDescriptor) {
            var element = getElement(elementDescriptor);

            this.fragment.appendChild(element);

            // Trigger all the text changed values.
            if (states && element.nodeName === '#text' && states.textChanged) {
                for (var x = 0; x < states.textChanged.length; x++) {
                    var callback = states.textChanged[x];
                    callback(fragment.parentNode, null, fragment.textContent);
                }
            }

            // Added state for transitions API.
            if (states && states.attached) {
                states.attached.forEach(callCallback, element);
            }
        };

        var titleCallback = function titleCallback(elementDescriptor) {
            var element = getElement(elementDescriptor);

            // Ensure the title is set correctly.
            if (element.tagName === 'title') {
                element.ownerDocument.title = element.childNodes[0].nodeValue;
            }
        };

        // Loop through all the patches and apply them.

        var _loop = function () {
            var patch = patches[i];
            var elementId = undefined,
                oldId = undefined,
                newId = undefined;

            if (patch.element) {
                patch.element = getElement(patch.element);
                elementId = patch.element;
            }

            if (patch.old) {
                patch.old = getElement(patch.old);
                oldId = patch.old.element;
            }

            if (patch['new']) {
                patch['new'] = getElement(patch['new']);
                newId = patch['new'].element;
            }

            // Replace the entire Node.
            if (patch.__do__ === 0) {
                patch.old.parentNode.replaceChild(patch['new'], patch.old);
            }

            // Node manip.
            else if (patch.__do__ === 1) {
                // Add.
                if (patch.element && patch.fragment && !patch.old) {
                    fragment = document.createDocumentFragment();

                    patch.fragment.forEach(attachedCallback, { fragment: fragment });
                    patch.element.appendChild(fragment);
                    patch.fragment.forEach(titleCallback);
                }

                // Remove
                else if (patch.old && !patch['new']) {
                    if (!patch.old.parentNode) {
                        throw new Error('Can\'t remove without parent, is this the ' + 'document root?');
                    }

                    if (states && states.detached) {
                        states.detached.forEach(callCallback, patch.old);
                    }

                    // Ensure the title is emptied.
                    if (patch.old.tagName === 'title') {
                        patch.old.ownerDocument.title = '';
                    }

                    patch.old.parentNode.removeChild(patch.old);
                    makeNode.nodes[oldId] = null;
                    delete makeNode.nodes[oldId];
                }

                // Replace
                else if (patch.old && patch['new']) {
                    if (!patch.old.parentNode) {
                        throw new Error('Can\'t replace without parent, is this the ' + 'document root?');
                    }

                    // Append the element first, before doing the replacement.
                    patch.old.parentNode.insertBefore(patch['new'], patch.old.nextSibling);

                    // Added state for transitions API.
                    if (states && states.attached) {
                        states.attached.forEach(function (callback) {
                            callback(patch['new']);
                        });
                    }

                    // Removed state for transitions API.
                    if (states && states.detached) {
                        states.detached.forEach(function (callback) {
                            callback(patch.old);
                        });
                    }

                    // Replaced state for transitions API.
                    if (states && states.replaced) {
                        states.replaced.forEach(function (callback) {
                            callback(patch.old, patch['new']);
                        });
                    }

                    // Ensure the title is set correctly.
                    if (patch['new'].tagName === 'title') {
                        patch.old.ownerDocument.title = patch['new'].childNodes[0].nodeValue;
                    }

                    patch.old.parentNode.replaceChild(patch['new'], patch.old);
                    makeNode.nodes[oldId] = null;
                    delete makeNode.nodes[oldId];
                }
            }

            // Attribute manipulation.
            else if (patch.__do__ === 2) {
                originalValue = patch.element.getAttribute(patch.name);

                // Remove.
                if (!patch.value) {
                    patch.element.removeAttribute(patch.name);
                } else {
                    patch.element.setAttribute(patch.name, patch.value);
                }

                // Trigger all the attribute changed values.
                if (states && states.attributeChanged) {
                    for (x = 0; x < states.attributeChanged.length; x++) {
                        callback = states.attributeChanged[x];

                        callback(patch.element, patch.name, originalValue, patch.value);
                    }
                }
            }

            // Text node manipulation.
            else if (patch.__do__ === 3) {
                originalValue = patch.element.textContent;

                patch.element.textContent = patch.value;

                if (patch.element.parentNode === null) {
                    document.title = patch.value;
                } else {
                    // Trigger all the text changed values.
                    if (states && states.textChanged) {
                        for (x = 0; x < states.textChanged.length; x++) {
                            callback = states.textChanged[x];

                            callback(patch.element.parentNode, originalValue, patch.value);
                        }
                    }
                }
            }
        };

        for (var i = 0; i < patches.length; i++) {
            var fragment;
            var originalValue;
            var x;
            var callback;
            var originalValue;
            var x;
            var callback;

            _loop();
        }
    }

    /**
     * Patches an element's DOM to match that of the passed markup.
     *
     * @param element
     * @param newHTML
     */

    function patchNode(element, newHTML, options) {
        // Ensure that the document disable worker is always picked up.
        if (typeof options.enableWorker !== 'boolean') {
            options.enableWorker = document.ENABLE_WORKER;
        }

        if (element.__is_rendering__) {
            return;
        }

        //TODO New error here
        //if (typeof newHTML !== 'string') {
        //  throw new Error('Invalid type passed to diffHTML, expected String');
        //}

        // Only calculate the parent's initial state one time.
        if (!element.__old_tree__) {
            element.__old_tree__ = makeNode(element);
        }
        // InnerHTML is the same.
        else if (options.inner && element.innerHTML === newHTML) {
            return;
        }
        // OuterHTML is the same.
        else if (!options.inner && element.outerHTML === newHTML) {
            return;
        }

        // Will want to ensure that the first render went through, the worker can
        // take a bit to startup and we want to show changes as soon as possible.
        if (options.enableWorker && hasWorker && element.__has_rendered__) {
            // Attach all properties here to transport.
            var transferObject = {
                oldTree: element.__old_tree__
            };

            if (typeof newHTML !== 'string') {
                transferObject.newTree = makeNode(newHTML);

                // Set a render lock as to not flood the worker.
                element.__is_rendering__ = true;

                // Transfer this buffer to the worker, which will take over and process the
                // markup.
                worker.postMessage(transferObject);

                // Wait for the worker to finish processing and then apply the patchset.
                worker.onmessage = function (e) {
                    processPatches(element, e);
                    element.__is_rendering__ = false;
                };

                return;
            }

            // Used to specify the outerHTML offset if passing the parent's markup.
            var offset = 0;

            // Craft a new buffer with the new contents.
            var newBuffer = buffers.stringToBuffer(newHTML);

            // Set the offset to be this byte length.
            offset = newBuffer.byteLength;

            // Calculate the bytelength for the transfer buffer, contains one extra for
            // the offset.
            var transferByteLength = newBuffer.byteLength;

            // This buffer starts with the offset and contains the data to be carried
            // to the worker.
            var transferBuffer = new Uint16Array(transferByteLength);

            // Set the newHTML payload.
            transferBuffer.set(newBuffer, 0);

            // Add properties to send to worker.
            transferObject.offset = newBuffer.byteLength;
            transferObject.buffer = transferBuffer.buffer;
            transferObject.isInner = options.inner;

            // Set a render lock as to not flood the worker.
            element.__is_rendering__ = true;

            // Transfer this buffer to the worker, which will take over and process the
            // markup.
            worker.postMessage(transferObject, [transferBuffer.buffer]);

            // Wait for the worker to finish processing and then apply the patchset.
            worker.onmessage = function (e) {
                processPatches(element, e);
                element.__is_rendering__ = false;
            };
        } else if (!options.enableWorker || !hasWorker || !element.__has_rendered__) {
            var patches = [];
            var oldTree = element.__old_tree__;
            var newTree = typeof newHTML === 'string' ? (0, _utilParser.parseHTML)(newHTML, options.inner) : makeNode(newHTML);

            if (options.inner) {
                var childNodes = newTree;

                newTree = {
                    attributes: oldTree.attributes,
                    childNodes: childNodes,
                    element: oldTree.element,
                    nodeName: oldTree.nodeName,
                    nodeValue: oldTree.nodeValue
                };
            }

            var oldNodeName = oldTree.nodeName || '';
            var newNodeName = newTree && newTree.nodeName;

            // If the element node types match, try and compare them.
            if (oldNodeName === newNodeName) {
                // Synchronize the tree.
                syncNode.call(patches, element.__old_tree__, newTree);
            }
            // Otherwise replace the top level elements.
            else if (newHTML) {
                patches.push({
                    __do__: 0,
                    old: oldTree,
                    'new': newTree
                });

                element.__old_tree__ = newTree;
            }

            // Process the patches immediately.
            processPatches(element, { data: patches });

            // Mark this element as initially rendered.
            if (!element.__has_rendered__) {
                element.__has_rendered__ = true;
            }

            // Element has stopped rendering.
            element.__is_rendering__ = false;

            // Clean out the patches array.
            patches.length = 0;
        }
    }

},{"./element":1,"./transitions":6,"./util/buffers":7,"./util/parser":8,"./util/pools":9,"./util/uuid":10,"./worker":11}],5:[function(_dereq_,module,exports){
// List of SVG elements.
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    var elements = ['altGlyph', 'altGlyphDef', 'altGlyphItem', 'animate', 'animateColor', 'animateMotion', 'animateTransform', 'circle', 'clipPath', 'color-profile', 'cursor', 'defs', 'desc', 'ellipse', 'feBlend', 'feColorMatrix', 'feComponentTransfer', 'feComposite', 'feConvolveMatrix', 'feDiffuseLighting', 'feDisplacementMap', 'feDistantLight', 'feFlood', 'feFuncA', 'feFuncB', 'feFuncG', 'feFuncR', 'feGaussianBlur', 'feImage', 'feMerge', 'feMergeNode', 'feMorphology', 'feOffset', 'fePointLight', 'feSpecularLighting', 'feSpotLight', 'feTile', 'feTurbulence', 'filter', 'font', 'font-face', 'font-face-format', 'font-face-name', 'font-face-src', 'font-face-uri', 'foreignObject', 'g', 'glyph', 'glyphRef', 'hkern', 'image', 'line', 'linearGradient', 'marker', 'mask', 'metadata', 'missing-glyph', 'mpath', 'path', 'pattern', 'polygon', 'polyline', 'radialGradient', 'rect', 'script', 'set', 'stop', 'style', 'svg', 'switch', 'symbol', 'text', 'textPath', 'title', 'tref', 'tspan', 'use', 'view', 'vkern'];

    exports.elements = elements;
// Namespace.
    var namespace = 'http://www.w3.org/2000/svg';
    exports.namespace = namespace;

},{}],6:[function(_dereq_,module,exports){
    /**
     * Transition states
     * =================
     *
     * - attached - For when elements come into the DOM. The callback triggers
     * ------------ immediately after the element enters the DOM. It is called with
     *              the element as the only argument.
     *
     * - detached - For when elements are removed from the DOM. The callback
     * ------------ triggers just before the element leaves the DOM. It is called
     *              with the element as the only argument.
     *
     * - replaced - For when elements are replaced in the DOM. The callback
     * ------------ triggers after the new element enters the DOM, and before the
     *              old element leaves. It is called with old and new elements as
     *              arguments, in that order.
     *
     * - attributeChanged - Triggered when an element's attribute has changed. The
     * -------------------- callback triggers after the attribute has changed in
     *                      the DOM. It is called with the element, the attribute
     *                      name, old value, and current value.
     *
     * - textChanged - Triggered when an element's `textContent` chnages. The
     * --------------- callback triggers after the textContent has changed in the
     *                 DOM. It is called with the element, the old value, and
     *                 current value.
     */
    "use strict";

    Object.defineProperty(exports, "__esModule", {
        value: true
    });
    var transitionStates = {
        attached: [],
        detached: [],
        replaced: [],
        attributeChanged: [],
        textChanged: []
    };
    exports.transitionStates = transitionStates;

},{}],7:[function(_dereq_,module,exports){
    /**
     * stringToBuffer
     *
     * @param string
     * @return
     */
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.stringToBuffer = stringToBuffer;
    exports.bufferToString = bufferToString;

    function stringToBuffer(string) {
        var buffer = new Uint16Array(string.length);

        for (var i = 0; i < string.length; i++) {
            buffer[i] = string.codePointAt(i);
        }

        return buffer;
    }

    /**
     * bufferToString
     *
     * @param buffer
     * @return
     */

    function bufferToString(buffer) {
        var tmpBuffer = new Uint16Array(buffer, 0, buffer.length);
        var string = '';

        for (var i = 0; i < tmpBuffer.length; i++) {
            string += String.fromCodePoint(tmpBuffer[i]);
        }

        return string;
    }

},{}],8:[function(_dereq_,module,exports){
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.parseHTML = parseHTML;
    exports.makeParser = makeParser;

    var _pools2 = _dereq_('./pools');

    var pools = _pools2.pools;
    var parser = makeParser();

    /**
     * parseHTML
     *
     * @param newHTML
     * @return
     */

    function parseHTML(newHTML, isInner) {
        var nodes = parser.parse(newHTML).childNodes;

        return isInner ? nodes : nodes[0];
    }

    function makeParser() {
        var ENTITIES = [['Aacute', [193]], ['aacute', [225]], ['Abreve', [258]], ['abreve', [259]], ['ac', [8766]], ['acd', [8767]], ['acE', [8766, 819]], ['Acirc', [194]], ['acirc', [226]], ['acute', [180]], ['Acy', [1040]], ['acy', [1072]], ['AElig', [198]], ['aelig', [230]], ['af', [8289]], ['Afr', [120068]], ['afr', [120094]], ['Agrave', [192]], ['agrave', [224]], ['alefsym', [8501]], ['aleph', [8501]], ['Alpha', [913]], ['alpha', [945]], ['Amacr', [256]], ['amacr', [257]], ['amalg', [10815]], ['amp', [38]], ['AMP', [38]], ['andand', [10837]], ['And', [10835]], ['and', [8743]], ['andd', [10844]], ['andslope', [10840]], ['andv', [10842]], ['ang', [8736]], ['ange', [10660]], ['angle', [8736]], ['angmsdaa', [10664]], ['angmsdab', [10665]], ['angmsdac', [10666]], ['angmsdad', [10667]], ['angmsdae', [10668]], ['angmsdaf', [10669]], ['angmsdag', [10670]], ['angmsdah', [10671]], ['angmsd', [8737]], ['angrt', [8735]], ['angrtvb', [8894]], ['angrtvbd', [10653]], ['angsph', [8738]], ['angst', [197]], ['angzarr', [9084]], ['Aogon', [260]], ['aogon', [261]], ['Aopf', [120120]], ['aopf', [120146]], ['apacir', [10863]], ['ap', [8776]], ['apE', [10864]], ['ape', [8778]], ['apid', [8779]], ['apos', [39]], ['ApplyFunction', [8289]], ['approx', [8776]], ['approxeq', [8778]], ['Aring', [197]], ['aring', [229]], ['Ascr', [119964]], ['ascr', [119990]], ['Assign', [8788]], ['ast', [42]], ['asymp', [8776]], ['asympeq', [8781]], ['Atilde', [195]], ['atilde', [227]], ['Auml', [196]], ['auml', [228]], ['awconint', [8755]], ['awint', [10769]], ['backcong', [8780]], ['backepsilon', [1014]], ['backprime', [8245]], ['backsim', [8765]], ['backsimeq', [8909]], ['Backslash', [8726]], ['Barv', [10983]], ['barvee', [8893]], ['barwed', [8965]], ['Barwed', [8966]], ['barwedge', [8965]], ['bbrk', [9141]], ['bbrktbrk', [9142]], ['bcong', [8780]], ['Bcy', [1041]], ['bcy', [1073]], ['bdquo', [8222]], ['becaus', [8757]], ['because', [8757]], ['Because', [8757]], ['bemptyv', [10672]], ['bepsi', [1014]], ['bernou', [8492]], ['Bernoullis', [8492]], ['Beta', [914]], ['beta', [946]], ['beth', [8502]], ['between', [8812]], ['Bfr', [120069]], ['bfr', [120095]], ['bigcap', [8898]], ['bigcirc', [9711]], ['bigcup', [8899]], ['bigodot', [10752]], ['bigoplus', [10753]], ['bigotimes', [10754]], ['bigsqcup', [10758]], ['bigstar', [9733]], ['bigtriangledown', [9661]], ['bigtriangleup', [9651]], ['biguplus', [10756]], ['bigvee', [8897]], ['bigwedge', [8896]], ['bkarow', [10509]], ['blacklozenge', [10731]], ['blacksquare', [9642]], ['blacktriangle', [9652]], ['blacktriangledown', [9662]], ['blacktriangleleft', [9666]], ['blacktriangleright', [9656]], ['blank', [9251]], ['blk12', [9618]], ['blk14', [9617]], ['blk34', [9619]], ['block', [9608]], ['bne', [61, 8421]], ['bnequiv', [8801, 8421]], ['bNot', [10989]], ['bnot', [8976]], ['Bopf', [120121]], ['bopf', [120147]], ['bot', [8869]], ['bottom', [8869]], ['bowtie', [8904]], ['boxbox', [10697]], ['boxdl', [9488]], ['boxdL', [9557]], ['boxDl', [9558]], ['boxDL', [9559]], ['boxdr', [9484]], ['boxdR', [9554]], ['boxDr', [9555]], ['boxDR', [9556]], ['boxh', [9472]], ['boxH', [9552]], ['boxhd', [9516]], ['boxHd', [9572]], ['boxhD', [9573]], ['boxHD', [9574]], ['boxhu', [9524]], ['boxHu', [9575]], ['boxhU', [9576]], ['boxHU', [9577]], ['boxminus', [8863]], ['boxplus', [8862]], ['boxtimes', [8864]], ['boxul', [9496]], ['boxuL', [9563]], ['boxUl', [9564]], ['boxUL', [9565]], ['boxur', [9492]], ['boxuR', [9560]], ['boxUr', [9561]], ['boxUR', [9562]], ['boxv', [9474]], ['boxV', [9553]], ['boxvh', [9532]], ['boxvH', [9578]], ['boxVh', [9579]], ['boxVH', [9580]], ['boxvl', [9508]], ['boxvL', [9569]], ['boxVl', [9570]], ['boxVL', [9571]], ['boxvr', [9500]], ['boxvR', [9566]], ['boxVr', [9567]], ['boxVR', [9568]], ['bprime', [8245]], ['breve', [728]], ['Breve', [728]], ['brvbar', [166]], ['bscr', [119991]], ['Bscr', [8492]], ['bsemi', [8271]], ['bsim', [8765]], ['bsime', [8909]], ['bsolb', [10693]], ['bsol', [92]], ['bsolhsub', [10184]], ['bull', [8226]], ['bullet', [8226]], ['bump', [8782]], ['bumpE', [10926]], ['bumpe', [8783]], ['Bumpeq', [8782]], ['bumpeq', [8783]], ['Cacute', [262]], ['cacute', [263]], ['capand', [10820]], ['capbrcup', [10825]], ['capcap', [10827]], ['cap', [8745]], ['Cap', [8914]], ['capcup', [10823]], ['capdot', [10816]], ['CapitalDifferentialD', [8517]], ['caps', [8745, 65024]], ['caret', [8257]], ['caron', [711]], ['Cayleys', [8493]], ['ccaps', [10829]], ['Ccaron', [268]], ['ccaron', [269]], ['Ccedil', [199]], ['ccedil', [231]], ['Ccirc', [264]], ['ccirc', [265]], ['Cconint', [8752]], ['ccups', [10828]], ['ccupssm', [10832]], ['Cdot', [266]], ['cdot', [267]], ['cedil', [184]], ['Cedilla', [184]], ['cemptyv', [10674]], ['cent', [162]], ['centerdot', [183]], ['CenterDot', [183]], ['cfr', [120096]], ['Cfr', [8493]], ['CHcy', [1063]], ['chcy', [1095]], ['check', [10003]], ['checkmark', [10003]], ['Chi', [935]], ['chi', [967]], ['circ', [710]], ['circeq', [8791]], ['circlearrowleft', [8634]], ['circlearrowright', [8635]], ['circledast', [8859]], ['circledcirc', [8858]], ['circleddash', [8861]], ['CircleDot', [8857]], ['circledR', [174]], ['circledS', [9416]], ['CircleMinus', [8854]], ['CirclePlus', [8853]], ['CircleTimes', [8855]], ['cir', [9675]], ['cirE', [10691]], ['cire', [8791]], ['cirfnint', [10768]], ['cirmid', [10991]], ['cirscir', [10690]], ['ClockwiseContourIntegral', [8754]], ['CloseCurlyDoubleQuote', [8221]], ['CloseCurlyQuote', [8217]], ['clubs', [9827]], ['clubsuit', [9827]], ['colon', [58]], ['Colon', [8759]], ['Colone', [10868]], ['colone', [8788]], ['coloneq', [8788]], ['comma', [44]], ['commat', [64]], ['comp', [8705]], ['compfn', [8728]], ['complement', [8705]], ['complexes', [8450]], ['cong', [8773]], ['congdot', [10861]], ['Congruent', [8801]], ['conint', [8750]], ['Conint', [8751]], ['ContourIntegral', [8750]], ['copf', [120148]], ['Copf', [8450]], ['coprod', [8720]], ['Coproduct', [8720]], ['copy', [169]], ['COPY', [169]], ['copysr', [8471]], ['CounterClockwiseContourIntegral', [8755]], ['crarr', [8629]], ['cross', [10007]], ['Cross', [10799]], ['Cscr', [119966]], ['cscr', [119992]], ['csub', [10959]], ['csube', [10961]], ['csup', [10960]], ['csupe', [10962]], ['ctdot', [8943]], ['cudarrl', [10552]], ['cudarrr', [10549]], ['cuepr', [8926]], ['cuesc', [8927]], ['cularr', [8630]], ['cularrp', [10557]], ['cupbrcap', [10824]], ['cupcap', [10822]], ['CupCap', [8781]], ['cup', [8746]], ['Cup', [8915]], ['cupcup', [10826]], ['cupdot', [8845]], ['cupor', [10821]], ['cups', [8746, 65024]], ['curarr', [8631]], ['curarrm', [10556]], ['curlyeqprec', [8926]], ['curlyeqsucc', [8927]], ['curlyvee', [8910]], ['curlywedge', [8911]], ['curren', [164]], ['curvearrowleft', [8630]], ['curvearrowright', [8631]], ['cuvee', [8910]], ['cuwed', [8911]], ['cwconint', [8754]], ['cwint', [8753]], ['cylcty', [9005]], ['dagger', [8224]], ['Dagger', [8225]], ['daleth', [8504]], ['darr', [8595]], ['Darr', [8609]], ['dArr', [8659]], ['dash', [8208]], ['Dashv', [10980]], ['dashv', [8867]], ['dbkarow', [10511]], ['dblac', [733]], ['Dcaron', [270]], ['dcaron', [271]], ['Dcy', [1044]], ['dcy', [1076]], ['ddagger', [8225]], ['ddarr', [8650]], ['DD', [8517]], ['dd', [8518]], ['DDotrahd', [10513]], ['ddotseq', [10871]], ['deg', [176]], ['Del', [8711]], ['Delta', [916]], ['delta', [948]], ['demptyv', [10673]], ['dfisht', [10623]], ['Dfr', [120071]], ['dfr', [120097]], ['dHar', [10597]], ['dharl', [8643]], ['dharr', [8642]], ['DiacriticalAcute', [180]], ['DiacriticalDot', [729]], ['DiacriticalDoubleAcute', [733]], ['DiacriticalGrave', [96]], ['DiacriticalTilde', [732]], ['diam', [8900]], ['diamond', [8900]], ['Diamond', [8900]], ['diamondsuit', [9830]], ['diams', [9830]], ['die', [168]], ['DifferentialD', [8518]], ['digamma', [989]], ['disin', [8946]], ['div', [247]], ['divide', [247]], ['divideontimes', [8903]], ['divonx', [8903]], ['DJcy', [1026]], ['djcy', [1106]], ['dlcorn', [8990]], ['dlcrop', [8973]], ['dollar', [36]], ['Dopf', [120123]], ['dopf', [120149]], ['Dot', [168]], ['dot', [729]], ['DotDot', [8412]], ['doteq', [8784]], ['doteqdot', [8785]], ['DotEqual', [8784]], ['dotminus', [8760]], ['dotplus', [8724]], ['dotsquare', [8865]], ['doublebarwedge', [8966]], ['DoubleContourIntegral', [8751]], ['DoubleDot', [168]], ['DoubleDownArrow', [8659]], ['DoubleLeftArrow', [8656]], ['DoubleLeftRightArrow', [8660]], ['DoubleLeftTee', [10980]], ['DoubleLongLeftArrow', [10232]], ['DoubleLongLeftRightArrow', [10234]], ['DoubleLongRightArrow', [10233]], ['DoubleRightArrow', [8658]], ['DoubleRightTee', [8872]], ['DoubleUpArrow', [8657]], ['DoubleUpDownArrow', [8661]], ['DoubleVerticalBar', [8741]], ['DownArrowBar', [10515]], ['downarrow', [8595]], ['DownArrow', [8595]], ['Downarrow', [8659]], ['DownArrowUpArrow', [8693]], ['DownBreve', [785]], ['downdownarrows', [8650]], ['downharpoonleft', [8643]], ['downharpoonright', [8642]], ['DownLeftRightVector', [10576]], ['DownLeftTeeVector', [10590]], ['DownLeftVectorBar', [10582]], ['DownLeftVector', [8637]], ['DownRightTeeVector', [10591]], ['DownRightVectorBar', [10583]], ['DownRightVector', [8641]], ['DownTeeArrow', [8615]], ['DownTee', [8868]], ['drbkarow', [10512]], ['drcorn', [8991]], ['drcrop', [8972]], ['Dscr', [119967]], ['dscr', [119993]], ['DScy', [1029]], ['dscy', [1109]], ['dsol', [10742]], ['Dstrok', [272]], ['dstrok', [273]], ['dtdot', [8945]], ['dtri', [9663]], ['dtrif', [9662]], ['duarr', [8693]], ['duhar', [10607]], ['dwangle', [10662]], ['DZcy', [1039]], ['dzcy', [1119]], ['dzigrarr', [10239]], ['Eacute', [201]], ['eacute', [233]], ['easter', [10862]], ['Ecaron', [282]], ['ecaron', [283]], ['Ecirc', [202]], ['ecirc', [234]], ['ecir', [8790]], ['ecolon', [8789]], ['Ecy', [1069]], ['ecy', [1101]], ['eDDot', [10871]], ['Edot', [278]], ['edot', [279]], ['eDot', [8785]], ['ee', [8519]], ['efDot', [8786]], ['Efr', [120072]], ['efr', [120098]], ['eg', [10906]], ['Egrave', [200]], ['egrave', [232]], ['egs', [10902]], ['egsdot', [10904]], ['el', [10905]], ['Element', [8712]], ['elinters', [9191]], ['ell', [8467]], ['els', [10901]], ['elsdot', [10903]], ['Emacr', [274]], ['emacr', [275]], ['empty', [8709]], ['emptyset', [8709]], ['EmptySmallSquare', [9723]], ['emptyv', [8709]], ['EmptyVerySmallSquare', [9643]], ['emsp13', [8196]], ['emsp14', [8197]], ['emsp', [8195]], ['ENG', [330]], ['eng', [331]], ['ensp', [8194]], ['Eogon', [280]], ['eogon', [281]], ['Eopf', [120124]], ['eopf', [120150]], ['epar', [8917]], ['eparsl', [10723]], ['eplus', [10865]], ['epsi', [949]], ['Epsilon', [917]], ['epsilon', [949]], ['epsiv', [1013]], ['eqcirc', [8790]], ['eqcolon', [8789]], ['eqsim', [8770]], ['eqslantgtr', [10902]], ['eqslantless', [10901]], ['Equal', [10869]], ['equals', [61]], ['EqualTilde', [8770]], ['equest', [8799]], ['Equilibrium', [8652]], ['equiv', [8801]], ['equivDD', [10872]], ['eqvparsl', [10725]], ['erarr', [10609]], ['erDot', [8787]], ['escr', [8495]], ['Escr', [8496]], ['esdot', [8784]], ['Esim', [10867]], ['esim', [8770]], ['Eta', [919]], ['eta', [951]], ['ETH', [208]], ['eth', [240]], ['Euml', [203]], ['euml', [235]], ['euro', [8364]], ['excl', [33]], ['exist', [8707]], ['Exists', [8707]], ['expectation', [8496]], ['exponentiale', [8519]], ['ExponentialE', [8519]], ['fallingdotseq', [8786]], ['Fcy', [1060]], ['fcy', [1092]], ['female', [9792]], ['ffilig', [64259]], ['fflig', [64256]], ['ffllig', [64260]], ['Ffr', [120073]], ['ffr', [120099]], ['filig', [64257]], ['FilledSmallSquare', [9724]], ['FilledVerySmallSquare', [9642]], ['fjlig', [102, 106]], ['flat', [9837]], ['fllig', [64258]], ['fltns', [9649]], ['fnof', [402]], ['Fopf', [120125]], ['fopf', [120151]], ['forall', [8704]], ['ForAll', [8704]], ['fork', [8916]], ['forkv', [10969]], ['Fouriertrf', [8497]], ['fpartint', [10765]], ['frac12', [189]], ['frac13', [8531]], ['frac14', [188]], ['frac15', [8533]], ['frac16', [8537]], ['frac18', [8539]], ['frac23', [8532]], ['frac25', [8534]], ['frac34', [190]], ['frac35', [8535]], ['frac38', [8540]], ['frac45', [8536]], ['frac56', [8538]], ['frac58', [8541]], ['frac78', [8542]], ['frasl', [8260]], ['frown', [8994]], ['fscr', [119995]], ['Fscr', [8497]], ['gacute', [501]], ['Gamma', [915]], ['gamma', [947]], ['Gammad', [988]], ['gammad', [989]], ['gap', [10886]], ['Gbreve', [286]], ['gbreve', [287]], ['Gcedil', [290]], ['Gcirc', [284]], ['gcirc', [285]], ['Gcy', [1043]], ['gcy', [1075]], ['Gdot', [288]], ['gdot', [289]], ['ge', [8805]], ['gE', [8807]], ['gEl', [10892]], ['gel', [8923]], ['geq', [8805]], ['geqq', [8807]], ['geqslant', [10878]], ['gescc', [10921]], ['ges', [10878]], ['gesdot', [10880]], ['gesdoto', [10882]], ['gesdotol', [10884]], ['gesl', [8923, 65024]], ['gesles', [10900]], ['Gfr', [120074]], ['gfr', [120100]], ['gg', [8811]], ['Gg', [8921]], ['ggg', [8921]], ['gimel', [8503]], ['GJcy', [1027]], ['gjcy', [1107]], ['gla', [10917]], ['gl', [8823]], ['glE', [10898]], ['glj', [10916]], ['gnap', [10890]], ['gnapprox', [10890]], ['gne', [10888]], ['gnE', [8809]], ['gneq', [10888]], ['gneqq', [8809]], ['gnsim', [8935]], ['Gopf', [120126]], ['gopf', [120152]], ['grave', [96]], ['GreaterEqual', [8805]], ['GreaterEqualLess', [8923]], ['GreaterFullEqual', [8807]], ['GreaterGreater', [10914]], ['GreaterLess', [8823]], ['GreaterSlantEqual', [10878]], ['GreaterTilde', [8819]], ['Gscr', [119970]], ['gscr', [8458]], ['gsim', [8819]], ['gsime', [10894]], ['gsiml', [10896]], ['gtcc', [10919]], ['gtcir', [10874]], ['gt', [62]], ['GT', [62]], ['Gt', [8811]], ['gtdot', [8919]], ['gtlPar', [10645]], ['gtquest', [10876]], ['gtrapprox', [10886]], ['gtrarr', [10616]], ['gtrdot', [8919]], ['gtreqless', [8923]], ['gtreqqless', [10892]], ['gtrless', [8823]], ['gtrsim', [8819]], ['gvertneqq', [8809, 65024]], ['gvnE', [8809, 65024]], ['Hacek', [711]], ['hairsp', [8202]], ['half', [189]], ['hamilt', [8459]], ['HARDcy', [1066]], ['hardcy', [1098]], ['harrcir', [10568]], ['harr', [8596]], ['hArr', [8660]], ['harrw', [8621]], ['Hat', [94]], ['hbar', [8463]], ['Hcirc', [292]], ['hcirc', [293]], ['hearts', [9829]], ['heartsuit', [9829]], ['hellip', [8230]], ['hercon', [8889]], ['hfr', [120101]], ['Hfr', [8460]], ['HilbertSpace', [8459]], ['hksearow', [10533]], ['hkswarow', [10534]], ['hoarr', [8703]], ['homtht', [8763]], ['hookleftarrow', [8617]], ['hookrightarrow', [8618]], ['hopf', [120153]], ['Hopf', [8461]], ['horbar', [8213]], ['HorizontalLine', [9472]], ['hscr', [119997]], ['Hscr', [8459]], ['hslash', [8463]], ['Hstrok', [294]], ['hstrok', [295]], ['HumpDownHump', [8782]], ['HumpEqual', [8783]], ['hybull', [8259]], ['hyphen', [8208]], ['Iacute', [205]], ['iacute', [237]], ['ic', [8291]], ['Icirc', [206]], ['icirc', [238]], ['Icy', [1048]], ['icy', [1080]], ['Idot', [304]], ['IEcy', [1045]], ['iecy', [1077]], ['iexcl', [161]], ['iff', [8660]], ['ifr', [120102]], ['Ifr', [8465]], ['Igrave', [204]], ['igrave', [236]], ['ii', [8520]], ['iiiint', [10764]], ['iiint', [8749]], ['iinfin', [10716]], ['iiota', [8489]], ['IJlig', [306]], ['ijlig', [307]], ['Imacr', [298]], ['imacr', [299]], ['image', [8465]], ['ImaginaryI', [8520]], ['imagline', [8464]], ['imagpart', [8465]], ['imath', [305]], ['Im', [8465]], ['imof', [8887]], ['imped', [437]], ['Implies', [8658]], ['incare', [8453]], ['in', [8712]], ['infin', [8734]], ['infintie', [10717]], ['inodot', [305]], ['intcal', [8890]], ['int', [8747]], ['Int', [8748]], ['integers', [8484]], ['Integral', [8747]], ['intercal', [8890]], ['Intersection', [8898]], ['intlarhk', [10775]], ['intprod', [10812]], ['InvisibleComma', [8291]], ['InvisibleTimes', [8290]], ['IOcy', [1025]], ['iocy', [1105]], ['Iogon', [302]], ['iogon', [303]], ['Iopf', [120128]], ['iopf', [120154]], ['Iota', [921]], ['iota', [953]], ['iprod', [10812]], ['iquest', [191]], ['iscr', [119998]], ['Iscr', [8464]], ['isin', [8712]], ['isindot', [8949]], ['isinE', [8953]], ['isins', [8948]], ['isinsv', [8947]], ['isinv', [8712]], ['it', [8290]], ['Itilde', [296]], ['itilde', [297]], ['Iukcy', [1030]], ['iukcy', [1110]], ['Iuml', [207]], ['iuml', [239]], ['Jcirc', [308]], ['jcirc', [309]], ['Jcy', [1049]], ['jcy', [1081]], ['Jfr', [120077]], ['jfr', [120103]], ['jmath', [567]], ['Jopf', [120129]], ['jopf', [120155]], ['Jscr', [119973]], ['jscr', [119999]], ['Jsercy', [1032]], ['jsercy', [1112]], ['Jukcy', [1028]], ['jukcy', [1108]], ['Kappa', [922]], ['kappa', [954]], ['kappav', [1008]], ['Kcedil', [310]], ['kcedil', [311]], ['Kcy', [1050]], ['kcy', [1082]], ['Kfr', [120078]], ['kfr', [120104]], ['kgreen', [312]], ['KHcy', [1061]], ['khcy', [1093]], ['KJcy', [1036]], ['kjcy', [1116]], ['Kopf', [120130]], ['kopf', [120156]], ['Kscr', [119974]], ['kscr', [120000]], ['lAarr', [8666]], ['Lacute', [313]], ['lacute', [314]], ['laemptyv', [10676]], ['lagran', [8466]], ['Lambda', [923]], ['lambda', [955]], ['lang', [10216]], ['Lang', [10218]], ['langd', [10641]], ['langle', [10216]], ['lap', [10885]], ['Laplacetrf', [8466]], ['laquo', [171]], ['larrb', [8676]], ['larrbfs', [10527]], ['larr', [8592]], ['Larr', [8606]], ['lArr', [8656]], ['larrfs', [10525]], ['larrhk', [8617]], ['larrlp', [8619]], ['larrpl', [10553]], ['larrsim', [10611]], ['larrtl', [8610]], ['latail', [10521]], ['lAtail', [10523]], ['lat', [10923]], ['late', [10925]], ['lates', [10925, 65024]], ['lbarr', [10508]], ['lBarr', [10510]], ['lbbrk', [10098]], ['lbrace', [123]], ['lbrack', [91]], ['lbrke', [10635]], ['lbrksld', [10639]], ['lbrkslu', [10637]], ['Lcaron', [317]], ['lcaron', [318]], ['Lcedil', [315]], ['lcedil', [316]], ['lceil', [8968]], ['lcub', [123]], ['Lcy', [1051]], ['lcy', [1083]], ['ldca', [10550]], ['ldquo', [8220]], ['ldquor', [8222]], ['ldrdhar', [10599]], ['ldrushar', [10571]], ['ldsh', [8626]], ['le', [8804]], ['lE', [8806]], ['LeftAngleBracket', [10216]], ['LeftArrowBar', [8676]], ['leftarrow', [8592]], ['LeftArrow', [8592]], ['Leftarrow', [8656]], ['LeftArrowRightArrow', [8646]], ['leftarrowtail', [8610]], ['LeftCeiling', [8968]], ['LeftDoubleBracket', [10214]], ['LeftDownTeeVector', [10593]], ['LeftDownVectorBar', [10585]], ['LeftDownVector', [8643]], ['LeftFloor', [8970]], ['leftharpoondown', [8637]], ['leftharpoonup', [8636]], ['leftleftarrows', [8647]], ['leftrightarrow', [8596]], ['LeftRightArrow', [8596]], ['Leftrightarrow', [8660]], ['leftrightarrows', [8646]], ['leftrightharpoons', [8651]], ['leftrightsquigarrow', [8621]], ['LeftRightVector', [10574]], ['LeftTeeArrow', [8612]], ['LeftTee', [8867]], ['LeftTeeVector', [10586]], ['leftthreetimes', [8907]], ['LeftTriangleBar', [10703]], ['LeftTriangle', [8882]], ['LeftTriangleEqual', [8884]], ['LeftUpDownVector', [10577]], ['LeftUpTeeVector', [10592]], ['LeftUpVectorBar', [10584]], ['LeftUpVector', [8639]], ['LeftVectorBar', [10578]], ['LeftVector', [8636]], ['lEg', [10891]], ['leg', [8922]], ['leq', [8804]], ['leqq', [8806]], ['leqslant', [10877]], ['lescc', [10920]], ['les', [10877]], ['lesdot', [10879]], ['lesdoto', [10881]], ['lesdotor', [10883]], ['lesg', [8922, 65024]], ['lesges', [10899]], ['lessapprox', [10885]], ['lessdot', [8918]], ['lesseqgtr', [8922]], ['lesseqqgtr', [10891]], ['LessEqualGreater', [8922]], ['LessFullEqual', [8806]], ['LessGreater', [8822]], ['lessgtr', [8822]], ['LessLess', [10913]], ['lesssim', [8818]], ['LessSlantEqual', [10877]], ['LessTilde', [8818]], ['lfisht', [10620]], ['lfloor', [8970]], ['Lfr', [120079]], ['lfr', [120105]], ['lg', [8822]], ['lgE', [10897]], ['lHar', [10594]], ['lhard', [8637]], ['lharu', [8636]], ['lharul', [10602]], ['lhblk', [9604]], ['LJcy', [1033]], ['ljcy', [1113]], ['llarr', [8647]], ['ll', [8810]], ['Ll', [8920]], ['llcorner', [8990]], ['Lleftarrow', [8666]], ['llhard', [10603]], ['lltri', [9722]], ['Lmidot', [319]], ['lmidot', [320]], ['lmoustache', [9136]], ['lmoust', [9136]], ['lnap', [10889]], ['lnapprox', [10889]], ['lne', [10887]], ['lnE', [8808]], ['lneq', [10887]], ['lneqq', [8808]], ['lnsim', [8934]], ['loang', [10220]], ['loarr', [8701]], ['lobrk', [10214]], ['longleftarrow', [10229]], ['LongLeftArrow', [10229]], ['Longleftarrow', [10232]], ['longleftrightarrow', [10231]], ['LongLeftRightArrow', [10231]], ['Longleftrightarrow', [10234]], ['longmapsto', [10236]], ['longrightarrow', [10230]], ['LongRightArrow', [10230]], ['Longrightarrow', [10233]], ['looparrowleft', [8619]], ['looparrowright', [8620]], ['lopar', [10629]], ['Lopf', [120131]], ['lopf', [120157]], ['loplus', [10797]], ['lotimes', [10804]], ['lowast', [8727]], ['lowbar', [95]], ['LowerLeftArrow', [8601]], ['LowerRightArrow', [8600]], ['loz', [9674]], ['lozenge', [9674]], ['lozf', [10731]], ['lpar', [40]], ['lparlt', [10643]], ['lrarr', [8646]], ['lrcorner', [8991]], ['lrhar', [8651]], ['lrhard', [10605]], ['lrm', [8206]], ['lrtri', [8895]], ['lsaquo', [8249]], ['lscr', [120001]], ['Lscr', [8466]], ['lsh', [8624]], ['Lsh', [8624]], ['lsim', [8818]], ['lsime', [10893]], ['lsimg', [10895]], ['lsqb', [91]], ['lsquo', [8216]], ['lsquor', [8218]], ['Lstrok', [321]], ['lstrok', [322]], ['ltcc', [10918]], ['ltcir', [10873]], ['lt', [60]], ['LT', [60]], ['Lt', [8810]], ['ltdot', [8918]], ['lthree', [8907]], ['ltimes', [8905]], ['ltlarr', [10614]], ['ltquest', [10875]], ['ltri', [9667]], ['ltrie', [8884]], ['ltrif', [9666]], ['ltrPar', [10646]], ['lurdshar', [10570]], ['luruhar', [10598]], ['lvertneqq', [8808, 65024]], ['lvnE', [8808, 65024]], ['macr', [175]], ['male', [9794]], ['malt', [10016]], ['maltese', [10016]], ['Map', [10501]], ['map', [8614]], ['mapsto', [8614]], ['mapstodown', [8615]], ['mapstoleft', [8612]], ['mapstoup', [8613]], ['marker', [9646]], ['mcomma', [10793]], ['Mcy', [1052]], ['mcy', [1084]], ['mdash', [8212]], ['mDDot', [8762]], ['measuredangle', [8737]], ['MediumSpace', [8287]], ['Mellintrf', [8499]], ['Mfr', [120080]], ['mfr', [120106]], ['mho', [8487]], ['micro', [181]], ['midast', [42]], ['midcir', [10992]], ['mid', [8739]], ['middot', [183]], ['minusb', [8863]], ['minus', [8722]], ['minusd', [8760]], ['minusdu', [10794]], ['MinusPlus', [8723]], ['mlcp', [10971]], ['mldr', [8230]], ['mnplus', [8723]], ['models', [8871]], ['Mopf', [120132]], ['mopf', [120158]], ['mp', [8723]], ['mscr', [120002]], ['Mscr', [8499]], ['mstpos', [8766]], ['Mu', [924]], ['mu', [956]], ['multimap', [8888]], ['mumap', [8888]], ['nabla', [8711]], ['Nacute', [323]], ['nacute', [324]], ['nang', [8736, 8402]], ['nap', [8777]], ['napE', [10864, 824]], ['napid', [8779, 824]], ['napos', [329]], ['napprox', [8777]], ['natural', [9838]], ['naturals', [8469]], ['natur', [9838]], ['nbsp', [160]], ['nbump', [8782, 824]], ['nbumpe', [8783, 824]], ['ncap', [10819]], ['Ncaron', [327]], ['ncaron', [328]], ['Ncedil', [325]], ['ncedil', [326]], ['ncong', [8775]], ['ncongdot', [10861, 824]], ['ncup', [10818]], ['Ncy', [1053]], ['ncy', [1085]], ['ndash', [8211]], ['nearhk', [10532]], ['nearr', [8599]], ['neArr', [8663]], ['nearrow', [8599]], ['ne', [8800]], ['nedot', [8784, 824]], ['NegativeMediumSpace', [8203]], ['NegativeThickSpace', [8203]], ['NegativeThinSpace', [8203]], ['NegativeVeryThinSpace', [8203]], ['nequiv', [8802]], ['nesear', [10536]], ['nesim', [8770, 824]], ['NestedGreaterGreater', [8811]], ['NestedLessLess', [8810]], ['nexist', [8708]], ['nexists', [8708]], ['Nfr', [120081]], ['nfr', [120107]], ['ngE', [8807, 824]], ['nge', [8817]], ['ngeq', [8817]], ['ngeqq', [8807, 824]], ['ngeqslant', [10878, 824]], ['nges', [10878, 824]], ['nGg', [8921, 824]], ['ngsim', [8821]], ['nGt', [8811, 8402]], ['ngt', [8815]], ['ngtr', [8815]], ['nGtv', [8811, 824]], ['nharr', [8622]], ['nhArr', [8654]], ['nhpar', [10994]], ['ni', [8715]], ['nis', [8956]], ['nisd', [8954]], ['niv', [8715]], ['NJcy', [1034]], ['njcy', [1114]], ['nlarr', [8602]], ['nlArr', [8653]], ['nldr', [8229]], ['nlE', [8806, 824]], ['nle', [8816]], ['nleftarrow', [8602]], ['nLeftarrow', [8653]], ['nleftrightarrow', [8622]], ['nLeftrightarrow', [8654]], ['nleq', [8816]], ['nleqq', [8806, 824]], ['nleqslant', [10877, 824]], ['nles', [10877, 824]], ['nless', [8814]], ['nLl', [8920, 824]], ['nlsim', [8820]], ['nLt', [8810, 8402]], ['nlt', [8814]], ['nltri', [8938]], ['nltrie', [8940]], ['nLtv', [8810, 824]], ['nmid', [8740]], ['NoBreak', [8288]], ['NonBreakingSpace', [160]], ['nopf', [120159]], ['Nopf', [8469]], ['Not', [10988]], ['not', [172]], ['NotCongruent', [8802]], ['NotCupCap', [8813]], ['NotDoubleVerticalBar', [8742]], ['NotElement', [8713]], ['NotEqual', [8800]], ['NotEqualTilde', [8770, 824]], ['NotExists', [8708]], ['NotGreater', [8815]], ['NotGreaterEqual', [8817]], ['NotGreaterFullEqual', [8807, 824]], ['NotGreaterGreater', [8811, 824]], ['NotGreaterLess', [8825]], ['NotGreaterSlantEqual', [10878, 824]], ['NotGreaterTilde', [8821]], ['NotHumpDownHump', [8782, 824]], ['NotHumpEqual', [8783, 824]], ['notin', [8713]], ['notindot', [8949, 824]], ['notinE', [8953, 824]], ['notinva', [8713]], ['notinvb', [8951]], ['notinvc', [8950]], ['NotLeftTriangleBar', [10703, 824]], ['NotLeftTriangle', [8938]], ['NotLeftTriangleEqual', [8940]], ['NotLess', [8814]], ['NotLessEqual', [8816]], ['NotLessGreater', [8824]], ['NotLessLess', [8810, 824]], ['NotLessSlantEqual', [10877, 824]], ['NotLessTilde', [8820]], ['NotNestedGreaterGreater', [10914, 824]], ['NotNestedLessLess', [10913, 824]], ['notni', [8716]], ['notniva', [8716]], ['notnivb', [8958]], ['notnivc', [8957]], ['NotPrecedes', [8832]], ['NotPrecedesEqual', [10927, 824]], ['NotPrecedesSlantEqual', [8928]], ['NotReverseElement', [8716]], ['NotRightTriangleBar', [10704, 824]], ['NotRightTriangle', [8939]], ['NotRightTriangleEqual', [8941]], ['NotSquareSubset', [8847, 824]], ['NotSquareSubsetEqual', [8930]], ['NotSquareSuperset', [8848, 824]], ['NotSquareSupersetEqual', [8931]], ['NotSubset', [8834, 8402]], ['NotSubsetEqual', [8840]], ['NotSucceeds', [8833]], ['NotSucceedsEqual', [10928, 824]], ['NotSucceedsSlantEqual', [8929]], ['NotSucceedsTilde', [8831, 824]], ['NotSuperset', [8835, 8402]], ['NotSupersetEqual', [8841]], ['NotTilde', [8769]], ['NotTildeEqual', [8772]], ['NotTildeFullEqual', [8775]], ['NotTildeTilde', [8777]], ['NotVerticalBar', [8740]], ['nparallel', [8742]], ['npar', [8742]], ['nparsl', [11005, 8421]], ['npart', [8706, 824]], ['npolint', [10772]], ['npr', [8832]], ['nprcue', [8928]], ['nprec', [8832]], ['npreceq', [10927, 824]], ['npre', [10927, 824]], ['nrarrc', [10547, 824]], ['nrarr', [8603]], ['nrArr', [8655]], ['nrarrw', [8605, 824]], ['nrightarrow', [8603]], ['nRightarrow', [8655]], ['nrtri', [8939]], ['nrtrie', [8941]], ['nsc', [8833]], ['nsccue', [8929]], ['nsce', [10928, 824]], ['Nscr', [119977]], ['nscr', [120003]], ['nshortmid', [8740]], ['nshortparallel', [8742]], ['nsim', [8769]], ['nsime', [8772]], ['nsimeq', [8772]], ['nsmid', [8740]], ['nspar', [8742]], ['nsqsube', [8930]], ['nsqsupe', [8931]], ['nsub', [8836]], ['nsubE', [10949, 824]], ['nsube', [8840]], ['nsubset', [8834, 8402]], ['nsubseteq', [8840]], ['nsubseteqq', [10949, 824]], ['nsucc', [8833]], ['nsucceq', [10928, 824]], ['nsup', [8837]], ['nsupE', [10950, 824]], ['nsupe', [8841]], ['nsupset', [8835, 8402]], ['nsupseteq', [8841]], ['nsupseteqq', [10950, 824]], ['ntgl', [8825]], ['Ntilde', [209]], ['ntilde', [241]], ['ntlg', [8824]], ['ntriangleleft', [8938]], ['ntrianglelefteq', [8940]], ['ntriangleright', [8939]], ['ntrianglerighteq', [8941]], ['Nu', [925]], ['nu', [957]], ['num', [35]], ['numero', [8470]], ['numsp', [8199]], ['nvap', [8781, 8402]], ['nvdash', [8876]], ['nvDash', [8877]], ['nVdash', [8878]], ['nVDash', [8879]], ['nvge', [8805, 8402]], ['nvgt', [62, 8402]], ['nvHarr', [10500]], ['nvinfin', [10718]], ['nvlArr', [10498]], ['nvle', [8804, 8402]], ['nvlt', [60, 8402]], ['nvltrie', [8884, 8402]], ['nvrArr', [10499]], ['nvrtrie', [8885, 8402]], ['nvsim', [8764, 8402]], ['nwarhk', [10531]], ['nwarr', [8598]], ['nwArr', [8662]], ['nwarrow', [8598]], ['nwnear', [10535]], ['Oacute', [211]], ['oacute', [243]], ['oast', [8859]], ['Ocirc', [212]], ['ocirc', [244]], ['ocir', [8858]], ['Ocy', [1054]], ['ocy', [1086]], ['odash', [8861]], ['Odblac', [336]], ['odblac', [337]], ['odiv', [10808]], ['odot', [8857]], ['odsold', [10684]], ['OElig', [338]], ['oelig', [339]], ['ofcir', [10687]], ['Ofr', [120082]], ['ofr', [120108]], ['ogon', [731]], ['Ograve', [210]], ['ograve', [242]], ['ogt', [10689]], ['ohbar', [10677]], ['ohm', [937]], ['oint', [8750]], ['olarr', [8634]], ['olcir', [10686]], ['olcross', [10683]], ['oline', [8254]], ['olt', [10688]], ['Omacr', [332]], ['omacr', [333]], ['Omega', [937]], ['omega', [969]], ['Omicron', [927]], ['omicron', [959]], ['omid', [10678]], ['ominus', [8854]], ['Oopf', [120134]], ['oopf', [120160]], ['opar', [10679]], ['OpenCurlyDoubleQuote', [8220]], ['OpenCurlyQuote', [8216]], ['operp', [10681]], ['oplus', [8853]], ['orarr', [8635]], ['Or', [10836]], ['or', [8744]], ['ord', [10845]], ['order', [8500]], ['orderof', [8500]], ['ordf', [170]], ['ordm', [186]], ['origof', [8886]], ['oror', [10838]], ['orslope', [10839]], ['orv', [10843]], ['oS', [9416]], ['Oscr', [119978]], ['oscr', [8500]], ['Oslash', [216]], ['oslash', [248]], ['osol', [8856]], ['Otilde', [213]], ['otilde', [245]], ['otimesas', [10806]], ['Otimes', [10807]], ['otimes', [8855]], ['Ouml', [214]], ['ouml', [246]], ['ovbar', [9021]], ['OverBar', [8254]], ['OverBrace', [9182]], ['OverBracket', [9140]], ['OverParenthesis', [9180]], ['para', [182]], ['parallel', [8741]], ['par', [8741]], ['parsim', [10995]], ['parsl', [11005]], ['part', [8706]], ['PartialD', [8706]], ['Pcy', [1055]], ['pcy', [1087]], ['percnt', [37]], ['period', [46]], ['permil', [8240]], ['perp', [8869]], ['pertenk', [8241]], ['Pfr', [120083]], ['pfr', [120109]], ['Phi', [934]], ['phi', [966]], ['phiv', [981]], ['phmmat', [8499]], ['phone', [9742]], ['Pi', [928]], ['pi', [960]], ['pitchfork', [8916]], ['piv', [982]], ['planck', [8463]], ['planckh', [8462]], ['plankv', [8463]], ['plusacir', [10787]], ['plusb', [8862]], ['pluscir', [10786]], ['plus', [43]], ['plusdo', [8724]], ['plusdu', [10789]], ['pluse', [10866]], ['PlusMinus', [177]], ['plusmn', [177]], ['plussim', [10790]], ['plustwo', [10791]], ['pm', [177]], ['Poincareplane', [8460]], ['pointint', [10773]], ['popf', [120161]], ['Popf', [8473]], ['pound', [163]], ['prap', [10935]], ['Pr', [10939]], ['pr', [8826]], ['prcue', [8828]], ['precapprox', [10935]], ['prec', [8826]], ['preccurlyeq', [8828]], ['Precedes', [8826]], ['PrecedesEqual', [10927]], ['PrecedesSlantEqual', [8828]], ['PrecedesTilde', [8830]], ['preceq', [10927]], ['precnapprox', [10937]], ['precneqq', [10933]], ['precnsim', [8936]], ['pre', [10927]], ['prE', [10931]], ['precsim', [8830]], ['prime', [8242]], ['Prime', [8243]], ['primes', [8473]], ['prnap', [10937]], ['prnE', [10933]], ['prnsim', [8936]], ['prod', [8719]], ['Product', [8719]], ['profalar', [9006]], ['profline', [8978]], ['profsurf', [8979]], ['prop', [8733]], ['Proportional', [8733]], ['Proportion', [8759]], ['propto', [8733]], ['prsim', [8830]], ['prurel', [8880]], ['Pscr', [119979]], ['pscr', [120005]], ['Psi', [936]], ['psi', [968]], ['puncsp', [8200]], ['Qfr', [120084]], ['qfr', [120110]], ['qint', [10764]], ['qopf', [120162]], ['Qopf', [8474]], ['qprime', [8279]], ['Qscr', [119980]], ['qscr', [120006]], ['quaternions', [8461]], ['quatint', [10774]], ['quest', [63]], ['questeq', [8799]], ['quot', [34]], ['QUOT', [34]], ['rAarr', [8667]], ['race', [8765, 817]], ['Racute', [340]], ['racute', [341]], ['radic', [8730]], ['raemptyv', [10675]], ['rang', [10217]], ['Rang', [10219]], ['rangd', [10642]], ['range', [10661]], ['rangle', [10217]], ['raquo', [187]], ['rarrap', [10613]], ['rarrb', [8677]], ['rarrbfs', [10528]], ['rarrc', [10547]], ['rarr', [8594]], ['Rarr', [8608]], ['rArr', [8658]], ['rarrfs', [10526]], ['rarrhk', [8618]], ['rarrlp', [8620]], ['rarrpl', [10565]], ['rarrsim', [10612]], ['Rarrtl', [10518]], ['rarrtl', [8611]], ['rarrw', [8605]], ['ratail', [10522]], ['rAtail', [10524]], ['ratio', [8758]], ['rationals', [8474]], ['rbarr', [10509]], ['rBarr', [10511]], ['RBarr', [10512]], ['rbbrk', [10099]], ['rbrace', [125]], ['rbrack', [93]], ['rbrke', [10636]], ['rbrksld', [10638]], ['rbrkslu', [10640]], ['Rcaron', [344]], ['rcaron', [345]], ['Rcedil', [342]], ['rcedil', [343]], ['rceil', [8969]], ['rcub', [125]], ['Rcy', [1056]], ['rcy', [1088]], ['rdca', [10551]], ['rdldhar', [10601]], ['rdquo', [8221]], ['rdquor', [8221]], ['rdsh', [8627]], ['real', [8476]], ['realine', [8475]], ['realpart', [8476]], ['reals', [8477]], ['Re', [8476]], ['rect', [9645]], ['reg', [174]], ['REG', [174]], ['ReverseElement', [8715]], ['ReverseEquilibrium', [8651]], ['ReverseUpEquilibrium', [10607]], ['rfisht', [10621]], ['rfloor', [8971]], ['rfr', [120111]], ['Rfr', [8476]], ['rHar', [10596]], ['rhard', [8641]], ['rharu', [8640]], ['rharul', [10604]], ['Rho', [929]], ['rho', [961]], ['rhov', [1009]], ['RightAngleBracket', [10217]], ['RightArrowBar', [8677]], ['rightarrow', [8594]], ['RightArrow', [8594]], ['Rightarrow', [8658]], ['RightArrowLeftArrow', [8644]], ['rightarrowtail', [8611]], ['RightCeiling', [8969]], ['RightDoubleBracket', [10215]], ['RightDownTeeVector', [10589]], ['RightDownVectorBar', [10581]], ['RightDownVector', [8642]], ['RightFloor', [8971]], ['rightharpoondown', [8641]], ['rightharpoonup', [8640]], ['rightleftarrows', [8644]], ['rightleftharpoons', [8652]], ['rightrightarrows', [8649]], ['rightsquigarrow', [8605]], ['RightTeeArrow', [8614]], ['RightTee', [8866]], ['RightTeeVector', [10587]], ['rightthreetimes', [8908]], ['RightTriangleBar', [10704]], ['RightTriangle', [8883]], ['RightTriangleEqual', [8885]], ['RightUpDownVector', [10575]], ['RightUpTeeVector', [10588]], ['RightUpVectorBar', [10580]], ['RightUpVector', [8638]], ['RightVectorBar', [10579]], ['RightVector', [8640]], ['ring', [730]], ['risingdotseq', [8787]], ['rlarr', [8644]], ['rlhar', [8652]], ['rlm', [8207]], ['rmoustache', [9137]], ['rmoust', [9137]], ['rnmid', [10990]], ['roang', [10221]], ['roarr', [8702]], ['robrk', [10215]], ['ropar', [10630]], ['ropf', [120163]], ['Ropf', [8477]], ['roplus', [10798]], ['rotimes', [10805]], ['RoundImplies', [10608]], ['rpar', [41]], ['rpargt', [10644]], ['rppolint', [10770]], ['rrarr', [8649]], ['Rrightarrow', [8667]], ['rsaquo', [8250]], ['rscr', [120007]], ['Rscr', [8475]], ['rsh', [8625]], ['Rsh', [8625]], ['rsqb', [93]], ['rsquo', [8217]], ['rsquor', [8217]], ['rthree', [8908]], ['rtimes', [8906]], ['rtri', [9657]], ['rtrie', [8885]], ['rtrif', [9656]], ['rtriltri', [10702]], ['RuleDelayed', [10740]], ['ruluhar', [10600]], ['rx', [8478]], ['Sacute', [346]], ['sacute', [347]], ['sbquo', [8218]], ['scap', [10936]], ['Scaron', [352]], ['scaron', [353]], ['Sc', [10940]], ['sc', [8827]], ['sccue', [8829]], ['sce', [10928]], ['scE', [10932]], ['Scedil', [350]], ['scedil', [351]], ['Scirc', [348]], ['scirc', [349]], ['scnap', [10938]], ['scnE', [10934]], ['scnsim', [8937]], ['scpolint', [10771]], ['scsim', [8831]], ['Scy', [1057]], ['scy', [1089]], ['sdotb', [8865]], ['sdot', [8901]], ['sdote', [10854]], ['searhk', [10533]], ['searr', [8600]], ['seArr', [8664]], ['searrow', [8600]], ['sect', [167]], ['semi', [59]], ['seswar', [10537]], ['setminus', [8726]], ['setmn', [8726]], ['sext', [10038]], ['Sfr', [120086]], ['sfr', [120112]], ['sfrown', [8994]], ['sharp', [9839]], ['SHCHcy', [1065]], ['shchcy', [1097]], ['SHcy', [1064]], ['shcy', [1096]], ['ShortDownArrow', [8595]], ['ShortLeftArrow', [8592]], ['shortmid', [8739]], ['shortparallel', [8741]], ['ShortRightArrow', [8594]], ['ShortUpArrow', [8593]], ['shy', [173]], ['Sigma', [931]], ['sigma', [963]], ['sigmaf', [962]], ['sigmav', [962]], ['sim', [8764]], ['simdot', [10858]], ['sime', [8771]], ['simeq', [8771]], ['simg', [10910]], ['simgE', [10912]], ['siml', [10909]], ['simlE', [10911]], ['simne', [8774]], ['simplus', [10788]], ['simrarr', [10610]], ['slarr', [8592]], ['SmallCircle', [8728]], ['smallsetminus', [8726]], ['smashp', [10803]], ['smeparsl', [10724]], ['smid', [8739]], ['smile', [8995]], ['smt', [10922]], ['smte', [10924]], ['smtes', [10924, 65024]], ['SOFTcy', [1068]], ['softcy', [1100]], ['solbar', [9023]], ['solb', [10692]], ['sol', [47]], ['Sopf', [120138]], ['sopf', [120164]], ['spades', [9824]], ['spadesuit', [9824]], ['spar', [8741]], ['sqcap', [8851]], ['sqcaps', [8851, 65024]], ['sqcup', [8852]], ['sqcups', [8852, 65024]], ['Sqrt', [8730]], ['sqsub', [8847]], ['sqsube', [8849]], ['sqsubset', [8847]], ['sqsubseteq', [8849]], ['sqsup', [8848]], ['sqsupe', [8850]], ['sqsupset', [8848]], ['sqsupseteq', [8850]], ['square', [9633]], ['Square', [9633]], ['SquareIntersection', [8851]], ['SquareSubset', [8847]], ['SquareSubsetEqual', [8849]], ['SquareSuperset', [8848]], ['SquareSupersetEqual', [8850]], ['SquareUnion', [8852]], ['squarf', [9642]], ['squ', [9633]], ['squf', [9642]], ['srarr', [8594]], ['Sscr', [119982]], ['sscr', [120008]], ['ssetmn', [8726]], ['ssmile', [8995]], ['sstarf', [8902]], ['Star', [8902]], ['star', [9734]], ['starf', [9733]], ['straightepsilon', [1013]], ['straightphi', [981]], ['strns', [175]], ['sub', [8834]], ['Sub', [8912]], ['subdot', [10941]], ['subE', [10949]], ['sube', [8838]], ['subedot', [10947]], ['submult', [10945]], ['subnE', [10955]], ['subne', [8842]], ['subplus', [10943]], ['subrarr', [10617]], ['subset', [8834]], ['Subset', [8912]], ['subseteq', [8838]], ['subseteqq', [10949]], ['SubsetEqual', [8838]], ['subsetneq', [8842]], ['subsetneqq', [10955]], ['subsim', [10951]], ['subsub', [10965]], ['subsup', [10963]], ['succapprox', [10936]], ['succ', [8827]], ['succcurlyeq', [8829]], ['Succeeds', [8827]], ['SucceedsEqual', [10928]], ['SucceedsSlantEqual', [8829]], ['SucceedsTilde', [8831]], ['succeq', [10928]], ['succnapprox', [10938]], ['succneqq', [10934]], ['succnsim', [8937]], ['succsim', [8831]], ['SuchThat', [8715]], ['sum', [8721]], ['Sum', [8721]], ['sung', [9834]], ['sup1', [185]], ['sup2', [178]], ['sup3', [179]], ['sup', [8835]], ['Sup', [8913]], ['supdot', [10942]], ['supdsub', [10968]], ['supE', [10950]], ['supe', [8839]], ['supedot', [10948]], ['Superset', [8835]], ['SupersetEqual', [8839]], ['suphsol', [10185]], ['suphsub', [10967]], ['suplarr', [10619]], ['supmult', [10946]], ['supnE', [10956]], ['supne', [8843]], ['supplus', [10944]], ['supset', [8835]], ['Supset', [8913]], ['supseteq', [8839]], ['supseteqq', [10950]], ['supsetneq', [8843]], ['supsetneqq', [10956]], ['supsim', [10952]], ['supsub', [10964]], ['supsup', [10966]], ['swarhk', [10534]], ['swarr', [8601]], ['swArr', [8665]], ['swarrow', [8601]], ['swnwar', [10538]], ['szlig', [223]], ['Tab', [9]], ['target', [8982]], ['Tau', [932]], ['tau', [964]], ['tbrk', [9140]], ['Tcaron', [356]], ['tcaron', [357]], ['Tcedil', [354]], ['tcedil', [355]], ['Tcy', [1058]], ['tcy', [1090]], ['tdot', [8411]], ['telrec', [8981]], ['Tfr', [120087]], ['tfr', [120113]], ['there4', [8756]], ['therefore', [8756]], ['Therefore', [8756]], ['Theta', [920]], ['theta', [952]], ['thetasym', [977]], ['thetav', [977]], ['thickapprox', [8776]], ['thicksim', [8764]], ['ThickSpace', [8287, 8202]], ['ThinSpace', [8201]], ['thinsp', [8201]], ['thkap', [8776]], ['thksim', [8764]], ['THORN', [222]], ['thorn', [254]], ['tilde', [732]], ['Tilde', [8764]], ['TildeEqual', [8771]], ['TildeFullEqual', [8773]], ['TildeTilde', [8776]], ['timesbar', [10801]], ['timesb', [8864]], ['times', [215]], ['timesd', [10800]], ['tint', [8749]], ['toea', [10536]], ['topbot', [9014]], ['topcir', [10993]], ['top', [8868]], ['Topf', [120139]], ['topf', [120165]], ['topfork', [10970]], ['tosa', [10537]], ['tprime', [8244]], ['trade', [8482]], ['TRADE', [8482]], ['triangle', [9653]], ['triangledown', [9663]], ['triangleleft', [9667]], ['trianglelefteq', [8884]], ['triangleq', [8796]], ['triangleright', [9657]], ['trianglerighteq', [8885]], ['tridot', [9708]], ['trie', [8796]], ['triminus', [10810]], ['TripleDot', [8411]], ['triplus', [10809]], ['trisb', [10701]], ['tritime', [10811]], ['trpezium', [9186]], ['Tscr', [119983]], ['tscr', [120009]], ['TScy', [1062]], ['tscy', [1094]], ['TSHcy', [1035]], ['tshcy', [1115]], ['Tstrok', [358]], ['tstrok', [359]], ['twixt', [8812]], ['twoheadleftarrow', [8606]], ['twoheadrightarrow', [8608]], ['Uacute', [218]], ['uacute', [250]], ['uarr', [8593]], ['Uarr', [8607]], ['uArr', [8657]], ['Uarrocir', [10569]], ['Ubrcy', [1038]], ['ubrcy', [1118]], ['Ubreve', [364]], ['ubreve', [365]], ['Ucirc', [219]], ['ucirc', [251]], ['Ucy', [1059]], ['ucy', [1091]], ['udarr', [8645]], ['Udblac', [368]], ['udblac', [369]], ['udhar', [10606]], ['ufisht', [10622]], ['Ufr', [120088]], ['ufr', [120114]], ['Ugrave', [217]], ['ugrave', [249]], ['uHar', [10595]], ['uharl', [8639]], ['uharr', [8638]], ['uhblk', [9600]], ['ulcorn', [8988]], ['ulcorner', [8988]], ['ulcrop', [8975]], ['ultri', [9720]], ['Umacr', [362]], ['umacr', [363]], ['uml', [168]], ['UnderBar', [95]], ['UnderBrace', [9183]], ['UnderBracket', [9141]], ['UnderParenthesis', [9181]], ['Union', [8899]], ['UnionPlus', [8846]], ['Uogon', [370]], ['uogon', [371]], ['Uopf', [120140]], ['uopf', [120166]], ['UpArrowBar', [10514]], ['uparrow', [8593]], ['UpArrow', [8593]], ['Uparrow', [8657]], ['UpArrowDownArrow', [8645]], ['updownarrow', [8597]], ['UpDownArrow', [8597]], ['Updownarrow', [8661]], ['UpEquilibrium', [10606]], ['upharpoonleft', [8639]], ['upharpoonright', [8638]], ['uplus', [8846]], ['UpperLeftArrow', [8598]], ['UpperRightArrow', [8599]], ['upsi', [965]], ['Upsi', [978]], ['upsih', [978]], ['Upsilon', [933]], ['upsilon', [965]], ['UpTeeArrow', [8613]], ['UpTee', [8869]], ['upuparrows', [8648]], ['urcorn', [8989]], ['urcorner', [8989]], ['urcrop', [8974]], ['Uring', [366]], ['uring', [367]], ['urtri', [9721]], ['Uscr', [119984]], ['uscr', [120010]], ['utdot', [8944]], ['Utilde', [360]], ['utilde', [361]], ['utri', [9653]], ['utrif', [9652]], ['uuarr', [8648]], ['Uuml', [220]], ['uuml', [252]], ['uwangle', [10663]], ['vangrt', [10652]], ['varepsilon', [1013]], ['varkappa', [1008]], ['varnothing', [8709]], ['varphi', [981]], ['varpi', [982]], ['varpropto', [8733]], ['varr', [8597]], ['vArr', [8661]], ['varrho', [1009]], ['varsigma', [962]], ['varsubsetneq', [8842, 65024]], ['varsubsetneqq', [10955, 65024]], ['varsupsetneq', [8843, 65024]], ['varsupsetneqq', [10956, 65024]], ['vartheta', [977]], ['vartriangleleft', [8882]], ['vartriangleright', [8883]], ['vBar', [10984]], ['Vbar', [10987]], ['vBarv', [10985]], ['Vcy', [1042]], ['vcy', [1074]], ['vdash', [8866]], ['vDash', [8872]], ['Vdash', [8873]], ['VDash', [8875]], ['Vdashl', [10982]], ['veebar', [8891]], ['vee', [8744]], ['Vee', [8897]], ['veeeq', [8794]], ['vellip', [8942]], ['verbar', [124]], ['Verbar', [8214]], ['vert', [124]], ['Vert', [8214]], ['VerticalBar', [8739]], ['VerticalLine', [124]], ['VerticalSeparator', [10072]], ['VerticalTilde', [8768]], ['VeryThinSpace', [8202]], ['Vfr', [120089]], ['vfr', [120115]], ['vltri', [8882]], ['vnsub', [8834, 8402]], ['vnsup', [8835, 8402]], ['Vopf', [120141]], ['vopf', [120167]], ['vprop', [8733]], ['vrtri', [8883]], ['Vscr', [119985]], ['vscr', [120011]], ['vsubnE', [10955, 65024]], ['vsubne', [8842, 65024]], ['vsupnE', [10956, 65024]], ['vsupne', [8843, 65024]], ['Vvdash', [8874]], ['vzigzag', [10650]], ['Wcirc', [372]], ['wcirc', [373]], ['wedbar', [10847]], ['wedge', [8743]], ['Wedge', [8896]], ['wedgeq', [8793]], ['weierp', [8472]], ['Wfr', [120090]], ['wfr', [120116]], ['Wopf', [120142]], ['wopf', [120168]], ['wp', [8472]], ['wr', [8768]], ['wreath', [8768]], ['Wscr', [119986]], ['wscr', [120012]], ['xcap', [8898]], ['xcirc', [9711]], ['xcup', [8899]], ['xdtri', [9661]], ['Xfr', [120091]], ['xfr', [120117]], ['xharr', [10231]], ['xhArr', [10234]], ['Xi', [926]], ['xi', [958]], ['xlarr', [10229]], ['xlArr', [10232]], ['xmap', [10236]], ['xnis', [8955]], ['xodot', [10752]], ['Xopf', [120143]], ['xopf', [120169]], ['xoplus', [10753]], ['xotime', [10754]], ['xrarr', [10230]], ['xrArr', [10233]], ['Xscr', [119987]], ['xscr', [120013]], ['xsqcup', [10758]], ['xuplus', [10756]], ['xutri', [9651]], ['xvee', [8897]], ['xwedge', [8896]], ['Yacute', [221]], ['yacute', [253]], ['YAcy', [1071]], ['yacy', [1103]], ['Ycirc', [374]], ['ycirc', [375]], ['Ycy', [1067]], ['ycy', [1099]], ['yen', [165]], ['Yfr', [120092]], ['yfr', [120118]], ['YIcy', [1031]], ['yicy', [1111]], ['Yopf', [120144]], ['yopf', [120170]], ['Yscr', [119988]], ['yscr', [120014]], ['YUcy', [1070]], ['yucy', [1102]], ['yuml', [255]], ['Yuml', [376]], ['Zacute', [377]], ['zacute', [378]], ['Zcaron', [381]], ['zcaron', [382]], ['Zcy', [1047]], ['zcy', [1079]], ['Zdot', [379]], ['zdot', [380]], ['zeetrf', [8488]], ['ZeroWidthSpace', [8203]], ['Zeta', [918]], ['zeta', [950]], ['zfr', [120119]], ['Zfr', [8488]], ['ZHcy', [1046]], ['zhcy', [1078]], ['zigrarr', [8669]], ['zopf', [120171]], ['Zopf', [8484]], ['Zscr', [119989]], ['zscr', [120015]], ['zwj', [8205]], ['zwnj', [8204]]];

        var alphaIndex = {};
        var charIndex = {};

        createIndexes(alphaIndex, charIndex);

        /**
         * @param {Object} alphaIndex Passed by reference.
         * @param {Object} charIndex Passed by reference.
         */
        function createIndexes(alphaIndex, charIndex) {
            var i = ENTITIES.length;
            var _results = [];
            while (i--) {
                var e = ENTITIES[i];
                var alpha = e[0];
                var chars = e[1];
                var chr = chars[0];
                var addChar = chr < 32 || chr > 126 || chr === 62 || chr === 60 || chr === 38 || chr === 34 || chr === 39;
                var charInfo;
                if (addChar) {
                    charInfo = charIndex[chr] = charIndex[chr] || {};
                }
                if (chars[1]) {
                    var chr2 = chars[1];
                    alphaIndex[alpha] = String.fromCharCode(chr) + String.fromCharCode(chr2);
                    _results.push(addChar && (charInfo[chr2] = alpha));
                } else {
                    alphaIndex[alpha] = String.fromCharCode(chr);
                    _results.push(addChar && (charInfo[''] = alpha));
                }
            }
        }

        function decode(str) {
            if (str.length === 0) {
                return '';
            }
            return str.replace(/&(#?[\w\d]+);?/g, function (s, entity) {
                var chr;
                if (entity.charAt(0) === "#") {
                    var code = entity.charAt(1) === 'x' ? parseInt(entity.substr(2).toLowerCase(), 16) : parseInt(entity.substr(1));

                    if (!(isNaN(code) || code < -32768 || code > 65535)) {
                        chr = String.fromCharCode(code);
                    }
                } else {
                    chr = alphaIndex[entity];
                }
                return chr || s;
            });
        }

        /**
         * Node Class as base class for TextNode and HTMLElement.
         */
        function Node() {}
        Node.prototype = {
            constructor: Node,
            ELEMENT_NODE: 1,
            TEXT_NODE: 3
        };

        Node.ELEMENT_NODE = 1;
        Node.TEXT_NODE = 3;

        /**
         * TextNode to contain a text element in DOM tree.
         * @param {string} value [description]
         */
        function TextNode(value) {
            this.nodeValue = decode(value);
            this.nodeName = '#text';
            this.element = pools.uuid.get();
        }

        TextNode.prototype = Object.defineProperties({
            constructor: TextNode,
            __proto__: Node.prototype,

            /**
             * Node Type declaration.
             * @type {Number}
             */
            nodeType: Node.TEXT_NODE

        }, {
            text: { /**
             * Get unescaped text value of current node and its children.
             * @return {string} text content
             */

            get: function get() {
                return this.nodeValue;
            },
                configurable: true,
                enumerable: true
            },
            isWhitespace: {

                /**
                 * Detect if the node contains only white space.
                 * @return {bool}
                 */

                get: function get() {
                    return (/^(\s|&nbsp;)*$/.test(this.nodeValue)
                    );
                },
                configurable: true,
                enumerable: true
            }
        });

        var kBlockElements = {
            div: true,
            p: true,
            // ul: true,
            // ol: true,
            li: true,
            // table: true,
            // tr: true,
            td: true,
            section: true,
            br: true
        };

        /**
         * HTMLElement, which contains a set of children.
         * Note: this is a minimalist implementation, no complete tree
         *   structure provided (no parentNode, nextSibling,
         *   previousSibling etc).
         * @param {string} name     nodeName
         * @param {Object} keyAttrs id and class attribute
         * @param {Object} rawAttrs attributes in string
         */
        function HTMLElement(name, keyAttrs, rawAttrs) {
            this.nodeName = name;
            this.attributes = [];

            if (rawAttrs) {
                var re = /\b([a-z][a-z0-9\-]*)\s*=\s*("([^"]+)"|'([^']+)'|(\S+))/ig;

                for (var match; match = re.exec(rawAttrs);) {
                    var attr = {};
                    attr.name = match[1];
                    attr.value = match[3] || match[4] || match[5];
                    this.attributes.push(attr);
                }
            }

            // this.parentNode = null;
            this.childNodes = [];
            this.element = pools.uuid.get();
        }
        HTMLElement.prototype = {
            constructor: HTMLElement,
            __proto__: Node.prototype,

            /**
             * Node Type declaration.
             * @type {Number}
             */
            nodeType: Node.ELEMENT_NODE
        };

        /**
         * Cache to store generated match functions
         * @type {Object}
         */
        var pMatchFunctionCache = {};

        /**
         * Matcher class to make CSS match
         * @param {string} selector Selector
         */
        function Matcher(selector) {
            this.matchers = selector.split(' ').map(function (matcher) {
                if (pMatchFunctionCache[matcher]) return pMatchFunctionCache[matcher];
                var parts = matcher.split('.');
                var nodeName = parts[0];
                var classes = parts.slice(1).sort();
                var source = '';
                if (nodeName && nodeName != '*') {
                    if (nodeName[0] == '#') source += 'if (el.id != ' + JSON.stringify(nodeName.substr(1)) + ') return false;';else source += 'if (el.nodeName != ' + JSON.stringify(nodeName) + ') return false;';
                }
                if (classes.length > 0) source += 'for (var cls = ' + JSON.stringify(classes) + ', i = 0; i < cls.length; i++) if (el.classNames.indexOf(cls[i]) === -1) return false;';
                source += 'return true;';
                return pMatchFunctionCache[matcher] = new Function('el', source);
            });
            this.nextMatch = 0;
        }
        Matcher.prototype = Object.defineProperties({
            /**
             * Trying to advance match pointer
             * @param  {HTMLElement} el element to make the match
             * @return {bool}           true when pointer advanced.
             */
            advance: function advance(el) {
                if (this.nextMatch < this.matchers.length && this.matchers[this.nextMatch](el)) {
                    this.nextMatch++;
                    return true;
                }
                return false;
            },
            /**
             * Rewind the match pointer
             */
            rewind: function rewind() {
                this.nextMatch--;
            },

            /**
             * Rest match pointer.
             * @return {[type]} [description]
             */
            reset: function reset() {
                this.nextMatch = 0;
            }
        }, {
            matched: { /**
             * Trying to determine if match made.
             * @return {bool} true when the match is made
             */

            get: function get() {
                return this.nextMatch == this.matchers.length;
            },
                configurable: true,
                enumerable: true
            }
        });
        /**
         * flush cache to free memory
         */
        Matcher.flushCache = function () {
            pMatchFunctionCache = {};
        };

        var kMarkupPattern = /<!--[^]*?(?=-->)-->|<(\/?)([a-z\-][a-z0-9\-]*)\s*([^>]*?)(\/?)>/ig;
        var kAttributePattern = /\b(id|class)\s*=\s*("([^"]+)"|'([^']+)'|(\S+))/ig;
        var kSelfClosingElements = {
            meta: true,
            img: true,
            link: true,
            input: true,
            area: true,
            br: true,
            hr: true
        };
        var kElementsClosedByOpening = {
            li: { li: true },
            p: { p: true, div: true },
            td: { td: true, th: true },
            th: { td: true, th: true }
        };
        var kElementsClosedByClosing = {
            li: { ul: true, ol: true },
            a: { div: true },
            b: { div: true },
            i: { div: true },
            p: { div: true },
            td: { tr: true, table: true },
            th: { tr: true, table: true }
        };
        var kBlockTextElements = {
            script: true,
            noscript: true,
            style: true,
            pre: true
        };

        /**
         * Parses HTML and returns a root element
         */
        var htmlParser = {

            Matcher: Matcher,
            Node: Node,
            HTMLElement: HTMLElement,
            TextNode: TextNode,

            /**
             * Parse a chuck of HTML source.
             * @param  {string} data      html
             * @return {HTMLElement}      root element
             */
            parse: function parse(data, options) {

                var root = new HTMLElement(null, {});
                var currentParent = root;
                var stack = [root];
                var lastTextPos = -1;

                options = options || {};

                for (var match, text; match = kMarkupPattern.exec(data);) {
                    if (lastTextPos > -1) {
                        if (lastTextPos + match[0].length < kMarkupPattern.lastIndex) {
                            // if has content
                            text = data.substring(lastTextPos, kMarkupPattern.lastIndex - match[0].length);
                            if (text.trim()) {
                                currentParent.childNodes.push(new TextNode(text));
                            }
                        }
                    }
                    lastTextPos = kMarkupPattern.lastIndex;
                    if (match[0][1] == '!') {
                        // this is a comment
                        continue;
                    }
                    if (options.lowerCaseTagName) match[2] = match[2].toLowerCase();
                    if (!match[1]) {
                        // not </ tags
                        var attrs = {};
                        for (var attMatch; attMatch = kAttributePattern.exec(match[3]);) attrs[attMatch[1]] = attMatch[3] || attMatch[4] || attMatch[5];
                        if (!match[4] && kElementsClosedByOpening[currentParent.nodeName]) {
                            if (kElementsClosedByOpening[currentParent.nodeName][match[2]]) {
                                stack.pop();
                                currentParent = stack[stack.length - 1];
                            }
                        }
                        currentParent = currentParent.childNodes[currentParent.childNodes.push(new HTMLElement(match[2], attrs, match[3])) - 1];
                        stack.push(currentParent);
                        if (kBlockTextElements[match[2]]) {
                            // a little test to find next </script> or </style> ...
                            var closeMarkup = '</' + match[2] + '>';
                            var index = data.indexOf(closeMarkup, kMarkupPattern.lastIndex);
                            if (options[match[2]]) {
                                if (index == -1) {
                                    // there is no matching ending for the text element.
                                    text = data.substr(kMarkupPattern.lastIndex);
                                } else {
                                    text = data.substring(kMarkupPattern.lastIndex, index);
                                }
                                if (text.length > 0) currentParent.childNodes.push(new TextNode(text));
                            }
                            if (index == -1) {
                                lastTextPos = kMarkupPattern.lastIndex = data.length + 1;
                            } else {
                                lastTextPos = kMarkupPattern.lastIndex = index + closeMarkup.length;
                                match[1] = true;
                            }
                        }
                    }
                    if (match[1] || match[4] || kSelfClosingElements[match[2]]) {
                        // </ or /> or <br> etc.
                        while (true && currentParent) {
                            if (currentParent.nodeName == match[2]) {
                                stack.pop();
                                currentParent = stack[stack.length - 1];
                                break;
                            } else {
                                // Trying to close current tag, and move on
                                if (kElementsClosedByClosing[currentParent.nodeName]) {
                                    if (kElementsClosedByClosing[currentParent.nodeName][match[2]]) {
                                        stack.pop();
                                        currentParent = stack[stack.length - 1];
                                        continue;
                                    }
                                }
                                // Use aggressive strategy to handle unmatching markups.
                                break;
                            }
                        }
                    }
                }

                return root;
            }

        };

        return htmlParser;
    }

    ;

},{"./pools":9}],9:[function(_dereq_,module,exports){
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.createPool = createPool;
    exports.initializePools = initializePools;

    var _uuid = _dereq_('./uuid');

// Babel rewrites variables, which means this temporary hack must be in place
// to avoid clobbering the global name.
    var uuid = _uuid.uuid;

    var pools = {};

    exports.pools = pools;

    function createPool(size, name, fill) {
        var _free = [];
        var allocated = [];
        var index = new WeakMap();

        // Prime the cache with n objects.
        for (var i = 0; i < size; i++) {
            _free[i] = fill();
        }

        return {
            _free: _free,
            _allocated: allocated,

            get: function get() {
                var obj = null;
                var freeLength = _free.length;
                var minusOne = freeLength - 1;

                if (freeLength) {
                    obj = _free[minusOne];
                    _free.length = minusOne;
                } else {
                    obj = fill();
                }

                var idx = allocated.push(obj);

                if (typeof obj === 'string') {
                    index[obj] = idx;
                } else {
                    index.set(obj, idx - 1);
                }

                return obj;
            },

            freeAll: function freeAll() {
                var allocatedLength = allocated.length;

                for (var i = 0; i < allocatedLength; i++) {
                    var obj = allocated[i];

                    if (typeof obj === 'string') {
                        var idx = index[obj];
                        delete index[obj];
                    } else {
                        var idx = index.get(obj);
                        // Remove from index map.
                        index['delete'](obj);
                    }

                    idx = idx || -1;

                    // Already freed.
                    if (idx === -1) {
                        continue;
                    }

                    // Clean.
                    if (obj.length) {
                        obj.length = 0;
                    } else {
                        for (var key in obj) {
                            obj[key] = void 0;
                        }
                    }

                    // Only put back into the free queue if we're under the size.
                    _free.push(obj);
                }

                allocated.length = 0;
            },

            free: function free(obj) {
                if (typeof obj === 'string') {
                    var idx = index[obj];
                    delete index[obj];
                } else {
                    var idx = index.get(obj);
                    // Remove from index map.
                    index['delete'](obj);
                }

                idx = idx || -1;

                // Already freed.
                if (idx === -1) {
                    return;
                }

                // Clean.
                if (obj.length) {
                    obj.length = 0;
                } else {
                    for (var key in obj) {
                        obj[key] = void 0;
                    }
                }

                // Only put back into the free queue if we're under the size.
                if (_free.length < size) {
                    _free.push(obj);
                }

                allocated.splice(idx, 1);
            }
        };
    }

    function initializePools(COUNT) {
        pools.object = createPool(COUNT, 'object', function () {
            return {};
        });

        pools.array = createPool(COUNT, 'array', function () {
            return [];
        });

        pools.uuid = createPool(COUNT, 'uuid', function () {
            return uuid();
        });
    }

},{"./uuid":10}],10:[function(_dereq_,module,exports){
    /**
     * Generates a uuid.
     *
     * @see http://stackoverflow.com/a/2117523/282175
     * @return {string} uuid
     */
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    exports.uuid = uuid;

    function uuid() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0,
                v = c == 'x' ? r : r & 0x3 | 0x8;
            return v.toString(16);
        });
    }

},{}],11:[function(_dereq_,module,exports){
    'use strict';

    Object.defineProperty(exports, '__esModule', {
        value: true
    });
    var bufferToString;
    var parseHTML;
    var syncNode;
    var pools;

    /**
     * startup
     *
     * @param worker
     * @return
     */
    function startup(worker) {
        var oldTree = null;
        var patches = [];

        worker.onmessage = function (e) {
            var data = e.data;
            var offset = data.offset;
            var transferBuffer = data.buffer;
            var isInner = data.isInner;

            if (!oldTree) {
                // Keep a virtual tree in memory to diff against.
                oldTree = data.oldTree;
            }

            var newTree = null;

            if (data.newTree) {
                newTree = data.newTree;
            } else {
                var newBuffer = transferBuffer.slice(0, offset);
                var newHTML = bufferToString(newBuffer);

                // Calculate a new tree.
                newTree = parseHTML(newHTML, isInner);

                if (isInner) {
                    var childNodes = newTree;

                    newTree = {
                        attributes: oldTree.attributes,
                        childNodes: childNodes,
                        element: oldTree.element,
                        nodeName: oldTree.nodeName,
                        nodeValue: oldTree.nodeValue
                    };
                }
            }

            // Synchronize the old virtual tree with the new virtual tree.  This will
            // produce a series of patches that will be excuted to update the DOM.
            syncNode.call(patches, oldTree, newTree);

            // Send the patches back to the userland.
            worker.postMessage(patches);

            // Cleanup sync node allocations.
            //pools.uuid.freeAll(); // TODO Wipe out the node cache when free'ing.
            pools.object.freeAll();
            pools.array.freeAll();

            // Wipe out the patches in memory.
            patches.length = 0;
        };
    }

    exports['default'] = startup;
    module.exports = exports['default'];

},{}]},{},[3])(3)
});