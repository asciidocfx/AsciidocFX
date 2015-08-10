(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.diff = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var _make_node = require('./make_node');

var _make_node2 = _interopRequireDefault(_make_node);

var _svg = require('./svg');

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
  _make_node2['default'].nodes[descriptor.element] = element;

  return element;
}

exports['default'] = makeElement;
module.exports = exports['default'];

},{"./make_node":2,"./svg":4}],2:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});

var _utilPools = require('../util/pools');

var push = Array.prototype.push;
var nodes = makeNode.nodes = {};

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
  var id = _utilPools.pools.uuid.get();

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

exports['default'] = makeNode;
module.exports = exports['default'];

},{"../util/pools":10}],3:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});

function _interopRequireWildcard(obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (obj != null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj['default'] = obj; return newObj; } }

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var _utilPools = require('../util/pools');

var _utilHtmls = require('../util/htmls');

var _utilHtmls2 = _interopRequireDefault(_utilHtmls);

var _utilParser = require('../util/parser');

var _utilBuffers = require('../util/buffers');

var buffers = _interopRequireWildcard(_utilBuffers);

var _utilUuid = require('../util/uuid');

var _sync_node = require('./sync_node');

var _make_node = require('./make_node');

var _make_node2 = _interopRequireDefault(_make_node);

var _make_element = require('./make_element');

var _make_element2 = _interopRequireDefault(_make_element);

var _worker = require('../worker');

var _worker2 = _interopRequireDefault(_worker);

var poolCount = 10000;

// Initialize with a reasonable amount of objects.
(0, _utilPools.initializePools)(poolCount);

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
  _sync_node.syncNode,

  // Add in the ability to parseHTML.
  _utilHtmls2['default'],

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
  if (element in _make_node2['default'].nodes) {
    return _make_node2['default'].nodes[element];
  }
  // Need to create.
  else {
      return (0, _make_element2['default'])(ref);
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
  var states = element._transitionStates;

  // Loop through all the patches and apply them.
  for (var i = 0; i < patches.length; i++) {
    var patch = patches[i];

    if (patch.element) {
      patch.element = getElement(patch.element);
      var elementId = patch.element;
    }

    if (patch.old) {
      patch.old = getElement(patch.old);
      var oldId = patch.old.element;
    }

    if (patch['new']) {
      patch['new'] = getElement(patch['new']);
      var newId = patch['new'].element;
    }

    // Replace the entire Node.
    if (patch.__do__ === 0) {
      patch.old.parentNode.replaceChild(patch['new'], patch.old);
    }

    // Node manip.
    else if (patch.__do__ === 1) {
        // Add.
        if (patch.element && patch.fragment && !patch.old) {
          var fragment = document.createDocumentFragment();

          patch.fragment.forEach(function (elementDescriptor) {
            var element = getElement(elementDescriptor);

            fragment.appendChild(element);

            // Added state for transitions API.
            if (states && states.added) {
              states.added.forEach(function (callback) {
                callback(element);
              });
            }
          });

          patch.element.appendChild(fragment);
        }

        // Remove
        else if (patch.old && !patch['new']) {
            if (!patch.old.parentNode) {
              throw new Error('Can\'t remove without parent, is this the ' + 'document root?');
            }

            var removeNode = (function () {
              this.parentNode.removeChild(this);
              _make_node2['default'].nodes[oldId] = null;
              delete _make_node2['default'].nodes[oldId];
            }).bind(patch.old);

            var removed;

            if (states && states.removed) {
              removed = states.removed.map(function (callback) {
                return callback(patch.old);
              });
            }

            var promises = [].concat(removed).filter(Boolean);

            if (promises.length) {
              Promise.all(promises).then(removeNode, removeNode);
            } else {
              removeNode();
            }
          }

          // Replace
          else if (patch.old && patch['new']) {
              if (!patch.old.parentNode) {
                throw new Error('Can\'t replace without parent, is this the ' + 'document root?');
              }

              // Append the element first, before doing the replacement.
              patch.old.parentNode.insertBefore(patch['new'], patch.old.nextSibling);

              var removeNode = (function () {
                this[0].parentNode.replaceChild(this[1], this[0]);
                _make_node2['default'].nodes[oldId] = null;
                delete _make_node2['default'].nodes[oldId];
              }).bind([patch.old, patch['new']]);

              var added, removed, replaced;

              // Added state for transitions API.
              if (states && states.added) {
                added = states.added.map(function (callback) {
                  return callback(patch['new']);
                });
              }

              // Removed state for transitions API.
              if (states && states.removed) {
                removed = states.removed.map(function (callback) {
                  return callback(patch.old);
                });
              }

              // Removed state for transitions API.
              if (states && states.replaced) {
                replaced = states.removed.map(function (callback) {
                  return callback(patch.old, patch['new']);
                });
              }

              // Replaced state for transitions API.
              var promises = [].concat(added, removed, replaced).filter(Boolean);

              if (promises.length) {
                Promise.all(promises).then(removeNode, removeNode);
              } else {
                removeNode();
              }
            }
      }

      // Attribute manipulation.
      else if (patch.__do__ === 2) {
          // Remove.
          if (!patch.value) {
            patch.element.removeAttribute(patch.name);
          } else {
            patch.element.setAttribute(patch.name, patch.value);
          }
        }

        // Text node manipulation.
        else if (patch.__do__ === 3) {
            patch.element.nodeValue = patch.value;
          }
  }
}

/**
 * Patches an element's DOM to match that of the passed markup.
 *
 * @param element
 * @param newHTML
 */
function patch(element, newHTML, options) {
  // Ensure that the document disable worker is always picked up.
  if (typeof options.disableWorker !== 'boolean') {
    options.disableWorker = document.DISABLE_WORKER;
  }

  var wantsWorker = hasWorker && !options.disableWorker;

  if (element.__is_rendering__) {
    return;
  }

  //if (typeof newHTML !== 'string') {
  //  throw new Error('Invalid type passed to diffHTML, expected String');
  //}

  // Only calculate the parent's initial state one time.
  if (!element.__old_tree__) {
    element.__old_tree__ = (0, _make_node2['default'])(element);
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
  if (wantsWorker && hasWorker && element.__has_rendered__) {
    // Attach all properties here to transport.
    var transferObject = {
      oldTree: element.__old_tree__
    };

    if (typeof newHTML !== 'string') {
      transferObject.newTree = (0, _make_node2['default'])(newHTML);

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
  } else if (!wantsWorker || !hasWorker || !element.__has_rendered__) {
    var patches = [];
    var oldTree = element.__old_tree__;
    var newTree = typeof newHTML === 'string' ? (0, _utilHtmls2['default'])(newHTML, options.inner) : (0, _make_node2['default'])(newHTML);

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
      _sync_node.syncNode.call(patches, element.__old_tree__, newTree);
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

exports['default'] = patch;
module.exports = exports['default'];

},{"../util/buffers":7,"../util/htmls":8,"../util/parser":9,"../util/pools":10,"../util/uuid":11,"../worker":12,"./make_element":1,"./make_node":2,"./sync_node":5}],4:[function(require,module,exports){
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

},{}],5:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});
exports.syncNode = syncNode;

var _utilPools = require('../util/pools');

var pools = _utilPools.pools;

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

},{"../util/pools":10}],6:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});
exports.outerHTML = outerHTML;
exports.innerHTML = innerHTML;
exports.element = element;
exports.enableProllyfill = enableProllyfill;

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { 'default': obj }; }

var _diffPatch_node = require('./diff/patch_node');

var _diffPatch_node2 = _interopRequireDefault(_diffPatch_node);

/**
 * outer
 *
 * @param element
 * @param markup=''
 * @param options={}
 * @return
 */

function outerHTML(element) {
  var markup = arguments.length <= 1 || arguments[1] === undefined ? '' : arguments[1];
  var options = arguments.length <= 2 || arguments[2] === undefined ? {} : arguments[2];

  options.inner = false;
  (0, _diffPatch_node2['default'])(element, markup, options);
}

/**
 * inner
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
  (0, _diffPatch_node2['default'])(element, markup, options);
}

/**
 * element
 *
 * @param element
 * @param newElement
 * @param options={}
 * @return
 */

function element(element, newElement) {
  var options = arguments.length <= 2 || arguments[2] === undefined ? {} : arguments[2];

  options.inner = false;
  (0, _diffPatch_node2['default'])(element, newElement, options);
}

/**
 * enableProllyfill
 *
 * @return
 */

function enableProllyfill() {
  Object.defineProperty(Element.prototype, 'addTransitionState', {
    configurable: true,

    value: function value(name, callback) {
      var states = this._transitionStates = this._transitionStates || {};

      states[name] = states[name] || [];

      states[name].push(callback);
    }
  });

  Object.defineProperty(Element.prototype, 'removeTransitionState', {
    configurable: true,

    value: function value(name, callback) {
      var states = this._transitionStates = this._transitionStates || {};

      states[name] = states[name] || [];

      if (!callback) {
        state[name] = [];
      } else {
        states[name].splice(states.indexOf(callback), 1);
      }
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

},{"./diff/patch_node":3}],7:[function(require,module,exports){
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

},{}],8:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});

var _pools = require('./pools');

var _parser = require('./parser');

var parser = (0, _parser.makeParser)();

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

exports['default'] = parseHTML;
module.exports = exports['default'];

},{"./parser":9,"./pools":10}],9:[function(require,module,exports){
(function (global){
"use strict";Object.defineProperty(exports,"__esModule",{value:true});exports.makeParser = makeParser;var _pools2=require('./pools');var pools=_pools2.pools;function makeParser(){var g={};(function(f){g.htmlParser = f();})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof dynamicRequire == "function" && dynamicRequire;if(!u && a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '" + o + "'");throw (f.code = "MODULE_NOT_FOUND",f);}var l=n[o] = {exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e);},l,l.exports,e,t,n,r);}return n[o].exports;}var i=typeof dynamicRequire == "function" && dynamicRequire;for(var o=0;o < r.length;o++) s(r[o]);return s;})({1:[function(dynamicRequire,module,exports){if(typeof Object.create === 'function'){ // implementation from standard node.js 'util' module
module.exports = function inherits(ctor,superCtor){ctor.super_ = superCtor;ctor.prototype = Object.create(superCtor.prototype,{constructor:{value:ctor,enumerable:false,writable:true,configurable:true}});};}else { // old school shim for old browsers
module.exports = function inherits(ctor,superCtor){ctor.super_ = superCtor;var TempCtor=function TempCtor(){};TempCtor.prototype = superCtor.prototype;ctor.prototype = new TempCtor();ctor.prototype.constructor = ctor;};}},{}],2:[function(dynamicRequire,module,exports){ // shim for using process in browser
var process=module.exports = {};var queue=[];var draining=false;function drainQueue(){if(draining){return;}draining = true;var currentQueue;var len=queue.length;while(len) {currentQueue = queue;queue = [];var i=-1;while(++i < len) {currentQueue[i]();}len = queue.length;}draining = false;}process.nextTick = function(fun){queue.push(fun);if(!draining){setTimeout(drainQueue,0);}};process.title = 'browser';process.browser = true;process.env = {};process.argv = [];process.version = ''; // empty string to avoid regexp issues
process.versions = {};function noop(){}process.on = noop;process.addListener = noop;process.once = noop;process.off = noop;process.removeListener = noop;process.removeAllListeners = noop;process.emit = noop;process.binding = function(name){throw new Error('process.binding is not supported');}; // TODO(shtylman)
process.cwd = function(){return '/';};process.chdir = function(dir){throw new Error('process.chdir is not supported');};process.umask = function(){return 0;};},{}],3:[function(dynamicRequire,module,exports){module.exports = function isBuffer(arg){return arg && typeof arg === 'object' && typeof arg.copy === 'function' && typeof arg.fill === 'function' && typeof arg.readUInt8 === 'function';};},{}],4:[function(dynamicRequire,module,exports){(function(process,global){ // Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.
var formatRegExp=/%[sdj%]/g;exports.format = function(f){if(!isString(f)){var objects=[];for(var i=0;i < arguments.length;i++) {objects.push(inspect(arguments[i]));}return objects.join(' ');}var i=1;var args=arguments;var len=args.length;var str=String(f).replace(formatRegExp,function(x){if(x === '%%')return '%';if(i >= len)return x;switch(x){case '%s':return String(args[i++]);case '%d':return Number(args[i++]);case '%j':try{return JSON.stringify(args[i++]);}catch(_) {return '[Circular]';}default:return x;}});for(var x=args[i];i < len;x = args[++i]) {if(isNull(x) || !isObject(x)){str += ' ' + x;}else {str += ' ' + inspect(x);}}return str;}; // Mark that a method should not be used.
// Returns a modified function which warns once by default.
// If --no-deprecation is set, then it is a no-op.
exports.deprecate = function(fn,msg){ // Allow for deprecating things in the process of starting up.
if(isUndefined(global.process)){return function(){return exports.deprecate(fn,msg).apply(this,arguments);};}if(process.noDeprecation === true){return fn;}var warned=false;function deprecated(){if(!warned){if(process.throwDeprecation){throw new Error(msg);}else if(process.traceDeprecation){console.trace(msg);}else {console.error(msg);}warned = true;}return fn.apply(this,arguments);}return deprecated;};var debugs={};var debugEnviron;exports.debuglog = function(set){if(isUndefined(debugEnviron))debugEnviron = process.env.NODE_DEBUG || '';set = set.toUpperCase();if(!debugs[set]){if(new RegExp('\\b' + set + '\\b','i').test(debugEnviron)){var pid=process.pid;debugs[set] = function(){var msg=exports.format.apply(exports,arguments);console.error('%s %d: %s',set,pid,msg);};}else {debugs[set] = function(){};}}return debugs[set];}; /**
   * Echos the value of a value. Trys to print the value out
   * in the best way possible given the different types.
   *
   * @param {Object} obj The object to print out.
   * @param {Object} opts Optional options object that alters the output.
   */ /* legacy: obj, showHidden, depth, colors*/function inspect(obj,opts){ // default options
var ctx={seen:[],stylize:stylizeNoColor}; // legacy...
if(arguments.length >= 3)ctx.depth = arguments[2];if(arguments.length >= 4)ctx.colors = arguments[3];if(isBoolean(opts)){ // legacy...
ctx.showHidden = opts;}else if(opts){ // got an "options" object
exports._extend(ctx,opts);} // set default options
if(isUndefined(ctx.showHidden))ctx.showHidden = false;if(isUndefined(ctx.depth))ctx.depth = 2;if(isUndefined(ctx.colors))ctx.colors = false;if(isUndefined(ctx.customInspect))ctx.customInspect = true;if(ctx.colors)ctx.stylize = stylizeWithColor;return formatValue(ctx,obj,ctx.depth);}exports.inspect = inspect; // http://en.wikipedia.org/wiki/ANSI_escape_code#graphics
inspect.colors = {'bold':[1,22],'italic':[3,23],'underline':[4,24],'inverse':[7,27],'white':[37,39],'grey':[90,39],'black':[30,39],'blue':[34,39],'cyan':[36,39],'green':[32,39],'magenta':[35,39],'red':[31,39],'yellow':[33,39]}; // Don't use 'blue' not visible on cmd.exe
inspect.styles = {'special':'cyan','number':'yellow','boolean':'yellow','undefined':'grey','null':'bold','string':'green','date':'magenta', // "name": intentionally not styling
'regexp':'red'};function stylizeWithColor(str,styleType){var style=inspect.styles[styleType];if(style){return "\u001b[" + inspect.colors[style][0] + 'm' + str + "\u001b[" + inspect.colors[style][1] + 'm';}else {return str;}}function stylizeNoColor(str,styleType){return str;}function arrayToHash(array){var hash={};array.forEach(function(val,idx){hash[val] = true;});return hash;}function formatValue(ctx,value,recurseTimes){ // Provide a hook for user-specified inspect functions.
// Check that value is an object with an inspect function on it
if(ctx.customInspect && value && isFunction(value.inspect) &&  // Filter out the util module, it's inspect function is special
value.inspect !== exports.inspect &&  // Also filter out any prototype objects using the circular check.
!(value.constructor && value.constructor.prototype === value)){var ret=value.inspect(recurseTimes,ctx);if(!isString(ret)){ret = formatValue(ctx,ret,recurseTimes);}return ret;} // Primitive types cannot have properties
var primitive=formatPrimitive(ctx,value);if(primitive){return primitive;} // Look up the keys of the object.
var keys=Object.keys(value);var visibleKeys=arrayToHash(keys);if(ctx.showHidden){keys = Object.getOwnPropertyNames(value);} // IE doesn't make error fields non-enumerable
// http://msdn.microsoft.com/en-us/library/ie/dww52sbt(v=vs.94).aspx
if(isError(value) && (keys.indexOf('message') >= 0 || keys.indexOf('description') >= 0)){return formatError(value);} // Some type of object without properties can be shortcutted.
if(keys.length === 0){if(isFunction(value)){var name=value.name?': ' + value.name:'';return ctx.stylize('[Function' + name + ']','special');}if(isRegExp(value)){return ctx.stylize(RegExp.prototype.toString.call(value),'regexp');}if(isDate(value)){return ctx.stylize(Date.prototype.toString.call(value),'date');}if(isError(value)){return formatError(value);}}var base='',array=false,braces=['{','}']; // Make Array say that they are Array
if(isArray(value)){array = true;braces = ['[',']'];} // Make functions say that they are functions
if(isFunction(value)){var n=value.name?': ' + value.name:'';base = ' [Function' + n + ']';} // Make RegExps say that they are RegExps
if(isRegExp(value)){base = ' ' + RegExp.prototype.toString.call(value);} // Make dates with properties first say the date
if(isDate(value)){base = ' ' + Date.prototype.toUTCString.call(value);} // Make error with message first say the error
if(isError(value)){base = ' ' + formatError(value);}if(keys.length === 0 && (!array || value.length == 0)){return braces[0] + base + braces[1];}if(recurseTimes < 0){if(isRegExp(value)){return ctx.stylize(RegExp.prototype.toString.call(value),'regexp');}else {return ctx.stylize('[Object]','special');}}ctx.seen.push(value);var output;if(array){output = formatArray(ctx,value,recurseTimes,visibleKeys,keys);}else {output = keys.map(function(key){return formatProperty(ctx,value,recurseTimes,visibleKeys,key,array);});}ctx.seen.pop();return reduceToSingleString(output,base,braces);}function formatPrimitive(ctx,value){if(isUndefined(value))return ctx.stylize('undefined','undefined');if(isString(value)){var simple='\'' + JSON.stringify(value).replace(/^"|"$/g,'').replace(/'/g,"\\'").replace(/\\"/g,'"') + '\'';return ctx.stylize(simple,'string');}if(isNumber(value))return ctx.stylize('' + value,'number');if(isBoolean(value))return ctx.stylize('' + value,'boolean'); // For some reason typeof null is "object", so special case here.
if(isNull(value))return ctx.stylize('null','null');}function formatError(value){return '[' + Error.prototype.toString.call(value) + ']';}function formatArray(ctx,value,recurseTimes,visibleKeys,keys){var output=[];for(var i=0,l=value.length;i < l;++i) {if(hasOwnProperty(value,String(i))){output.push(formatProperty(ctx,value,recurseTimes,visibleKeys,String(i),true));}else {output.push('');}}keys.forEach(function(key){if(!key.match(/^\d+$/)){output.push(formatProperty(ctx,value,recurseTimes,visibleKeys,key,true));}});return output;}function formatProperty(ctx,value,recurseTimes,visibleKeys,key,array){var name,str,desc;desc = Object.getOwnPropertyDescriptor(value,key) || {value:value[key]};if(desc.get){if(desc.set){str = ctx.stylize('[Getter/Setter]','special');}else {str = ctx.stylize('[Getter]','special');}}else {if(desc.set){str = ctx.stylize('[Setter]','special');}}if(!hasOwnProperty(visibleKeys,key)){name = '[' + key + ']';}if(!str){if(ctx.seen.indexOf(desc.value) < 0){if(isNull(recurseTimes)){str = formatValue(ctx,desc.value,null);}else {str = formatValue(ctx,desc.value,recurseTimes - 1);}if(str.indexOf('\n') > -1){if(array){str = str.split('\n').map(function(line){return '  ' + line;}).join('\n').substr(2);}else {str = '\n' + str.split('\n').map(function(line){return '   ' + line;}).join('\n');}}}else {str = ctx.stylize('[Circular]','special');}}if(isUndefined(name)){if(array && key.match(/^\d+$/)){return str;}name = JSON.stringify('' + key);if(name.match(/^"([a-zA-Z_][a-zA-Z_0-9]*)"$/)){name = name.substr(1,name.length - 2);name = ctx.stylize(name,'name');}else {name = name.replace(/'/g,"\\'").replace(/\\"/g,'"').replace(/(^"|"$)/g,"'");name = ctx.stylize(name,'string');}}return name + ': ' + str;}function reduceToSingleString(output,base,braces){var numLinesEst=0;var length=output.reduce(function(prev,cur){numLinesEst++;if(cur.indexOf('\n') >= 0)numLinesEst++;return prev + cur.replace(/\u001b\[\d\d?m/g,'').length + 1;},0);if(length > 60){return braces[0] + (base === ''?'':base + '\n ') + ' ' + output.join(',\n  ') + ' ' + braces[1];}return braces[0] + base + ' ' + output.join(', ') + ' ' + braces[1];} // NOTE: These type checking functions intentionally don't use `instanceof`
// because it is fragile and can be easily faked with `Object.create()`.
function isArray(ar){return Array.isArray(ar);}exports.isArray = isArray;function isBoolean(arg){return typeof arg === 'boolean';}exports.isBoolean = isBoolean;function isNull(arg){return arg === null;}exports.isNull = isNull;function isNullOrUndefined(arg){return arg == null;}exports.isNullOrUndefined = isNullOrUndefined;function isNumber(arg){return typeof arg === 'number';}exports.isNumber = isNumber;function isString(arg){return typeof arg === 'string';}exports.isString = isString;function isSymbol(arg){return typeof arg === 'symbol';}exports.isSymbol = isSymbol;function isUndefined(arg){return arg === void 0;}exports.isUndefined = isUndefined;function isRegExp(re){return isObject(re) && objectToString(re) === '[object RegExp]';}exports.isRegExp = isRegExp;function isObject(arg){return typeof arg === 'object' && arg !== null;}exports.isObject = isObject;function isDate(d){return isObject(d) && objectToString(d) === '[object Date]';}exports.isDate = isDate;function isError(e){return isObject(e) && (objectToString(e) === '[object Error]' || e instanceof Error);}exports.isError = isError;function isFunction(arg){return typeof arg === 'function';}exports.isFunction = isFunction;function isPrimitive(arg){return arg === null || typeof arg === 'boolean' || typeof arg === 'number' || typeof arg === 'string' || typeof arg === 'symbol' ||  // ES6 symbol
typeof arg === 'undefined';}exports.isPrimitive = isPrimitive;exports.isBuffer = dynamicRequire('./support/isBuffer');function objectToString(o){return Object.prototype.toString.call(o);}function pad(n){return n < 10?'0' + n.toString(10):n.toString(10);}var months=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec']; // 26 Feb 16:19:34
function timestamp(){var d=new Date();var time=[pad(d.getHours()),pad(d.getMinutes()),pad(d.getSeconds())].join(':');return [d.getDate(),months[d.getMonth()],time].join(' ');} // log is just a thin wrapper to console.log that prepends a timestamp
exports.log = function(){console.log('%s - %s',timestamp(),exports.format.apply(exports,arguments));}; /**
   * Inherit the prototype methods from one constructor into another.
   *
   * The Function.prototype.inherits from lang.js rewritten as a standalone
   * function (not on Function.prototype). NOTE: If this file is to be loaded
   * during bootstrapping this function needs to be rewritten using some native
   * functions as prototype setup using normal JavaScript does not work as
   * expected during bootstrapping (see mirror.js in r114903).
   *
   * @param {function} ctor Constructor function which needs to inherit the
   *     prototype.
   * @param {function} superCtor Constructor function to inherit prototype from.
   */exports.inherits = dynamicRequire('inherits');exports._extend = function(origin,add){ // Don't do anything if add isn't an object
if(!add || !isObject(add))return origin;var keys=Object.keys(add);var i=keys.length;while(i--) {origin[keys[i]] = add[keys[i]];}return origin;};function hasOwnProperty(obj,prop){return Object.prototype.hasOwnProperty.call(obj,prop);}}).call(this,dynamicRequire('_process'),typeof global !== "undefined"?global:typeof self !== "undefined"?self:typeof window !== "undefined"?window:{});},{"./support/isBuffer":3,"_process":2,"inherits":1}],5:[function(dynamicRequire,module,exports){module.exports = {"name":"apollojs","author":{"name":"Xiaoyi Shi","email":"ashi009@gmail.com"},"description":"A framework to extend global objects with advance features.","version":"1.3.0","contributors":[{"name":"Yan Dong","email":"idy0013@gmail.com"},{"name":"Steve Yang","email":"me@iyyang.com"}],"repository":{"type":"git","url":"https://github.com/apollojs/apollojs.git"},"scripts":{"prepublish":"make clean server","test":"mocha","posttest":"mocha -R travis-cov","coverage":"mocha -R html-cov > coverage.html","start":"node server.js"},"main":"./server.js","license":"MIT","engines":{"node":">=0.8"},"devDependencies":{"mocha":"*","should":"*","blanket":"*","travis-cov":"*"},"config":{"blanket":{"pattern":"server.js"},"travis-cov":{"threshold":70}},"bugs":{"url":"https://github.com/apollojs/apollojs/issues"},"homepage":"https://github.com/apollojs/apollojs","_id":"apollojs@1.3.0","_shasum":"5f7b00304d9740e2a7be5b52c7c0807d51f9255e","_from":"apollojs@>=1.3.0 <2.0.0","_npmVersion":"1.4.10","_npmUser":{"name":"ashi009","email":"ashi009@gmail.com"},"maintainers":[{"name":"ashi009","email":"ashi009@gmail.com"}],"dist":{"shasum":"5f7b00304d9740e2a7be5b52c7c0807d51f9255e","tarball":"http://registry.npmjs.org/apollojs/-/apollojs-1.3.0.tgz"},"directories":{},"_resolved":"https://registry.npmjs.org/apollojs/-/apollojs-1.3.0.tgz"};},{}],6:[function(dynamicRequire,module,exports){(function(global){if(!global.$apollo){var $format;(function(){ /**
   * Extend an object with another object
   * @param  {Object} obj      object to be extended
   * @param  {Object} ext      extension object
   * @param  {bool} override   Overwrite existing properties in obj
   * @param  {bool} deep       Doing an deep extend (perform extend on every object property)
   * @return {Object}          reference to obj
   */var $extend=function $extend(obj,ext,override,deep){var key;if(override){if(deep)_overrideDeepExtend(obj,ext);else for(key in ext) obj[key] = ext[key];}else {if(deep)_deepExtend(obj,ext);else for(key in ext) if(!(key in obj))obj[key] = ext[key];}return obj;};var _overrideDeepExtend=function _overrideDeepExtend(obj,ext){for(var key in ext) if(Object.isObjectStrict(obj[key]) && Object.isObjectStrict(ext[key]))_overrideDeepExtend(obj[key],ext[key]);else obj[key] = ext[key];};var _deepExtend=function _deepExtend(obj,ext){for(var key in ext) if(Object.isObjectStrict(obj[key]) && Object.isObjectStrict(ext[key]))_deepExtend(obj[key],ext[key]);else if(!(key in obj))obj[key] = ext[key];} /**
   * Define properties of an Object, Which usually used to extend prototype
   *   of an object, as it will set properties as non-enumerable, and will
   *   turn setValue(value) and getValue() functions to setter and getters.
   * Note: You should only use $define or Object.defineProperty on prototype,
   *   or on a class' itself (to define static methods), instead of on instances
   *   which could lead to severe performance issue.
   * @param  {Object} object    target object
   * @param  {Object} prototype extension object
   * @param  {bool} preserve    preserve existing property
   * @return {Object}           reference to object
   */;var $define=function $define(object,prototype,preserve){Object.getOwnPropertyNames(prototype).forEach(function(key){if(preserve && key in object)return;var desc=Object.getOwnPropertyDescriptor(prototype,key);if('value' in desc)desc.writable = true;delete desc.enumerable;delete desc.configurable;Object.defineProperty(object,key,desc);});return object;} /**
   * Declare a Class.
   * @param  {Function} fn      constructor of the Class
   * @param  {Object} prototype prototype of Class
   * @return {Function}         reference to constructor
   */;var $declare=function $declare(fn,prototype){fn.prototype.constructor = fn;$define(fn.prototype,prototype);return fn;} /**
   * Inherit another Class to current Class
   * @param  {Function} fn      constructor of the Class
   * @param  {Function} parent  parent Class
   * @param  {Object} prototype prototype of Class
   * @return {Function}         reference to constructor
   */;var $inherit=function $inherit(fn,parent,prototype){fn.prototype = {constructor:fn,__proto__:parent.prototype};if(prototype)$define(fn.prototype,prototype);return fn;} /**
   * Adding enumerations to a Class (both static and prototype).
   * @param  {Function} fn     constructor of the Class
   * @param  {Object}   values object holding all enumerates want to define
   * @return {Function}        reference to constructor
   */;var $defenum=function $defenum(fn,values){$define(fn,values);$define(fn.prototype,values);return fn;} /**
   * Format a string with given pattern.
   * @param  {string} str pattern
   * @return {string}     formatted string
   */; /**
   * Making an Error instance with given format and parameters.
   * Note: this is a helper function works like util.format(),
   *   apart from it returns an Error object instead of string.
   * @return {Error} generated Error instance
   */var $error=function $error(){return new Error($format.apply(null,arguments));} /**
   * Generate a deep copy of an Object with its primitive typed
   * fields (exclude functions).
   * @param  {mixed} obj  source object
   * @return {mixed}      cloned object
   */;var $valueCopy=function $valueCopy(obj){var res;if(Array.isArray(obj)){res = obj.slice(0);for(var i=0;i < res.length;i++) if(Object.isObject(res[i]))res[i] = $valueCopy(res[i]);}else if(Object.isObjectStrict(obj)){res = {};for(var key in obj) res[key] = $valueCopy(obj[key]);}else if(Function.isFunction(obj)){return undefined;}else {return obj;}return res;} /**
   * Generates a copy of an Object.
   * @param  {Mixed} org  source object
   * @param  {bool} deep  perform a deep clone
   * @return {Mixed}      cloned object
   */;var $clone=function $clone(obj,deep){var res;var _deep=deep === true || deep - 1;if(Array.isArray(obj)){res = obj.slice(0);if(deep)for(var i=0;i < res.length;i++) if(Object.isObject(res[i]))res[i] = $clone(res[i],_deep);}else if(Object.isObjectStrict(obj)){res = {};for(var key in obj) res[key] = obj[key];if(deep)for(var key in obj) if(Object.isObject(res[key]))res[key] = $clone(res[key],_deep);}else {return obj;}return res;} /**
   * Return default value of an undefined variable.
   * @param  {Mixed} val  value
   * @param  {Mixed} def  default value
   * @return {Mixed}
   */;var $default=function $default(val,def){return val === undefined?def:val;} /**
   * Wrap an object with given Class.
   * Note: it will call Class.__wrap method to do custom wrapping.
   * @param  {Object} obj     object to be wrapped
   * @param  {Function} Type  wrapping Class
   * @return {Object}         wrapped object
   */;var $wrap=function $wrap(obj,Type){obj.__proto__ = Type.prototype;if(Type.__wrap)Type.__wrap(obj);return obj;} /**
   * Removing prototype chain from a given object.
   * @param  {Object} object   object to be stripped
   * @return {Object}          object stripped
   */;var $strip=function $strip(object){object.__proto__ = Object.prototype;return object;} /**
   * Use Object.prototype.toString to determine an element's type
   * This method provide more stricter strategy on type detection,
   * can be worked with typeof.
   * @param  {Mixed}  obj  Variable
   * @return {String}      type of the variable, like typeof,
   *                       but with better precision.
   */;var $typeof=function $typeof(obj){var type=Object.prototype.toString.call(obj);return type.substring(8,type.length - 1).toLowerCase();};$format = dynamicRequire('util').format;$define(global,{$extend:$extend,$define:$define,$declare:$declare,$inherit:$inherit,$defenum:$defenum,$format:$format,$error:$error,$valueCopy:$valueCopy,$clone:$clone,$default:$default,$wrap:$wrap,$apollo:dynamicRequire('./package').version,$strip:$strip,$typeof:$typeof});$define(String.prototype,Object.defineProperties({ /**
     * Repeat current string for given times.
     * @param  {int} times  Times to repeat
     * @return {string}     result
     */repeat:function repeat(times){var res='';for(var i=0;i < times;i++) res += this;return res;}, /**
     * Padding this to given length with specified char from left.
     * @param  {char} ch    padding char
     * @param  {int} length desired length
     * @return {string}     result
     */paddingLeft:function paddingLeft(ch,length){if(this.length < length)return ch.repeat(length - this.length) + this;return this;}, /**
     * Padding this to given length with specified char from right.
     * @param  {char} ch    padding char
     * @param  {int} length desired length
     * @return {string}     result
     */paddingRight:function paddingRight(ch,length){if(this.length < length)return this + ch.repeat(length - this.length);return this;}, /**
     * Tests if this string starts with the given one.
     * @param  {string} str string to test with
     * @param  {number} pos optional, position to start compare, defaults
     *                      to 0
     * @return {bool}       result
     */startsWith:function startsWith(str,pos){if(str === null || str === undefined || str.length === 0)return true;return this.substr(pos || 0,str.length) === str;}, /**
     * Tests if this string ends with the given one.
     * @param  {string} str string to test with
     * @param  {number} len optional, pretend this string is of given length,
     *                      defaults to actual length
     * @return {bool}       result
     */endsWith:function endsWith(str,len){if(str === null || str === undefined || str.length === 0)return true;return this.substr((len || this.length) - str.length,str.length) === str;}, /**
     * Return a string in it's title form.
     * @return {string} string in title case
     * Note: if a word containing upper case, nothing
     *   will be done.
     */toTitleCase:function toTitleCase(){return this.replace(/\b([a-z])(['a-z]*)\b/g,function(all,letter,rest){return letter.toUpperCase() + rest;});}, /**
     * Trim whitespaces at the begining of the string
     * @return {string} trimmed string
     */trimLeft:function trimLeft(){return this.replace(/^\s+/,'');}, /**
     * Trim whitespaces at the ending of the string
     * @return {string} trimmed string
     */trimRight:function trimRight(){return this.replace(/\s+$/,'');}},{back:{ /**
     * get last character in this string
     * @return {String} last character
     */get:function get(){return this[this.length - 1];},configurable:true,enumerable:true},front:{ /**
     * get first character in this string
     * @return {String} first character
     */get:function get(){return this[0];},configurable:true,enumerable:true}}),true);$define(Number.prototype,{ /**
     * Clamp current value to the given range [lb, ub]
     * @param  {number} lb lower bound
     * @param  {number} ub upper bound
     * @return {number}    result
     */clamp:function clamp(lb,ub){var rtn=Number(this);if(lb !== undefined && rtn < lb)rtn = lb;if(ub !== undefined && rtn > ub)rtn = ub;return rtn;}, /**
     * Shortcut to Math.floor(this)
     * @return {number} Math.floor(this)
     */floor:function floor(){return Math.floor(this);}, /**
     * Shortcut to Math.ceil(this)
     * @return {number} Math.ceil(this)
     */ceil:function ceil(){return Math.ceil(this);}, /**
     * Shortcut to Math.round(this) with additional parameters
     * @param  {number} decimals number of decimal digits to round up to
     * @return {number}          rounded number
     */round:function round(decimals){if(decimals){var unit=Math.pow(10,decimals);return Math.round(this * unit) / unit;}return Math.round(this);}, /**
     * Get the thousands separated number
     * @param  {number} decimals  number of decimal digits to remain
     * @param  {string} separator separator
     * @return {string}           separated number
     */toGroup:function toGroup(decimals,separator){decimals = decimals || 0;if(this > -1000 && this < 1000)return this.toFixed(decimals);separator = separator || ',';var sign=this < 0?'-':'';var tmp=Math.abs(this).toFixed(decimals);var intPart,decimalPart;if(decimals > 0){intPart = tmp.substr(0,tmp.length - decimals - 1);decimalPart = tmp.substr(tmp.length - decimals - 1);}else {intPart = tmp;decimalPart = '';}var res='';for(var pos=0,len=intPart.length % 3 || 3;pos < intPart.length;pos += len,len = 3) {if(res !== '')res += separator;res += intPart.substr(pos,len);}return sign + res + decimalPart;}});$define(Array.prototype,Object.defineProperties({ /**
     * get minimum value in this array
     * @return {Mixed} minimal value
     */min:function min(){var res=this[0];for(var i=1;i < this.length;i++) if(this[i] < res)res = this[i];return res;}, /**
     * get maximum value in this array
     * @return {Mixed} maximum value
     */max:function max(){var res=this[0];for(var i=1;i < this.length;i++) if(this[i] > res)res = this[i];return res;}, /**
     * Push a value iif it's not in this array, and return value's index.
     * @param  {Mixed} val  new value
     * @return {int}        index of the value
     * Note: This only works with primitive typed elements, which can be found
     *       with Array#indexOf().
     */add:function add(val){var index=this.indexOf(val);if(index === -1)return this.push(val) - 1;return index;}, /**
     * Find a value in the array and remove it.
     * @param  {Mixed} val  value to remove
     * @return {Array}      this
     * Note: This only works with primitive typed elements, which can be found
     *       with Array#indexOf().
     */remove:function remove(val){var index=this.indexOf(val);if(index > -1){ // Shift copy elements instead of Array#splice() for better performance.
// http://jsperf.com/fast-array-splice/18
while(++index < this.length) this[index - 1] = this[index];this.pop();}return this;}, /**
     * Rotate this array (n->0, n+1->1, ...)
     * @param  {int} n   the offset
     * @return {Array}   this
     */rotate:function rotate(n){if(n < 0)n = n % this.length + this.length;n %= this.length;var middle=n;var next=n;var first=0;while(first < this.length) {var t=this[first];this[first] = this[next];this[next] = t;first++;next++;if(next == this.length)next = middle;else if(first == middle)middle = next;}return this;}, /**
     * Flattern a array with sub arrays.
     * @param  {bool} deep if continue to flatten sub arrays
     * @return {Array}     flattened array.
     */flatten:function flatten(deep){var res=[];if(!deep)return res.concat.apply(res,this);for(var i=0;i < this.length;i++) if(Array.isArray(this[i]))res.push.apply(res,this[i].flatten(true));else res.push(this[i]);return res;}, /**
     * Return unique elements in the array
     * @return {Array}
     */unique:function unique(){var res=[];var dict={};for(var i=0;i < this.length;++i) {var key=this[i].toString();if(dict.hasOwnProperty(key))continue;dict[key] = true;res.push(this[i]);}return res;}, /**
     * shuffle elements in the array in-place
     * @return {Array}
     */shuffle:function shuffle(){for(var n=this.length;n > 0;n--) {var idx=Math.floor(n * Math.random());if(idx != n - 1){var tmp=this[idx];this[idx] = this[n - 1];this[n - 1] = tmp;}}return this;}},{back:{ /**
     * get last element in this array
     * Note: It's not a reference when returning a non-object!
     * @return {Mixed} last element
     */get:function get(){return this[this.length - 1];},configurable:true,enumerable:true},front:{ /**
     * get first element in this array
     * Note: It's not a reference when returning a non-object!
     * @return {Mixed} first element
     */get:function get(){return this[0];},configurable:true,enumerable:true}})); /**
   * Forward declaring prototype functions to Array's static
   * methods.
   */if(Array.map === undefined)['forEach','every','some','filter','map','reduce','reduceRight','slice'].forEach(function(method){var fn=Array.prototype[method];Object.defineProperty(Array,method,{value:function value(a,b,c){return fn.call(a,b,c);}});});if(String.trim === undefined)['trim','trimLeft','trimRight'].forEach(function(method){var fn=String.prototype[method];Object.defineProperty(String,method,{value:function value(a){return fn.call(a);}});});$define(Object,{ /**
     * Determine if an object is empty
     * @param  {Object} obj  object to test
     * @return {bool}        object is empty
     */isEmpty:function isEmpty(obj){if(!obj)return true;for(var key in obj) return false;return true;}, /**
     * Get values of an object, like Object.keys().
     * @param  {Object} obj  object to extract
     * @return {Array}       values in the object
     */values:function values(obj){return Object.keys(obj).map(function(k){return obj[k];});}, /**
     * Vague but fast isObject test
     * Note: new String(), function, array, etc will return true
     * @param  {Mixed} obj  object to test
     * @return {bool}       true if obj is an object and not null
     */isObject:function isObject(obj){ /**
       * Known fastest way to test, the order of the test
       * following: http://jsperf.com/typeof-vs-bool.
       */return obj && typeof obj === 'object';}, /**
     * Strict isObject test, only pure Object will return true
     * Note: only {} will return true
     * @param  {Mixed} obj  object to test
     * @return {bool}       true if obj is strictly an object
     */isObjectStrict:function isObjectStrict(obj){return Object.prototype.toString.call(obj) === '[object Object]';}, /**
     * project $object with projectiong, same behaviour with mongodb projection
     * @param  {Object} object      target object
     * @param  {Object} projection  An object mapping fields to values
     * @param  {Boolean} deep       if true, go deep for sub objects
     * @param  {Boolean} keep       if true, keep undefined field of this
     * @return {Object}             projected object
     */project:function project(object,projection,deep,keep){if(!Object.isObject(projection))return object;var res={};Object.keys(projection).forEach(function(key){var proj=projection[key];if(proj){var obj=object[key];if(deep && Object.isObjectStrict(obj) && Object.isObjectStrict(proj)){res[key] = Object.project(obj,projection[key],deep,keep);}else {if(keep)res[key] = obj;else if(obj !== undefined)res[key] = obj;}}});return res;},Transformer:function Transformer(mapping){var expr=[];expr.push('exec=function (object) {');expr.push('var res = {};');(function loop(lhv,mapping){Object.keys(mapping).forEach(function(key){var source=mapping[key];if(/\W/.test(key))key = '["' + key + '"]';else key = '.' + key;var target=lhv + key;if($typeof(source) == 'object'){expr.push(target + ' = {};');return loop(target,source);}if(true === source)source = 'object' + key;else if($typeof(source) == 'string')source = 'object' + source;else if($typeof(source) == 'function')source = '(' + source.toString() + ')(object)';expr.push(target + ' = ' + source + ';');});})('res',mapping);expr.push('return res;');expr.push('}');this.exec = eval(expr.join(''));}});$define(Function,{ /**
     * Test if an object is a function
     * @param  {Mixed} obj  object to test
     * @return {bool}       true if so
     */isFunction:function isFunction(obj){return typeof obj === 'function';}});$define(Date,{ /**
     * Cast a value to Date
     * @param  {Mixed} obj  object to cast
     * @return {Date}       casted value
     */cast:function cast(obj){if(obj instanceof Date)return obj;if(typeof obj === 'string')obj = Date.parse(obj);if(typeof obj === 'number'){if(isNaN(obj))return null;obj = new Date(obj);if(isNaN(obj.valueOf()))return null;return obj;}return null;}, /**
     * Determine if an object is a Date
     * @param  {Object}     object to test
     * @return {bool}       true iif it's a date.
     */isDate:function isDate(obj){obj = Date.cast(obj);if(obj)return obj >= 0 && obj < 2147483647000;return false;}});$define(Boolean,{ /**
     * Cast a value to bool
     * @param  {Object} obj  object to cast
     * @return {bool}        casted value
     */cast:function cast(obj){if(obj === true || obj === false)return obj;if(typeof obj === 'string')return (/^(true|yes|ok|y|on)$/i.test(obj));return Boolean(obj);}});$define(RegExp,{ /**
     * Escape a string to work within a regular expression
     * @param  {string} str string to escape
     * @return {strign}     escaped string
     */escape:function escape(str){return str.replace(/[-\/\\^$*+?.()|[\]{}]/g,'\\$&');}});$define(JSON,{ /**
     * Try to parse a json string
     * @param  {string} str json string
     * @return {mixed}      parsed result
     */tryParse:function tryParse(str){try{return JSON.parse(str);}catch(e) {return;}}});})();}}).call(this,typeof global !== "undefined"?global:typeof self !== "undefined"?self:typeof window !== "undefined"?window:{});},{"./package":5,"util":4}],7:[function(dynamicRequire,module,exports){var encode=dynamicRequire("./lib/encode.js"),decode=dynamicRequire("./lib/decode.js");exports.decode = function(data,level){return (!level || level <= 0?decode.XML:decode.HTML)(data);};exports.decodeStrict = function(data,level){return (!level || level <= 0?decode.XML:decode.HTMLStrict)(data);};exports.encode = function(data,level){return (!level || level <= 0?encode.XML:encode.HTML)(data);};exports.encodeXML = encode.XML;exports.encodeHTML4 = exports.encodeHTML5 = exports.encodeHTML = encode.HTML;exports.decodeXML = exports.decodeXMLStrict = decode.XML;exports.decodeHTML4 = exports.decodeHTML5 = exports.decodeHTML = decode.HTML;exports.decodeHTML4Strict = exports.decodeHTML5Strict = exports.decodeHTMLStrict = decode.HTMLStrict;exports.escape = encode.escape;},{"./lib/decode.js":8,"./lib/encode.js":10}],8:[function(dynamicRequire,module,exports){var entityMap=dynamicRequire("../maps/entities.json"),legacyMap=dynamicRequire("../maps/legacy.json"),xmlMap=dynamicRequire("../maps/xml.json"),decodeCodePoint=dynamicRequire("./decode_codepoint.js");var decodeXMLStrict=getStrictDecoder(xmlMap),decodeHTMLStrict=getStrictDecoder(entityMap);function getStrictDecoder(map){var keys=Object.keys(map).join("|"),replace=getReplacer(map);keys += "|#[xX][\\da-fA-F]+|#\\d+";var re=new RegExp("&(?:" + keys + ");","g");return function(str){return String(str).replace(re,replace);};}var decodeHTML=(function(){var legacy=Object.keys(legacyMap).sort(sorter);var keys=Object.keys(entityMap).sort(sorter);for(var i=0,j=0;i < keys.length;i++) {if(legacy[j] === keys[i]){keys[i] += ";?";j++;}else {keys[i] += ";";}}var re=new RegExp("&(?:" + keys.join("|") + "|#[xX][\\da-fA-F]+;?|#\\d+;?)","g"),replace=getReplacer(entityMap);function replacer(str){if(str.substr(-1) !== ";")str += ";";return replace(str);} //TODO consider creating a merged map
return function(str){return String(str).replace(re,replacer);};})();function sorter(a,b){return a < b?1:-1;}function getReplacer(map){return function replace(str){if(str.charAt(1) === "#"){if(str.charAt(2) === "X" || str.charAt(2) === "x"){return decodeCodePoint(parseInt(str.substr(3),16));}return decodeCodePoint(parseInt(str.substr(2),10));}return map[str.slice(1,-1)];};}module.exports = {XML:decodeXMLStrict,HTML:decodeHTML,HTMLStrict:decodeHTMLStrict};},{"../maps/entities.json":12,"../maps/legacy.json":13,"../maps/xml.json":14,"./decode_codepoint.js":9}],9:[function(dynamicRequire,module,exports){var decodeMap=dynamicRequire("../maps/decode.json");module.exports = decodeCodePoint; // modified version of https://github.com/mathiasbynens/he/blob/master/src/he.js#L94-L119
function decodeCodePoint(codePoint){if(codePoint >= 0xD800 && codePoint <= 0xDFFF || codePoint > 0x10FFFF){return "�";}if(codePoint in decodeMap){codePoint = decodeMap[codePoint];}var output="";if(codePoint > 0xFFFF){codePoint -= 0x10000;output += String.fromCharCode(codePoint >>> 10 & 0x3FF | 0xD800);codePoint = 0xDC00 | codePoint & 0x3FF;}output += String.fromCharCode(codePoint);return output;}},{"../maps/decode.json":11}],10:[function(dynamicRequire,module,exports){var inverseXML=getInverseObj(dynamicRequire("../maps/xml.json")),xmlReplacer=getInverseReplacer(inverseXML);exports.XML = getInverse(inverseXML,xmlReplacer);var inverseHTML=getInverseObj(dynamicRequire("../maps/entities.json")),htmlReplacer=getInverseReplacer(inverseHTML);exports.HTML = getInverse(inverseHTML,htmlReplacer);function getInverseObj(obj){return Object.keys(obj).sort().reduce(function(inverse,name){inverse[obj[name]] = "&" + name + ";";return inverse;},{});}function getInverseReplacer(inverse){var single=[],multiple=[];Object.keys(inverse).forEach(function(k){if(k.length === 1){single.push("\\" + k);}else {multiple.push(k);}}); //TODO add ranges
multiple.unshift("[" + single.join("") + "]");return new RegExp(multiple.join("|"),"g");}var re_nonASCII=/[^\0-\x7F]/g,re_astralSymbols=/[\uD800-\uDBFF][\uDC00-\uDFFF]/g;function singleCharReplacer(c){return "&#x" + c.charCodeAt(0).toString(16).toUpperCase() + ";";}function astralReplacer(c){ // http://mathiasbynens.be/notes/javascript-encoding#surrogate-formulae
var high=c.charCodeAt(0);var low=c.charCodeAt(1);var codePoint=(high - 0xD800) * 0x400 + low - 0xDC00 + 0x10000;return "&#x" + codePoint.toString(16).toUpperCase() + ";";}function getInverse(inverse,re){function func(name){return inverse[name];}return function(data){return data.replace(re,func).replace(re_astralSymbols,astralReplacer).replace(re_nonASCII,singleCharReplacer);};}var re_xmlChars=getInverseReplacer(inverseXML);function escapeXML(data){return data.replace(re_xmlChars,singleCharReplacer).replace(re_astralSymbols,astralReplacer).replace(re_nonASCII,singleCharReplacer);}exports.escape = escapeXML;},{"../maps/entities.json":12,"../maps/xml.json":14}],11:[function(dynamicRequire,module,exports){module.exports = {"0":65533,"128":8364,"130":8218,"131":402,"132":8222,"133":8230,"134":8224,"135":8225,"136":710,"137":8240,"138":352,"139":8249,"140":338,"142":381,"145":8216,"146":8217,"147":8220,"148":8221,"149":8226,"150":8211,"151":8212,"152":732,"153":8482,"154":353,"155":8250,"156":339,"158":382,"159":376};},{}],12:[function(dynamicRequire,module,exports){module.exports = {"Aacute":"Á","aacute":"á","Abreve":"Ă","abreve":"ă","ac":"∾","acd":"∿","acE":"∾̳","Acirc":"Â","acirc":"â","acute":"´","Acy":"А","acy":"а","AElig":"Æ","aelig":"æ","af":"⁡","Afr":"𝔄","afr":"𝔞","Agrave":"À","agrave":"à","alefsym":"ℵ","aleph":"ℵ","Alpha":"Α","alpha":"α","Amacr":"Ā","amacr":"ā","amalg":"⨿","amp":"&","AMP":"&","andand":"⩕","And":"⩓","and":"∧","andd":"⩜","andslope":"⩘","andv":"⩚","ang":"∠","ange":"⦤","angle":"∠","angmsdaa":"⦨","angmsdab":"⦩","angmsdac":"⦪","angmsdad":"⦫","angmsdae":"⦬","angmsdaf":"⦭","angmsdag":"⦮","angmsdah":"⦯","angmsd":"∡","angrt":"∟","angrtvb":"⊾","angrtvbd":"⦝","angsph":"∢","angst":"Å","angzarr":"⍼","Aogon":"Ą","aogon":"ą","Aopf":"𝔸","aopf":"𝕒","apacir":"⩯","ap":"≈","apE":"⩰","ape":"≊","apid":"≋","apos":"'","ApplyFunction":"⁡","approx":"≈","approxeq":"≊","Aring":"Å","aring":"å","Ascr":"𝒜","ascr":"𝒶","Assign":"≔","ast":"*","asymp":"≈","asympeq":"≍","Atilde":"Ã","atilde":"ã","Auml":"Ä","auml":"ä","awconint":"∳","awint":"⨑","backcong":"≌","backepsilon":"϶","backprime":"‵","backsim":"∽","backsimeq":"⋍","Backslash":"∖","Barv":"⫧","barvee":"⊽","barwed":"⌅","Barwed":"⌆","barwedge":"⌅","bbrk":"⎵","bbrktbrk":"⎶","bcong":"≌","Bcy":"Б","bcy":"б","bdquo":"„","becaus":"∵","because":"∵","Because":"∵","bemptyv":"⦰","bepsi":"϶","bernou":"ℬ","Bernoullis":"ℬ","Beta":"Β","beta":"β","beth":"ℶ","between":"≬","Bfr":"𝔅","bfr":"𝔟","bigcap":"⋂","bigcirc":"◯","bigcup":"⋃","bigodot":"⨀","bigoplus":"⨁","bigotimes":"⨂","bigsqcup":"⨆","bigstar":"★","bigtriangledown":"▽","bigtriangleup":"△","biguplus":"⨄","bigvee":"⋁","bigwedge":"⋀","bkarow":"⤍","blacklozenge":"⧫","blacksquare":"▪","blacktriangle":"▴","blacktriangledown":"▾","blacktriangleleft":"◂","blacktriangleright":"▸","blank":"␣","blk12":"▒","blk14":"░","blk34":"▓","block":"█","bne":"=⃥","bnequiv":"≡⃥","bNot":"⫭","bnot":"⌐","Bopf":"𝔹","bopf":"𝕓","bot":"⊥","bottom":"⊥","bowtie":"⋈","boxbox":"⧉","boxdl":"┐","boxdL":"╕","boxDl":"╖","boxDL":"╗","boxdr":"┌","boxdR":"╒","boxDr":"╓","boxDR":"╔","boxh":"─","boxH":"═","boxhd":"┬","boxHd":"╤","boxhD":"╥","boxHD":"╦","boxhu":"┴","boxHu":"╧","boxhU":"╨","boxHU":"╩","boxminus":"⊟","boxplus":"⊞","boxtimes":"⊠","boxul":"┘","boxuL":"╛","boxUl":"╜","boxUL":"╝","boxur":"└","boxuR":"╘","boxUr":"╙","boxUR":"╚","boxv":"│","boxV":"║","boxvh":"┼","boxvH":"╪","boxVh":"╫","boxVH":"╬","boxvl":"┤","boxvL":"╡","boxVl":"╢","boxVL":"╣","boxvr":"├","boxvR":"╞","boxVr":"╟","boxVR":"╠","bprime":"‵","breve":"˘","Breve":"˘","brvbar":"¦","bscr":"𝒷","Bscr":"ℬ","bsemi":"⁏","bsim":"∽","bsime":"⋍","bsolb":"⧅","bsol":"\\","bsolhsub":"⟈","bull":"•","bullet":"•","bump":"≎","bumpE":"⪮","bumpe":"≏","Bumpeq":"≎","bumpeq":"≏","Cacute":"Ć","cacute":"ć","capand":"⩄","capbrcup":"⩉","capcap":"⩋","cap":"∩","Cap":"⋒","capcup":"⩇","capdot":"⩀","CapitalDifferentialD":"ⅅ","caps":"∩︀","caret":"⁁","caron":"ˇ","Cayleys":"ℭ","ccaps":"⩍","Ccaron":"Č","ccaron":"č","Ccedil":"Ç","ccedil":"ç","Ccirc":"Ĉ","ccirc":"ĉ","Cconint":"∰","ccups":"⩌","ccupssm":"⩐","Cdot":"Ċ","cdot":"ċ","cedil":"¸","Cedilla":"¸","cemptyv":"⦲","cent":"¢","centerdot":"·","CenterDot":"·","cfr":"𝔠","Cfr":"ℭ","CHcy":"Ч","chcy":"ч","check":"✓","checkmark":"✓","Chi":"Χ","chi":"χ","circ":"ˆ","circeq":"≗","circlearrowleft":"↺","circlearrowright":"↻","circledast":"⊛","circledcirc":"⊚","circleddash":"⊝","CircleDot":"⊙","circledR":"®","circledS":"Ⓢ","CircleMinus":"⊖","CirclePlus":"⊕","CircleTimes":"⊗","cir":"○","cirE":"⧃","cire":"≗","cirfnint":"⨐","cirmid":"⫯","cirscir":"⧂","ClockwiseContourIntegral":"∲","CloseCurlyDoubleQuote":"”","CloseCurlyQuote":"’","clubs":"♣","clubsuit":"♣","colon":":","Colon":"∷","Colone":"⩴","colone":"≔","coloneq":"≔","comma":",","commat":"@","comp":"∁","compfn":"∘","complement":"∁","complexes":"ℂ","cong":"≅","congdot":"⩭","Congruent":"≡","conint":"∮","Conint":"∯","ContourIntegral":"∮","copf":"𝕔","Copf":"ℂ","coprod":"∐","Coproduct":"∐","copy":"©","COPY":"©","copysr":"℗","CounterClockwiseContourIntegral":"∳","crarr":"↵","cross":"✗","Cross":"⨯","Cscr":"𝒞","cscr":"𝒸","csub":"⫏","csube":"⫑","csup":"⫐","csupe":"⫒","ctdot":"⋯","cudarrl":"⤸","cudarrr":"⤵","cuepr":"⋞","cuesc":"⋟","cularr":"↶","cularrp":"⤽","cupbrcap":"⩈","cupcap":"⩆","CupCap":"≍","cup":"∪","Cup":"⋓","cupcup":"⩊","cupdot":"⊍","cupor":"⩅","cups":"∪︀","curarr":"↷","curarrm":"⤼","curlyeqprec":"⋞","curlyeqsucc":"⋟","curlyvee":"⋎","curlywedge":"⋏","curren":"¤","curvearrowleft":"↶","curvearrowright":"↷","cuvee":"⋎","cuwed":"⋏","cwconint":"∲","cwint":"∱","cylcty":"⌭","dagger":"†","Dagger":"‡","daleth":"ℸ","darr":"↓","Darr":"↡","dArr":"⇓","dash":"‐","Dashv":"⫤","dashv":"⊣","dbkarow":"⤏","dblac":"˝","Dcaron":"Ď","dcaron":"ď","Dcy":"Д","dcy":"д","ddagger":"‡","ddarr":"⇊","DD":"ⅅ","dd":"ⅆ","DDotrahd":"⤑","ddotseq":"⩷","deg":"°","Del":"∇","Delta":"Δ","delta":"δ","demptyv":"⦱","dfisht":"⥿","Dfr":"𝔇","dfr":"𝔡","dHar":"⥥","dharl":"⇃","dharr":"⇂","DiacriticalAcute":"´","DiacriticalDot":"˙","DiacriticalDoubleAcute":"˝","DiacriticalGrave":"`","DiacriticalTilde":"˜","diam":"⋄","diamond":"⋄","Diamond":"⋄","diamondsuit":"♦","diams":"♦","die":"¨","DifferentialD":"ⅆ","digamma":"ϝ","disin":"⋲","div":"÷","divide":"÷","divideontimes":"⋇","divonx":"⋇","DJcy":"Ђ","djcy":"ђ","dlcorn":"⌞","dlcrop":"⌍","dollar":"$","Dopf":"𝔻","dopf":"𝕕","Dot":"¨","dot":"˙","DotDot":"⃜","doteq":"≐","doteqdot":"≑","DotEqual":"≐","dotminus":"∸","dotplus":"∔","dotsquare":"⊡","doublebarwedge":"⌆","DoubleContourIntegral":"∯","DoubleDot":"¨","DoubleDownArrow":"⇓","DoubleLeftArrow":"⇐","DoubleLeftRightArrow":"⇔","DoubleLeftTee":"⫤","DoubleLongLeftArrow":"⟸","DoubleLongLeftRightArrow":"⟺","DoubleLongRightArrow":"⟹","DoubleRightArrow":"⇒","DoubleRightTee":"⊨","DoubleUpArrow":"⇑","DoubleUpDownArrow":"⇕","DoubleVerticalBar":"∥","DownArrowBar":"⤓","downarrow":"↓","DownArrow":"↓","Downarrow":"⇓","DownArrowUpArrow":"⇵","DownBreve":"̑","downdownarrows":"⇊","downharpoonleft":"⇃","downharpoonright":"⇂","DownLeftRightVector":"⥐","DownLeftTeeVector":"⥞","DownLeftVectorBar":"⥖","DownLeftVector":"↽","DownRightTeeVector":"⥟","DownRightVectorBar":"⥗","DownRightVector":"⇁","DownTeeArrow":"↧","DownTee":"⊤","drbkarow":"⤐","drcorn":"⌟","drcrop":"⌌","Dscr":"𝒟","dscr":"𝒹","DScy":"Ѕ","dscy":"ѕ","dsol":"⧶","Dstrok":"Đ","dstrok":"đ","dtdot":"⋱","dtri":"▿","dtrif":"▾","duarr":"⇵","duhar":"⥯","dwangle":"⦦","DZcy":"Џ","dzcy":"џ","dzigrarr":"⟿","Eacute":"É","eacute":"é","easter":"⩮","Ecaron":"Ě","ecaron":"ě","Ecirc":"Ê","ecirc":"ê","ecir":"≖","ecolon":"≕","Ecy":"Э","ecy":"э","eDDot":"⩷","Edot":"Ė","edot":"ė","eDot":"≑","ee":"ⅇ","efDot":"≒","Efr":"𝔈","efr":"𝔢","eg":"⪚","Egrave":"È","egrave":"è","egs":"⪖","egsdot":"⪘","el":"⪙","Element":"∈","elinters":"⏧","ell":"ℓ","els":"⪕","elsdot":"⪗","Emacr":"Ē","emacr":"ē","empty":"∅","emptyset":"∅","EmptySmallSquare":"◻","emptyv":"∅","EmptyVerySmallSquare":"▫","emsp13":" ","emsp14":" ","emsp":" ","ENG":"Ŋ","eng":"ŋ","ensp":" ","Eogon":"Ę","eogon":"ę","Eopf":"𝔼","eopf":"𝕖","epar":"⋕","eparsl":"⧣","eplus":"⩱","epsi":"ε","Epsilon":"Ε","epsilon":"ε","epsiv":"ϵ","eqcirc":"≖","eqcolon":"≕","eqsim":"≂","eqslantgtr":"⪖","eqslantless":"⪕","Equal":"⩵","equals":"=","EqualTilde":"≂","equest":"≟","Equilibrium":"⇌","equiv":"≡","equivDD":"⩸","eqvparsl":"⧥","erarr":"⥱","erDot":"≓","escr":"ℯ","Escr":"ℰ","esdot":"≐","Esim":"⩳","esim":"≂","Eta":"Η","eta":"η","ETH":"Ð","eth":"ð","Euml":"Ë","euml":"ë","euro":"€","excl":"!","exist":"∃","Exists":"∃","expectation":"ℰ","exponentiale":"ⅇ","ExponentialE":"ⅇ","fallingdotseq":"≒","Fcy":"Ф","fcy":"ф","female":"♀","ffilig":"ﬃ","fflig":"ﬀ","ffllig":"ﬄ","Ffr":"𝔉","ffr":"𝔣","filig":"ﬁ","FilledSmallSquare":"◼","FilledVerySmallSquare":"▪","fjlig":"fj","flat":"♭","fllig":"ﬂ","fltns":"▱","fnof":"ƒ","Fopf":"𝔽","fopf":"𝕗","forall":"∀","ForAll":"∀","fork":"⋔","forkv":"⫙","Fouriertrf":"ℱ","fpartint":"⨍","frac12":"½","frac13":"⅓","frac14":"¼","frac15":"⅕","frac16":"⅙","frac18":"⅛","frac23":"⅔","frac25":"⅖","frac34":"¾","frac35":"⅗","frac38":"⅜","frac45":"⅘","frac56":"⅚","frac58":"⅝","frac78":"⅞","frasl":"⁄","frown":"⌢","fscr":"𝒻","Fscr":"ℱ","gacute":"ǵ","Gamma":"Γ","gamma":"γ","Gammad":"Ϝ","gammad":"ϝ","gap":"⪆","Gbreve":"Ğ","gbreve":"ğ","Gcedil":"Ģ","Gcirc":"Ĝ","gcirc":"ĝ","Gcy":"Г","gcy":"г","Gdot":"Ġ","gdot":"ġ","ge":"≥","gE":"≧","gEl":"⪌","gel":"⋛","geq":"≥","geqq":"≧","geqslant":"⩾","gescc":"⪩","ges":"⩾","gesdot":"⪀","gesdoto":"⪂","gesdotol":"⪄","gesl":"⋛︀","gesles":"⪔","Gfr":"𝔊","gfr":"𝔤","gg":"≫","Gg":"⋙","ggg":"⋙","gimel":"ℷ","GJcy":"Ѓ","gjcy":"ѓ","gla":"⪥","gl":"≷","glE":"⪒","glj":"⪤","gnap":"⪊","gnapprox":"⪊","gne":"⪈","gnE":"≩","gneq":"⪈","gneqq":"≩","gnsim":"⋧","Gopf":"𝔾","gopf":"𝕘","grave":"`","GreaterEqual":"≥","GreaterEqualLess":"⋛","GreaterFullEqual":"≧","GreaterGreater":"⪢","GreaterLess":"≷","GreaterSlantEqual":"⩾","GreaterTilde":"≳","Gscr":"𝒢","gscr":"ℊ","gsim":"≳","gsime":"⪎","gsiml":"⪐","gtcc":"⪧","gtcir":"⩺","gt":">","GT":">","Gt":"≫","gtdot":"⋗","gtlPar":"⦕","gtquest":"⩼","gtrapprox":"⪆","gtrarr":"⥸","gtrdot":"⋗","gtreqless":"⋛","gtreqqless":"⪌","gtrless":"≷","gtrsim":"≳","gvertneqq":"≩︀","gvnE":"≩︀","Hacek":"ˇ","hairsp":" ","half":"½","hamilt":"ℋ","HARDcy":"Ъ","hardcy":"ъ","harrcir":"⥈","harr":"↔","hArr":"⇔","harrw":"↭","Hat":"^","hbar":"ℏ","Hcirc":"Ĥ","hcirc":"ĥ","hearts":"♥","heartsuit":"♥","hellip":"…","hercon":"⊹","hfr":"𝔥","Hfr":"ℌ","HilbertSpace":"ℋ","hksearow":"⤥","hkswarow":"⤦","hoarr":"⇿","homtht":"∻","hookleftarrow":"↩","hookrightarrow":"↪","hopf":"𝕙","Hopf":"ℍ","horbar":"―","HorizontalLine":"─","hscr":"𝒽","Hscr":"ℋ","hslash":"ℏ","Hstrok":"Ħ","hstrok":"ħ","HumpDownHump":"≎","HumpEqual":"≏","hybull":"⁃","hyphen":"‐","Iacute":"Í","iacute":"í","ic":"⁣","Icirc":"Î","icirc":"î","Icy":"И","icy":"и","Idot":"İ","IEcy":"Е","iecy":"е","iexcl":"¡","iff":"⇔","ifr":"𝔦","Ifr":"ℑ","Igrave":"Ì","igrave":"ì","ii":"ⅈ","iiiint":"⨌","iiint":"∭","iinfin":"⧜","iiota":"℩","IJlig":"Ĳ","ijlig":"ĳ","Imacr":"Ī","imacr":"ī","image":"ℑ","ImaginaryI":"ⅈ","imagline":"ℐ","imagpart":"ℑ","imath":"ı","Im":"ℑ","imof":"⊷","imped":"Ƶ","Implies":"⇒","incare":"℅","in":"∈","infin":"∞","infintie":"⧝","inodot":"ı","intcal":"⊺","int":"∫","Int":"∬","integers":"ℤ","Integral":"∫","intercal":"⊺","Intersection":"⋂","intlarhk":"⨗","intprod":"⨼","InvisibleComma":"⁣","InvisibleTimes":"⁢","IOcy":"Ё","iocy":"ё","Iogon":"Į","iogon":"į","Iopf":"𝕀","iopf":"𝕚","Iota":"Ι","iota":"ι","iprod":"⨼","iquest":"¿","iscr":"𝒾","Iscr":"ℐ","isin":"∈","isindot":"⋵","isinE":"⋹","isins":"⋴","isinsv":"⋳","isinv":"∈","it":"⁢","Itilde":"Ĩ","itilde":"ĩ","Iukcy":"І","iukcy":"і","Iuml":"Ï","iuml":"ï","Jcirc":"Ĵ","jcirc":"ĵ","Jcy":"Й","jcy":"й","Jfr":"𝔍","jfr":"𝔧","jmath":"ȷ","Jopf":"𝕁","jopf":"𝕛","Jscr":"𝒥","jscr":"𝒿","Jsercy":"Ј","jsercy":"ј","Jukcy":"Є","jukcy":"є","Kappa":"Κ","kappa":"κ","kappav":"ϰ","Kcedil":"Ķ","kcedil":"ķ","Kcy":"К","kcy":"к","Kfr":"𝔎","kfr":"𝔨","kgreen":"ĸ","KHcy":"Х","khcy":"х","KJcy":"Ќ","kjcy":"ќ","Kopf":"𝕂","kopf":"𝕜","Kscr":"𝒦","kscr":"𝓀","lAarr":"⇚","Lacute":"Ĺ","lacute":"ĺ","laemptyv":"⦴","lagran":"ℒ","Lambda":"Λ","lambda":"λ","lang":"⟨","Lang":"⟪","langd":"⦑","langle":"⟨","lap":"⪅","Laplacetrf":"ℒ","laquo":"«","larrb":"⇤","larrbfs":"⤟","larr":"←","Larr":"↞","lArr":"⇐","larrfs":"⤝","larrhk":"↩","larrlp":"↫","larrpl":"⤹","larrsim":"⥳","larrtl":"↢","latail":"⤙","lAtail":"⤛","lat":"⪫","late":"⪭","lates":"⪭︀","lbarr":"⤌","lBarr":"⤎","lbbrk":"❲","lbrace":"{","lbrack":"[","lbrke":"⦋","lbrksld":"⦏","lbrkslu":"⦍","Lcaron":"Ľ","lcaron":"ľ","Lcedil":"Ļ","lcedil":"ļ","lceil":"⌈","lcub":"{","Lcy":"Л","lcy":"л","ldca":"⤶","ldquo":"“","ldquor":"„","ldrdhar":"⥧","ldrushar":"⥋","ldsh":"↲","le":"≤","lE":"≦","LeftAngleBracket":"⟨","LeftArrowBar":"⇤","leftarrow":"←","LeftArrow":"←","Leftarrow":"⇐","LeftArrowRightArrow":"⇆","leftarrowtail":"↢","LeftCeiling":"⌈","LeftDoubleBracket":"⟦","LeftDownTeeVector":"⥡","LeftDownVectorBar":"⥙","LeftDownVector":"⇃","LeftFloor":"⌊","leftharpoondown":"↽","leftharpoonup":"↼","leftleftarrows":"⇇","leftrightarrow":"↔","LeftRightArrow":"↔","Leftrightarrow":"⇔","leftrightarrows":"⇆","leftrightharpoons":"⇋","leftrightsquigarrow":"↭","LeftRightVector":"⥎","LeftTeeArrow":"↤","LeftTee":"⊣","LeftTeeVector":"⥚","leftthreetimes":"⋋","LeftTriangleBar":"⧏","LeftTriangle":"⊲","LeftTriangleEqual":"⊴","LeftUpDownVector":"⥑","LeftUpTeeVector":"⥠","LeftUpVectorBar":"⥘","LeftUpVector":"↿","LeftVectorBar":"⥒","LeftVector":"↼","lEg":"⪋","leg":"⋚","leq":"≤","leqq":"≦","leqslant":"⩽","lescc":"⪨","les":"⩽","lesdot":"⩿","lesdoto":"⪁","lesdotor":"⪃","lesg":"⋚︀","lesges":"⪓","lessapprox":"⪅","lessdot":"⋖","lesseqgtr":"⋚","lesseqqgtr":"⪋","LessEqualGreater":"⋚","LessFullEqual":"≦","LessGreater":"≶","lessgtr":"≶","LessLess":"⪡","lesssim":"≲","LessSlantEqual":"⩽","LessTilde":"≲","lfisht":"⥼","lfloor":"⌊","Lfr":"𝔏","lfr":"𝔩","lg":"≶","lgE":"⪑","lHar":"⥢","lhard":"↽","lharu":"↼","lharul":"⥪","lhblk":"▄","LJcy":"Љ","ljcy":"љ","llarr":"⇇","ll":"≪","Ll":"⋘","llcorner":"⌞","Lleftarrow":"⇚","llhard":"⥫","lltri":"◺","Lmidot":"Ŀ","lmidot":"ŀ","lmoustache":"⎰","lmoust":"⎰","lnap":"⪉","lnapprox":"⪉","lne":"⪇","lnE":"≨","lneq":"⪇","lneqq":"≨","lnsim":"⋦","loang":"⟬","loarr":"⇽","lobrk":"⟦","longleftarrow":"⟵","LongLeftArrow":"⟵","Longleftarrow":"⟸","longleftrightarrow":"⟷","LongLeftRightArrow":"⟷","Longleftrightarrow":"⟺","longmapsto":"⟼","longrightarrow":"⟶","LongRightArrow":"⟶","Longrightarrow":"⟹","looparrowleft":"↫","looparrowright":"↬","lopar":"⦅","Lopf":"𝕃","lopf":"𝕝","loplus":"⨭","lotimes":"⨴","lowast":"∗","lowbar":"_","LowerLeftArrow":"↙","LowerRightArrow":"↘","loz":"◊","lozenge":"◊","lozf":"⧫","lpar":"(","lparlt":"⦓","lrarr":"⇆","lrcorner":"⌟","lrhar":"⇋","lrhard":"⥭","lrm":"‎","lrtri":"⊿","lsaquo":"‹","lscr":"𝓁","Lscr":"ℒ","lsh":"↰","Lsh":"↰","lsim":"≲","lsime":"⪍","lsimg":"⪏","lsqb":"[","lsquo":"‘","lsquor":"‚","Lstrok":"Ł","lstrok":"ł","ltcc":"⪦","ltcir":"⩹","lt":"<","LT":"<","Lt":"≪","ltdot":"⋖","lthree":"⋋","ltimes":"⋉","ltlarr":"⥶","ltquest":"⩻","ltri":"◃","ltrie":"⊴","ltrif":"◂","ltrPar":"⦖","lurdshar":"⥊","luruhar":"⥦","lvertneqq":"≨︀","lvnE":"≨︀","macr":"¯","male":"♂","malt":"✠","maltese":"✠","Map":"⤅","map":"↦","mapsto":"↦","mapstodown":"↧","mapstoleft":"↤","mapstoup":"↥","marker":"▮","mcomma":"⨩","Mcy":"М","mcy":"м","mdash":"—","mDDot":"∺","measuredangle":"∡","MediumSpace":" ","Mellintrf":"ℳ","Mfr":"𝔐","mfr":"𝔪","mho":"℧","micro":"µ","midast":"*","midcir":"⫰","mid":"∣","middot":"·","minusb":"⊟","minus":"−","minusd":"∸","minusdu":"⨪","MinusPlus":"∓","mlcp":"⫛","mldr":"…","mnplus":"∓","models":"⊧","Mopf":"𝕄","mopf":"𝕞","mp":"∓","mscr":"𝓂","Mscr":"ℳ","mstpos":"∾","Mu":"Μ","mu":"μ","multimap":"⊸","mumap":"⊸","nabla":"∇","Nacute":"Ń","nacute":"ń","nang":"∠⃒","nap":"≉","napE":"⩰̸","napid":"≋̸","napos":"ŉ","napprox":"≉","natural":"♮","naturals":"ℕ","natur":"♮","nbsp":" ","nbump":"≎̸","nbumpe":"≏̸","ncap":"⩃","Ncaron":"Ň","ncaron":"ň","Ncedil":"Ņ","ncedil":"ņ","ncong":"≇","ncongdot":"⩭̸","ncup":"⩂","Ncy":"Н","ncy":"н","ndash":"–","nearhk":"⤤","nearr":"↗","neArr":"⇗","nearrow":"↗","ne":"≠","nedot":"≐̸","NegativeMediumSpace":"​","NegativeThickSpace":"​","NegativeThinSpace":"​","NegativeVeryThinSpace":"​","nequiv":"≢","nesear":"⤨","nesim":"≂̸","NestedGreaterGreater":"≫","NestedLessLess":"≪","NewLine":"\n","nexist":"∄","nexists":"∄","Nfr":"𝔑","nfr":"𝔫","ngE":"≧̸","nge":"≱","ngeq":"≱","ngeqq":"≧̸","ngeqslant":"⩾̸","nges":"⩾̸","nGg":"⋙̸","ngsim":"≵","nGt":"≫⃒","ngt":"≯","ngtr":"≯","nGtv":"≫̸","nharr":"↮","nhArr":"⇎","nhpar":"⫲","ni":"∋","nis":"⋼","nisd":"⋺","niv":"∋","NJcy":"Њ","njcy":"њ","nlarr":"↚","nlArr":"⇍","nldr":"‥","nlE":"≦̸","nle":"≰","nleftarrow":"↚","nLeftarrow":"⇍","nleftrightarrow":"↮","nLeftrightarrow":"⇎","nleq":"≰","nleqq":"≦̸","nleqslant":"⩽̸","nles":"⩽̸","nless":"≮","nLl":"⋘̸","nlsim":"≴","nLt":"≪⃒","nlt":"≮","nltri":"⋪","nltrie":"⋬","nLtv":"≪̸","nmid":"∤","NoBreak":"⁠","NonBreakingSpace":" ","nopf":"𝕟","Nopf":"ℕ","Not":"⫬","not":"¬","NotCongruent":"≢","NotCupCap":"≭","NotDoubleVerticalBar":"∦","NotElement":"∉","NotEqual":"≠","NotEqualTilde":"≂̸","NotExists":"∄","NotGreater":"≯","NotGreaterEqual":"≱","NotGreaterFullEqual":"≧̸","NotGreaterGreater":"≫̸","NotGreaterLess":"≹","NotGreaterSlantEqual":"⩾̸","NotGreaterTilde":"≵","NotHumpDownHump":"≎̸","NotHumpEqual":"≏̸","notin":"∉","notindot":"⋵̸","notinE":"⋹̸","notinva":"∉","notinvb":"⋷","notinvc":"⋶","NotLeftTriangleBar":"⧏̸","NotLeftTriangle":"⋪","NotLeftTriangleEqual":"⋬","NotLess":"≮","NotLessEqual":"≰","NotLessGreater":"≸","NotLessLess":"≪̸","NotLessSlantEqual":"⩽̸","NotLessTilde":"≴","NotNestedGreaterGreater":"⪢̸","NotNestedLessLess":"⪡̸","notni":"∌","notniva":"∌","notnivb":"⋾","notnivc":"⋽","NotPrecedes":"⊀","NotPrecedesEqual":"⪯̸","NotPrecedesSlantEqual":"⋠","NotReverseElement":"∌","NotRightTriangleBar":"⧐̸","NotRightTriangle":"⋫","NotRightTriangleEqual":"⋭","NotSquareSubset":"⊏̸","NotSquareSubsetEqual":"⋢","NotSquareSuperset":"⊐̸","NotSquareSupersetEqual":"⋣","NotSubset":"⊂⃒","NotSubsetEqual":"⊈","NotSucceeds":"⊁","NotSucceedsEqual":"⪰̸","NotSucceedsSlantEqual":"⋡","NotSucceedsTilde":"≿̸","NotSuperset":"⊃⃒","NotSupersetEqual":"⊉","NotTilde":"≁","NotTildeEqual":"≄","NotTildeFullEqual":"≇","NotTildeTilde":"≉","NotVerticalBar":"∤","nparallel":"∦","npar":"∦","nparsl":"⫽⃥","npart":"∂̸","npolint":"⨔","npr":"⊀","nprcue":"⋠","nprec":"⊀","npreceq":"⪯̸","npre":"⪯̸","nrarrc":"⤳̸","nrarr":"↛","nrArr":"⇏","nrarrw":"↝̸","nrightarrow":"↛","nRightarrow":"⇏","nrtri":"⋫","nrtrie":"⋭","nsc":"⊁","nsccue":"⋡","nsce":"⪰̸","Nscr":"𝒩","nscr":"𝓃","nshortmid":"∤","nshortparallel":"∦","nsim":"≁","nsime":"≄","nsimeq":"≄","nsmid":"∤","nspar":"∦","nsqsube":"⋢","nsqsupe":"⋣","nsub":"⊄","nsubE":"⫅̸","nsube":"⊈","nsubset":"⊂⃒","nsubseteq":"⊈","nsubseteqq":"⫅̸","nsucc":"⊁","nsucceq":"⪰̸","nsup":"⊅","nsupE":"⫆̸","nsupe":"⊉","nsupset":"⊃⃒","nsupseteq":"⊉","nsupseteqq":"⫆̸","ntgl":"≹","Ntilde":"Ñ","ntilde":"ñ","ntlg":"≸","ntriangleleft":"⋪","ntrianglelefteq":"⋬","ntriangleright":"⋫","ntrianglerighteq":"⋭","Nu":"Ν","nu":"ν","num":"#","numero":"№","numsp":" ","nvap":"≍⃒","nvdash":"⊬","nvDash":"⊭","nVdash":"⊮","nVDash":"⊯","nvge":"≥⃒","nvgt":">⃒","nvHarr":"⤄","nvinfin":"⧞","nvlArr":"⤂","nvle":"≤⃒","nvlt":"<⃒","nvltrie":"⊴⃒","nvrArr":"⤃","nvrtrie":"⊵⃒","nvsim":"∼⃒","nwarhk":"⤣","nwarr":"↖","nwArr":"⇖","nwarrow":"↖","nwnear":"⤧","Oacute":"Ó","oacute":"ó","oast":"⊛","Ocirc":"Ô","ocirc":"ô","ocir":"⊚","Ocy":"О","ocy":"о","odash":"⊝","Odblac":"Ő","odblac":"ő","odiv":"⨸","odot":"⊙","odsold":"⦼","OElig":"Œ","oelig":"œ","ofcir":"⦿","Ofr":"𝔒","ofr":"𝔬","ogon":"˛","Ograve":"Ò","ograve":"ò","ogt":"⧁","ohbar":"⦵","ohm":"Ω","oint":"∮","olarr":"↺","olcir":"⦾","olcross":"⦻","oline":"‾","olt":"⧀","Omacr":"Ō","omacr":"ō","Omega":"Ω","omega":"ω","Omicron":"Ο","omicron":"ο","omid":"⦶","ominus":"⊖","Oopf":"𝕆","oopf":"𝕠","opar":"⦷","OpenCurlyDoubleQuote":"“","OpenCurlyQuote":"‘","operp":"⦹","oplus":"⊕","orarr":"↻","Or":"⩔","or":"∨","ord":"⩝","order":"ℴ","orderof":"ℴ","ordf":"ª","ordm":"º","origof":"⊶","oror":"⩖","orslope":"⩗","orv":"⩛","oS":"Ⓢ","Oscr":"𝒪","oscr":"ℴ","Oslash":"Ø","oslash":"ø","osol":"⊘","Otilde":"Õ","otilde":"õ","otimesas":"⨶","Otimes":"⨷","otimes":"⊗","Ouml":"Ö","ouml":"ö","ovbar":"⌽","OverBar":"‾","OverBrace":"⏞","OverBracket":"⎴","OverParenthesis":"⏜","para":"¶","parallel":"∥","par":"∥","parsim":"⫳","parsl":"⫽","part":"∂","PartialD":"∂","Pcy":"П","pcy":"п","percnt":"%","period":".","permil":"‰","perp":"⊥","pertenk":"‱","Pfr":"𝔓","pfr":"𝔭","Phi":"Φ","phi":"φ","phiv":"ϕ","phmmat":"ℳ","phone":"☎","Pi":"Π","pi":"π","pitchfork":"⋔","piv":"ϖ","planck":"ℏ","planckh":"ℎ","plankv":"ℏ","plusacir":"⨣","plusb":"⊞","pluscir":"⨢","plus":"+","plusdo":"∔","plusdu":"⨥","pluse":"⩲","PlusMinus":"±","plusmn":"±","plussim":"⨦","plustwo":"⨧","pm":"±","Poincareplane":"ℌ","pointint":"⨕","popf":"𝕡","Popf":"ℙ","pound":"£","prap":"⪷","Pr":"⪻","pr":"≺","prcue":"≼","precapprox":"⪷","prec":"≺","preccurlyeq":"≼","Precedes":"≺","PrecedesEqual":"⪯","PrecedesSlantEqual":"≼","PrecedesTilde":"≾","preceq":"⪯","precnapprox":"⪹","precneqq":"⪵","precnsim":"⋨","pre":"⪯","prE":"⪳","precsim":"≾","prime":"′","Prime":"″","primes":"ℙ","prnap":"⪹","prnE":"⪵","prnsim":"⋨","prod":"∏","Product":"∏","profalar":"⌮","profline":"⌒","profsurf":"⌓","prop":"∝","Proportional":"∝","Proportion":"∷","propto":"∝","prsim":"≾","prurel":"⊰","Pscr":"𝒫","pscr":"𝓅","Psi":"Ψ","psi":"ψ","puncsp":" ","Qfr":"𝔔","qfr":"𝔮","qint":"⨌","qopf":"𝕢","Qopf":"ℚ","qprime":"⁗","Qscr":"𝒬","qscr":"𝓆","quaternions":"ℍ","quatint":"⨖","quest":"?","questeq":"≟","quot":"\"","QUOT":"\"","rAarr":"⇛","race":"∽̱","Racute":"Ŕ","racute":"ŕ","radic":"√","raemptyv":"⦳","rang":"⟩","Rang":"⟫","rangd":"⦒","range":"⦥","rangle":"⟩","raquo":"»","rarrap":"⥵","rarrb":"⇥","rarrbfs":"⤠","rarrc":"⤳","rarr":"→","Rarr":"↠","rArr":"⇒","rarrfs":"⤞","rarrhk":"↪","rarrlp":"↬","rarrpl":"⥅","rarrsim":"⥴","Rarrtl":"⤖","rarrtl":"↣","rarrw":"↝","ratail":"⤚","rAtail":"⤜","ratio":"∶","rationals":"ℚ","rbarr":"⤍","rBarr":"⤏","RBarr":"⤐","rbbrk":"❳","rbrace":"}","rbrack":"]","rbrke":"⦌","rbrksld":"⦎","rbrkslu":"⦐","Rcaron":"Ř","rcaron":"ř","Rcedil":"Ŗ","rcedil":"ŗ","rceil":"⌉","rcub":"}","Rcy":"Р","rcy":"р","rdca":"⤷","rdldhar":"⥩","rdquo":"”","rdquor":"”","rdsh":"↳","real":"ℜ","realine":"ℛ","realpart":"ℜ","reals":"ℝ","Re":"ℜ","rect":"▭","reg":"®","REG":"®","ReverseElement":"∋","ReverseEquilibrium":"⇋","ReverseUpEquilibrium":"⥯","rfisht":"⥽","rfloor":"⌋","rfr":"𝔯","Rfr":"ℜ","rHar":"⥤","rhard":"⇁","rharu":"⇀","rharul":"⥬","Rho":"Ρ","rho":"ρ","rhov":"ϱ","RightAngleBracket":"⟩","RightArrowBar":"⇥","rightarrow":"→","RightArrow":"→","Rightarrow":"⇒","RightArrowLeftArrow":"⇄","rightarrowtail":"↣","RightCeiling":"⌉","RightDoubleBracket":"⟧","RightDownTeeVector":"⥝","RightDownVectorBar":"⥕","RightDownVector":"⇂","RightFloor":"⌋","rightharpoondown":"⇁","rightharpoonup":"⇀","rightleftarrows":"⇄","rightleftharpoons":"⇌","rightrightarrows":"⇉","rightsquigarrow":"↝","RightTeeArrow":"↦","RightTee":"⊢","RightTeeVector":"⥛","rightthreetimes":"⋌","RightTriangleBar":"⧐","RightTriangle":"⊳","RightTriangleEqual":"⊵","RightUpDownVector":"⥏","RightUpTeeVector":"⥜","RightUpVectorBar":"⥔","RightUpVector":"↾","RightVectorBar":"⥓","RightVector":"⇀","ring":"˚","risingdotseq":"≓","rlarr":"⇄","rlhar":"⇌","rlm":"‏","rmoustache":"⎱","rmoust":"⎱","rnmid":"⫮","roang":"⟭","roarr":"⇾","robrk":"⟧","ropar":"⦆","ropf":"𝕣","Ropf":"ℝ","roplus":"⨮","rotimes":"⨵","RoundImplies":"⥰","rpar":")","rpargt":"⦔","rppolint":"⨒","rrarr":"⇉","Rrightarrow":"⇛","rsaquo":"›","rscr":"𝓇","Rscr":"ℛ","rsh":"↱","Rsh":"↱","rsqb":"]","rsquo":"’","rsquor":"’","rthree":"⋌","rtimes":"⋊","rtri":"▹","rtrie":"⊵","rtrif":"▸","rtriltri":"⧎","RuleDelayed":"⧴","ruluhar":"⥨","rx":"℞","Sacute":"Ś","sacute":"ś","sbquo":"‚","scap":"⪸","Scaron":"Š","scaron":"š","Sc":"⪼","sc":"≻","sccue":"≽","sce":"⪰","scE":"⪴","Scedil":"Ş","scedil":"ş","Scirc":"Ŝ","scirc":"ŝ","scnap":"⪺","scnE":"⪶","scnsim":"⋩","scpolint":"⨓","scsim":"≿","Scy":"С","scy":"с","sdotb":"⊡","sdot":"⋅","sdote":"⩦","searhk":"⤥","searr":"↘","seArr":"⇘","searrow":"↘","sect":"§","semi":";","seswar":"⤩","setminus":"∖","setmn":"∖","sext":"✶","Sfr":"𝔖","sfr":"𝔰","sfrown":"⌢","sharp":"♯","SHCHcy":"Щ","shchcy":"щ","SHcy":"Ш","shcy":"ш","ShortDownArrow":"↓","ShortLeftArrow":"←","shortmid":"∣","shortparallel":"∥","ShortRightArrow":"→","ShortUpArrow":"↑","shy":"­","Sigma":"Σ","sigma":"σ","sigmaf":"ς","sigmav":"ς","sim":"∼","simdot":"⩪","sime":"≃","simeq":"≃","simg":"⪞","simgE":"⪠","siml":"⪝","simlE":"⪟","simne":"≆","simplus":"⨤","simrarr":"⥲","slarr":"←","SmallCircle":"∘","smallsetminus":"∖","smashp":"⨳","smeparsl":"⧤","smid":"∣","smile":"⌣","smt":"⪪","smte":"⪬","smtes":"⪬︀","SOFTcy":"Ь","softcy":"ь","solbar":"⌿","solb":"⧄","sol":"/","Sopf":"𝕊","sopf":"𝕤","spades":"♠","spadesuit":"♠","spar":"∥","sqcap":"⊓","sqcaps":"⊓︀","sqcup":"⊔","sqcups":"⊔︀","Sqrt":"√","sqsub":"⊏","sqsube":"⊑","sqsubset":"⊏","sqsubseteq":"⊑","sqsup":"⊐","sqsupe":"⊒","sqsupset":"⊐","sqsupseteq":"⊒","square":"□","Square":"□","SquareIntersection":"⊓","SquareSubset":"⊏","SquareSubsetEqual":"⊑","SquareSuperset":"⊐","SquareSupersetEqual":"⊒","SquareUnion":"⊔","squarf":"▪","squ":"□","squf":"▪","srarr":"→","Sscr":"𝒮","sscr":"𝓈","ssetmn":"∖","ssmile":"⌣","sstarf":"⋆","Star":"⋆","star":"☆","starf":"★","straightepsilon":"ϵ","straightphi":"ϕ","strns":"¯","sub":"⊂","Sub":"⋐","subdot":"⪽","subE":"⫅","sube":"⊆","subedot":"⫃","submult":"⫁","subnE":"⫋","subne":"⊊","subplus":"⪿","subrarr":"⥹","subset":"⊂","Subset":"⋐","subseteq":"⊆","subseteqq":"⫅","SubsetEqual":"⊆","subsetneq":"⊊","subsetneqq":"⫋","subsim":"⫇","subsub":"⫕","subsup":"⫓","succapprox":"⪸","succ":"≻","succcurlyeq":"≽","Succeeds":"≻","SucceedsEqual":"⪰","SucceedsSlantEqual":"≽","SucceedsTilde":"≿","succeq":"⪰","succnapprox":"⪺","succneqq":"⪶","succnsim":"⋩","succsim":"≿","SuchThat":"∋","sum":"∑","Sum":"∑","sung":"♪","sup1":"¹","sup2":"²","sup3":"³","sup":"⊃","Sup":"⋑","supdot":"⪾","supdsub":"⫘","supE":"⫆","supe":"⊇","supedot":"⫄","Superset":"⊃","SupersetEqual":"⊇","suphsol":"⟉","suphsub":"⫗","suplarr":"⥻","supmult":"⫂","supnE":"⫌","supne":"⊋","supplus":"⫀","supset":"⊃","Supset":"⋑","supseteq":"⊇","supseteqq":"⫆","supsetneq":"⊋","supsetneqq":"⫌","supsim":"⫈","supsub":"⫔","supsup":"⫖","swarhk":"⤦","swarr":"↙","swArr":"⇙","swarrow":"↙","swnwar":"⤪","szlig":"ß","Tab":"\t","target":"⌖","Tau":"Τ","tau":"τ","tbrk":"⎴","Tcaron":"Ť","tcaron":"ť","Tcedil":"Ţ","tcedil":"ţ","Tcy":"Т","tcy":"т","tdot":"⃛","telrec":"⌕","Tfr":"𝔗","tfr":"𝔱","there4":"∴","therefore":"∴","Therefore":"∴","Theta":"Θ","theta":"θ","thetasym":"ϑ","thetav":"ϑ","thickapprox":"≈","thicksim":"∼","ThickSpace":"  ","ThinSpace":" ","thinsp":" ","thkap":"≈","thksim":"∼","THORN":"Þ","thorn":"þ","tilde":"˜","Tilde":"∼","TildeEqual":"≃","TildeFullEqual":"≅","TildeTilde":"≈","timesbar":"⨱","timesb":"⊠","times":"×","timesd":"⨰","tint":"∭","toea":"⤨","topbot":"⌶","topcir":"⫱","top":"⊤","Topf":"𝕋","topf":"𝕥","topfork":"⫚","tosa":"⤩","tprime":"‴","trade":"™","TRADE":"™","triangle":"▵","triangledown":"▿","triangleleft":"◃","trianglelefteq":"⊴","triangleq":"≜","triangleright":"▹","trianglerighteq":"⊵","tridot":"◬","trie":"≜","triminus":"⨺","TripleDot":"⃛","triplus":"⨹","trisb":"⧍","tritime":"⨻","trpezium":"⏢","Tscr":"𝒯","tscr":"𝓉","TScy":"Ц","tscy":"ц","TSHcy":"Ћ","tshcy":"ћ","Tstrok":"Ŧ","tstrok":"ŧ","twixt":"≬","twoheadleftarrow":"↞","twoheadrightarrow":"↠","Uacute":"Ú","uacute":"ú","uarr":"↑","Uarr":"↟","uArr":"⇑","Uarrocir":"⥉","Ubrcy":"Ў","ubrcy":"ў","Ubreve":"Ŭ","ubreve":"ŭ","Ucirc":"Û","ucirc":"û","Ucy":"У","ucy":"у","udarr":"⇅","Udblac":"Ű","udblac":"ű","udhar":"⥮","ufisht":"⥾","Ufr":"𝔘","ufr":"𝔲","Ugrave":"Ù","ugrave":"ù","uHar":"⥣","uharl":"↿","uharr":"↾","uhblk":"▀","ulcorn":"⌜","ulcorner":"⌜","ulcrop":"⌏","ultri":"◸","Umacr":"Ū","umacr":"ū","uml":"¨","UnderBar":"_","UnderBrace":"⏟","UnderBracket":"⎵","UnderParenthesis":"⏝","Union":"⋃","UnionPlus":"⊎","Uogon":"Ų","uogon":"ų","Uopf":"𝕌","uopf":"𝕦","UpArrowBar":"⤒","uparrow":"↑","UpArrow":"↑","Uparrow":"⇑","UpArrowDownArrow":"⇅","updownarrow":"↕","UpDownArrow":"↕","Updownarrow":"⇕","UpEquilibrium":"⥮","upharpoonleft":"↿","upharpoonright":"↾","uplus":"⊎","UpperLeftArrow":"↖","UpperRightArrow":"↗","upsi":"υ","Upsi":"ϒ","upsih":"ϒ","Upsilon":"Υ","upsilon":"υ","UpTeeArrow":"↥","UpTee":"⊥","upuparrows":"⇈","urcorn":"⌝","urcorner":"⌝","urcrop":"⌎","Uring":"Ů","uring":"ů","urtri":"◹","Uscr":"𝒰","uscr":"𝓊","utdot":"⋰","Utilde":"Ũ","utilde":"ũ","utri":"▵","utrif":"▴","uuarr":"⇈","Uuml":"Ü","uuml":"ü","uwangle":"⦧","vangrt":"⦜","varepsilon":"ϵ","varkappa":"ϰ","varnothing":"∅","varphi":"ϕ","varpi":"ϖ","varpropto":"∝","varr":"↕","vArr":"⇕","varrho":"ϱ","varsigma":"ς","varsubsetneq":"⊊︀","varsubsetneqq":"⫋︀","varsupsetneq":"⊋︀","varsupsetneqq":"⫌︀","vartheta":"ϑ","vartriangleleft":"⊲","vartriangleright":"⊳","vBar":"⫨","Vbar":"⫫","vBarv":"⫩","Vcy":"В","vcy":"в","vdash":"⊢","vDash":"⊨","Vdash":"⊩","VDash":"⊫","Vdashl":"⫦","veebar":"⊻","vee":"∨","Vee":"⋁","veeeq":"≚","vellip":"⋮","verbar":"|","Verbar":"‖","vert":"|","Vert":"‖","VerticalBar":"∣","VerticalLine":"|","VerticalSeparator":"❘","VerticalTilde":"≀","VeryThinSpace":" ","Vfr":"𝔙","vfr":"𝔳","vltri":"⊲","vnsub":"⊂⃒","vnsup":"⊃⃒","Vopf":"𝕍","vopf":"𝕧","vprop":"∝","vrtri":"⊳","Vscr":"𝒱","vscr":"𝓋","vsubnE":"⫋︀","vsubne":"⊊︀","vsupnE":"⫌︀","vsupne":"⊋︀","Vvdash":"⊪","vzigzag":"⦚","Wcirc":"Ŵ","wcirc":"ŵ","wedbar":"⩟","wedge":"∧","Wedge":"⋀","wedgeq":"≙","weierp":"℘","Wfr":"𝔚","wfr":"𝔴","Wopf":"𝕎","wopf":"𝕨","wp":"℘","wr":"≀","wreath":"≀","Wscr":"𝒲","wscr":"𝓌","xcap":"⋂","xcirc":"◯","xcup":"⋃","xdtri":"▽","Xfr":"𝔛","xfr":"𝔵","xharr":"⟷","xhArr":"⟺","Xi":"Ξ","xi":"ξ","xlarr":"⟵","xlArr":"⟸","xmap":"⟼","xnis":"⋻","xodot":"⨀","Xopf":"𝕏","xopf":"𝕩","xoplus":"⨁","xotime":"⨂","xrarr":"⟶","xrArr":"⟹","Xscr":"𝒳","xscr":"𝓍","xsqcup":"⨆","xuplus":"⨄","xutri":"△","xvee":"⋁","xwedge":"⋀","Yacute":"Ý","yacute":"ý","YAcy":"Я","yacy":"я","Ycirc":"Ŷ","ycirc":"ŷ","Ycy":"Ы","ycy":"ы","yen":"¥","Yfr":"𝔜","yfr":"𝔶","YIcy":"Ї","yicy":"ї","Yopf":"𝕐","yopf":"𝕪","Yscr":"𝒴","yscr":"𝓎","YUcy":"Ю","yucy":"ю","yuml":"ÿ","Yuml":"Ÿ","Zacute":"Ź","zacute":"ź","Zcaron":"Ž","zcaron":"ž","Zcy":"З","zcy":"з","Zdot":"Ż","zdot":"ż","zeetrf":"ℨ","ZeroWidthSpace":"​","Zeta":"Ζ","zeta":"ζ","zfr":"𝔷","Zfr":"ℨ","ZHcy":"Ж","zhcy":"ж","zigrarr":"⇝","zopf":"𝕫","Zopf":"ℤ","Zscr":"𝒵","zscr":"𝓏","zwj":"‍","zwnj":"‌"};},{}],13:[function(dynamicRequire,module,exports){module.exports = {"Aacute":"Á","aacute":"á","Acirc":"Â","acirc":"â","acute":"´","AElig":"Æ","aelig":"æ","Agrave":"À","agrave":"à","amp":"&","AMP":"&","Aring":"Å","aring":"å","Atilde":"Ã","atilde":"ã","Auml":"Ä","auml":"ä","brvbar":"¦","Ccedil":"Ç","ccedil":"ç","cedil":"¸","cent":"¢","copy":"©","COPY":"©","curren":"¤","deg":"°","divide":"÷","Eacute":"É","eacute":"é","Ecirc":"Ê","ecirc":"ê","Egrave":"È","egrave":"è","ETH":"Ð","eth":"ð","Euml":"Ë","euml":"ë","frac12":"½","frac14":"¼","frac34":"¾","gt":">","GT":">","Iacute":"Í","iacute":"í","Icirc":"Î","icirc":"î","iexcl":"¡","Igrave":"Ì","igrave":"ì","iquest":"¿","Iuml":"Ï","iuml":"ï","laquo":"«","lt":"<","LT":"<","macr":"¯","micro":"µ","middot":"·","nbsp":" ","not":"¬","Ntilde":"Ñ","ntilde":"ñ","Oacute":"Ó","oacute":"ó","Ocirc":"Ô","ocirc":"ô","Ograve":"Ò","ograve":"ò","ordf":"ª","ordm":"º","Oslash":"Ø","oslash":"ø","Otilde":"Õ","otilde":"õ","Ouml":"Ö","ouml":"ö","para":"¶","plusmn":"±","pound":"£","quot":"\"","QUOT":"\"","raquo":"»","reg":"®","REG":"®","sect":"§","shy":"­","sup1":"¹","sup2":"²","sup3":"³","szlig":"ß","THORN":"Þ","thorn":"þ","times":"×","Uacute":"Ú","uacute":"ú","Ucirc":"Û","ucirc":"û","Ugrave":"Ù","ugrave":"ù","uml":"¨","Uuml":"Ü","uuml":"ü","Yacute":"Ý","yacute":"ý","yen":"¥","yuml":"ÿ"};},{}],14:[function(dynamicRequire,module,exports){module.exports = {"amp":"&","apos":"'","gt":">","lt":"<","quot":"\""};},{}],"fast-html-parser":[function(dynamicRequire,module,exports){dynamicRequire('apollojs');var entities=dynamicRequire('entities'); /**
   * Node Class as base class for TextNode and HTMLElement.
   */function Node(){}$declare(Node,{});$defenum(Node,{ELEMENT_NODE:1,TEXT_NODE:3}); /**
   * TextNode to contain a text element in DOM tree.
   * @param {string} value [description]
   */function TextNode(value){this.nodeValue = entities.decodeHTML5(value);this.nodeName = '#text';this.element = pools.uuid.get();}$inherit(TextNode,Node,Object.defineProperties({ /**
     * Node Type declaration.
     * @type {Number}
     */nodeType:Node.TEXT_NODE},{text:{ /**
     * Get unescaped text value of current node and its children.
     * @return {string} text content
     */get:function get(){return entities.decodeHTML5(this.rawText);},configurable:true,enumerable:true},isWhitespace:{ /**
     * Detect if the node contains only white space.
     * @return {bool}
     */get:function get(){return (/^(\s|&nbsp;)*$/.test(this.nodeValue));},configurable:true,enumerable:true}}));var kBlockElements={div:true,p:true, // ul: true,
// ol: true,
li:true, // table: true,
// tr: true,
td:true,section:true,br:true}; /**
   * HTMLElement, which contains a set of children.
   * Note: this is a minimalist implementation, no complete tree
   *   structure provided (no parentNode, nextSibling,
   *   previousSibling etc).
   * @param {string} name     nodeName
   * @param {Object} keyAttrs id and class attribute
   * @param {Object} rawAttrs attributes in string
   */function HTMLElement(name,keyAttrs,rawAttrs){this.nodeName = name;this.attributes = [];if(rawAttrs){var re=/\b([a-z][a-z0-9\-]*)\s*=\s*("([^"]+)"|'([^']+)'|(\S+))/ig;for(var match;match = re.exec(rawAttrs);) {var attr={};attr.name = match[1];attr.value = match[3] || match[4] || match[5];this.attributes.push(attr);}} // this.parentNode = null;
this.childNodes = [];this.element = pools.uuid.get();}$inherit(HTMLElement,Node,Object.defineProperties({ /**
     * Node Type declaration.
     * @type {Number}
     */nodeType:Node.ELEMENT_NODE, /**
     * Remove whitespaces in this sub tree.
     * @return {HTMLElement} pointer to this
     */removeWhitespace:function removeWhitespace(){var i=0,o=0;for(;i < this.childNodes.length;i++) {var node=this.childNodes[i];if(node.nodeType === Node.TEXT_NODE){if(node.isWhitespace)continue;node.nodeValue = node.nodeValue.trim();}else if(node.nodeType === Node.ELEMENT_NODE){node.removeWhitespace();}this.childNodes[o++] = node;}this.childNodes.length = o;return this;}},{text:{ /**
     * Get unescaped text value of current node and its children.
     * @return {string} text content
     */get:function get(){return entities.decodeHTML5(this.rawText);},configurable:true,enumerable:true},rawText:{ /**
     * Get escpaed (as-it) text value of current node and its children.
     * @return {string} text content
     */get:function get(){var res='';for(var i=0;i < this.childNodes.length;i++) res += this.childNodes[i].rawText;return res;},configurable:true,enumerable:true}}));$define(HTMLElement,{__wrap:function __wrap(el){el.childNodes.forEach(function(node){if(node.rawText){$wrap(node,TextNode);}else {$wrap(node,HTMLElement);}});}}); /**
   * Cache to store generated match functions
   * @type {Object}
   */var pMatchFunctionCache={}; /**
   * Matcher class to make CSS match
   * @param {string} selector Selector
   */function Matcher(selector){this.matchers = selector.split(' ').map(function(matcher){if(pMatchFunctionCache[matcher])return pMatchFunctionCache[matcher];var parts=matcher.split('.');var nodeName=parts[0];var classes=parts.slice(1).sort();var source='';if(nodeName && nodeName != '*'){if(nodeName[0] == '#')source += 'if (el.id != ' + JSON.stringify(nodeName.substr(1)) + ') return false;';else source += 'if (el.nodeName != ' + JSON.stringify(nodeName) + ') return false;';}if(classes.length > 0)source += 'for (var cls = ' + JSON.stringify(classes) + ', i = 0; i < cls.length; i++) if (el.classNames.indexOf(cls[i]) === -1) return false;';source += 'return true;';return pMatchFunctionCache[matcher] = new Function('el',source);});this.nextMatch = 0;}$declare(Matcher,Object.defineProperties({ /**
     * Trying to advance match pointer
     * @param  {HTMLElement} el element to make the match
     * @return {bool}           true when pointer advanced.
     */advance:function advance(el){if(this.nextMatch < this.matchers.length && this.matchers[this.nextMatch](el)){this.nextMatch++;return true;}return false;}, /**
     * Rewind the match pointer
     */rewind:function rewind(){this.nextMatch--;}, /**
     * Rest match pointer.
     * @return {[type]} [description]
     */reset:function reset(){this.nextMatch = 0;}},{matched:{ /**
     * Trying to determine if match made.
     * @return {bool} true when the match is made
     */get:function get(){return this.nextMatch == this.matchers.length;},configurable:true,enumerable:true}}));$define(Matcher,{ /**
     * flush cache to free memory
     */flushCache:function flushCache(){pMatchFunctionCache = {};}});var kMarkupPattern=/<!--[^]*?(?=-->)-->|<(\/?)([a-z][a-z0-9]*)\s*([^>]*?)(\/?)>/ig;var kAttributePattern=/\b(id|class)\s*=\s*("([^"]+)"|'([^']+)'|(\S+))/ig;var kSelfClosingElements={meta:true,img:true,link:true,input:true,area:true,br:true,hr:true};var kElementsClosedByOpening={li:{li:true},p:{p:true,div:true},td:{td:true,th:true},th:{td:true,th:true}};var kElementsClosedByClosing={li:{ul:true,ol:true},a:{div:true},b:{div:true},i:{div:true},p:{div:true},td:{tr:true,table:true},th:{tr:true,table:true}};var kBlockTextElements={script:true,noscript:true,style:true,pre:true}; /**
   * Parses HTML and returns a root element
   */module.exports = {Matcher:Matcher,Node:Node,HTMLElement:HTMLElement,TextNode:TextNode, /**
     * Parse a chuck of HTML source.
     * @param  {string} data      html
     * @return {HTMLElement}      root element
     */parse:function parse(data,options){var root=new HTMLElement(null,{});var currentParent=root;var stack=[root];var lastTextPos=-1;options = options || {};for(var match,text;match = kMarkupPattern.exec(data);) {if(lastTextPos > -1){if(lastTextPos + match[0].length < kMarkupPattern.lastIndex){ // if has content
text = data.substring(lastTextPos,kMarkupPattern.lastIndex - match[0].length);if(text.trim()){currentParent.childNodes.push({nodeName:'#text',element:pools.uuid.get(),nodeValue:entities.decodeHTML5(text)});}}}lastTextPos = kMarkupPattern.lastIndex;if(match[0][1] == '!'){ // this is a comment
continue;}if(options.lowerCaseTagName)match[2] = match[2].toLowerCase();if(!match[1]){ // not </ tags
var attrs={};for(var attMatch;attMatch = kAttributePattern.exec(match[3]);) attrs[attMatch[1]] = attMatch[3] || attMatch[4] || attMatch[5];if(!match[4] && kElementsClosedByOpening[currentParent.nodeName]){if(kElementsClosedByOpening[currentParent.nodeName][match[2]]){stack.pop();currentParent = stack.back;}}currentParent = currentParent.childNodes[currentParent.childNodes.push(new HTMLElement(match[2],attrs,match[3])) - 1];stack.push(currentParent);if(kBlockTextElements[match[2]]){ // a little test to find next </script> or </style> ...
var closeMarkup='</' + match[2] + '>';var index=data.indexOf(closeMarkup,kMarkupPattern.lastIndex);if(options[match[2]]){if(index == -1){ // there is no matching ending for the text element.
text = data.substr(kMarkupPattern.lastIndex);}else {text = data.substring(kMarkupPattern.lastIndex,index);}if(text.length > 0)currentParent.childNodes.push({nodeValue:entities.decodeHTML5(text),nodeName:'#text',element:pools.uuid.get()});}if(index == -1){lastTextPos = kMarkupPattern.lastIndex = data.length + 1;}else {lastTextPos = kMarkupPattern.lastIndex = index + closeMarkup.length;match[1] = true;}}}if(match[1] || match[4] || kSelfClosingElements[match[2]]){ // </ or /> or <br> etc.
while(true) {if(currentParent.nodeName == match[2]){stack.pop();currentParent = stack.back;break;}else { // Trying to close current tag, and move on
if(kElementsClosedByClosing[currentParent.nodeName]){if(kElementsClosedByClosing[currentParent.nodeName][match[2]]){stack.pop();currentParent = stack.back;continue;}} // Use aggressive strategy to handle unmatching markups.
break;}}}}return root;}};},{"apollojs":6,"entities":7}]},{},[])("fast-html-parser");});return g.htmlParser;};

}).call(this,typeof global !== "undefined" ? global : typeof self !== "undefined" ? self : typeof window !== "undefined" ? window : {})
},{"./pools":10}],10:[function(require,module,exports){
'use strict';

Object.defineProperty(exports, '__esModule', {
  value: true
});
exports.createPool = createPool;
exports.initializePools = initializePools;

var _uuid = require('./uuid');

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

},{"./uuid":11}],11:[function(require,module,exports){
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

},{}],12:[function(require,module,exports){
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

},{}]},{},[6])(6)
});