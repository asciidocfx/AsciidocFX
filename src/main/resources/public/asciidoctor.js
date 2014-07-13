(function (d) {
    var a = d.top, u = d.nil, t = d.slice, w = d.klass, r = d.module;
    (function (a, $super) {
        function r() {
        }

        var m = r = w(a, $super, "Set", r), q = m._proto, h = m._scope, n, b, c;
        q.hash = u;
        m.$include(h.Enumerable);
        d.defs(m, "$[]", function (a) {
            a = t.call(arguments, 0);
            return this.$new(a)
        });
        q.$initialize = n = function (a) {
            var b, c, g = n._p || u;
            null == a && (a = u);
            n._p = null;
            this.hash = h.Hash.$new();
            return(b = a["$nil?"]()) === u || b._isBoolean && !0 != b ? !1 !== g && g !== u ? (b = this.$do_with_enum, b._p = (c = function (a) {
                var b = c._s || this;
                null == a && (a = u);
                return b.$add(g["$[]"](a))
            },
                c._s = this, c), b).call(this, a) : this.$merge(a) : u
        };
        q["$=="] = function (a) {
            var b, c, g;
            return(b = this["$equal?"](a)) === u || b._isBoolean && !0 != b ? (b = a["$instance_of?"](this.$class())) === u || b._isBoolean && !0 != b ? (b = (c = a["$is_a?"](h.Set), !1 !== c && c !== u ? this.$size()["$=="](a.$size()) : c)) === u || b._isBoolean && !0 != b ? !1 : (b = (c = a)["$all?"], b._p = (g = function (a) {
                var b = g._s || this;
                null == b.hash && (b.hash = u);
                null == a && (a = u);
                return b.hash["$include?"](a)
            }, g._s = this, g), b).call(c) : this.hash["$=="](a.$instance_variable_get("@hash")) : !0
        };
        q.$add = function (a) {
            this.hash["$[]="](a, !0);
            return this
        };
        d.defn(m, "$<<", q.$add);
        q["$add?"] = function (a) {
            var b;
            return(b = this["$include?"](a)) === u || b._isBoolean && !0 != b ? this.$add(a) : u
        };
        q.$each = b = function () {
            var a, c, e = b._p || u;
            b._p = null;
            if (e === u)return this.$enum_for("each");
            (a = (c = this.hash).$each_key, a._p = e.$to_proc(), a).call(c);
            return this
        };
        q["$empty?"] = function () {
            return this.hash["$empty?"]()
        };
        q.$clear = function () {
            this.hash.$clear();
            return this
        };
        q["$include?"] = function (a) {
            return this.hash["$include?"](a)
        };
        d.defn(m, "$member?", q["$include?"]);
        q.$merge = function (a) {
            var b, c;
            (b = this.$do_with_enum, b._p = (c = function (a) {
                var b = c._s || this;
                null == a && (a = u);
                return b.$add(a)
            }, c._s = this, c), b).call(this, a);
            return this
        };
        q.$do_with_enum = c = function (a) {
            var b, e = c._p || u;
            c._p = null;
            return(b = a.$each, b._p = e.$to_proc(), b).call(a)
        };
        q.$size = function () {
            return this.hash.$size()
        };
        d.defn(m, "$length", q.$size);
        return(q.$to_a = function () {
            return this.hash.$keys()
        }, u) && "to_a"
    })(a, null);
    return function (a) {
        a = r(a, "Enumerable");
        var x = a._scope,
            A;
        a._proto.$to_set = A = function (a, d) {
            var h, n, b = A._p || u;
            d = t.call(arguments, 1);
            null == a && (a = x.Set);
            A._p = null;
            return(h = (n = a).$new, h._p = b.$to_proc(), h).apply(n, [this].concat(d))
        };
        d.donate(a, ["$to_set"])
    }(a)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module;
    return function (t) {
        t = u(t, "Comparable");
        var w = t._proto, r = t._scope;
        d.defs(t, "$normalize", function (d) {
            var t;
            return(t = r.Integer["$==="](d)) === a || t._isBoolean && !0 != t ? d["$>"](0) ? 1 : d["$<"](0) ? -1 : 0 : d
        });
        w["$=="] = function (v) {
            var t, A = a;
            try {
                return(t = this["$equal?"](v)) === a || t._isBoolean && !0 != t ? (t = A = this["$<=>"](v)) === a || t._isBoolean && !0 != t ? !1 : r.Comparable.$normalize(A)["$=="](0) : !0
            } catch (m) {
                if (d.$rescue(m, [r.StandardError]))return!1;
                throw m;
            }
        };
        w["$>"] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " + d.$class() + " failed");
            return r.Comparable.$normalize(A)["$>"](0)
        };
        w["$>="] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " + d.$class() + " failed");
            return r.Comparable.$normalize(A)["$>="](0)
        };
        w["$<"] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError,
                "comparison of " + this.$class() + " with " + d.$class() + " failed");
            return r.Comparable.$normalize(A)["$<"](0)
        };
        w["$<="] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " + d.$class() + " failed");
            return r.Comparable.$normalize(A)["$<="](0)
        };
        w["$between?"] = function (a, d) {
            return this["$<"](a) || this["$>"](d) ? !1 : !0
        };
        d.donate(t, "$== $> $>= $< $<= $between?".split(" "))
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.breaker, t = d.slice, w = d.klass, r = d.gvars;
    (function (v, $super) {
        function A() {
        }

        var m = A = w(v, $super, "String", A), q = m._proto, h = m._scope, n, b, c, f, p, e, g;
        q.length = a;
        m.$include(h.Comparable);
        q._isString = !0;
        d.defs(m, "$try_convert", function (b) {
            try {
                return b.$to_str()
            } catch (c) {
                return a
            }
        });
        d.defs(m, "$new", function (a) {
            null == a && (a = "");
            return new String(a)
        });
        q["$%"] = function (b) {
            var c;
            return(c = h.Array["$==="](b)) === a || c._isBoolean && !0 != c ? this.$format(this, b) : (c = this).$format.apply(c, [this].concat(b))
        };
        q["$*"] = function (a) {
            if (1 > a)return"";
            for (var b = "", c = this; 0 < a;)a & 1 && (b += c), a >>= 1, c += c;
            return b
        };
        q["$+"] = function (a) {
            a = h.Opal.$coerce_to(a, h.String, "to_str");
            return this + a.$to_s()
        };
        q["$<=>"] = function (b) {
            var c;
            if ((c = b["$respond_to?"]("to_str")) === a || c._isBoolean && !0 != c)return b = b["$<=>"](this), b === a ? a : 0 < b ? -1 : 0 > b ? 1 : 0;
            b = b.$to_str().$to_s();
            return this > b ? 1 : this < b ? -1 : 0
        };
        q["$=="] = function (b) {
            var c;
            return(c = h.String["$==="](b)) === a || c._isBoolean && !0 != c ? !1 : this.$to_s() == b.$to_s()
        };
        d.defn(m, "$eql?", q["$=="]);
        d.defn(m, "$===", q["$=="]);
        q["$=~"] = function (a) {
            a._isString && this.$raise(h.TypeError, "type mismatch: String given");
            return a["$=~"](this)
        };
        q["$[]"] = function (b, c) {
            var e = this.length;
            if (b._isRange) {
                var f = b.exclude;
                c = b.end;
                b = b.begin;
                0 > b && (b += e);
                0 > c && (c += e);
                f || (c += 1);
                if (b > e)return a;
                c -= b;
                0 > c && (c = 0);
                return this.substr(b, c)
            }
            0 > b && (b += this.length);
            return null == c ? b >= this.length || 0 > b ? a : this.substr(b, 1) : b > this.length || 0 > b ? a : this.substr(b, c)
        };
        q.$capitalize = function () {
            return this.charAt(0).toUpperCase() + this.substr(1).toLowerCase()
        };
        q.$casecmp = function (a) {
            a = h.Opal.$coerce_to(a, h.String, "to_str").$to_s();
            return this.toLowerCase()["$<=>"](a.toLowerCase())
        };
        q.$center = function (b, c) {
            var e;
            null == c && (c = " ");
            b = h.Opal.$coerce_to(b, h.Integer, "to_int");
            c = h.Opal.$coerce_to(c, h.String, "to_str").$to_s();
            (e = c["$empty?"]()) === a || e._isBoolean && !0 != e || this.$raise(h.ArgumentError, "zero width padding");
            if ((e = b <= this.length) !== a && (!e._isBoolean || !0 == e))return this;
            e = this.$ljust(b["$+"](this.length)["$/"](2).$ceil(), c);
            return this.$rjust(b["$+"](this.length)["$/"](2).$floor(),
                c) + e.slice(this.length)
        };
        q.$chars = n = function () {
            var b, c = n._p || a;
            n._p = null;
            return!1 === c || c === a ? this.$each_char().$to_a() : (b = this.$each_char, b._p = c.$to_proc(), b).call(this)
        };
        q.$chomp = function (b) {
            var c;
            null == r["/"] && (r["/"] = a);
            null == b && (b = r["/"]);
            if ((c = b === a || 0 === this.length) !== a && (!c._isBoolean || !0 == c))return this;
            b = h.Opal["$coerce_to!"](b, h.String, "to_str").$to_s();
            return"\n" === b ? this.replace(/\r?\n?$/, "") : "" === b ? this.replace(/(\r?\n)+$/, "") : this.length > b.length && this.substr(this.length - b.length, b.length) ===
                b ? this.substr(0, this.length - b.length) : this
        };
        q.$chop = function () {
            var a = this.length;
            return 1 >= a ? "" : "\n" === this.charAt(a - 1) && "\r" === this.charAt(a - 2) ? this.substr(0, a - 2) : this.substr(0, a - 1)
        };
        q.$chr = function () {
            return this.charAt(0)
        };
        q.$clone = function () {
            var b = a, b = this.slice();
            b.$initialize_clone(this);
            return b
        };
        q.$dup = function () {
            var b = a, b = this.slice();
            b.$initialize_dup(this);
            return b
        };
        q.$count = function (a) {
            return(this.length - this.replace(new RegExp(a, "g"), "").length) / a.length
        };
        d.defn(m, "$dup", q.$clone);
        q.$downcase =
            function () {
                return this.toLowerCase()
            };
        q.$each_char = b = function () {
            var c, k = b._p || a;
            b._p = null;
            if (k === a)return this.$enum_for("each_char");
            for (var e = 0, f = this.length; e < f; e++)(c = d.$yield1(k, this.charAt(e))) === u ? u.$v : c;
            return this
        };
        q.$each_line = c = function (b) {
            var k, e = c._p || a;
            null == r["/"] && (r["/"] = a);
            null == b && (b = r["/"]);
            c._p = null;
            if (e === a)return this.$split(b);
            for (var f = this.$chomp(), g = this.length != f.length, f = f.split(b), p = 0, n = f.length; p < n; p++)p < n - 1 || g ? (k = d.$yield1(e, f[p] + b)) === u ? u.$v : k : (k = d.$yield1(e, f[p])) ===
                u ? u.$v : k;
            return this
        };
        q["$empty?"] = function () {
            return 0 === this.length
        };
        q["$end_with?"] = function (a) {
            a = t.call(arguments, 0);
            for (var b = 0, c = a.length; b < c; b++) {
                var e = h.Opal.$coerce_to(a[b], h.String, "to_str").$to_s();
                if (this.length >= e.length && this.substr(this.length - e.length, e.length) == e)return!0
            }
            return!1
        };
        d.defn(m, "$eql?", q["$=="]);
        d.defn(m, "$equal?", q["$==="]);
        q.$gsub = f = function (b, c) {
            var e, g, p = f._p || a;
            f._p = null;
            (e = !1 !== (g = h.String["$==="](b)) && g !== a ? g : b["$respond_to?"]("to_str")) === a || e._isBoolean && !0 !=
                e || (b = new RegExp("" + h.Regexp.$escape(b.$to_str())));
            ((e = h.Regexp["$==="](b)) === a || e._isBoolean && !0 != e) && this.$raise(h.TypeError, "wrong argument type " + b.$class() + " (expected Regexp)");
            b = b.toString();
            e = b.substr(b.lastIndexOf("/") + 1) + "g";
            g = b.substr(1, b.lastIndexOf("/") - 1);
            this.$sub._p = p;
            return this.$sub(new RegExp(g, e), c)
        };
        q.$hash = function () {
            return this.toString()
        };
        q.$hex = function () {
            return this.$to_i(16)
        };
        q["$include?"] = function (b) {
            var c;
            if (b._isString)return-1 !== this.indexOf(b);
            ((c = b["$respond_to?"]("to_str")) ===
                a || c._isBoolean && !0 != c) && this.$raise(h.TypeError, "no implicit conversion of " + b.$class().$name() + " into String");
            return-1 !== this.indexOf(b.$to_str())
        };
        q.$index = function (b, c) {
            var e, f = a;
            null == c && (c = a);
            (e = h.String["$==="](b)) === a || e._isBoolean && !0 != e ? (e = b["$respond_to?"]("to_str")) === a || e._isBoolean && !0 != e ? (e = h.Regexp["$==="](b)["$!"]()) === a || e._isBoolean && !0 != e || this.$raise(h.TypeError, "type mismatch: " + b.$class() + " given") : b = b.$to_str().$to_s() : b = b.$to_s();
            if (!1 !== c && c !== a) {
                c = h.Opal.$coerce_to(c, h.Integer,
                    "to_int");
                f = this.length;
                0 > c && (c += f);
                if (c > f)return a;
                f = (e = h.Regexp["$==="](b)) === a || e._isBoolean && !0 != e ? this.substr(c).indexOf(b) : !1 !== (e = b["$=~"](this.substr(c))) && e !== a ? e : -1;
                -1 !== f && (f += c)
            } else f = (e = h.Regexp["$==="](b)) === a || e._isBoolean && !0 != e ? this.indexOf(b) : !1 !== (e = b["$=~"](this)) && e !== a ? e : -1;
            return(e = -1 === f) === a || e._isBoolean && !0 != e ? f : a
        };
        q.$inspect = function () {
            var a = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, b = {"\b": "\\b",
                "\t": "\\t", "\n": "\\n", "\f": "\\f", "\r": "\\r", '"': '\\"', "\\": "\\\\"};
            a.lastIndex = 0;
            return a.test(this) ? '"' + this.replace(a, function (a) {
                var c = b[a];
                return"string" === typeof c ? c : "\\u" + ("0000" + a.charCodeAt(0).toString(16)).slice(-4)
            }) + '"' : '"' + this + '"'
        };
        q.$intern = function () {
            return this
        };
        q.$lines = function (b) {
            null == r["/"] && (r["/"] = a);
            null == b && (b = r["/"]);
            return this.$each_line(b).$to_a()
        };
        q.$length = function () {
            return this.length
        };
        q.$ljust = function (b, c) {
            var e;
            null == c && (c = " ");
            b = h.Opal.$coerce_to(b, h.Integer, "to_int");
            c = h.Opal.$coerce_to(c, h.String, "to_str").$to_s();
            (e = c["$empty?"]()) === a || e._isBoolean && !0 != e || this.$raise(h.ArgumentError, "zero width padding");
            if ((e = b <= this.length) !== a && (!e._isBoolean || !0 == e))return this;
            e = -1;
            var f = "";
            for (b -= this.length; ++e < b;)f += c;
            return this + f.slice(0, b)
        };
        q.$lstrip = function () {
            return this.replace(/^\s*/, "")
        };
        q.$match = p = function (b, c) {
            var e, f, g = p._p || a;
            p._p = null;
            (e = !1 !== (f = h.String["$==="](b)) && f !== a ? f : b["$respond_to?"]("to_str")) === a || e._isBoolean && !0 != e || (b = new RegExp("" + h.Regexp.$escape(b.$to_str())));
            ((e = h.Regexp["$==="](b)) === a || e._isBoolean && !0 != e) && this.$raise(h.TypeError, "wrong argument type " + b.$class() + " (expected Regexp)");
            return(e = (f = b).$match, e._p = g.$to_proc(), e).call(f, this, c)
        };
        q.$next = function () {
            if (0 === this.length)return"";
            var a = this.substr(0, this.length - 1), b = String.fromCharCode(this.charCodeAt(this.length - 1) + 1);
            return a + b
        };
        q.$ord = function () {
            return this.charCodeAt(0)
        };
        q.$partition = function (a) {
            var b = this.split(a);
            return[b[0], b[0].length === this.length ? "" : a, b.slice(1).join(a.toString())]
        };
        q.$reverse = function () {
            return this.split("").reverse().join("")
        };
        q.$rindex = function (b, c) {
            var e = null == b ? Opal.NilClass : b.constructor;
            e != String && e != RegExp && this.$raise(h.TypeError.$new("type mismatch: " + e + " given"));
            if (0 == this.length)return 0 == b.length ? 0 : a;
            var f = -1;
            null != c ? (0 > c && (c = this.length + c), e == String ? f = this.lastIndexOf(b, c) : (f = this.substr(0, c + 1).$reverse().search(b), -1 !== f && (f = c - f))) : e == String ? f = this.lastIndexOf(b) : (f = this.$reverse().search(b), -1 !== f && (f = this.length - 1 - f));
            return-1 === f ? a : f
        };
        q.$rjust =
            function (b, c) {
                var e;
                null == c && (c = " ");
                b = h.Opal.$coerce_to(b, h.Integer, "to_int");
                c = h.Opal.$coerce_to(c, h.String, "to_str").$to_s();
                (e = c["$empty?"]()) === a || e._isBoolean && !0 != e || this.$raise(h.ArgumentError, "zero width padding");
                if ((e = b <= this.length) !== a && (!e._isBoolean || !0 == e))return this;
                e = Math.floor(b - this.length);
                var f = Array(Math.floor(e / c.length) + 1).join(c);
                return f + c.slice(0, e - f.length) + this
            };
        q.$rstrip = function () {
            return this.replace(/\s*$/, "")
        };
        q.$scan = e = function (b) {
            var c = e._p || a;
            e._p = null;
            b.global ?
                b.lastIndex = 0 : b = new RegExp(b.source, "g" + (b.multiline ? "m" : "") + (b.ignoreCase ? "i" : ""));
            for (var f = [], g; null != (g = b.exec(this));)h.MatchData.$new(b, g), c === a ? 1 == g.length ? f.push(g[0]) : f.push(g.slice(1)) : 1 == g.length ? c(g[0]) : c.apply(this, g.slice(1));
            return c !== a ? this : f
        };
        d.defn(m, "$size", q.$length);
        d.defn(m, "$slice", q["$[]"]);
        q.$split = function (b, c) {
            var e;
            null == r[";"] && (r[";"] = a);
            null == b && (b = !1 !== (e = r[";"]) && e !== a ? e : " ");
            if (b === a || void 0 === b)b = r[";"];
            e = [];
            void 0 !== c && (c = h.Opal["$coerce_to!"](c, h.Integer, "to_int"));
            if (0 === this.length)return[];
            if (1 === c)return[this];
            if (b && b._isRegexp) {
                var f = b.toString(), g = "/^/" == f.substr(0, 3) || "/(?:)/" == f.substr(0, 6);
                if (void 0 === c || 0 === c)e = this.split(g ? /(?:)/ : b); else {
                    b.global || (b = eval(f + "g"));
                    var p = 0;
                    for (b.lastIndex = 0; null !== (f = b.exec(this));) {
                        p = this.slice(p, f.index);
                        e.push(p);
                        p = b.lastIndex;
                        if (0 === f[0].length) {
                            g && (b = /(?:)/);
                            e = this.split(b);
                            void 0 !== c && 0 > c && g && e.push("");
                            p = void 0;
                            break
                        }
                        if (void 0 !== c && 1 < c && e.length + 1 == c)break
                    }
                    void 0 !== p && e.push(this.slice(p, this.length))
                }
            } else {
                f =
                    g = 0;
                b = b === a || void 0 === b ? " " : h.Opal.$try_convert(b, h.String, "to_str").$to_s();
                for (var p = " " == b ? this.replace(/[\r\n\t\v]\s+/g, " ") : this, d = -1; -1 < (d = p.indexOf(b, f)) && d < p.length && g + 1 !== c;)" " == b && d == f ? f = d + 1 : (e.push(p.substr(f, b.length ? d - f : 1)), g++, f = d + (b.length ? b.length : 1));
                0 < p.length && (0 > c || p.length > f) && (p.length == f ? e.push("") : e.push(p.substr(f, p.length)))
            }
            if (void 0 === c || 0 === c)for (; "" === e[e.length - 1];)e.length -= 1;
            0 < c && (g = e.slice(c - 1).join(""), e.splice(c - 1, e.length - 1, g));
            return e
        };
        q.$squeeze = function (a) {
            a =
                t.call(arguments, 0);
            if (0 === a.length)return this.replace(/(.)\1+/g, "$1");
            for (var b = h.Opal.$coerce_to(a[0], h.String, "to_str").$chars(), c = 1, e = a.length; c < e; c++)b = b["$&"](h.Opal.$coerce_to(a[c], h.String, "to_str").$chars());
            return 0 === b.length ? this : this.replace(new RegExp("([" + h.Regexp.$escape(b.$join()) + "])\\1+", "g"), "$1")
        };
        q["$start_with?"] = function (a) {
            a = t.call(arguments, 0);
            for (var b = 0, c = a.length; b < c; b++) {
                var e = h.Opal.$coerce_to(a[b], h.String, "to_str").$to_s();
                if (0 === this.indexOf(e))return!0
            }
            return!1
        };
        q.$strip = function () {
            return this.replace(/^\s*/, "").replace(/\s*$/, "")
        };
        q.$sub = g = function (b, c) {
            var e = this, f = g._p || a;
            g._p = null;
            if ("string" === typeof c)return c = c.replace(/\\([1-9])/g, "$$$1"), e.replace(b, c);
            if (f !== a)return e.replace(b, function () {
                for (var b = [], c = 0, e = arguments.length; c < e; c++) {
                    var k = arguments[c];
                    void 0 == k ? b.push(a) : b.push(k)
                }
                b.pop();
                b.pop();
                r["&"] = b[0];
                r["~"] = b;
                return f(b[0])
            });
            if (void 0 !== c) {
                if (c["$is_a?"](h.Hash))return e.replace(b, function (b) {
                    return null == c["$[]"](e.$str()) ? a : e.$value().$to_s()
                });
                c = h.String.$try_convert(c);
                null == c && e.$raise(h.TypeError, "can't convert " + c.$class() + " into String");
                return e.replace(b, c)
            }
            c = c.toString().replace(/\\([1-9])/g, "$$$1");
            return e.replace(b, c)
        };
        d.defn(m, "$succ", q.$next);
        q.$sum = function (a) {
            null == a && (a = 16);
            for (var b = 0, c = 0, e = this.length; c < e; c++)b += this.charCodeAt(c) % ((1 << a) - 1);
            return b
        };
        q.$swapcase = function () {
            var a = this.replace(/([a-z]+)|([A-Z]+)/g, function (a, b, c) {
                return b ? a.toUpperCase() : a.toLowerCase()
            });
            return this.constructor === String ? a : this.$class().$new(a)
        };
        q.$to_f = function () {
            if ("_" === this.charAt(0))return 0;
            var a = parseFloat(this.replace(/_/g, ""));
            return isNaN(a) || Infinity == a || -Infinity == a ? 0 : a
        };
        q.$to_i = function (a) {
            null == a && (a = 10);
            a = parseInt(this, a);
            return isNaN(a) ? 0 : a
        };
        q.$to_proc = function () {
            var b, c;
            return(b = this.$proc, b._p = (c = function (b, e) {
                var f = c._s || this, g;
                null == b && (b = a);
                e = t.call(arguments, 1);
                return(g = b).$send.apply(g, [f].concat(e))
            }, c._s = this, c), b).call(this)
        };
        q.$to_s = function () {
            return this.toString()
        };
        d.defn(m, "$to_str", q.$to_s);
        d.defn(m, "$to_sym",
            q.$intern);
        q.$tr = function (a, b) {
            if (0 == a.length || a === b)return this;
            var c = {}, e = a.split(""), f = e.length, g = b.split(""), p = g.length, d = !1, n = null;
            "^" === e[0] && (d = !0, e.shift(), n = g[p - 1], f -= 1);
            for (var h = [], m = null, q = !1, B = 0; B < f; B++) {
                var r = e[B];
                if (null == m)m = r, h.push(r); else if ("-" === r)"-" === m ? (h.push("-"), h.push("-")) : B == f - 1 ? h.push("-") : q = !0; else if (q) {
                    q = m.charCodeAt(0) + 1;
                    for (m = r.charCodeAt(0); q < m; q++)h.push(String.fromCharCode(q));
                    h.push(r);
                    m = q = null
                } else h.push(r)
            }
            e = h;
            f = e.length;
            if (d)for (B = 0; B < f; B++)c[e[B]] = !0; else {
                if (0 <
                    p) {
                    h = [];
                    q = !1;
                    for (B = 0; B < p; B++)if (r = g[B], null == m)m = r, h.push(r); else if ("-" === r)B == p - 1 ? h.push("-") : q = !0; else if (q) {
                        q = m.charCodeAt(0) + 1;
                        for (m = r.charCodeAt(0); q < m; q++)h.push(String.fromCharCode(q));
                        h.push(r);
                        m = q = null
                    } else h.push(r);
                    g = h;
                    p = g.length
                }
                r = f - p;
                if (0 < r)for (p = 0 < p ? g[p - 1] : "", B = 0; B < r; B++)g.push(p);
                for (B = 0; B < f; B++)c[e[B]] = g[B]
            }
            e = "";
            B = 0;
            for (f = this.length; B < f; B++)r = this.charAt(B), g = c[r], e = d ? e + (null == g ? n : r) : e + (null != g ? g : r);
            return e
        };
        q.$tr_s = function (a, b) {
            if (0 == a.length)return this;
            var c = {}, e = a.split(""),
                f = e.length, g = b.split(""), p = g.length, d = !1, n = null;
            "^" === e[0] && (d = !0, e.shift(), n = g[p - 1], f -= 1);
            for (var h = [], m = null, q = !1, r = 0; r < f; r++) {
                var v = e[r];
                if (null == m)m = v, h.push(v); else if ("-" === v)"-" === m ? (h.push("-"), h.push("-")) : r == f - 1 ? h.push("-") : q = !0; else if (q) {
                    q = m.charCodeAt(0) + 1;
                    for (m = v.charCodeAt(0); q < m; q++)h.push(String.fromCharCode(q));
                    h.push(v);
                    m = q = null
                } else h.push(v)
            }
            e = h;
            f = e.length;
            if (d)for (r = 0; r < f; r++)c[e[r]] = !0; else {
                if (0 < p) {
                    h = [];
                    q = !1;
                    for (r = 0; r < p; r++)if (v = g[r], null == m)m = v, h.push(v); else if ("-" === v)r ==
                        p - 1 ? h.push("-") : q = !0; else if (q) {
                        q = m.charCodeAt(0) + 1;
                        for (m = v.charCodeAt(0); q < m; q++)h.push(String.fromCharCode(q));
                        h.push(v);
                        m = q = null
                    } else h.push(v);
                    g = h;
                    p = g.length
                }
                v = f - p;
                if (0 < v)for (p = 0 < p ? g[p - 1] : "", r = 0; r < v; r++)g.push(p);
                for (r = 0; r < f; r++)c[e[r]] = g[r]
            }
            e = "";
            f = null;
            r = 0;
            for (g = this.length; r < g; r++)if (v = this.charAt(r), p = c[v], d)null == p ? null == f && (e += n, f = !0) : (e += v, f = null); else if (null != p) {
                if (null == f || f !== p)e += p, f = p
            } else e += v, f = null;
            return e
        };
        q.$upcase = function () {
            return this.toUpperCase()
        };
        q.$freeze = function () {
            return this
        };
        return(q["$frozen?"] = function () {
            return!0
        }, a) && "frozen?"
    })(d.top, null);
    return d.cdecl(d, "Symbol", d.String)
})(Opal);
(function (d) {
    var a, u, t, w, r, v, x, A = d.top, m = d.nil, q = d.breaker, h = d.klass, n = d.hash2;
    (function (a, $super) {
        function f() {
        }

        var p = f = h(a, $super, "Encoding", f), e = p._proto, g = p._scope, l;
        e.ascii = e.dummy = e.name = m;
        d.defs(p, "$register", l = function (a, b) {
            var e, f, p, d, h = l._p || m, q = m, M = m;
            null == b && (b = n([], {}));
            l._p = null;
            q = [a]["$+"](!1 !== (e = b["$[]"]("aliases")) && e !== m ? e : []);
            M = (e = (f = g.Class).$new, e._p = h.$to_proc(), e).call(f, this).$new(a, q, !1 !== (e = b["$[]"]("ascii")) && e !== m ? e : !1, !1 !== (e = b["$[]"]("dummy")) && e !== m ? e : !1);
            return(e =
                (p = q).$each, e._p = (d = function (a) {
                var b = d._s || this;
                null == a && (a = m);
                return b.$const_set(a.$sub("-", "_"), M)
            }, d._s = this, d), e).call(p)
        });
        d.defs(p, "$find", function (a) {
            try {
                var b, e, f;
                if ((b = this["$==="](a)) !== m && (!b._isBoolean || !0 == b))return a;
                (b = (e = this.$constants()).$each, b._p = (f = function (b) {
                    var e = f._s || this, g, l, p = m;
                    null == b && (b = m);
                    p = e.$const_get(b);
                    if ((g = !1 !== (l = p.$name()["$=="](a)) && l !== m ? l : p.$names()["$include?"](a)) === m || g._isBoolean && !0 != g)return m;
                    d.$return(p)
                }, f._s = this, f), b).call(e);
                return this.$raise(g.ArgumentError,
                    "unknown encoding name - " + a)
            } catch (l) {
                if (l === d.returner)return l.$v;
                throw l;
            }
        });
        (function (a) {
            return a.$attr_accessor("default_external")
        })(p.$singleton_class());
        p.$attr_reader("name", "names");
        e.$initialize = function (a, b, e, f) {
            this.name = a;
            this.names = b;
            this.ascii = e;
            return this.dummy = f
        };
        e["$ascii_compatible?"] = function () {
            return this.ascii
        };
        e["$dummy?"] = function () {
            return this.dummy
        };
        e.$to_s = function () {
            return this.name
        };
        e.$inspect = function () {
            var a;
            return"#<Encoding:" + this.name + ((a = this.dummy) === m || a._isBoolean &&
                !0 != a ? m : " (dummy)") + ">"
        };
        e.$each_byte = function () {
            return this.$raise(g.NotImplementedError)
        };
        e.$getbyte = function () {
            return this.$raise(g.NotImplementedError)
        };
        return(e.$bytesize = function () {
            return this.$raise(g.NotImplementedError)
        }, m) && "bytesize"
    })(A, null);
    (a = (u = d.Encoding).$register, a._p = (t = function () {
        var a = t._s || this, c;
        d.defn(a, "$each_byte", c = function (a) {
            var b, e = c._p || m;
            c._p = null;
            for (var g = 0, l = a.length; g < l; g++) {
                var k = a.charCodeAt(g);
                if (127 >= k)(b = d.$yield1(e, k)) === q ? q.$v : b; else for (var k = encodeURIComponent(a.charAt(g)).substr(1).split("%"),
                                                                                   s = 0, n = k.length; s < n; s++)(b = d.$yield1(e, parseInt(k[s], 16))) === q ? q.$v : b
            }
        });
        return(d.defn(a, "$bytesize", function () {
            return this.$bytes().$length()
        }), m) && "bytesize"
    }, t._s = A, t), a).call(u, "UTF-8", n(["aliases", "ascii"], {aliases: ["CP65001"], ascii: !0}));
    (a = (w = d.Encoding).$register, a._p = (r = function () {
        var a = r._s || this, c;
        d.defn(a, "$each_byte", c = function (a) {
            var b, e = c._p || m;
            c._p = null;
            for (var g = 0, l = a.length; g < l; g++) {
                var k = a.charCodeAt(g);
                (b = d.$yield1(e, k & 255)) === q ? q.$v : b;
                (b = d.$yield1(e, k >> 8)) === q ? q.$v : b
            }
        });
        return(d.defn(a,
            "$bytesize", function () {
                return this.$bytes().$length()
            }), m) && "bytesize"
    }, r._s = A, r), a).call(w, "UTF-16LE");
    (a = (v = d.Encoding).$register, a._p = (x = function () {
        var a = x._s || this, c;
        d.defn(a, "$each_byte", c = function (a) {
            var b, e = c._p || m;
            c._p = null;
            for (var g = 0, l = a.length; g < l; g++)(b = d.$yield1(e, a.charCodeAt(g) & 255)) === q ? q.$v : b
        });
        return(d.defn(a, "$bytesize", function () {
            return this.$bytes().$length()
        }), m) && "bytesize"
    }, x._s = A, x), a).call(v, "ASCII-8BIT", n(["aliases", "ascii"], {aliases: ["BINARY"], ascii: !0}));
    return function (a, $super) {
        function f() {
        }

        var p = f = h(a, $super, "String", f), e = p._proto, g = p._scope, l;
        e.encoding = m;
        e.encoding = g.Encoding._scope.UTF_16LE;
        e.$bytes = function () {
            return this.$each_byte().$to_a()
        };
        e.$bytesize = function () {
            return this.encoding.$bytesize(this)
        };
        e.$each_byte = l = function () {
            var a, b, e = l._p || m;
            l._p = null;
            if (e === m)return this.$enum_for("each_byte");
            (a = (b = this.encoding).$each_byte, a._p = e.$to_proc(), a).call(b, this);
            return this
        };
        e.$encoding = function () {
            return this.encoding
        };
        e.$force_encoding = function (a) {
            a = g.Encoding.$find(a);
            if (a["$=="](this.encoding))return this;
            var b = new String(this);
            b.encoding = a;
            return b
        };
        return(e.$getbyte = function (a) {
            return this.encoding.$getbyte(this, a)
        }, m) && "getbyte"
    }(A, null)
})(Opal);
(function (d) {
    var a = d.nil, u = d.klass;
    return function (t, $super) {
        function r() {
        }

        var v = r = u(t, $super, "StringScanner", r), x = v._proto;
        x.pos = x.string = x.working = x.prev_pos = x.matched = x.match = a;
        v.$attr_reader("pos");
        v.$attr_reader("matched");
        x.$initialize = function (d) {
            this.string = d;
            this.pos = 0;
            this.matched = a;
            this.working = d;
            return this.match = []
        };
        x["$bol?"] = function () {
            return 0 === this.pos || "\n" === this.string.charAt(this.pos - 1)
        };
        x.$scan = function (d) {
            d = new RegExp("^" + d.toString().substring(1, d.toString().length - 1));
            d =
                d.exec(this.working);
            return null == d ? this.matched = a : "object" === typeof d ? (this.prev_pos = this.pos, this.pos += d[0].length, this.working = this.working.substring(d[0].length), this.matched = d[0], this.match = d, d[0]) : "string" === typeof d ? (this.pos += d.length, this.working = this.working.substring(d.length), d) : a
        };
        x["$[]"] = function (d) {
            var m = this.match;
            0 > d && (d += m.length);
            return 0 > d || d >= m.length ? a : m[d]
        };
        x.$check = function (d) {
            d = (new RegExp("^" + d.toString().substring(1, d.toString().length - 1))).exec(this.working);
            return null ==
                d ? this.matched = a : this.matched = d[0]
        };
        x.$peek = function (a) {
            return this.working.substring(0, a)
        };
        x["$eos?"] = function () {
            return 0 === this.working.length
        };
        x.$skip = function (d) {
            d = new RegExp("^" + d.source);
            d = d.exec(this.working);
            if (null == d)return this.matched = a;
            d = d[0];
            var m = d.length;
            this.matched = d;
            this.prev_pos = this.pos;
            this.pos += m;
            this.working = this.working.substring(m);
            return m
        };
        x.$get_byte = function () {
            var d = a;
            this.pos < this.string.length ? (this.prev_pos = this.pos, this.pos += 1, d = this.matched = this.working.substring(0,
                1), this.working = this.working.substring(1)) : this.matched = a;
            return d
        };
        d.defn(v, "$getch", x.$get_byte);
        x["$pos="] = function (a) {
            0 > a && (a += this.string.$length());
            this.pos = a;
            return this.working = this.string.slice(a)
        };
        x.$rest = function () {
            return this.working
        };
        x.$terminate = function () {
            this.match = a;
            return this["$pos="](this.string.$length())
        };
        return(x.$unscan = function () {
            this.pos = this.prev_pos;
            this.match = this.prev_pos = a;
            return this
        }, a) && "unscan"
    }(d.top, null)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module;
    return function (t) {
        t = u(t, "Comparable");
        var w = t._proto, r = t._scope;
        w["$=="] = function (v) {
            var t, A = a;
            try {
                return(t = this["$equal?"](v)) === a || t._isBoolean && !0 != t ? (t = A = this["$<=>"](v)) === a || t._isBoolean && !0 != t ? !1 : 0 == A : !0
            } catch (m) {
                if (d.$rescue(m, [r.StandardError]))return!1;
                throw m;
            }
        };
        w["$>"] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " + d.$class() + " failed");
            return 0 < A
        };
        w["$>="] =
            function (d) {
                var t, A = a;
                ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " + d.$class() + " failed");
                return 0 <= A
            };
        w["$<"] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " + d.$class() + " failed");
            return 0 > A
        };
        w["$<="] = function (d) {
            var t, A = a;
            ((t = A = this["$<=>"](d)) === a || t._isBoolean && !0 != t) && this.$raise(r.ArgumentError, "comparison of " + this.$class() + " with " +
                d.$class() + " failed");
            return 0 >= A
        };
        d.donate(t, ["$==", "$>", "$>=", "$<", "$<="])
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.klass;
    return function (t, $super) {
        function r() {
        }

        var v = r = u(t, $super, "Dir", r), x = v._scope;
        d.defs(v, "$pwd", function () {
            var d;
            return!1 !== (d = x.ENV["$[]"]("PWD")) && d !== a ? d : "."
        });
        d.defs(v, "$getwd", function () {
            var d;
            return!1 !== (d = x.ENV["$[]"]("PWD")) && d !== a ? d : "."
        });
        return(d.defs(v, "$home", function () {
            return x.ENV["$[]"]("HOME")
        }), a) && "home"
    }(d.top, null)
})(Opal);
(function (d) {
    var a = d.nil, u = d.klass;
    return function (d, $super) {
        function r() {
        }

        r = u(d, $super, "SecurityError", r);
        return a
    }(d.top, d.Exception)
})(Opal);
(function (d) {
    var a = d.top, u = d.nil, t = d.breaker, w = d.slice, r = d.klass, v = d.gvars, x = d.range;
    (function (a, $super) {
        function q() {
        }

        var h = q = r(a, $super, "Kernel", q), n = h._scope, b;
        return(h._proto.$open = b = function (a, f) {
            var p, e, g = b._p || u, l = u;
            f = w.call(arguments, 1);
            b._p = null;
            l = (p = n.File).$new.apply(p, [a].concat(f));
            return g !== u ? (e = d.$yield1(g, l), e) : l
        }, u) && "open"
    })(a, null);
    return function (a, $super) {
        function q() {
        }

        var h = q = r(a, $super, "File", q), n = h._proto, b = h._scope, c;
        n.eof = n.path = u;
        d.cdecl(b, "SEPARATOR", "/");
        d.cdecl(b, "ALT_SEPARATOR",
            u);
        h.$attr_reader("eof");
        h.$attr_reader("lineno");
        h.$attr_reader("path");
        n.$initialize = function (a, b) {
            this.path = a;
            this.contents = u;
            this.eof = !1;
            return this.lineno = 0
        };
        n.$read = function () {
            var a, c = u;
            return(a = this.eof) === u || a._isBoolean && !0 != a ? (c = b.File.$read(this.path), this.eof = !0, this.lineno = c.$size(), c) : ""
        };
        n.$each_line = c = function (a) {
            var p, e = c._p || u, g = u;
            null == v["/"] && (v["/"] = u);
            null == a && (a = v["/"]);
            c._p = null;
            if ((p = this.eof) !== u && (!p._isBoolean || !0 == p))return e !== u ? this : [].$to_enum();
            if (e !== u) {
                g = b.File.$read(this.path);
                this.eof = !1;
                this.lineno = 0;
                for (var l = g.$chomp(), g = g.length != l.length, l = l.split(a), k = 0, s = l.length; k < s; k++)this.lineno += 1, k < s - 1 || g ? (p = d.$yield1(e, l[k] + a)) === t ? t.$v : p : (p = d.$yield1(e, l[k])) === t ? t.$v : p;
                this.eof = !0;
                return this
            }
            return this.$read().$each_line()
        };
        d.defs(h, "$expand_path", function (a) {
            return a
        });
        d.defs(h, "$join", function (a) {
            a = w.call(arguments, 0);
            return a["$*"](b.SEPARATOR)
        });
        d.defs(h, "$basename", function (a) {
            var c, e = u;
            return(c = e = a.$rindex(b.SEPARATOR)) === u || c._isBoolean && !0 != c ? a : a["$[]"](x(e["$+"](1),
                -1, !1))
        });
        d.defs(h, "$dirname", function (a) {
            var c, e = u;
            return(c = e = a.$rindex(b.SEPARATOR)) === u || c._isBoolean && !0 != c ? "." : a["$[]"](x(0, e["$-"](1), !1))
        });
        d.defs(h, "$extname", function (a) {
            var b, c = u;
            if ((b = a["$nil_or_empty?"]()) !== u && (!b._isBoolean || !0 == b))return"";
            c = a["$[]"](x(1, -1, !1)).$rindex(".");
            return(b = c["$nil?"]()) === u || b._isBoolean && !0 != b ? a["$[]"](x(c["$+"](1), -1, !1)) : ""
        });
        d.defs(h, "$file?", function (a) {
            return!0
        });
        return(d.defs(h, "$read", function (a) {
            var c = "", e = -1;
            try {
                var g = new XMLHttpRequest;
                g.open("GET",
                    a, !1);
                g.addEventListener("load", function () {
                    e = this.status;
                    if (0 == e || 200 == e)c = this.responseText
                });
                g.overrideMimeType("text/plain");
                g.send()
            } catch (l) {
                e = 0
            }
            if (404 == e || 0 == e && "" == c)throw b.IOError.$new("No such file or directory: " + a);
            return c
        }), u) && "read"
    }(a, null)
})(Opal);
(function (d) {
    return!0
})(Opal);
(function (d) {
    var a = d.nil, u = d.breaker, t = d.slice, w = d.module;
    return function (r) {
        (function (r) {
            r = w(r, "Debug");
            var x = r._scope, A;
            r.show_debug = a;
            d.defs(r, "$debug", A = function () {
                var m, q = A._p || a;
                A._p = null;
                return(m = this["$show_debug_output?"]()) === a || m._isBoolean && !0 != m ? a : this.$warn((m = d.$yieldX(q, [])) === u ? u.$v : m)
            });
            d.defs(r, "$set_debug", function (a) {
                return this.show_debug = a
            });
            d.defs(r, "$show_debug_output?", function () {
                var d, q;
                null == this.show_debug && (this.show_debug = a);
                return!1 !== (d = this.show_debug) && d !== a ? d : (q =
                    x.ENV["$[]"]("DEBUG")["$=="]("true")) ? x.ENV["$[]"]("SUPPRESS_DEBUG")["$=="]("true")["$!"]() : q
            });
            d.defs(r, "$puts_indented", function (d, q) {
                var h, n, b, c = a;
                q = t.call(arguments, 1);
                c = " "["$*"](d)["$*"](2);
                return(h = (n = q).$each, h._p = (b = function (f) {
                    var p = b._s || this, e, g;
                    null == f && (f = a);
                    return(e = p.$debug, e._p = (g = function () {
                        return"" + c + f
                    }, g._s = p, g), e).call(p)
                }, b._s = this, b), h).call(n)
            })
        })(w(r, "Asciidoctor"))
    }(d.top)
})(Opal);
(function (d) {
    var a = d.module, a = a(d.top, "Asciidoctor")._scope;
    d.cdecl(a, "VERSION", "1.5.0-preview.7")
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2, r = d.gvars;
    return function (v) {
        (function (v, $super) {
            function m() {
            }

            var q = (m = t(v, $super, "Timings", m))._proto;
            q.timers = q.log = a;
            q.$initialize = function () {
                this.log = w([], {});
                return this.timers = w([], {})
            };
            q.$start = function (a) {
                var n;
                return this.timers["$[]="](a, (null == (n = d.Object._scope.Time) ? d.cm("Time") : n).$now())
            };
            q.$record = function (a) {
                var n;
                return this.log["$[]="](a, (null == (n = d.Object._scope.Time) ? d.cm("Time") : n).$now()["$-"](this.timers.$delete(a)))
            };
            q.$read_parse = function () {
                var d, n = a;
                return(n = (!1 !== (d = this.log["$[]"]("read")) && d !== a ? d : 0)["$+"](!1 !== (d = this.log["$[]"]("parse")) && d !== a ? d : 0))["$>"](0) ? n : a
            };
            q.$convert = function () {
                return this.log["$[]"]("convert")
            };
            q.$read_parse_convert = function () {
                var d, n = a;
                return(n = (!1 !== (d = this.log["$[]"]("read")) && d !== a ? d : 0)["$+"](!1 !== (d = this.log["$[]"]("parse")) && d !== a ? d : 0)["$+"](!1 !== (d = this.log["$[]"]("convert")) && d !== a ? d : 0))["$>"](0) ? n : a
            };
            q.$total = function () {
                var d, n = a;
                return(n = (!1 !== (d = this.log["$[]"]("read")) &&
                    d !== a ? d : 0)["$+"](!1 !== (d = this.log["$[]"]("parse")) && d !== a ? d : 0)["$+"](!1 !== (d = this.log["$[]"]("convert")) && d !== a ? d : 0)["$+"](!1 !== (d = this.log["$[]"]("write")) && d !== a ? d : 0))["$>"](0) ? n : a
            };
            return(q.$print_report = function (d, n) {
                null == r.stdout && (r.stdout = a);
                null == d && (d = r.stdout);
                null == n && (n = a);
                !1 !== n && n !== a && d.$puts("Input file: " + n);
                d.$puts("  Time to read and parse source: " + "%05.5f"["$%"](this.$read_parse()));
                d.$puts("  Time to convert document: " + "%05.5f"["$%"](this.$convert()));
                return d.$puts("  Total time (read, parse and convert): " +
                    "%05.5f"["$%"](this.$read_parse_convert()))
            }, a) && "print_report"
        })(u(v, "Asciidoctor"), null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.top, u = d.nil, t = d.klass;
    (function (a, $super) {
        function v() {
        }

        var x = v = t(a, $super, "NilClass", v), A = x._proto, m;
        return(m = x["$respond_to?"]("nil_or_empty?")) === u || m._isBoolean && !0 != m ? d.defn(x, "$nil_or_empty?", A["$nil?"]) : u
    })(a, null);
    (function (a, $super) {
        function v() {
        }

        var x = v = t(a, $super, "String", v), A = x._proto, m;
        return(m = x["$respond_to?"]("nil_or_empty?")) === u || m._isBoolean && !0 != m ? d.defn(x, "$nil_or_empty?", A["$empty?"]) : u
    })(a, null);
    (function (a, $super) {
        function v() {
        }

        var x = v = t(a, $super, "Array",
            v), A = x._proto, m;
        return(m = x["$respond_to?"]("nil_or_empty?")) === u || m._isBoolean && !0 != m ? d.defn(x, "$nil_or_empty?", A["$empty?"]) : u
    })(a, null);
    (function (a, $super) {
        function v() {
        }

        var x = v = t(a, $super, "Hash", v), A = x._proto, m;
        return(m = x["$respond_to?"]("nil_or_empty?")) === u || m._isBoolean && !0 != m ? d.defn(x, "$nil_or_empty?", A["$empty?"]) : u
    })(a, null);
    return function (a, $super) {
        function v() {
        }

        var x = v = t(a, $super, "Numeric", v), A = x._proto, m;
        return(m = x["$respond_to?"]("nil_or_empty?")) === u || m._isBoolean && !0 != m ? d.defn(x, "$nil_or_empty?",
            A["$nil?"]) : u
    }(a, null)
})(Opal);
(function (d) {
    var a = d.nil;
    if (d.RUBY_ENGINE["$=="]("opal"))return a
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.range, w = d.gvars;
    return function (r) {
        (function (r) {
            r = u(r, "Helpers");
            var x = r._scope;
            d.defs(r, "$require_library", function (a, d) {
                return!0
            });
            d.defs(r, "$normalize_lines", function (a) {
                var m;
                return a.$class()["$=="](null == (m = d.Object._scope.String) ? d.cm("String") : m) ? this.$normalize_lines_from_string(a) : this.$normalize_lines_array(a)
            });
            d.defs(r, "$normalize_lines_array", function (r) {
                var m, q, h, n, b, c, f, p = a, e = a, g = a, l = a;
                if ((m = r["$empty?"]()) !== a && (!m._isBoolean || !0 == m))return[];
                p = (e = r["$[]"](0))["$[]"](t(0, 2, !1)).$bytes().$to_a();
                if ((m = x.COERCE_ENCODING) === a || m._isBoolean && !0 != m) {
                    if (p["$=="](x.BOM_BYTES_UTF_8))r["$[]="](0, e["$[]"](t(3, -1, !1)));
                    return(m = r.$map, m._p = (f = function (b) {
                        null == b && (b = a);
                        return b.$rstrip()
                    }, f._s = this, f), m).call(r)
                }
                g = (null == (m = d.Object._scope.Encoding) ? d.cm("Encoding") : m)._scope.UTF_8;
                if ((l = p["$[]"](t(0, 1, !1)))["$=="](x.BOM_BYTES_UTF_16LE))return(m = (q = r.$join().$force_encoding((null == (n = d.Object._scope.Encoding) ? d.cm("Encoding") : n)._scope.UTF_16LE)["$[]"](t(1,
                    -1, !1)).$encode(g).$lines()).$map, m._p = (h = function (b) {
                    null == b && (b = a);
                    return b.$rstrip()
                }, h._s = this, h), m).call(q);
                if (l["$=="](x.BOM_BYTES_UTF_16BE))return r["$[]="](0, e.$force_encoding((null == (m = d.Object._scope.Encoding) ? d.cm("Encoding") : m)._scope.UTF_16BE)["$[]"](t(1, -1, !1))), (m = (n = r).$map, m._p = (b = function (b) {
                    var c;
                    null == b && (b = a);
                    return"" + b.$force_encoding((null == (c = d.Object._scope.Encoding) ? d.cm("Encoding") : c)._scope.UTF_16BE).$encode(g).$rstrip()
                }, b._s = this, b), m).call(n);
                if (p["$[]"](t(0, 2, !1))["$=="](x.BOM_BYTES_UTF_8))r["$[]="](0,
                    e.$force_encoding(g)["$[]"](t(1, -1, !1)));
                return(m = r.$map, m._p = (c = function (b) {
                    null == b && (b = a);
                    return b.$encoding()["$=="](g) ? b.$rstrip() : b.$force_encoding(g).$rstrip()
                }, c._s = this, c), m).call(r)
            });
            d.defs(r, "$normalize_lines_from_string", function (r) {
                var m, q, h, n = a, b = a, c = a;
                if ((m = r["$nil_or_empty?"]()) !== a && (!m._isBoolean || !0 == m))return[];
                (m = x.COERCE_ENCODING) === a || m._isBoolean && !0 != m ? r["$[]"](t(0, 2, !1)).$bytes().$to_a()["$=="](x.BOM_BYTES_UTF_8) && (r = r["$[]"](t(3, -1, !1))) : (n = (null == (m = d.Object._scope.Encoding) ?
                    d.cm("Encoding") : m)._scope.UTF_8, b = r["$[]"](t(0, 2, !1)).$bytes().$to_a(), (c = b["$[]"](t(0, 1, !1)))["$=="](x.BOM_BYTES_UTF_16LE) ? r = r.$force_encoding((null == (m = d.Object._scope.Encoding) ? d.cm("Encoding") : m)._scope.UTF_16LE)["$[]"](t(1, -1, !1)).$encode(n) : c["$=="](x.BOM_BYTES_UTF_16BE) ? r = r.$force_encoding((null == (m = d.Object._scope.Encoding) ? d.cm("Encoding") : m)._scope.UTF_16BE)["$[]"](t(1, -1, !1)).$encode(n) : b["$[]"](t(0, 2, !1))["$=="](x.BOM_BYTES_UTF_8) ? r = r.$encoding()["$=="](n) ? r["$[]"](t(1, -1, !1)) : r.$force_encoding(n)["$[]"](t(1,
                    -1, !1)) : r.$encoding()["$=="](n) || (r = r.$force_encoding(n)));
                return(m = (q = r.$each_line()).$map, m._p = (h = function (b) {
                    null == b && (b = a);
                    return b.$rstrip()
                }, h._s = this, h), m).call(q)
            });
            d.cdecl(x, "REGEXP_ENCODE_URI_CHARS", /[^\w\-.!~*';:@=+$,()\[\]]/);
            d.defs(r, "$encode_uri", function (d) {
                var m, q;
                return(m = d.$gsub, m._p = (q = function () {
                    var d = q._s || this, n, b, c;
                    null == w["&"] && (w["&"] = a);
                    return(n = (b = w["&"].$each_byte()).$map, n._p = (c = function (b) {
                        var d = c._s || this;
                        null == b && (b = a);
                        return d.$sprintf("%%%02X", b)
                    }, c._s = d, c), n).call(b).$join()
                },
                    q._s = this, q), m).call(d, x.REGEXP_ENCODE_URI_CHARS)
            });
            d.defs(r, "$rootname", function (r) {
                var m, q, h = a;
                return(m = (h = (null == (q = d.Object._scope.File) ? d.cm("File") : q).$extname(r))["$empty?"]()) === a || m._isBoolean && !0 != m ? r["$[]"](t(0, h.$length()["$-@"](), !0)) : r
            });
            d.defs(r, "$mkdir_p", function (r) {
                var m, q, h, n = a;
                return(m = (null == (q = d.Object._scope.File) ? d.cm("File") : q)["$directory?"](r)) === a || m._isBoolean && !0 != m ? (n = (null == (m = d.Object._scope.File) ? d.cm("File") : m).$dirname(r), (m = (q = (null == (h = d.Object._scope.File) ? d.cm("File") :
                    h)["$directory?"](n = (null == (h = d.Object._scope.File) ? d.cm("File") : h).$dirname(r))["$!"](), !1 !== q && q !== a ? n["$=="](".")["$!"]() : q)) === a || m._isBoolean && !0 != m || this.$mkdir_p(n), (null == (m = d.Object._scope.Dir) ? d.cm("Dir") : m).$mkdir(r)) : a
            })
        })(u(r, "Asciidoctor"))
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.breaker, t = d.slice, w = d.module, r = d.hash2, v = d.gvars, x = d.range;
    return function (A) {
        (function (m) {
            m = w(m, "Substitutors");
            var q = m._proto, h = m._scope;
            d.cdecl(h, "SPECIAL_CHARS", r(["&", "<", ">"], {"&": "&amp;", "<": "&lt;", ">": "&gt;"}));
            d.cdecl(h, "SPECIAL_CHARS_PATTERN", new RegExp("[" + h.SPECIAL_CHARS.$keys().$join() + "]"));
            d.cdecl(h, "SUBS", r("basic normal verbatim title header pass".split(" "), {basic: ["specialcharacters"], normal: "specialcharacters quotes attributes replacements macros post_replacements".split(" "),
                verbatim: ["specialcharacters", "callouts"], title: "specialcharacters quotes replacements macros attributes post_replacements".split(" "), header: ["specialcharacters", "attributes"], pass: []}));
            d.cdecl(h, "COMPOSITE_SUBS", r(["none", "normal", "verbatim", "specialchars"], {none: [], normal: h.SUBS["$[]"]("normal"), verbatim: h.SUBS["$[]"]("verbatim"), specialchars: ["specialcharacters"]}));
            d.cdecl(h, "SUB_SYMBOLS", r("amnpqrcv".split(""), {a: "attributes", m: "macros", n: "normal", p: "post_replacements", q: "quotes", r: "replacements",
                c: "specialcharacters", v: "verbatim"}));
            d.cdecl(h, "SUB_OPTIONS", r(["block", "inline"], {block: h.COMPOSITE_SUBS.$keys()["$+"](h.SUBS["$[]"]("normal"))["$+"](["callouts"]), inline: h.COMPOSITE_SUBS.$keys()["$+"](h.SUBS["$[]"]("normal"))}));
            d.cdecl(h, "PASS_START", "\u0096");
            d.cdecl(h, "PASS_END", "\u0097");
            d.cdecl(h, "PASS_MATCH", /\u0096(\d+)\u0097/);
            d.cdecl(h, "PASS_MATCH_HI", /<span[^>]*>\u0096<\/span>[^\d]*(\d+)[^\d]*<span[^>]*>\u0097<\/span>/);
            m.$attr_reader("passthroughs");
            q.$apply_subs = function (n, b, c) {
                var f,
                    p, e, g, l, k = a, s = a, z = a, G = a;
                null == b && (b = "normal");
                null == c && (c = !1);
                if ((f = b["$!"]()) !== a && (!f._isBoolean || !0 == f))return n;
                b["$=="]("normal") ? b = h.SUBS["$[]"]("normal") : !1 !== c && c !== a && ((f = b["$is_a?"](null == (p = d.Object._scope.Symbol) ? d.cm("Symbol") : p)) === a || f._isBoolean && !0 != f ? (k = [], (f = (p = b).$each, f._p = (e = function (b) {
                    var c;
                    null == b && (b = a);
                    return(c = h.COMPOSITE_SUBS["$has_key?"](b)) === a || c._isBoolean && !0 != c ? k["$<<"](b) : k = k["$+"](h.COMPOSITE_SUBS["$[]"](b))
                }, e._s = this, e), f).call(p), b = k) : b = !1 !== (f = h.COMPOSITE_SUBS["$[]"](b)) &&
                    f !== a ? f : [b]);
                if ((f = b["$empty?"]()) !== a && (!f._isBoolean || !0 == f))return n;
                s = (f = z = n["$is_a?"](null == (g = d.Object._scope.Array) ? d.cm("Array") : g)) === a || f._isBoolean && !0 != f ? n : n["$*"](h.EOL);
                (f = G = b["$include?"]("macros")) === a || f._isBoolean && !0 != f || (s = this.$extract_passthroughs(s));
                (f = (g = b).$each, f._p = (l = function (c) {
                    var e = l._s || this, f, k = a;
                    null == c && (c = a);
                    k = c;
                    return"specialcharacters"["$==="](k) ? s = e.$sub_specialcharacters(s) : "quotes"["$==="](k) ? s = e.$sub_quotes(s) : "attributes"["$==="](k) ? s = e.$sub_attributes(s.$split(h.EOL))["$*"](h.EOL) :
                        "replacements"["$==="](k) ? s = e.$sub_replacements(s) : "macros"["$==="](k) ? s = e.$sub_macros(s) : "highlight"["$==="](k) ? s = e.$highlight_source(s, b["$include?"]("callouts")) : "callouts"["$==="](k) ? (f = b["$include?"]("highlight")) === a || f._isBoolean && !0 != f ? s = e.$sub_callouts(s) : a : "post_replacements"["$==="](k) ? s = e.$sub_post_replacements(s) : e.$warn("asciidoctor: WARNING: unknown substitution type " + c)
                }, l._s = this, l), f).call(g);
                !1 !== G && G !== a && (s = this.$restore_passthroughs(s));
                return!1 !== z && z !== a ? s.$split(h.EOL) : s
            };
            q.$apply_normal_subs = function (n) {
                var b, c;
                return this.$apply_subs((b = n["$is_a?"](null == (c = d.Object._scope.Array) ? d.cm("Array") : c)) === a || b._isBoolean && !0 != b ? n : n["$*"](h.EOL))
            };
            q.$apply_title_subs = function (a) {
                return this.$apply_subs(a, h.SUBS["$[]"]("title"))
            };
            q.$apply_header_subs = function (a) {
                return this.$apply_subs(a, h.SUBS["$[]"]("header"))
            };
            q.$extract_passthroughs = function (n) {
                var b, c, f, p, e, g, l;
                (b = !1 !== (c = !1 !== (f = n["$include?"]("++")) && f !== a ? f : n["$include?"]("$$")) && c !== a ? c : n["$include?"]("ss:")) ===
                    a || b._isBoolean && !0 != b || (n = (b = (c = n).$gsub, b._p = (p = function () {
                    var b = p._s || this, c, e = a, e = e = a;
                    null == b.passthroughs && (b.passthroughs = a);
                    null == v["~"] && (v["~"] = a);
                    e = v["~"];
                    if ((c = e["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return e["$[]"](0)["$[]"](x(1, -1, !1));
                    (c = e["$[]"](4)["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c ? (n = b.$unescape_brackets(e["$[]"](4)), e = (c = e["$[]"](3)["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c ? b.$resolve_pass_subs(e["$[]"](3)) : []) : (n = e["$[]"](2), e = e["$[]"](1)["$=="]("$$") ?
                        ["specialcharacters"] : []);
                    b.passthroughs["$<<"](r(["text", "subs"], {text: n, subs: e}));
                    e = b.passthroughs.$size()["$-"](1);
                    return"" + h.PASS_START + e + h.PASS_END
                }, p._s = this, p), b).call(c, h.PassInlineMacroRx));
                (b = n["$include?"]("`")) === a || b._isBoolean && !0 != b || (n = (b = (f = n).$gsub, b._p = (e = function () {
                    var b = e._s || this, c, f, g = a, l = a, p = a, p = a;
                    null == b.passthroughs && (b.passthroughs = a);
                    null == v["~"] && (v["~"] = a);
                    g = v["~"];
                    if ((c = null == (f = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : f) !== a && (!c._isBoolean || !0 == c) &&
                        g["$[]"](2)["$=="](""))g["$[]="](2, a);
                    l = a;
                    if ((c = g["$[]"](3)["$start_with?"]("\\")) === a || c._isBoolean && !0 != c)(c = (f = g["$[]"](1)["$=="]("\\")) ? g["$[]"](2) : f) === a || c._isBoolean && !0 != c || (l = "[" + g["$[]"](2) + "]"); else return(c = g["$[]"](2)) === a || c._isBoolean && !0 != c ? "" + g["$[]"](1) + g["$[]"](3)["$[]"](x(1, -1, !1)) : "" + g["$[]"](1) + "[" + g["$[]"](2) + "]" + g["$[]"](3)["$[]"](x(1, -1, !1));
                    p = (c = (f = l["$!"](), !1 !== f && f !== a ? g["$[]"](2) : f)) === a || c._isBoolean && !0 != c ? a : b.$parse_attributes(g["$[]"](2));
                    b.passthroughs["$<<"](r(["text",
                        "subs", "attributes", "type"], {text: g["$[]"](4), subs: ["specialcharacters"], attributes: p, type: "monospaced"}));
                    p = b.passthroughs.$size()["$-"](1);
                    return"" + (!1 !== (c = l) && c !== a ? c : g["$[]"](1)) + h.PASS_START + p + h.PASS_END
                }, e._s = this, e), b).call(f, h.PassInlineLiteralRx));
                (b = n["$include?"]("math:")) === a || b._isBoolean && !0 != b || (n = (b = (g = n).$gsub, b._p = (l = function () {
                    var b = l._s || this, c, e = a, f = a, g = a, f = e = a;
                    null == b.document && (b.document = a);
                    null == b.passthroughs && (b.passthroughs = a);
                    null == v["~"] && (v["~"] = a);
                    e = v["~"];
                    if ((c = e["$[]"](0)["$start_with?"]("\\")) !==
                        a && (!c._isBoolean || !0 == c))return e["$[]"](0)["$[]"](x(1, -1, !1));
                    f = e["$[]"](1).$to_sym();
                    f["$=="]("math") && (f = ((c = (g = b.$document().$attributes()["$[]"]("math"))["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c ? g : "asciimath").$to_sym());
                    n = b.$unescape_brackets(e["$[]"](3));
                    e = (c = e["$[]"](2)["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c ? b.$resolve_pass_subs(e["$[]"](2)) : (c = b.document["$basebackend?"]("html")) === a || c._isBoolean && !0 != c ? [] : ["specialcharacters"];
                    b.passthroughs["$<<"](r(["text", "subs", "type"],
                        {text: n, subs: e, type: f}));
                    f = b.passthroughs.$size()["$-"](1);
                    return"" + h.PASS_START + f + h.PASS_END
                }, l._s = this, l), b).call(g, h.MathInlineMacroRx));
                return n
            };
            q.$restore_passthroughs = function (d) {
                var b, c, f;
                null == this.passthroughs && (this.passthroughs = a);
                return(b = !1 !== (c = this.passthroughs["$nil_or_empty?"]()) && c !== a ? c : d["$include?"](h.PASS_START)["$!"]()) === a || b._isBoolean && !0 != b ? (b = (c = d).$gsub, b._p = (f = function () {
                    var b = f._s || this, c, g = a, l = a, k = a, d = a;
                    null == b.passthroughs && (b.passthroughs = a);
                    null == v["~"] && (v["~"] =
                        a);
                    g = b.passthroughs["$[]"](v["~"]["$[]"](1).$to_i());
                    l = (c = k = g["$[]"]("subs")) === a || c._isBoolean && !0 != c ? g["$[]"]("text") : b.$apply_subs(g["$[]"]("text"), k);
                    return(c = d = g["$[]"]("type")) === a || c._isBoolean && !0 != c ? l : h.Inline.$new(b, "quoted", l, r(["type", "attributes"], {type: d, attributes: g["$[]"]("attributes")})).$convert()
                }, f._s = this, f), b).call(c, h.PASS_MATCH) : d
            };
            q.$sub_specialcharacters = function (d) {
                var b, c;
                return(b = h.SUPPORTS_GSUB_RESULT_HASH) === a || b._isBoolean && !0 != b ? (b = d.$gsub, b._p = (c = function () {
                    null ==
                        v["&"] && (v["&"] = a);
                    return h.SPECIAL_CHARS["$[]"](v["&"])
                }, c._s = this, c), b).call(d, h.SPECIAL_CHARS_PATTERN) : d.$gsub(h.SPECIAL_CHARS_PATTERN, h.SPECIAL_CHARS)
            };
            d.defn(m, "$sub_specialchars", q.$sub_specialcharacters);
            q.$sub_quotes = function (n) {
                var b, c, f, p, e, g = a;
                (b = null == (c = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : c) === a || b._isBoolean && !0 != b ? (g = "" + n, (b = (p = h.QUOTE_SUBS).$each, b._p = (e = function (b, c, f) {
                    var d = e._s || this, p, n, h;
                    null == b && (b = a);
                    null == c && (c = a);
                    null == f && (f = a);
                    return(p = (n = g)["$gsub!"],
                        p._p = (h = function () {
                            var e = h._s || this;
                            null == v["~"] && (v["~"] = a);
                            return e.$convert_quoted_text(v["~"], b, c)
                        }, h._s = d, h), p).call(n, f)
                }, e._s = this, e), b).call(p)) : (g = n, (b = (c = h.QUOTE_SUBS).$each, b._p = (f = function (b, c, e) {
                    var d = f._s || this, p, n, h;
                    null == b && (b = a);
                    null == c && (c = a);
                    null == e && (e = a);
                    return g = (p = (n = g).$gsub, p._p = (h = function () {
                        var e = h._s || this;
                        null == v["~"] && (v["~"] = a);
                        return e.$convert_quoted_text(v["~"], b, c)
                    }, h._s = d, h), p).call(n, e)
                }, f._s = this, f), b).call(c));
                return g
            };
            q.$sub_replacements = function (n) {
                var b,
                    c, f, p, e, g = a;
                (b = null == (c = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : c) === a || b._isBoolean && !0 != b ? (g = "" + n, (b = (p = h.REPLACEMENTS).$each, b._p = (e = function (b, c, f) {
                    var d = e._s || this, p, n, h;
                    null == b && (b = a);
                    null == c && (c = a);
                    null == f && (f = a);
                    return(p = (n = g)["$gsub!"], p._p = (h = function () {
                        var b = h._s || this;
                        null == v["~"] && (v["~"] = a);
                        return b.$do_replacement(v["~"], c, f)
                    }, h._s = d, h), p).call(n, b)
                }, e._s = this, e), b).call(p)) : (g = n, (b = (c = h.REPLACEMENTS).$each, b._p = (f = function (b, c, e) {
                    var d = f._s || this, p, n, h;
                    null == b &&
                    (b = a);
                    null == c && (c = a);
                    null == e && (e = a);
                    return g = (p = (n = g).$gsub, p._p = (h = function () {
                        var b = h._s || this;
                        null == v["~"] && (v["~"] = a);
                        return b.$do_replacement(v["~"], c, e)
                    }, h._s = d, h), p).call(n, b)
                }, f._s = this, f), b).call(c));
                return g
            };
            q.$do_replacement = function (d, b, c) {
                var f, p = a, p = a;
                (f = (p = d["$[]"](0))["$include?"]("\\")) === a || f._isBoolean && !0 != f ? (p = c, d = "none"["$==="](p) ? b : "leading"["$==="](p) ? "" + d["$[]"](1) + b : "bounding"["$==="](p) ? "" + d["$[]"](1) + b + d["$[]"](2) : a) : d = p.$tr("\\", "");
                return d
            };
            q.$sub_attributes = function (n, b) {
                var c, f, p, e = a, g = a, l = a;
                null == b && (b = r([], {}));
                if ((c = n["$nil_or_empty?"]()) !== a && (!c._isBoolean || !0 == c))return n;
                e = n["$is_a?"](null == (c = d.Object._scope.String) ? d.cm("String") : c);
                g = !1 !== e && e !== a ? [n] : n;
                l = [];
                (c = (f = g).$each, c._p = (p = function (c) {
                    var e = p._s || this, f, g, n, m, q, y = a, r = a;
                    null == c && (c = a);
                    r = y = !1;
                    (f = c["$include?"]("{")) === a || f._isBoolean && !0 != f || (c = (f = (g = c).$gsub, f._p = (n = function () {
                        var c = n._s || this, e, f, g, k, l, p = a, s = a, z = a, G = a, m = a, q = a, da = a, Z = a, t = a;
                        null == c.document && (c.document = a);
                        null == v["~"] && (v["~"] =
                            a);
                        p = v["~"];
                        if ((e = !1 !== (f = p["$[]"](1)["$=="]("\\")) && f !== a ? f : p["$[]"](4)["$=="]("\\")) === a || e._isBoolean && !0 != e) {
                            if ((e = p["$[]"](3)["$nil_or_empty?"]()["$!"]()) === a || e._isBoolean && !0 != e)return(e = (k = t = p["$[]"](2).$downcase(), !1 !== k && k !== a ? c.document.$attributes()["$has_key?"](t) : k)) === a || e._isBoolean && !0 != e ? (e = h.INTRINSIC_ATTRIBUTES["$has_key?"](t)) === a || e._isBoolean && !0 != e ? function () {
                                m = !1 !== (e = b["$[]"]("attribute_missing")) && e !== a ? e : c.document.$attributes().$fetch("attribute-missing", h.Compliance.$attribute_missing());
                                if ("skip"["$==="](m))return p["$[]"](0);
                                if ("drop-line"["$==="](m))return(e = (k = h.Debug).$debug, e._p = (l = function () {
                                    return"Missing attribute: " + t + ", line marked for removal"
                                }, l._s = c, l), e).call(k), y = !0, u.$v = "", u;
                                r = !0;
                                return""
                            }() : h.INTRINSIC_ATTRIBUTES["$[]"](t) : c.document.$attributes()["$[]"](t);
                            s = (z = p["$[]"](3)).$length()["$+"](1);
                            G = p["$[]"](2)["$[]"](x(s, -1, !1));
                            return function () {
                                m = z;
                                if ("set"["$==="](m)) {
                                    q = G.$split(":");
                                    e = d.to_ary(h.Parser.$store_attribute(q["$[]"](0), !1 !== (f = q["$[]"](1)) && f !== a ? f :
                                        "", c.document));
                                    da = null == e[1] ? a : e[1];
                                    if ((!1 === da || da === a) && c.document.$attributes().$fetch("attribute-undefined", h.Compliance.$attribute_undefined())["$=="]("drop-line"))return(e = (f = h.Debug).$debug, e._p = (g = function () {
                                        return"Undefining attribute: " + (g._s || this).$key() + ", line marked for removal"
                                    }, g._s = c, g), e).call(f), y = !0, u.$v = "", u;
                                    r = !0;
                                    return""
                                }
                                if ("counter"["$==="](m) || "counter2"["$==="](m))return q = G.$split(":"), Z = c.document.$counter(q["$[]"](0), q["$[]"](1)), z["$=="]("counter2") ? (r = !0, "") : Z;
                                c.$warn("asciidoctor: WARNING: illegal attribute directive: " +
                                    p["$[]"](3));
                                return p["$[]"](0)
                            }()
                        }
                        return"{" + p["$[]"](2) + "}"
                    }, n._s = e, n), f).call(g, h.AttributeReferenceRx));
                    return(f = !1 !== (m = y) && m !== a ? m : (q = !1 !== r && r !== a) ? c["$empty?"]() : q) === a || f._isBoolean && !0 != f ? l["$<<"](c) : a
                }, p._s = this, p), c).call(f);
                return!1 !== e && e !== a ? l["$*"](h.EOL) : l
            };
            q.$sub_macros = function (n) {
                var b, c, f, p, e, g, l, k, s, z, G, R, m, q, y, M, ga, D, B, C, u, J = a, F = a, A = a, L = a, w = a, S = a;
                null == this.document && (this.document = a);
                if ((b = n["$nil_or_empty?"]()) !== a && (!b._isBoolean || !0 == b))return n;
                J = r([], {});
                J["$[]="]("square_bracket",
                    n["$include?"]("["));
                J["$[]="]("round_bracket", n["$include?"]("("));
                J["$[]="]("colon", F = n["$include?"](":"));
                J["$[]="]("macroish", (b = J["$[]"]("square_bracket"), !1 !== b && b !== a ? F : b));
                J["$[]="]("macroish_short_form", (b = (c = J["$[]"]("square_bracket"), !1 !== c && c !== a ? F : c), !1 !== b && b !== a ? n["$include?"](":[") : b));
                A = this.document.$attributes()["$has_key?"]("linkattrs");
                L = this.document.$attributes()["$has_key?"]("experimental");
                w = "" + n;
                !1 !== L && L !== a && ((b = (c = J["$[]"]("macroish_short_form"), !1 !== c && c !== a ? !1 !== (f =
                    w["$include?"]("kbd:")) && f !== a ? f : w["$include?"]("btn:") : c)) === a || b._isBoolean && !0 != b || (w = (b = (c = w).$gsub, b._p = (p = function () {
                    var b = p._s || this, c, e, f, g = a, k = a, g = g = a;
                    null == v["~"] && (v["~"] = a);
                    g = v["~"];
                    if ((c = (k = g["$[]"](0))["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return k["$[]"](x(1, -1, !1));
                    if ((c = k["$start_with?"]("kbd")) === a || c._isBoolean && !0 != c) {
                        if ((c = k["$start_with?"]("btn")) === a || c._isBoolean && !0 != c)return a;
                        g = b.$unescape_bracketed_text(g["$[]"](1));
                        return h.Inline.$new(b, "button", g).$convert()
                    }
                    g =
                        b.$unescape_bracketed_text(g["$[]"](1));
                    g = g["$=="]("+") ? ["+"] : (c = (e = g.$split(h.KbdDelimiterRx)).$inject, c._p = (f = function (b, c) {
                        var e;
                        null == b && (b = a);
                        null == c && (c = a);
                        if ((e = c["$end_with?"]("++")) === a || e._isBoolean && !0 != e)b["$<<"](c.$strip()); else b["$<<"](c["$[]"](x(0, -3, !1)).$strip()), b["$<<"]("+");
                        return b
                    }, f._s = b, f), c).call(e, []);
                    return h.Inline.$new(b, "kbd", a, r(["attributes"], {attributes: r(["keys"], {keys: g})})).$convert()
                }, p._s = this, p), b).call(c, h.KbdBtnInlineMacroRx)), (b = (f = J["$[]"]("macroish"), !1 !==
                    f && f !== a ? w["$include?"]("menu:") : f)) === a || b._isBoolean && !0 != b || (w = (b = (f = w).$gsub, b._p = (e = function () {
                    var b = e._s || this, c, f, g, k = a, l = a, d = k = l = a, d = k = a;
                    null == v["~"] && (v["~"] = a);
                    k = v["~"];
                    if ((c = (l = k["$[]"](0))["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return l["$[]"](x(1, -1, !1));
                    l = k["$[]"](1);
                    k = k["$[]"](2);
                    (c = k["$!"]()) === a || c._isBoolean && !0 != c ? (c = d = (f = k["$include?"]("&gt;")) === a || f._isBoolean && !0 != f ? (f = k["$include?"](",")) === a || f._isBoolean && !0 != f ? a : "," : "&gt;") === a || c._isBoolean && !0 != c ? (d = [], k = k.$rstrip()) :
                        (d = (c = (f = k.$split(d)).$map, c._p = (g = function (b) {
                            null == b && (b = a);
                            return b.$strip()
                        }, g._s = b, g), c).call(f), k = d.$pop()) : (d = [], k = a);
                    return h.Inline.$new(b, "menu", a, r(["attributes"], {attributes: r(["menu", "submenus", "menuitem"], {menu: l, submenus: d, menuitem: k})})).$convert()
                }, e._s = this, e), b).call(f, h.MenuInlineMacroRx)), (b = (g = w["$include?"]('"'), !1 !== g && g !== a ? w["$include?"]("&gt;") : g)) === a || b._isBoolean && !0 != b || (w = (b = (g = w).$gsub, b._p = (l = function () {
                    var b = l._s || this, c, e, f, g, k = a, p = a, s = p = k = k = a;
                    null == v["~"] && (v["~"] =
                        a);
                    k = v["~"];
                    if ((c = (p = k["$[]"](0))["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return p["$[]"](x(1, -1, !1));
                    k = k["$[]"](1);
                    c = d.to_ary((e = (f = k.$split("&gt;")).$map, e._p = (g = function (b) {
                        null == b && (b = a);
                        return b.$strip()
                    }, g._s = b, g), e).call(f));
                    k = null == c[0] ? a : c[0];
                    p = t.call(c, 1);
                    s = p.$pop();
                    return h.Inline.$new(b, "menu", a, r(["attributes"], {attributes: r(["menu", "submenus", "menuitem"], {menu: k, submenus: p, menuitem: s})})).$convert()
                }, l._s = this, l), b).call(g, h.MenuInlineRx)));
                (b = (k = S = this.document.$extensions(),
                    !1 !== k && k !== a ? S["$inline_macros?"]() : k)) === a || b._isBoolean && !0 != b || (b = (k = S.$inline_macros()).$each, b._p = (s = function (b) {
                    var c = s._s || this, e, f, g;
                    null == b && (b = a);
                    return w = (e = (f = w).$gsub, e._p = (g = function () {
                        var c = g._s || this, e, f = a, k = a, f = a;
                        null == v["~"] && (v["~"] = a);
                        f = v["~"];
                        if ((e = f["$[]"](0)["$start_with?"]("\\")) !== a && (!e._isBoolean || !0 == e))return f["$[]"](0)["$[]"](x(1, -1, !1));
                        k = f["$[]"](1);
                        f = b.$config()["$[]"]("format")["$=="]("short") ? r([], {}) : b.$config()["$[]"]("content_model")["$=="]("attributes") ? c.$parse_attributes(f["$[]"](2),
                            !1 !== (e = b.$config()["$[]"]("pos_attrs")) && e !== a ? e : [], r(["sub_input", "unescape_input"], {sub_input: !0, unescape_input: !0})) : r(["text"], {text: c.$unescape_bracketed_text(f["$[]"](2))});
                        return b.$process_method()["$[]"](c, k, f)
                    }, g._s = c, g), e).call(f, b.$config()["$[]"]("regexp"))
                }, s._s = this, s), b).call(k);
                (b = (z = J["$[]"]("macroish"), !1 !== z && z !== a ? !1 !== (G = w["$include?"]("image:")) && G !== a ? G : w["$include?"]("icon:") : z)) === a || b._isBoolean && !0 != b || (w = (b = (z = w).$gsub, b._p = (R = function () {
                    var b = R._s || this, c, e, f, g = a, k =
                        e = a, l = a, l = g = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    g = v["~"];
                    if ((c = g["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return g["$[]"](0)["$[]"](x(1, -1, !1));
                    e = b.$unescape_bracketed_text(g["$[]"](2));
                    (c = g["$[]"](0)["$start_with?"]("icon:")) === a || c._isBoolean && !0 != c ? (k = "image", l = ["alt", "width", "height"]) : (k = "icon", l = ["size"]);
                    g = b.$sub_attributes(g["$[]"](1));
                    k["$=="]("icon") || b.document.$register("images", g);
                    l = b.$parse_attributes(e, l);
                    c = "alt";
                    e = l;
                    !1 !== (f = e["$[]"](c)) &&
                        f !== a ? f : e["$[]="](c, h.File.$basename(g, h.File.$extname(g)));
                    return h.Inline.$new(b, "image", a, r(["type", "target", "attributes"], {type: k, target: g, attributes: l})).$convert()
                }, R._s = this, R), b).call(z, h.ImageInlineMacroRx));
                (b = !1 !== (G = J["$[]"]("macroish_short_form")) && G !== a ? G : J["$[]"]("round_bracket")) === a || b._isBoolean && !0 != b || (w = (b = (G = w).$gsub, b._p = (m = function () {
                    var b = m._s || this, c, e, f = a, g = a, k = a, l = a, f = f = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    f = v["~"];
                    if ((c = f["$[]"](0)["$start_with?"]("\\")) !==
                        a && (!c._isBoolean || !0 == c))return f["$[]"](0)["$[]"](x(1, -1, !1));
                    if ((c = null == (e = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : e) !== a && (!c._isBoolean || !0 == c) && f["$[]"](1)["$=="](""))f["$[]="](1, a);
                    g = 0;
                    k = a;
                    if ((c = l = f["$[]"](1)) === a || c._isBoolean && !0 != c)k = f["$[]"](3), (c = (e = k["$start_with?"]("("), !1 !== e && e !== a ? k["$end_with?"](")") : e)) === a || c._isBoolean && !0 != c ? g = 2 : (k = k["$[]"](x(1, -1, !0)), g = 3);
                    if ((c = !1 !== (e = l["$=="]("indexterm")) && e !== a ? e : g["$=="](3)) === a || c._isBoolean && !0 != c)return f = (c = l["$!"]()) ===
                        a || c._isBoolean && !0 != c ? b.$normalize_string(f["$[]"](2), !0) : b.$normalize_string(k), b.document.$register("indexterms", [f]), h.Inline.$new(b, "indexterm", f, r(["type"], {type: "visible"})).$convert();
                    f = (c = l["$!"]()) === a || c._isBoolean && !0 != c ? b.$split_simple_csv(b.$normalize_string(f["$[]"](2), !0)) : b.$split_simple_csv(b.$normalize_string(k));
                    b.document.$register("indexterms", [].concat(f));
                    return h.Inline.$new(b, "indexterm", a, r(["attributes"], {attributes: r(["terms"], {terms: f})})).$convert()
                }, m._s = this, m), b).call(G,
                        h.IndextermInlineMacroRx));
                (b = (q = !1 !== F && F !== a) ? w["$include?"]("://") : q) === a || b._isBoolean && !0 != b || (w = (b = (q = w).$gsub, b._p = (y = function () {
                    var b = y._s || this, c, e, f, g = a, k = a, l = a, p = a, s = a, g = s = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    g = v["~"];
                    if ((c = g["$[]"](2)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return"" + g["$[]"](1) + g["$[]"](2)["$[]"](x(1, -1, !1)) + g["$[]"](3);
                    if ((c = null == (e = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : e) !== a && (!c._isBoolean || !0 == c) && g["$[]"](3)["$=="](""))g["$[]="](3,
                        a);
                    if ((c = (e = g["$[]"](1)["$=="]("link:")) ? g["$[]"](3)["$!"]() : e) !== a && (!c._isBoolean || !0 == c))return g["$[]"](0);
                    k = (c = g["$[]"](1)["$=="]("link:")["$!"]()) === a || c._isBoolean && !0 != c ? "" : g["$[]"](1);
                    l = g["$[]"](2);
                    p = "";
                    if ((c = !1 !== (e = g["$[]"](3)) && e !== a ? e : (f = l["$=~"](h.UriTerminator), f === a || !1 === f)) === a || c._isBoolean && !0 != c)s = v["~"]["$[]"](0), ")"["$==="](s) ? (l = l["$[]"](x(0, -2, !1)), p = ")") : ";"["$==="](s) ? (c = (e = k["$start_with?"]("&lt;"), !1 !== e && e !== a ? l["$end_with?"]("&gt;") : e)) === a || c._isBoolean && !0 != c ? (c = l["$end_with?"](");")) ===
                        a || c._isBoolean && !0 != c ? (l = l["$[]"](x(0, -2, !1)), p = ";") : (l = l["$[]"](x(0, -3, !1)), p = ");") : (k = k["$[]"](x(4, -1, !1)), l = l["$[]"](x(0, -5, !1))) : ":"["$==="](s) && ((c = l["$end_with?"]("):")) === a || c._isBoolean && !0 != c ? (l = l["$[]"](x(0, -2, !1)), p = ":") : (l = l["$[]"](x(0, -3, !1)), p = "):"));
                    b.document.$register("links", l);
                    s = a;
                    if ((c = g["$[]"](3)["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c) {
                        if ((c = (e = !1 !== A && A !== a) ? !1 !== (f = g["$[]"](3)["$start_with?"]('"')) && f !== a ? f : g["$[]"](3)["$include?"](",") : e) === a || c._isBoolean && !0 != c ? g =
                            b.$sub_attributes(g["$[]"](3).$gsub("\\]", "]")) : (s = b.$parse_attributes(b.$sub_attributes(g["$[]"](3).$gsub("\\]", "]")), []), g = s["$[]"](1)), (c = g["$end_with?"]("^")) !== a && (!c._isBoolean || !0 == c) && (g = g.$chop(), !1 !== (c = s) && c !== a ? c : s = r([], {}), (c = s["$has_key?"]("window")) === a || c._isBoolean && !0 != c))s["$[]="]("window", "_blank")
                    } else g = "";
                    (c = g["$empty?"]()) === a || c._isBoolean && !0 != c || (g = (c = b.document["$attr?"]("hide-uri-scheme")) === a || c._isBoolean && !0 != c ? l : l.$sub(h.UriSniffRx, ""));
                    return"" + k + h.Inline.$new(b,
                        "anchor", g, r(["type", "target", "attributes"], {type: "link", target: l, attributes: s})).$convert() + p
                }, y._s = this, y), b).call(q, h.LinkInlineRx));
                (b = !1 !== (M = (ga = J["$[]"]("macroish"), !1 !== ga && ga !== a ? w["$include?"]("link:") : ga)) && M !== a ? M : w["$include?"]("mailto:")) === a || b._isBoolean && !0 != b || (w = (b = (M = w).$gsub, b._p = (D = function () {
                    var b = D._s || this, c, e, f, g = a, k = a, l = a, d = a, p = a, g = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    g = v["~"];
                    if ((c = g["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return g["$[]"](0)["$[]"](x(1,
                        -1, !1));
                    k = g["$[]"](1);
                    l = g["$[]"](0)["$start_with?"]("mailto:");
                    d = !1 !== l && l !== a ? "mailto:" + k : k;
                    p = a;
                    (c = (e = !1 !== A && A !== a) ? !1 !== (f = g["$[]"](2)["$start_with?"]('"')) && f !== a ? f : g["$[]"](2)["$include?"](",") : e) === a || c._isBoolean && !0 != c ? g = b.$sub_attributes(g["$[]"](2).$gsub("\\]", "]")) : (p = b.$parse_attributes(b.$sub_attributes(g["$[]"](2).$gsub("\\]", "]")), []), g = p["$[]"](1), !1 === l || l === a || (c = p["$has_key?"](2)) === a || c._isBoolean && !0 != c || (d = "" + d + "?subject=" + h.Helpers.$encode_uri(p["$[]"](2)), (c = p["$has_key?"](3)) ===
                        a || c._isBoolean && !0 != c || (d = "" + d + "&amp;body=" + h.Helpers.$encode_uri(p["$[]"](3)))));
                    if ((c = g["$end_with?"]("^")) !== a && (!c._isBoolean || !0 == c) && (g = g.$chop(), !1 !== (c = p) && c !== a ? c : p = r([], {}), (c = p["$has_key?"]("window")) === a || c._isBoolean && !0 != c))p["$[]="]("window", "_blank");
                    b.document.$register("links", d);
                    (c = g["$empty?"]()) === a || c._isBoolean && !0 != c || (g = (c = b.document["$attr?"]("hide-uri-scheme")) === a || c._isBoolean && !0 != c ? k : k.$sub(h.UriSniffRx, ""));
                    return h.Inline.$new(b, "anchor", g, r(["type", "target", "attributes"],
                        {type: "link", target: d, attributes: p})).$convert()
                }, D._s = this, D), b).call(M, h.LinkInlineMacroRx));
                (b = w["$include?"]("@")) === a || b._isBoolean && !0 != b || (w = (b = (ga = w).$gsub, b._p = (B = function () {
                    var b = B._s || this, c, e = a, f = a, e = e = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    e = v["~"];
                    f = e["$[]"](0);
                    if ((c = e = e["$[]"](1)) !== a && (!c._isBoolean || !0 == c))return"\\"["$==="](e) ? f["$[]"](x(1, -1, !1)) : f;
                    e = "mailto:" + f;
                    b.document.$register("links", e);
                    return h.Inline.$new(b, "anchor", f, r(["type", "target"], {type: "link",
                        target: e})).$convert()
                }, B._s = this, B), b).call(ga, h.EmailInlineMacroRx));
                (b = (C = J["$[]"]("macroish_short_form"), !1 !== C && C !== a ? w["$include?"]("footnote") : C)) === a || b._isBoolean && !0 != b || (w = (b = (C = w).$gsub, b._p = (u = function () {
                    var b = u._s || this, c, e, f, g, k = a, l = a, p = a, s = k = a, n = a, p = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    k = v["~"];
                    if ((c = k["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return k["$[]"](0)["$[]"](x(1, -1, !1));
                    k["$[]"](1)["$=="]("footnote") ? (l = a, p = b.$restore_passthroughs(b.$sub_inline_xrefs(b.$sub_inline_anchors(b.$normalize_string(k["$[]"](2),
                        !0)))), k = b.document.$counter("footnote-number"), b.document.$register("footnotes", h.Document._scope.Footnote.$new(k, l, p)), n = s = a) : (c = d.to_ary(k["$[]"](2).$split(",", 2)), l = null == c[0] ? a : c[0], p = null == c[1] ? a : c[1], l = l.$strip(), (c = p["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c ? (p = b.$restore_passthroughs(b.$sub_inline_xrefs(b.$sub_inline_anchors(b.$normalize_string(p, !0)))), k = b.document.$counter("footnote-number"), b.document.$register("footnotes", h.Document._scope.Footnote.$new(k, l, p)), s = "ref", n = a) : ((c = p =
                        (e = (f = b.document.$references()["$[]"]("footnotes")).$find, e._p = (g = function (b) {
                            null == b && (b = a);
                            return b.$id()["$=="](l)
                        }, g._s = b, g), e).call(f)) === a || c._isBoolean && !0 != c ? (k = a, p = l) : (k = p.$index(), p = p.$text()), n = l, l = a, s = "xref"));
                    return h.Inline.$new(b, "footnote", p, r(["attributes", "id", "target", "type"], {attributes: r(["index"], {index: k}), id: l, target: n, type: s})).$convert()
                }, u._s = this, u), b).call(C, h.FootnoteInlineMacroRx));
                return this.$sub_inline_xrefs(this.$sub_inline_anchors(w, J), J)
            };
            q.$sub_inline_anchors =
                function (n, b) {
                    var c, f, p, e, g, l, k;
                    null == b && (b = a);
                    (c = (f = !1 !== (p = b["$!"]()) && p !== a ? p : b["$[]"]("square_bracket"), !1 !== f && f !== a ? n["$include?"]("[[[") : f)) === a || c._isBoolean && !0 != c || (n = (c = (f = n).$gsub, c._p = (e = function () {
                        var b = e._s || this, c, f = a, g = f = a;
                        null == v["~"] && (v["~"] = a);
                        f = v["~"];
                        if ((c = f["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return f["$[]"](0)["$[]"](x(1, -1, !1));
                        f = g = f["$[]"](1);
                        return h.Inline.$new(b, "anchor", g, r(["type", "target"], {type: "bibref", target: f})).$convert()
                    }, e._s = this, e),
                        c).call(f, h.InlineBiblioAnchorRx));
                    (c = !1 !== (p = (g = !1 !== (l = b["$!"]()) && l !== a ? l : b["$[]"]("square_bracket"), !1 !== g && g !== a ? n["$include?"]("[[") : g)) && p !== a ? p : (g = !1 !== (l = b["$!"]()) && l !== a ? l : b["$[]"]("macroish"), !1 !== g && g !== a ? n["$include?"]("anchor:") : g)) === a || c._isBoolean && !0 != c || (n = (c = (p = n).$gsub, c._p = (k = function () {
                        var b = k._s || this, c, e, f, g = a, l = a, g = a;
                        null == b.document && (b.document = a);
                        null == v["~"] && (v["~"] = a);
                        g = v["~"];
                        if ((c = g["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return g["$[]"](0)["$[]"](x(1,
                            -1, !1));
                        if ((c = null == (e = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : e) !== a && (!c._isBoolean || !0 == c)) {
                            if (g["$[]"](1)["$=="](""))g["$[]="](1, a);
                            if (g["$[]"](2)["$=="](""))g["$[]="](2, a);
                            if (g["$[]"](4)["$=="](""))g["$[]="](4, a)
                        }
                        l = !1 !== (c = g["$[]"](1)) && c !== a ? c : g["$[]"](3);
                        g = !1 !== (c = !1 !== (e = g["$[]"](2)) && e !== a ? e : g["$[]"](4)) && c !== a ? c : "[" + l + "]";
                        ((c = b.document.$references()["$[]"]("ids")["$has_key?"](l)) === a || c._isBoolean && !0 != c) && (c = (e = h.Debug).$debug, c._p = (f = function () {
                            return"Missing reference for anchor " +
                                l
                        }, f._s = b, f), c).call(e);
                        return h.Inline.$new(b, "anchor", g, r(["type", "target"], {type: "ref", target: l})).$convert()
                    }, k._s = this, k), c).call(p, h.InlineAnchorRx));
                    return n
                };
            q.$sub_inline_xrefs = function (n, b) {
                var c, f, p, e;
                null == b && (b = a);
                (c = !1 !== (f = !1 !== (p = b["$!"]()) && p !== a ? p : b["$[]"]("macroish")) && f !== a ? f : n["$include?"]("&lt;&lt;")) === a || c._isBoolean && !0 != c || (n = (c = (f = n).$gsub, c._p = (e = function () {
                    var b = e._s || this, c, f, p, n, G = a, R = a, m = a, q = R = G = a, y = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    G = v["~"];
                    if ((c = G["$[]"](0)["$start_with?"]("\\")) !== a && (!c._isBoolean || !0 == c))return G["$[]"](0)["$[]"](x(1, -1, !1));
                    if ((c = null == (f = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : f) !== a && (!c._isBoolean || !0 == c) && G["$[]"](1)["$=="](""))G["$[]="](1, a);
                    if ((c = G["$[]"](1)) === a || c._isBoolean && !0 != c) {
                        if (R = G["$[]"](2), (c = G["$[]"](3)["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c)m = G["$[]"](3)
                    } else c = d.to_ary((f = (p = G["$[]"](1).$split(",", 2)).$map, f._p = (n = function (b) {
                        null == b && (b = a);
                        return b.$strip()
                    }, n._s =
                        b, n), f).call(p)), R = null == c[0] ? a : c[0], m = null == c[1] ? a : c[1], R = R.$sub(h.DoubleQuotedRx, "\\2"), m = (c = m["$nil_or_empty?"]()) === a || c._isBoolean && !0 != c ? m.$sub(h.DoubleQuotedMultiRx, "\\2") : a;
                    (c = R["$include?"]("#")) === a || c._isBoolean && !0 != c ? G = a : (c = d.to_ary(R.$split("#")), G = null == c[0] ? a : c[0], R = null == c[1] ? a : c[1]);
                    (c = G["$!"]()) === a || c._isBoolean && !0 != c ? (G = h.Helpers.$rootname(G), (c = !1 !== (f = b.document.$attributes()["$[]"]("docname")["$=="](G)) && f !== a ? f : b.document.$references()["$[]"]("includes")["$include?"](G)) ===
                        a || c._isBoolean && !0 != c ? (q = !1 !== R && R !== a ? "" + G + "#" + R : G, G = "" + b.document.$attributes()["$[]"]("relfileprefix") + G + b.document.$attributes().$fetch("outfilesuffix", ".html"), y = !1 !== R && R !== a ? "" + G + "#" + R : G) : (q = R, G = a, y = "#" + R)) : (q = R, y = "#" + R);
                    return h.Inline.$new(b, "anchor", m, r(["type", "target", "attributes"], {type: "xref", target: y, attributes: r(["path", "fragment", "refid"], {path: G, fragment: R, refid: q})})).$convert()
                }, e._s = this, e), c).call(f, h.XrefInlineMacroRx));
                return n
            };
            q.$sub_callouts = function (d) {
                var b, c;
                return(b =
                    d.$gsub, b._p = (c = function () {
                    var b = c._s || this, p = a;
                    null == b.document && (b.document = a);
                    null == v["~"] && (v["~"] = a);
                    p = v["~"];
                    return p["$[]"](1)["$=="]("\\") ? p["$[]"](0).$sub("\\", "") : h.Inline.$new(b, "callout", p["$[]"](3), r(["id"], {id: b.document.$callouts().$read_next_id()})).$convert()
                }, c._s = this, c), b).call(d, h.CalloutConvertRx)
            };
            q.$sub_post_replacements = function (d) {
                var b, c, f, p, e = a, g = a;
                null == this.document && (this.document = a);
                null == this.attributes && (this.attributes = a);
                if ((b = !1 !== (c = this.document.$attributes()["$has_key?"]("hardbreaks")) &&
                    c !== a ? c : this.attributes["$has_key?"]("hardbreaks-option")) === a || b._isBoolean && !0 != b)return(b = d["$include?"]("+")) === a || b._isBoolean && !0 != b ? d : (b = d.$gsub, b._p = (p = function () {
                    var b = p._s || this;
                    null == v["~"] && (v["~"] = a);
                    return h.Inline.$new(b, "break", v["~"]["$[]"](1), r(["type"], {type: "line"})).$convert()
                }, p._s = this, p), b).call(d, h.LineBreakRx);
                e = d.$split(h.EOL);
                if (e.$size()["$=="](1))return d;
                g = e.$pop();
                return(b = (c = e).$map, b._p = (f = function (b) {
                    var c = f._s || this;
                    null == b && (b = a);
                    return h.Inline.$new(c, "break",
                        b.$rstrip().$chomp(h.LINE_BREAK), r(["type"], {type: "line"})).$convert()
                }, f._s = this, f), b).call(c).$push(g)["$*"](h.EOL)
            };
            q.$convert_quoted_text = function (d, b, c) {
                var f, p, e = a, g = a, l = e = a, e = a;
                if ((f = d["$[]"](0)["$start_with?"]("\\")) !== a && (!f._isBoolean || !0 == f)) {
                    if ((f = (p = c["$=="]("constrained")) ? (g = d["$[]"](2))["$nil_or_empty?"]()["$!"]() : p) === a || f._isBoolean && !0 != f)return d["$[]"](0)["$[]"](x(1, -1, !1));
                    e = "[" + g + "]"
                }
                if (c["$=="]("constrained")) {
                    if (!1 !== e && e !== a)return"" + e + h.Inline.$new(this, "quoted", d["$[]"](3),
                        r(["type"], {type: b})).$convert();
                    e = this.$parse_quoted_text_attributes(d["$[]"](2));
                    l = !1 !== e && e !== a ? e.$delete("id") : a;
                    return"" + d["$[]"](1) + h.Inline.$new(this, "quoted", d["$[]"](3), r(["type", "id", "attributes"], {type: b, id: l, attributes: e})).$convert()
                }
                e = this.$parse_quoted_text_attributes(d["$[]"](1));
                l = !1 !== e && e !== a ? e.$delete("id") : a;
                return h.Inline.$new(this, "quoted", d["$[]"](2), r(["type", "id", "attributes"], {type: b, id: l, attributes: e})).$convert()
            };
            q.$parse_quoted_text_attributes = function (n) {
                var b, c, f =
                    a, p = a, e = a, e = f = a;
                if (!1 === n || n === a)return a;
                if ((b = n["$empty?"]()) !== a && (!b._isBoolean || !0 == b))return r([], {});
                (b = n["$include?"]("{")) === a || b._isBoolean && !0 != b || (n = this.$sub_attributes(n));
                n = n.$strip();
                (b = n["$include?"](",")) === a || b._isBoolean && !0 != b || (b = d.to_ary(n.$split(",", 2)), n = null == b[0] ? a : b[0]);
                if ((b = n["$empty?"]()) === a || b._isBoolean && !0 != b) {
                    if ((b = !1 !== (c = n["$start_with?"](".")) && c !== a ? c : n["$start_with?"]("#")) === a || b._isBoolean && !0 != b)return r(["role"], {role: n});
                    f = n.$split("#", 2);
                    f.$length()["$>"](1) ?
                        (b = d.to_ary(f["$[]"](1).$split(".")), p = null == b[0] ? a : b[0], e = t.call(b, 1)) : (p = a, e = []);
                    f = (b = f["$[]"](0)["$empty?"]()) === a || b._isBoolean && !0 != b ? f["$[]"](0).$split(".") : [];
                    f.$length()["$>"](1) && f.$shift();
                    e.$length()["$>"](0) && f.$concat(e);
                    e = r([], {});
                    if (!1 !== p && p !== a)e["$[]="]("id", p);
                    if ((b = f["$empty?"]()) === a || b._isBoolean && !0 != b)e["$[]="]("role", f["$*"](" "));
                    return e
                }
                return r([], {})
            };
            q.$parse_attributes = function (d, b, c) {
                var f, p = a, e = a;
                null == this.document && (this.document = a);
                null == b && (b = ["role"]);
                null ==
                    c && (c = r([], {}));
                if (!1 === d || d === a)return a;
                if ((f = d["$empty?"]()) !== a && (!f._isBoolean || !0 == f))return r([], {});
                (f = c["$[]"]("sub_input")) === a || f._isBoolean && !0 != f || (d = this.document.$sub_attributes(d));
                (f = c["$[]"]("unescape_input")) === a || f._isBoolean && !0 != f || (d = this.$unescape_bracketed_text(d));
                p = a;
                (f = c.$fetch("sub_result", !0)) === a || f._isBoolean && !0 != f || (p = this);
                return(f = e = c["$[]"]("into")) === a || f._isBoolean && !0 != f ? h.AttributeList.$new(d, p).$parse(b) : h.AttributeList.$new(d, p).$parse_into(e, b)
            };
            q.$unescape_bracketed_text =
                function (d) {
                    var b;
                    return(b = d["$empty?"]()) === a || b._isBoolean && !0 != b ? d.$strip().$tr(h.EOL, " ").$gsub("\\]", "]") : ""
                };
            q.$normalize_string = function (d, b) {
                var c;
                null == b && (b = !1);
                return(c = d["$empty?"]()) === a || c._isBoolean && !0 != c ? !1 !== b && b !== a ? this.$unescape_brackets(d.$strip().$tr(h.EOL, " ")) : d.$strip().$tr(h.EOL, " ") : ""
            };
            q.$unescape_brackets = function (d) {
                var b;
                return(b = d["$empty?"]()) === a || b._isBoolean && !0 != b ? d.$gsub("\\]", "]") : ""
            };
            q.$split_simple_csv = function (d) {
                var b, c, f, p, e = a, g = a, l = a;
                (b = d["$empty?"]()) ===
                    a || b._isBoolean && !0 != b ? (b = d["$include?"]('"')) === a || b._isBoolean && !0 != b ? e = (b = (f = d.$split(",")).$map, b._p = (p = function (b) {
                    null == b && (b = a);
                    return b.$strip()
                }, p._s = this, p), b).call(f) : (e = [], g = [], l = !1, (b = d.$each_char, b._p = (c = function (b) {
                    var c = a;
                    null == b && (b = a);
                    return function () {
                        c = b;
                        if (","["$==="](c)) {
                            if (!1 !== l && l !== a)return g.$push(b);
                            e["$<<"](g.$join().$strip());
                            return g = []
                        }
                        return'"'["$==="](c) ? l = l["$!"]() : g.$push(b)
                    }()
                }, c._s = this, c), b).call(d), e["$<<"](g.$join().$strip())) : e = [];
                return e
            };
            q.$resolve_subs =
                function (d, b, c, f) {
                    var p, e, g, l = a, k = a, s = a, z = a;
                    null == b && (b = "block");
                    null == c && (c = a);
                    null == f && (f = a);
                    if ((p = d["$nil_or_empty?"]()) !== a && (!p._isBoolean || !0 == p))return[];
                    l = [];
                    k = !1 !== c && c !== a ? a : !1;
                    (p = (e = d.$split(",")).$each, p._p = (g = function (e) {
                        var d = g._s || this, p, s, n, z = a, m = a, q = z = z = m = a, r = a;
                        null == e && (e = a);
                        z = e.$strip();
                        if ((p = k["$=="](!1)["$!"]()) !== a && (!p._isBoolean || !0 == p)) {
                            if ((m = z.$chr())["$=="]("+"))m = "append", z = z["$[]"](x(1, -1, !1)); else if (m["$=="]("-"))m = "remove", z = z["$[]"](x(1, -1, !1)); else if ((p = z["$end_with?"]("+")) ===
                                a || p._isBoolean && !0 != p) {
                                if (!1 !== k && k !== a)return d.$warn("asciidoctor: WARNING: invalid entry in substitution modification group" + (!1 !== f && f !== a ? " for " : a) + f + ": " + z), a;
                                m = a
                            } else m = "prepend", z = z.$chop();
                            (p = k["$nil?"]()) === a || p._isBoolean && !0 != p || (!1 !== m && m !== a ? (l = c.$dup(), k = !0) : k = !1)
                        }
                        z = z.$to_sym();
                        (p = (s = b["$=="]("inline")) ? !1 !== (n = z["$=="]("verbatim")) && n !== a ? n : z["$=="]("v") : s) === a || p._isBoolean && !0 != p ? (p = h.COMPOSITE_SUBS["$has_key?"](z)) === a || p._isBoolean && !0 != p ? (p = (s = (n = b["$=="]("inline")) ? z.$length()["$=="](1) :
                            n, !1 !== s && s !== a ? h.SUB_SYMBOLS["$has_key?"](z) : s)) === a || p._isBoolean && !0 != p ? z = [z] : (z = h.SUB_SYMBOLS["$[]"](z), z = (p = q = h.COMPOSITE_SUBS["$[]"](z)) === a || p._isBoolean && !0 != p ? [z] : q) : z = h.COMPOSITE_SUBS["$[]"](z) : z = ["specialcharacters"];
                        !1 !== k && k !== a ? (r = m, e = "append"["$==="](r) ? l = l["$+"](z) : "prepend"["$==="](r) ? l = z["$+"](l) : "remove"["$==="](r) ? l = l["$-"](z) : a) : e = l = l["$+"](z);
                        return e
                    }, g._s = this, g), p).call(e);
                    s = l["$&"](h.SUB_OPTIONS["$[]"](b));
                    if ((p = l["$-"](s)["$empty?"]()) === a || p._isBoolean && !0 != p)z = l["$-"](s),
                        this.$warn("asciidoctor: WARNING: invalid substitution type" + (z.$size()["$>"](1) ? "s" : "") + (!1 !== f && f !== a ? " for " : a) + f + ": " + z["$*"](", "));
                    return s
                };
            q.$resolve_block_subs = function (a, b, c) {
                return this.$resolve_subs(a, "block", b, c)
            };
            q.$resolve_pass_subs = function (d) {
                return this.$resolve_subs(d, "inline", a, "passthrough macro")
            };
            q.$highlight_source = function (n, b, c) {
                var f, p, e, g, l, k = a, s = a, z = a, G = a, m = a, q = a, Z = a, y = q = a, t = a;
                null == this.document && (this.document = a);
                null == this.passthroughs && (this.passthroughs = a);
                null ==
                    c && (c = a);
                !1 !== (f = c) && f !== a ? f : c = this.document.$attributes()["$[]"]("source-highlighter");
                h.Helpers.$require_library(c, c["$=="]("pygments") ? "pygments.rb" : c);
                k = r([], {});
                s = 0;
                z = !1;
                !1 !== b && b !== a && (G = -1, n = (f = (p = n.$split(h.EOL)).$map, f._p = (e = function (b) {
                    var c = e._s || this, f, g, l;
                    null == b && (b = a);
                    s = s["$+"](1);
                    return(f = (g = b).$gsub, f._p = (l = function () {
                        var b, c, e, f = a;
                        null == v["~"] && (v["~"] = a);
                        f = v["~"];
                        if (f["$[]"](1)["$=="]("\\"))return f["$[]"](0).$sub("\\", "");
                        (b = s, c = k, !1 !== (e = c["$[]"](b)) && e !== a ? e : c["$[]="](b, []))["$<<"](f["$[]"](3));
                        G = s;
                        return a
                    }, l._s = c, l), f).call(g, h.CalloutScanRx)
                }, e._s = this, e), f).call(p)["$*"](h.EOL), z = G["$=="](s));
                m = a;
                q = c;
                "coderay"["$==="](q) ? Z = (null == (f = d.Object._scope.CodeRay) ? d.cm("CodeRay") : f)._scope.Duo["$[]"](this.$attr("language", "text").$to_sym(), "html", r(["css", "line_numbers", "line_number_anchors"], {css: (!1 !== (f = this.document.$attributes()["$[]"]("coderay-css")) && f !== a ? f : "class").$to_sym(), line_numbers: m = (f = this["$attr?"]("linenums")) === a || f._isBoolean && !0 != f ? a : (!1 !== (f = this.document.$attributes()["$[]"]("coderay-linenums-mode")) &&
                    f !== a ? f : "table").$to_sym(), line_number_anchors: !1})).$highlight(n) : "pygments"["$==="](q) && ((f = q = (null == (g = d.Object._scope.Pygments) ? d.cm("Pygments") : g)._scope.Lexer["$[]"](this.$attr("language"))) === a || f._isBoolean && !0 != f ? Z = n : (y = r(["cssclass", "classprefix", "nobackground"], {cssclass: "pyhl", classprefix: "tok-", nobackground: !0}), (!1 !== (f = this.document.$attributes()["$[]"]("pygments-css")) && f !== a ? f : "class")["$=="]("class") || (y["$[]="]("noclasses", !0), y["$[]="]("style", !1 !== (f = this.document.$attributes()["$[]"]("pygments-style")) &&
                    f !== a ? f : h.Stylesheets._scope.DEFAULT_PYGMENTS_STYLE)), (f = this["$attr?"]("linenums")) === a || f._isBoolean && !0 != f ? (y["$[]="]("nowrap", !0), Z = q.$highlight(n, r(["options"], {options: y}))) : Z = y["$[]="]("linenos", !1 !== (f = this.document.$attributes()["$[]"]("pygments-linenums-mode")) && f !== a ? f : "table")["$=="]("table") ? q.$highlight(n, r(["options"], {options: y})).$sub(/<div class="pyhl">(.*)<\/div>/m, "\\1").$gsub(/<pre[^>]*>(.*?)<\/pre>\s*/m, "\\1") : q.$highlight(n, r(["options"], {options: y})).$sub(/<div class="pyhl"><pre[^>]*>(.*?)<\/pre><\/div>/m,
                    "\\1")));
                if ((f = this.passthroughs["$empty?"]()) === a || f._isBoolean && !0 != f)Z = Z.$gsub(h.PASS_MATCH_HI, "" + h.PASS_START + "\\1" + h.PASS_END);
                return(f = !1 !== (g = b["$!"]()) && g !== a ? g : k["$empty?"]()) === a || f._isBoolean && !0 != f ? (s = 0, t = m["$=="]("table")["$!"](), (f = (g = Z.$split(h.EOL)).$map, f._p = (l = function (b) {
                    var c = l._s || this, e, f, g, d, p = a, n = a, G = a, p = a;
                    null == c.document && (c.document = a);
                    null == b && (b = a);
                    if (!1 === t || t === a) {
                        if ((e = b["$include?"]('<td class="code">')) === a || e._isBoolean && !0 != e)return b;
                        t = !0
                    }
                    s = s["$+"](1);
                    if ((e = p =
                        k.$delete(s)) === a || e._isBoolean && !0 != e)return b;
                    n = a;
                    (e = (f = (g = !1 !== z && z !== a) ? k["$empty?"]() : g, !1 !== f && f !== a ? G = b.$index("</pre>") : f)) === a || e._isBoolean && !0 != e || (n = b["$[]"](x(G, -1, !1)), b = b["$[]"](x(0, G, !0)));
                    if (p.$size()["$=="](1))return"" + b + h.Inline.$new(c, "callout", p["$[]"](0), r(["id"], {id: c.document.$callouts().$read_next_id()})).$convert() + n;
                    p = (e = (f = p).$map, e._p = (d = function (b) {
                        var c = d._s || this;
                        null == c.document && (c.document = a);
                        null == b && (b = a);
                        return h.Inline.$new(c, "callout", b, r(["id"], {id: c.document.$callouts().$read_next_id()})).$convert()
                    },
                        d._s = c, d), e).call(f)["$*"](" ");
                    return"" + b + p + n
                }, l._s = this, l), f).call(g)["$*"](h.EOL)) : Z
            };
            q.$lock_in_subs = function () {
                var d, b, c, f, p, e, g = a, l = g = a, k = a;
                null == this.default_subs && (this.default_subs = a);
                null == this.content_model && (this.content_model = a);
                null == this.context && (this.context = a);
                null == this.attributes && (this.attributes = a);
                null == this.style && (this.style = a);
                null == this.document && (this.document = a);
                null == this.subs && (this.subs = a);
                if ((d = this.default_subs) === a || d._isBoolean && !0 != d)if (g = this.content_model, "simple"["$==="](g))g =
                    h.SUBS["$[]"]("normal"); else if ("verbatim"["$==="](g))g = (d = !1 !== (b = this.context["$=="]("listing")) && b !== a ? b : (c = this.context["$=="]("literal")) ? this["$option?"]("listparagraph")["$!"]() : c) === a || d._isBoolean && !0 != d ? this.context["$=="]("verse") ? h.SUBS["$[]"]("normal") : h.SUBS["$[]"]("basic") : h.SUBS["$[]"]("verbatim"); else if ("raw"["$==="](g))g = h.SUBS["$[]"]("pass"); else return a; else g = this.default_subs;
                (d = l = this.attributes["$[]"]("subs")) === a || d._isBoolean && !0 != d ? this.subs = g.$dup() : this.subs = this.$resolve_block_subs(l,
                    g, this.context);
                return(d = (b = (c = (f = (p = this.context["$=="]("listing")) ? this.style["$=="]("source") : p, !1 !== f && f !== a ? this.document["$basebackend?"]("html") : f), !1 !== c && c !== a ? !1 !== (f = (k = this.document.$attributes()["$[]"]("source-highlighter"))["$=="]("coderay")) && f !== a ? f : k["$=="]("pygments") : c), !1 !== b && b !== a ? this["$attr?"]("language") : b)) === a || d._isBoolean && !0 != d ? a : this.subs = (d = (b = this.subs).$map, d._p = (e = function (b) {
                    null == b && (b = a);
                    return b["$=="]("specialcharacters") ? "highlight" : b
                }, e._s = this, e), d).call(b)
            };
            d.donate(m, "$apply_subs $apply_normal_subs $apply_title_subs $apply_header_subs $extract_passthroughs $restore_passthroughs $sub_specialcharacters $sub_specialchars $sub_quotes $sub_replacements $do_replacement $sub_attributes $sub_macros $sub_inline_anchors $sub_inline_xrefs $sub_callouts $sub_post_replacements $convert_quoted_text $parse_quoted_text_attributes $parse_attributes $unescape_bracketed_text $normalize_string $unescape_brackets $split_simple_csv $resolve_subs $resolve_block_subs $resolve_pass_subs $highlight_source $lock_in_subs".split(" "))
        })(w(A,
                "Asciidoctor"))
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2, r = d.range;
    return function (v) {
        (function (v, $super) {
            function m() {
            }

            var q = m = t(v, $super, "AbstractNode", m), h = q._proto, n = q._scope;
            h.document = h.attributes = h.path_resolver = h.style = a;
            q.$include(n.Substitutors);
            q.$attr_reader("parent");
            q.$attr_reader("document");
            q.$attr_reader("context");
            q.$attr_reader("node_name");
            q.$attr_accessor("id");
            q.$attr_reader("attributes");
            h.$initialize = function (b, c) {
                var f;
                c["$=="]("document") ? (this.parent = a, this.document = b) : (f =
                    this.parent = b) === a || f._isBoolean && !0 != f ? this.document = a : this.document = b.$document();
                this.context = c;
                this.node_name = c.$to_s();
                this.attributes = w([], {});
                return this.passthroughs = []
            };
            h["$parent="] = function (b) {
                this.parent = b;
                this.document = b.$document();
                return a
            };
            h["$inline?"] = function () {
                var a;
                return this.$raise(null == (a = d.Object._scope.NotImplementedError) ? d.cm("NotImplementedError") : a)
            };
            h["$block?"] = function () {
                var a;
                return this.$raise(null == (a = d.Object._scope.NotImplementedError) ? d.cm("NotImplementedError") :
                    a)
            };
            h.$attr = function (b, c, f) {
                var p, e;
                null == c && (c = a);
                null == f && (f = !0);
                (p = b["$is_a?"](null == (e = d.Object._scope.Symbol) ? d.cm("Symbol") : e)) === a || p._isBoolean && !0 != p || (b = b.$to_s());
                this["$=="](this.document) && (f = !1);
                return!1 !== f && f !== a ? !1 !== (p = !1 !== (e = this.attributes["$[]"](b)) && e !== a ? e : this.document.$attributes()["$[]"](b)) && p !== a ? p : c : !1 !== (p = this.attributes["$[]"](b)) && p !== a ? p : c
            };
            h["$attr?"] = function (b, c, f) {
                var p, e;
                null == c && (c = a);
                null == f && (f = !0);
                (p = b["$is_a?"](null == (e = d.Object._scope.Symbol) ? d.cm("Symbol") :
                    e)) === a || p._isBoolean && !0 != p || (b = b.$to_s());
                this["$=="](this.document) && (f = !1);
                return(p = c["$nil?"]()) === a || p._isBoolean && !0 != p ? !1 !== f && f !== a ? c["$=="](!1 !== (p = this.attributes["$[]"](b)) && p !== a ? p : this.document.$attributes()["$[]"](b)) : c["$=="](this.attributes["$[]"](b)) : !1 !== (p = this.attributes["$has_key?"](b)) && p !== a ? p : (e = !1 !== f && f !== a) ? this.document.$attributes()["$has_key?"](b) : e
            };
            h.$set_attr = function (b, c, f) {
                var d, e;
                null == f && (f = a);
                if ((d = f["$nil?"]()) === a || d._isBoolean && !0 != d) {
                    if ((d = !1 !== (e = f) && e !==
                        a ? e : this.attributes["$key?"](b)["$!"]()) === a || d._isBoolean && !0 != d)return!1;
                    this.attributes["$[]="](b, c);
                    return!0
                }
                this.attributes["$[]="](b, c);
                return!0
            };
            h.$set_option = function (b) {
                var c;
                if ((c = this.attributes["$has_key?"]("options")) === a || c._isBoolean && !0 != c)this.attributes["$[]="]("options", b); else this.attributes["$[]="]("options", "" + this.attributes["$[]"]("options") + "," + b);
                return this.attributes["$[]="]("" + b + "-option", "")
            };
            h["$option?"] = function (a) {
                return this.attributes["$has_key?"]("" + a + "-option")
            };
            h.$update_attributes = function (b) {
                this.attributes.$update(b);
                return a
            };
            h.$converter = function () {
                return this.document.$converter()
            };
            h["$role?"] = function (b) {
                var c;
                null == b && (b = a);
                return(c = b["$nil?"]()) === a || c._isBoolean && !0 != c ? b["$=="](!1 !== (c = this.attributes["$[]"]("role")) && c !== a ? c : this.document.$attributes()["$[]"]("role")) : !1 !== (c = this.attributes["$has_key?"]("role")) && c !== a ? c : this.document.$attributes()["$has_key?"]("role")
            };
            h.$role = function () {
                var b;
                return!1 !== (b = this.attributes["$[]"]("role")) &&
                    b !== a ? b : this.document.$attributes()["$[]"]("role")
            };
            h["$has_role?"] = function (b) {
                var c, f, d = a;
                return(c = d = !1 !== (f = this.attributes["$[]"]("role")) && f !== a ? f : this.document.$attributes()["$[]"]("role")) === a || c._isBoolean && !0 != c ? !1 : d.$split(" ")["$include?"](b)
            };
            h.$roles = function () {
                var b, c, f = a;
                return(b = f = !1 !== (c = this.attributes["$[]"]("role")) && c !== a ? c : this.document.$attributes()["$[]"]("role")) === a || b._isBoolean && !0 != b ? [] : f.$split(" ")
            };
            h["$reftext?"] = function () {
                var b;
                return!1 !== (b = this.attributes["$has_key?"]("reftext")) &&
                    b !== a ? b : this.document.$attributes()["$has_key?"]("reftext")
            };
            h.$reftext = function () {
                var b;
                return!1 !== (b = this.attributes["$[]"]("reftext")) && b !== a ? b : this.document.$attributes()["$[]"]("reftext")
            };
            h.$icon_uri = function (b) {
                var c;
                return(c = this["$attr?"]("icon")) === a || c._isBoolean && !0 != c ? this.$image_uri("" + b + "." + this.document.$attr("icontype", "png"), "iconsdir") : this.$image_uri(this.$attr("icon"), a)
            };
            h.$media_uri = function (b, c) {
                var f, d;
                null == c && (c = "imagesdir");
                return(f = (d = b["$include?"](":"), !1 !== d && d !== a ?
                    n.UriSniffRx["$=~"](b) : d)) === a || f._isBoolean && !0 != f ? (f = (d = !1 !== c && c !== a) ? this["$attr?"](c) : d) === a || f._isBoolean && !0 != f ? this.$normalize_web_path(b) : this.$normalize_web_path(b, this.document.$attr(c)) : b
            };
            h.$image_uri = function (b, c) {
                var f, d;
                null == c && (c = "imagesdir");
                return(f = (d = b["$include?"](":"), !1 !== d && d !== a ? n.UriSniffRx["$=~"](b) : d)) === a || f._isBoolean && !0 != f ? (f = (d = this.document.$safe()["$<"](n.SafeMode._scope.SECURE)) ? this.document["$attr?"]("data-uri") : d) === a || f._isBoolean && !0 != f ? (f = (d = !1 !== c && c !==
                    a) ? this["$attr?"](c) : d) === a || f._isBoolean && !0 != f ? this.$normalize_web_path(b) : this.$normalize_web_path(b, this.document.$attr(c)) : this.$generate_data_uri(b, c) : b
            };
            h.$generate_data_uri = function (b, c) {
                var f, p, e, g, l = a, k = a, s = l = a;
                null == c && (c = a);
                l = (null == (f = d.Object._scope.File) ? d.cm("File") : f).$extname(b)["$[]"](r(1, -1, !1));
                k = "image/"["$+"](l);
                l["$=="]("svg") && (k = "" + k + "+xml");
                l = !1 !== c && c !== a ? this.$normalize_system_path(b, this.document.$attr(c), a, w(["target_name"], {target_name: "image"})) : this.$normalize_system_path(b);
                if ((f = (null == (p = d.Object._scope.File) ? d.cm("File") : p)["$readable?"](l)) === a || f._isBoolean && !0 != f)return this.$warn("asciidoctor: WARNING: image to embed not found or not readable: " + l), "data:" + k + ":base64,";
                s = a;
                s = (f = (null == (p = d.Object._scope.IO) ? d.cm("IO") : p)["$respond_to?"]("binread")) === a || f._isBoolean && !0 != f ? (f = (p = null == (g = d.Object._scope.File) ? d.cm("File") : g).$open, f._p = (e = function (b) {
                    null == b && (b = a);
                    return b.$read()
                }, e._s = this, e), f).call(p, l, "rb") : (null == (f = d.Object._scope.IO) ? d.cm("IO") : f).$binread(l);
                return"data:" + k + ";base64," + (null == (f = d.Object._scope.Base64) ? d.cm("Base64") : f).$encode64(s).$delete(n.EOL)
            };
            h.$read_asset = function (b, c) {
                var f, p;
                null == c && (c = !1);
                return(f = (null == (p = d.Object._scope.File) ? d.cm("File") : p)["$readable?"](b)) === a || f._isBoolean && !0 != f ? (!1 !== c && c !== a && this.$warn("asciidoctor: WARNING: file does not exist or cannot be read: " + b), a) : (null == (f = d.Object._scope.File) ? d.cm("File") : f).$read(b).$chomp()
            };
            h.$normalize_web_path = function (b, c) {
                var f;
                null == c && (c = a);
                return(!1 !== (f = this.path_resolver) &&
                    f !== a ? f : this.path_resolver = n.PathResolver.$new()).$web_path(b, c)
            };
            h.$normalize_system_path = function (b, c, f, d) {
                var e, g;
                null == c && (c = a);
                null == f && (f = a);
                null == d && (d = w([], {}));
                (e = c["$nil?"]()) === a || e._isBoolean && !0 != e || (c = this.document.$base_dir());
                (e = (g = f["$nil?"](), !1 !== g && g !== a ? this.document.$safe()["$>="](n.SafeMode._scope.SAFE) : g)) === a || e._isBoolean && !0 != e || (f = this.document.$base_dir());
                return(!1 !== (e = this.path_resolver) && e !== a ? e : this.path_resolver = n.PathResolver.$new()).$system_path(b, c, f, d)
            };
            h.$normalize_asset_path =
                function (b, c, f) {
                    null == c && (c = "path");
                    null == f && (f = !0);
                    return this.$normalize_system_path(b, this.document.$base_dir(), a, w(["target_name", "recover"], {target_name: c, recover: f}))
                };
            h.$relative_path = function (b) {
                var c;
                return(!1 !== (c = this.path_resolver) && c !== a ? c : this.path_resolver = n.PathResolver.$new()).$relative_path(b, this.document.$base_dir())
            };
            return(h.$list_marker_keyword = function (b) {
                var c;
                null == b && (b = a);
                return n.ORDERED_LIST_KEYWORDS["$[]"](!1 !== (c = b) && c !== a ? c : this.style)
            }, a) && "list_marker_keyword"
        })(u(v,
                "Asciidoctor"), null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.klass;
    return function (r) {
        r = t(r, "Asciidoctor");
        (function (r, $super) {
            function t() {
            }

            var m = t = w(r, $super, "AbstractBlock", t), q = m._proto, h = m._scope, n;
            q.document = q.attributes = q.blocks = q.subs = q.title = q.subbed_title = q.caption = q.context = q.next_section_index = q.next_section_number = a;
            m.$attr_accessor("content_model");
            m.$attr_reader("subs");
            m.$attr_reader("blocks");
            m.$attr_accessor("level");
            m.$attr_writer("title");
            m.$attr_accessor("style");
            m.$attr_accessor("caption");
            q.$initialize = n = function (b, c) {
                var f = u.call(arguments, 0), p, e, g = n._p;
                n._p = null;
                d.find_super_dispatcher(this, "initialize", n, g).apply(this, f);
                this.content_model = "compound";
                this.subs = [];
                this.default_subs = a;
                this.blocks = [];
                this.style = this.caption = this.title = this.id = a;
                this.level = c["$=="]("document") ? 0 : (p = (e = !1 !== b && b !== a) ? c["$=="]("section")["$!"]() : e) === a || p._isBoolean && !0 != p ? a : b.$level();
                this.next_section_index = 0;
                return this.next_section_number = 1
            };
            q["$block?"] = function () {
                return!0
            };
            q["$inline?"] = function () {
                return!1
            };
            q["$context="] = function (a) {
                this.context = a;
                return this.node_name = a.$to_s()
            };
            q.$convert = function () {
                this.document.$playback_attributes(this.attributes);
                return this.$converter().$convert(this)
            };
            d.defn(m, "$render", q.$convert);
            q.$content = function () {
                var b, c, f;
                return(b = (c = this.blocks).$map, b._p = (f = function (b) {
                    null == b && (b = a);
                    return b.$convert()
                }, f._s = this, f), b).call(c)["$*"](h.EOL)
            };
            q["$sub?"] = function (a) {
                return this.subs["$include?"](a)
            };
            q["$title?"] = function () {
                return this.title["$nil_or_empty?"]()["$!"]()
            };
            q.$title = function () {
                var b, c;
                return(b = (c = this.subbed_title, null != c && c !== a) ? "instance-variable" : a) === a || b._isBoolean && !0 != b ? (b = this.title) === a || b._isBoolean && !0 != b ? this.title : this.subbed_title = this.$apply_title_subs(this.title) : this.subbed_title
            };
            q.$captioned_title = function () {
                return"" + this.caption + this.$title()
            };
            q["$blocks?"] = function () {
                return this.blocks["$empty?"]()["$!"]()
            };
            q["$<<"] = function (a) {
                return this.blocks["$<<"](a)
            };
            q.$sections = function () {
                var b, c, f;
                return(b = (c = this.blocks).$select, b._p = (f =
                    function (b) {
                        null == b && (b = a);
                        return b.$context()["$=="]("section")
                    }, f._s = this, f), b).call(c)
            };
            q.$remove_sub = function (b) {
                this.subs.$delete(b);
                return a
            };
            q.$assign_caption = function (b, c) {
                var f, d, e = a, g = e = e = a;
                null == b && (b = a);
                null == c && (c = a);
                if ((f = !1 !== (d = this["$title?"]()) && d !== a ? d : this.caption["$!"]()) === a || f._isBoolean && !0 != f)return a;
                !1 !== b && b !== a ? this.caption = b : (f = e = this.document.$attributes()["$[]"]("caption")) === a || f._isBoolean && !0 != f ? (f = this["$title?"]()) === a || f._isBoolean && !0 != f || (!1 !== (f = c) && f !== a ?
                    f : c = this.context.$to_s(), e = "" + c + "-caption", (f = e = this.document.$attributes()["$[]"](e)) === a || f._isBoolean && !0 != f || (g = this.document.$counter_increment("" + c + "-number", this), this.caption = "" + e + " " + g + ". ")) : this.caption = e;
                return a
            };
            q.$assign_index = function (b) {
                var c, f, d, e, g = a, l = a;
                b["$index="](this.next_section_index);
                this.next_section_index = this.next_section_index["$+"](1);
                if (b.$sectname()["$=="]("appendix")) {
                    g = this.document.$counter("appendix-number", "A");
                    if ((c = b.$numbered()) !== a && (!c._isBoolean || !0 == c))b["$number="](g);
                    return(c = (l = this.document.$attr("appendix-caption", ""))["$=="]("")["$!"]()) === a || c._isBoolean && !0 != c ? b["$caption="]("" + g + ". ") : b["$caption="]("" + l + " " + g + ": ")
                }
                return(c = b.$numbered()) === a || c._isBoolean && !0 != c ? a : (c = (f = !1 !== (d = b.$level()["$=="](1)) && d !== a ? d : (e = b.$level()["$=="](0)) ? b.$special() : e, !1 !== f && f !== a ? this.document.$doctype()["$=="]("book") : f)) === a || c._isBoolean && !0 != c ? (b["$number="](this.next_section_number), this.next_section_number = this.next_section_number["$+"](1)) : b["$number="](this.document.$counter("chapter-number",
                    1))
            };
            return(q.$reindex_sections = function () {
                var b, c, f;
                this.next_section_number = this.next_section_index = 0;
                return(b = (c = this.blocks).$each, b._p = (f = function (b) {
                    var c = f._s || this;
                    null == b && (b = a);
                    return b.$context()["$=="]("section") ? (c.$assign_index(b), b.$reindex_sections()) : a
                }, f._s = this, f), b).call(c)
            }, a) && "reindex_sections"
        })(r, r._scope.AbstractNode)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (r) {
        (function (r, $super) {
            function u() {
            }

            var m = u = t(r, $super, "AttributeList", u), q = m._proto, h = m._scope;
            q.attributes = q.scanner = q.delimiter = q.block = q.delimiter_skip_pattern = q.delimiter_boundary_pattern = a;
            d.cdecl(h, "BoundaryRxs", w(['"', "'", ","], {'"': /.*?[^\\](?=")/, "'": /.*?[^\\](?=')/, ",": /.*?(?=[ \t]*(,|$))/}));
            d.cdecl(h, "EscapedQuoteRxs", w(['"', "'"], {'"': /\\"/, "'": /\\'/}));
            d.cdecl(h, "NameRx", /[A-Za-z:_][A-Za-z:_\-.]*/);
            d.cdecl(h, "BlankRx",
                /[ \t]+/);
            d.cdecl(h, "SkipRxs", w(["blank", ","], {blank: h.BlankRx, ",": /[ \t]*(,|$)/}));
            q.$initialize = function (n, b, c) {
                var f;
                null == b && (b = a);
                null == c && (c = ",");
                this.scanner = (null == (f = d.Object._scope.StringScanner) ? d.cm("StringScanner") : f).$new(n);
                this.block = b;
                this.delimiter = c;
                this.delimiter_skip_pattern = h.SkipRxs["$[]"](c);
                this.delimiter_boundary_pattern = h.BoundaryRxs["$[]"](c);
                return this.attributes = a
            };
            q.$parse_into = function (a, b) {
                null == b && (b = []);
                return a.$update(this.$parse(b))
            };
            q.$parse = function (d) {
                var b,
                    c, f = a;
                null == d && (d = []);
                if ((b = this.attributes) !== a && (!b._isBoolean || !0 == b))return this.attributes;
                this.attributes = w([], {});
                for (f = 0; (c = this.$parse_attribute(f, d)) !== a && (!c._isBoolean || !0 == c) && ((c = this.scanner["$eos?"]()) === a || c._isBoolean && !0 != c);)this.$skip_delimiter(), f = f["$+"](1);
                return this.attributes
            };
            q.$rekey = function (a) {
                return h.AttributeList.$rekey(this.attributes, a)
            };
            d.defs(m, "$rekey", function (d, b) {
                var c, f;
                (c = b.$each_with_index, c._p = (f = function (b, c) {
                    var f, l = a, k = a;
                    null == b && (b = a);
                    null == c && (c =
                        a);
                    if (!1 === b || b === a)return a;
                    l = c["$+"](1);
                    return(f = k = d["$[]"](l)) === a || f._isBoolean && !0 != f ? a : d["$[]="](b, k)
                }, f._s = this, f), c).call(b);
                return d
            });
            q.$parse_attribute = function (d, b) {
                var c, f, p, e, g = this, l = a, k = a, s = a, z = a, G = k = a, h = k = a, G = k = a;
                null == d && (d = 0);
                null == b && (b = []);
                l = !1;
                g.$skip_blank();
                if ((k = g.scanner.$peek(1))["$=="]('"'))s = g.$parse_attribute_value(g.scanner.$get_byte()), z = a; else if (k["$=="]("'"))s = g.$parse_attribute_value(g.scanner.$get_byte()), z = a, l = !0; else {
                    s = g.$scan_name();
                    k = 0;
                    G = a;
                    if ((c = g.scanner["$eos?"]()) ===
                        a || c._isBoolean && !0 != c)k = !1 !== (c = g.$skip_blank()) && c !== a ? c : 0, G = g.scanner.$get_byte(); else if (!1 === s || s === a)return!1;
                    if ((c = !1 !== (f = G["$!"]()) && f !== a ? f : G["$=="](g.delimiter)) === a || c._isBoolean && !0 != c)if ((c = !1 !== (f = G["$=="]("=")["$!"]()) && f !== a ? f : s["$!"]()) === a || c._isBoolean && !0 != c) {
                        if (g.$skip_blank(), (c = g.scanner.$peek(1)) !== a && (!c._isBoolean || !0 == c))if ((G = g.scanner.$get_byte())["$=="]('"'))z = g.$parse_attribute_value(G); else if (G["$=="]("'"))z = g.$parse_attribute_value(G), l = !0; else if (G["$=="](g.delimiter))z =
                            a; else if (z = "" + G + g.$scan_to_delimiter(), z["$=="]("None"))return!0
                    } else s = "" + s + " "["$*"](k) + G + g.$scan_to_delimiter(), z = a; else z = a
                }
                if (!1 !== z && z !== a)k = function () {
                    h = s;
                    return"options"["$==="](h) || "opts"["$==="](h) ? (s = "options", (c = (f = z.$split(",")).$each, c._p = (p = function (b) {
                        var c = p._s || this;
                        null == c.attributes && (c.attributes = a);
                        null == b && (b = a);
                        return c.attributes["$[]="]("" + b.$strip() + "-option", "")
                    }, p._s = g, p), c).call(f), z) : "title"["$==="](h) ? z : (c = (e = !1 !== l && l !== a) ? g.block : e) === a || c._isBoolean && !0 != c ? z : g.block.$apply_normal_subs(z)
                }(),
                    g.attributes["$[]="](s, k); else {
                    k = (c = (e = !1 !== l && l !== a) ? g.block : e) === a || c._isBoolean && !0 != c ? s : g.block.$apply_normal_subs(s);
                    if ((c = G = b["$[]"](d)) !== a && (!c._isBoolean || !0 == c))g.attributes["$[]="](G, k);
                    g.attributes["$[]="](d["$+"](1), k)
                }
                return!0
            };
            q.$parse_attribute_value = function (d) {
                var b, c = a;
                if (this.scanner.$peek(1)["$=="](d))return this.scanner.$get_byte(), "";
                if ((b = c = this.$scan_to_quote(d)) === a || b._isBoolean && !0 != b)return"" + d + this.$scan_to_delimiter();
                this.scanner.$get_byte();
                return c.$gsub(h.EscapedQuoteRxs["$[]"](d),
                    d)
            };
            q.$skip_blank = function () {
                return this.scanner.$skip(h.BlankRx)
            };
            q.$skip_delimiter = function () {
                return this.scanner.$skip(this.delimiter_skip_pattern)
            };
            q.$scan_name = function () {
                return this.scanner.$scan(h.NameRx)
            };
            q.$scan_to_delimiter = function () {
                return this.scanner.$scan(this.delimiter_boundary_pattern)
            };
            return(q.$scan_to_quote = function (a) {
                return this.scanner.$scan(h.BoundaryRxs["$[]"](a))
            }, a) && "scan_to_quote"
        })(u(r, "Asciidoctor"), null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.klass, r = d.hash2;
    return function (v) {
        v = t(v, "Asciidoctor");
        (function (t, $super) {
            function m() {
            }

            var q = m = w(t, $super, "Block", m), h = q._proto, n = q._scope, b, c, f;
            h.subs = h.attributes = h.content_model = h.lines = h.blocks = h.context = h.style = a;
            d.cdecl(n, "DEFAULT_CONTENT_MODEL", (null == (b = d.Object._scope.Hash) ? d.cm("Hash") : b).$new("simple").$merge(r("audio image listing literal math open page_break pass thematic_break video".split(" "), {audio: "empty", image: "empty", listing: "verbatim",
                literal: "verbatim", math: "raw", open: "compound", page_break: "empty", pass: "raw", thematic_break: "empty", video: "empty"})));
            d.defn(q, "$blockname", h.$context);
            q.$attr_accessor("lines");
            h.$initialize = c = function (b, e, f) {
                var l, k, s, z = a, h = a, m = a;
                null == f && (f = r([], {}));
                c._p = null;
                d.find_super_dispatcher(this, "initialize", c, null).apply(this, [b, e]);
                this.content_model = !1 !== (l = f["$[]"]("content_model")) && l !== a ? l : n.DEFAULT_CONTENT_MODEL["$[]"](e);
                (l = (z = f["$[]"]("attributes"))["$nil_or_empty?"]()) === a || l._isBoolean && !0 !=
                    l ? this.attributes = z.$dup() : this.attributes = r([], {});
                if ((l = f["$has_key?"]("subs")) !== a && (!l._isBoolean || !0 == l))if ((l = !1 !== (k = (h = f["$[]"]("subs"))["$!"]()) && k !== a ? k : h["$is_a?"](null == (s = d.Object._scope.Array) ? d.cm("Array") : s)) === a || l._isBoolean && !0 != l)this.attributes["$[]="]("subs", "" + h); else this.subs = !1 !== (l = h) && l !== a ? l : [], this.default_subs = this.subs.$dup(), this.attributes.$delete("subs");
                return(l = (m = f["$[]"]("source"))["$!"]()) === a || l._isBoolean && !0 != l ? (l = m["$is_a?"](null == (k = d.Object._scope.String) ?
                    d.cm("String") : k)) === a || l._isBoolean && !0 != l ? this.lines = m.$dup() : this.lines = n.Helpers.$normalize_lines_from_string(m) : this.lines = []
            };
            h.$content = f = function () {
                var b = u.call(arguments, 0), c, g, l = this, k = f._p, s = a, z = a, h = a, m = a;
                f._p = null;
                return function () {
                    s = l.content_model;
                    if ("compound"["$==="](s))return d.find_super_dispatcher(l, "content", f, k).apply(l, b);
                    if ("simple"["$==="](s))return l.$apply_subs(l.lines["$*"](n.EOL), l.subs);
                    if ("verbatim"["$==="](s) || "raw"["$==="](s)) {
                        z = l.$apply_subs(l.lines, l.subs);
                        if (z.$size()["$<"](2))return z["$[]"](0);
                        for (; (c = (g = h = z["$[]"](0), !1 !== g && g !== a ? h.$rstrip()["$empty?"]() : g)) !== a && (!c._isBoolean || !0 == c);)z.$shift();
                        for (; (c = (g = m = z["$[]"](-1), !1 !== g && g !== a ? m.$rstrip()["$empty?"]() : g)) !== a && (!c._isBoolean || !0 == c);)z.$pop();
                        return z["$*"](n.EOL)
                    }
                    l.content_model["$=="]("empty") || l.$warn("Unknown content model '" + l.content_model + "' for block: " + l.$to_s());
                    return a
                }()
            };
            h.$source = function () {
                return this.lines["$*"](n.EOL)
            };
            return(h.$to_s = function () {
                var b = a, b = this.content_model["$=="]("compound") ? "blocks: " + this.blocks.$size() :
                    "lines: " + this.lines.$size();
                return"#<" + this.$class() + "@" + this.$object_id() + " {context: " + this.context.$inspect() + ", content_model: " + this.content_model.$inspect() + ", style: " + this.style.$inspect() + ", " + b + "}>"
            }, a) && "to_s"
        })(v, v._scope.AbstractBlock)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (d) {
        (function (d, $super) {
            function r() {
            }

            var m = (r = t(d, $super, "Callouts", r))._proto;
            m.co_index = m.lists = m.list_index = a;
            m.$initialize = function () {
                this.lists = [];
                this.list_index = 0;
                return this.$next_list()
            };
            m.$register = function (d) {
                var h = a;
                this.$current_list()["$<<"](w(["ordinal", "id"], {ordinal: d.$to_i(), id: h = this.$generate_next_callout_id()}));
                this.co_index = this.co_index["$+"](1);
                return h
            };
            m.$read_next_id = function () {
                var d = a, h = a, d = a, h = this.$current_list();
                this.co_index["$<="](h.$size()) && (d = h["$[]"](this.co_index["$-"](1))["$[]"]("id"));
                this.co_index = this.co_index["$+"](1);
                return d
            };
            m.$callout_ids = function (d) {
                var h, n, b;
                return(h = (n = this.$current_list()).$map, h._p = (b = function (b) {
                    null == b && (b = a);
                    return b["$[]"]("ordinal")["$=="](d) ? "" + b["$[]"]("id") + " " : a
                }, b._s = this, b), h).call(n).$join().$chop()
            };
            m.$current_list = function () {
                return this.lists["$[]"](this.list_index["$-"](1))
            };
            m.$next_list = function () {
                this.list_index = this.list_index["$+"](1);
                if (this.lists.$size()["$<"](this.list_index))this.lists["$<<"]([]);
                this.co_index = 1;
                return a
            };
            m.$rewind = function () {
                this.co_index = this.list_index = 1;
                return a
            };
            m.$generate_next_callout_id = function () {
                return this.$generate_callout_id(this.list_index, this.co_index)
            };
            return(m.$generate_callout_id = function (a, d) {
                return"CO" + a + "-" + d
            }, a) && "generate_callout_id"
        })(u(d, "Asciidoctor"), null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (r) {
        r = u(r, "Asciidoctor");
        var v = r._scope;
        u(r, "Converter");
        a;
        (function (a, $super) {
            function d() {
            }

            var q = d = t(a, $super, "Base", d);
            return q.$include(q._scope.Converter)
        })(v.Converter, null);
        (function (r, $super) {
            function m() {
            }

            var q = m = t(r, $super, "BuiltIn", m), h = q._proto;
            h.$initialize = function (d, b) {
                null == b && w([], {});
                return a
            };
            h.$convert = function (d, b) {
                var c;
                null == b && (b = a);
                !1 !== (c = b) && c !== a ? c : b = d.$node_name();
                return this.$send(b, d)
            };
            h.$convert_with_options =
                function (d, b, c) {
                    var f;
                    null == b && (b = a);
                    null == c && (c = w([], {}));
                    !1 !== (f = b) && f !== a ? f : b = d.$node_name();
                    return this.$send(b, d, c)
                };
            d.defn(q, "$handles?", h["$respond_to?"]);
            h.$content = function (a) {
                return a.$content()
            };
            d.defn(q, "$pass", h.$content);
            return(h.$skip = function (d) {
                return a
            }, a) && "skip"
        })(v.Converter, null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (r) {
        (function (r) {
            (function (r, $super) {
                function m() {
                }

                var q = m = t(r, $super, "Factory", m), h = q._proto, n = q._scope;
                h.converters = h.star_converter = a;
                q.__default__ = a;
                (function (b) {
                    b._proto.$default = function (b) {
                        var f, p;
                        null == this.__default__ && (this.__default__ = a);
                        null == b && (b = !0);
                        if (!1 === b || b === a)b = !1 !== (f = this.__default__) && f !== a ? f : this.$new(); else {
                            if (!1 === (f = this.__default__) || f === a) {
                                try {
                                    p = null == d.Object._scope.ThreadSafe ? a : "constant", this.$new((null ==
                                        (p = d.Object._scope.ThreadSafe) ? d.cm("ThreadSafe") : p)._scope.Cache.$new())
                                } catch (e) {
                                    if (d.$rescue(e, [null == (p = d.Object._scope.LoadError) ? d.cm("LoadError") : p]))this.$warn("asciidoctor: WARNING: gem 'thread_safe' is not installed. This gem recommended when registering custom converters."), this.$new(); else throw e;
                                }
                                f = this.__default__ = void 0
                            }
                            b = f
                        }
                        return b
                    };
                    b._proto.$register = function (a, b) {
                        null == b && (b = ["*"]);
                        return this.$default().$register(a, b)
                    };
                    b._proto.$resolve = function (a) {
                        return this.$default().$resolve(a)
                    };
                    b._proto.$converters = function () {
                        return this.$default().$converters()
                    };
                    return(b._proto.$unregister_all = function () {
                        return this.$default().$unregister_all()
                    }, a) && "unregister_all"
                })(q.$singleton_class());
                q.$attr_reader("converters");
                h.$initialize = function (b) {
                    var c;
                    null == b && (b = a);
                    this.converters = !1 !== (c = b) && c !== a ? c : w([], {});
                    return this.star_converter = a
                };
                h.$register = function (b, c) {
                    var f, d, e;
                    null == c && (c = ["*"]);
                    (f = (d = c).$each, f._p = (e = function (c) {
                        var f = e._s || this;
                        null == f.converters && (f.converters = a);
                        null ==
                            c && (c = a);
                        f.converters["$[]="](c, b);
                        return c["$=="]("*") ? f.star_converter = b : a
                    }, e._s = this, e), f).call(d);
                    return a
                };
                h.$resolve = function (b) {
                    var c, f;
                    return c = this.converters, !1 !== c && c !== a ? !1 !== (f = this.converters["$[]"](b)) && f !== a ? f : this.star_converter : c
                };
                h.$unregister_all = function () {
                    this.converters.$clear();
                    return this.star_converter = a
                };
                return(h.$create = function (b, c) {
                    var f, p, e = a, g = e = a, l = a;
                    null == c && (c = w([], {}));
                    if ((f = e = this.$resolve(b)) !== a && (!f._isBoolean || !0 == f))return(f = e["$is_a?"](null == (p = d.Object._scope.Class) ?
                        d.cm("Class") : p)) === a || f._isBoolean && !0 != f ? e : e.$new(b, c);
                    e = function () {
                        g = b;
                        if ("html5"["$==="](g)) {
                            var e;
                            try {
                                e = null != (null == (p = d.Object._scope.Asciidoctor) ? d.cm("Asciidoctor") : p)._scope.Converter._scope.Html5Converter ? "constant" : a
                            } catch (k) {
                                if (k._klass === Opal.NameError)e = a; else throw k;
                            }
                            f = e;
                            return n.Html5Converter.$new(b, c)
                        }
                        if ("docbook5"["$==="](g)) {
                            var l;
                            try {
                                l = null != (null == (p = d.Object._scope.Asciidoctor) ? d.cm("Asciidoctor") : p)._scope.Converter._scope.DocBook5Converter ? "constant" : a
                            } catch (s) {
                                if (s._klass ===
                                    Opal.NameError)l = a; else throw s;
                            }
                            f = l;
                            return n.DocBook5Converter.$new(b, c)
                        }
                        if ("docbook45"["$==="](g)) {
                            var z;
                            try {
                                z = null != (null == (p = d.Object._scope.Asciidoctor) ? d.cm("Asciidoctor") : p)._scope.Converter._scope.DocBook45Converter ? "constant" : a
                            } catch (h) {
                                if (h._klass === Opal.NameError)z = a; else throw h;
                            }
                            f = z;
                            return n.DocBook45Converter.$new(b, c)
                        }
                        return a
                    }();
                    if ((f = c["$key?"]("template_dirs")) === a || f._isBoolean && !0 != f)return e;
                    var k;
                    try {
                        k = null != (null == (p = d.Object._scope.Asciidoctor) ? d.cm("Asciidoctor") : p)._scope.Converter._scope.TemplateConverter ?
                            "constant" : a
                    } catch (s) {
                        if (s._klass === Opal.NameError)k = a; else throw s;
                    }
                    f = k;
                    var z;
                    try {
                        z = null != (null == (p = d.Object._scope.Asciidoctor) ? d.cm("Asciidoctor") : p)._scope.Converter._scope.CompositeConverter ? "constant" : a
                    } catch (h) {
                        if (h._klass === Opal.NameError)z = a; else throw h;
                    }
                    f = z;
                    l = n.TemplateConverter.$new(b, c["$[]"]("template_dirs"), c);
                    return n.CompositeConverter.$new(b, l, e)
                }, a) && "create"
            })(u(r, "Converter"), null)
        })(u(r, "Asciidoctor"))
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.range, r = d.hash2;
    (function (v) {
        v = t(v, "Asciidoctor");
        (function (v) {
            v = t(v, "Converter");
            var A = v._proto, m = v._scope;
            (function (m) {
                m = t(m, "Config");
                var h = m._scope;
                m._proto.$register_for = function (d) {
                    var b, c, f, p, e, g = a;
                    d = u.call(arguments, 0);
                    h.Factory.$register(this, d);
                    g = function (a) {
                        return a
                    }(this.$singleton_class());
                    d["$=="](["*"]) ? (b = (c = g).$send, b._p = (f = function (a) {
                        return!0
                    }, f._s = this, f), b).call(c, "define_method", "converts?") : (b = (p = g).$send, b._p = (e = function (b) {
                        null ==
                            b && (b = a);
                        return d["$include?"](b)
                    }, e._s = this, e), b).call(p, "define_method", "converts?");
                    return a
                };
                d.donate(m, ["$register_for"])
            })(v);
            (function (m) {
                m = t(m, "BackendInfo");
                var h = m._proto, n = m._scope;
                h.$backend_info = function () {
                    var b;
                    null == this.backend_info && (this.backend_info = a);
                    return!1 !== (b = this.backend_info) && b !== a ? b : this.backend_info = this.$setup_backend_info()
                };
                h.$setup_backend_info = function () {
                    var b, c = a, f = a, p = a, e = a;
                    null == this.backend && (this.backend = a);
                    ((b = this.backend) === a || b._isBoolean && !0 != b) && this.$raise(null ==
                        (b = d.Object._scope.ArgumentError) ? d.cm("ArgumentError") : b, "Cannot determine backend for converter: " + this.$class());
                    c = this.backend.$sub(n.TrailingDigitsRx, "");
                    (b = f = n.DEFAULT_EXTENSIONS["$[]"](c)) === a || b._isBoolean && !0 != b ? (c = "html", f = ".html", e = p = "html") : p = f["$[]"](w(1, -1, !1));
                    return r(["basebackend", "outfilesuffix", "filetype", "htmlsyntax"], {basebackend: c, outfilesuffix: f, filetype: p, htmlsyntax: e})
                };
                h.$filetype = function (b) {
                    null == b && (b = a);
                    return!1 !== b && b !== a ? this.$backend_info()["$[]="]("filetype", b) :
                        this.$backend_info()["$[]"]("filetype")
                };
                h.$basebackend = function (b) {
                    null == b && (b = a);
                    return!1 !== b && b !== a ? this.$backend_info()["$[]="]("basebackend", b) : this.$backend_info()["$[]"]("basebackend")
                };
                h.$outfilesuffix = function (b) {
                    null == b && (b = a);
                    return!1 !== b && b !== a ? this.$backend_info()["$[]="]("outfilesuffix", b) : this.$backend_info()["$[]"]("outfilesuffix")
                };
                h.$htmlsyntax = function (b) {
                    null == b && (b = a);
                    return!1 !== b && b !== a ? this.$backend_info()["$[]="]("htmlsyntax", b) : this.$backend_info()["$[]"]("htmlsyntax")
                };
                d.donate(m,
                    "$backend_info $setup_backend_info $filetype $basebackend $outfilesuffix $htmlsyntax".split(" "))
            })(v);
            (function (d) {
                var h = d._scope;
                return(d._proto.$included = function (a) {
                    return a.$extend(h.Config)
                }, a) && "included"
            })(v.$singleton_class());
            v.$include(m.Config);
            v.$include(m.BackendInfo);
            A.$initialize = function (a, d) {
                null == d && r([], {});
                this.backend = a;
                return this.$setup_backend_info()
            };
            A.$convert = function (a, h) {
                var n;
                return this.$raise(null == (n = d.Object._scope.NotImplementedError) ? d.cm("NotImplementedError") :
                    n)
            };
            A.$convert_with_options = function (d, h, n) {
                null == h && (h = a);
                null == n && r([], {});
                return this.$convert(d, h)
            };
            d.donate(v, ["$initialize", "$convert", "$convert_with_options"])
        })(v);
        (function (r) {
            r = t(r, "Writer");
            var v = r._scope;
            r._proto.$write = function (m, q) {
                var h, n, b, c;
                (h = q["$respond_to?"]("write")) === a || h._isBoolean && !0 != h ? (h = (n = null == (c = d.Object._scope.File) ? d.cm("File") : c).$open, h._p = (b = function (b) {
                    null == b && (b = a);
                    return b.$write(m)
                }, b._s = this, b), h).call(n, q, "w") : (q.$write(m.$chomp()), q.$write(v.EOL));
                return a
            };
            d.donate(r, ["$write"])
        })(v)
    })(d.top);
    return!0
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2, r = d.range;
    return function (v) {
        v = u(v, "Asciidoctor")._scope;
        (function (v, $super) {
            function m() {
            }

            var q = m = t(v, $super, "Html5Converter", m), h = q._proto, n = q._scope;
            h.xml_mode = h.void_element_slash = h.stylesheets = a;
            d.cdecl(n, "QUOTE_TAGS", w("emphasis strong monospaced superscript subscript double single asciimath latexmath".split(" "), {emphasis: ["<em>", "</em>", !0], strong: ["<strong>", "</strong>", !0], monospaced: ["<code>", "</code>", !0], superscript: ["<sup>", "</sup>",
                !0], subscript: ["<sub>", "</sub>", !0], "double": ["&#8220;", "&#8221;", !1], single: ["&#8216;", "&#8217;", !1], asciimath: ["\\$", "\\$", !1], latexmath: ["\\(", "\\)", !1]}));
            n.QUOTE_TAGS["$default="]([a, a, a]);
            h.$initialize = function (b, c) {
                var f;
                null == c && (c = w([], {}));
                this.xml_mode = c["$[]"]("htmlsyntax")["$=="]("xml");
                this.void_element_slash = (f = this.xml_mode) === a || f._isBoolean && !0 != f ? a : "/";
                return this.stylesheets = n.Stylesheets.$instance()
            };
            h.$document = function (b) {
                var c, f, d, e, g, l = a, k = a, s = a, z = a, h = a, z = z = z = h = h = h = a, l = [],
                    k = this.void_element_slash, s = "<br" + k + ">", z = !1 !== (c = b.$safe()["$>="](n.SafeMode._scope.SECURE)) && c !== a ? c : b["$attr?"]("linkcss");
                l["$<<"]("<!DOCTYPE html>");
                h = (c = b["$attr?"]("nolang")) === a || c._isBoolean && !0 != c ? ' lang="' + b.$attr("lang", "en") + '"' : a;
                l["$<<"]("<html" + ((c = this.xml_mode) === a || c._isBoolean && !0 != c ? a : ' xmlns="http://www.w3.org/1999/xhtml"') + h + ">");
                l["$<<"]('<head>\n<meta charset="' + b.$attr("encoding", "UTF-8") + '"' + k + '>\n\x3c!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge"' + k + '><![endif]--\x3e\n<meta name="viewport" content="width=device-width, initial-scale=1.0"' +
                    k + '>\n<meta name="generator" content="Asciidoctor ' + b.$attr("asciidoctor-version") + '"' + k + ">");
                if ((c = b["$attr?"]("app-name")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<meta name="application-name" content="' + b.$attr("app-name") + '"' + k + ">");
                if ((c = b["$attr?"]("description")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<meta name="description" content="' + b.$attr("description") + '"' + k + ">");
                if ((c = b["$attr?"]("keywords")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<meta name="keywords" content="' + b.$attr("keywords") + '"' +
                    k + ">");
                if ((c = b["$attr?"]("authors")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<meta name="author" content="' + b.$attr("authors") + '"' + k + ">");
                if ((c = b["$attr?"]("copyright")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<meta name="copyright" content="' + b.$attr("copyright") + '"' + k + ">");
                l["$<<"]("<title>" + (!1 !== (c = b.$doctitle(w(["sanitize"], {sanitize: !0}))) && c !== a ? c : b.$attr("untitled-label")) + "</title>");
                if ((c = n.DEFAULT_STYLESHEET_KEYS["$include?"](b.$attr("stylesheet"))) !== a && (!c._isBoolean || !0 == c))if (!1 !== z && z !==
                    a)l["$<<"]('<link rel="stylesheet" href="' + b.$normalize_web_path(n.DEFAULT_STYLESHEET_NAME, b.$attr("stylesdir", "")) + '"' + k + ">"); else l["$<<"](this.stylesheets.$embed_primary_stylesheet()); else if ((c = b["$attr?"]("stylesheet")) !== a && (!c._isBoolean || !0 == c))if (!1 !== z && z !== a)l["$<<"]('<link rel="stylesheet" href="' + b.$normalize_web_path(b.$attr("stylesheet"), b.$attr("stylesdir", "")) + '"' + k + ">"); else l["$<<"]("<style>\n" + b.$read_asset(b.$normalize_system_path(b.$attr("stylesheet"), b.$attr("stylesdir", "")),
                    !0) + "\n</style>");
                if ((c = b["$attr?"]("icons", "font")) !== a && (!c._isBoolean || !0 == c))if ((c = b.$attr("iconfont-remote", "")["$nil?"]()["$!"]()) === a || c._isBoolean && !0 != c)h = "" + b.$attr("iconfont-name", "font-awesome") + ".css", l["$<<"]('<link rel="stylesheet" href="' + b.$normalize_web_path(h, b.$attr("stylesdir", "")) + '"' + k + ">"); else l["$<<"]('<link rel="stylesheet" href="' + b.$attr("iconfont-cdn", "http://cdnjs.cloudflare.com/ajax/libs/font-awesome/3.2.1/css/font-awesome.min.css") + '"' + k + ">");
                h = b.$attr("source-highlighter");
                if ("coderay"["$==="](h)) {
                    if (b.$attr("coderay-css", "class")["$=="]("class"))if (!1 !== z && z !== a)l["$<<"]('<link rel="stylesheet" href="' + b.$normalize_web_path(this.stylesheets.$coderay_stylesheet_name(), b.$attr("stylesdir", "")) + '"' + k + ">"); else l["$<<"](this.stylesheets.$embed_coderay_stylesheet())
                } else if ("pygments"["$==="](h)) {
                    if (b.$attr("pygments-css", "class")["$=="]("class"))if (h = this.$doc().$attr("pygments-style", "pastie"), !1 !== z && z !== a)l["$<<"]('<link rel="stylesheet" href="' + b.$normalize_web_path(this.stylesheets.$pygments_stylesheet_name(h),
                        b.$attr("stylesdir", "")) + '"' + k + ">"); else l["$<<"](this.stylesheets.$instance().$embed_pygments_stylesheet(h))
                } else if ("highlightjs"["$==="](h) || "highlight.js"["$==="](h))l["$<<"]('<link rel="stylesheet" href="' + b.$attr("highlightjsdir", "http://cdnjs.cloudflare.com/ajax/libs/highlight.js/7.4") + "/styles/" + b.$attr("highlightjs-theme", "googlecode") + '.min.css"' + k + '>\n<script src="' + b.$attr("highlightjsdir", "http://cdnjs.cloudflare.com/ajax/libs/highlight.js/7.4") + '/highlight.min.js">\x3c/script>\n<script src="' +
                    b.$attr("highlightjsdir", "http://cdnjs.cloudflare.com/ajax/libs/highlight.js/7.4") + '/lang/common.min.js">\x3c/script>\n<script>hljs.initHighlightingOnLoad()\x3c/script>'); else if ("prettify"["$==="](h))l["$<<"]('<link rel="stylesheet" href="' + b.$attr("prettifydir", "http://cdnjs.cloudflare.com/ajax/libs/prettify/r298") + "/" + b.$attr("prettify-theme", "prettify") + '.min.css"' + k + '>\n<script src="' + b.$attr("prettifydir", "http://cdnjs.cloudflare.com/ajax/libs/prettify/r298") + "/prettify.min.js\">\x3c/script>\n<script>document.addEventListener('DOMContentLoaded', prettyPrint)\x3c/script>");
                if ((c = b["$attr?"]("math")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<script type="text/x-mathjax-config">\nMathJax.Hub.Config({\n  tex2jax: {\n    inlineMath: [' + n.INLINE_MATH_DELIMITERS["$[]"]("latexmath") + "],\n    displayMath: [" + n.BLOCK_MATH_DELIMITERS["$[]"]("latexmath") + '],\n    ignoreClass: "nomath|nolatexmath"\n  },\n  asciimath2jax: {\n    delimiters: [' + n.BLOCK_MATH_DELIMITERS["$[]"]("asciimath") + '],\n    ignoreClass: "nomath|noasciimath"\n  }\n});\n\x3c/script>\n<script type="text/javascript" src="http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-MML-AM_HTMLorMML">\x3c/script>\n<script>document.addEventListener(\'DOMContentLoaded\', MathJax.Hub.TypeSet)\x3c/script>');
                if ((c = (z = b.$docinfo())["$empty?"]()) === a || c._isBoolean && !0 != c)l["$<<"](z);
                l["$<<"]("</head>");
                z = [];
                if ((c = b.$id()) !== a && (!c._isBoolean || !0 == c))z["$<<"]('id="' + b.$id() + '"');
                if ((c = (f = (d = b["$attr?"]("toc-class"), !1 !== d && d !== a ? b["$attr?"]("toc") : d), !1 !== f && f !== a ? b["$attr?"]("toc-placement", "auto") : f)) === a || c._isBoolean && !0 != c)z["$<<"]('class="' + b.$doctype() + '"'); else z["$<<"]('class="' + b.$doctype() + " " + b.$attr("toc-class") + " toc-" + b.$attr("toc-position", "left") + '"');
                if ((c = b["$attr?"]("max-width")) !== a &&
                    (!c._isBoolean || !0 == c))z["$<<"]('style="max-width: ' + b.$attr("max-width") + ';"');
                l["$<<"]("<body " + z["$*"](" ") + ">");
                if ((c = b.$noheader()) === a || c._isBoolean && !0 != c) {
                    l["$<<"]('<div id="header">');
                    if (b.$doctype()["$=="]("manpage")) {
                        l["$<<"]("<h1>" + b.$doctitle() + " Manual Page</h1>");
                        if ((c = (f = b["$attr?"]("toc"), !1 !== f && f !== a ? b["$attr?"]("toc-placement", "auto") : f)) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<div id="toc" class="' + b.$attr("toc-class", "toc") + '">\n<div id="toctitle">' + b.$attr("toc-title") + "</div>\n" +
                            this.$outline(b) + "\n</div>");
                        l["$<<"]("<h2>" + b.$attr("manname-title") + '</h2>\n<div class="sectionbody">\n<p>' + b.$attr("manname") + " - " + b.$attr("manpurpose") + "</p>\n</div>")
                    } else {
                        if ((c = b["$has_header?"]()) !== a && (!c._isBoolean || !0 == c)) {
                            if ((c = b.$notitle()) === a || c._isBoolean && !0 != c)l["$<<"]("<h1>" + b.$header().$title() + "</h1>");
                            if ((c = b["$attr?"]("author")) !== a && (!c._isBoolean || !0 == c)) {
                                l["$<<"]('<span id="author" class="author">' + b.$attr("author") + "</span>" + s);
                                if ((c = b["$attr?"]("email")) !== a && (!c._isBoolean ||
                                    !0 == c))l["$<<"]('<span id="email" class="email">' + b.$sub_macros(b.$attr("email")) + "</span>" + s);
                                (z = b.$attr("authorcount").$to_i())["$>"](1) && (c = (f = r(2, z, !1)).$each, c._p = (e = function (c) {
                                    var e;
                                    null == c && (c = a);
                                    l["$<<"]('<span id="author' + c + '" class="author">' + b.$attr("author_" + c) + "</span>" + s);
                                    return(e = b["$attr?"]("email_" + c)) === a || e._isBoolean && !0 != e ? a : l["$<<"]('<span id="email' + c + '" class="email">' + b.$sub_macros(b.$attr("email_" + c)) + "</span>" + s)
                                }, e._s = this, e), c).call(f)
                            }
                            if ((c = b["$attr?"]("revnumber")) !==
                                a && (!c._isBoolean || !0 == c))l["$<<"]('<span id="revnumber">' + (!1 !== (c = b.$attr("version-label")) && c !== a ? c : "").$downcase() + " " + b.$attr("revnumber") + ((c = b["$attr?"]("revdate")) === a || c._isBoolean && !0 != c ? "" : ",") + "</span>");
                            if ((c = b["$attr?"]("revdate")) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<span id="revdate">' + b.$attr("revdate") + "</span>");
                            if ((c = b["$attr?"]("revremark")) !== a && (!c._isBoolean || !0 == c))l["$<<"]("" + s + '<span id="revremark">' + b.$attr("revremark") + "</span>")
                        }
                        if ((c = (d = b["$attr?"]("toc"), !1 !== d &&
                            d !== a ? b["$attr?"]("toc-placement", "auto") : d)) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<div id="toc" class="' + b.$attr("toc-class", "toc") + '">\n<div id="toctitle">' + b.$attr("toc-title") + "</div>\n" + this.$outline(b) + "\n</div>")
                    }
                    l["$<<"]("</div>")
                }
                l["$<<"]('<div id="content">\n' + b.$content() + "\n</div>");
                (c = (d = b["$footnotes?"](), !1 !== d && d !== a ? b["$attr?"]("nofootnotes")["$!"]() : d)) === a || c._isBoolean && !0 != c || (l["$<<"]('<div id="footnotes">\n<hr' + k + ">"), (c = (d = b.$footnotes()).$each, c._p = (g = function (b) {
                    null == b &&
                    (b = a);
                    return l["$<<"]('<div class="footnote" id="_footnote_' + b.$index() + '">\n<a href="#_footnoteref_' + b.$index() + '">' + b.$index() + "</a>. " + b.$text() + "\n</div>")
                }, g._s = this, g), c).call(d), l["$<<"]("</div>"));
                if ((c = b.$nofooter()) === a || c._isBoolean && !0 != c) {
                    l["$<<"]('<div id="footer">');
                    l["$<<"]('<div id="footer-text">');
                    if ((c = b["$attr?"]("revnumber")) !== a && (!c._isBoolean || !0 == c))l["$<<"]("" + b.$attr("version-label") + " " + b.$attr("revnumber") + s);
                    if ((c = b["$attr?"]("last-update-label")) !== a && (!c._isBoolean ||
                        !0 == c))l["$<<"]("" + b.$attr("last-update-label") + " " + b.$attr("docdatetime"));
                    l["$<<"]("</div>");
                    if ((c = (z = b.$docinfo("footer"))["$empty?"]()) === a || c._isBoolean && !0 != c)l["$<<"](z);
                    l["$<<"]("</div>")
                }
                l["$<<"]("</body>");
                l["$<<"]("</html>");
                return l["$*"](n.EOL)
            };
            h.$embedded = function (b) {
                var c, f, d, e = a, g = a, e = [];
                (c = (f = b.$notitle()["$!"](), !1 !== f && f !== a ? b["$has_header?"]() : f)) === a || c._isBoolean && !0 != c || (g = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', e["$<<"]("<h1" + g + ">" + b.$header().$title() +
                    "</h1>"));
                e["$<<"](b.$content());
                (c = (f = b["$footnotes?"](), !1 !== f && f !== a ? b["$attr?"]("nofootnotes")["$!"]() : f)) === a || c._isBoolean && !0 != c || (e["$<<"]('<div id="footnotes">\n<hr' + this.void_element_slash + ">"), (c = (f = b.$footnotes()).$each, c._p = (d = function (b) {
                    null == b && (b = a);
                    return e["$<<"]('<div class="footnote" id="_footnote_' + b.$index() + '">\n<a href="#_footnoteref_' + b.$index() + '">' + b.$index() + "</a> " + b.$text() + "\n</div>")
                }, d._s = this, d), c).call(f), e["$<<"]("</div>"));
                return e["$*"](n.EOL)
            };
            h.$outline = function (b, c) {
                var f, d, e, g = a, l = a, k = a, s = a, z = a, h = a;
                null == c && (c = w([], {}));
                if ((f = (g = b.$sections())["$empty?"]()) !== a && (!f._isBoolean || !0 == f))return a;
                l = !1 !== (f = c["$[]"]("sectnumlevels")) && f !== a ? f : b.$document().$attr("sectnumlevels", 3).$to_i();
                k = !1 !== (f = c["$[]"]("toclevels")) && f !== a ? f : b.$document().$attr("toclevels", 2).$to_i();
                s = [];
                z = (h = g["$[]"](0)).$level();
                (f = (d = z["$=="](0)) ? h.$special() : d) === a || f._isBoolean && !0 != f || (z = 1);
                s["$<<"]('<ul class="sectlevel' + z + '">');
                (f = (d = g).$each, f._p = (e = function (b) {
                    var c = e._s || this,
                        f, g, d, p = a, n = a;
                    null == b && (b = a);
                    p = (f = (g = (d = b.$numbered(), !1 !== d && d !== a ? b.$caption()["$!"]() : d), !1 !== g && g !== a ? b.$level()["$<="](l) : g)) === a || f._isBoolean && !0 != f ? a : "" + b.$sectnum() + " ";
                    s["$<<"]('<li><a href="#' + b.$id() + '">' + p + b.$captioned_title() + "</a></li>");
                    if ((f = (g = b.$level()["$<"](k)) ? n = c.$outline(b, w(["toclevels", "secnumlevels"], {toclevels: k, secnumlevels: l})) : g) === a || f._isBoolean && !0 != f)return a;
                    s["$<<"]("<li>");
                    s["$<<"](n);
                    return s["$<<"]("</li>")
                }, e._s = this, e), f).call(d);
                s["$<<"]("</ul>");
                return s["$*"](n.EOL)
            };
            h.$section = function (b) {
                var c, f, d, e = a, g = a, l = a, k = a, s = a, n = a, h = a, m = a, q = a, e = b.$level();
                (c = (f = e["$=="](0)) ? b.$special() : f) === a || c._isBoolean && !0 != c || (e = 1);
                g = "h" + e["$+"](1);
                l = k = s = n = a;
                (c = b.$id()) === a || c._isBoolean && !0 != c || ((l = ' id="' + b.$id() + '"', (c = b.$document()["$attr?"]("sectanchors")) === a || c._isBoolean && !0 != c) ? (c = b.$document()["$attr?"]("sectlinks")) === a || c._isBoolean && !0 != c || (s = '<a class="link" href="#' + b.$id() + '">', n = "</a>") : k = '<a class="anchor" href="#' + b.$id() + '"></a>');
                if (e["$=="](0))return"<h1" +
                    l + ' class="sect0">' + k + s + b.$title() + n + "</h1>\n" + b.$content();
                h = (c = m = b.$role()) === a || c._isBoolean && !0 != c ? ' class="sect' + e + '"' : ' class="sect' + e + " " + m + '"';
                q = (c = (f = (d = b.$numbered(), !1 !== d && d !== a ? b.$caption()["$!"]() : d), !1 !== f && f !== a ? e["$<="](b.$document().$attr("sectnumlevels", 3).$to_i()) : f)) === a || c._isBoolean && !0 != c ? a : "" + b.$sectnum() + " ";
                return"<div" + h + ">\n<" + g + l + ">" + k + s + q + b.$captioned_title() + n + "</" + g + ">\n" + (e["$=="](1) ? '<div class="sectionbody">\n' + b.$content() + "\n</div>" : b.$content()) + "\n</div>"
            };
            h.$admonition = function (b) {
                var c, f = a, d = a, e = a, g = a, l = a, f = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', d = b.$attr("name"), e = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$title() + "</div>\n", g = (c = b.$document()["$attr?"]("icons")) === a || c._isBoolean && !0 != c ? '<div class="title">' + b.$caption() + "</div>" : (c = b.$document()["$attr?"]("icons", "font")) === a || c._isBoolean && !0 != c ? '<img src="' + b.$icon_uri(d) + '" alt="' + b.$caption() + '"' + this.void_element_slash + ">" : '<i class="icon-' +
                    d + '" title="' + b.$caption() + '"></i>';
                return"<div" + f + ' class="admonitionblock ' + d + (c = l = b.$role(), !1 !== c && c !== a ? " " + l : c) + '">\n<table>\n<tr>\n<td class="icon">\n' + g + '\n</td>\n<td class="content">\n' + e + b.$content() + "\n</td>\n</tr>\n</table>\n</div>"
            };
            h.$audio = function (b) {
                var c, f = a, d = a, e = a, g = e = a, f = b.$document()["$attr?"]("htmlsyntax", "xml"), d = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', e = ["audioblock", b.$style(), b.$role()].$compact(), e = ' class="' + e["$*"](" ") + '"', g = (c = b["$title?"]()) === a ||
                    c._isBoolean && !0 != c ? a : '<div class="title">' + b.$captioned_title() + "</div>\n";
                return"<div" + d + e + ">\n" + g + '<div class="content">\n<audio src="' + b.$media_uri(b.$attr("target")) + '"' + ((c = b["$option?"]("autoplay")) === a || c._isBoolean && !0 != c ? a : this.$append_boolean_attribute("autoplay", f)) + ((c = b["$option?"]("nocontrols")) === a || c._isBoolean && !0 != c ? this.$append_boolean_attribute("controls", f) : a) + ((c = b["$option?"]("loop")) === a || c._isBoolean && !0 != c ? a : this.$append_boolean_attribute("loop", f)) + ">\nYour browser does not support the audio tag.\n</audio>\n</div>\n</div>"
            };
            h.$colist = function (b) {
                var c, f, d, e, g, l = a, k = a, s = a, z = s = a, l = [], k = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', s = ["colist", b.$style(), b.$role()].$compact(), s = ' class="' + s["$*"](" ") + '"';
                l["$<<"]("<div" + k + s + ">");
                if ((c = b["$title?"]()) !== a && (!c._isBoolean || !0 == c))l["$<<"]('<div class="title">' + b.$title() + "</div>");
                (c = b.$document()["$attr?"]("icons")) === a || c._isBoolean && !0 != c ? (l["$<<"]("<ol>"), (c = (e = b.$items()).$each, c._p = (g = function (b) {
                    null == b && (b = a);
                    return l["$<<"]("<li>\n<p>" + b.$text() + "</p>\n</li>")
                },
                    g._s = this, g), c).call(e), l["$<<"]("</ol>")) : (l["$<<"]("<table>"), z = b.$document()["$attr?"]("icons", "font"), (c = (f = b.$items()).$each_with_index, c._p = (d = function (c, e) {
                    var f = d._s || this, g = a, g = a;
                    null == f.void_element_slash && (f.void_element_slash = a);
                    null == c && (c = a);
                    null == e && (e = a);
                    g = e["$+"](1);
                    g = !1 !== z && z !== a ? '<i class="conum" data-value="' + g + '"></i><b>' + g + "</b>" : '<img src="' + b.$icon_uri("callouts/" + g) + '" alt="' + g + '"' + f.void_element_slash + ">";
                    return l["$<<"]("<tr>\n<td>" + g + "</td>\n<td>" + c.$text() + "</td>\n</tr>")
                },
                    d._s = this, d), c).call(f), l["$<<"]("</table>"));
                l["$<<"]("</div>");
                return l["$*"](n.EOL)
            };
            h.$dlist = function (b) {
                var c, f, d, e, g, l, k, s = a, z = a, h = a, m = h = h = a, q = z = a, s = [], z = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', h = b.$style(), h = ("qanda"["$==="](h) ? ["qlist", "qanda", b.$role()] : "horizontal"["$==="](h) ? ["hdlist", b.$role()] : ["dlist", b.$style(), b.$role()]).$compact(), h = ' class="' + h["$*"](" ") + '"';
                s["$<<"]("<div" + z + h + ">");
                if ((c = b["$title?"]()) !== a && (!c._isBoolean || !0 == c))s["$<<"]('<div class="title">' +
                    b.$title() + "</div>");
                h = b.$style();
                "qanda"["$==="](h) ? (s["$<<"]("<ol>"), (c = (f = b.$items()).$each, c._p = (d = function (b, c) {
                    var e = d._s || this, f, g, k;
                    null == b && (b = a);
                    null == c && (c = a);
                    s["$<<"]("<li>");
                    (f = (g = [].concat(b)).$each, f._p = (k = function (b) {
                        null == b && (b = a);
                        return s["$<<"]("<p><em>" + b.$text() + "</em></p>")
                    }, k._s = e, k), f).call(g);
                    if (!1 !== c && c !== a) {
                        if ((f = c["$text?"]()) !== a && (!f._isBoolean || !0 == f))s["$<<"]("<p>" + c.$text() + "</p>");
                        if ((f = c["$blocks?"]()) !== a && (!f._isBoolean || !0 == f))s["$<<"](c.$content())
                    }
                    return s["$<<"]("</li>")
                },
                    d._s = this, d), c).call(f), s["$<<"]("</ol>")) : "horizontal"["$==="](h) ? (m = this.void_element_slash, s["$<<"]("<table>"), (c = !1 !== (e = b["$attr?"]("labelwidth")) && e !== a ? e : b["$attr?"]("itemwidth")) === a || c._isBoolean && !0 != c || (s["$<<"]("<colgroup>"), z = (c = b["$attr?"]("labelwidth")) === a || c._isBoolean && !0 != c ? a : ' style="width: ' + b.$attr("labelwidth").$chomp("%") + '%;"', s["$<<"]("<col" + z + m + ">"), z = (c = b["$attr?"]("itemwidth")) === a || c._isBoolean && !0 != c ? a : ' style="width: ' + b.$attr("itemwidth").$chomp("%") + '%;"', s["$<<"]("<col" +
                    z + m + ">"), s["$<<"]("</colgroup>")), (c = (e = b.$items()).$each, c._p = (g = function (c, e) {
                    var f = g._s || this, d, k, l, p = a, n = a;
                    null == c && (c = a);
                    null == e && (e = a);
                    s["$<<"]("<tr>");
                    s["$<<"]('<td class="hdlist1' + ((d = b["$option?"]("strong")) === a || d._isBoolean && !0 != d ? a : " strong") + '">');
                    p = [].concat(c);
                    n = p["$[]"](-1);
                    (d = (k = p).$each, d._p = (l = function (b) {
                        var c;
                        null == b && (b = a);
                        s["$<<"](b.$text());
                        return(c = b["$=="](n)["$!"]()) === a || c._isBoolean && !0 != c ? a : s["$<<"]("<br" + m + ">")
                    }, l._s = f, l), d).call(k);
                    s["$<<"]("</td>");
                    s["$<<"]('<td class="hdlist2">');
                    if (!1 !== e && e !== a) {
                        if ((d = e["$text?"]()) !== a && (!d._isBoolean || !0 == d))s["$<<"]("<p>" + e.$text() + "</p>");
                        if ((d = e["$blocks?"]()) !== a && (!d._isBoolean || !0 == d))s["$<<"](e.$content())
                    }
                    s["$<<"]("</td>");
                    return s["$<<"]("</tr>")
                }, g._s = this, g), c).call(e), s["$<<"]("</table>")) : (s["$<<"]("<dl>"), q = (c = b.$style()) === a || c._isBoolean && !0 != c ? ' class="hdlist1"' : a, (c = (l = b.$items()).$each, c._p = (k = function (b, c) {
                    var e = k._s || this, f, g, d;
                    null == b && (b = a);
                    null == c && (c = a);
                    (f = (g = [].concat(b)).$each, f._p = (d = function (b) {
                        null == b && (b =
                            a);
                        return s["$<<"]("<dt" + q + ">" + b.$text() + "</dt>")
                    }, d._s = e, d), f).call(g);
                    if (!1 !== c && c !== a) {
                        s["$<<"]("<dd>");
                        if ((f = c["$text?"]()) !== a && (!f._isBoolean || !0 == f))s["$<<"]("<p>" + c.$text() + "</p>");
                        if ((f = c["$blocks?"]()) !== a && (!f._isBoolean || !0 == f))s["$<<"](c.$content());
                        return s["$<<"]("</dd>")
                    }
                    return a
                }, k._s = this, k), c).call(l), s["$<<"]("</dl>"));
                s["$<<"]("</div>");
                return s["$*"](n.EOL)
            };
            h.$example = function (b) {
                var c, f = a, d = a, e = a, f = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', d = (c = b["$title?"]()) ===
                    a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$captioned_title() + "</div>\n";
                return"<div" + f + ' class="' + ((c = e = b.$role()) === a || c._isBoolean && !0 != c ? "exampleblock" : ["exampleblock", e]["$*"](" ")) + '">\n' + d + '<div class="content">\n' + b.$content() + "\n</div>\n</div>"
            };
            h.$floating_title = function (b) {
                var c, f = a, d = a, e = a, f = "h" + b.$level()["$+"](1), d = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', e = [b.$style(), b.$role()].$compact();
                return"<" + f + d + ' class="' + e["$*"](" ") + '">' + b.$title() + "</" + f + ">"
            };
            h.$image =
                function (b) {
                    var c, f, d = a, e = a, g = e = d = d = a, l = e = a, k = g = a, s = k = a, d = (c = b["$attr?"]("align")) === a || c._isBoolean && !0 != c ? a : b.$attr("align"), e = (c = b["$attr?"]("float")) === a || c._isBoolean && !0 != c ? a : b.$attr("float");
                    (c = !1 !== (f = d) && f !== a ? f : e) === a || c._isBoolean && !0 != c ? d = a : (d = [!1 !== d && d !== a ? "text-align: " + d : a, !1 !== e && e !== a ? "float: " + e : a].$compact(), d = ' style="' + d["$*"](";") + '"');
                    e = (c = b["$attr?"]("width")) === a || c._isBoolean && !0 != c ? a : ' width="' + b.$attr("width") + '"';
                    g = (c = b["$attr?"]("height")) === a || c._isBoolean && !0 != c ? a :
                        ' height="' + b.$attr("height") + '"';
                    e = '<img src="' + b.$image_uri(b.$attr("target")) + '" alt="' + b.$attr("alt") + '"' + e + g + this.void_element_slash + ">";
                    (c = l = b.$attr("link")) === a || c._isBoolean && !0 != c || (e = '<a class="image" href="' + l + '">' + e + "</a>");
                    g = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"';
                    k = ["imageblock", b.$style(), b.$role()].$compact();
                    k = ' class="' + k["$*"](" ") + '"';
                    s = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '\n<div class="title">' + b.$captioned_title() + "</div>";
                    return"<div" + g + k + d + '>\n<div class="content">\n' +
                        e + "\n</div>" + s + "\n</div>"
                };
            h.$listing = function (b) {
                var c, f = a, d = a, e = a, g = a, l = g = f = e = d = d = f = a, f = !1 !== (c = b.$document()["$attr?"]("prewrap")["$!"]()) && c !== a ? c : b["$option?"]("nowrap");
                b.$style()["$=="]("source") ? (d = b.$attr("language"), e = !1 !== d && d !== a ? "" + d + " language-" + d : a, g = b.$attr("source-highlighter"), "coderay"["$==="](g) ? (f = !1 !== f && f !== a ? ' class="CodeRay nowrap"' : ' class="CodeRay"', d = !1 !== d && d !== a ? ' class="' + e + '"' : a) : "pygments"["$==="](g) ? (f = !1 !== f && f !== a ? ' class="pygments highlight nowrap"' : ' class="pygments highlight"',
                    d = !1 !== d && d !== a ? ' class="' + e + '"' : a) : "highlightjs"["$==="](g) || "highlight.js"["$==="](g) ? (f = !1 !== f && f !== a ? ' class="highlight nowrap"' : ' class="highlight"', d = !1 !== d && d !== a ? ' class="' + e + '"' : a) : "prettify"["$==="](g) ? (f = ' class="prettyprint' + (!1 !== f && f !== a ? " nowrap" : a) + ((c = b["$attr?"]("linenums")) === a || c._isBoolean && !0 != c ? a : " linenums") + '"', d = !1 !== d && d !== a ? ' class="' + e + '"' : a) : "html-pipeline"["$==="](g) ? (f = !1 !== d && d !== a ? ' lang="' + d + '"' : a, d = a) : (f = !1 !== f && f !== a ? ' class="highlight nowrap"' : ' class="highlight"',
                    d = !1 !== d && d !== a ? ' class="' + e + '"' : a), d = "<pre" + f + "><code" + d + ">", e = "</code></pre>") : (d = "<pre" + (!1 !== f && f !== a ? ' class="nowrap"' : a) + ">", e = "</pre>");
                f = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"';
                g = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$captioned_title() + "</div>\n";
                return"<div" + f + ' class="listingblock' + (c = l = b.$role(), !1 !== c && c !== a ? " " + l : c) + '">\n' + g + '<div class="content">\n' + d + b.$content() + e + "\n</div>\n</div>"
            };
            h.$literal = function (b) {
                var c, f = a, d = a, e = a, g = a, f =
                    (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', d = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$title() + "</div>\n", e = !1 !== (c = b.$document()["$attr?"]("prewrap")["$!"]()) && c !== a ? c : b["$option?"]("nowrap");
                return"<div" + f + ' class="literalblock' + (c = g = b.$role(), !1 !== c && c !== a ? " " + g : c) + '">\n' + d + '<div class="content">\n<pre' + (!1 !== e && e !== a ? ' class="nowrap"' : a) + ">" + b.$content() + "</pre>\n</div>\n</div>"
            };
            h.$math = function (b) {
                var c, f, p = a, e = a, g = a, l = a, k = a, s = a, p = (c = b.$id()) === a ||
                    c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', e = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$title() + "</div>\n";
                c = d.to_ary(n.BLOCK_MATH_DELIMITERS["$[]"](b.$style().$to_sym()));
                g = null == c[0] ? a : c[0];
                l = null == c[1] ? a : c[1];
                k = b.$content().$strip();
                (c = (f = b.$subs()["$nil_or_empty?"](), !1 !== f && f !== a ? b["$attr?"]("subs")["$!"]() : f)) === a || c._isBoolean && !0 != c || (k = b.$sub_specialcharacters(k));
                if ((c = (f = k["$start_with?"](g), !1 !== f && f !== a ? k["$end_with?"](l) : f)) === a || c._isBoolean && !0 != c)k = "" + g +
                    k + l;
                return"<div" + p + ' class="' + ((c = s = b.$role()) === a || c._isBoolean && !0 != c ? "mathblock" : ["mathblock", s]["$*"](" ")) + '">\n' + e + '<div class="content">\n' + k + "\n</div>\n</div>"
            };
            h.$olist = function (b) {
                var c, f, d, e = a, g = a, l = a, k = g = l = a, l = a, e = [], g = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', l = ["olist", b.$style(), b.$role()].$compact(), l = ' class="' + l["$*"](" ") + '"';
                e["$<<"]("<div" + g + l + ">");
                if ((c = b["$title?"]()) !== a && (!c._isBoolean || !0 == c))e["$<<"]('<div class="title">' + b.$title() + "</div>");
                g = (c = k = b.$list_marker_keyword()) ===
                    a || c._isBoolean && !0 != c ? a : ' type="' + k + '"';
                l = (c = b["$attr?"]("start")) === a || c._isBoolean && !0 != c ? a : ' start="' + b.$attr("start") + '"';
                e["$<<"]('<ol class="' + b.$style() + '"' + g + l + ">");
                (c = (f = b.$items()).$each, c._p = (d = function (b) {
                    var c;
                    null == b && (b = a);
                    e["$<<"]("<li>");
                    e["$<<"]("<p>" + b.$text() + "</p>");
                    if ((c = b["$blocks?"]()) !== a && (!c._isBoolean || !0 == c))e["$<<"](b.$content());
                    return e["$<<"]("</li>")
                }, d._s = this, d), c).call(f);
                e["$<<"]("</ol>");
                e["$<<"]("</div>");
                return e["$*"](n.EOL)
            };
            h.$open = function (b) {
                var c, f,
                    d, e, g = a, l = a, k = a, s = a;
                if ((g = b.$style())["$=="]("abstract")) {
                    if ((c = (f = b.$parent()["$=="](b.$document())) ? b.$document().$doctype()["$=="]("book") : f) === a || c._isBoolean && !0 != c)return l = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', k = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$title() + "</div>", "<div" + l + ' class="quoteblock abstract' + (c = s = b.$role(), !1 !== c && c !== a ? " " + s : c) + '">\n' + k + "<blockquote>\n" + b.$content() + "\n</blockquote>\n</div>";
                    this.$warn("asciidoctor: WARNING: abstract block cannot be used in a document without a title when doctype is book. Excluding block content.");
                    return""
                }
                if ((c = (f = g["$=="]("partintro")) ? !1 !== (d = !1 !== (e = b.$level()["$=="](0)["$!"]()) && e !== a ? e : b.$parent().$context()["$=="]("section")["$!"]()) && d !== a ? d : b.$document().$doctype()["$=="]("book")["$!"]() : f) === a || c._isBoolean && !0 != c)return l = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', k = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$title() + "</div>", "<div" + l + ' class="openblock' + ((c = (f = !1 !== g && g !== a) ? g["$=="]("open")["$!"]() : f) === a || c._isBoolean && !0 != c ? "" : " " + g) +
                    (c = s = b.$role(), !1 !== c && c !== a ? " " + s : c) + '">\n' + k + '<div class="content">\n' + b.$content() + "\n</div>\n</div>";
                this.$warn("asciidoctor: ERROR: partintro block can only be used when doctype is book and it's a child of a book part. Excluding block content.");
                return""
            };
            h.$page_break = function (a) {
                return'<div style="page-break-after: always;"></div>'
            };
            h.$paragraph = function (b) {
                var c, f = a, f = (c = b.$id()) === a || c._isBoolean && !0 != c ? (c = b.$role()) === a || c._isBoolean && !0 != c ? ' class="paragraph"' : ' class="paragraph ' + b.$role() +
                    '"' : (c = b.$role()) === a || c._isBoolean && !0 != c ? ' id="' + b.$id() + '" class="paragraph"' : ' id="' + b.$id() + '" class="paragraph ' + b.$role() + '"';
                return(c = b["$title?"]()) === a || c._isBoolean && !0 != c ? "<div" + f + ">\n<p>" + b.$content() + "</p>\n</div>" : "<div" + f + '>\n<div class="title">' + b.$title() + "</div>\n<p>" + b.$content() + "</p>\n</div>"
            };
            h.$preamble = function (b) {
                var c, f, d = a, d = (c = (f = b["$attr?"]("toc"), !1 !== f && f !== a ? b["$attr?"]("toc-placement", "preamble") : f)) === a || c._isBoolean && !0 != c ? a : '\n<div id="toc" class="' + b.$attr("toc-class",
                    "toc") + '">\n<div id="toctitle">' + b.$attr("toc-title") + "</div>\n" + this.$outline(b.$document()) + "\n</div>";
                return'<div id="preamble">\n<div class="sectionbody">\n' + b.$content() + "\n</div>" + d + "\n</div>"
            };
            h.$quote = function (b) {
                var c, f, d = a, e = a, g = e = a, l = a, k = a, s = a, s = l = a, d = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', e = ["quoteblock", b.$role()].$compact(), e = ' class="' + e["$*"](" ") + '"', g = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '\n<div class="title">' + b.$title() + "</div>", l = (c = b["$attr?"]("attribution")) ===
                    a || c._isBoolean && !0 != c ? a : b.$attr("attribution"), k = (c = b["$attr?"]("citetitle")) === a || c._isBoolean && !0 != c ? a : b.$attr("citetitle");
                (c = !1 !== (f = l) && f !== a ? f : k) === a || c._isBoolean && !0 != c ? s = a : (s = !1 !== k && k !== a ? "<cite>" + k + "</cite>" : a, l = !1 !== l && l !== a ? "" + (!1 !== k && k !== a ? "<br" + this.void_element_slash + ">\n" : a) + "&#8212; " + l : a, s = '\n<div class="attribution">\n' + s + l + "\n</div>");
                return"<div" + d + e + ">" + g + "\n<blockquote>\n" + b.$content() + "\n</blockquote>" + s + "\n</div>"
            };
            h.$thematic_break = function (a) {
                return"<hr" + this.void_element_slash +
                    ">"
            };
            h.$sidebar = function (b) {
                var c, f = a, d = a, e = a, f = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', d = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '<div class="title">' + b.$title() + "</div>\n";
                return"<div" + f + ' class="' + ((c = e = b.$role()) === a || c._isBoolean && !0 != c ? "sidebarblock" : ["sidebarblock", e]["$*"](" ")) + '">\n<div class="content">\n' + d + b.$content() + "\n</div>\n</div>"
            };
            h.$table = function (b) {
                var c, f, d, e, g, l, k, s, z, h, m = a, q = a, r = a, y = a, t = y = y = r = a, v = a, m = [], q = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' +
                    b.$id() + '"', r = ["tableblock", "frame-" + b.$attr("frame", "all"), "grid-" + b.$attr("grid", "all")];
                if ((c = y = b.$role()) !== a && (!c._isBoolean || !0 == c))r["$<<"](y);
                r = ' class="' + r["$*"](" ") + '"';
                y = [(c = b["$option?"]("autowidth")) === a || c._isBoolean && !0 != c ? "width: " + b.$attr("tablepcwidth") + "%;" : a, (c = b["$attr?"]("float")) === a || c._isBoolean && !0 != c ? a : "float: " + b.$attr("float") + ";"].$compact();
                y = y.$size()["$>"](0) ? ' style="' + y["$*"](" ") + '"' : a;
                m["$<<"]("<table" + q + r + y + ">");
                if ((c = b["$title?"]()) !== a && (!c._isBoolean || !0 ==
                    c))m["$<<"]('<caption class="title">' + b.$captioned_title() + "</caption>");
                b.$attr("rowcount")["$>"](0) && (t = this.void_element_slash, m["$<<"]("<colgroup>"), (c = b["$option?"]("autowidth")) === a || c._isBoolean && !0 != c ? (c = (e = b.$columns()).$each, c._p = (g = function (b) {
                    null == b && (b = a);
                    return m["$<<"]('<col style="width: ' + b.$attr("colpcwidth") + '%;"' + t + ">")
                }, g._s = this, g), c).call(e) : (v = "<col" + t + ">", (c = (f = b.$columns().$size()).$times, c._p = (d = function () {
                    return m["$<<"](v)
                }, d._s = this, d), c).call(f)), m["$<<"]("</colgroup>"),
                    (c = (l = (s = (z = ["head", "foot", "body"]).$select, s._p = (h = function (c) {
                        null == c && (c = a);
                        return b.$rows()["$[]"](c)["$empty?"]()["$!"]()
                    }, h._s = this, h), s).call(z)).$each, c._p = (k = function (c) {
                        var e = k._s || this, f, d, g;
                        null == c && (c = a);
                        m["$<<"]("<t" + c + ">");
                        (f = (d = b.$rows()["$[]"](c)).$each, f._p = (g = function (e) {
                            var f = g._s || this, d, k, l;
                            null == e && (e = a);
                            m["$<<"]("<tr>");
                            (d = (k = e).$each, d._p = (l = function (e) {
                                var f = l._s || this, d, g, k, p, s = a, n = a, z = n = a, h = a, G = a, q = a;
                                null == e && (e = a);
                                c["$=="]("head") ? s = e.$text() : (n = e.$style(), "asciidoc"["$==="](n) ?
                                    s = "<div>" + e.$content() + "</div>" : "verse"["$==="](n) ? s = '<div class="verse">' + e.$text() + "</div>" : "literal"["$==="](n) ? s = '<div class="literal"><pre>' + e.$text() + "</pre></div>" : (s = "", (d = (g = e.$content()).$each, d._p = (k = function (b) {
                                    null == b && (b = a);
                                    return s = "" + s + '<p class="tableblock">' + b + "</p>"
                                }, k._s = f, k), d).call(g)));
                                n = (d = !1 !== (p = c["$=="]("head")) && p !== a ? p : e.$style()["$=="]("header")) === a || d._isBoolean && !0 != d ? "td" : "th";
                                z = ' class="tableblock halign-' + e.$attr("halign") + " valign-" + e.$attr("valign") + '"';
                                h = (d =
                                    e.$colspan()) === a || d._isBoolean && !0 != d ? a : ' colspan="' + e.$colspan() + '"';
                                G = (d = e.$rowspan()) === a || d._isBoolean && !0 != d ? a : ' rowspan="' + e.$rowspan() + '"';
                                q = (d = b.$document()["$attr?"]("cellbgcolor")) === a || d._isBoolean && !0 != d ? a : ' style="background-color: ' + b.$document().$attr("cellbgcolor") + ';"';
                                return m["$<<"]("<" + n + z + h + G + q + ">" + s + "</" + n + ">")
                            }, l._s = f, l), d).call(k);
                            return m["$<<"]("</tr>")
                        }, g._s = e, g), f).call(d);
                        return m["$<<"]("</t" + c + ">")
                    }, k._s = this, k), c).call(l));
                m["$<<"]("</table>");
                return m["$*"](n.EOL)
            };
            h.$toc = function (b) {
                var c, f, d = a, e = a, g = a, l = a, k = a, s = a;
                if ((c = (d = b.$document())["$attr?"]("toc")) === a || c._isBoolean && !0 != c)return"\x3c!-- toc disabled --\x3e";
                (c = b.$id()) === a || c._isBoolean && !0 != c ? (c = !1 !== (f = d["$embedded?"]()) && f !== a ? f : d["$attr?"]("toc-placement")["$!"]()) === a || c._isBoolean && !0 != c ? g = e = a : (e = ' id="toc"', g = ' id="toctitle"') : (e = ' id="' + b.$id() + '"', g = "");
                l = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? d.$attr("toc-title") : b.$title();
                k = (c = b["$attr?"]("levels")) === a || c._isBoolean && !0 != c ? a : b.$attr("levels").$to_i();
                s = (c = b["$role?"]()) === a || c._isBoolean && !0 != c ? d.$attr("toc-class", "toc") : b.$role();
                return"<div" + e + ' class="' + s + '">\n<div' + g + ' class="title">' + l + "</div>\n" + this.$outline(d, w(["toclevels"], {toclevels: k})) + "\n</div>"
            };
            h.$ulist = function (b) {
                var c, f, d, e = a, g = a, l = a, k = a, s = a, z = a, h = a, e = [], g = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', l = ["ulist", b.$style(), b.$role()].$compact(), s = k = a;
                (c = z = b["$option?"]("checklist")) === a || c._isBoolean && !0 != c ? h = (c = b.$style()) === a || c._isBoolean && !0 != c ? a : ' class="' +
                    b.$style() + '"' : (l.$insert(1, "checklist"), h = ' class="checklist"', (c = b["$option?"]("interactive")) === a || c._isBoolean && !0 != c ? (c = b.$document()["$attr?"]("icons", "font")) === a || c._isBoolean && !0 != c ? (k = "&#10003; ", s = "&#10063; ") : (k = '<i class="icon-check"></i> ', s = '<i class="icon-check-empty"></i> ') : (c = b.$document()["$attr?"]("htmlsyntax", "xml")) === a || c._isBoolean && !0 != c ? (k = '<input type="checkbox" data-item-complete="1" checked> ', s = '<input type="checkbox" data-item-complete="0"> ') : (k = '<input type="checkbox" data-item-complete="1" checked="checked"/> ',
                    s = '<input type="checkbox" data-item-complete="0"/> '));
                e["$<<"]("<div" + g + ' class="' + l["$*"](" ") + '">');
                if ((c = b["$title?"]()) !== a && (!c._isBoolean || !0 == c))e["$<<"]('<div class="title">' + b.$title() + "</div>");
                e["$<<"]("<ul" + h + ">");
                (c = (f = b.$items()).$each, c._p = (d = function (b) {
                    var c, f;
                    null == b && (b = a);
                    e["$<<"]("<li>");
                    if ((c = (f = !1 !== z && z !== a) ? b["$attr?"]("checkbox") : f) === a || c._isBoolean && !0 != c)e["$<<"]("<p>" + b.$text() + "</p>"); else e["$<<"]("<p>" + ((c = b["$attr?"]("checked")) === a || c._isBoolean && !0 != c ? s : k) + b.$text() +
                        "</p>");
                    if ((c = b["$blocks?"]()) !== a && (!c._isBoolean || !0 == c))e["$<<"](b.$content());
                    return e["$<<"]("</li>")
                }, d._s = this, d), c).call(f);
                e["$<<"]("</ul>");
                e["$<<"]("</div>");
                return e["$*"](n.EOL)
            };
            h.$verse = function (b) {
                var c, f, d = a, e = a, g = e = a, l = a, k = a, s = a, s = l = a, d = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', e = ["verseblock", b.$role()].$compact(), e = ' class="' + e["$*"](" ") + '"', g = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '\n<div class="title">' + b.$title() + "</div>", l = (c = b["$attr?"]("attribution")) ===
                    a || c._isBoolean && !0 != c ? a : b.$attr("attribution"), k = (c = b["$attr?"]("citetitle")) === a || c._isBoolean && !0 != c ? a : b.$attr("citetitle");
                (c = !1 !== (f = l) && f !== a ? f : k) === a || c._isBoolean && !0 != c ? s = a : (s = !1 !== k && k !== a ? "<cite>" + k + "</cite>" : a, l = !1 !== l && l !== a ? "" + (!1 !== k && k !== a ? "<br" + this.void_element_slash + ">\n" : a) + "&#8212; " + l : a, s = '\n<div class="attribution">\n' + s + l + "\n</div>");
                return"<div" + d + e + ">" + g + '\n<pre class="content">' + b.$content() + "</pre>" + s + "\n</div>"
            };
            h.$video = function (b) {
                var c, f, d = this, e = a, g = a, l = a, k = a, s = a,
                    n = a, h = a, m = a, q = a, r = a, y = a, t = a, v = a, D = a, B = a, C = a, u = a, x = a, e = b.$document()["$attr?"]("htmlsyntax", "xml"), g = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="' + b.$id() + '"', l = ["videoblock", b.$style(), b.$role()].$compact(), k = ' class="' + l["$*"](" ") + '"', s = (c = b["$title?"]()) === a || c._isBoolean && !0 != c ? a : '\n<div class="title">' + b.$captioned_title() + "</div>", n = (c = b["$attr?"]("width")) === a || c._isBoolean && !0 != c ? a : ' width="' + b.$attr("width") + '"', h = (c = b["$attr?"]("height")) === a || c._isBoolean && !0 != c ? a : ' height="' + b.$attr("height") +
                        '"';
                return function () {
                    m = b.$attr("poster");
                    if ("vimeo"["$==="](m))return q = (c = b["$attr?"]("start")) === a || c._isBoolean && !0 != c ? a : "#at=" + b.$attr("start"), r = "?", y = (c = b["$option?"]("autoplay")) === a || c._isBoolean && !0 != c ? a : "" + r + "autoplay=1", !1 !== y && y !== a && (r = "&amp;"), t = (c = b["$option?"]("loop")) === a || c._isBoolean && !0 != c ? a : "" + r + "loop=1", "<div" + g + k + ">" + s + '\n<div class="content">\n<iframe' + n + h + ' src="//player.vimeo.com/video/' + b.$attr("target") + q + y + t + '" frameborder="0"' + d.$append_boolean_attribute("webkitAllowFullScreen",
                        e) + d.$append_boolean_attribute("mozallowfullscreen", e) + d.$append_boolean_attribute("allowFullScreen", e) + "></iframe>\n</div>\n</div>";
                    if ("youtube"["$==="](m))return v = (c = b["$attr?"]("start")) === a || c._isBoolean && !0 != c ? a : "&amp;start=" + b.$attr("start"), D = (c = b["$attr?"]("end")) === a || c._isBoolean && !0 != c ? a : "&amp;end=" + b.$attr("end"), y = (c = b["$option?"]("autoplay")) === a || c._isBoolean && !0 != c ? a : "&amp;autoplay=1", t = (c = b["$option?"]("loop")) === a || c._isBoolean && !0 != c ? a : "&amp;loop=1", B = (c = b["$option?"]("nocontrols")) ===
                        a || c._isBoolean && !0 != c ? a : "&amp;controls=0", "<div" + g + k + ">" + s + '\n<div class="content">\n<iframe' + n + h + ' src="//www.youtube.com/embed/' + b.$attr("target") + "?rel=0" + v + D + y + t + B + '" frameborder="0"' + ((c = b["$option?"]("nofullscreen")) === a || c._isBoolean && !0 != c ? d.$append_boolean_attribute("allowfullscreen", e) : a) + "></iframe>\n</div>\n</div>";
                    C = (c = ("" + (u = b.$attr("poster")))["$empty?"]()) === a || c._isBoolean && !0 != c ? ' poster="' + b.$media_uri(u) + '"' : a;
                    x = (c = !1 !== (f = b["$attr?"]("start")) && f !== a ? f : b["$attr?"]("end")) ===
                        a || c._isBoolean && !0 != c ? a : "#t=" + b.$attr("start") + ((c = b["$attr?"]("end")) === a || c._isBoolean && !0 != c ? a : ",") + b.$attr("end");
                    return"<div" + g + k + ">" + s + '\n<div class="content">\n<video src="' + b.$media_uri(b.$attr("target")) + x + '"' + n + h + C + ((c = b["$option?"]("autoplay")) === a || c._isBoolean && !0 != c ? a : d.$append_boolean_attribute("autoplay", e)) + ((c = b["$option?"]("nocontrols")) === a || c._isBoolean && !0 != c ? d.$append_boolean_attribute("controls", e) : a) + ((c = b["$option?"]("loop")) === a || c._isBoolean && !0 != c ? a : d.$append_boolean_attribute("loop",
                        e)) + ">\nYour browser does not support the video tag.\n</video>\n</div>\n</div>"
                }()
            };
            h.$inline_anchor = function (b) {
                var c, f, d = this, e = a, g = a, l = a, k = a, s = a, n = a, h = a, e = b.$target();
                return function () {
                    g = b.$type();
                    return"xref"["$==="](g) ? (l = !1 !== (c = b.$attr("refid")) && c !== a ? c : e, k = !1 !== (c = b.$text()) && c !== a ? c : !1 !== (f = b.$document().$references()["$[]"]("ids")["$[]"](l)) && f !== a ? f : "[" + l + "]", '<a href="' + e + '">' + k + "</a>") : "ref"["$==="](g) ? '<a id="' + e + '"></a>' : "link"["$==="](g) ? (s = (c = n = b.$role()) === a || c._isBoolean && !0 !=
                        c ? a : ' class="' + n + '"', h = (c = b["$attr?"]("window")) === a || c._isBoolean && !0 != c ? a : ' target="' + b.$attr("window") + '"', '<a href="' + e + '"' + s + h + ">" + b.$text() + "</a>") : "bibref"["$==="](g) ? '<a id="' + e + '"></a>[' + e + "]" : d.$warn("asciidoctor: WARNING: unknown anchor type: " + b.$type().$inspect())
                }()
            };
            h.$inline_break = function (a) {
                return"" + a.$text() + "<br" + this.void_element_slash + ">"
            };
            h.$inline_button = function (a) {
                return'<b class="button">' + a.$text() + "</b>"
            };
            h.$inline_callout = function (b) {
                var c, f = a;
                if ((c = b.$document()["$attr?"]("icons",
                    "font")) === a || c._isBoolean && !0 != c) {
                    if ((c = b.$document()["$attr?"]("icons")) === a || c._isBoolean && !0 != c)return"<b>(" + b.$text() + ")</b>";
                    f = b.$icon_uri("callouts/" + b.$text());
                    return'<img src="' + f + '" alt="' + b.$text() + '"' + this.void_element_slash + ">"
                }
                return'<i class="conum" data-value="' + b.$text() + '"></i><b>(' + b.$text() + ")</b>"
            };
            h.$inline_footnote = function (b) {
                var c, f = a, d = a;
                if ((c = f = b.$attr("index")) === a || c._isBoolean && !0 != c)return b.$type()["$=="]("xref") ? '<span class="footnoteref red" title="Unresolved footnote reference.">[' +
                    b.$text() + "]</span>" : a;
                if (b.$type()["$=="]("xref"))return'<span class="footnoteref">[<a class="footnote" href="#_footnote_' + f + '" title="View footnote.">' + f + "</a>]</span>";
                d = (c = b.$id()) === a || c._isBoolean && !0 != c ? a : ' id="_footnote_' + b.$id() + '"';
                return'<span class="footnote"' + d + '>[<a id="_footnoteref_' + f + '" class="footnote" href="#_footnote_' + f + '" title="View footnote.">' + f + "</a>]</span>"
            };
            h.$inline_image = function (b) {
                var c, f, d, e = a, g = a, l = a, k = e = l = l = g = g = a, l = a;
                (c = (f = (e = b.$type())["$=="]("icon")) ? b.$document()["$attr?"]("icons",
                    "font") : f) === a || c._isBoolean && !0 != c ? (c = (f = e["$=="]("icon")) ? b.$document()["$attr?"]("icons")["$!"]() : f) === a || c._isBoolean && !0 != c ? (g = (c = e["$=="]("icon")) === a || c._isBoolean && !0 != c ? b.$image_uri(b.$target()) : b.$icon_uri(b.$target()), l = (c = (f = ["alt", "width", "height", "title"]).$map, c._p = (d = function (c) {
                    var e;
                    null == c && (c = a);
                    return(e = b["$attr?"](c)) === a || e._isBoolean && !0 != e ? a : " " + c + '="' + b.$attr(c) + '"'
                }, d._s = this, d), c).call(f).$join(), g = '<img src="' + g + '"' + l + this.void_element_slash + ">") : g = "[" + b.$attr("alt") +
                    "]" : (g = "icon-" + b.$target(), (c = b["$attr?"]("size")) === a || c._isBoolean && !0 != c || (g = "" + g + " icon-" + b.$attr("size")), (c = b["$attr?"]("rotate")) === a || c._isBoolean && !0 != c || (g = "" + g + " icon-rotate-" + b.$attr("rotate")), (c = b["$attr?"]("flip")) === a || c._isBoolean && !0 != c || (g = "" + g + " icon-flip-" + b.$attr("flip")), l = (c = b["$attr?"]("title")) === a || c._isBoolean && !0 != c ? a : ' title="' + b.$attr("title") + '"', g = '<i class="' + g + '"' + l + "></i>");
                (c = b["$attr?"]("link")) === a || c._isBoolean && !0 != c || (l = (c = b["$attr?"]("window")) === a || c._isBoolean &&
                    !0 != c ? a : ' target="' + b.$attr("window") + '"', g = '<a class="image" href="' + b.$attr("link") + '"' + l + ">" + g + "</a>");
                e = (c = k = b.$role()) === a || c._isBoolean && !0 != c ? e : "" + e + " " + k;
                l = (c = b["$attr?"]("float")) === a || c._isBoolean && !0 != c ? a : ' style="float: ' + b.$attr("float") + '"';
                return'<span class="' + e + '"' + l + ">" + g + "</span>"
            };
            h.$inline_indexterm = function (a) {
                return a.$type()["$=="]("visible") ? a.$text() : ""
            };
            h.$inline_kbd = function (b) {
                var c, f, d, e = a, e = a;
                if ((e = b.$attr("keys")).$size()["$=="](1))return"<kbd>" + e["$[]"](0) + "</kbd>";
                e = (c = (f = e).$map, c._p = (d = function (b) {
                    null == b && (b = a);
                    return"<kbd>" + b + "</kbd>+"
                }, d._s = this, d), c).call(f).$join().$chop();
                return'<span class="keyseq">' + e + "</span>"
            };
            h.$inline_menu = function (b) {
                var c, f, d, e = a, g = a, l = g = a, e = b.$attr("menu");
                if ((c = (g = b.$attr("submenus"))["$empty?"]()["$!"]()) === a || c._isBoolean && !0 != c)return(c = l = b.$attr("menuitem")) === a || c._isBoolean && !0 != c ? '<span class="menu">' + e + "</span>" : '<span class="menuseq"><span class="menu">' + e + '</span>&#160;&#9656; <span class="menuitem">' + l + "</span></span>";
                g = (c = (f = g).$map, c._p = (d = function (b) {
                    null == b && (b = a);
                    return'<span class="submenu">' + b + "</span>&#160;&#9656; "
                }, d._s = this, d), c).call(f).$join().$chop();
                return'<span class="menuseq"><span class="menu">' + e + "</span>&#160;&#9656; " + g + ' <span class="menuitem">' + b.$attr("menuitem") + "</span></span>"
            };
            h.$inline_quoted = function (b) {
                var c, f = a, p = a, e = a, g = f = a;
                c = d.to_ary(n.QUOTE_TAGS["$[]"](b.$type()));
                f = null == c[0] ? a : c[0];
                p = null == c[1] ? a : c[1];
                e = null == c[2] ? a : c[2];
                f = (c = g = b.$role()) === a || c._isBoolean && !0 != c ? "" + f + b.$text() +
                    p : !1 !== e && e !== a ? "" + f.$chop() + ' class="' + g + '">' + b.$text() + p : '<span class="' + g + '">' + f + b.$text() + p + "</span>";
                return(c = b.$id()) === a || c._isBoolean && !0 != c ? f : '<a id="' + b.$id() + '"></a>' + f
            };
            return(h.$append_boolean_attribute = function (b, c) {
                return!1 !== c && c !== a ? " " + b + '="' + b + '"' : " " + b
            }, a) && "append_boolean_attribute"
        })(v.Converter, v.Converter._scope.BuiltIn)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (r) {
        r = u(r, "Asciidoctor")._scope;
        (function (r, $super) {
            function u() {
            }

            var m = u = t(r, $super, "DocBook5Converter", u), q = m._proto, h = m._scope;
            q.$document = function (d) {
                var b, c = a, f = a, p = a, p = p = a, c = [], f = d.$doctype();
                c["$<<"]('<?xml version="1.0" encoding="UTF-8"?>');
                if ((b = p = this.$doctype_declaration(f)) !== a && (!b._isBoolean || !0 == b))c["$<<"](p);
                if ((b = d["$attr?"]("toc")) !== a && (!b._isBoolean || !0 == b))c["$<<"]("<?asciidoc-toc?>");
                if ((b = d["$attr?"]("numbered")) !==
                    a && (!b._isBoolean || !0 == b))c["$<<"]("<?asciidoc-numbered?>");
                p = (b = d["$attr?"]("nolang")) === a || b._isBoolean && !0 != b ? ' lang="' + d.$attr("lang", "en") + '"' : a;
                c["$<<"]("<" + f + this.$document_ns_attributes(d) + p + ">");
                c["$<<"](this.$document_info_element(d, f));
                if ((b = d["$blocks?"]()) !== a && (!b._isBoolean || !0 == b))c["$<<"](d.$content());
                if ((b = (p = d.$docinfo("footer"))["$empty?"]()) === a || b._isBoolean && !0 != b)c["$<<"](p);
                c["$<<"]("</" + f + ">");
                return c["$*"](h.EOL)
            };
            d.defn(m, "$embedded", q.$content);
            q.$section = function (d) {
                var b,
                    c, f = a, f = (b = d.$special()) === a || b._isBoolean && !0 != b ? (b = (c = d.$document().$doctype()["$=="]("book")) ? d.$level()["$<="](1) : c) === a || b._isBoolean && !0 != b ? "section" : d.$level()["$=="](0) ? "part" : "chapter" : d.$level()["$<="](1) ? d.$sectname() : "section";
                return"<" + f + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n<title>" + d.$title() + "</title>\n" + d.$content() + "\n</" + f + ">"
            };
            q.$admonition = function (d) {
                var b = a;
                return"<" + (b = d.$attr("name")) + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n" + this.$title_tag(d) +
                    this.$resolve_content(d) + "\n</" + b + ">"
            };
            d.defn(m, "$audio", q.$skip);
            q.$colist = function (d) {
                var b, c, f, p = a, p = [];
                p["$<<"]("<calloutlist" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">");
                if ((b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b))p["$<<"]("<title>" + d.$title() + "</title>");
                (b = (c = d.$items()).$each, b._p = (f = function (b) {
                    var c;
                    null == b && (b = a);
                    p["$<<"]('<callout arearefs="' + b.$attr("coids") + '">');
                    p["$<<"]("<para>" + b.$text() + "</para>");
                    if ((c = b["$blocks?"]()) !== a && (!c._isBoolean || !0 == c))p["$<<"](b.$content());
                    return p["$<<"]("</callout>")
                }, f._s = this, f), b).call(c);
                p["$<<"]("</calloutlist>");
                return p["$*"](h.EOL)
            };
            d.cdecl(h, "DLIST_TAGS", w(["labeled", "qanda", "glossary"], {labeled: w(["list", "entry", "term", "item"], {list: "variablelist", entry: "varlistentry", term: "term", item: "listitem"}), qanda: w(["list", "entry", "label", "term", "item"], {list: "qandaset", entry: "qandaentry", label: "question", term: "simpara", item: "answer"}), glossary: w(["list", "entry", "term", "item"], {list: a, entry: "glossentry", term: "glossterm", item: "glossdef"})}));
            h.DLIST_TAGS["$default="](h.DLIST_TAGS["$[]"]("labeled"));
            q.$dlist = function (d) {
                var b, c, f, p, e, g = a, l = a, k = l = a, s = a, z = a, m = a, q = a, g = [];
                if (d.$style()["$=="]("horizontal"))g["$<<"]("<" + (l = (b = d["$title?"]()) === a || b._isBoolean && !0 != b ? "informaltable" : "table") + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ' tabstyle="horizontal" frame="none" colsep="0" rowsep="0">\n' + this.$title_tag(d) + '<tgroup cols="2">\n<colspec colwidth="' + d.$attr("labelwidth", 15) + '*"/>\n<colspec colwidth="' + d.$attr("itemwidth", 85) +
                    '*"/>\n<tbody valign="top">'), (b = (c = d.$items()).$each, b._p = (f = function (b, c) {
                    var e = f._s || this, d, k, l;
                    null == b && (b = a);
                    null == c && (c = a);
                    g["$<<"]("<row>\n<entry>");
                    (d = (k = [].concat(b)).$each, d._p = (l = function (b) {
                        null == b && (b = a);
                        return g["$<<"]("<simpara>" + b.$text() + "</simpara>")
                    }, l._s = e, l), d).call(k);
                    g["$<<"]("</entry>\n<entry>");
                    if ((d = c["$nil?"]()) === a || d._isBoolean && !0 != d) {
                        if ((d = c["$text?"]()) !== a && (!d._isBoolean || !0 == d))g["$<<"]("<simpara>" + c.$text() + "</simpara>");
                        if ((d = c["$blocks?"]()) !== a && (!d._isBoolean ||
                            !0 == d))g["$<<"](c.$content())
                    }
                    return g["$<<"]("</entry>\n</row>")
                }, f._s = this, f), b).call(c), g["$<<"]("</tbody>\n</tgroup>\n</" + l + ">"); else {
                    l = h.DLIST_TAGS["$[]"](d.$style());
                    k = l["$[]"]("list");
                    s = l["$[]"]("entry");
                    z = l["$[]"]("label");
                    m = l["$[]"]("term");
                    q = l["$[]"]("item");
                    if (!1 !== k && k !== a && (g["$<<"]("<" + k + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">"), (b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b)))g["$<<"]("<title>" + d.$title() + "</title>");
                    (b = (p = d.$items()).$each, b._p = (e = function (b, c) {
                        var d =
                            e._s || this, f, k, l;
                        null == b && (b = a);
                        null == c && (c = a);
                        g["$<<"]("<" + s + ">");
                        if (!1 !== z && z !== a)g["$<<"]("<" + z + ">");
                        (f = (k = [].concat(b)).$each, f._p = (l = function (b) {
                            null == b && (b = a);
                            return g["$<<"]("<" + m + ">" + b.$text() + "</" + m + ">")
                        }, l._s = d, l), f).call(k);
                        if (!1 !== z && z !== a)g["$<<"]("</" + z + ">");
                        g["$<<"]("<" + q + ">");
                        if ((f = c["$nil?"]()) === a || f._isBoolean && !0 != f) {
                            if ((f = c["$text?"]()) !== a && (!f._isBoolean || !0 == f))g["$<<"]("<simpara>" + c.$text() + "</simpara>");
                            if ((f = c["$blocks?"]()) !== a && (!f._isBoolean || !0 == f))g["$<<"](c.$content())
                        }
                        g["$<<"]("</" +
                            q + ">");
                        return g["$<<"]("</" + s + ">")
                    }, e._s = this, e), b).call(p);
                    if (!1 !== k && k !== a)g["$<<"]("</" + k + ">")
                }
                return g["$*"](h.EOL)
            };
            q.$example = function (d) {
                var b;
                return(b = d["$title?"]()) === a || b._isBoolean && !0 != b ? "<informalexample" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n" + this.$resolve_content(d) + "\n</informalexample>" : "<example" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n<title>" + d.$title() + "</title>\n" + this.$resolve_content(d) + "\n</example>"
            };
            q.$floating_title = function (a) {
                return"<bridgehead" +
                    this.$common_attributes(a.$id(), a.$role(), a.$reftext()) + ' renderas="sect' + a.$level() + '">' + a.$title() + "</bridgehead>"
            };
            q.$image = function (d) {
                var b, c = a, f = a, p = a, e = a, g = a, c = (b = d["$attr?"]("width")) === a || b._isBoolean && !0 != b ? a : ' contentwidth="' + d.$attr("width") + '"', f = (b = d["$attr?"]("height")) === a || b._isBoolean && !0 != b ? a : ' contentdepth="' + d.$attr("height") + '"', p = (b = d["$attr?"]("scaledwidth")) === a || b._isBoolean && !0 != b ? a : ' width="' + d.$attr("scaledwidth") + '" scalefit="1"', e = (b = d["$attr?"]("scale")) === a || b._isBoolean &&
                    !0 != b ? a : ' scale="' + d.$attr("scale") + '"', g = (b = d["$attr?"]("align")) === a || b._isBoolean && !0 != b ? a : ' align="' + d.$attr("align") + '"';
                return"<figure" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n" + this.$title_tag(d) + '<mediaobject>\n<imageobject>\n<imagedata fileref="' + d.$image_uri(d.$attr("target")) + '"' + c + f + p + e + g + "/>\n</imageobject>\n<textobject><phrase>" + d.$attr("alt") + "</phrase></textobject>\n</mediaobject>\n</figure>"
            };
            q.$listing = function (d) {
                var b, c, f = a, p = a, e = a, e = a, f = d["$title?"]()["$!"](),
                    p = this.$common_attributes(d.$id(), d.$role(), d.$reftext());
                (b = (c = d.$style()["$=="]("source")) ? d["$attr?"]("language") : c) === a || b._isBoolean && !0 != b ? e = "<screen" + (!1 !== f && f !== a ? p : a) + ">" + d.$content() + "</screen>" : (e = (b = d["$attr?"]("linenums")) === a || b._isBoolean && !0 != b ? "unnumbered" : "numbered", e = "<programlisting" + (!1 !== f && f !== a ? p : a) + ' language="' + d.$attr("language") + '" linenumbering="' + e + '">' + d.$content() + "</programlisting>");
                return!1 !== f && f !== a ? e : "<formalpara" + p + ">\n<title>" + d.$title() + "</title>\n<para>\n" +
                    e + "\n</para>\n</formalpara>"
            };
            q.$literal = function (d) {
                var b;
                return(b = d["$title?"]()) === a || b._isBoolean && !0 != b ? "<literallayout" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ' class="monospaced">' + d.$content() + "</literallayout>" : "<formalpara" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n<title>" + d.$title() + '</title>\n<para>\n<literallayout class="monospaced">' + d.$content() + "</literallayout>\n</para>\n</formalpara>"
            };
            q.$math = function (d) {
                var b, c = a, c = a, c = d.$content().$strip(),
                    c = d.$style()["$=="]("latexmath") ? "<alt><![CDATA[" + c + "]]\x3e</alt>\n<mediaobject><textobject><phrase></phrase></textobject></mediaobject>" : "<mediaobject><textobject><phrase><![CDATA[" + c + "]]\x3e</phrase></textobject></mediaobject>";
                return(b = d["$title?"]()) === a || b._isBoolean && !0 != b ? "<informalequation" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n" + c + "\n</informalequation>" : "<equation" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n<title>" + d.$title() + "</title>\n" + c + "\n</equation>"
            };
            q.$olist = function (d) {
                var b, c, f, p = a, e = a, p = [], e = (b = d.$style()) === a || b._isBoolean && !0 != b ? a : ' numeration="' + d.$style() + '"';
                p["$<<"]("<orderedlist" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + e + ">");
                if ((b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b))p["$<<"]("<title>" + d.$title() + "</title>");
                (b = (c = d.$items()).$each, b._p = (f = function (b) {
                    var c;
                    null == b && (b = a);
                    p["$<<"]("<listitem>");
                    p["$<<"]("<simpara>" + b.$text() + "</simpara>");
                    if ((c = b["$blocks?"]()) !== a && (!c._isBoolean || !0 == c))p["$<<"](b.$content());
                    return p["$<<"]("</listitem>")
                }, f._s = this, f), b).call(c);
                p["$<<"]("</orderedlist>");
                return p["$*"](h.EOL)
            };
            q.$open = function (d) {
                var b, c, f, p = this, e = a;
                return function () {
                    e = d.$style();
                    if ("abstract"["$==="](e)) {
                        if ((b = (c = d.$parent()["$=="](d.$document())) ? d.$document()["$attr?"]("doctype", "book") : c) === a || b._isBoolean && !0 != b)return"<abstract>\n" + p.$title_tag(d) + p.$resolve_content(d) + "\n</abstract>";
                        p.$warn("asciidoctor: WARNING: abstract block cannot be used in a document without a title when doctype is book. Excluding block content.");
                        return""
                    }
                    return"partintro"["$==="](e) ? (b = (c = (f = d.$level()["$=="](0)) ? d.$parent().$context()["$=="]("section") : f, !1 !== c && c !== a ? d.$document().$doctype()["$=="]("book") : c)) === a || b._isBoolean && !0 != b ? (p.$warn("asciidoctor: ERROR: partintro block can only be used when doctype is book and it's a child of a part section. Excluding block content."), "") : "<partintro" + p.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n" + p.$title_tag(d) + p.$resolve_content(d) + "\n</partintro>" : d.$content()
                }()
            };
            q.$page_break =
                function (a) {
                    return"<simpara><?asciidoc-pagebreak?></simpara>"
                };
            q.$paragraph = function (d) {
                var b;
                return(b = d["$title?"]()) === a || b._isBoolean && !0 != b ? "<simpara" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">" + d.$content() + "</simpara>" : "<formalpara" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">\n<title>" + d.$title() + "</title>\n<para>" + d.$content() + "</para>\n</formalpara>"
            };
            q.$preamble = function (a) {
                return a.$document().$doctype()["$=="]("book") ? "<preface" + this.$common_attributes(a.$id(),
                    a.$role(), a.$reftext()) + ">\n" + this.$title_tag(a, !1) + a.$content() + "\n</preface>" : a.$content()
            };
            q.$quote = function (d) {
                var b, c, f = a, f = [];
                f["$<<"]("<blockquote" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">");
                if ((b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b))f["$<<"]("<title>" + d.$title() + "</title>");
                if ((b = !1 !== (c = d["$attr?"]("attribution")) && c !== a ? c : d["$attr?"]("citetitle")) !== a && (!b._isBoolean || !0 == b)) {
                    f["$<<"]("<attribution>");
                    if ((b = d["$attr?"]("attribution")) !== a && (!b._isBoolean || !0 == b))f["$<<"](d.$attr("attribution"));
                    if ((b = d["$attr?"]("citetitle")) !== a && (!b._isBoolean || !0 == b))f["$<<"]("<citetitle>" + d.$attr("citetitle") + "</citetitle>");
                    f["$<<"]("</attribution>")
                }
                f["$<<"](this.$resolve_content(d));
                f["$<<"]("</blockquote>");
                return f["$*"](h.EOL)
            };
            q.$thematic_break = function (a) {
                return"<simpara><?asciidoc-hr?></simpara>"
            };
            q.$sidebar = function (a) {
                return"<sidebar" + this.$common_attributes(a.$id(), a.$role(), a.$reftext()) + ">\n" + this.$title_tag(a) + this.$resolve_content(a) + "\n</sidebar>"
            };
            d.cdecl(h, "TABLE_PI_NAMES", ["dbhtml",
                "dbfo", "dblatex"]);
            d.cdecl(h, "TABLE_SECTIONS", ["head", "foot", "body"]);
            q.$table = function (d) {
                var b, c, f, p, e, g, l, k, s, z, m = a, q = a, r = a, t = a, m = [], q = (b = d["$option?"]("pgwide")) === a || b._isBoolean && !0 != b ? a : ' pgwide="1"';
                m["$<<"]("<" + (r = (b = d["$title?"]()) === a || b._isBoolean && !0 != b ? "informaltable" : "table") + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + q + ' frame="' + d.$attr("frame", "all") + '" rowsep="' + ((b = ["none", "cols"]["$include?"](d.$attr("grid"))) === a || b._isBoolean && !0 != b ? 1 : 0) + '" colsep="' + ((b = ["none",
                    "rows"]["$include?"](d.$attr("grid"))) === a || b._isBoolean && !0 != b ? 1 : 0) + '">');
                if (r["$=="]("table"))m["$<<"]("<title>" + d.$title() + "</title>");
                (b = t = (c = d["$attr?"]("width")) === a || c._isBoolean && !0 != c ? a : d.$attr("width")) === a || b._isBoolean && !0 != b || (b = (c = h.TABLE_PI_NAMES).$each, b._p = (f = function (b) {
                    null == b && (b = a);
                    return m["$<<"]("<?" + b + ' table-width="' + t + '"?>')
                }, f._s = this, f), b).call(c);
                m["$<<"]('<tgroup cols="' + d.$attr("colcount") + '">');
                (b = (p = d.$columns()).$each, b._p = (e = function (b) {
                    null == b && (b = a);
                    return m["$<<"]('<colspec colname="col_' +
                        b.$attr("colnumber") + '" colwidth="' + b.$attr(!1 !== t && t !== a ? "colabswidth" : "colpcwidth") + '*"/>')
                }, e._s = this, e), b).call(p);
                (b = (g = (k = (s = h.TABLE_SECTIONS).$select, k._p = (z = function (b) {
                    null == b && (b = a);
                    return d.$rows()["$[]"](b)["$empty?"]()["$!"]()
                }, z._s = this, z), k).call(s)).$each, b._p = (l = function (b) {
                    var c = l._s || this, e, f, g;
                    null == b && (b = a);
                    m["$<<"]("<t" + b + ">");
                    (e = (f = d.$rows()["$[]"](b)).$each, e._p = (g = function (c) {
                        var e = g._s || this, f, k, l;
                        null == c && (c = a);
                        m["$<<"]("<row>");
                        (f = (k = c).$each, f._p = (l = function (c) {
                            var e =
                                l._s || this, f, g, k, p, s, z = a, h = a, q = a, R = a, r = a, da = h = z = a, q = a;
                            null == c && (c = a);
                            z = (f = c["$attr?"]("halign")) === a || f._isBoolean && !0 != f ? a : ' align="' + c.$attr("halign") + '"';
                            h = (f = c["$attr?"]("valign")) === a || f._isBoolean && !0 != f ? a : ' valign="' + c.$attr("valign") + '"';
                            q = (f = c.$colspan()) === a || f._isBoolean && !0 != f ? a : ' namest="col_' + (R = c.$column().$attr("colnumber")) + '" nameend="col_' + R["$+"](c.$colspan())["$-"](1) + '"';
                            r = (f = c.$rowspan()) === a || f._isBoolean && !0 != f ? a : ' morerows="' + c.$rowspan()["$-"](1) + '"';
                            z = "<entry" + z + h + q + r +
                                ">";
                            h = function () {
                                return b["$=="]("head") ? c.$text() : function () {
                                    da = c.$style();
                                    return"asciidoc"["$==="](da) ? c.$content() : "verse"["$==="](da) ? "<literallayout>" + c.$text() + "</literallayout>" : "literal"["$==="](da) ? '<literallayout class="monospaced">' + c.$text() + "</literallayout>" : "header"["$==="](da) ? (f = (g = c.$content()).$map, f._p = (k = function (b) {
                                        null == b && (b = a);
                                        return'<simpara><emphasis role="strong">' + b + "</emphasis></simpara>"
                                    }, k._s = e, k), f).call(g).$join() : (f = (p = c.$content()).$map, f._p = (s = function (b) {
                                        null ==
                                            b && (b = a);
                                        return"<simpara>" + b + "</simpara>"
                                    }, s._s = e, s), f).call(p).$join()
                                }()
                            }();
                            q = (f = d.$document()["$attr?"]("cellbgcolor")) === a || f._isBoolean && !0 != f ? "</entry>" : '<?dbfo bgcolor="' + d.$document().$attr("cellbgcolor") + '"?></entry>';
                            return m["$<<"]("" + z + h + q)
                        }, l._s = e, l), f).call(k);
                        return m["$<<"]("</row>")
                    }, g._s = c, g), e).call(f);
                    return m["$<<"]("</t" + b + ">")
                }, l._s = this, l), b).call(g);
                m["$<<"]("</tgroup>");
                m["$<<"]("</" + r + ">");
                return m["$*"](h.EOL)
            };
            d.defn(m, "$toc", q.$skip);
            q.$ulist = function (d) {
                var b, c, f, p,
                    e, g = a, l = a, k = a, l = a, g = [];
                if (d.$style()["$=="]("bibliography")) {
                    g["$<<"]("<bibliodiv" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">");
                    if ((b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b))g["$<<"]("<title>" + d.$title() + "</title>");
                    (b = (c = d.$items()).$each, b._p = (f = function (b) {
                        var c;
                        null == b && (b = a);
                        g["$<<"]("<bibliomixed>");
                        g["$<<"]("<bibliomisc>" + b.$text() + "</bibliomisc>");
                        if ((c = b["$blocks?"]()) !== a && (!c._isBoolean || !0 == c))g["$<<"](b.$content());
                        return g["$<<"]("</bibliomixed>")
                    }, f._s = this, f), b).call(c);
                    g["$<<"]("</bibliodiv>")
                } else {
                    l = (b = k = d["$option?"]("checklist")) === a || b._isBoolean && !0 != b ? d.$style() : "none";
                    l = !1 !== l && l !== a ? ' mark="' + l + '"' : a;
                    g["$<<"]("<itemizedlist" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + l + ">");
                    if ((b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b))g["$<<"]("<title>" + d.$title() + "</title>");
                    (b = (p = d.$items()).$each, b._p = (e = function (b) {
                        var c, d, e = a;
                        null == b && (b = a);
                        e = (c = (d = !1 !== k && k !== a) ? b["$attr?"]("checkbox") : d) === a || c._isBoolean && !0 != c ? a : (c = b["$attr?"]("checked")) === a ||
                            c._isBoolean && !0 != c ? "&#10063; " : "&#10003; ";
                        g["$<<"]("<listitem>");
                        g["$<<"]("<simpara>" + e + b.$text() + "</simpara>");
                        if ((c = b["$blocks?"]()) !== a && (!c._isBoolean || !0 == c))g["$<<"](b.$content());
                        return g["$<<"]("</listitem>")
                    }, e._s = this, e), b).call(p);
                    g["$<<"]("</itemizedlist>")
                }
                return g["$*"](h.EOL)
            };
            q.$verse = function (d) {
                var b, c, f = a, f = [];
                f["$<<"]("<blockquote" + this.$common_attributes(d.$id(), d.$role(), d.$reftext()) + ">");
                if ((b = d["$title?"]()) !== a && (!b._isBoolean || !0 == b))f["$<<"]("<title>" + d.$title() + "</title>");
                if ((b = !1 !== (c = d["$attr?"]("attribution")) && c !== a ? c : d["$attr?"]("citetitle")) !== a && (!b._isBoolean || !0 == b)) {
                    f["$<<"]("<attribution>");
                    if ((b = d["$attr?"]("attribution")) !== a && (!b._isBoolean || !0 == b))f["$<<"](d.$attr("attribution"));
                    if ((b = d["$attr?"]("citetitle")) !== a && (!b._isBoolean || !0 == b))f["$<<"]("<citetitle>" + d.$attr("citetitle") + "</citetitle>");
                    f["$<<"]("</attribution>")
                }
                f["$<<"]("<literallayout>" + d.$content() + "</literallayout>");
                f["$<<"]("</blockquote>");
                return f["$*"](h.EOL)
            };
            d.defn(m, "$video",
                q.$skip);
            q.$inline_anchor = function (d) {
                var b, c = this, f = a, p = a, e = a;
                return function () {
                    f = d.$type();
                    if ("ref"["$==="](f))return"<anchor" + c.$common_attributes(d.$target(), a, d.$text()) + "/>";
                    if ("xref"["$==="](f)) {
                        if ((b = d["$attr?"]("path", a)) === a || b._isBoolean && !0 != b)return'<link xlink:href="' + c.$target() + '">' + (!1 !== (b = d.$text()) && b !== a ? b : d.$attr("path")) + "</link>";
                        p = !1 !== (b = d.$attr("fragment")) && b !== a ? b : d.$target();
                        return(b = e = d.$text()) === a || b._isBoolean && !0 != b ? '<xref linkend="' + p + '"/>' : '<link linkend="' + p +
                            '">' + e + "</link>"
                    }
                    return"link"["$==="](f) ? '<link xlink:href="' + d.$target() + '">' + d.$text() + "</link>" : "bibref"["$==="](f) ? "<anchor" + c.$common_attributes(c.$target(), a, "[" + d.$target() + "]") + "/>[" + d.$target() + "]" : c.$warn("asciidoctor: WARNING: unknown anchor type: " + d.$type().$inspect())
                }()
            };
            q.$inline_break = function (a) {
                return"" + a.$text() + "<?asciidoc-br?>"
            };
            q.$inline_button = function (a) {
                return"<guibutton>" + a.$text() + "</guibutton>"
            };
            q.$inline_callout = function (a) {
                return"<co" + this.$common_attributes(a.$id()) +
                    "/>"
            };
            q.$inline_footnote = function (a) {
                return a.$type()["$=="]("xref") ? '<footnoteref linkend="' + a.$target() + '"/>' : "<footnote" + this.$common_attributes(a.$id()) + "><simpara>" + a.$text() + "</simpara></footnote>"
            };
            q.$inline_image = function (d) {
                var b, c = a, f = a, c = (b = d["$attr?"]("width")) === a || b._isBoolean && !0 != b ? a : ' contentwidth="' + d.$attr("width") + '"', f = (b = d["$attr?"]("height")) === a || b._isBoolean && !0 != b ? a : ' contentdepth="' + d.$attr("height") + '"';
                return'<inlinemediaobject>\n<imageobject>\n<imagedata fileref="' + (d.$type()["$=="]("icon") ?
                    d.$icon_uri(d.$target()) : d.$image_uri(d.$target())) + '"' + c + f + "/>\n</imageobject>\n<textobject><phrase>" + d.$attr("alt") + "</phrase></textobject>\n</inlinemediaobject>"
            };
            q.$inline_indexterm = function (d) {
                var b = a, c = a, f = a;
                if (d.$type()["$=="]("visible"))return"<indexterm><primary>" + d.$text() + "</primary></indexterm>" + d.$text();
                b = d.$attr("terms");
                c = [];
                if ((f = b.$size())["$>"](2))c["$<<"]("<indexterm>\n<primary>" + b["$[]"](0) + "</primary><secondary>" + b["$[]"](1) + "</secondary><tertiary>" + b["$[]"](2) + "</tertiary>\n</indexterm>");
                if (f["$>"](1))c["$<<"]("<indexterm>\n<primary>" + b["$[]"](-2) + "</primary><secondary>" + b["$[]"](-1) + "</secondary>\n</indexterm>");
                c["$<<"]("<indexterm>\n<primary>" + b["$[]"](-1) + "</primary>\n</indexterm>");
                return c["$*"](h.EOL)
            };
            q.$inline_kbd = function (d) {
                var b, c, f, p = a, p = a;
                if ((p = d.$attr("keys")).$size()["$=="](1))return"<keycap>" + p["$[]"](0) + "</keycap>";
                p = (b = (c = p).$map, b._p = (f = function (b) {
                    null == b && (b = a);
                    return"<keycap>" + b + "</keycap>"
                }, f._s = this, f), b).call(c).$join();
                return"<keycombo>" + p + "</keycombo>"
            };
            q.$inline_menu = function (d) {
                var b, c, f, p = a, e = a, g = e = a, p = d.$attr("menu");
                if ((b = (e = d.$attr("submenus"))["$empty?"]()["$!"]()) === a || b._isBoolean && !0 != b)return(b = g = d.$attr("menuitem")) === a || b._isBoolean && !0 != b ? "<guimenu>" + p + "</guimenu>" : "<menuchoice><guimenu>" + p + "</guimenu> <guimenuitem>" + g + "</guimenuitem></menuchoice>";
                e = (b = (c = e).$map, b._p = (f = function (b) {
                    null == b && (b = a);
                    return"<guisubmenu>" + b + "</guisubmenu> "
                }, f._s = this, f), b).call(c).$join().$chop();
                return"<menuchoice><guimenu>" + p + "</guimenu> " + e + " <guimenuitem>" +
                    d.$attr("menuitem") + "</guimenuitem></menuchoice>"
            };
            d.cdecl(h, "QUOTED_TAGS", w("emphasis strong monospaced superscript subscript double single".split(" "), {emphasis: ["<emphasis>", "</emphasis>"], strong: ['<emphasis role="strong">', "</emphasis>"], monospaced: ["<literal>", "</literal>"], superscript: ["<superscript>", "</superscript>"], subscript: ["<subscript>", "</subscript>"], "double": ["&#8220;", "&#8221;"], single: ["&#8216;", "&#8217;"]}));
            h.QUOTED_TAGS["$default="]([a, a]);
            q.$inline_quoted = function (m) {
                var b, c =
                    b = a, f = a, p = a, e = c = a;
                if ((b = m.$type())["$=="]("latexmath"))return"<inlineequation>\n<alt><![CDATA[" + m.$text() + "]]\x3e</alt>\n<inlinemediaobject><textobject><phrase><![CDATA[" + m.$text() + "]]\x3e</phrase></textobject></inlinemediaobject>\n</inlineequation>";
                b = d.to_ary(h.QUOTED_TAGS["$[]"](b));
                c = null == b[0] ? a : b[0];
                f = null == b[1] ? a : b[1];
                p = m.$text();
                c = (b = e = m.$role()) === a || b._isBoolean && !0 != b ? "" + c + p + f : "" + c + '<phrase role="' + e + '">' + p + "</phrase>" + f;
                return(b = m.$id()) === a || b._isBoolean && !0 != b ? c : "<anchor" + this.$common_attributes(m.$id(),
                    a, p) + "/>" + c
            };
            q.$author_element = function (d, b) {
                var c, f = a, p = a, e = a, g = a, l = a;
                null == b && (b = a);
                f = !1 !== b && b !== a ? "firstname_" + b : "firstname";
                p = !1 !== b && b !== a ? "middlename_" + b : "middlename";
                e = !1 !== b && b !== a ? "lastname_" + b : "lastname";
                g = !1 !== b && b !== a ? "email_" + b : "email";
                l = [];
                l["$<<"]("<author>");
                l["$<<"]("<personname>");
                if ((c = d["$attr?"](f)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<firstname>" + d.$attr(f) + "</firstname>");
                if ((c = d["$attr?"](p)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<othername>" + d.$attr(p) + "</othername>");
                if ((c = d["$attr?"](e)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<surname>" + d.$attr(e) + "</surname>");
                l["$<<"]("</personname>");
                if ((c = d["$attr?"](g)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<email>" + d.$attr(g) + "</email>");
                l["$<<"]("</author>");
                return l["$*"](h.EOL)
            };
            q.$common_attributes = function (d, b, c) {
                var f = a;
                null == b && (b = a);
                null == c && (c = a);
                f = !1 !== d && d !== a ? ' xml:id="' + d + '"' : "";
                !1 !== b && b !== a && (f = "" + f + ' role="' + b + '"');
                !1 !== c && c !== a && (f = "" + f + ' xreflabel="' + c + '"');
                return f
            };
            q.$doctype_declaration = function (a) {
                return""
            };
            q.$document_info_element = function (d, b, c) {
                var f, p, e, g, l, k = a, s = a, s = a;
                null == c && (c = !1);
                if (!1 === c || c === a)b = "";
                k = [];
                k["$<<"]("<" + b + "info>");
                if ((f = d.$notitle()) === a || f._isBoolean && !0 != f)k["$<<"]((f = d["$header?"]()) === a || f._isBoolean && !0 != f ? "<title>" + d.$attr("untitled-label") + "</title>" : this.$document_title_tags(d.$header().$title()));
                k["$<<"]("<date>" + ((f = d["$attr?"]("revdate")) === a || f._isBoolean && !0 != f ? d.$attr("docdate") : d.$attr("revdate")) + "</date>");
                if ((f = d["$has_header?"]()) !== a && (!f._isBoolean || !0 ==
                    f)) {
                    if ((f = d["$attr?"]("author")) !== a && (!f._isBoolean || !0 == f))if ((s = d.$attr("authorcount").$to_i())["$<"](2)) {
                        if (k["$<<"](this.$author_element(d)), (f = d["$attr?"]("authorinitials")) !== a && (!f._isBoolean || !0 == f))k["$<<"]("<authorinitials>" + d.$attr("authorinitials") + "</authorinitials>")
                    } else k["$<<"]("<authorgroup>"), (f = (p = s).$times, f._p = (e = function (b) {
                        var c = e._s || this;
                        null == b && (b = a);
                        return k["$<<"](c.$author_element(d, b["$+"](1)))
                    }, e._s = this, e), f).call(p), k["$<<"]("</authorgroup>");
                    if ((f = (g = d["$attr?"]("revdate"),
                        !1 !== g && g !== a ? !1 !== (l = d["$attr?"]("revnumber")) && l !== a ? l : d["$attr?"]("revremark") : g)) !== a && (!f._isBoolean || !0 == f)) {
                        k["$<<"]("<revhistory>\n<revision>");
                        if ((f = d["$attr?"]("revnumber")) !== a && (!f._isBoolean || !0 == f))k["$<<"]("<revnumber>" + d.$attr("revnumber") + "</revnumber>");
                        if ((f = d["$attr?"]("revdate")) !== a && (!f._isBoolean || !0 == f))k["$<<"]("<date>" + d.$attr("revdate") + "</date>");
                        if ((f = d["$attr?"]("authorinitials")) !== a && (!f._isBoolean || !0 == f))k["$<<"]("<authorinitials>" + d.$attr("authorinitials") + "</authorinitials>");
                        if ((f = d["$attr?"]("revremark")) !== a && (!f._isBoolean || !0 == f))k["$<<"]("<revremark>" + d.$attr("revremark") + "</revremark>");
                        k["$<<"]("</revision>\n</revhistory>")
                    }
                    if ((f = (s = d.$docinfo("header"))["$empty?"]()) === a || f._isBoolean && !0 != f)k["$<<"](s);
                    if ((f = d["$attr?"]("orgname")) !== a && (!f._isBoolean || !0 == f))k["$<<"]("<orgname>" + d.$attr("orgname") + "</orgname>")
                }
                k["$<<"]("</" + b + "info>");
                return k["$*"](h.EOL)
            };
            q.$document_ns_attributes = function (a) {
                return' xmlns="http://docbook.org/ns/docbook" xmlns:xlink="http://www.w3.org/1999/xlink" version="5.0"'
            };
            q.$document_title_tags = function (h) {
                var b, c = a;
                if ((b = h["$include?"](": ")) === a || b._isBoolean && !0 != b)return"<title>" + h + "</title>";
                b = d.to_ary(h.$rpartition(": "));
                h = null == b[0] ? a : b[0];
                c = null == b[2] ? a : b[2];
                return"<title>" + h + "</title>\n<subtitle>" + c + "</subtitle>"
            };
            q.$resolve_content = function (a) {
                return a.$content_model()["$=="]("compound") ? a.$content() : "<simpara>" + a.$content() + "</simpara>"
            };
            return(q.$title_tag = function (d, b) {
                var c, f;
                null == b && (b = !0);
                return(c = !1 !== (f = b["$!"]()) && f !== a ? f : d["$title?"]()) === a ||
                    c._isBoolean && !0 != c ? a : "<title>" + d.$title() + "</title>\n"
            }, a) && "title_tag"
        })(r.Converter, r.Converter._scope.BuiltIn)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass;
    return function (w) {
        w = u(w, "Asciidoctor")._scope;
        (function (r, $super) {
            function u() {
            }

            var w = u = t(r, $super, "DocBook45Converter", u), m = w._proto, q = w._scope, h;
            m.$inline_anchor = function (d) {
                var b, c = this, f = a, p = a, e = a, g = a, f = d.$target();
                return function () {
                    p = d.$type();
                    if ("ref"["$==="](p))return"<anchor" + c.$common_attributes(f, a, d.$text()) + "/>";
                    if ("xref"["$==="](p)) {
                        if ((b = d["$attr?"]("path", a)) === a || b._isBoolean && !0 != b)return g = !1 !== (b = d.$text()) && b !== a ? b : d.$attr("path"),
                            '<ulink url="' + f + '">' + g + "</ulink>";
                        e = !1 !== (b = d.$attr("fragment")) && b !== a ? b : f;
                        return(b = g = d.$text()) === a || b._isBoolean && !0 != b ? '<xref linkend="' + e + '"/>' : '<link linkend="' + e + '">' + g + "</link>"
                    }
                    return"link"["$==="](p) ? '<ulink url="' + f + '">' + d.$text() + "</ulink>" : "bibref"["$==="](p) ? "<anchor" + c.$common_attributes(f, a, "[" + f + "]") + "/>[" + f + "]" : a
                }()
            };
            m.$author_element = function (d, b) {
                var c, f = a, p = a, e = a, g = a, l = a;
                null == b && (b = a);
                f = !1 !== b && b !== a ? "firstname_" + b : "firstname";
                p = !1 !== b && b !== a ? "middlename_" + b : "middlename";
                e =
                    !1 !== b && b !== a ? "lastname_" + b : "lastname";
                g = !1 !== b && b !== a ? "email_" + b : "email";
                l = [];
                l["$<<"]("<author>");
                if ((c = d["$attr?"](f)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<firstname>" + d.$attr(f) + "</firstname>");
                if ((c = d["$attr?"](p)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<othername>" + d.$attr(p) + "</othername>");
                if ((c = d["$attr?"](e)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<surname>" + d.$attr(e) + "</surname>");
                if ((c = d["$attr?"](g)) !== a && (!c._isBoolean || !0 == c))l["$<<"]("<email>" + d.$attr(g) + "</email>");
                l["$<<"]("</author>");
                return l["$*"](q.EOL)
            };
            m.$common_attributes = function (d, b, c) {
                var f = a;
                null == b && (b = a);
                null == c && (c = a);
                f = !1 !== d && d !== a ? ' id="' + d + '"' : "";
                !1 !== b && b !== a && (f = "" + f + ' role="' + b + '"');
                !1 !== c && c !== a && (f = "" + f + ' xreflabel="' + c + '"');
                return f
            };
            m.$doctype_declaration = function (a) {
                return"<!DOCTYPE " + a + ' PUBLIC "-//OASIS//DTD DocBook XML V4.5//EN" "http://www.oasis-open.org/docbook/xml/4.5/docbookx.dtd">'
            };
            m.$document_info_element = h = function (a, b) {
                h._p = null;
                return d.find_super_dispatcher(this, "document_info_element", h,
                    null).apply(this, [a, b, !0])
            };
            return(m.$document_ns_attributes = function (d) {
                var b;
                return(b = d["$attr?"]("noxmlns")) === a || b._isBoolean && !0 != b ? ' xmlns="http://docbook.org/ns/docbook"' : a
            }, a) && "document_ns_attributes"
        })(w.Converter, w.Converter._scope.DocBook5Converter)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.klass, r = d.hash2, v = d.range;
    return function (x) {
        x = t(x, "Asciidoctor");
        (function (t, $super) {
            function q() {
            }

            var h = q = w(t, $super, "Document", q), n = h._proto, b = h._scope, c, f, p, e;
            n.attributes = n.safe = n.reader = n.callouts = n.base_dir = n.parsed = n.parent_document = n.extensions = n.options = n.counters = n.references = n.doctype = n.backend = n.header = n.blocks = n.attributes_modified = n.id = n.original_attributes = n.attribute_overrides = n.converter = a;
            d.cdecl(b, "Footnote", (null == (c = d.Object._scope.Struct) ?
                d.cm("Struct") : c).$new("index", "id", "text"));
            (function (b, $super) {
                function c() {
                }

                var d = c = w(b, $super, "AttributeEntry", c), e = d._proto;
                d.$attr_reader("name", "value", "negate");
                e.$initialize = function (b, c, d) {
                    var e;
                    null == d && (d = a);
                    this.name = b;
                    this.value = c;
                    return this.negate = (e = d["$nil?"]()) === a || e._isBoolean && !0 != e ? d : c["$nil?"]()
                };
                return(e.$save_to = function (b) {
                    var c;
                    return(!1 !== (c = b["$[]"]("attribute_entries")) && c !== a ? c : b["$[]="]("attribute_entries", []))["$<<"](this)
                }, a) && "save_to"
            })(h, null);
            h.$attr_reader("safe");
            h.$attr_reader("references");
            h.$attr_reader("counters");
            h.$attr_reader("callouts");
            h.$attr_reader("header");
            h.$attr_reader("base_dir");
            h.$attr_reader("parent_document");
            h.$attr_reader("reader");
            h.$attr_reader("converter");
            h.$attr_reader("extensions");
            n.$initialize = f = function (c, e) {
                var k, p, z, h, q, n, t, y, M, u, D, B = a, C = a, x = a, w = a, F = w = a, A = a, L = w = w = a, U = B = w = B = a, S = a;
                null == c && (c = a);
                null == e && (e = r([], {}));
                f._p = null;
                d.find_super_dispatcher(this, "initialize", f, null).apply(this, [this, "document"]);
                if ((k = B = e.$delete("parent")) ===
                    a || k._isBoolean && !0 != k) {
                    this.parent_document = a;
                    this.references = r("ids footnotes links images indexterms includes".split(" "), {ids: r([], {}), footnotes: [], links: [], images: [], indexterms: [], includes: (null == (k = d.Object._scope.Set) ? d.cm("Set") : k).$new()});
                    C = r([], {});
                    (k = (z = !1 !== (q = e["$[]"]("attributes")) && q !== a ? q : r([], {})).$each, k._p = (p = function (b, c) {
                        var d;
                        null == b && (b = a);
                        null == c && (c = a);
                        (d = b["$start_with?"]("!")) === a || d._isBoolean && !0 != d ? (d = b["$end_with?"]("!")) === a || d._isBoolean && !0 != d || (b = b.$chop(), c = a) :
                            (b = b["$[]"](v(1, -1, !1)), c = a);
                        return C["$[]="](b.$downcase(), c)
                    }, p._s = this, p), k).call(z);
                    this.attribute_overrides = C;
                    this.converter = this.safe = a;
                    try {
                        x = null != (null == (k = d.Object._scope.Asciidoctor) ? d.cm("Asciidoctor") : k)._scope.Extensions ? "constant" : a
                    } catch ($) {
                        if ($._klass === Opal.NameError)x = a; else throw $;
                    }
                    this.extensions = a
                } else this.parent_document = B, k = "base_dir", p = e, !1 !== (z = p["$[]"](k)) && z !== a ? z : p["$[]="](k, B.$base_dir()), this.references = (k = (p = B.$references()).$inject, k._p = (h = function (b, c) {
                    null == b &&
                    (b = a);
                    key = c[0];
                    ref = c[1];
                    if (key["$=="]("footnotes"))b["$[]="]("footnotes", []); else b["$[]="](key, ref);
                    return b
                }, h._s = this, h), k).call(p, r([], {})), C = B.$attributes().$dup(), C.$delete("doctype"), this.attribute_overrides = C, this.safe = B.$safe(), this.converter = B.$converter(), x = !1, this.extensions = B.$extensions();
                this.parsed = !1;
                this.header = a;
                this.counters = r([], {});
                this.callouts = b.Callouts.$new();
                this.attributes_modified = (null == (k = d.Object._scope.Set) ? d.cm("Set") : k).$new();
                this.options = e;
                if (!1 === B || B === a)if ((k =
                    (w = e["$[]"]("safe"))["$!"]()) === a || k._isBoolean && !0 != k)if ((k = w["$is_a?"](null == (q = d.Object._scope.Fixnum) ? d.cm("Fixnum") : q)) === a || k._isBoolean && !0 != k) {
                    var Q;
                    try {
                        Q = b.SafeMode.$const_get(w.$to_s().$upcase()).$to_i()
                    } catch (V) {
                        Q = b.SafeMode._scope.SECURE.$to_i()
                    }
                    this.safe = Q
                } else this.safe = w; else this.safe = b.SafeMode._scope.SECURE;
                w = (k = "header_footer", q = e, !1 !== (n = q["$[]"](k)) && n !== a ? n : q["$[]="](k, !1));
                F = this.attributes;
                F["$[]="]("encoding", "UTF-8");
                F["$[]="]("sectids", "");
                if (!1 === w || w === a)F["$[]="]("notitle",
                    "");
                F["$[]="]("toc-placement", "auto");
                F["$[]="]("stylesheet", "");
                if (!1 !== w && w !== a)F["$[]="]("copycss", "");
                F["$[]="]("prewrap", "");
                F["$[]="]("attribute-undefined", b.Compliance.$attribute_undefined());
                F["$[]="]("attribute-missing", b.Compliance.$attribute_missing());
                F["$[]="]("caution-caption", "Caution");
                F["$[]="]("important-caption", "Important");
                F["$[]="]("note-caption", "Note");
                F["$[]="]("tip-caption", "Tip");
                F["$[]="]("warning-caption", "Warning");
                F["$[]="]("appendix-caption", "Appendix");
                F["$[]="]("example-caption",
                    "Example");
                F["$[]="]("figure-caption", "Figure");
                F["$[]="]("table-caption", "Table");
                F["$[]="]("toc-title", "Table of Contents");
                F["$[]="]("manname-title", "NAME");
                F["$[]="]("untitled-label", "Untitled");
                F["$[]="]("version-label", "Version");
                F["$[]="]("last-update-label", "Last updated");
                C["$[]="]("asciidoctor", "");
                C["$[]="]("asciidoctor-version", b.VERSION);
                A = (k = (q = b.SafeMode.$constants()).$detect, k._p = (t = function (c) {
                    var d = t._s || this;
                    null == d.safe && (d.safe = a);
                    null == c && (c = a);
                    return b.SafeMode.$const_get(c)["$=="](d.safe)
                },
                    t._s = this, t), k).call(q).$to_s().$downcase();
                C["$[]="]("safe-mode-name", A);
                C["$[]="]("safe-mode-" + A, "");
                C["$[]="]("safe-mode-level", this.safe);
                C["$[]="]("embedded", !1 !== w && w !== a ? a : "");
                k = "max-include-depth";
                n = C;
                !1 !== (y = n["$[]"](k)) && y !== a ? y : n["$[]="](k, 64);
                if ((k = C["$[]"]("allow-uri-read")["$nil?"]()["$!"]()) === a || k._isBoolean && !0 != k)C["$[]="]("allow-uri-read", a);
                C["$[]="]("user-home", b.USER_HOME);
                (k = e["$[]"]("base_dir")) === a || k._isBoolean && !0 != k ? (k = C["$[]"]("docdir")) === a || k._isBoolean && !0 != k ? this.base_dir =
                    C["$[]="]("docdir", (null == (k = d.Object._scope.File) ? d.cm("File") : k).$expand_path((null == (k = d.Object._scope.Dir) ? d.cm("Dir") : k).$pwd())) : this.base_dir = C["$[]="]("docdir", (null == (k = d.Object._scope.File) ? d.cm("File") : k).$expand_path(C["$[]"]("docdir"))) : this.base_dir = C["$[]="]("docdir", (null == (k = d.Object._scope.File) ? d.cm("File") : k).$expand_path(e["$[]"]("base_dir")));
                if ((k = w = e["$[]"]("backend")) !== a && (!k._isBoolean || !0 == k))C["$[]="]("backend", "" + w);
                if ((k = w = e["$[]"]("doctype")) !== a && (!k._isBoolean || !0 ==
                    k))C["$[]="]("doctype", "" + w);
                if (this.safe["$>="](b.SafeMode._scope.SERVER)) {
                    k = "copycss";
                    n = C;
                    !1 !== (y = n["$[]"](k)) && y !== a ? y : n["$[]="](k, a);
                    k = "source-highlighter";
                    n = C;
                    !1 !== (y = n["$[]"](k)) && y !== a ? y : n["$[]="](k, a);
                    k = "backend";
                    n = C;
                    !1 !== (y = n["$[]"](k)) && y !== a ? y : n["$[]="](k, b.DEFAULT_BACKEND);
                    if ((k = (n = B["$!"](), !1 !== n && n !== a ? C["$key?"]("docfile") : n)) !== a && (!k._isBoolean || !0 == k))C["$[]="]("docfile", C["$[]"]("docfile")["$[]"](v(C["$[]"]("docdir").$length()["$+"](1), -1, !1)));
                    C["$[]="]("docdir", "");
                    C["$[]="]("user-home",
                        ".");
                    if (this.safe["$>="](b.SafeMode._scope.SECURE)) {
                        if ((k = C.$fetch("linkcss", "")["$nil?"]()) === a || k._isBoolean && !0 != k)C["$[]="]("linkcss", "");
                        k = "icons";
                        n = C;
                        !1 !== (y = n["$[]"](k)) && y !== a ? y : n["$[]="](k, a)
                    }
                }
                (k = (n = C).$delete_if, k._p = (M = function (b, c) {
                    var e, f, g, k = a;
                    null == b && (b = a);
                    null == c && (c = a);
                    k = !1;
                    (e = c["$nil?"]()) === a || e._isBoolean && !0 != e ? ((e = (f = c["$is_a?"](null == (g = d.Object._scope.String) ? d.cm("String") : g), !1 !== f && f !== a ? c["$end_with?"]("@") : f)) === a || e._isBoolean && !0 != e || (c = c.$chop(), k = !0), F["$[]="](b,
                        c)) : F.$delete(b);
                    return k
                }, M._s = this, M), k).call(n);
                if (!1 !== B && B !== a)return this.reader = b.Reader.$new(c, e["$[]"]("cursor")), b.Parser.$parse(this.reader, this), this.callouts.$rewind(), this.parsed = !0;
                k = "backend";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, b.DEFAULT_BACKEND);
                k = "doctype";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, b.DEFAULT_DOCTYPE);
                this.$update_backend_attributes(F["$[]"]("backend"), !0);
                L = (null == (k = d.Object._scope.Time) ? d.cm("Time") : k).$now();
                B = (k = "localdate", y = F, !1 !== (u = y["$[]"](k)) &&
                    u !== a ? u : y["$[]="](k, L.$strftime("%Y-%m-%d")));
                w = (k = "localtime", y = F, !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, function () {
                    try {
                        return L.$strftime("%H:%M:%S %Z")
                    } catch (a) {
                        return L.$strftime("%H:%M:%S")
                    }
                }()));
                k = "localdatetime";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, "" + B + " " + w);
                k = "docdate";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, B);
                k = "doctime";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, w);
                k = "docdatetime";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, "" + B + " " + w);
                k = "stylesdir";
                y = F;
                !1 !== (u =
                    y["$[]"](k)) && u !== a ? u : y["$[]="](k, ".");
                k = "iconsdir";
                y = F;
                !1 !== (u = y["$[]"](k)) && u !== a ? u : y["$[]="](k, (null == (D = d.Object._scope.File) ? d.cm("File") : D).$join(F.$fetch("imagesdir", "./images"), "icons"));
                !1 !== x && x !== a ? (B = (k = U = e["$[]"]("extensions_registry")) === a || k._isBoolean && !0 != k ? (k = (S = e["$[]"]("extensions"))["$is_a?"](null == (y = d.Object._scope.Proc) ? d.cm("Proc") : y)) === a || k._isBoolean && !0 != k ? a : (k = (y = b.Extensions).$build_registry, k._p = S.$to_proc(), k).call(y) : (k = !1 !== (y = U["$is_a?"](b.Extensions._scope.Registry)) &&
                    y !== a ? y : (u = null == (D = d.Object._scope.RUBY_ENGINE_JRUBY) ? d.cm("RUBY_ENGINE_JRUBY") : D, !1 !== u && u !== a ? U["$is_a?"]((null == (D = d.Object._scope.AsciidoctorJ) ? d.cm("AsciidoctorJ") : D)._scope.Extensions._scope.ExtensionRegistry) : u)) === a || k._isBoolean && !0 != k ? a : U, z = (!1 !== (k = B) && k !== a ? k : B = b.Extensions._scope.Registry.$new()).$activate(this)) : z = a;
                this.extensions = z;
                this.reader = b.PreprocessorReader.$new(this, c, b.Reader._scope.Cursor.$new(F["$[]"]("docfile"), this.base_dir));
                return(k = (u = !1 !== c && c !== a) ? e["$[]"]("parse") :
                    u) === a || k._isBoolean && !0 != k ? a : this.$parse()
            };
            n.$parse = function (c) {
                var d, e, f, p, h, q = a;
                null == c && (c = a);
                if ((d = this.parsed) === a || d._isBoolean && !0 != d)!1 !== c && c !== a && (this.reader = b.PreprocessorReader.$new(this, c, b.Reader._scope.Cursor.$new(this.attributes["$[]"]("docfile"), this.base_dir))), (d = (e = q = (f = this.parent_document) === a || f._isBoolean && !0 != f ? this.extensions : a, !1 !== e && e !== a ? q["$preprocessors?"]() : e)) === a || d._isBoolean && !0 != d || (d = (e = q.$preprocessors()).$each, d._p = (p = function (b) {
                    var c = p._s || this, d;
                    null ==
                        c.reader && (c.reader = a);
                    null == b && (b = a);
                    return c.reader = !1 !== (d = b.$process_method()["$[]"](c, c.reader)) && d !== a ? d : c.reader
                }, p._s = this, p), d).call(e), b.Parser.$parse(this.reader, this, r(["header_only"], {header_only: this.options["$[]"]("parse_header_only")["$!"]()["$!"]()})), this.callouts.$rewind(), (d = (f = !1 !== q && q !== a) ? q["$treeprocessors?"]() : f) === a || d._isBoolean && !0 != d || (d = (f = q.$treeprocessors()).$each, d._p = (h = function (b) {
                    var c = h._s || this;
                    null == b && (b = a);
                    return b.$process_method()["$[]"](c)
                }, h._s = this, h),
                    d).call(f), this.parsed = !0;
                return this
            };
            n.$counter = function (b, c) {
                var d, e, f = a, p = a;
                null == c && (c = a);
                if ((d = (e = f = (p = this.attributes["$[]"](b))["$nil_or_empty?"]()["$!"](), !1 !== e && e !== a ? this.counters["$key?"](b) : e)) === a || d._isBoolean && !0 != d)(d = c["$nil?"]()) === a || d._isBoolean && !0 != d ? c.$to_i().$to_s()["$=="](c) && (c = c.$to_i()) : c = this.$nextval(!1 !== f && f !== a ? p : 0), this.counters["$[]="](b, c); else this.counters["$[]="](b, this.$nextval(p));
                return this.attributes["$[]="](b, this.counters["$[]"](b))
            };
            n.$counter_increment =
                function (c, d) {
                    var e = a, e = this.$counter(c);
                    b.AttributeEntry.$new(c, e).$save_to(d.$attributes());
                    return e
                };
            n.$nextval = function (b) {
                var c, e, f = a;
                return(c = b["$is_a?"](null == (e = d.Object._scope.Integer) ? d.cm("Integer") : e)) === a || c._isBoolean && !0 != c ? (f = b.$to_i(), (c = f.$to_s()["$=="](b.$to_s())["$!"]()) === a || c._isBoolean && !0 != c ? f["$+"](1) : b["$[]"](0).$ord()["$+"](1).$chr()) : b["$+"](1)
            };
            n.$register = function (b, c) {
                var e, f, p = a, p = b;
                return"ids"["$==="](p) ? (e = c["$is_a?"](null == (f = d.Object._scope.Array) ? d.cm("Array") :
                    f)) === a || e._isBoolean && !0 != e ? this.references["$[]"]("ids")["$[]="](c, "["["$+"](c)["$+"]("]")) : this.references["$[]"]("ids")["$[]="](c["$[]"](0), !1 !== (e = c["$[]"](1)) && e !== a ? e : "["["$+"](c["$[]"](0))["$+"]("]")) : "footnotes"["$==="](p) || "indexterms"["$==="](p) ? this.references["$[]"](b)["$<<"](c) : (e = this.options["$[]"]("catalog_assets")) === a || e._isBoolean && !0 != e ? a : this.references["$[]"](b)["$<<"](c)
            };
            n["$footnotes?"] = function () {
                return this.references["$[]"]("footnotes")["$empty?"]()["$!"]()
            };
            n.$footnotes =
                function () {
                    return this.references["$[]"]("footnotes")
                };
            n["$nested?"] = function () {
                return this.parent_document["$!"]()["$!"]()
            };
            n["$embedded?"] = function () {
                return this.attributes["$key?"]("embedded")
            };
            n["$extensions?"] = function () {
                return this.extensions["$!"]()["$!"]()
            };
            n.$source = function () {
                var b;
                return(b = this.reader) === a || b._isBoolean && !0 != b ? a : this.reader.$source()
            };
            n.$source_lines = function () {
                var b;
                return(b = this.reader) === a || b._isBoolean && !0 != b ? a : this.reader.$source_lines()
            };
            n.$doctype = function () {
                var b;
                return!1 !== (b = this.doctype) && b !== a ? b : this.doctype = this.attributes["$[]"]("doctype")
            };
            n.$backend = function () {
                var b;
                return!1 !== (b = this.backend) && b !== a ? b : this.backend = this.attributes["$[]"]("backend")
            };
            n["$basebackend?"] = function (a) {
                return this.attributes["$[]"]("basebackend")["$=="](a)
            };
            n.$title = function () {
                return this.attributes["$[]"]("title")
            };
            n["$title="] = function (c) {
                var d;
                !1 !== (d = this.header) && d !== a ? d : this.header = b.Section.$new(this, 0);
                return this.header["$title="](c)
            };
            n.$doctitle = function (c) {
                var d,
                    e, f = a, f = a;
                null == c && (c = r([], {}));
                if ((d = this.attributes.$fetch("title", "")["$empty?"]()["$!"]()) === a || d._isBoolean && !0 != d) {
                    if ((d = (e = f = this.$first_section(), !1 !== e && e !== a ? f["$title?"]() : e)) === a || d._isBoolean && !0 != d)return a;
                    f = f.$title()
                } else f = this.$title();
                return(d = (e = c["$[]"]("sanitize"), !1 !== e && e !== a ? f["$include?"]("<") : e)) === a || d._isBoolean && !0 != d ? f : f.$gsub(b.XmlSanitizeRx, "").$tr_s(" ", " ").$strip()
            };
            d.defn(h, "$name", n.$doctitle);
            n.$author = function () {
                return this.attributes["$[]"]("author")
            };
            n.$revdate =
                function () {
                    return this.attributes["$[]"]("revdate")
                };
            n.$notitle = function () {
                var b;
                return b = this.attributes["$key?"]("showtitle")["$!"](), !1 !== b && b !== a ? this.attributes["$key?"]("notitle") : b
            };
            n.$noheader = function () {
                return this.attributes["$key?"]("noheader")
            };
            n.$nofooter = function () {
                return this.attributes["$key?"]("nofooter")
            };
            n.$first_section = function () {
                var b, c, d, e;
                return(b = this["$has_header?"]()) === a || b._isBoolean && !0 != b ? (b = (c = !1 !== (e = this.blocks) && e !== a ? e : []).$detect, b._p = (d = function (b) {
                    null == b && (b =
                        a);
                    return b.$context()["$=="]("section")
                }, d._s = this, d), b).call(c) : this.header
            };
            n["$has_header?"] = function () {
                var b;
                return(b = this.header) === a || b._isBoolean && !0 != b ? !1 : !0
            };
            d.defn(h, "$header?", n["$has_header?"]);
            n["$<<"] = p = function (b) {
                var c = u.call(arguments, 0), e = p._p;
                p._p = null;
                d.find_super_dispatcher(this, "<<", p, e).apply(this, c);
                return b.$context()["$=="]("section") ? this.$assign_index(b) : a
            };
            n.$finalize_header = function (b, c) {
                null == c && (c = !0);
                this.$clear_playback_attributes(b);
                this.$save_attributes();
                if (!1 ===
                    c || c === a)b["$[]="]("invalid-header", !0);
                return b
            };
            n.$save_attributes = function () {
                var c, d, e, f, p, h, q = a, n = a, r = a, t = a, v = a, u = a, r = n = a;
                if (this.attributes["$[]"]("basebackend")["$=="]("docbook")) {
                    if ((c = !1 !== (d = this["$attribute_locked?"]("toc")) && d !== a ? d : this.attributes_modified["$include?"]("toc")) === a || c._isBoolean && !0 != c)this.attributes["$[]="]("toc", "");
                    if ((c = !1 !== (d = this["$attribute_locked?"]("numbered")) && d !== a ? d : this.attributes_modified["$include?"]("numbered")) === a || c._isBoolean && !0 != c)this.attributes["$[]="]("numbered",
                        "")
                }
                if ((c = !1 !== (d = this.attributes["$key?"]("doctitle")) && d !== a ? d : (q = this.$doctitle())["$!"]()) === a || c._isBoolean && !0 != c)this.attributes["$[]="]("doctitle", q);
                if ((c = this.id) === a || c._isBoolean && !0 != c)this.id = this.attributes["$[]"]("css-signature");
                n = this.attributes["$[]"]("toc");
                r = this.attributes["$[]"]("toc2");
                t = this.attributes["$[]"]("toc-position");
                if ((c = !1 !== (d = (e = !1 !== n && n !== a) ? !1 !== (f = n["$=="]("")["$!"]()) && f !== a ? f : t["$nil_or_empty?"]()["$!"]() : e) && d !== a ? d : r) !== a && (!c._isBoolean || !0 == c)) {
                    v = "left";
                    u = "toc2";
                    n = (c = (d = [t, r, n]).$find, c._p = (p = function (b) {
                        null == b && (b = a);
                        return b["$nil_or_empty?"]()["$!"]()
                    }, p._s = this, p), c).call(d);
                    (c = (e = n["$!"](), !1 !== e && e !== a ? r : e)) === a || c._isBoolean && !0 != c || (n = v);
                    this.attributes["$[]="]("toc", "");
                    r = n;
                    if ("left"["$==="](r) || "<"["$==="](r) || "&lt;"["$==="](r))this.attributes["$[]="]("toc-position", "left"); else if ("right"["$==="](r) || ">"["$==="](r) || "&gt;"["$==="](r))this.attributes["$[]="]("toc-position", "right"); else if ("top"["$==="](r) || "^"["$==="](r))this.attributes["$[]="]("toc-position",
                        "top"); else if ("bottom"["$==="](r) || "v"["$==="](r))this.attributes["$[]="]("toc-position", "bottom"); else"preamble"["$==="](r) ? (this.attributes.$delete("toc2"), this.attributes["$[]="]("toc-placement", "preamble"), v = u = a) : "default"["$==="](r) && (this.attributes.$delete("toc2"), u = a, v = "default");
                    !1 !== u && u !== a && (c = "toc-class", e = this.attributes, !1 !== (f = e["$[]"](c)) && f !== a ? f : e["$[]="](c, u));
                    !1 !== v && v !== a && (c = "toc-position", e = this.attributes, !1 !== (f = e["$[]"](c)) && f !== a ? f : e["$[]="](c, v))
                }
                this.original_attributes =
                    this.attributes.$dup();
                return(c = this["$nested?"]()) === a || c._isBoolean && !0 != c ? (c = (e = b.FLEXIBLE_ATTRIBUTES).$each, c._p = (h = function (b) {
                    var c = h._s || this, d, e;
                    null == c.attribute_overrides && (c.attribute_overrides = a);
                    null == b && (b = a);
                    return(d = (e = c.attribute_overrides["$key?"](b), !1 !== e && e !== a ? c.attribute_overrides["$[]"](b)["$nil?"]()["$!"]() : e)) === a || d._isBoolean && !0 != d ? a : c.attribute_overrides.$delete(b)
                }, h._s = this, h), c).call(e) : a
            };
            n.$restore_attributes = function () {
                return this.attributes = this.original_attributes
            };
            n.$clear_playback_attributes = function (a) {
                return a.$delete("attribute_entries")
            };
            n.$playback_attributes = function (b) {
                var c, d, e;
                return(c = b["$key?"]("attribute_entries")) === a || c._isBoolean && !0 != c ? a : (c = (d = b["$[]"]("attribute_entries")).$each, c._p = (e = function (b) {
                    var c = e._s || this, d;
                    null == c.attributes && (c.attributes = a);
                    null == b && (b = a);
                    return(d = b.$negate()) === a || d._isBoolean && !0 != d ? c.attributes["$[]="](b.$name(), b.$value()) : c.attributes.$delete(b.$name())
                }, e._s = this, e), c).call(d)
            };
            n.$set_attribute = function (b, c) {
                var d, e = a;
                if ((d = this["$attribute_locked?"](b)) === a || d._isBoolean && !0 != d) {
                    e = b;
                    if ("backend"["$==="](e))this.$update_backend_attributes(this.$apply_attribute_value_subs(c)); else if ("doctype"["$==="](e))this.$update_doctype_attributes(this.$apply_attribute_value_subs(c)); else this.attributes["$[]="](b, this.$apply_attribute_value_subs(c));
                    this.attributes_modified["$<<"](b);
                    return!0
                }
                return!1
            };
            n.$delete_attribute = function (b) {
                var c;
                return(c = this["$attribute_locked?"](b)) === a || c._isBoolean && !0 != c ? (this.attributes.$delete(b),
                    this.attributes_modified["$<<"](b), !0) : !1
            };
            n["$attribute_locked?"] = function (a) {
                return this.attribute_overrides["$key?"](a)
            };
            n.$apply_attribute_value_subs = function (c) {
                var d, e = a, f = a;
                if ((d = e = b.AttributeEntryPassMacroRx.$match(c)) === a || d._isBoolean && !0 != d)return this.$apply_header_subs(c);
                if ((d = e["$[]"](1)["$empty?"]()["$!"]()) === a || d._isBoolean && !0 != d)return e["$[]"](2);
                f = this.$resolve_pass_subs(e["$[]"](1));
                return(d = f["$empty?"]()) === a || d._isBoolean && !0 != d ? this.$apply_subs(e["$[]"](2), f) : e["$[]"](2)
            };
            n.$update_backend_attributes = function (c, d) {
                var e, f, p, h = a, q = a, n = a, r = a, t = a, M = q = a, u = a, D = a, M = a;
                null == d && (d = !1);
                if ((e = !1 !== (f = d) && f !== a ? f : (p = !1 !== c && c !== a) ? c["$=="](this.attributes["$[]"]("backend"))["$!"]() : p) === a || e._isBoolean && !0 != e)return a;
                h = this.attributes;
                q = h["$[]"]("backend");
                n = h["$[]"]("basebackend");
                r = h["$[]"]("doctype");
                if ((e = c["$start_with?"]("xhtml")) !== a && (!e._isBoolean || !0 == e))h["$[]="]("htmlsyntax", "xml"), c = c["$[]"](v(1, -1, !1)); else if ((e = c["$start_with?"]("html")) !== a && (!e._isBoolean ||
                    !0 == e))h["$[]="]("htmlsyntax", "html");
                (e = t = b.BACKEND_ALIASES["$[]"](c)) === a || e._isBoolean && !0 != e || (c = t);
                !1 !== q && q !== a && (h.$delete("backend-" + q), !1 !== r && r !== a && h.$delete("backend-" + q + "-doctype-" + r));
                !1 !== r && r !== a && (h["$[]="]("doctype-" + r, ""), h["$[]="]("backend-" + c + "-doctype-" + r, ""));
                h["$[]="]("backend", c);
                h["$[]="]("backend-" + c, "");
                if ((e = (this.converter = this.$create_converter())["$is_a?"](b.Converter._scope.BackendInfo)) === a || e._isBoolean && !0 != e) {
                    if (q = c.$sub(b.TrailingDigitsRx, ""), u = !1 !== (e = b.DEFAULT_EXTENSIONS["$[]"](q)) &&
                        e !== a ? e : ".html", M = u["$[]"](v(1, -1, !1)), (e = this["$attribute_locked?"]("outfilesuffix")) === a || e._isBoolean && !0 != e)h["$[]="]("outfilesuffix", u)
                } else q = this.converter.$basebackend(), h["$[]="]("outfilesuffix", this.converter.$outfilesuffix()), M = this.converter.$filetype();
                (e = D = h["$[]"]("filetype")) === a || e._isBoolean && !0 != e || h.$delete("filetype-" + D);
                h["$[]="]("filetype", M);
                h["$[]="]("filetype-" + M, "");
                if ((e = M = b.DEFAULT_PAGE_WIDTHS["$[]"](q)) === a || e._isBoolean && !0 != e)h.$delete("pagewidth"); else h["$[]="]("pagewidth",
                    M);
                if ((e = q["$=="](n)["$!"]()) !== a && (!e._isBoolean || !0 == e) && (!1 !== n && n !== a && (h.$delete("basebackend-" + n), !1 !== r && r !== a && h.$delete("basebackend-" + n + "-doctype-" + r)), h["$[]="]("basebackend", q), h["$[]="]("basebackend-" + q, ""), !1 !== r && r !== a))h["$[]="]("basebackend-" + q + "-doctype-" + r, "");
                return this.backend = a
            };
            n.$update_doctype_attributes = function (b) {
                var c, d, e = a, f = a, p = a, h = a;
                if ((c = (d = !1 !== b && b !== a) ? b["$=="](this.attributes["$[]"]("doctype"))["$!"]() : d) === a || c._isBoolean && !0 != c)return a;
                e = this.attributes;
                f =
                    e["$[]"]("doctype");
                p = e["$[]"]("backend");
                h = e["$[]"]("basebackend");
                !1 !== f && f !== a && (e.$delete("doctype-" + f), !1 !== p && p !== a && e.$delete("backend-" + p + "-doctype-" + f), !1 !== h && h !== a && e.$delete("basebackend-" + h + "-doctype-" + f));
                e["$[]="]("doctype", b);
                e["$[]="]("doctype-" + b, "");
                if (!1 !== p && p !== a)e["$[]="]("backend-" + p + "-doctype-" + b, "");
                if (!1 !== h && h !== a)e["$[]="]("basebackend-" + h + "-doctype-" + b, "");
                return this.doctype = a
            };
            n.$create_converter = function () {
                var c, e = a, f = a, p = a, h = a, q = a, e = r([], {});
                e["$[]="]("htmlsyntax",
                    this.attributes["$[]"]("htmlsyntax"));
                f = (c = p = this.options["$[]"]("template_dir")) === a || c._isBoolean && !0 != c ? (c = f = this.options["$[]"]("template_dirs")) === a || c._isBoolean && !0 != c ? a : e["$[]="]("template_dirs", f) : e["$[]="]("template_dirs", [p]);
                !1 !== f && f !== a && (e["$[]="]("template_cache", this.options.$fetch("template_cache", !0)), e["$[]="]("template_engine", this.options["$[]"]("template_engine")), e["$[]="]("template_engine_options", this.options["$[]"]("template_engine_options")), e["$[]="]("eruby", this.options["$[]"]("eruby")));
                h = (c = q = this.options["$[]"]("converter")) === a || c._isBoolean && !0 != c ? b.Converter._scope.Factory.$default(!1) : b.Converter._scope.Factory.$new((null == (c = d.Object._scope.Hash) ? d.cm("Hash") : c)["$[]"](this.$backend(), q));
                return h.$create(this.$backend(), e)
            };
            n.$convert = function (b) {
                var c, d, e, f = a, p = a, h = a, q = a;
                null == b && (b = r([], {}));
                ((c = this.parsed) === a || c._isBoolean && !0 != c) && this.$parse();
                this.$restore_attributes();
                this.$doctype()["$=="]("inline") ? p = (c = (d = f = this.blocks["$[]"](0), !1 !== d && d !== a ? f.$content_model()["$=="]("compound")["$!"]() :
                    d)) === a || c._isBoolean && !0 != c ? "" : f.$content() : (h = (c = (d = b["$key?"]("header_footer")) === a || d._isBoolean && !0 != d ? this.options["$[]"]("header_footer") : b["$[]"]("header_footer")) === a || c._isBoolean && !0 != c ? "embedded" : "document", p = this.converter.$convert(this, h));
                !((c = this.parent_document) === a || c._isBoolean && !0 != c) || (c = (d = q = this.extensions, !1 !== d && d !== a ? q["$postprocessors?"]() : d)) === a || c._isBoolean && !0 != c || (c = (d = q.$postprocessors()).$each, c._p = (e = function (b) {
                    var c = e._s || this;
                    null == b && (b = a);
                    return p = b.$process_method()["$[]"](c,
                        p)
                }, e._s = this, e), c).call(d);
                return p
            };
            d.defn(h, "$render", n.$convert);
            n.$write = function (c, e) {
                var f, p, h, q;
                return(f = this.converter["$is_a?"](b.Writer)) === a || f._isBoolean && !0 != f ? ((f = e["$respond_to?"]("write")) === a || f._isBoolean && !0 != f ? (f = (p = null == (q = d.Object._scope.File) ? d.cm("File") : q).$open, f._p = (h = function (b) {
                    null == b && (b = a);
                    return b.$write(c)
                }, h._s = this, h), f).call(p, e, "w") : (e.$write(c.$chomp()), e.$write(b.EOL)), a) : this.converter.$write(c, e)
            };
            n.$content = e = function () {
                var a = u.call(arguments, 0), b = e._p;
                e._p = null;
                this.attributes.$delete("title");
                return d.find_super_dispatcher(this, "content", e, b).apply(this, a)
            };
            n.$docinfo = function (c, e) {
                var f, p, h, q = a, n = a, r = a, t = a, y = a, r = t = q = a;
                null == c && (c = "header");
                null == e && (e = a);
                if (this.$safe()["$>="](b.SafeMode._scope.SECURE))return"";
                q = "footer"["$==="](c) ? "-footer" : a;
                (f = e["$nil?"]()) === a || f._isBoolean && !0 != f || (e = this.attributes["$[]"]("outfilesuffix"));
                n = a;
                r = this.attributes["$key?"]("docinfo");
                t = this.attributes["$key?"]("docinfo1");
                y = this.attributes["$key?"]("docinfo2");
                q = "docinfo" + q + e;
                (f = !1 !== (p = t) && p !== a ? p : y) !== a && (!f._isBoolean || !0 == f) && (t = this.$normalize_system_path(q), n = this.$read_asset(t), (f = n["$nil?"]()) === a || f._isBoolean && !0 != f) && ((f = b.FORCE_ENCODING) === a || f._isBoolean && !0 != f || n.$force_encoding((null == (f = d.Object._scope.Encoding) ? d.cm("Encoding") : f)._scope.UTF_8), n = this.$sub_attributes(n.$split(b.EOL))["$*"](b.EOL));
                (f = (p = !1 !== (h = r) && h !== a ? h : y, !1 !== p && p !== a ? this.attributes["$key?"]("docname") : p)) !== a && (!f._isBoolean || !0 == f) && (t = this.$normalize_system_path("" +
                    this.attributes["$[]"]("docname") + "-" + q), r = this.$read_asset(t), (f = r["$nil?"]()) === a || f._isBoolean && !0 != f) && ((f = b.FORCE_ENCODING) === a || f._isBoolean && !0 != f || r.$force_encoding((null == (f = d.Object._scope.Encoding) ? d.cm("Encoding") : f)._scope.UTF_8), r = this.$sub_attributes(r.$split(b.EOL))["$*"](b.EOL), n = (f = n["$nil?"]()) === a || f._isBoolean && !0 != f ? "" + n + b.EOL + r : r);
                return n.$to_s()
            };
            return(n.$to_s = function () {
                var b;
                return"#<" + this.$class() + "@" + this.$object_id() + " {doctype: " + this.$doctype().$inspect() + ", doctitle: " +
                    ((b = this.header["$=="](a)["$!"]()) === a || b._isBoolean && !0 != b ? a : this.header.$title()).$inspect() + ", blocks: " + this.blocks.$size() + "}>"
            }, a) && "to_s"
        })(x, x._scope.AbstractBlock)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (r) {
        r = u(r, "Asciidoctor");
        (function (r, $super) {
            function u() {
            }

            var m = u = t(r, $super, "Inline", u), q = m._proto, h;
            m.$attr_reader("text");
            m.$attr_reader("type");
            m.$attr_accessor("target");
            q.$initialize = h = function (m, b, c, f) {
                var p, e = a;
                null == c && (c = a);
                null == f && (f = w([], {}));
                h._p = null;
                d.find_super_dispatcher(this, "initialize", h, null).apply(this, [m, b]);
                this.node_name = "inline_" + b;
                this.text = c;
                this.id = f["$[]"]("id");
                this.type = f["$[]"]("type");
                this.target =
                    f["$[]"]("target");
                return(p = (e = f["$[]"]("attributes"))["$nil_or_empty?"]()) === a || p._isBoolean && !0 != p ? this.$update_attributes(e) : a
            };
            q["$block?"] = function () {
                return!1
            };
            q["$inline?"] = function () {
                return!0
            };
            q.$convert = function () {
                return this.$converter().$convert(this)
            };
            return d.defn(m, "$render", q.$convert)
        })(r, r._scope.AbstractNode)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.klass;
    return function (r) {
        r = t(r, "Asciidoctor");
        var v = r._scope;
        (function (r, $super) {
            function m() {
            }

            var q = m = w(r, $super, "List", m), h = q._proto, n, b;
            h.blocks = h.context = h.document = h.style = a;
            d.defn(q, "$items", h.$blocks);
            d.defn(q, "$items?", h["$blocks?"]);
            h.$initialize = n = function (a, b) {
                var p = u.call(arguments, 0), e = n._p;
                n._p = null;
                return d.find_super_dispatcher(this, "initialize", n, e).apply(this, p)
            };
            h.$content = function () {
                return this.blocks
            };
            h.$convert = b = function () {
                var c =
                    u.call(arguments, 0), f = b._p, p = a;
                b._p = null;
                return this.context["$=="]("colist") ? (p = d.find_super_dispatcher(this, "convert", b, f).apply(this, c), this.document.$callouts().$next_list(), p) : d.find_super_dispatcher(this, "convert", b, f).apply(this, c)
            };
            d.defn(q, "$render", h.$convert);
            return(h.$to_s = function () {
                return"#<" + this.$class() + "@" + this.$object_id() + " {context: " + this.context.$inspect() + ", style: " + this.style.$inspect() + ", items: " + this.$items().$size() + "}>"
            }, a) && "to_s"
        })(r, v.AbstractBlock);
        (function (r, $super) {
            function m() {
            }

            var q = m = w(r, $super, "ListItem", m), h = q._proto, n = q._scope, b;
            h.text = h.blocks = a;
            q.$attr_accessor("marker");
            h.$initialize = b = function (c, f) {
                null == f && (f = a);
                b._p = null;
                d.find_super_dispatcher(this, "initialize", b, null).apply(this, [c, "list_item"]);
                this.text = f;
                return this.level = c.$level()
            };
            h["$text?"] = function () {
                return this.text["$nil_or_empty?"]()["$!"]()
            };
            h.$text = function () {
                return this.$apply_subs(this.text)
            };
            h.$fold_first = function (b, d) {
                var p, e, g, l, k, s, h = a, m = a;
                null == b && (b = !1);
                null == d && (d = !1);
                (p = (e = (g = h = this.blocks["$[]"](0),
                    !1 !== g && g !== a ? h["$is_a?"](n.Block) : g), !1 !== e && e !== a ? !1 !== (g = (l = h.$context()["$=="]("paragraph")) ? b["$!"]() : l) && g !== a ? g : (l = (k = !1 !== (s = d) && s !== a ? s : b["$!"](), !1 !== k && k !== a ? h.$context()["$=="]("literal") : k), !1 !== l && l !== a ? h["$option?"]("listparagraph") : l) : e)) === a || p._isBoolean && !0 != p || (m = this.$blocks().$shift(), ((p = this.text["$nil_or_empty?"]()) === a || p._isBoolean && !0 != p) && m.$lines().$unshift(this.text), this.text = m.$source());
                return a
            };
            return(h.$to_s = function () {
                var b;
                return"#<" + this.$class() + "@" + this.$object_id() +
                    " {list_context: " + this.$parent().$context().$inspect() + ", text: " + this.text.$inspect() + ", blocks: " + (!1 !== (b = this.blocks) && b !== a ? b : []).$size() + "}>"
            }, a) && "to_s"
        })(r, v.AbstractBlock)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.breaker, t = d.module, w = d.klass, r = d.hash2, v = d.range, x = d.gvars;
    return function (A) {
        (function (m, $super) {
            function h() {
            }

            var n = h = w(m, $super, "Parser", h), b = n._proto, c = n._scope;
            d.cdecl(c, "BlockMatchData", c.Struct.$new("context", "masq", "tip", "terminator"));
            b.$initialize = function () {
                return this.$raise("Au contraire, mon frere. No lexer instances will be running around.")
            };
            d.defs(n, "$parse", function (b, c, e) {
                var g, l, k = a, s = a;
                null == e && (e = r([], {}));
                k = this.$parse_document_header(b, c);
                if ((g =
                    e["$[]"]("header_only")) === a || g._isBoolean && !0 != g)for (; (l = b["$has_more_lines?"]()) !== a && (!l._isBoolean || !0 == l);)if (l = d.to_ary(this.$next_section(b, c, k)), s = null == l[0] ? a : l[0], k = null == l[1] ? a : l[1], !1 !== s && s !== a)c["$<<"](s);
                return c
            });
            d.defs(n, "$parse_document_header", function (b, c) {
                var e, g, l = a, k = a, s = a, h = a, h = a, l = this.$parse_block_metadata_lines(b, c);
                if ((e = l["$has_key?"]("title")) !== a && (!e._isBoolean || !0 == e))return c.$finalize_header(l, !1);
                k = a;
                if ((e = (s = c.$attributes()["$[]"]("doctitle"))["$nil_or_empty?"]()) ===
                    a || e._isBoolean && !0 != e)c["$title="](s), k = s;
                h = a;
                if ((e = this["$is_next_line_document_title?"](b, l)) !== a && (!e._isBoolean || !0 == e)) {
                    e = d.to_ary(this.$parse_section_title(b, c));
                    c["$id="](null == e[0] ? a : e[0]);
                    h = null == e[2] ? a : e[2];
                    if (!1 === k || k === a)c["$title="](h), k = h;
                    c.$attributes()["$[]="]("doctitle", h);
                    if ((e = c.$id()) === a || e._isBoolean && !0 != e)c["$id="](l.$delete("id"));
                    this.$parse_header_metadata(b, c)
                }
                (e = (g = (s = c.$attributes()["$[]"]("doctitle"))["$nil_or_empty?"]()["$!"](), !1 !== g && g !== a ? s["$=="](h)["$!"]() : g)) ===
                    a || e._isBoolean && !0 != e || (c["$title="](s), k = s);
                if (!1 !== k && k !== a)c.$attributes()["$[]="]("doctitle", k);
                c.$doctype()["$=="]("manpage") && this.$parse_manpage_header(b, c);
                return c.$finalize_header(l)
            });
            d.defs(n, "$parse_manpage_header", function (b, d) {
                var e, g = a, g = g = a;
                (e = g = c.ManpageTitleVolnumRx.$match(d.$attributes()["$[]"]("doctitle"))) === a || e._isBoolean && !0 != e ? this.$warn("asciidoctor: ERROR: " + b.$prev_line_info() + ": malformed manpage title") : (d.$attributes()["$[]="]("mantitle", d.$sub_attributes(g["$[]"](1).$rstrip().$downcase())),
                    d.$attributes()["$[]="]("manvolnum", g["$[]"](2).$strip()));
                b.$skip_blank_lines();
                if ((e = this["$is_next_line_section?"](b, r([], {}))) === a || e._isBoolean && !0 != e)return this.$warn("asciidoctor: ERROR: " + b.$prev_line_info() + ": name section expected");
                g = this.$initialize_section(b, d, r([], {}));
                if (g.$level()["$=="](1)) {
                    g = b.$read_lines_until(r(["break_on_blank_lines"], {break_on_blank_lines: !0})).$join(" ").$tr_s(" ", " ");
                    if ((e = g = c.ManpageNamePurposeRx.$match(g)) === a || e._isBoolean && !0 != e)return this.$warn("asciidoctor: ERROR: " +
                        b.$prev_line_info() + ": malformed name section body");
                    d.$attributes()["$[]="]("manname", g["$[]"](1));
                    d.$attributes()["$[]="]("manpurpose", g["$[]"](2));
                    return d.$backend()["$=="]("manpage") ? (d.$attributes()["$[]="]("docname", d.$attributes()["$[]"]("manname")), d.$attributes()["$[]="]("outfilesuffix", "." + d.$attributes()["$[]"]("manvolnum"))) : a
                }
                return this.$warn("asciidoctor: ERROR: " + b.$prev_line_info() + ": name section title must be at level 1")
            });
            d.defs(n, "$next_section", function (b, p, e) {
                var g, l, k, s,
                    h = a, m = a, n = a, t = a, v = a, y = a, M = a, u = a, D = M = a, B = D = D = a, w = a, v = m = a;
                null == e && (e = r([], {}));
                n = m = h = !1;
                (g = (l = (k = p.$context()["$=="]("document")) ? p.$blocks()["$empty?"]() : k, !1 !== l && l !== a ? !1 !== (k = !1 !== (s = t = p["$has_header?"]()) && s !== a ? s : e.$delete("invalid-header")) && k !== a ? k : this["$is_next_line_section?"](b, e)["$!"]() : l)) === a || g._isBoolean && !0 != g ? (v = p.$document().$doctype(), y = this.$initialize_section(b, p, e), e = (g = M = e["$[]"]("title")) === a || g._isBoolean && !0 != g ? r([], {}) : r(["title"], {title: M}), M = y.$level(), (g = (l = M["$=="](0)) ?
                    v["$=="]("book") : l) === a || g._isBoolean && !0 != g ? u = [M["$+"](1)] : (m = y.$special()["$!"](), u = (g = (l = y.$special(), !1 !== l && l !== a ? ["preface", "appendix"]["$include?"](y.$sectname()) : l)) === a || g._isBoolean && !0 != g ? [M["$+"](1)] : [M["$+"](2)])) : (v = p.$doctype(), !1 !== t && t !== a && (h = n = c.Block.$new(p, "preamble", r(["content_model"], {content_model: "compound"})), p["$<<"](h)), y = p, M = 0, u = (g = p.$attributes()["$has_key?"]("fragment")) === a || g._isBoolean && !0 != g ? v["$=="]("book") ? [0, 1] : [1] : a);
                for (b.$skip_blank_lines(); (l = b["$has_more_lines?"]()) !==
                    a && (!l._isBoolean || !0 == l);) {
                    this.$parse_block_metadata_lines(b, y, e);
                    if ((l = D = this["$is_next_line_section?"](b, e)) === a || l._isBoolean && !0 != l)(D = b.$line_info(), (l = B = this.$next_block(b, !1 !== (k = n) && k !== a ? k : y, e, r(["parse_metadata"], {parse_metadata: !1}))) === a || l._isBoolean && !0 != l) || (!1 !== m && m !== a && ((l = y["$blocks?"]()["$!"]()) === a || l._isBoolean && !0 != l ? y.$blocks().$size()["$=="](1) && ((w = y.$blocks()["$[]"](0), (l = (k = n["$!"](), !1 !== k && k !== a ? w.$content_model()["$=="]("compound") : k)) === a || l._isBoolean && !0 != l) ? (l =
                        w.$content_model()["$=="]("compound")["$!"]()) === a || l._isBoolean && !0 != l || (n = c.Block.$new(y, "open", r(["content_model"], {content_model: "compound"})), n["$style="]("partintro"), y.$blocks().$shift(), w.$style()["$=="]("partintro") && (w["$context="]("paragraph"), w["$style="](a)), w["$parent="](n), n["$<<"](w), B["$parent="](n), y["$<<"](n)) : this.$warn("asciidoctor: ERROR: " + D + ": illegal block content outside of partintro block")) : (l = B.$style()["$=="]("partintro")["$!"]()) === a || l._isBoolean && !0 != l || (B.$context()["$=="]("paragraph") ?
                        (B["$context="]("open"), B["$style="]("partintro")) : (n = c.Block.$new(y, "open", r(["content_model"], {content_model: "compound"})), n["$style="]("partintro"), B["$parent="](n), y["$<<"](n)))), (!1 !== (l = n) && l !== a ? l : y)["$<<"](B), e = r([], {})); else if (D = D["$+"](y.$document().$attr("leveloffset", 0).$to_i()), (l = !1 !== (k = D["$>"](M)) && k !== a ? k : (s = y.$context()["$=="]("document")) ? D["$=="](0) : s) === a || l._isBoolean && !0 != l) {
                        (l = (k = D["$=="](0)) ? v["$=="]("book")["$!"]() : k) === a || l._isBoolean && !0 != l || this.$warn("asciidoctor: ERROR: " +
                            b.$line_info() + ": only book doctypes can contain level 0 sections");
                        break
                    } else(l = (k = D["$=="](0)) ? v["$=="]("book")["$!"]() : k) === a || l._isBoolean && !0 != l ? (l = (k = !1 !== u && u !== a) ? u["$include?"](D)["$!"]() : k) === a || l._isBoolean && !0 != l || this.$warn(("asciidoctor: WARNING: " + b.$line_info() + ": section title out of sequence: ")["$+"]("expected " + (u.$size()["$>"](1) ? "levels" : "level") + " " + u["$*"](" or ") + ", ")["$+"]("got level " + D)) : this.$warn("asciidoctor: ERROR: " + b.$line_info() + ": only book doctypes can contain level 0 sections"),
                        l = d.to_ary(this.$next_section(b, y, e)), D = null == l[0] ? a : l[0], e = null == l[1] ? a : l[1], y["$<<"](D);
                    b.$skip_blank_lines()
                }
                if (!1 !== m && m !== a)((g = (l = y["$blocks?"](), !1 !== l && l !== a ? y.$blocks()["$[]"](-1).$context()["$=="]("section") : l)) === a || g._isBoolean && !0 != g) && this.$warn("asciidoctor: ERROR: " + b.$line_info() + ": invalid part, must have at least one section (e.g., chapter, appendix, etc.)"); else if (!1 !== h && h !== a)if (m = p, (g = h["$blocks?"]()) === a || g._isBoolean && !0 != g)m.$blocks().$shift(); else if ((g = (l = (k = c.Compliance.$unwrap_standalone_preamble(),
                    !1 !== k && k !== a ? m.$blocks().$size()["$=="](1) : k), !1 !== l && l !== a ? !1 !== (k = v["$=="]("book")["$!"]()) && k !== a ? k : h.$blocks()["$[]"](0).$style()["$=="]("abstract")["$!"]() : l)) !== a && (!g._isBoolean || !0 == g))for (m.$blocks().$shift(); (l = v = h.$blocks().$shift()) !== a && (!l._isBoolean || !0 == l);)v["$parent="](m), m["$<<"](v);
                return[(g = y["$=="](p)["$!"]()) === a || g._isBoolean && !0 != g ? a : y, e.$dup()]
            });
            d.defs(n, "$next_block", function (b, p, e, g) {
                var l, k, s, h, m, n, t, u, y, M, w, D, B, C, x, A, F, H, L, U = a, S = a, $ = a, Q = a, V = a, aa = a, ea = a, fa = a, E = a, N = a,
                    ka = a, O = a, I = a, K = a, ha = a, Y = a, X = a, ia = a, W = a, ca = I = X = I = a, ba = X = I = E = E = a, la = a, ja = ba = X = I = E = ba = a, P = a, ma = E = a, oa = I = I = E = E = a, pa = O = K = I = a, qa = O = K = K = a, na = U = a, ra = a;
                null == e && (e = r([], {}));
                null == g && (g = r([], {}));
                U = b.$skip_blank_lines();
                if ((l = b["$has_more_lines?"]()) === a || l._isBoolean && !0 != l)return a;
                (l = (k = S = g["$[]"]("text"), !1 !== k && k !== a ? U["$>"](0) : k)) === a || l._isBoolean && !0 != l || (g.$delete("text"), S = !1);
                $ = g.$fetch("parse_metadata", !0);
                Q = p.$document();
                (l = V = Q.$extensions()) === a || l._isBoolean && !0 != l ? aa = ea = !1 : (aa = V["$blocks?"](),
                    ea = V["$block_macros?"]());
                fa = p["$is_a?"](c.List);
                for (ka = N = E = a; (k = (s = E["$!"](), !1 !== s && s !== a ? b["$has_more_lines?"]() : s)) !== a && (!k._isBoolean || !0 == k);)if ((k = (s = !1 !== $ && $ !== a) ? this.$parse_block_metadata_line(b, Q, e, g) : s) === a || k._isBoolean && !0 != k) {
                    O = b.$read_line();
                    I = !1;
                    Y = ha = K = a;
                    (k = e["$[]"](1)) === a || k._isBoolean && !0 != k || (k = d.to_ary(this.$parse_style_attribute(e, b)), N = null == k[0] ? a : k[0], ka = null == k[1] ? a : k[1]);
                    (k = X = this["$is_delimited_block?"](O, !0)) === a || k._isBoolean && !0 != k || ((I = !0, K = ha = X.$context(), Y = X.$terminator(),
                        (k = N["$!"]()) === a || k._isBoolean && !0 != k) ? (k = N["$=="](K.$to_s())["$!"]()) === a || k._isBoolean && !0 != k || ((k = X.$masq()["$include?"](N)) === a || k._isBoolean && !0 != k ? (k = (s = X.$masq()["$include?"]("admonition"), !1 !== s && s !== a ? c.ADMONITION_STYLES["$include?"](N) : s)) === a || k._isBoolean && !0 != k ? (k = (s = !1 !== aa && aa !== a) ? V["$registered_for_block?"](N, K) : s) === a || k._isBoolean && !0 != k ? (this.$warn("asciidoctor: WARNING: " + b.$prev_line_info() + ": invalid style for " + K + " block: " + N), N = K.$to_s()) : K = N.$to_sym() : K = "admonition" : K = N.$to_sym()) :
                        N = e["$[]="]("style", K.$to_s()));
                    if (!1 === I || I === a)for (; (s = !0) !== a && (!s._isBoolean || !0 == s);) {
                        if ((s = (h = (m = !1 !== N && N !== a) ? c.Compliance.$strict_verbatim_paragraphs() : m, !1 !== h && h !== a ? c.VERBATIM_STYLES["$include?"](N) : h)) !== a && (!s._isBoolean || !0 == s)) {
                            K = N.$to_sym();
                            b.$unshift_line(O);
                            break
                        }
                        if (!1 === S || S === a)if (ia = (s = c.Compliance.$markdown_syntax()) === a || s._isBoolean && !0 != s ? O.$chr() : O.$lstrip().$chr(), (s = (h = (m = c.LAYOUT_BREAK_LINES["$has_key?"](ia), !1 !== m && m !== a ? O.$length()["$>="](3) : m), !1 !== h && h !== a ? ((m = c.Compliance.$markdown_syntax()) ===
                            a || m._isBoolean && !0 != m ? c.LayoutBreakLineRx : c.LayoutBreakLinePlusRx)["$=~"](O) : h)) !== a && (!s._isBoolean || !0 == s)) {
                            E = c.Block.$new(p, c.LAYOUT_BREAK_LINES["$[]"](ia), r(["content_model"], {content_model: "empty"}));
                            break
                        } else if ((s = (h = O["$end_with?"]("]"), !1 !== h && h !== a ? W = c.MediaBlockMacroRx.$match(O) : h)) !== a && (!s._isBoolean || !0 == s)) {
                            I = W["$[]"](1).$to_sym();
                            E = c.Block.$new(p, I, r(["content_model"], {content_model: "empty"}));
                            X = I["$=="]("image") ? ["alt", "width", "height"] : I["$=="]("video") ? ["poster", "width", "height"] :
                                [];
                            if ((s = !1 !== (h = N["$!"]()) && h !== a ? h : ka) === a || s._isBoolean && !0 != s) {
                                if (I["$=="]("image"))e["$[]="]("alt", N);
                                e.$delete("style");
                                N = a
                            }
                            E.$parse_attributes(W["$[]"](3), X, r(["unescape_input", "sub_input", "sub_result", "into"], {unescape_input: I["$=="]("image"), sub_input: !0, sub_result: !1, into: e}));
                            I = E.$sub_attributes(W["$[]"](2), r(["attribute_missing"], {attribute_missing: "drop-line"}));
                            if ((s = I["$empty?"]()) !== a && (!s._isBoolean || !0 == s)) {
                                if (Q.$attributes().$fetch("attribute-missing", c.Compliance.$attribute_missing())["$=="]("skip"))return c.Block.$new(p,
                                    "paragraph", r(["content_model", "source"], {content_model: "simple", source: [O]}));
                                e.$clear();
                                return a
                            }
                            e["$[]="]("target", I);
                            break
                        } else if ((s = (h = ia["$=="]("t")) ? W = c.TocBlockMacroRx.$match(O) : h) !== a && (!s._isBoolean || !0 == s)) {
                            E = c.Block.$new(p, "toc", r(["content_model"], {content_model: "empty"}));
                            E.$parse_attributes(W["$[]"](1), [], r(["sub_result", "into"], {sub_result: !1, into: e}));
                            break
                        } else if ((s = (h = (m = !1 !== ea && ea !== a) ? W = c.GenericBlockMacroRx.$match(O) : m, !1 !== h && h !== a ? ca = V["$registered_for_block_macro?"](W["$[]"](1)) :
                            h)) !== a && (!s._isBoolean || !0 == s)) {
                            I = W["$[]"](2);
                            E = W["$[]"](3);
                            if (ca.$config()["$[]"]("content_model")["$=="]("attributes"))((s = E["$empty?"]()) === a || s._isBoolean && !0 != s) && Q.$parse_attributes(E, !1 !== (s = ca.$config()["$[]"]("pos_attrs")) && s !== a ? s : [], r(["sub_input", "sub_result", "into"], {sub_input: !0, sub_result: !1, into: e})); else e["$[]="]("text", E);
                            (s = E = ca.$config()["$[]"]("default_attrs")) === a || s._isBoolean && !0 != s || (s = (h = E).$each, s._p = (n = function (b, c) {
                                var d, f, g;
                                null == b && (b = a);
                                null == c && (c = a);
                                return d = b,
                                    f = e, !1 !== (g = f["$[]"](d)) && g !== a ? g : f["$[]="](d, c)
                            }, n._s = this, n), s).call(h);
                            if ((s = E = ca.$process_method()["$[]"](p, I, e.$dup())) === a || s._isBoolean && !0 != s)return e.$clear(), a;
                            e.$replace(E.$attributes());
                            break
                        }
                        if ((s = W = c.CalloutListRx.$match(O)) !== a && (!s._isBoolean || !0 == s)) {
                            E = c.List.$new(p, "colist");
                            e["$[]="]("style", "arabic");
                            b.$unshift_line(O);
                            for (I = 1; (m = (t = b["$has_more_lines?"](), !1 !== t && t !== a ? W = c.CalloutListRx.$match(b.$peek_line()) : t)) !== a && (!m._isBoolean || !0 == m);)if ((m = W["$[]"](1).$to_i()["$=="](I)["$!"]()) ===
                                a || m._isBoolean && !0 != m || this.$warn("asciidoctor: WARNING: " + b.$path() + ": line " + b.$lineno()["$-"](2) + ": callout list item index: expected " + I + " got " + W["$[]"](1)), X = this.$next_list_item(b, E, W), I = I["$+"](1), !1 !== X && X !== a)if (E["$<<"](X), ba = Q.$callouts().$callout_ids(E.$items().$size()), (m = ba["$empty?"]()["$!"]()) === a || m._isBoolean && !0 != m)this.$warn("asciidoctor: WARNING: " + b.$path() + ": line " + b.$lineno()["$-"](2) + ": no callouts refer to list item " + E.$items().$size()); else X.$attributes()["$[]="]("coids",
                                ba);
                            Q.$callouts().$next_list();
                            break
                        } else if ((s = c.UnorderedListRx["$=~"](O)) !== a && (!s._isBoolean || !0 == s)) {
                            b.$unshift_line(O);
                            E = this.$next_outline_list(b, "ulist", p);
                            break
                        } else if ((s = W = c.OrderedListRx.$match(O)) !== a && (!s._isBoolean || !0 == s)) {
                            b.$unshift_line(O);
                            E = this.$next_outline_list(b, "olist", p);
                            if ((s = (m = e["$[]"]("style")["$!"](), !1 !== m && m !== a ? E.$attributes()["$[]"]("style")["$!"]() : m)) !== a && (!s._isBoolean || !0 == s))if (la = E.$items()["$[]"](0).$marker(), (s = la["$start_with?"](".")) === a || s._isBoolean &&
                                !0 != s)N = (s = (m = c.ORDERED_LIST_STYLES).$detect, s._p = (u = function (b) {
                                null == b && (b = a);
                                return c.OrderedListMarkerRxMap["$[]"](b)["$=~"](la)
                            }, u._s = this, u), s).call(m), e["$[]="]("style", (!1 !== (s = N) && s !== a ? s : c.ORDERED_LIST_STYLES["$[]"](0)).$to_s()); else e["$[]="]("style", (!1 !== (s = c.ORDERED_LIST_STYLES["$[]"](la.$length()["$-"](1))) && s !== a ? s : c.ORDERED_LIST_STYLES["$[]"](0)).$to_s());
                            break
                        } else if ((s = W = c.DefinitionListRx.$match(O)) !== a && (!s._isBoolean || !0 == s)) {
                            b.$unshift_line(O);
                            E = this.$next_labeled_list(b, W,
                                p);
                            break
                        } else if ((s = (t = !1 !== (y = N["$=="]("float")) && y !== a ? y : N["$=="]("discrete"), !1 !== t && t !== a ? this["$is_section_title?"](O, (y = c.Compliance.$underline_style_section_titles()) === a || y._isBoolean && !0 != y ? a : b.$peek_line(!0)) : t)) !== a && (!s._isBoolean || !0 == s)) {
                            b.$unshift_line(O);
                            s = d.to_ary(this.$parse_section_title(b, Q));
                            ba = null == s[0] ? a : s[0];
                            E = null == s[1] ? a : s[1];
                            I = null == s[2] ? a : s[2];
                            X = null == s[3] ? a : s[3];
                            if (!1 !== E && E !== a)e["$[]="]("reftext", E);
                            (s = e["$has_key?"]("id")) === a || s._isBoolean && !0 != s || (!1 !== (s = ba) && s !==
                                a ? s : ba = e["$[]"]("id"));
                            E = c.Block.$new(p, "floating_title", r(["content_model"], {content_model: "empty"}));
                            if ((s = ba["$nil_or_empty?"]()) === a || s._isBoolean && !0 != s)E["$id="](ba); else ba = c.Section.$new(p), ba["$title="](I), E["$id="](ba.$generate_id());
                            E["$level="](X);
                            E["$title="](I);
                            break
                        } else if ((s = (t = !1 !== N && N !== a) ? N["$=="]("normal")["$!"]() : t) !== a && (!s._isBoolean || !0 == s))if ((s = c.PARAGRAPH_STYLES["$include?"](N)) === a || s._isBoolean && !0 != s)if ((s = c.ADMONITION_STYLES["$include?"](N)) === a || s._isBoolean && !0 !=
                            s)if ((s = (t = !1 !== aa && aa !== a) ? V["$registered_for_block?"](N, "paragraph") : t) === a || s._isBoolean && !0 != s)this.$warn("asciidoctor: WARNING: " + b.$prev_line_info() + ": invalid style for paragraph: " + N), N = a; else {
                            K = N.$to_sym();
                            ha = "paragraph";
                            b.$unshift_line(O);
                            break
                        } else {
                            K = "admonition";
                            ha = "paragraph";
                            b.$unshift_line(O);
                            break
                        } else {
                            K = N.$to_sym();
                            ha = "paragraph";
                            b.$unshift_line(O);
                            break
                        }
                        ja = (s = U["$=="](0)) ? fa : s;
                        if ((s = (t = N["$=="]("normal")["$!"](), !1 !== t && t !== a ? c.LiteralParagraphRx["$=~"](O) : t)) === a || s._isBoolean &&
                            !0 != s) {
                            b.$unshift_line(O);
                            P = (s = (y = b).$read_lines_until, s._p = (w = function (b) {
                                var d = w._s || this, e, f, g;
                                null == b && (b = a);
                                return!1 !== (e = (f = !1 !== ja && ja !== a) ? c.AnyListRx["$=~"](b) : f) && e !== a ? e : (f = c.Compliance.$block_terminates_paragraph(), !1 !== f && f !== a ? !1 !== (g = d["$is_delimited_block?"](b)) && g !== a ? g : c.BlockAttributeLineRx["$=~"](b) : f)
                            }, w._s = this, w), s).call(y, r(["break_on_blank_lines", "break_on_list_continuation", "preserve_last_line", "skip_line_comments"], {break_on_blank_lines: !0, break_on_list_continuation: !0,
                                preserve_last_line: !0, skip_line_comments: !0}));
                            if ((s = P["$empty?"]()) !== a && (!s._isBoolean || !0 == s))return b.$advance(), a;
                            this.$catalog_inline_anchors(P.$join(c.EOL), Q);
                            E = P["$[]"](0);
                            if ((s = (D = S["$!"](), !1 !== D && D !== a ? ma = c.AdmonitionParagraphRx.$match(E) : D)) === a || s._isBoolean && !0 != s)if ((s = (D = (B = S["$!"](), !1 !== B && B !== a ? c.Compliance.$markdown_syntax() : B), !1 !== D && D !== a ? E["$start_with?"]("> ") : D)) === a || s._isBoolean && !0 != s)if ((s = (B = (x = (A = (F = S["$!"](), !1 !== F && F !== a ? P.$size()["$>"](1) : F), !1 !== A && A !== a ? E["$start_with?"]('"') :
                                A), !1 !== x && x !== a ? P["$[]"](-1)["$start_with?"]("-- ") : x), !1 !== B && B !== a ? P["$[]"](-2)["$end_with?"]('"') : B)) === a || s._isBoolean && !0 != s)(s = (B = N["$=="]("normal")) ? !1 !== (x = (ia = P["$[]"](0).$chr())["$=="](" ")) && x !== a ? x : ia["$=="](c.TAB) : B) === a || s._isBoolean && !0 != s || (E = P["$[]"](0), I = E.$lstrip(), oa = this.$line_length(E)["$-"](this.$line_length(I)), P["$[]="](0, I), (s = (B = P.$size()).$times, s._p = (H = function (b) {
                                null == b && (b = a);
                                return b["$>"](0) ? P["$[]="](b, P["$[]"](b)["$[]"](v(oa, -1, !1))) : a
                            }, H._s = this, H), s).call(B)),
                                E = c.Block.$new(p, "paragraph", r(["content_model", "source", "attributes"], {content_model: "simple", source: P, attributes: e})); else {
                                P["$[]="](0, E["$[]"](v(1, -1, !1)));
                                s = d.to_ary(P.$pop()["$[]"](v(3, -1, !1)).$split(", ", 2));
                                E = null == s[0] ? a : s[0];
                                for (I = null == s[1] ? a : s[1]; (B = P["$[]"](-1)["$empty?"]()) !== a && (!B._isBoolean || !0 == B);)P.$pop();
                                P["$[]="](-1, P["$[]"](-1).$chop());
                                e["$[]="]("style", "quote");
                                if (!1 !== E && E !== a)e["$[]="]("attribution", E);
                                if (!1 !== I && I !== a)e["$[]="]("citetitle", I);
                                E = c.Block.$new(p, "quote", r(["content_model",
                                    "source", "attributes"], {content_model: "simple", source: P, attributes: e}))
                            } else {
                                (s = (D = P)["$map!"], s._p = (C = function (b) {
                                    var c;
                                    null == b && (b = a);
                                    return b["$=="](">") ? b["$[]"](v(1, -1, !1)) : (c = b["$start_with?"]("> ")) === a || c._isBoolean && !0 != c ? b : b["$[]"](v(2, -1, !1))
                                }, C._s = this, C), s).call(D);
                                if ((s = P["$[]"](-1)["$start_with?"]("-- ")) === a || s._isBoolean && !0 != s)s = d.to_ary(a), E = null == s[0] ? a : s[0], I = null == s[1] ? a : s[1]; else for (s = d.to_ary(P.$pop()["$[]"](v(3, -1, !1)).$split(", ", 2)), E = null == s[0] ? a : s[0], I = null == s[1] ? a : s[1]; (B =
                                    P["$[]"](-1)["$empty?"]()) !== a && (!B._isBoolean || !0 == B);)P.$pop();
                                e["$[]="]("style", "quote");
                                if (!1 !== E && E !== a)e["$[]="]("attribution", E);
                                if (!1 !== I && I !== a)e["$[]="]("citetitle", I);
                                E = this.$build_block("quote", "compound", !1, p, c.Reader.$new(P), e)
                            } else P["$[]="](0, ma.$post_match().$lstrip()), e["$[]="]("style", ma["$[]"](1)), e["$[]="]("name", E = ma["$[]"](1).$downcase()), s = "caption", D = e, !1 !== (B = D["$[]"](s)) && B !== a ? B : D["$[]="](s, Q.$attributes()["$[]"]("" + E + "-caption")), E = c.Block.$new(p, "admonition", r(["content_model",
                                "source", "attributes"], {content_model: "simple", source: P, attributes: e}))
                        } else b.$unshift_line(O), P = (s = (t = b).$read_lines_until, s._p = (M = function (b) {
                            var d = M._s || this, e, f, g;
                            null == b && (b = a);
                            return!1 !== (e = (f = !1 !== ja && ja !== a) ? c.AnyListRx["$=~"](b) : f) && e !== a ? e : (f = c.Compliance.$block_terminates_paragraph(), !1 !== f && f !== a ? !1 !== (g = d["$is_delimited_block?"](b)) && g !== a ? g : c.BlockAttributeLineRx["$=~"](b) : f)
                        }, M._s = this, M), s).call(t, r(["break_on_blank_lines", "break_on_list_continuation", "preserve_last_line"], {break_on_blank_lines: !0,
                            break_on_list_continuation: !0, preserve_last_line: !0})), this["$reset_block_indent!"](P), E = c.Block.$new(p, "literal", r(["content_model", "source", "attributes"], {content_model: "verbatim", source: P, attributes: e})), !1 !== fa && fa !== a && E.$set_option("listparagraph");
                        break
                    }
                    if ((k = (s = E["$!"](), !1 !== s && s !== a ? K : s)) !== a && (!k._isBoolean || !0 == k))if ((k = !1 !== (s = K["$=="]("abstract")) && s !== a ? s : K["$=="]("partintro")) === a || k._isBoolean && !0 != k || (K = "open"), I = K, "admonition"["$==="](I))e["$[]="]("name", E = N.$downcase()), k = "caption",
                        s = e, !1 !== (x = s["$[]"](k)) && x !== a ? x : s["$[]="](k, Q.$attributes()["$[]"]("" + E + "-caption")), E = this.$build_block(K, "compound", Y, p, b, e); else {
                        if ("comment"["$==="](I))return this.$build_block(K, "skip", Y, p, b, e), a;
                        if ("example"["$==="](I))E = this.$build_block(K, "compound", Y, p, b, e); else if ("listing"["$==="](I) || "fenced_code"["$==="](I) || "source"["$==="](I)) {
                            if (K["$=="]("fenced_code")) {
                                N = e["$[]="]("style", "source");
                                k = d.to_ary(O["$[]"](v(3, -1, !1)).$split(",", 2));
                                K = null == k[0] ? a : k[0];
                                O = null == k[1] ? a : k[1];
                                if (!((k = (s =
                                    !1 !== K && K !== a) ? (K = K.$strip())["$empty?"]()["$!"]() : s) === a || k._isBoolean && !0 != k || (e["$[]="]("language", K), (k = (s = !1 !== O && O !== a) ? O.$strip()["$empty?"]()["$!"]() : s) === a || k._isBoolean && !0 != k)))e["$[]="]("linenums", "");
                                Y = Y["$[]"](v(0, 2, !1))
                            } else K["$=="]("source") && c.AttributeList.$rekey(e, [a, "language", "linenums"]);
                            E = this.$build_block("listing", "verbatim", Y, p, b, e)
                        } else if ("literal"["$==="](I))E = this.$build_block(K, "verbatim", Y, p, b, e); else if ("pass"["$==="](I))E = this.$build_block(K, "raw", Y, p, b, e); else if ("math"["$==="](I) ||
                            "latexmath"["$==="](I) || "asciimath"["$==="](I)) {
                            if (K["$=="]("math"))e["$[]="]("style", (k = (pa = Q.$attributes()["$[]"]("math"))["$nil_or_empty?"]()) === a || k._isBoolean && !0 != k ? pa : "asciimath");
                            E = this.$build_block("math", "raw", Y, p, b, e)
                        } else if ("open"["$==="](I) || "sidebar"["$==="](I))E = this.$build_block(K, "compound", Y, p, b, e); else if ("table"["$==="](I)) {
                            K = b.$cursor();
                            K = c.Reader.$new(b.$read_lines_until(r(["terminator", "skip_line_comments"], {terminator: Y, skip_line_comments: !0})), K);
                            I = Y.$chr();
                            if (","["$==="](I))e["$[]="]("format",
                                "csv"); else if (":"["$==="](I))e["$[]="]("format", "dsv");
                            E = this.$next_table(K, p, e)
                        } else if ("quote"["$==="](I) || "verse"["$==="](I))c.AttributeList.$rekey(e, [a, "attribution", "citetitle"]), E = this.$build_block(K, K["$=="]("verse") ? "verbatim" : "compound", Y, p, b, e); else if ((k = (s = !1 !== aa && aa !== a) ? ca = V["$registered_for_block?"](K, ha) : s) === a || k._isBoolean && !0 != k)this.$raise("Unsupported block type " + K + " at " + b.$line_info()); else if ((k = (O = ca.$config()["$[]"]("content_model"))["$=="]("skip")["$!"]()) === a || k._isBoolean &&
                            !0 != k || ((k = (qa = !1 !== (s = ca.$config()["$[]"]("pos_attrs")) && s !== a ? s : [])["$empty?"]()["$!"]()) === a || k._isBoolean && !0 != k || c.AttributeList.$rekey(e, [a].$concat(qa)), (k = E = ca.$config()["$[]"]("default_attrs")) === a || k._isBoolean && !0 != k || (k = (s = E).$each, k._p = (L = function (b, c) {
                            var d, f, g;
                            null == b && (b = a);
                            null == c && (c = a);
                            return d = b, f = e, !1 !== (g = f["$[]"](d)) && g !== a ? g : f["$[]="](d, c)
                        }, L._s = this, L), k).call(s)), E = this.$build_block(K, O, Y, p, b, e, r(["extension"], {extension: ca})), (k = (x = !1 !== E && E !== a) ? O["$=="]("skip")["$!"]() :
                            x) === a || k._isBoolean && !0 != k)return e.$clear(), a
                    }
                } else b.$advance();
                if (!1 !== E && E !== a) {
                    if ((l = E["$title?"]()) === a || l._isBoolean && !0 != l)E["$title="](e["$[]"]("title"));
                    if (E.$context()["$=="]("image")) {
                        if (U = e["$[]"]("target"), E.$document().$register("images", U), l = "alt", k = e, !1 !== (x = k["$[]"](l)) && x !== a ? x : k["$[]="](l, (null == (A = d.Object._scope.File) ? d.cm("File") : A).$basename(U, (null == (A = d.Object._scope.File) ? d.cm("File") : A).$extname(U)).$tr("_-", " ")), E.$assign_caption(e.$delete("caption"), "figure"), !((l =
                            na = e["$[]"]("scaledwidth")) === a || l._isBoolean && !0 != l || (l = v(48, 57, !1)["$include?"]((!1 !== (k = na["$[]"](-1)) && k !== a ? k : 0).$ord())) === a || l._isBoolean && !0 != l))e["$[]="]("scaledwidth", "" + na + "%")
                    } else l = E, !1 !== (k = l.$caption()) && k !== a ? k : l["$caption="](e.$delete("caption"));
                    E["$style="](e["$[]"]("style"));
                    (l = ra = (k = E, !1 !== (x = k.$id()) && x !== a ? x : k["$id="](e["$[]"]("id")))) === a || l._isBoolean && !0 != l || Q.$register("ids", [ra, !1 !== (l = e["$[]"]("reftext")) && l !== a ? l : (k = E["$title?"]()) === a || k._isBoolean && !0 != k ? a : E.$title()]);
                    ((l = e["$empty?"]()) === a || l._isBoolean && !0 != l) && E.$attributes().$update(e);
                    E.$lock_in_subs();
                    (l = E["$sub?"]("callouts")) === a || l._isBoolean && !0 != l || !((l = this.$catalog_callouts(E.$source(), Q)) === a || l._isBoolean && !0 != l) || E.$remove_sub("callouts")
                }
                return E
            });
            d.defs(n, "$is_delimited_block?", function (b, d) {
                var e, g, l = a, k = a, s = a, h = a, m = a, s = l = a;
                null == d && (d = !1);
                if ((e = (g = (l = b.$length())["$>"](1)) ? c.DELIMITED_BLOCK_LEADERS["$include?"](b["$[]"](v(0, 1, !1))) : g) === a || e._isBoolean && !0 != e)return a;
                if (l["$=="](2))k = b, s =
                    2; else {
                    l["$<="](4) ? (k = b, s = l) : (k = b["$[]"](v(0, 3, !1)), s = 4);
                    h = !1;
                    if ((e = c.Compliance.$markdown_syntax()) !== a && (!e._isBoolean || !0 == e))if (m = s["$=="](4) ? k.$chop() : k, m["$=="]("```")) {
                        if ((e = (g = s["$=="](4)) ? k["$end_with?"]("`") : g) !== a && (!e._isBoolean || !0 == e))return a;
                        k = m;
                        s = 3;
                        h = !0
                    } else if (m["$=="]("~~~")) {
                        if ((e = (g = s["$=="](4)) ? k["$end_with?"]("~") : g) !== a && (!e._isBoolean || !0 == e))return a;
                        k = m;
                        s = 3;
                        h = !0
                    }
                    if ((e = (g = s["$=="](3)) ? h["$!"]() : g) !== a && (!e._isBoolean || !0 == e))return a
                }
                return(e = c.DELIMITED_BLOCKS["$has_key?"](k)) ===
                    a || e._isBoolean && !0 != e ? a : (e = !1 !== (g = s["$<"](4)) && g !== a ? g : s["$=="](l)) === a || e._isBoolean && !0 != e ? ("" + k + k["$[]"](v(-1, -1, !1))["$*"](l["$-"](s)))["$=="](b) ? !1 !== d && d !== a ? ((e = c.DELIMITED_BLOCKS["$[]"](k)).$to_a ? e = e.$to_a() : e._isArray ? e : e = [e], l = null == e[0] ? a : e[0], s = null == e[1] ? a : e[1], c.BlockMatchData.$new(l, s, k, b)) : !0 : a : !1 !== d && d !== a ? ((e = c.DELIMITED_BLOCKS["$[]"](k)).$to_a ? e = e.$to_a() : e._isArray ? e : e = [e], l = null == e[0] ? a : e[0], s = null == e[1] ? a : e[1], c.BlockMatchData.$new(l, s, k, k)) : !0
            });
            d.defs(n, "$build_block", function (b, d, e, g, l, k, s) {
                var h, m, n, t, v = a, y = a, u = v = y = a, w = a, u = u = a;
                null == s && (s = r([], {}));
                (h = !1 !== (m = d["$=="]("skip")) && m !== a ? m : d["$=="]("raw")) === a || h._isBoolean && !0 != h ? (v = !1, y = d) : (v = d["$=="]("skip"), y = "simple");
                (h = e["$nil?"]()) === a || h._isBoolean && !0 != h ? (h = y["$=="]("compound")["$!"]()) === a || h._isBoolean && !0 != h ? e["$=="](!1) ? (y = a, v = l) : (y = a, u = l.$cursor(), v = c.Reader.$new(l.$read_lines_until(r(["terminator", "skip_processing"], {terminator: e, skip_processing: v})), u)) : (y = l.$read_lines_until(r(["terminator", "skip_processing"],
                    {terminator: e, skip_processing: v})), v = a) : (y["$=="]("verbatim") ? y = l.$read_lines_until(r(["break_on_blank_lines", "break_on_list_continuation"], {break_on_blank_lines: !0, break_on_list_continuation: !0})) : (d["$=="]("compound") && (d = "simple"), y = (h = (m = l).$read_lines_until, h._p = (n = function (b) {
                    var d = n._s || this, e, f;
                    null == b && (b = a);
                    return e = c.Compliance.$block_terminates_paragraph(), !1 !== e && e !== a ? !1 !== (f = d["$is_delimited_block?"](b)) && f !== a ? f : c.BlockAttributeLineRx["$=~"](b) : e
                }, n._s = this, n), h).call(m, r(["break_on_blank_lines",
                    "break_on_list_continuation", "preserve_last_line", "skip_line_comments", "skip_processing"], {break_on_blank_lines: !0, break_on_list_continuation: !0, preserve_last_line: !0, skip_line_comments: !0, skip_processing: v}))), v = a);
                if (d["$=="]("skip"))return k.$clear(), y;
                if ((h = (t = d["$=="]("verbatim")) ? w = k["$[]"]("indent") : t) !== a && (!h._isBoolean || !0 == h))this["$reset_block_indent!"](y, w.$to_i());
                if ((h = u = s["$[]"]("extension")) === a || h._isBoolean && !0 != h)u = c.Block.$new(g, b, r(["content_model", "source", "attributes"], {content_model: d,
                    source: y, attributes: k})); else {
                    k.$delete("style");
                    if ((h = u = u.$process_method()["$[]"](g, !1 !== (t = v) && t !== a ? t : c.Reader.$new(y), k.$dup())) === a || h._isBoolean && !0 != h)return a;
                    k.$replace(u.$attributes());
                    (h = (t = u.$content_model()["$=="]("compound")) ? (y = u.$lines())["$nil_or_empty?"]()["$!"]() : t) === a || h._isBoolean && !0 != h || (d = "compound", v = c.Reader.$new(y))
                }
                (h = (t = k["$has_key?"]("title"), !1 !== t && t !== a ? u.$document()["$attr?"]("" + u.$context() + "-caption") : t)) === a || h._isBoolean && !0 != h || (u["$title="](k.$delete("title")),
                    u.$assign_caption(k.$delete("caption")));
                d["$=="]("compound") && this.$parse_blocks(v, u);
                return u
            });
            d.defs(n, "$parse_blocks", function (b, d) {
                for (var e, g = a; (e = b["$has_more_lines?"]()) !== a && (!e._isBoolean || !0 == e);)if (g = c.Parser.$next_block(b, d), !1 !== g && g !== a)d["$<<"](g)
            });
            d.defs(n, "$next_outline_list", function (b, d, e) {
                var g, l, k = a, s = a, h = a, m = a, n = a, r = a, k = c.List.$new(e, d);
                if (e.$context()["$=="](d))k["$level="](e.$level()["$+"](1)); else k["$level="](1);
                for (; (g = (l = b["$has_more_lines?"](), !1 !== l && l !== a ? s = c.ListRxMap["$[]"](d).$match(b.$peek_line()) :
                    l)) !== a && (!g._isBoolean || !0 == g);) {
                    h = this.$resolve_list_marker(d, s["$[]"](1));
                    if ((g = (l = k["$items?"](), !1 !== l && l !== a ? h["$=="](k.$items()["$[]"](0).$marker())["$!"]() : l)) === a || g._isBoolean && !0 != g)m = k.$level(); else for (m = k.$level()["$+"](1), n = e; n.$context()["$=="](d);) {
                        if (h["$=="](n.$items()["$[]"](0).$marker())) {
                            m = n.$level();
                            break
                        }
                        n = n.$parent()
                    }
                    if ((g = !1 !== (l = k["$items?"]()["$!"]()) && l !== a ? l : m["$=="](k.$level())) !== a && (!g._isBoolean || !0 == g))r = this.$next_list_item(b, k, s); else if (m["$<"](k.$level()))break;
                    else if (m["$>"](k.$level()))k.$items()["$[]"](-1)["$<<"](this.$next_block(b, k));
                    if (!1 !== r && r !== a)k["$<<"](r);
                    r = a;
                    b.$skip_blank_lines()
                }
                return k
            });
            d.defs(n, "$catalog_callouts", function (b, d) {
                var e, g, l = a, l = !1;
                (e = b["$include?"]("<")) === a || e._isBoolean && !0 != e || (e = b.$scan, e._p = (g = function () {
                    var b, c = a;
                    null == x["~"] && (x["~"] = a);
                    c = x["~"];
                    (b = c["$[]"](0).$chr()["$=="]("\\")["$!"]()) === a || b._isBoolean && !0 != b || d.$callouts().$register(c["$[]"](2));
                    return l = !0
                }, g._s = this, g), e).call(b, c.CalloutQuickScanRx);
                return l
            });
            d.defs(n, "$catalog_inline_anchors", function (b, d) {
                var e, g;
                (e = b["$include?"]("[")) === a || e._isBoolean && !0 != e || (e = b.$scan, e._p = (g = function () {
                    var b, c = a, e = a, c = a;
                    null == x["~"] && (x["~"] = a);
                    c = x["~"];
                    if ((b = c["$[]"](0)["$start_with?"]("\\")) !== a && (!b._isBoolean || !0 == b))return a;
                    e = !1 !== (b = c["$[]"](1)) && b !== a ? b : c["$[]"](3);
                    c = !1 !== (b = c["$[]"](2)) && b !== a ? b : c["$[]"](4);
                    return d.$register("ids", [e, c])
                }, g._s = this, g), e).call(b, c.InlineAnchorRx);
                return a
            });
            d.defs(n, "$next_labeled_list", function (b, p, e) {
                for (var g, l, k = a,
                         s = a, h = a, m = a, n = a, k = c.List.$new(e, "dlist"), s = a, h = c.DefinitionListSiblingRx["$[]"](p["$[]"](2)); (g = (l = b["$has_more_lines?"](), !1 !== l && l !== a ? p = h.$match(b.$peek_line()) : l)) !== a && (!g._isBoolean || !0 == g);)if (g = d.to_ary(this.$next_list_item(b, k, p, h)), m = null == g[0] ? a : g[0], n = null == g[1] ? a : g[1], (g = (l = !1 !== s && s !== a) ? s["$[]"](-1)["$!"]() : l) === a || g._isBoolean && !0 != g)k.$items()["$<<"](s = [
                    [m],
                    n
                ]); else s.$pop(), s["$[]"](0)["$<<"](m), s["$<<"](n);
                return k
            });
            d.defs(n, "$next_list_item", function (b, d, e, g) {
                var l, k, s = a, h = a,
                    m = a, n = a, t = m = a, u = t = t = n = a, y = a, M = y = u = a, w = a;
                null == g && (g = a);
                if ((s = d.$context())["$=="]("dlist"))h = c.ListItem.$new(d, e["$[]"](1)), m = c.ListItem.$new(d, e["$[]"](3)), n = e["$[]"](3)["$nil_or_empty?"]()["$!"](); else {
                    m = e["$[]"](2);
                    t = !1;
                    (l = (k = s["$=="]("ulist")) ? m["$start_with?"]("[") : k) === a || l._isBoolean && !0 != l || ((l = m["$start_with?"]("[ ] ")) === a || l._isBoolean && !0 != l ? (l = !1 !== (k = m["$start_with?"]("[*] ")) && k !== a ? k : m["$start_with?"]("[x] ")) === a || l._isBoolean && !0 != l || (n = t = !0, m = m["$[]"](v(3, -1, !1)).$lstrip()) : (t = !0, n = !1, m = m["$[]"](v(3, -1, !1)).$lstrip()));
                    m = c.ListItem.$new(d, m);
                    if (!1 !== t && t !== a && (d.$attributes()["$[]="]("checklist-option", ""), m.$attributes()["$[]="]("checkbox", ""), !1 !== n && n !== a))m.$attributes()["$[]="]("checked", "");
                    !1 !== (l = g) && l !== a ? l : g = this.$resolve_list_marker(s, e["$[]"](1), d.$items().$size(), !0, b);
                    m["$marker="](g);
                    n = !0
                }
                b.$advance();
                t = b.$cursor();
                t = c.Reader.$new(this.$read_lines_for_list_item(b, s, g, n), t);
                if ((l = t["$has_more_lines?"]()) !== a && (!l._isBoolean || !0 == l)) {
                    u = t.$skip_line_comments();
                    y = t.$peek_line();
                    ((l = u["$empty?"]()) === a || l._isBoolean && !0 != l) && t.$unshift_lines(u);
                    (l = y["$nil?"]()["$!"]()) === a || l._isBoolean && !0 != l ? y = u = !1 : (u = y["$empty?"](), (l = (k = u["$!"](), !1 !== k && k !== a ? s["$=="]("dlist")["$!"]() : k)) === a || l._isBoolean && !0 != l || (n = !1), y = (l = u["$!"](), !1 !== l && l !== a ? y["$empty?"]()["$!"]() : l));
                    for (M = r(["text"], {text: n["$!"]()}); (k = t["$has_more_lines?"]()) !== a && (!k._isBoolean || !0 == k);)if (w = this.$next_block(t, d, r([], {}), M), !1 !== w && w !== a)m["$<<"](w);
                    m.$fold_first(u, y)
                }
                if (s["$=="]("dlist")) {
                    if ((l =
                        !1 !== (k = m["$text?"]()) && k !== a ? k : m["$blocks?"]()) === a || l._isBoolean && !0 != l)m = a;
                    return[h, m]
                }
                return m
            });
            d.defs(n, "$read_lines_for_list_item", function (b, d, e, g) {
                var l, k, s, h, m, n, t, v, y, u, w, D, B, C = a, A = a, J = a, F = a, H = a, L = a, U = a, L = a;
                null == x["~"] && (x["~"] = a);
                null == e && (e = a);
                null == g && (g = !0);
                C = [];
                A = "inactive";
                J = !1;
                for (F = a; (k = b["$has_more_lines?"]()) !== a && (!k._isBoolean || !0 == k);) {
                    H = b.$read_line();
                    if ((k = this["$is_sibling_list_item?"](H, d, e)) !== a && (!k._isBoolean || !0 == k))break;
                    L = (k = C["$empty?"]()) === a || k._isBoolean && !0 !=
                        k ? C["$[]"](-1) : a;
                    if (L["$=="](c.LIST_CONTINUATION)) {
                        if (A["$=="]("inactive") && (A = "active", g = !0, !1 === J || J === a))C["$[]="](-1, "");
                        if (H["$=="](c.LIST_CONTINUATION)) {
                            (k = A["$=="]("frozen")["$!"]()) === a || k._isBoolean && !0 != k || (A = "frozen", C["$<<"](H));
                            H = a;
                            continue
                        }
                    }
                    if ((k = U = this["$is_delimited_block?"](H, !0)) === a || k._isBoolean && !0 != k)if ((k = (s = (h = d["$=="]("dlist")) ? A["$=="]("active")["$!"]() : h, !1 !== s && s !== a ? c.BlockAttributeLineRx["$=~"](H) : s)) === a || k._isBoolean && !0 != k)if ((k = (s = A["$=="]("active")) ? H["$empty?"]()["$!"]() :
                        s) === a || k._isBoolean && !0 != k)if ((k = (h = L["$nil?"]()["$!"](), !1 !== h && h !== a ? L["$empty?"]() : h)) === a || k._isBoolean && !0 != k)(k = H["$empty?"]()["$!"]()) === a || k._isBoolean && !0 != k || (g = !0), (k = L = (w = (D = !1 !== J && J !== a ? ["dlist"] : c.NESTABLE_LIST_CONTEXTS).$detect, w._p = (B = function (b) {
                        null == b && (b = a);
                        return c.ListRxMap["$[]"](b)["$=~"](H)
                    }, B._s = this, B), w).call(D)) === a || k._isBoolean && !0 != k || (J = !0, (k = (w = L["$=="]("dlist")) ? x["~"]["$[]"](3)["$nil_or_empty?"]() : w) === a || k._isBoolean && !0 != k || (g = !1)), C["$<<"](H); else {
                        if (!((k =
                            H["$empty?"]()) === a || k._isBoolean && !0 != k || (b.$skip_blank_lines(), H = b.$read_line(), (k = !1 !== (h = H["$nil?"]()) && h !== a ? h : this["$is_sibling_list_item?"](H, d, e)) === a || k._isBoolean && !0 != k)))break;
                        if (H["$=="](c.LIST_CONTINUATION))F = C.$size(), C["$<<"](H); else if (!1 !== g && g !== a)if ((k = this["$is_sibling_list_item?"](H, d, e)) === a || k._isBoolean && !0 != k)if ((k = L = (h = (v = c.NESTABLE_LIST_CONTEXTS).$detect, h._p = (y = function (b) {
                            null == b && (b = a);
                            return c.ListRxMap["$[]"](b)["$=~"](H)
                        }, y._s = this, y), h).call(v)) === a || k._isBoolean &&
                            !0 != k)if ((k = c.LiteralParagraphRx["$=~"](H)) === a || k._isBoolean && !0 != k)break; else b.$unshift_line(H), C.$concat((k = (h = b).$read_lines_until, k._p = (u = function (b) {
                            var c = u._s || this, f;
                            null == b && (b = a);
                            return(f = d["$=="]("dlist")) ? c["$is_sibling_list_item?"](b, d, e) : f
                        }, u._s = this, u), k).call(h, r(["preserve_last_line", "break_on_blank_lines", "break_on_list_continuation"], {preserve_last_line: !0, break_on_blank_lines: !0, break_on_list_continuation: !0}))); else C["$<<"](H), J = !0, (k = (h = L["$=="]("dlist")) ? x["~"]["$[]"](3)["$nil_or_empty?"]() :
                            h) === a || k._isBoolean && !0 != k || (g = !1); else break; else!1 !== J && J !== a || C.$pop(), C["$<<"](H), g = !0
                    } else if ((k = c.LiteralParagraphRx["$=~"](H)) === a || k._isBoolean && !0 != k)if ((k = !1 !== (h = !1 !== (n = c.BlockTitleRx["$=~"](H)) && n !== a ? n : c.BlockAttributeLineRx["$=~"](H)) && h !== a ? h : c.AttributeEntryRx["$=~"](H)) === a || k._isBoolean && !0 != k)(k = L = (h = (n = !1 !== J && J !== a ? ["dlist"] : c.NESTABLE_LIST_CONTEXTS).$detect, h._p = (t = function (b) {
                        null == b && (b = a);
                        return c.ListRxMap["$[]"](b)["$=~"](H)
                    }, t._s = this, t), h).call(n)) === a || k._isBoolean &&
                        !0 != k || (J = !0, (k = (h = L["$=="]("dlist")) ? x["~"]["$[]"](3)["$nil_or_empty?"]() : h) === a || k._isBoolean && !0 != k || (g = !1)), C["$<<"](H), A = "inactive"; else C["$<<"](H); else b.$unshift_line(H), C.$concat((k = (s = b).$read_lines_until, k._p = (m = function (b) {
                        var c = m._s || this, f;
                        null == b && (b = a);
                        return(f = d["$=="]("dlist")) ? c["$is_sibling_list_item?"](b, d, e) : f
                    }, m._s = this, m), k).call(s, r(["preserve_last_line", "break_on_blank_lines", "break_on_list_continuation"], {preserve_last_line: !0, break_on_blank_lines: !0, break_on_list_continuation: !0}))),
                        A = "inactive"; else break; else if (A["$=="]("active"))C["$<<"](H), C.$concat(b.$read_lines_until(r(["terminator", "read_last_line"], {terminator: U.$terminator(), read_last_line: !0}))), A = "inactive"; else break;
                    H = a
                }
                !1 !== H && H !== a && b.$unshift_line(H);
                for (!1 !== F && F !== a && C.$delete_at(F); (k = (w = C["$empty?"]()["$!"](), !1 !== w && w !== a ? C["$[]"](-1)["$empty?"]() : w)) !== a && (!k._isBoolean || !0 == k);)C.$pop();
                (l = (k = C["$empty?"]()["$!"](), !1 !== k && k !== a ? C["$[]"](-1)["$=="](c.LIST_CONTINUATION) : k)) === a || l._isBoolean && !0 != l || C.$pop();
                return C
            });
            d.defs(n, "$initialize_section", function (b, p, e) {
                var g, l, k = a, s = a, h = a, m = a, n = a, t = s = h = a;
                null == e && (e = r([], {}));
                k = p.$document();
                g = d.to_ary(this.$parse_section_title(b, k));
                s = null == g[0] ? a : g[0];
                h = null == g[1] ? a : g[1];
                m = null == g[2] ? a : g[2];
                n = null == g[3] ? a : g[3];
                if (!1 !== h && h !== a)e["$[]="]("reftext", h);
                h = c.Section.$new(p, n, k.$attributes()["$has_key?"]("numbered"));
                h["$id="](s);
                h["$title="](m);
                if ((g = e["$[]"](1)) === a || g._isBoolean && !0 != g)if ((g = (l = m.$downcase()["$=="]("synopsis")) ? k.$doctype()["$=="]("manpage") :
                    l) === a || g._isBoolean && !0 != g)h["$sectname="]("sect" + h.$level()); else h["$special="](!0), h["$sectname="]("synopsis"); else if (g = d.to_ary(this.$parse_style_attribute(e, b)), s = null == g[0] ? a : g[0], !1 !== s && s !== a)h["$sectname="](s), h["$special="](!0), (g = (l = h.$sectname()["$=="]("abstract")) ? k.$doctype()["$=="]("book") : l) === a || g._isBoolean && !0 != g || (h["$sectname="]("sect1"), h["$special="](!1), h["$level="](1)); else h["$sectname="]("sect" + h.$level());
                if ((g = (l = h.$id()["$!"](), !1 !== l && l !== a ? t = e["$[]"]("id") : l)) ===
                    a || g._isBoolean && !0 != g)g = h, !1 !== (l = g.$id()) && l !== a ? l : g["$id="](h.$generate_id()); else h["$id="](t);
                (g = h.$id()) === a || g._isBoolean && !0 != g || h.$document().$register("ids", [h.$id(), !1 !== (g = e["$[]"]("reftext")) && g !== a ? g : h.$title()]);
                h.$update_attributes(e);
                b.$skip_blank_lines();
                return h
            });
            d.defs(n, "$section_level", function (a) {
                return c.SECTION_LEVELS["$[]"](a.$chr())
            });
            d.defs(n, "$single_line_section_level", function (a) {
                return a.$length()["$-"](1)
            });
            d.defs(n, "$is_next_line_section?", function (b, d) {
                var e, g,
                    l, k, h = a, m = a;
                return(e = (g = (l = (h = d["$[]"](1))["$nil?"]()["$!"](), !1 !== l && l !== a ? !1 !== (k = (m = h["$[]"](0).$ord())["$=="](100)) && k !== a ? k : m["$=="](102) : l), !1 !== g && g !== a ? h["$=~"](c.FloatingTitleStyleRx) : g)) !== a && (!e._isBoolean || !0 == e) || (e = b["$has_more_lines?"]()) === a || e._isBoolean && !0 != e ? !1 : (e = c.Compliance.$underline_style_section_titles()) === a || e._isBoolean && !0 != e ? this["$is_section_title?"](b.$peek_line()) : (e = this)["$is_section_title?"].apply(e, [].concat(b.$peek_lines(2)))
            });
            d.defs(n, "$is_next_line_document_title?",
                function (a, b) {
                    return this["$is_next_line_section?"](a, b)["$=="](0)
                });
            d.defs(n, "$is_section_title?", function (b, c) {
                var d, g, l = a;
                null == c && (c = a);
                return(d = l = this["$is_single_line_section_title?"](b)) === a || d._isBoolean && !0 != d ? (d = (g = !1 !== c && c !== a) ? l = this["$is_two_line_section_title?"](b, c) : g) === a || d._isBoolean && !0 != d ? !1 : l : l
            });
            d.defs(n, "$is_single_line_section_title?", function (b) {
                var d, e, g, l, k = a, h = a, k = !1 !== b && b !== a ? b.$chr() : a;
                return(d = (e = !1 !== (g = k["$=="]("=")) && g !== a ? g : (l = c.Compliance.$markdown_syntax(), !1 !==
                    l && l !== a ? k["$=="]("#") : l), !1 !== e && e !== a ? h = c.AtxSectionRx.$match(b) : e)) === a || d._isBoolean && !0 != d ? !1 : this.$single_line_section_level(h["$[]"](1))
            });
            d.defs(n, "$is_two_line_section_title?", function (b, d) {
                var e, g, l, k, h, m;
                return(e = (g = (l = (k = (h = (m = !1 !== b && b !== a) ? d : m, !1 !== h && h !== a ? c.SECTION_LEVELS["$has_key?"](d.$chr()) : h), !1 !== k && k !== a ? d["$=~"](c.SetextSectionLineRx) : k), !1 !== l && l !== a ? b["$=~"](c.SetextSectionTitleRx) : l), !1 !== g && g !== a ? this.$line_length(b)["$-"](this.$line_length(d)).$abs()["$<="](1) : g)) === a ||
                    e._isBoolean && !0 != e ? !1 : this.$section_level(d)
            });
            d.defs(n, "$parse_section_title", function (b, d) {
                var e, g, l, k, h, m = a, n = a, r = a, t = a, v = a, y = a, u = a, w = a, D = a, B = a, x = a, m = b.$read_line(), r = n = a, t = -1, v = a, y = !0, u = m.$chr();
                (e = (g = !1 !== (l = u["$=="]("=")) && l !== a ? l : (k = c.Compliance.$markdown_syntax(), !1 !== k && k !== a ? u["$=="]("#") : k), !1 !== g && g !== a ? w = c.AtxSectionRx.$match(m) : g)) === a || e._isBoolean && !0 != e ? (e = c.Compliance.$underline_style_section_titles()) === a || e._isBoolean && !0 != e || (e = (g = (l = (k = (h = B = b.$peek_line(!0), !1 !== h && h !== a ? c.SECTION_LEVELS["$has_key?"](B.$chr()) :
                    h), !1 !== k && k !== a ? B["$=~"](c.SetextSectionLineRx) : k), !1 !== l && l !== a ? x = c.SetextSectionTitleRx.$match(m) : l), !1 !== g && g !== a ? this.$line_length(m)["$-"](this.$line_length(B)).$abs()["$<="](1) : g)) === a || e._isBoolean && !0 != e || (r = x["$[]"](1), (e = (g = r["$end_with?"]("]]"), !1 !== g && g !== a ? D = c.InlineSectionAnchorRx.$match(r) : g)) === a || e._isBoolean && !0 != e || (e = D["$[]"](2)["$nil?"]()) === a || e._isBoolean && !0 != e || (r = D["$[]"](1), n = D["$[]"](3), v = D["$[]"](4)), t = this.$section_level(B), y = !1, b.$advance()) : (t = this.$single_line_section_level(w["$[]"](1)),
                    r = w["$[]"](2), (e = (g = r["$end_with?"]("]]"), !1 !== g && g !== a ? D = c.InlineSectionAnchorRx.$match(r) : g)) === a || e._isBoolean && !0 != e || (e = D["$[]"](2)["$nil?"]()) === a || e._isBoolean && !0 != e || (r = D["$[]"](1), n = D["$[]"](3), v = D["$[]"](4)));
                t["$>="](0) && (t = t["$+"](d.$attr("leveloffset", 0).$to_i()));
                return[n, v, r, t, y]
            });
            d.defs(n, "$line_length", function (b) {
                var d;
                return(d = c.FORCE_UNICODE_LINE_LENGTH) === a || d._isBoolean && !0 != d ? b.$length() : b.$scan(c.UnicodeCharScanRx).$length()
            });
            d.defs(n, "$parse_header_metadata", function (b, p) {
                var e, g, l, k, h, m, n = a, t = a, v = a, u = a, y = u = a, w = a, x = a, v = t = a;
                null == p && (p = a);
                this.$process_attribute_entries(b, p);
                n = r([], {});
                v = t = a;
                if ((e = (g = b["$has_more_lines?"](), !1 !== g && g !== a ? b["$next_line_empty?"]()["$!"]() : g)) !== a && (!e._isBoolean || !0 == e)) {
                    u = this.$process_authors(b.$read_line());
                    if ((e = u["$empty?"]()) === a || e._isBoolean && !0 != e)!1 !== p && p !== a && ((e = (g = u).$each, e._p = (l = function (b, c) {
                        var e, f;
                        null == b && (b = a);
                        null == c && (c = a);
                        return(e = p.$attributes()["$has_key?"](b)) === a || e._isBoolean && !0 != e ? p.$attributes()["$[]="](b,
                            (e = c["$is_a?"](null == (f = d.Object._scope.String) ? d.cm("String") : f)) === a || e._isBoolean && !0 != e ? c : p.$apply_header_subs(c)) : a
                    }, l._s = this, l), e).call(g), t = p.$attributes()["$[]"]("author"), v = p.$attributes()["$[]"]("authors")), n = u;
                    this.$process_attribute_entries(b, p);
                    u = r([], {});
                    if ((e = (k = b["$has_more_lines?"](), !1 !== k && k !== a ? b["$next_line_empty?"]()["$!"]() : k)) !== a && (!e._isBoolean || !0 == e))if (y = b.$read_line(), (e = w = c.RevisionInfoLineRx.$match(y)) === a || e._isBoolean && !0 != e)b.$unshift_line(y); else {
                        u["$[]="]("revdate",
                            w["$[]"](2).$strip());
                        if ((e = w["$[]"](1)["$nil?"]()) === a || e._isBoolean && !0 != e)u["$[]="]("revnumber", w["$[]"](1).$rstrip());
                        if ((e = w["$[]"](3)["$nil?"]()) === a || e._isBoolean && !0 != e)u["$[]="]("revremark", w["$[]"](3).$rstrip())
                    }
                    if ((e = u["$empty?"]()) === a || e._isBoolean && !0 != e)!1 !== p && p !== a && (e = (k = u).$each, e._p = (h = function (b, c) {
                        var d;
                        null == b && (b = a);
                        null == c && (c = a);
                        return(d = p.$attributes()["$has_key?"](b)) === a || d._isBoolean && !0 != d ? p.$attributes()["$[]="](b, p.$apply_header_subs(c)) : a
                    }, h._s = this, h), e).call(k),
                        n.$update(u);
                    this.$process_attribute_entries(b, p);
                    b.$skip_blank_lines()
                }
                if (!1 !== p && p !== a) {
                    u = a;
                    if ((e = (m = p.$attributes()["$has_key?"]("author"), !1 !== m && m !== a ? (x = p.$attributes()["$[]"]("author"))["$=="](t)["$!"]() : m)) === a || e._isBoolean && !0 != e)if ((e = (m = p.$attributes()["$has_key?"]("authors"), !1 !== m && m !== a ? (x = p.$attributes()["$[]"]("authors"))["$=="](v)["$!"]() : m)) === a || e._isBoolean && !0 != e) {
                        t = [];
                        for (v = "author_" + t.$size()["$+"](1); (m = p.$attributes()["$has_key?"](v)) !== a && (!m._isBoolean || !0 == m);)t["$<<"](p.$attributes()["$[]"](v)),
                            v = "author_" + t.$size()["$+"](1);
                        t.$size()["$=="](1) ? u = this.$process_authors(t["$[]"](0), !0, !1) : t.$size()["$>"](1) && (u = this.$process_authors(t.$join("; "), !0))
                    } else u = this.$process_authors(x, !0); else u = this.$process_authors(x, !0, !1);
                    if (!1 !== u && u !== a && (p.$attributes().$update(u), (e = (m = p.$attributes()["$has_key?"]("email")["$!"](), !1 !== m && m !== a ? p.$attributes()["$has_key?"]("email_1") : m)) !== a && (!e._isBoolean || !0 == e)))p.$attributes()["$[]="]("email", p.$attributes()["$[]"]("email_1"))
                }
                return n
            });
            d.defs(n,
                "$process_authors", function (b, d, e) {
                    var g, l, k, h, m, n = this, t = a, v = a, u = a;
                    null == d && (d = !1);
                    null == e && (e = !0);
                    t = r([], {});
                    v = "author authorinitials firstname middlename lastname email".split(" ");
                    u = function () {
                        return!1 !== e && e !== a ? (g = (l = b.$split(";")).$map, g._p = (k = function (b) {
                            null == b && (b = a);
                            return b.$strip()
                        }, k._s = n, k), g).call(l) : [b]
                    }();
                    (g = (h = u).$each_with_index, g._p = (m = function (b, e) {
                        var f = m._s || this, g, k, l, h, s, n, G, u = a, w = a, x = a, Z = x = a, A = a;
                        null == b && (b = a);
                        null == e && (e = a);
                        if ((g = b["$empty?"]()) !== a && (!g._isBoolean || !0 ==
                            g))return a;
                        u = r([], {});
                        (g = e["$zero?"]()) === a || g._isBoolean && !0 != g ? (g = (h = v).$each, g._p = (s = function (b) {
                            null == b && (b = a);
                            return u["$[]="](b.$to_sym(), "" + b + "_" + e["$+"](1))
                        }, s._s = f, s), g).call(h) : (g = (k = v).$each, g._p = (l = function (b) {
                            null == b && (b = a);
                            return u["$[]="](b.$to_sym(), b)
                        }, l._s = f, l), g).call(k);
                        w = a;
                        !1 !== d && d !== a ? w = b.$split(" ", 3) : (g = x = c.AuthorInfoLineRx.$match(b)) === a || g._isBoolean && !0 != g || (w = x.$to_a(), w.$shift());
                        if ((g = w["$nil?"]()) === a || g._isBoolean && !0 != g) {
                            if (t["$[]="](u["$[]"]("firstname"), x = w["$[]"](0).$tr("_",
                                " ")), t["$[]="](u["$[]"]("author"), x), t["$[]="](u["$[]"]("authorinitials"), x["$[]"](0, 1)), (g = (n = w["$[]"](1)["$nil?"]()["$!"](), !1 !== n && n !== a ? w["$[]"](2)["$nil?"]()["$!"]() : n)) === a || g._isBoolean && !0 != g ? (g = w["$[]"](1)["$nil?"]()["$!"]()) === a || g._isBoolean && !0 != g || (t["$[]="](u["$[]"]("lastname"), A = w["$[]"](1).$tr("_", " ")), t["$[]="](u["$[]"]("author"), [x, A].$join(" ")), t["$[]="](u["$[]"]("authorinitials"), [x["$[]"](0, 1), A["$[]"](0, 1)].$join())) : (t["$[]="](u["$[]"]("middlename"), Z = w["$[]"](1).$tr("_",
                                " ")), t["$[]="](u["$[]"]("lastname"), A = w["$[]"](2).$tr("_", " ")), t["$[]="](u["$[]"]("author"), [x, Z, A].$join(" ")), t["$[]="](u["$[]"]("authorinitials"), [x["$[]"](0, 1), Z["$[]"](0, 1), A["$[]"](0, 1)].$join())), (g = !1 !== (n = d) && n !== a ? n : w["$[]"](3)["$nil?"]()) === a || g._isBoolean && !0 != g)t["$[]="](u["$[]"]("email"), w["$[]"](3))
                        } else t["$[]="](u["$[]"]("author"), t["$[]="](u["$[]"]("firstname"), x = b.$strip().$tr_s(" ", " "))), t["$[]="](u["$[]"]("authorinitials"), x["$[]"](0, 1));
                        t["$[]="]("authorcount", e["$+"](1));
                        e["$=="](1) && (g = (n = v).$each, g._p = (G = function (b) {
                            var c;
                            null == b && (b = a);
                            return(c = t["$has_key?"](b)) === a || c._isBoolean && !0 != c ? a : t["$[]="]("" + b + "_1", t["$[]"](b))
                        }, G._s = f, G), g).call(n);
                        return(g = e["$zero?"]()) === a || g._isBoolean && !0 != g ? t["$[]="]("authors", "" + t["$[]"]("authors") + ", " + t["$[]"](u["$[]"]("author"))) : t["$[]="]("authors", t["$[]"](u["$[]"]("author")))
                    }, m._s = n, m), g).call(h);
                    return t
                });
            d.defs(n, "$parse_block_metadata_lines", function (b, c, d, g) {
                var l;
                null == d && (d = r([], {}));
                for (null == g && (g = r([], {})); (l =
                    this.$parse_block_metadata_line(b, c, d, g)) !== a && (!l._isBoolean || !0 == l);)b.$advance(), b.$skip_blank_lines();
                return d
            });
            d.defs(n, "$parse_block_metadata_line", function (b, d, e, g) {
                var l, k, h, m = a, n = a, t = a, n = m = a;
                null == g && (g = r([], {}));
                if ((l = b["$has_more_lines?"]()) === a || l._isBoolean && !0 != l)return!1;
                m = b.$peek_line();
                if ((l = (k = n = m["$start_with?"]("//"), !1 !== k && k !== a ? t = c.CommentBlockRx.$match(m) : k)) !== a && (!l._isBoolean || !0 == l))m = t["$[]"](0), b.$read_lines_until(r(["skip_first_line", "preserve_last_line", "terminator",
                    "skip_processing"], {skip_first_line: !0, preserve_last_line: !0, terminator: m, skip_processing: !0})); else if ((l = (k = !1 !== n && n !== a) ? c.CommentLineRx["$=~"](m) : k) === a || l._isBoolean && !0 != l)if ((l = (k = (h = g["$[]"]("text")["$!"](), !1 !== h && h !== a ? m["$start_with?"](":") : h), !1 !== k && k !== a ? t = c.AttributeEntryRx.$match(m) : k)) !== a && (!l._isBoolean || !0 == l))this.$process_attribute_entry(b, d, e, t); else if ((l = (k = n = (h = m["$start_with?"]("["), !1 !== h && h !== a ? m["$end_with?"]("]") : h), !1 !== k && k !== a ? t = c.BlockAnchorRx.$match(m) : k)) === a ||
                    l._isBoolean && !0 != l)if ((l = (k = !1 !== n && n !== a) ? t = c.BlockAttributeListRx.$match(m) : k) === a || l._isBoolean && !0 != l) {
                    if ((l = (k = g["$[]"]("text")["$!"](), !1 !== k && k !== a ? t = c.BlockTitleRx.$match(m) : k)) === a || l._isBoolean && !0 != l)return!1;
                    e["$[]="]("title", t["$[]"](1))
                } else d.$document().$parse_attributes(t["$[]"](1), [], r(["sub_input", "into"], {sub_input: !0, into: e})); else if ((l = t["$[]"](1)["$nil_or_empty?"]()) === a || l._isBoolean && !0 != l)if (e["$[]="]("id", t["$[]"](1)), (l = t["$[]"](2)["$nil?"]()) === a || l._isBoolean && !0 !=
                    l)e["$[]="]("reftext", t["$[]"](2));
                return!0
            });
            d.defs(n, "$process_attribute_entries", function (b, c, d) {
                var g;
                null == d && (d = a);
                for (b.$skip_comment_lines(); (g = this.$process_attribute_entry(b, c, d)) !== a && (!g._isBoolean || !0 == g);)b.$advance(), b.$skip_comment_lines()
            });
            d.defs(n, "$process_attribute_entry", function (b, d, e, g) {
                var l, k, h = a, m = a, n = a;
                null == e && (e = a);
                null == g && (g = a);
                !1 !== (l = g) && l !== a ? l : g = (k = b["$has_more_lines?"]()) === a || k._isBoolean && !0 != k ? a : c.AttributeEntryRx.$match(b.$peek_line());
                if (!1 !== g && g !== a) {
                    h =
                        g["$[]"](1);
                    m = !1 !== (l = g["$[]"](2)) && l !== a ? l : "";
                    if ((l = m["$end_with?"](c.LINE_BREAK)) !== a && (!l._isBoolean || !0 == l))for (m = m.$chop().$rstrip(); (k = b.$advance()) !== a && (!k._isBoolean || !0 == k);) {
                        n = b.$peek_line().$strip();
                        if ((k = n["$empty?"]()) !== a && (!k._isBoolean || !0 == k))break;
                        if ((k = n["$end_with?"](c.LINE_BREAK)) === a || k._isBoolean && !0 != k) {
                            m = "" + m + " " + n;
                            break
                        } else m = "" + m + " " + n.$chop().$rstrip()
                    }
                    this.$store_attribute(h, m, !1 !== d && d !== a ? d.$document() : a, e);
                    return!0
                }
                return!1
            });
            d.defs(n, "$store_attribute", function (b, d, e, g) {
                var l, k, h = a;
                null == e && (e = a);
                null == g && (g = a);
                (l = b["$end_with?"]("!")) === a || l._isBoolean && !0 != l ? (l = b["$start_with?"]("!")) === a || l._isBoolean && !0 != l || (d = a, b = b["$[]"](v(1, -1, !1))) : (d = a, b = b.$chop());
                b = this.$sanitize_attribute_name(b);
                h = !0;
                !1 !== e && e !== a && (h = (l = d["$nil?"]()) === a || l._isBoolean && !0 != l ? e.$set_attribute(b, d) : e.$delete_attribute(b));
                ((l = !1 !== (k = h["$!"]()) && k !== a ? k : g["$nil?"]()) === a || l._isBoolean && !0 != l) && c.Document._scope.AttributeEntry.$new(b, d).$save_to(g);
                return[b, d]
            });
            d.defs(n, "$resolve_list_marker",
                function (b, c, d, g, l) {
                    var k, h;
                    null == d && (d = 0);
                    null == g && (g = !1);
                    null == l && (l = a);
                    return(k = (h = b["$=="]("olist")) ? c["$start_with?"](".")["$!"]() : h) === a || k._isBoolean && !0 != k ? b["$=="]("colist") ? "<1>" : c : this.$resolve_ordered_list_marker(c, d, g, l)
                });
            d.defs(n, "$resolve_ordered_list_marker", function (b, d, e, g) {
                var l, k, h, m, n = a, r = a, t = a, n = a;
                null == d && (d = 0);
                null == e && (e = !1);
                null == g && (g = a);
                n = (l = (k = c.ORDERED_LIST_STYLES).$detect, l._p = (h = function (d) {
                    null == d && (d = a);
                    return c.OrderedListMarkerRxMap["$[]"](d)["$=~"](b)
                }, h._s = this,
                    h), l).call(k);
                r = t = a;
                "arabic"["$==="](n) ? (!1 !== e && e !== a && (r = d["$+"](1), t = b.$to_i()), b = "1.") : "loweralpha"["$==="](n) ? (!1 !== e && e !== a && (r = "a"["$[]"](0).$ord()["$+"](d).$chr(), t = b.$chomp(".")), b = "a.") : "upperalpha"["$==="](n) ? (!1 !== e && e !== a && (r = "A"["$[]"](0).$ord()["$+"](d).$chr(), t = b.$chomp(".")), b = "A.") : "lowerroman"["$==="](n) ? (!1 !== e && e !== a && (r = d["$+"](1), t = this.$roman_numeral_to_int(b.$chomp(")"))), b = "i)") : "upperroman"["$==="](n) && (!1 !== e && e !== a && (r = d["$+"](1), t = this.$roman_numeral_to_int(b.$chomp(")"))),
                    b = "I)");
                (l = (m = !1 !== e && e !== a) ? r["$=="](t)["$!"]() : m) === a || l._isBoolean && !0 != l || this.$warn("asciidoctor: WARNING: " + g.$line_info() + ": list item index: expected " + r + ", got " + t);
                return b
            });
            d.defs(n, "$is_sibling_list_item?", function (b, p, e) {
                var g, l, k = a, h = a, m = a;
                (g = e["$is_a?"](null == (l = d.Object._scope.Regexp) ? d.cm("Regexp") : l)) === a || g._isBoolean && !0 != g ? (k = c.ListRxMap["$[]"](p), h = e) : (k = e, h = !1);
                return(g = m = k.$match(b)) === a || g._isBoolean && !0 != g ? !1 : !1 !== h && h !== a ? h["$=="](this.$resolve_list_marker(p, m["$[]"](1))) :
                    !0
            });
            d.defs(n, "$next_table", function (b, p, e) {
                var g, l, k, h, m, n, r, t = a, u = a, y = a, w = a, x = a, D = a, B = a, C = a, A = a, J = a, F = D = a, t = c.Table.$new(p, e);
                (g = e["$has_key?"]("title")) === a || g._isBoolean && !0 != g || (t["$title="](e.$delete("title")), t.$assign_caption(e.$delete("caption")));
                (g = e["$has_key?"]("cols")) === a || g._isBoolean && !0 != g ? u = !1 : (t.$create_columns(this.$parse_col_specs(e["$[]"]("cols"))), u = !0);
                y = b.$skip_blank_lines();
                w = c.Table._scope.ParserContext.$new(b, t, e);
                for (x = -1; (l = b["$has_more_lines?"]()) !== a && (!l._isBoolean ||
                    !0 == l);) {
                    x = x["$+"](1);
                    D = b.$read_line();
                    (l = (k = (h = (m = (n = y["$=="](0)) ? x["$zero?"]() : n, !1 !== m && m !== a ? e["$has_key?"]("options")["$!"]() : m), !1 !== h && h !== a ? (B = b.$peek_line())["$nil?"]()["$!"]() : h), !1 !== k && k !== a ? B["$empty?"]() : k)) === a || l._isBoolean && !0 != l || (t["$has_header_option="](!0), t.$set_option("header"));
                    w.$format()["$=="]("psv") && ((l = w["$starts_with_delimiter?"](D)) === a || l._isBoolean && !0 != l ? (l = d.to_ary(this.$parse_cell_spec(D, "start")), C = null == l[0] ? a : l[0], D = null == l[1] ? a : l[1], (l = C["$nil?"]()["$!"]()) ===
                        a || l._isBoolean && !0 != l || w.$close_open_cell(C)) : (D = D["$[]"](v(1, -1, !1)), w.$close_open_cell()));
                    for (A = !1; (k = !1 !== (h = A["$!"]()) && h !== a ? h : D["$empty?"]()["$!"]()) !== a && (!k._isBoolean || !0 == k);)if (A = !0, (k = J = w.$match_delimiter(D)) === a || k._isBoolean && !0 != k) {
                        w["$buffer="]("" + w.$buffer() + D + c.EOL);
                        if (w.$format()["$=="]("csv"))w["$buffer="]("" + w.$buffer().$rstrip() + " ");
                        D = "";
                        (k = !1 !== (h = w.$format()["$=="]("psv")) && h !== a ? h : (m = w.$format()["$=="]("csv")) ? w["$buffer_has_unclosed_quotes?"]() : m) === a || k._isBoolean && !0 !=
                            k ? w.$close_cell(!0) : w.$keep_cell_open()
                    } else {
                        if (w.$format()["$=="]("csv")) {
                            if ((k = w["$buffer_has_unclosed_quotes?"](J.$pre_match())) !== a && (!k._isBoolean || !0 == k)) {
                                D = w.$skip_matched_delimiter(J);
                                continue
                            }
                        } else if ((k = J.$pre_match()["$end_with?"]("\\")) !== a && (!k._isBoolean || !0 == k)) {
                            D = w.$skip_matched_delimiter(J, !0);
                            continue
                        }
                        if (w.$format()["$=="]("psv"))k = d.to_ary(this.$parse_cell_spec(J.$pre_match(), "end")), C = null == k[0] ? a : k[0], D = null == k[1] ? a : k[1], w.$push_cell_spec(C), w["$buffer="]("" + w.$buffer() + D); else w["$buffer="]("" +
                            w.$buffer() + J.$pre_match());
                        D = J.$post_match();
                        w.$close_cell()
                    }
                    if ((l = w["$cell_open?"]()) === a || l._isBoolean && !0 != l)y = b.$skip_blank_lines();
                    (l = b["$has_more_lines?"]()["$!"]()) === a || l._isBoolean && !0 != l || w.$close_cell(!0)
                }
                g = "colcount";
                l = t.$attributes();
                !1 !== (k = l["$[]"](g)) && k !== a ? k : l["$[]="](g, w.$col_count());
                (g = u["$!"]()) === a || g._isBoolean && !0 != g || (F = 100["$/"](w.$col_count()).$floor(), (g = (l = t.$columns()).$each, g._p = (r = function (b) {
                    null == b && (b = a);
                    return b.$assign_width(0, F)
                }, r._s = this, r), g).call(l));
                t.$partition_header_footer(e);
                return t
            });
            d.defs(n, "$parse_col_specs", function (b) {
                var h, e, g, l, k, m = a;
                if ((h = c.DigitsRx["$=~"](b)) !== a && (!h._isBoolean || !0 == h))return(h = (e = null == (l = d.Object._scope.Array) ? d.cm("Array") : l).$new, h._p = (g = function () {
                    return r(["width"], {width: 1})
                }, g._s = this, g), h).call(e, b.$to_i());
                m = [];
                (h = (l = b.$split(",")).$each, h._p = (k = function (b) {
                    var e = k._s || this, f, g, l, h = a, p = a, n = a, t = a, h = a;
                    null == b && (b = a);
                    if ((f = h = c.ColumnSpecRx.$match(b)) === a || f._isBoolean && !0 != f)return a;
                    p = r([], {});
                    if ((f = h["$[]"](2)) !== a && (!f._isBoolean ||
                        !0 == f)) {
                        f = d.to_ary(h["$[]"](2).$split("."));
                        n = null == f[0] ? a : f[0];
                        t = null == f[1] ? a : f[1];
                        if ((f = (g = n["$nil_or_empty?"]()["$!"](), !1 !== g && g !== a ? c.Table._scope.ALIGNMENTS["$[]"]("h")["$has_key?"](n) : g)) !== a && (!f._isBoolean || !0 == f))p["$[]="]("halign", c.Table._scope.ALIGNMENTS["$[]"]("h")["$[]"](n));
                        if ((f = (g = t["$nil_or_empty?"]()["$!"](), !1 !== g && g !== a ? c.Table._scope.ALIGNMENTS["$[]"]("v")["$has_key?"](t) : g)) !== a && (!f._isBoolean || !0 == f))p["$[]="]("valign", c.Table._scope.ALIGNMENTS["$[]"]("v")["$[]"](t))
                    }
                    p["$[]="]("width",
                        (f = h["$[]"](3)["$nil?"]()["$!"]()) === a || f._isBoolean && !0 != f ? 1 : h["$[]"](3).$to_i());
                    if ((f = (g = h["$[]"](4), !1 !== g && g !== a ? c.Table._scope.TEXT_STYLES["$has_key?"](h["$[]"](4)) : g)) !== a && (!f._isBoolean || !0 == f))p["$[]="]("style", c.Table._scope.TEXT_STYLES["$[]"](h["$[]"](4)));
                    h = (f = h["$[]"](1)["$nil?"]()["$!"]()) === a || f._isBoolean && !0 != f ? 1 : h["$[]"](1).$to_i();
                    return(f = (g = 1).$upto, f._p = (l = function () {
                        return m["$<<"](p.$dup())
                    }, l._s = e, l), f).call(g, h)
                }, k._s = this, k), h).call(l);
                return m
            });
            d.defs(n, "$parse_cell_spec",
                function (b, h) {
                    var e, g, l = a, k = a, m = a, n = a, t = a;
                    null == h && (h = "start");
                    l = h["$=="]("end") ? r([], {}) : a;
                    k = b;
                    if ((e = m = (h["$=="]("start") ? c.CellSpecStartRx : c.CellSpecEndRx).$match(b)) !== a && (!e._isBoolean || !0 == e)) {
                        l = r([], {});
                        if ((e = m["$[]"](0)["$empty?"]()) !== a && (!e._isBoolean || !0 == e))return[l, b];
                        k = h["$=="]("start") ? m.$post_match() : m.$pre_match();
                        if ((e = m["$[]"](1)) !== a && (!e._isBoolean || !0 == e))if (e = d.to_ary(m["$[]"](1).$split(".")), n = null == e[0] ? a : e[0], t = null == e[1] ? a : e[1], n = (e = n["$nil_or_empty?"]()) === a || e._isBoolean &&
                            !0 != e ? n.$to_i() : 1, t = (e = t["$nil_or_empty?"]()) === a || e._isBoolean && !0 != e ? t.$to_i() : 1, m["$[]"](2)["$=="]("+")) {
                            if (!n["$=="](1))l["$[]="]("colspan", n);
                            if (!t["$=="](1))l["$[]="]("rowspan", t)
                        } else if (m["$[]"](2)["$=="]("*") && !n["$=="](1))l["$[]="]("repeatcol", n);
                        if ((e = m["$[]"](3)) !== a && (!e._isBoolean || !0 == e)) {
                            e = d.to_ary(m["$[]"](3).$split("."));
                            n = null == e[0] ? a : e[0];
                            t = null == e[1] ? a : e[1];
                            if ((e = (g = n["$nil_or_empty?"]()["$!"](), !1 !== g && g !== a ? c.Table._scope.ALIGNMENTS["$[]"]("h")["$has_key?"](n) : g)) !== a && (!e._isBoolean ||
                                !0 == e))l["$[]="]("halign", c.Table._scope.ALIGNMENTS["$[]"]("h")["$[]"](n));
                            if ((e = (g = t["$nil_or_empty?"]()["$!"](), !1 !== g && g !== a ? c.Table._scope.ALIGNMENTS["$[]"]("v")["$has_key?"](t) : g)) !== a && (!e._isBoolean || !0 == e))l["$[]="]("valign", c.Table._scope.ALIGNMENTS["$[]"]("v")["$[]"](t))
                        }
                        if ((e = (g = m["$[]"](4), !1 !== g && g !== a ? c.Table._scope.TEXT_STYLES["$has_key?"](m["$[]"](4)) : g)) !== a && (!e._isBoolean || !0 == e))l["$[]="]("style", c.Table._scope.TEXT_STYLES["$[]"](m["$[]"](4)))
                    }
                    return[l, k]
                });
            d.defs(n, "$parse_style_attribute",
                function (b, c) {
                    var d, g, l, k, h, m, n, t = a, v = a, u = a, y = a, w = a, x = a, A = v = a, B = a;
                    null == c && (c = a);
                    t = b["$[]"]("style");
                    v = b["$[]"](1);
                    if ((d = !1 !== (g = v["$!"]()) && g !== a ? g : v["$include?"](" ")) === a || d._isBoolean && !0 != d) {
                        u = "style";
                        y = [];
                        w = r([], {});
                        x = (d = (g = this).$lambda, d._p = (l = function () {
                            var b = l._s || this, d, e, f = a;
                            return(d = y["$empty?"]()) === a || d._isBoolean && !0 != d ? (f = u, "role"["$==="](f) || "option"["$==="](f) ? (d = u, b = w, !1 !== (e = b["$[]"](d)) && e !== a ? e : b["$[]="](d, []), w["$[]"](u).$push(y.$join())) : ("id"["$==="](f) && ((d = w["$has_key?"]("id")) ===
                                a || d._isBoolean && !0 != d || b.$warn("asciidoctor: WARNING:" + ((d = c["$nil?"]()) === a || d._isBoolean && !0 != d ? " " + c.$prev_line_info() + ":" : a) + " multiple ids detected in style attribute")), w["$[]="](u, y.$join())), y = []) : (d = u["$=="]("style")["$!"]()) === a || d._isBoolean && !0 != d ? a : b.$warn("asciidoctor: WARNING:" + ((d = c["$nil?"]()) === a || d._isBoolean && !0 != d ? " " + c.$prev_line_info() + ":" : a) + " invalid empty " + u + " detected in style attribute")
                        }, l._s = this, l), d).call(g);
                        (d = (k = v).$each_char, d._p = (h = function (b) {
                            var c, d, e, f = a;
                            null ==
                                b && (b = a);
                            if ((c = !1 !== (d = !1 !== (e = b["$=="](".")) && e !== a ? e : b["$=="]("#")) && d !== a ? d : b["$=="]("%")) === a || c._isBoolean && !0 != c)return y.$push(b);
                            x.$call();
                            f = b;
                            return"."["$==="](f) ? u = "role" : "#"["$==="](f) ? u = "id" : "%"["$==="](f) ? u = "option" : a
                        }, h._s = this, h), d).call(k);
                        if (u["$=="]("style"))v = b["$[]="]("style", v); else {
                            x.$call();
                            v = (d = w["$has_key?"]("style")) === a || d._isBoolean && !0 != d ? a : b["$[]="]("style", w["$[]"]("style"));
                            if ((d = w["$has_key?"]("id")) !== a && (!d._isBoolean || !0 == d))b["$[]="]("id", w["$[]"]("id"));
                            if ((d =
                                w["$has_key?"]("role")) !== a && (!d._isBoolean || !0 == d))b["$[]="]("role", w["$[]"]("role")["$*"](" "));
                            if ((d = w["$has_key?"]("option")) !== a && (!d._isBoolean || !0 == d))if ((d = (m = A = w["$[]"]("option")).$each, d._p = (n = function (c) {
                                null == c && (c = a);
                                return b["$[]="]("" + c + "-option", "")
                            }, n._s = this, n), d).call(m), (d = B = b["$[]"]("options")) === a || d._isBoolean && !0 != d)b["$[]="]("options", A["$*"](",")); else b["$[]="]("options", A["$+"](B.$split(","))["$*"](","))
                        }
                        return[v, t]
                    }
                    b["$[]="]("style", v);
                    return[v, t]
                });
            d.defs(n, "$reset_block_indent!",
                function (b, d) {
                    var e, g, l, k, h, m, n = a, r = a, t = a, w = a, y = a;
                    null == d && (d = 0);
                    if ((e = !1 !== (g = d["$!"]()) && g !== a ? g : b["$empty?"]()) !== a && (!e._isBoolean || !0 == e))return a;
                    n = !1;
                    r = "    ";
                    t = (e = (g = b).$map, e._p = (l = function (b) {
                        var d, e = a, f = a;
                        null == b && (b = a);
                        if ((d = b.$chr().$lstrip()["$empty?"]()) === a || d._isBoolean && !0 != d)return u.$v = [], u;
                        (d = b["$include?"](c.TAB)) === a || d._isBoolean && !0 != d || (n = !0, b = b.$gsub(c.TAB_PATTERN, r));
                        return(d = (e = b.$lstrip())["$empty?"]()) === a || d._isBoolean && !0 != d ? (f = b.$length()["$-"](e.$length()))["$=="](0) ?
                            (u.$v = [], u) : f : a
                    }, l._s = this, l), e).call(g);
                    ((e = !1 !== (k = t["$empty?"]()) && k !== a ? k : (t = t.$compact())["$empty?"]()) === a || e._isBoolean && !0 != e) && (w = t.$min())["$>"](0) && (e = (k = b)["$map!"], e._p = (h = function (b) {
                        null == b && (b = a);
                        !1 !== n && n !== a && (b = b.$gsub(c.TAB_PATTERN, r));
                        return b["$[]"](v(w, -1, !1)).$to_s()
                    }, h._s = this, h), e).call(k);
                    d["$>"](0) && (y = " "["$*"](d), (e = b["$map!"], e._p = (m = function (b) {
                        null == b && (b = a);
                        return"" + y + b
                    }, m._s = this, m), e).call(b));
                    return a
                });
            d.defs(n, "$sanitize_attribute_name", function (a) {
                return a.$gsub(c.InvalidAttributeNameCharsRx,
                    "").$downcase()
            });
            return(d.defs(n, "$roman_numeral_to_int", function (b) {
                var c, d, g, l = a, k = a;
                b = b.$downcase();
                l = r(["i", "v", "x"], {i: 1, v: 5, x: 10});
                k = 0;
                (c = (d = v(0, b.$length()["$-"](1), !1)).$each, c._p = (g = function (c) {
                    var d, e, g = a;
                    null == c && (c = a);
                    g = l["$[]"](b["$[]"](v(c, c, !1)));
                    return(d = (e = c["$+"](1)["$<"](b.$length())) ? l["$[]"](b["$[]"](v(c["$+"](1), c["$+"](1), !1)))["$>"](g) : e) === a || d._isBoolean && !0 != d ? k = k["$+"](g) : k = k["$-"](g)
                }, g._s = this, g), c).call(d);
                return k
            }), a) && "roman_numeral_to_int"
        })(t(A, "Asciidoctor"),
                null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2, r = d.range, v = d.gvars;
    return function (x) {
        (function (u, $super) {
            function q() {
            }

            var h = q = t(u, $super, "PathResolver", q), n = h._proto, b = h._scope;
            n.file_separator = n._partition_path_web = n._partition_path_sys = n.working_dir = a;
            d.cdecl(b, "DOT", ".");
            d.cdecl(b, "DOT_DOT", "..");
            d.cdecl(b, "DOT_SLASH", "./");
            d.cdecl(b, "SLASH", "/");
            d.cdecl(b, "BACKSLASH", "\\");
            d.cdecl(b, "DOUBLE_SLASH", "//");
            d.cdecl(b, "WindowsRootRx", /^[a-zA-Z]:(?:\\|\/)/);
            h.$attr_accessor("file_separator");
            h.$attr_accessor("working_dir");
            n.$initialize = function (b, f) {
                var h, e;
                null == b && (b = a);
                null == f && (f = a);
                this.file_separator = !1 !== b && b !== a ? b : !1 !== (h = (null == (e = d.Object._scope.File) ? d.cm("File") : e)._scope.ALT_SEPARATOR) && h !== a ? h : (null == (e = d.Object._scope.File) ? d.cm("File") : e)._scope.SEPARATOR;
                this.working_dir = !1 !== f && f !== a ? (h = this["$is_root?"](f)) === a || h._isBoolean && !0 != h ? (null == (h = d.Object._scope.File) ? d.cm("File") : h).$expand_path(f) : f : (null == (h = d.Object._scope.File) ? d.cm("File") : h).$expand_path((null == (h = d.Object._scope.Dir) ? d.cm("Dir") :
                    h).$pwd());
                this._partition_path_sys = w([], {});
                return this._partition_path_web = w([], {})
            };
            n["$is_root?"] = function (c) {
                var d, h;
                return(d = c["$start_with?"](b.SLASH)) === a || d._isBoolean && !0 != d ? (d = (h = this.file_separator["$=="](b.BACKSLASH)) ? b.WindowsRootRx["$=~"](c) : h) === a || d._isBoolean && !0 != d ? !1 : !0 : !0
            };
            n["$is_unc?"] = function (a) {
                return a["$start_with?"](b.DOUBLE_SLASH)
            };
            n["$is_web_root?"] = function (a) {
                return a["$start_with?"](b.SLASH)
            };
            n.$posixfy = function (c) {
                var d;
                return(d = c["$nil_or_empty?"]()) === a || d._isBoolean &&
                    !0 != d ? (d = c["$include?"](b.BACKSLASH)) === a || d._isBoolean && !0 != d ? c : c.$tr(b.BACKSLASH, b.SLASH) : ""
            };
            n.$expand_path = function (b) {
                var f = a, h = a;
                b = d.to_ary(this.$partition_path(b));
                f = null == b[0] ? a : b[0];
                h = null == b[1] ? a : b[1];
                return this.$join_path(f, h)
            };
            n.$partition_path = function (c, d) {
                var h, e = a, g = e = a, l = a;
                null == d && (d = !1);
                if ((h = e = !1 !== d && d !== a ? this._partition_path_web["$[]"](c) : this._partition_path_sys["$[]"](c)) !== a && (!h._isBoolean || !0 == h))return e;
                e = this.$posixfy(c);
                g = !1 !== d && d !== a ? (h = this["$is_web_root?"](e)) ===
                    a || h._isBoolean && !0 != h ? (h = e["$start_with?"](b.DOT_SLASH)) === a || h._isBoolean && !0 != h ? a : b.DOT_SLASH : b.SLASH : (h = this["$is_root?"](e)) === a || h._isBoolean && !0 != h ? (h = e["$start_with?"](b.DOT_SLASH)) === a || h._isBoolean && !0 != h ? a : b.DOT_SLASH : (h = this["$is_unc?"](e)) === a || h._isBoolean && !0 != h ? (h = e["$start_with?"](b.SLASH)) === a || h._isBoolean && !0 != h ? e["$[]"](r(0, e.$index(b.SLASH), !1)) : b.SLASH : b.DOUBLE_SLASH;
                l = e.$split(b.SLASH);
                g["$=="](b.DOUBLE_SLASH) ? l = l["$[]"](r(2, -1, !1)) : !1 !== g && g !== a && l.$shift();
                l.$delete(b.DOT);
                return(!1 !== d && d !== a ? this._partition_path_web : this._partition_path_sys)["$[]="](c, [l, g, e])
            };
            n.$join_path = function (c, d) {
                null == d && (d = a);
                return!1 !== d && d !== a ? "" + d + c["$*"](b.SLASH) : c["$*"](b.SLASH)
            };
            n.$system_path = function (c, f, h, e) {
                var g, l, k, s = a, n = a, q = a, r = a, t = a, v = a, u = v = r = a, x = a;
                null == h && (h = a);
                null == e && (e = w([], {}));
                s = e.$fetch("recover", !0);
                !1 !== h && h !== a && (((g = this["$is_root?"](h)) === a || g._isBoolean && !0 != g) && this.$raise(null == (g = d.Object._scope.SecurityError) ? d.cm("SecurityError") : g, "Jail is not an absolute path: " +
                    h), h = this.$posixfy(h));
                (g = c["$nil_or_empty?"]()) === a || g._isBoolean && !0 != g ? (g = d.to_ary(this.$partition_path(c)), n = null == g[0] ? a : g[0], q = null == g[1] ? a : g[1]) : n = [];
                if ((g = n["$empty?"]()) !== a && (!g._isBoolean || !0 == g))if ((g = f["$nil_or_empty?"]()) === a || g._isBoolean && !0 != g) {
                    if ((g = this["$is_root?"](f)) === a || g._isBoolean && !0 != g)return this.$system_path(f, h, h);
                    if (!1 === h || h === a)return this.$expand_path(f)
                } else return!1 !== h && h !== a ? h : this.working_dir;
                if (!((g = (l = !1 !== q && q !== a) ? q["$=="](b.DOT_SLASH)["$!"]() : l) === a || g._isBoolean &&
                    !0 != g || (r = this.$join_path(n, q), (g = !1 !== (l = h["$!"]()) && l !== a ? l : r["$start_with?"](h)) === a || g._isBoolean && !0 != g)))return r;
                f = (g = f["$nil_or_empty?"]()) === a || g._isBoolean && !0 != g ? (g = this["$is_root?"](f)) === a || g._isBoolean && !0 != g ? this.$system_path(f, h, h) : this.$posixfy(f) : !1 !== h && h !== a ? h : this.working_dir;
                h["$=="](f) ? (g = d.to_ary(this.$partition_path(h)), t = null == g[0] ? a : g[0], v = null == g[1] ? a : g[1], r = t.$dup()) : (!1 !== h && h !== a ? (((g = f["$start_with?"](h)) === a || g._isBoolean && !0 != g) && this.$raise(null == (g = d.Object._scope.SecurityError) ?
                    d.cm("SecurityError") : g, "" + (!1 !== (g = e["$[]"]("target_name")) && g !== a ? g : "Start path") + " " + f + " is outside of jail: " + h + " (disallowed in safe mode)"), g = d.to_ary(this.$partition_path(f)), r = null == g[0] ? a : g[0], v = null == g[1] ? a : g[1], g = d.to_ary(this.$partition_path(h)), t = null == g[0] ? a : g[0]) : (g = d.to_ary(this.$partition_path(f)), r = null == g[0] ? a : g[0]), v = null == g[1] ? a : g[1]);
                u = r.$dup();
                x = !1;
                (g = (l = n).$each, g._p = (k = function (f) {
                    var g = k._s || this, l;
                    null == f && (f = a);
                    if (f["$=="](b.DOT_DOT)) {
                        if (!1 !== h && h !== a) {
                            if (u.$length()["$>"](t.$length()))return u.$pop();
                            if ((l = s["$!"]()) === a || l._isBoolean && !0 != l) {
                                if ((l = x["$!"]()) === a || l._isBoolean && !0 != l)return a;
                                g.$warn("asciidoctor: WARNING: " + (!1 !== (l = e["$[]"]("target_name")) && l !== a ? l : "path") + " has illegal reference to ancestor of jail, auto-recovering");
                                return x = !0
                            }
                            return g.$raise(null == (l = d.Object._scope.SecurityError) ? d.cm("SecurityError") : l, "" + (!1 !== (l = e["$[]"]("target_name")) && l !== a ? l : "path") + " " + c + " refers to location outside jail: " + h + " (disallowed in safe mode)")
                        }
                        return u.$pop()
                    }
                    return u.$push(f)
                }, k._s =
                    this, k), g).call(l);
                return this.$join_path(u, v)
            };
            n.$web_path = function (c, f) {
                var h, e, g, l = a, k = a, s = a, n = a;
                null == v["~"] && (v["~"] = a);
                null == f && (f = a);
                c = this.$posixfy(c);
                f = this.$posixfy(f);
                l = a;
                if ((h = !1 !== (e = f["$nil_or_empty?"]()) && e !== a ? e : this["$is_web_root?"](c)) === a || h._isBoolean && !0 != h)c = "" + f + b.SLASH + c, (h = (e = c["$include?"](":"), !1 !== e && e !== a ? b.UriSniffRx["$=~"](c) : e)) === a || h._isBoolean && !0 != h || (l = v["~"]["$[]"](0), c = c["$[]"](r(l.$length(), -1, !1)));
                h = d.to_ary(this.$partition_path(c, !0));
                k = null == h[0] ? a : h[0];
                s = null == h[1] ? a : h[1];
                n = [];
                (h = (e = k).$each, h._p = (g = function (c) {
                    var d, e;
                    null == c && (c = a);
                    return c["$=="](b.DOT_DOT) ? (d = n["$empty?"]()) === a || d._isBoolean && !0 != d ? n["$[]"](-1)["$=="](b.DOT_DOT) ? n["$<<"](c) : n.$pop() : (d = (e = !1 !== s && s !== a) ? s["$=="](b.DOT_SLASH)["$!"]() : e) === a || d._isBoolean && !0 != d ? n["$<<"](c) : a : n["$<<"](c)
                }, g._s = this, g), h).call(e);
                return!1 !== l && l !== a ? "" + l + this.$join_path(n, s) : this.$join_path(n, s)
            };
            return(n.$relative_path = function (b, d) {
                var h, e, g = a;
                if ((h = (e = this["$is_root?"](b), !1 !== e && e !== a ? this["$is_root?"](d) :
                    e)) === a || h._isBoolean && !0 != h)return b;
                g = d.$chomp(this.file_separator).$length()["$+"](1);
                return b["$[]"](r(g, -1, !1))
            }, a) && "relative_path"
        })(u(x, "Asciidoctor"), null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.breaker, t = d.slice, w = d.module, r = d.klass, v = d.hash2, x = d.range;
    return function (A) {
        A = w(A, "Asciidoctor");
        var m = A._scope;
        (function (m, $super) {
            function n() {
            }

            var b = n = r(m, $super, "Reader", n), c = b._proto, f = b._scope, p;
            c.file = c.dir = c.lines = c.process_lines = c.look_ahead = c.eof = c.unescape_next_line = c.lineno = c.path = c.source_lines = a;
            (function (b, $super) {
                function c() {
                }

                var f = c = r(b, $super, "Cursor", c), m = f._proto;
                f.$attr_accessor("file");
                f.$attr_accessor("dir");
                f.$attr_accessor("path");
                f.$attr_accessor("lineno");
                m.$initialize = function (b, c, d, e) {
                    null == c && (c = a);
                    null == d && (d = a);
                    null == e && (e = a);
                    this.file = b;
                    this.dir = c;
                    this.path = d;
                    return this.lineno = e
                };
                m.$line_info = function () {
                    return"" + this.$path() + ": line " + this.$lineno()
                };
                return d.defn(f, "$to_s", m.$line_info)
            })(b, null);
            b.$attr_reader("file");
            b.$attr_reader("dir");
            b.$attr_reader("path");
            b.$attr_reader("lineno");
            b.$attr_reader("source_lines");
            b.$attr_accessor("process_lines");
            c.$initialize = function (b, c, f) {
                var k, m;
                null == b && (b = a);
                null == c && (c = a);
                null == f && (f = v(["normalize"],
                    {normalize: !1}));
                if ((k = c["$!"]()) === a || k._isBoolean && !0 != k)if ((k = c["$is_a?"](null == (m = d.Object._scope.String) ? d.cm("String") : m)) === a || k._isBoolean && !0 != k) {
                    this.file = c.$file();
                    this.dir = c.$dir();
                    this.path = !1 !== (k = c.$path()) && k !== a ? k : "<stdin>";
                    if ((k = this.file) !== a && (!k._isBoolean || !0 == k)) {
                        if ((k = this.dir) === a || k._isBoolean && !0 != k)this.dir = (null == (k = d.Object._scope.File) ? d.cm("File") : k).$dirname(this.file), this.dir["$=="](".") && (this.dir = a);
                        if ((k = c.$path()) === a || k._isBoolean && !0 != k)this.path = (null == (k =
                            d.Object._scope.File) ? d.cm("File") : k).$basename(this.file)
                    }
                    this.lineno = !1 !== (k = c.$lineno()) && k !== a ? k : 1
                } else this.file = c, this.dir = (null == (k = d.Object._scope.File) ? d.cm("File") : k).$dirname(this.file), this.path = (null == (k = d.Object._scope.File) ? d.cm("File") : k).$basename(this.file), this.lineno = 1; else this.file = this.dir = a, this.path = "<stdin>", this.lineno = 1;
                this.lines = !1 !== b && b !== a ? this.$prepare_lines(b, f) : [];
                this.source_lines = this.lines.$dup();
                this.eof = this.lines["$empty?"]();
                this.look_ahead = 0;
                this.process_lines = !0;
                return this.unescape_next_line = !1
            };
            c.$prepare_lines = function (b, c) {
                var l, k;
                null == c && (c = v([], {}));
                return(l = b["$is_a?"](null == (k = d.Object._scope.String) ? d.cm("String") : k)) === a || l._isBoolean && !0 != l ? (l = c["$[]"]("normalize")) === a || l._isBoolean && !0 != l ? b.$dup() : f.Helpers.$normalize_lines_array(b) : (l = c["$[]"]("normalize")) === a || l._isBoolean && !0 != l ? b.$split(f.EOL) : f.Helpers.$normalize_lines_from_string(b)
            };
            c.$process_line = function (b) {
                var c;
                (c = this.process_lines) === a || c._isBoolean && !0 != c || (this.look_ahead =
                    this.look_ahead["$+"](1));
                return b
            };
            c["$has_more_lines?"] = function () {
                var b;
                return(!1 !== (b = this.eof) && b !== a ? b : this.eof = this.$peek_line()["$nil?"]())["$!"]()
            };
            c["$next_line_empty?"] = function () {
                return this.$peek_line()["$nil_or_empty?"]()
            };
            c.$peek_line = function (b) {
                var c, d, f = a;
                null == b && (b = !1);
                if ((c = !1 !== (d = b) && d !== a ? d : this.look_ahead["$>"](0)) === a || c._isBoolean && !0 != c) {
                    if ((c = !1 !== (d = this.eof) && d !== a ? d : this.lines["$empty?"]()) === a || c._isBoolean && !0 != c)return(c = (f = this.$process_line(this.lines["$[]"](0)))["$!"]()) ===
                        a || c._isBoolean && !0 != c ? f : this.$peek_line();
                    this.eof = !0;
                    this.look_ahead = 0;
                    return a
                }
                return(c = this.unescape_next_line) === a || c._isBoolean && !0 != c ? this.lines["$[]"](0) : this.lines["$[]"](0)["$[]"](x(1, -1, !1))
            };
            c.$peek_lines = function (b, c) {
                var d, f, m, p, n, q = a, r = a;
                null == b && (b = 1);
                null == c && (c = !0);
                q = this.look_ahead;
                r = [];
                (d = (f = b).$times, d._p = (m = function () {
                    var b, d = a;
                    return(b = d = (m._s || this).$read_line(c)) === a || b._isBoolean && !0 != b ? (u.$v = a, u) : r["$<<"](d)
                }, m._s = this, m), d).call(f);
                if ((d = r["$empty?"]()) === a || d._isBoolean &&
                    !0 != d)(d = (p = r).$reverse_each, d._p = (n = function (b) {
                    var c = n._s || this;
                    null == b && (b = a);
                    return c.$unshift(b)
                }, n._s = this, n), d).call(p), !1 !== c && c !== a && (this.look_ahead = q);
                return r
            };
            c.$read_line = function (b) {
                var c, d, f;
                null == b && (b = !1);
                return(c = !1 !== (d = !1 !== (f = b) && f !== a ? f : this.look_ahead["$>"](0)) && d !== a ? d : this["$has_more_lines?"]()) === a || c._isBoolean && !0 != c ? a : this.$shift()
            };
            c.$read_lines = function () {
                for (var b, c = a, c = []; (b = this["$has_more_lines?"]()) !== a && (!b._isBoolean || !0 == b);)c["$<<"](this.$shift());
                return c
            };
            d.defn(b, "$readlines", c.$read_lines);
            c.$read = function () {
                return this.$read_lines()["$*"](f.EOL)
            };
            c.$advance = function (a) {
                null == a && (a = !0);
                return this.$read_line(a)["$!"]()["$!"]()
            };
            c.$unshift_line = function (b) {
                this.$unshift(b);
                return a
            };
            d.defn(b, "$restore_line", c.$unshift_line);
            c.$unshift_lines = function (b) {
                var c, d;
                (c = b.$reverse_each, c._p = (d = function (b) {
                    var c = d._s || this;
                    null == b && (b = a);
                    return c.$unshift(b)
                }, d._s = this, d), c).call(b);
                return a
            };
            d.defn(b, "$restore_lines", c.$unshift_lines);
            c.$replace_line = function (b) {
                this.$advance();
                this.$unshift(b);
                return a
            };
            c.$skip_blank_lines = function () {
                var b, c, d = a, f = a;
                if ((b = this["$eof?"]()) !== a && (!b._isBoolean || !0 == b))return 0;
                for (d = 0; !((c = f = this.$peek_line()) === a || c._isBoolean && !0 != c || (c = f["$empty?"]()) === a || c._isBoolean && !0 != c);)this.$advance(), d = d["$+"](1);
                return d
            };
            c.$skip_comment_lines = function (b) {
                var c, d, k, m, p = a, n = a, q = a, r = a, t = a;
                null == b && (b = v([], {}));
                if ((c = this["$eof?"]()) !== a && (!c._isBoolean || !0 == c))return[];
                p = [];
                for (n = b["$[]"]("include_blank_lines"); (d = q = this.$peek_line()) !== a && (!d._isBoolean ||
                    !0 == d);)if ((d = (k = !1 !== n && n !== a) ? q["$empty?"]() : k) === a || d._isBoolean && !0 != d)if ((d = (k = r = q["$start_with?"]("//"), !1 !== k && k !== a ? t = f.CommentBlockRx.$match(q) : k)) === a || d._isBoolean && !0 != d)if ((k = (m = !1 !== r && r !== a) ? f.CommentLineRx["$=~"](q) : m) === a || k._isBoolean && !0 != k)break; else p["$<<"](this.$shift()); else p["$<<"](this.$shift()), (d = p).$push.apply(d, [].concat(this.$read_lines_until(v(["terminator", "read_last_line", "skip_processing"], {terminator: t["$[]"](0), read_last_line: !0, skip_processing: !0})))); else p["$<<"](this.$shift());
                return p
            };
            c.$skip_line_comments = function () {
                var b, c, d = a, k = a;
                if ((b = this["$eof?"]()) !== a && (!b._isBoolean || !0 == b))return[];
                for (d = []; !((c = k = this.$peek_line()) === a || c._isBoolean && !0 != c || (c = f.CommentLineRx["$=~"](k)) === a || c._isBoolean && !0 != c);)d["$<<"](this.$shift());
                return d
            };
            c.$terminate = function () {
                this.lineno = this.lineno["$+"](this.lines.$size());
                this.lines.$clear();
                this.eof = !0;
                this.look_ahead = 0;
                return a
            };
            c["$eof?"] = function () {
                return this["$has_more_lines?"]()["$!"]()
            };
            d.defn(b, "$empty?", c["$eof?"]);
            c.$read_lines_until = p = function (b) {
                var c, l, k, m, n, q = p._p || a, r = a, t = a, w = a, y = a, x = a, A = a, D = a, B = a, C = a, T = a;
                null == b && (b = v([], {}));
                p._p = null;
                r = [];
                (c = b["$[]"]("skip_first_line")) === a || c._isBoolean && !0 != c || this.$advance();
                (c = (l = this.process_lines, !1 !== l && l !== a ? b["$[]"]("skip_processing") : l)) === a || c._isBoolean && !0 != c ? t = !1 : (this.process_lines = !1, t = !0);
                (c = w = b["$[]"]("terminator")) === a || c._isBoolean && !0 != c ? (y = b["$[]"]("break_on_blank_lines"), x = b["$[]"]("break_on_list_continuation")) : x = y = !1;
                A = b["$[]"]("skip_line_comments");
                for (C = B = D = !1; (l = (k = C["$!"](), !1 !== k && k !== a ? T = this.$read_line() : k)) !== a && (!l._isBoolean || !0 == l);)if (C = function () {
                    for (; (k = !0) !== a && (!k._isBoolean || !0 == k);)return((k = (m = !1 !== w && w !== a) ? T["$=="](w) : m) === a || k._isBoolean && !0 != k) && ((k = (m = !1 !== y && y !== a) ? T["$empty?"]() : m) === a || k._isBoolean && !0 != k) ? (k = (m = (n = !1 !== x && x !== a) ? D : n, !1 !== m && m !== a ? T["$=="](f.LIST_CONTINUATION) : m)) === a || k._isBoolean && !0 != k ? (k = (m = q !== a) ? (n = d.$yield1(q, T)) === u ? u.$v : n : m) === a || k._isBoolean && !0 != k ? !1 : !0 : (b["$[]="]("preserve_last_line", !0),
                        !0) : !0;
                    return a
                }(), !1 !== C && C !== a)(l = b["$[]"]("read_last_line")) === a || l._isBoolean && !0 != l || (r["$<<"](T), D = !0), (l = b["$[]"]("preserve_last_line")) === a || l._isBoolean && !0 != l || (this.$restore_line(T), B = !0); else if ((l = (k = (m = !1 !== A && A !== a) ? T["$start_with?"]("//") : m, !1 !== k && k !== a ? f.CommentLineRx["$=~"](T) : k)) === a || l._isBoolean && !0 != l)r["$<<"](T), D = !0;
                !1 !== t && t !== a && (this.process_lines = !0, (c = (l = !1 !== B && B !== a) ? w["$!"]() : l) === a || c._isBoolean && !0 != c || (this.look_ahead = this.look_ahead["$-"](1)));
                return r
            };
            c.$shift =
                function () {
                    this.lineno = this.lineno["$+"](1);
                    this.look_ahead["$=="](0) || (this.look_ahead = this.look_ahead["$-"](1));
                    return this.lines.$shift()
                };
            c.$unshift = function (a) {
                this.lineno = this.lineno["$-"](1);
                this.look_ahead = this.look_ahead["$+"](1);
                this.eof = !1;
                return this.lines.$unshift(a)
            };
            c.$cursor = function () {
                return f.Cursor.$new(this.file, this.dir, this.path, this.lineno)
            };
            c.$line_info = function () {
                return"" + this.path + ": line " + this.lineno
            };
            d.defn(b, "$next_line_info", c.$line_info);
            c.$prev_line_info = function () {
                return"" +
                    this.path + ": line " + this.lineno["$-"](1)
            };
            c.$lines = function () {
                return this.lines.$dup()
            };
            c.$string = function () {
                return this.lines["$*"](f.EOL)
            };
            c.$source = function () {
                return this.source_lines["$*"](f.EOL)
            };
            return(c.$to_s = function () {
                return this.$line_info()
            }, a) && "to_s"
        })(A, null);
        (function (m, $super) {
            function n() {
            }

            var b = n = r(m, $super, "PreprocessorReader", n), c = b._proto, f = b._scope, p, e, g, l;
            c.document = c.lineno = c.process_lines = c.look_ahead = c.skipping = c.include_stack = c.conditional_stack = c.path = c.include_processor_extensions =
                c.maxdepth = c.dir = c.lines = c.file = c.includes = c.unescape_next_line = a;
            b.$attr_reader("include_stack");
            b.$attr_reader("includes");
            c.$initialize = p = function (b, c, e) {
                var f, g, l = a;
                null == c && (c = a);
                null == e && (e = a);
                p._p = null;
                this.document = b;
                d.find_super_dispatcher(this, "initialize", p, null).apply(this, [c, e, v(["normalize"], {normalize: !0})]);
                l = b.$attributes().$fetch("max-include-depth", 64).$to_i();
                l["$<"](0) && (l = 0);
                this.maxdepth = v(["abs", "rel"], {abs: l, rel: l});
                this.include_stack = [];
                this.includes = (f = b.$references(), !1 !==
                    (g = f["$[]"]("includes")) && g !== a ? g : f["$[]="]("includes", []));
                this.skipping = !1;
                this.conditional_stack = [];
                return this.include_processor_extensions = a
            };
            c.$prepare_lines = e = function (b, c) {
                var g = t.call(arguments, 0), l, m, p, n = e._p, q = a, r = a, u = a, w = a, x = a;
                null == c && (c = v([], {}));
                e._p = null;
                q = d.find_super_dispatcher(this, "prepare_lines", e, n).apply(this, g);
                if (!((l = (m = this.document, !1 !== m && m !== a ? this.document.$attributes()["$has_key?"]("skip-front-matter") : m)) === a || l._isBoolean && !0 != l || (l = r = this["$skip_front_matter!"](q)) ===
                    a || l._isBoolean && !0 != l))this.document.$attributes()["$[]="]("front-matter", r["$*"](f.EOL));
                if ((l = c.$fetch("condense", !0)) !== a && (!l._isBoolean || !0 == l)) {
                    for (; (m = (p = u = q["$[]"](0), !1 !== p && p !== a ? u["$empty?"]() : p)) !== a && (!m._isBoolean || !0 == m);)m = q.$shift(), !1 !== m && m !== a ? this.lineno = this.lineno["$+"](1) : m;
                    for (; (m = (p = w = q["$[]"](-1), !1 !== p && p !== a ? w["$empty?"]() : p)) !== a && (!m._isBoolean || !0 == m);)q.$pop()
                }
                if ((l = x = c.$fetch("indent", a)) !== a && (!l._isBoolean || !0 == l))f.Parser["$reset_block_indent!"](q, x.$to_i());
                return q
            };
            c.$process_line = function (b) {
                var c, d, e, g, l = a, m = a;
                if ((c = this.process_lines) === a || c._isBoolean && !0 != c)return b;
                if ((c = b["$empty?"]()) !== a && (!c._isBoolean || !0 == c))return this.look_ahead = this.look_ahead["$+"](1), "";
                if ((c = (d = (e = b["$end_with?"]("]"), !1 !== e && e !== a ? b["$start_with?"]("[")["$!"]() : e), !1 !== d && d !== a ? b["$include?"]("::") : d)) === a || c._isBoolean && !0 != c) {
                    if ((c = this.skipping) === a || c._isBoolean && !0 != c)return this.look_ahead = this.look_ahead["$+"](1), b;
                    this.$advance();
                    return a
                }
                if ((c = (d = b["$include?"]("if"),
                    !1 !== d && d !== a ? l = f.ConditionalDirectiveRx.$match(b) : d)) === a || c._isBoolean && !0 != c) {
                    if ((c = this.skipping) === a || c._isBoolean && !0 != c)return(c = (e = !1 !== (g = m = b["$start_with?"]("\\include::")) && g !== a ? g : b["$start_with?"]("include::"), !1 !== e && e !== a ? l = f.IncludeDirectiveRx.$match(b) : e)) === a || c._isBoolean && !0 != c ? (this.look_ahead = this.look_ahead["$+"](1), b) : !1 !== m && m !== a ? (this.unescape_next_line = !0, this.look_ahead = this.look_ahead["$+"](1), b["$[]"](x(1, -1, !1))) : (c = this.$preprocess_include(l["$[]"](1), l["$[]"](2).$strip())) ===
                        a || c._isBoolean && !0 != c ? (this.look_ahead = this.look_ahead["$+"](1), b) : a;
                    this.$advance();
                    return a
                }
                if ((c = b["$start_with?"]("\\")) === a || c._isBoolean && !0 != c) {
                    if ((c = (d = this).$preprocess_conditional_inclusion.apply(d, [].concat(l.$captures()))) === a || c._isBoolean && !0 != c)return this.look_ahead = this.look_ahead["$+"](1), b;
                    this.$advance();
                    return a
                }
                this.unescape_next_line = !0;
                this.look_ahead = this.look_ahead["$+"](1);
                return b["$[]"](x(1, -1, !1))
            };
            c.$peek_line = g = function (b) {
                var c = t.call(arguments, 0), e, f = g._p, l = a;
                null ==
                    b && (b = !1);
                g._p = null;
                return(e = l = d.find_super_dispatcher(this, "peek_line", g, f).apply(this, c)) === a || e._isBoolean && !0 != e ? (e = this.include_stack["$empty?"]()) === a || e._isBoolean && !0 != e ? (this.$pop_include(), this.$peek_line(b)) : a : l
            };
            c.$preprocess_conditional_inclusion = function (b, c, d, e) {
                var g, l, m, p, n, q, r, t, u, w, x = a, A = x = a, H = a, L = H = A = x = a, A = a;
                if ((g = !1 !== (l = (m = !1 !== (p = b["$=="]("ifdef")) && p !== a ? p : b["$=="]("ifndef"), !1 !== m && m !== a ? c["$empty?"]() : m)) && l !== a ? l : (m = b["$=="]("endif")) ? e : m) !== a && (!g._isBoolean || !0 == g))return!1;
                c = c.$downcase();
                if (b["$=="]("endif"))return x = this.conditional_stack.$size(), x["$>"](0) ? (x = this.conditional_stack["$[]"](-1), (g = !1 !== (l = c["$empty?"]()) && l !== a ? l : c["$=="](x["$[]"]("target"))) === a || g._isBoolean && !0 != g ? this.$warn("asciidoctor: ERROR: " + this.$line_info() + ": mismatched macro: endif::" + c + "[], expected endif::" + x["$[]"]("target") + "[]") : (this.conditional_stack.$pop(), this.skipping = (g = this.conditional_stack["$empty?"]()) === a || g._isBoolean && !0 != g ? this.conditional_stack["$[]"](-1)["$[]"]("skipping") :
                    !1)) : this.$warn("asciidoctor: ERROR: " + this.$line_info() + ": unmatched macro: endif::" + c + "[]"), !0;
                A = !1;
                if ((g = this.skipping) === a || g._isBoolean && !0 != g)if (H = b, "ifdef"["$==="](H))H = d, a["$==="](H) ? A = this.document.$attributes()["$has_key?"](c)["$!"]() : ","["$==="](H) ? A = (g = (l = c.$split(",")).$detect, g._p = (n = function (b) {
                    var c = n._s || this;
                    null == c.document && (c.document = a);
                    null == b && (b = a);
                    return c.document.$attributes()["$has_key?"](b)
                }, n._s = this, n), g).call(l)["$!"]() : "+"["$==="](H) && (A = (g = (m = c.$split("+")).$detect,
                    g._p = (q = function (b) {
                        var c = q._s || this;
                        null == c.document && (c.document = a);
                        null == b && (b = a);
                        return c.document.$attributes()["$has_key?"](b)["$!"]()
                    }, q._s = this, q), g).call(m)); else if ("ifndef"["$==="](H))H = d, a["$==="](H) ? A = this.document.$attributes()["$has_key?"](c) : ","["$==="](H) ? A = (g = (p = c.$split(",")).$detect, g._p = (r = function (b) {
                    var c = r._s || this;
                    null == c.document && (c.document = a);
                    null == b && (b = a);
                    return c.document.$attributes()["$has_key?"](b)["$!"]()
                }, r._s = this, r), g).call(p)["$!"]() : "+"["$==="](H) && (A = (g = (t =
                    c.$split("+")).$detect, g._p = (u = function (b) {
                    var c = u._s || this;
                    null == c.document && (c.document = a);
                    null == b && (b = a);
                    return c.document.$attributes()["$has_key?"](b)
                }, u._s = this, u), g).call(t)); else if ("ifeval"["$==="](H)) {
                    if ((g = !1 !== (w = c["$empty?"]()["$!"]()) && w !== a ? w : (x = f.EvalExpressionRx.$match(e.$strip()))["$!"]()) !== a && (!g._isBoolean || !0 == g))return!1;
                    A = this.$resolve_expr_val(x["$[]"](1));
                    H = x["$[]"](2);
                    L = this.$resolve_expr_val(x["$[]"](3));
                    A = A.$send(H.$to_sym(), L)["$!"]()
                }
                if ((g = !1 !== (w = b["$=="]("ifeval")) &&
                    w !== a ? w : e["$!"]()) !== a && (!g._isBoolean || !0 == g))!1 !== A && A !== a && (this.skipping = !0), this.conditional_stack["$<<"](v(["target", "skip", "skipping"], {target: c, skip: A, skipping: this.skipping})); else if ((g = !1 !== (w = this.skipping) && w !== a ? w : A) === a || g._isBoolean && !0 != g)A = this.$peek_line(!0), this.$replace_line(e.$rstrip()), this.$unshift(A);
                return!0
            };
            c.$preprocess_include = function (b, c) {
                var e, g, l, m, p, n, q, r, t, w = a, x = a, A = a, J = a, F = a, H = a, L = a, U = a, S = a, $ = a, Q = a, V = a, aa = a, ea = a, fa = a;
                if ((e = (w = this.document.$sub_attributes(b, v(["attribute_missing"],
                    {attribute_missing: "drop-line"})))["$empty?"]()) === a || e._isBoolean && !0 != e) {
                    if ((e = (g = this["$include_processors?"](), !1 !== g && g !== a ? x = (l = (m = this.include_processor_extensions).$find, l._p = (p = function (b) {
                        null == b && (b = a);
                        return b.$instance()["$handles?"](w)
                    }, p._s = this, p), l).call(m) : g)) === a || e._isBoolean && !0 != e) {
                        if (this.document.$safe()["$>="](f.SafeMode._scope.SECURE))return this.$replace_line("link:" + w + "[]"), !0;
                        if ((e = (g = (A = this.maxdepth["$[]"]("abs"))["$>"](0)) ? this.include_stack.$size()["$>="](A) : g) ===
                            a || e._isBoolean && !0 != e) {
                            if (A["$>"](0)) {
                                if ((e = null == (g = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : g) === a || e._isBoolean && !0 != e)if ((e = (g = w["$include?"](":"), !1 !== g && g !== a ? f.UriSniffRx["$=~"](w) : g)) === a || e._isBoolean && !0 != e) {
                                    J = "file";
                                    F = this.document.$normalize_system_path(w, this.dir, a, v(["target_name"], {target_name: "include file"}));
                                    if ((e = (null == (g = d.Object._scope.File) ? d.cm("File") : g)["$file?"](F)) === a || e._isBoolean && !0 != e)return this.$warn("asciidoctor: WARNING: " + this.$line_info() +
                                        ": include file not found: " + F), this.$replace_line("Unresolved directive in " + this.path + " - include::" + w + "[" + c + "]"), !0;
                                    H = f.PathResolver.$new().$relative_path(F, this.document.$base_dir())
                                } else {
                                    if ((e = this.document.$attributes()["$has_key?"]("allow-uri-read")) === a || e._isBoolean && !0 != e)return this.$replace_line("link:" + w + "[]"), !0;
                                    J = "uri";
                                    F = H = w;
                                    (e = this.document.$attributes()["$has_key?"]("cache-uri")) === a || e._isBoolean && !0 != e ? (e = (null == (g = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : g)["$!"]()) ===
                                        a || e._isBoolean && !0 != e || (null == (e = d.Object._scope.OpenURI) ? d.cm("OpenURI") : e) : f.Helpers.$require_library("open-uri/cached", "open-uri-cached")
                                } else J = "file", F = H = (e = this.include_stack["$empty?"]()) === a || e._isBoolean && !0 != e ? (null == (e = d.Object._scope.File) ? d.cm("File") : e).$join(this.dir, w) : (null == (e = d.Object._scope.Dir) ? d.cm("Dir") : e).$pwd()["$=="](this.document.$base_dir()) ? w : (null == (e = d.Object._scope.File) ? d.cm("File") : e).$join(this.dir, w);
                                U = L = a;
                                S = v([], {});
                                (e = c["$empty?"]()["$!"]()) === a || e._isBoolean &&
                                    !0 != e || (S = f.AttributeList.$new(c).$parse(), (e = S["$has_key?"]("lines")) === a || e._isBoolean && !0 != e ? (e = S["$has_key?"]("tag")) === a || e._isBoolean && !0 != e ? (e = S["$has_key?"]("tags")) === a || e._isBoolean && !0 != e || (U = S["$[]"]("tags").$split(f.DataDelimiterRx).$uniq().$to_set()) : U = [S["$[]"]("tag")].$to_set() : (L = [], (e = (g = S["$[]"]("lines").$split(f.DataDelimiterRx)).$each, e._p = (n = function (b) {
                                    var c, e, f, g = a, k = a;
                                    null == b && (b = a);
                                    if ((c = b["$include?"]("..")) === a || c._isBoolean && !0 != c)return L["$<<"](b.$to_i());
                                    c = d.to_ary((e =
                                        (f = b.$split("..")).$map, e._p = "to_i".$to_proc(), e).call(f));
                                    g = null == c[0] ? a : c[0];
                                    k = null == c[1] ? a : c[1];
                                    return k["$=="](-1) ? (L["$<<"](g), L["$<<"](1["$/"](0))) : L.$concat((null == (c = d.Object._scope.Range) ? d.cm("Range") : c).$new(g, k).$to_a())
                                }, n._s = this, n), e).call(g), L = L.$sort().$uniq()));
                                if ((e = L["$nil?"]()["$!"]()) !== a && (!e._isBoolean || !0 == e)) {
                                    if ((e = L["$empty?"]()["$!"]()) !== a && (!e._isBoolean || !0 == e)) {
                                        $ = [];
                                        V = Q = 0;
                                        try {
                                            (e = (l = this).$open, e._p = (q = function (b) {
                                                var c = q._s || this, e, f, g;
                                                null == b && (b = a);
                                                return(e = (f = b).$each_line,
                                                    e._p = (g = function (c) {
                                                        var e, f, g, k = a;
                                                        null == c && (c = a);
                                                        V = V["$+"](1);
                                                        k = L["$[]"](0);
                                                        if ((e = (f = k["$is_a?"](null == (g = d.Object._scope.Float) ? d.cm("Float") : g), !1 !== f && f !== a ? k["$infinite?"]() : f)) === a || e._isBoolean && !0 != e)return b.$lineno()["$=="](k) && ($.$push(c), Q["$=="](0) && (Q = V), L.$shift()), (e = L["$empty?"]()) === a || e._isBoolean && !0 != e ? a : (u.$v = a, u);
                                                        $.$push(c);
                                                        return Q["$=="](0) ? Q = V : a
                                                    }, g._s = c, g), e).call(f)
                                            }, q._s = this, q), e).call(l, F, "r")
                                        } catch (E) {
                                            return this.$warn("asciidoctor: WARNING: " + this.$line_info() + ": include " +
                                                J + " not readable: " + F), this.$replace_line("Unresolved directive in " + this.path + " - include::" + w + "[" + c + "]"), !0
                                        }
                                        this.$advance();
                                        this.$push_include($, F, H, Q, S)
                                    }
                                } else if ((e = U["$nil?"]()["$!"]()) === a || e._isBoolean && !0 != e)try {
                                    this.$advance(), this.$push_include((e = this.$open, e._p = (t = function (b) {
                                        null == b && (b = a);
                                        return b.$read()
                                    }, t._s = this, t), e).call(this, F, "r"), F, H, 1, S)
                                } catch (N) {
                                    this.$warn("asciidoctor: WARNING: " + this.$line_info() + ": include " + J + " not readable: " + F), this.$replace_line("Unresolved directive in " +
                                        this.path + " - include::" + w + "[" + c + "]")
                                } else if ((e = U["$empty?"]()["$!"]()) !== a && (!e._isBoolean || !0 == e)) {
                                    $ = [];
                                    V = Q = 0;
                                    aa = a;
                                    ea = (null == (e = d.Object._scope.Set) ? d.cm("Set") : e).$new();
                                    try {
                                        (e = this.$open, e._p = (r = function (b) {
                                            var c = r._s || this, e, g, k;
                                            null == b && (b = a);
                                            return(e = (g = b).$each_line, e._p = (k = function (b) {
                                                var c = k._s || this, e, g, l;
                                                null == b && (b = a);
                                                V = V["$+"](1);
                                                (e = f.FORCE_ENCODING) === a || e._isBoolean && !0 != e || b.$force_encoding((null == (e = d.Object._scope.Encoding) ? d.cm("Encoding") : e)._scope.UTF_8);
                                                return(e = aa["$nil?"]()["$!"]()) ===
                                                    a || e._isBoolean && !0 != e ? (e = (g = U).$each, e._p = (l = function (c) {
                                                    var d;
                                                    null == c && (c = a);
                                                    if ((d = b["$include?"]("tag::" + c + "[]")) === a || d._isBoolean && !0 != d)return a;
                                                    aa = c;
                                                    ea["$<<"](c);
                                                    return u.$v = a, u
                                                }, l._s = c, l), e).call(g) : (e = b["$include?"]("end::" + aa + "[]")) === a || e._isBoolean && !0 != e ? ($.$push(b), Q["$=="](0) ? Q = V : a) : aa = a
                                            }, k._s = c, k), e).call(g)
                                        }, r._s = this, r), e).call(this, F, "r")
                                    } catch (ka) {
                                        return this.$warn("asciidoctor: WARNING: " + this.$line_info() + ": include " + J + " not readable: " + F), this.$replace_line("Unresolved directive in " +
                                            this.path + " - include::" + w + "[" + c + "]"), !0
                                    }
                                    ((e = (fa = U.$to_a()["$-"](ea.$to_a()))["$empty?"]()) === a || e._isBoolean && !0 != e) && this.$warn("asciidoctor: WARNING: " + this.$line_info() + ": tag" + (fa.$size()["$>"](1) ? "s" : a) + " '" + fa["$*"](",") + "' not found in include " + J + ": " + F);
                                    this.$advance();
                                    this.$push_include($, F, H, Q, S)
                                }
                                return!0
                            }
                            return!1
                        }
                        this.$warn("asciidoctor: ERROR: " + this.$line_info() + ": maximum include depth of " + this.maxdepth["$[]"]("rel") + " exceeded");
                        return!1
                    }
                    this.$advance();
                    x.$process_method()["$[]"](this,
                        w, f.AttributeList.$new(c).$parse());
                    return!0
                }
                this.document.$attributes().$fetch("attribute-missing", f.Compliance.$attribute_missing())["$=="]("skip") ? this.$replace_line("Unresolved directive in " + this.path + " - include::" + b + "[" + c + "]") : this.$advance();
                return!0
            };
            c.$push_include = function (b, c, e, g, l) {
                var m, p = a;
                null == c && (c = a);
                null == e && (e = a);
                null == g && (g = 1);
                null == l && (l = v([], {}));
                this.include_stack["$<<"]([this.lines, this.file, this.dir, this.path, this.lineno, this.maxdepth, this.process_lines]);
                !1 !== c && c !== a ?
                    (this.file = c, this.dir = f.File.$dirname(c), this.process_lines = f.ASCIIDOC_EXTENSIONS["$[]"]((null == (m = d.Object._scope.File) ? d.cm("File") : m).$extname(c))) : (this.file = a, this.dir = ".", this.process_lines = !0);
                !1 !== e && e !== a ? (this.includes["$<<"](f.Helpers.$rootname(e)), c = e) : c = "<stdin>";
                this.path = c;
                this.lineno = g;
                (m = l["$has_key?"]("depth")) === a || m._isBoolean && !0 != m || (p = l["$[]"]("depth").$to_i(), p["$<="](0) && (p = 1), this.maxdepth = v(["abs", "rel"], {abs: this.include_stack.$size()["$-"](1)["$+"](p), rel: p}));
                this.lines =
                    this.$prepare_lines(b, v(["normalize", "condense", "indent"], {normalize: !0, condense: !1, indent: l["$[]"]("indent")}));
                (m = this.lines["$empty?"]()) === a || m._isBoolean && !0 != m ? (this.eof = !1, this.look_ahead = 0) : this.$pop_include();
                return a
            };
            c.$pop_include = function () {
                var b;
                this.include_stack.$size()["$>"](0) && (b = d.to_ary(this.include_stack.$pop()), this.lines = null == b[0] ? a : b[0], this.file = null == b[1] ? a : b[1], this.dir = null == b[2] ? a : b[2], this.path = null == b[3] ? a : b[3], this.lineno = null == b[4] ? a : b[4], this.maxdepth = null == b[5] ?
                    a : b[5], this.process_lines = null == b[6] ? a : b[6], this.eof = this.lines["$empty?"](), this.look_ahead = 0);
                return a
            };
            c.$include_depth = function () {
                return this.include_stack.$size()
            };
            c["$exceeded_max_depth?"] = function () {
                var b, c, d = a;
                return(b = (c = (d = this.maxdepth["$[]"]("abs"))["$>"](0)) ? this.include_stack.$size()["$>="](d) : c) === a || b._isBoolean && !0 != b ? !1 : this.maxdepth["$[]"]("rel")
            };
            c.$shift = l = function () {
                var b = t.call(arguments, 0), c, e = l._p;
                l._p = null;
                if ((c = this.unescape_next_line) === a || c._isBoolean && !0 != c)return d.find_super_dispatcher(this,
                    "shift", l, e).apply(this, b);
                this.unescape_next_line = !1;
                return d.find_super_dispatcher(this, "shift", l, e).apply(this, b)["$[]"](x(1, -1, !1))
            };
            c["$skip_front_matter!"] = function (b, c) {
                var d, e, f, g = a, l = a;
                null == c && (c = !0);
                g = a;
                if (b["$[]"](0)["$=="]("---")) {
                    l = b.$dup();
                    g = [];
                    b.$shift();
                    !1 !== c && c !== a && (this.lineno = this.lineno["$+"](1));
                    for (; (e = (f = b["$empty?"]()["$!"](), !1 !== f && f !== a ? b["$[]"](0)["$=="]("---")["$!"]() : f)) !== a && (!e._isBoolean || !0 == e);)g.$push(b.$shift()), !1 !== c && c !== a && (this.lineno = this.lineno["$+"](1));
                    (d = b["$empty?"]()) === a || d._isBoolean && !0 != d ? (b.$shift(), !1 !== c && c !== a && (this.lineno = this.lineno["$+"](1))) : ((d = b).$unshift.apply(d, [].concat(l)), !1 !== c && c !== a && (this.lineno = 0), g = a)
                }
                return g
            };
            c.$resolve_expr_val = function (b) {
                var c, d, e, f = a, g = a, f = b, g = a;
                (c = !1 !== (d = (e = f["$start_with?"]('"'), !1 !== e && e !== a ? f["$end_with?"]('"') : e)) && d !== a ? d : (e = f["$start_with?"]("'"), !1 !== e && e !== a ? f["$end_with?"]("'") : e)) === a || c._isBoolean && !0 != c || (g = "string", f = f["$[]"](x(1, -1, !0)));
                (c = f["$include?"]("{")) === a || c._isBoolean &&
                    !0 != c || (f = this.document.$sub_attributes(f));
                g["$=="]("string") || (f = (c = f["$empty?"]()) === a || c._isBoolean && !0 != c ? (c = f.$strip()["$empty?"]()) === a || c._isBoolean && !0 != c ? f["$=="]("true") ? !0 : f["$=="]("false") ? !1 : (c = f["$include?"](".")) === a || c._isBoolean && !0 != c ? f.$to_i() : f.$to_f() : " " : a);
                return f
            };
            c["$include_processors?"] = function () {
                var b, c;
                if ((b = this.include_processor_extensions["$!"]()) === a || b._isBoolean && !0 != b)return this.include_processor_extensions["$=="](!1)["$!"]();
                if ((b = (c = this.document["$extensions?"](),
                    !1 !== c && c !== a ? this.document.$extensions()["$include_processors?"]() : c)) === a || b._isBoolean && !0 != b)return this.include_processor_extensions = !1;
                this.include_processor_extensions = this.document.$extensions().$include_processors();
                return!0
            };
            return(c.$to_s = function () {
                var b, c, d;
                return"#<" + this.$class() + "@" + this.$object_id() + " {path: " + this.path.$inspect() + ", line #: " + this.lineno + ", include depth: " + this.include_stack.$size() + ", include stack: [" + (b = (c = this.include_stack).$map, b._p = (d = function (b) {
                    null == b &&
                    (b = a);
                    return b.$to_s()
                }, d._s = this, d), b).call(c).$join(", ") + "]}>"
            }, a) && "to_s"
        })(A, m.Reader)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.klass, r = d.range;
    return function (v) {
        v = t(v, "Asciidoctor");
        (function (t, $super) {
            function m() {
            }

            var q = m = w(t, $super, "Section", m), h = q._proto, n = q._scope, b, c, f;
            h.level = h.document = h.parent = h.number = h.title = h.numbered = h.blocks = a;
            q.$attr_accessor("index");
            q.$attr_accessor("number");
            q.$attr_accessor("sectname");
            q.$attr_accessor("special");
            q.$attr_accessor("numbered");
            h.$initialize = b = function (c, e, f) {
                var l, k;
                null == c && (c = a);
                null == e && (e = a);
                null == f && (f = !0);
                b._p = null;
                d.find_super_dispatcher(this, "initialize", b, null).apply(this, [c, "section"]);
                (l = e["$nil?"]()) === a || l._isBoolean && !0 != l ? this.level = e : !1 !== c && c !== a ? this.level = c.$level()["$+"](1) : (l = this.level["$nil?"]()) === a || l._isBoolean && !0 != l || (this.level = 1);
                this.numbered = (l = !1 !== f && f !== a) ? this.level["$>"](0) : l;
                this.special = (l = (k = !1 !== c && c !== a) ? c.$context()["$=="]("section") : k, !1 !== l && l !== a ? c.$special() : l);
                this.index = 0;
                return this.number = 1
            };
            d.defn(q, "$name", h.$title);
            h.$generate_id = function () {
                var b, c, d = a, f = a, k = a,
                    h = f = a;
                if ((b = this.document.$attributes()["$has_key?"]("sectids")) === a || b._isBoolean && !0 != b)return a;
                d = !1 !== (b = this.document.$attributes()["$[]"]("idseparator")) && b !== a ? b : "_";
                f = !1 !== (b = this.document.$attributes()["$[]"]("idprefix")) && b !== a ? b : "_";
                k = "" + f + this.$title().$downcase().$gsub(n.InvalidSectionIdCharsRx, d).$tr_s(d, d).$chomp(d);
                if ((b = (c = f["$empty?"](), !1 !== c && c !== a ? k["$start_with?"](d) : c)) !== a && (!b._isBoolean || !0 == b))for (k = k["$[]"](r(1, -1, !1)); (c = k["$start_with?"](d)) !== a && (!c._isBoolean || !0 ==
                    c);)k = k["$[]"](r(1, -1, !1));
                f = k;
                for (h = 2; (c = this.document.$references()["$[]"]("ids")["$has_key?"](f)) !== a && (!c._isBoolean || !0 == c);)f = "" + k + d + h, h = h["$+"](1);
                return f
            };
            h.$sectnum = function (b, c) {
                var d, f, k, h;
                null == b && (b = ".");
                null == c && (c = a);
                !1 !== (d = c) && d !== a ? d : c = c["$=="](!1) ? "" : b;
                return(d = (f = (k = (h = this.level, !1 !== h && h !== a ? this.level["$>"](1) : h), !1 !== k && k !== a ? this.parent : k), !1 !== f && f !== a ? this.parent.$context()["$=="]("section") : f)) === a || d._isBoolean && !0 != d ? "" + this.number + c : "" + this.parent.$sectnum(b) + this.number +
                    c
            };
            h["$<<"] = c = function (b) {
                var e = u.call(arguments, 0), f = c._p;
                c._p = null;
                d.find_super_dispatcher(this, "<<", c, f).apply(this, e);
                return b.$context()["$=="]("section") ? this.$assign_index(b) : a
            };
            return(h.$to_s = f = function () {
                var b = u.call(arguments, 0), c, g = f._p, l = a;
                f._p = null;
                if ((c = this.title["$=="](a)["$!"]()) === a || c._isBoolean && !0 != c)return d.find_super_dispatcher(this, "to_s", f, g).apply(this, b);
                l = (c = this.numbered) === a || c._isBoolean && !0 != c ? this.title : "" + this.$sectnum() + " " + this.title;
                return"#<" + this.$class() + "@" +
                    this.$object_id() + " {level: " + this.level + ", title: " + l.$inspect() + ", blocks: " + this.blocks.$size() + "}>"
            }, a) && "to_s"
        })(v, v._scope.AbstractBlock)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.module, t = d.klass, w = d.hash2;
    return function (r) {
        (function (r, $super) {
            function u() {
            }

            var m = u = t(r, $super, "Stylesheets", u), q = m._proto, h = m._scope, n;
            q.primary_stylesheet_data = q.coderay_stylesheet_data = q.pygments_stylesheet_data = a;
            d.cdecl(h, "DEFAULT_STYLESHEET_NAME", "asciidoctor.css");
            d.cdecl(h, "DEFAULT_PYGMENTS_STYLE", "pastie");
            d.cdecl(h, "STYLESHEETS_DATA_PATH", (null == (n = d.Object._scope.File) ? d.cm("File") : n).$join(h.DATA_PATH, "stylesheets"));
            m.__instance__ = m.$new();
            d.defs(m, "$instance",
                function () {
                    null == this.__instance__ && (this.__instance__ = a);
                    return this.__instance__
                });
            q.$primary_stylesheet_name = function () {
                return h.DEFAULT_STYLESHEET_NAME
            };
            q.$primary_stylesheet_data = function () {
                var b, c;
                return!1 !== (b = this.primary_stylesheet_data) && b !== a ? b : this.primary_stylesheet_data = (null == (c = d.Object._scope.IO) ? d.cm("IO") : c).$read((null == (c = d.Object._scope.File) ? d.cm("File") : c).$join(h.STYLESHEETS_DATA_PATH, "asciidoctor-default.css")).$chomp()
            };
            q.$embed_primary_stylesheet = function () {
                return"<style>\n" +
                    this.$primary_stylesheet_data() + "\n</style>"
            };
            q.$write_primary_stylesheet = function (b) {
                var c, f, h, e;
                return(c = (f = null == (e = d.Object._scope.File) ? d.cm("File") : e).$open, c._p = (h = function (b) {
                    var c = h._s || this;
                    null == b && (b = a);
                    return b.$write(c.$primary_stylesheet_data())
                }, h._s = this, h), c).call(f, (null == (e = d.Object._scope.File) ? d.cm("File") : e).$join(b, this.$primary_stylesheet_name()), "w")
            };
            q.$coderay_stylesheet_name = function () {
                return"coderay-asciidoctor.css"
            };
            q.$coderay_stylesheet_data = function () {
                var b, c;
                return!1 !==
                    (b = this.coderay_stylesheet_data) && b !== a ? b : this.coderay_stylesheet_data = (null == (c = d.Object._scope.IO) ? d.cm("IO") : c).$read((null == (c = d.Object._scope.File) ? d.cm("File") : c).$join(h.STYLESHEETS_DATA_PATH, "coderay-asciidoctor.css")).$chomp()
            };
            q.$embed_coderay_stylesheet = function () {
                return"<style>\n" + this.$coderay_stylesheet_data() + "\n</style>"
            };
            q.$write_coderay_stylesheet = function (b) {
                var c, f, h, e;
                return(c = (f = null == (e = d.Object._scope.File) ? d.cm("File") : e).$open, c._p = (h = function (b) {
                    var c = h._s || this;
                    null == b &&
                    (b = a);
                    return b.$write(c.$coderay_stylesheet_data())
                }, h._s = this, h), c).call(f, (null == (e = d.Object._scope.File) ? d.cm("File") : e).$join(b, this.$coderay_stylesheet_name()), "w")
            };
            q.$pygments_stylesheet_name = function (b) {
                var c;
                null == b && (b = a);
                !1 !== (c = b) && c !== a ? c : b = h.DEFAULT_PYGMENTS_STYLE;
                return"pygments-" + b + ".css"
            };
            q.$pygments_stylesheet_data = function (b) {
                var c;
                null == b && (b = a);
                !1 !== (c = b) && c !== a && c;
                return!1 !== (c = this.pygments_stylesheet_data) && c !== a ? c : this.pygments_stylesheet_data = this.$load_pygments()
            };
            q.$embed_pygments_stylesheet =
                function (b) {
                    null == b && (b = a);
                    return"<style>\n" + this.$pygments_stylesheet_data(b) + "\n</style>"
                };
            q.$write_pygments_stylesheet = function (b, c) {
                var f, h, e, g;
                null == c && (c = a);
                return(f = (h = null == (g = d.Object._scope.File) ? d.cm("File") : g).$open, f._p = (e = function (b) {
                    var d = e._s || this;
                    null == b && (b = a);
                    return b.$write(d.$pygments_stylesheet_data(c))
                }, e._s = this, e), f).call(h, (null == (g = d.Object._scope.File) ? d.cm("File") : g).$join(b, this.$pygments_stylesheet_name(c)), "w")
            };
            return(q.$load_pygments = function () {
                var b;
                ((b = null == d.Object._scope.Pygments ?
                    a : "constant") === a || b._isBoolean && !0 != b) && h.Helpers.$require_library("pygments", "pygments.rb");
                return w([], {})
            }, a) && "load_pygments"
        })(u(r, "Asciidoctor"), null)
    }(d.top)
})(Opal);
(function (d) {
    var a = d.nil, u = d.slice, t = d.module, w = d.klass, r = d.hash2, v = d.range;
    return function (x) {
        x = t(x, "Asciidoctor");
        var A = x._scope;
        (function (m, $super) {
            function h() {
            }

            var n = h = w(m, $super, "Table", h), b = n._proto, c = n._scope, f;
            b.attributes = b.document = b.has_header_option = b.rows = a;
            (function (a, $super) {
                function b() {
                }

                var c = b = w(a, $super, "Rows", b), f = c._proto;
                c.$attr_accessor("head", "foot", "body");
                f.$initialize = function (a, b, c) {
                    null == a && (a = []);
                    null == b && (b = []);
                    null == c && (c = []);
                    this.head = a;
                    this.foot = b;
                    return this.body =
                        c
                };
                return d.defn(c, "$[]", f.$send)
            })(n, null);
            d.cdecl(c, "DEFAULT_DATA_FORMAT", "psv");
            d.cdecl(c, "DATA_FORMATS", ["psv", "dsv", "csv"]);
            d.cdecl(c, "DEFAULT_DELIMITERS", r(["psv", "dsv", "csv"], {psv: "|", dsv: ":", csv: ","}));
            d.cdecl(c, "TEXT_STYLES", r("dsemhlva".split(""), {d: "none", s: "strong", e: "emphasis", m: "monospaced", h: "header", l: "literal", v: "verse", a: "asciidoc"}));
            d.cdecl(c, "ALIGNMENTS", r(["h", "v"], {h: r(["<", ">", "^"], {"<": "left", ">": "right", "^": "center"}), v: r(["<", ">", "^"], {"<": "top", ">": "bottom", "^": "middle"})}));
            n.$attr_accessor("columns");
            n.$attr_accessor("rows");
            n.$attr_accessor("has_header_option");
            b.$initialize = f = function (b, e) {
                var g, h, k, m = a, n = a;
                f._p = null;
                d.find_super_dispatcher(this, "initialize", f, null).apply(this, [b, "table"]);
                this.rows = c.Rows.$new();
                this.columns = [];
                this.has_header_option = e["$has_key?"]("header-option");
                m = e["$[]"]("width");
                n = m.$to_i().$abs();
                (g = !1 !== (h = (k = n["$=="](0)) ? m["$=="]("0")["$!"]() : k) && h !== a ? h : n["$>"](100)) === a || g._isBoolean && !0 != g || (n = 100);
                this.attributes["$[]="]("tablepcwidth",
                    n);
                return(g = this.document.$attributes()["$has_key?"]("pagewidth")) === a || g._isBoolean && !0 != g ? a : (g = "tableabswidth", h = this.attributes, !1 !== (k = h["$[]"](g)) && k !== a ? k : h["$[]="](g, this.attributes["$[]"]("tablepcwidth").$to_f()["$/"](100)["$*"](this.document.$attributes()["$[]"]("pagewidth")).$round()))
            };
            b["$header_row?"] = function () {
                var b;
                return b = this.has_header_option, !1 !== b && b !== a ? this.rows.$body()["$empty?"]() : b
            };
            b.$create_columns = function (b) {
                var d, f, h, k, m = a, n = a, r = a, m = 0, n = [];
                (d = b.$each, d._p = (f = function (b) {
                    var d =
                        f._s || this;
                    null == b && (b = a);
                    m = m["$+"](b["$[]"]("width"));
                    return n["$<<"](c.Column.$new(d, n.$size(), b))
                }, f._s = this, f), d).call(b);
                if ((d = n["$empty?"]()) === a || d._isBoolean && !0 != d)this.attributes["$[]="]("colcount", n.$size()), r = 100["$/"](n.$size()).$floor(), (d = (h = n).$each, d._p = (k = function (b) {
                    null == b && (b = a);
                    return b.$assign_width(m, r)
                }, k._s = this, k), d).call(h);
                this.columns = n;
                return a
            };
            return(b.$partition_header_footer = function (b) {
                var c, d, f, h, m = a, n = a;
                this.attributes["$[]="]("rowcount", this.rows.$body().$size());
                m = this.rows.$body().$size();
                (c = (d = m["$>"](0)) ? this.has_header_option : d) === a || c._isBoolean && !0 != c || (n = this.rows.$body().$shift(), m = m["$-"](1), (c = (d = n).$each, c._p = (f = function (b) {
                    null == b && (b = a);
                    return b["$style="](a)
                }, f._s = this, f), c).call(d), this.rows["$head="]([n]));
                if ((c = (h = m["$>"](0)) ? b["$has_key?"]("footer-option") : h) !== a && (!c._isBoolean || !0 == c))this.rows["$foot="]([this.rows.$body().$pop()]);
                return a
            }, a) && "partition_header_footer"
        })(x, A.AbstractBlock);
        (function (m, $super) {
            function h() {
            }

            var n = h = w(m,
                $super, "Column", h), b = n._proto, c;
            b.attributes = a;
            n.$attr_accessor("style");
            b.$initialize = c = function (b, h, e) {
                var g;
                null == e && (e = r([], {}));
                c._p = null;
                d.find_super_dispatcher(this, "initialize", c, null).apply(this, [b, "column"]);
                this.style = e["$[]"]("style");
                e["$[]="]("colnumber", h["$+"](1));
                b = "width";
                h = e;
                !1 !== (g = h["$[]"](b)) && g !== a ? g : h["$[]="](b, 1);
                b = "halign";
                h = e;
                !1 !== (g = h["$[]"](b)) && g !== a ? g : h["$[]="](b, "left");
                b = "valign";
                h = e;
                !1 !== (g = h["$[]"](b)) && g !== a ? g : h["$[]="](b, "top");
                return this.$update_attributes(e)
            };
            d.defn(n, "$table", b.$parent);
            return(b.$assign_width = function (b, c) {
                var d, g = a, g = b["$>"](0) ? this.attributes["$[]"]("width").$to_f()["$/"](b)["$*"](100).$floor() : c;
                this.attributes["$[]="]("colpcwidth", g);
                if ((d = this.$parent().$attributes()["$has_key?"]("tableabswidth")) !== a && (!d._isBoolean || !0 == d))this.attributes["$[]="]("colabswidth", g.$to_f()["$/"](100)["$*"](this.$parent().$attributes()["$[]"]("tableabswidth")).$round());
                return a
            }, a) && "assign_width"
        })(A.Table, A.AbstractNode);
        (function (m, $super) {
            function h() {
            }

            var n = h = w(m, $super, "Cell", h), b = n._proto, c = n._scope, f, p;
            b.style = b.document = b.text = b.inner_document = b.colspan = b.rowspan = b.attributes = a;
            n.$attr_accessor("style");
            n.$attr_accessor("colspan");
            n.$attr_accessor("rowspan");
            d.defn(n, "$column", b.$parent);
            n.$attr_reader("inner_document");
            b.$initialize = f = function (b, g, h, k) {
                var m, p, n = a, t = a, u = a, v = a;
                null == h && (h = r([], {}));
                null == k && (k = a);
                f._p = null;
                d.find_super_dispatcher(this, "initialize", f, null).apply(this, [b, "cell"]);
                this.text = g;
                this.rowspan = this.colspan = this.style =
                    a;
                !1 !== b && b !== a && (this.style = b.$attributes()["$[]"]("style"), this.$update_attributes(b.$attributes()));
                !1 !== h && h !== a && (this.colspan = h.$delete("colspan"), this.rowspan = h.$delete("rowspan"), (m = h["$has_key?"]("style")) === a || m._isBoolean && !0 != m || (this.style = h["$[]"]("style")), this.$update_attributes(h));
                if ((m = (p = this.style["$=="]("asciidoc")) ? b.$table()["$header_row?"]()["$!"]() : p) === a || m._isBoolean && !0 != m)return a;
                n = this.document.$attributes().$delete("doctitle");
                t = this.text.$split(c.EOL);
                if ((m = !1 !==
                    (p = t["$empty?"]()) && p !== a ? p : t["$[]"](0)["$include?"]("::")["$!"]()) === a || m._isBoolean && !0 != m)u = t["$[]"](0), v = c.PreprocessorReader.$new(this.document, u).$readlines(), (m = v["$=="](u)["$!"]()) === a || m._isBoolean && !0 != m || (t.$shift(), (m = t).$unshift.apply(m, [].concat(v)));
                this.inner_document = c.Document.$new(t, r(["header_footer", "parent", "cursor"], {header_footer: !1, parent: this.document, cursor: k}));
                return(p = n["$nil?"]()) === a || p._isBoolean && !0 != p ? this.document.$attributes()["$[]="]("doctitle", n) : a
            };
            b.$text =
                function () {
                    return this.$apply_normal_subs(this.text).$strip()
                };
            b.$content = function () {
                var b, d, f;
                return this.style["$=="]("asciidoc") ? this.inner_document.$convert() : (b = (d = this.$text().$split(c.BlankLineRx)).$map, b._p = (f = function (b) {
                    var d = f._s || this, e, g;
                    null == d.style && (d.style = a);
                    null == b && (b = a);
                    return(e = !1 !== (g = d.style["$!"]()) && g !== a ? g : d.style["$=="]("header")) === a || e._isBoolean && !0 != e ? c.Inline.$new(d.$parent(), "quoted", b, r(["type"], {type: d.style})).$convert() : b
                }, f._s = this, f), b).call(d)
            };
            return(b.$to_s =
                p = function () {
                    var b = u.call(arguments, 0), c, f = p._p;
                    p._p = null;
                    return"" + d.find_super_dispatcher(this, "to_s", p, f).apply(this, b).$to_s() + " - [text: " + this.text + ", colspan: " + (!1 !== (c = this.colspan) && c !== a ? c : 1) + ", rowspan: " + (!1 !== (c = this.rowspan) && c !== a ? c : 1) + ", attributes: " + this.attributes + "]"
                }, a) && "to_s"
        })(A.Table, A.AbstractNode);
        (function (d, $super) {
            function h() {
            }

            var n = h = w(d, $super, "ParserContext", h), b = n._proto, c = n._scope;
            b.format = b.delimiter = b.delimiter_re = b.buffer = b.cell_specs = b.cell_open = b.last_cursor =
                b.table = b.current_row = b.col_count = b.col_visits = b.active_rowspans = b.linenum = a;
            n.$attr_accessor("table");
            n.$attr_accessor("format");
            n.$attr_reader("col_count");
            n.$attr_accessor("buffer");
            n.$attr_reader("delimiter");
            n.$attr_reader("delimiter_re");
            b.$initialize = function (b, d, e) {
                var g, h, k;
                null == e && (e = r([], {}));
                this.reader = b;
                this.table = d;
                this.last_cursor = b.$cursor();
                (g = this.format = e["$[]"]("format")) === a || g._isBoolean && !0 != g ? this.format = c.Table._scope.DEFAULT_DATA_FORMAT : ((g = c.Table._scope.DATA_FORMATS["$include?"](this.format)) ===
                    a || g._isBoolean && !0 != g) && this.$raise("Illegal table format: " + this.format);
                (g = (h = (k = this.format["$=="]("psv")) ? e["$has_key?"]("separator")["$!"]() : k, !1 !== h && h !== a ? d.$document()["$nested?"]() : h)) === a || g._isBoolean && !0 != g ? this.delimiter = !1 !== (g = e["$[]"]("separator")) && g !== a ? g : c.Table._scope.DEFAULT_DELIMITERS["$[]"](this.format) : this.delimiter = "!";
                this.delimiter_re = new RegExp("" + c.Regexp.$escape(this.delimiter));
                this.col_count = (g = d.$columns()["$empty?"]()) === a || g._isBoolean && !0 != g ? d.$columns().$size() :
                    -1;
                this.buffer = "";
                this.cell_specs = [];
                this.cell_open = !1;
                this.active_rowspans = [0];
                this.col_visits = 0;
                this.current_row = [];
                return this.linenum = -1
            };
            b["$starts_with_delimiter?"] = function (a) {
                return a["$start_with?"](this.delimiter)
            };
            b.$match_delimiter = function (a) {
                return this.delimiter_re.$match(a)
            };
            b.$skip_matched_delimiter = function (b, c) {
                null == c && (c = !1);
                this.buffer = "" + this.buffer + (!1 !== c && c !== a ? b.$pre_match().$chop() : b.$pre_match()) + this.delimiter;
                return b.$post_match()
            };
            b["$buffer_has_unclosed_quotes?"] =
                function (b) {
                    var c, d, g = a;
                    null == b && (b = a);
                    g = ("" + this.buffer + b).$strip();
                    return c = (d = g["$start_with?"]('"'), !1 !== d && d !== a ? g["$start_with?"]('""')["$!"]() : d), !1 !== c && c !== a ? g["$end_with?"]('"')["$!"]() : c
                };
            b["$buffer_quoted?"] = function () {
                var b;
                this.buffer = this.buffer.$lstrip();
                return b = this.buffer["$start_with?"]('"'), !1 !== b && b !== a ? this.buffer["$start_with?"]('""')["$!"]() : b
            };
            b.$take_cell_spec = function () {
                return this.cell_specs.$shift()
            };
            b.$push_cell_spec = function (b) {
                var c;
                null == b && (b = r([], {}));
                this.cell_specs["$<<"](!1 !==
                    (c = b) && c !== a ? c : r([], {}));
                return a
            };
            b.$keep_cell_open = function () {
                this.cell_open = !0;
                return a
            };
            b.$mark_cell_closed = function () {
                this.cell_open = !1;
                return a
            };
            b["$cell_open?"] = function () {
                return this.cell_open
            };
            b["$cell_closed?"] = function () {
                return this.cell_open["$!"]()
            };
            b.$close_open_cell = function (b) {
                var c;
                null == b && (b = r([], {}));
                this.$push_cell_spec(b);
                (c = this["$cell_open?"]()) === a || c._isBoolean && !0 != c || this.$close_cell(!0);
                this.$advance();
                return a
            };
            b.$close_cell = function (b) {
                var d, e, g, h = a, k = a, m = a;
                null == b &&
                (b = !1);
                h = this.buffer.$strip();
                this.buffer = "";
                this.format["$=="]("psv") ? (k = this.$take_cell_spec(), (d = k["$nil?"]()) === a || d._isBoolean && !0 != d ? (m = k.$fetch("repeatcol", 1), k.$delete("repeatcol")) : (this.$warn("asciidoctor: ERROR: " + this.last_cursor.$line_info() + ": table missing leading separator, recovering automatically"), k = r([], {}), m = 1)) : (k = a, m = 1, !this.format["$=="]("csv") || (d = (e = h["$empty?"]()["$!"](), !1 !== e && e !== a ? h["$include?"]('"') : e)) === a || d._isBoolean && !0 != d || ((d = (e = h["$start_with?"]('"'), !1 !== e &&
                    e !== a ? h["$end_with?"]('"') : e)) === a || d._isBoolean && !0 != d || (h = h["$[]"](v(1, -1, !0)).$strip()), h = h.$tr_s('"', '"')));
                (d = (e = 1).$upto, d._p = (g = function (d) {
                    var e = g._s || this, p, n, r, t, u = a, u = a;
                    null == e.col_count && (e.col_count = a);
                    null == e.table && (e.table = a);
                    null == e.current_row && (e.current_row = a);
                    null == e.last_cursor && (e.last_cursor = a);
                    null == e.reader && (e.reader = a);
                    null == e.col_visits && (e.col_visits = a);
                    null == e.linenum && (e.linenum = a);
                    null == d && (d = a);
                    e.col_count["$=="](-1) ? (e.table.$columns()["$<<"](c.Table._scope.Column.$new(e.table,
                        e.current_row.$size()["$+"](d)["$-"](1))), u = e.table.$columns()["$[]"](-1)) : u = e.table.$columns()["$[]"](e.current_row.$size());
                    u = c.Table._scope.Cell.$new(u, h, k, e.last_cursor);
                    e.last_cursor = e.reader.$cursor();
                    ((p = !1 !== (n = u.$rowspan()["$!"]()) && n !== a ? n : u.$rowspan()["$=="](1)) === a || p._isBoolean && !0 != p) && e.$activate_rowspan(u.$rowspan(), !1 !== (p = u.$colspan()) && p !== a ? p : 1);
                    e.col_visits = e.col_visits["$+"](!1 !== (p = u.$colspan()) && p !== a ? p : 1);
                    e.current_row["$<<"](u);
                    return(p = (n = e["$end_of_row?"](), !1 !== n && n !==
                        a ? !1 !== (r = !1 !== (t = e.col_count["$=="](-1)["$!"]()) && t !== a ? t : e.linenum["$>"](0)) && r !== a ? r : (t = !1 !== b && b !== a) ? d["$=="](m) : t : n)) === a || p._isBoolean && !0 != p ? a : e.$close_row()
                }, g._s = this, g), d).call(e, m);
                this.open_cell = !1;
                return a
            };
            b.$close_row = function () {
                var b, c;
                this.table.$rows().$body()["$<<"](this.current_row);
                this.col_count["$=="](-1) && (this.col_count = this.col_visits);
                this.col_visits = 0;
                this.current_row = [];
                this.active_rowspans.$shift();
                b = this.active_rowspans;
                !1 !== (c = b["$[]"](0)) && c !== a ? c : b["$[]="](0, 0);
                return a
            };
            b.$activate_rowspan = function (b, c) {
                var d, g, h;
                (d = (g = (1).$upto(b["$-"](1))).$each, d._p = (h = function (b) {
                    var d = h._s || this, e;
                    null == d.active_rowspans && (d.active_rowspans = a);
                    null == b && (b = a);
                    return d.active_rowspans["$[]="](b, (!1 !== (e = d.active_rowspans["$[]"](b)) && e !== a ? e : 0)["$+"](c))
                }, h._s = this, h), d).call(g);
                return a
            };
            b["$end_of_row?"] = function () {
                var b;
                return!1 !== (b = this.col_count["$=="](-1)) && b !== a ? b : this.$effective_col_visits()["$=="](this.col_count)
            };
            b.$effective_col_visits = function () {
                return this.col_visits["$+"](this.active_rowspans["$[]"](0))
            };
            return(b.$advance = function () {
                return this.linenum = this.linenum["$+"](1)
            }, a) && "advance"
        })(A.Table, null)
    }(d.top)
})(Opal);
(function (d) {
    var a, u = d.top, t = d.nil, w = d.gvars, r = d.module, v = d.hash2, x = d.range;
    null == w[":"] && (w[":"] = t);
    ((a = null != d.RUBY_ENGINE) === t || a._isBoolean && !0 != a) && d.cdecl(d, "RUBY_ENGINE", "unknown");
    d.cdecl(d, "RUBY_ENGINE_OPAL", d.RUBY_ENGINE["$=="]("opal"));
    d.cdecl(d, "RUBY_ENGINE_JRUBY", d.RUBY_ENGINE["$=="]("jruby"));
    d.cdecl(d, "RUBY_MIN_VERSION_1_9", d.RUBY_VERSION["$>="]("1.9"));
    d.cdecl(d, "RUBY_MIN_VERSION_2", d.RUBY_VERSION["$>="]("2"));
    d.RUBY_ENGINE["$=="]("opal");
    w[":"].$unshift(d.File.$dirname("asciidoctor"));
    (function (a) {
        a = r(a, "Asciidoctor");
        var m = a._scope, q, h, n;
        d.cdecl(m, "RUBY_ENGINE", null == (q = d.Object._scope.RUBY_ENGINE) ? d.cm("RUBY_ENGINE") : q);
        (function (a) {
            a = r(a, "SafeMode")._scope;
            d.cdecl(a, "UNSAFE", 0);
            d.cdecl(a, "SAFE", 1);
            d.cdecl(a, "SERVER", 10);
            d.cdecl(a, "SECURE", 20)
        })(a);
        (function (a) {
            a = r(a, "Compliance");
            a.keys = [].$to_set();
            (function (a) {
                return a.$attr("keys")
            })(a.$singleton_class());
            d.defs(a, "$define", function (a, b) {
                var h, e;
                null == this.keys && (this.keys = t);
                (h = !1 !== (e = a["$=="]("keys")) && e !== t ? e : this["$respond_to?"](a)) ===
                    t || h._isBoolean && !0 != h || this.$raise(null == (h = d.Object._scope.ArgumentError) ? d.cm("ArgumentError") : h, "Illegal key name: " + a);
                this.$instance_variable_set("@" + a, b);
                this.$singleton_class().$send("attr_accessor", a);
                return this.keys["$<<"](a)
            });
            a.$define("block_terminates_paragraph", !0);
            a.$define("strict_verbatim_paragraphs", !0);
            a.$define("underline_style_section_titles", !0);
            a.$define("unwrap_standalone_preamble", !0);
            a.$define("attribute_missing", "skip");
            a.$define("attribute_undefined", "drop-line");
            a.$define("markdown_syntax",
                !0)
        })(a);
        d.cdecl(m, "ROOT_PATH", (null == (q = d.Object._scope.File) ? d.cm("File") : q).$dirname((null == (q = d.Object._scope.File) ? d.cm("File") : q).$dirname((null == (q = d.Object._scope.File) ? d.cm("File") : q).$expand_path("asciidoctor"))));
        d.cdecl(m, "LIB_PATH", (null == (q = d.Object._scope.File) ? d.cm("File") : q).$join(m.ROOT_PATH, "lib"));
        d.cdecl(m, "DATA_PATH", (null == (q = d.Object._scope.File) ? d.cm("File") : q).$join(m.ROOT_PATH, "data"));
        d.cdecl(m, "USER_HOME", function () {
            try {
                return(null == (q = d.Object._scope.Dir) ? d.cm("Dir") :
                    q).$home()
            } catch (a) {
                return!1 !== (q = (null == (h = d.Object._scope.ENV) ? d.cm("ENV") : h)["$[]"]("HOME")) && q !== t ? q : (null == (h = d.Object._scope.Dir) ? d.cm("Dir") : h).$pwd()
            }
        }());
        d.cdecl(m, "COERCE_ENCODING", (q = (null == (h = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : h)["$!"](), !1 !== q && q !== t ? null == (h = d.Object._scope.RUBY_MIN_VERSION_1_9) ? d.cm("RUBY_MIN_VERSION_1_9") : h : q));
        d.cdecl(m, "FORCE_ENCODING", (q = m.COERCE_ENCODING, !1 !== q && q !== t ? (null == (h = d.Object._scope.Encoding) ? d.cm("Encoding") : h).$default_external()["$=="]((null ==
            (h = d.Object._scope.Encoding) ? d.cm("Encoding") : h)._scope.UTF_8)["$!"]() : q));
        d.cdecl(m, "BOM_BYTES_UTF_8", "\u00ef\u00bb\u00bf".$bytes().$to_a());
        d.cdecl(m, "BOM_BYTES_UTF_16LE", "\u00ff\u00fe".$bytes().$to_a());
        d.cdecl(m, "BOM_BYTES_UTF_16BE", "\u00fe\u00ff".$bytes().$to_a());
        d.cdecl(m, "FORCE_UNICODE_LINE_LENGTH", (null == (q = d.Object._scope.RUBY_MIN_VERSION_1_9) ? d.cm("RUBY_MIN_VERSION_1_9") : q)["$!"]());
        d.cdecl(m, "SUPPORTS_GSUB_RESULT_HASH", (q = null == (h = d.Object._scope.RUBY_MIN_VERSION_1_9) ? d.cm("RUBY_MIN_VERSION_1_9") :
            h, !1 !== q && q !== t ? (null == (h = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : h)["$!"]() : q));
        d.cdecl(m, "EOL", "\n");
        d.cdecl(m, "NULL", "\x00");
        d.cdecl(m, "TAB", "\t");
        d.cdecl(m, "TAB_PATTERN", /\t/);
        d.cdecl(m, "DEFAULT_DOCTYPE", "article");
        d.cdecl(m, "DEFAULT_BACKEND", "html5");
        d.cdecl(m, "DEFAULT_STYLESHEET_KEYS", ["", "DEFAULT"].$to_set());
        d.cdecl(m, "DEFAULT_STYLESHEET_NAME", "asciidoctor.css");
        d.cdecl(m, "BACKEND_ALIASES", v(["html", "docbook"], {html: "html5", docbook: "docbook5"}));
        d.cdecl(m, "DEFAULT_PAGE_WIDTHS",
            v(["docbook"], {docbook: 425}));
        d.cdecl(m, "DEFAULT_EXTENSIONS", v(["html", "docbook", "pdf", "epub", "asciidoc"], {html: ".html", docbook: ".xml", pdf: ".pdf", epub: ".epub", asciidoc: ".adoc"}));
        d.cdecl(m, "ASCIIDOC_EXTENSIONS", v([".asciidoc", ".adoc", ".ad", ".asc", ".txt"], {".asciidoc": !0, ".adoc": !0, ".ad": !0, ".asc": !0, ".txt": !0}));
        d.cdecl(m, "SECTION_LEVELS", v(["=", "-", "~", "^", "+"], {"=": 0, "-": 1, "~": 2, "^": 3, "+": 4}));
        d.cdecl(m, "ADMONITION_STYLES", ["NOTE", "TIP", "IMPORTANT", "WARNING", "CAUTION"].$to_set());
        d.cdecl(m, "PARAGRAPH_STYLES",
            "comment example literal listing normal pass quote sidebar source verse abstract partintro".split(" ").$to_set());
        d.cdecl(m, "VERBATIM_STYLES", ["literal", "listing", "source", "verse"].$to_set());
        d.cdecl(m, "DELIMITED_BLOCKS", v('-- ---- .... ==== **** ____ "" ++++ |=== ,=== :=== !=== //// ``` ~~~'.split(" "), {"--": ["open", "comment example literal listing pass quote sidebar source verse admonition abstract partintro".split(" ").$to_set()], "----": ["listing", ["literal", "source"].$to_set()], "....": ["literal",
            ["listing", "source"].$to_set()], "====": ["example", ["admonition"].$to_set()], "****": ["sidebar", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], ____: ["quote", ["verse"].$to_set()], '""': ["quote", ["verse"].$to_set()], "++++": ["pass", ["math", "latexmath", "asciimath"].$to_set()], "|===": ["table", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], ",===": ["table", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], ":===": ["table", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], "!===": ["table",
            (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], "////": ["comment", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], "```": ["fenced_code", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()], "~~~": ["fenced_code", (null == (q = d.Object._scope.Set) ? d.cm("Set") : q).$new()]}));
        d.cdecl(m, "DELIMITED_BLOCK_LEADERS", (q = (h = m.DELIMITED_BLOCKS.$keys()).$map, q._p = (n = function (a) {
            null == a && (a = t);
            return a["$[]"](x(0, 1, !1))
        }, n._s = a, n), q).call(h).$to_set());
        d.cdecl(m, "LAYOUT_BREAK_LINES", v(["'", "-", "*", "_", "<"],
            {"'": "thematic_break", "-": "thematic_break", "*": "thematic_break", _: "thematic_break", "<": "page_break"}));
        d.cdecl(m, "NESTABLE_LIST_CONTEXTS", ["ulist", "olist", "dlist"]);
        d.cdecl(m, "ORDERED_LIST_STYLES", ["arabic", "loweralpha", "lowerroman", "upperalpha", "upperroman"]);
        d.cdecl(m, "ORDERED_LIST_KEYWORDS", v(["loweralpha", "lowerroman", "upperalpha", "upperroman"], {loweralpha: "a", lowerroman: "i", upperalpha: "A", upperroman: "I"}));
        d.cdecl(m, "LIST_CONTINUATION", "+");
        d.cdecl(m, "LINE_BREAK", " +");
        d.cdecl(m, "BLOCK_MATH_DELIMITERS",
            v(["asciimath", "latexmath"], {asciimath: ["\\$", "\\$"], latexmath: ["\\[", "\\]"]}));
        d.cdecl(m, "INLINE_MATH_DELIMITERS", v(["asciimath", "latexmath"], {asciimath: ["\\$", "\\$"], latexmath: ["\\(", "\\)"]}));
        d.cdecl(m, "FLEXIBLE_ATTRIBUTES", ["numbered"]);
        m.RUBY_ENGINE["$=="]("opal") && (d.cdecl(m, "CC_ALPHA", "a-zA-Z"), d.cdecl(m, "CG_ALPHA", "[a-zA-Z]"), d.cdecl(m, "CC_ALNUM", "a-zA-Z0-9"), d.cdecl(m, "CG_ALNUM", "[a-zA-Z0-9]"), d.cdecl(m, "CG_BLANK", "[ \\t]"), d.cdecl(m, "CC_EOL", "(?=\\n|$)"), d.cdecl(m, "CG_GRAPH", "[\\x21-\\x7E]"),
            d.cdecl(m, "CC_WORD", "a-zA-Z0-9_"), d.cdecl(m, "CG_WORD", "[a-zA-Z0-9_]"));
        d.cdecl(m, "AuthorInfoLineRx", new RegExp("^(" + m.CG_WORD + "[" + m.CC_WORD + "\\-'.]*)(?: +(" + m.CG_WORD + "[" + m.CC_WORD + "\\-'.]*))?(?: +(" + m.CG_WORD + "[" + m.CC_WORD + "\\-'.]*))?(?: +<([^>]+)>)?$"));
        d.cdecl(m, "RevisionInfoLineRx", /^(?:\D*(.*?),)?(?:\s*(?!:)(.*?))(?:\s*(?!^):\s*(.*))?$/);
        d.cdecl(m, "ManpageTitleVolnumRx", /^(.*)\((.*)\)$/);
        d.cdecl(m, "ManpageNamePurposeRx", new RegExp("^(.*?)" + m.CG_BLANK + "+-" + m.CG_BLANK + "+(.*)$"));
        d.cdecl(m, "ConditionalDirectiveRx",
            /^\\?(ifdef|ifndef|ifeval|endif)::(\S*?(?:([,\+])\S+?)?)\[(.+)?\]$/);
        d.cdecl(m, "EvalExpressionRx", new RegExp("^(\\S.*?)" + m.CG_BLANK + "*(==|!=|<=|>=|<|>)" + m.CG_BLANK + "*(\\S.*)$"));
        d.cdecl(m, "IncludeDirectiveRx", /^\\?include::([^\[]+)\[(.*?)\]$/);
        d.cdecl(m, "AttributeEntryRx", new RegExp("^:(!?\\w.*?):(?:" + m.CG_BLANK + "+(.*))?$"));
        d.cdecl(m, "InvalidAttributeNameCharsRx", /[^\w\-]/);
        d.cdecl(m, "AttributeEntryPassMacroRx", /^pass:([a-z,]*)\[(.*)\]$/);
        d.cdecl(m, "AttributeReferenceRx", /(\\)?\{((set|counter2?):.+?|\w+(?:[\-]\w+)*)(\\)?\}/);
        d.cdecl(m, "BlockAnchorRx", new RegExp("^\\[\\[(?:|([" + m.CC_ALPHA + ":_][" + m.CC_WORD + ":.-]*)(?:," + m.CG_BLANK + "*(\\S.*))?)\\]\\]$"));
        d.cdecl(m, "BlockAttributeListRx", new RegExp("^\\[(|" + m.CG_BLANK + "*[" + m.CC_WORD + "\\{,.#\"'%].*)\\]$"));
        d.cdecl(m, "BlockAttributeLineRx", new RegExp("^\\[(|" + m.CG_BLANK + "*[" + m.CC_WORD + "\\{,.#\"'%].*|\\[(?:|[" + m.CC_ALPHA + ":_][" + m.CC_WORD + ":.-]*(?:," + m.CG_BLANK + "*\\S.*)?)\\])\\]$"));
        d.cdecl(m, "BlockTitleRx", /^\.([^\s.].*)$/);
        d.cdecl(m, "AdmonitionParagraphRx", new RegExp("^(" + m.ADMONITION_STYLES.$to_a()["$*"]("|") +
            "):" + m.CG_BLANK));
        d.cdecl(m, "LiteralParagraphRx", new RegExp("^(" + m.CG_BLANK + "+.*)$"));
        d.cdecl(m, "CommentBlockRx", /^\/{4,}$/);
        d.cdecl(m, "CommentLineRx", /^\/\/(?:[^\/]|$)/);
        d.cdecl(m, "AtxSectionRx", new RegExp("^((?:=|#){1,6})" + m.CG_BLANK + "+(\\S.*?)(?:" + m.CG_BLANK + "+\\1)?$"));
        d.cdecl(m, "SetextSectionTitleRx", new RegExp("^((?=.*" + m.CG_WORD + "+.*)[^.].*?)$"));
        d.cdecl(m, "SetextSectionLineRx", /^(?:=|-|~|\^|\+)+$/);
        d.cdecl(m, "InlineSectionAnchorRx", new RegExp("^(.*?)" + m.CG_BLANK + "+(\\\\)?\\[\\[([" + m.CC_ALPHA +
            ":_][" + m.CC_WORD + ":.-]*)(?:," + m.CG_BLANK + "*(\\S.*?))?\\]\\]$"));
        d.cdecl(m, "InvalidSectionIdCharsRx", new RegExp("&(?:[a-zA-Z]{2,}|#\\d{2,5}|#x[a-fA-F0-9]{2,4});|[^" + m.CC_WORD + "]+?"));
        d.cdecl(m, "FloatingTitleStyleRx", /^(?:float|discrete)\b/);
        d.cdecl(m, "AnyListRx", new RegExp("^(?:<?\\d+>" + m.CG_BLANK + "+" + m.CG_GRAPH + "|" + m.CG_BLANK + "*(?:-|(?:\\*|\\.){1,5}|\\d+\\.|[a-zA-Z]\\.|[IVXivx]+\\))" + m.CG_BLANK + "+" + m.CG_GRAPH + "|" + m.CG_BLANK + "*.*?(?::{2,4}|;;)(?:" + m.CG_BLANK + "+" + m.CG_GRAPH + "|$))"));
        d.cdecl(m, "UnorderedListRx",
            new RegExp("^" + m.CG_BLANK + "*(-|\\*{1,5})" + m.CG_BLANK + "+(.*)$"));
        d.cdecl(m, "OrderedListRx", new RegExp("^" + m.CG_BLANK + "*(\\.{1,5}|\\d+\\.|[a-zA-Z]\\.|[IVXivx]+\\))" + m.CG_BLANK + "+(.*)$"));
        d.cdecl(m, "OrderedListMarkerRxMap", v(["arabic", "loweralpha", "lowerroman", "upperalpha", "upperroman"], {arabic: /\d+[.>]/, loweralpha: /[a-z]\./, lowerroman: /[ivx]+\)/, upperalpha: /[A-Z]\./, upperroman: /[IVX]+\)/}));
        d.cdecl(m, "DefinitionListRx", new RegExp("^(?!\\/\\/)" + m.CG_BLANK + "*(.*?)(:{2,4}|;;)(?:" + m.CG_BLANK + "+(.*))?$"));
        d.cdecl(m, "DefinitionListSiblingRx", v(["::", ":::", "::::", ";;"], {"::": new RegExp("^(?!\\/\\/)" + m.CG_BLANK + "*((?:.*[^:])?)(::)(?:" + m.CG_BLANK + "+(.*))?$"), ":::": new RegExp("^(?!\\/\\/)" + m.CG_BLANK + "*((?:.*[^:])?)(:::)(?:" + m.CG_BLANK + "+(.*))?$"), "::::": new RegExp("^(?!\\/\\/)" + m.CG_BLANK + "*((?:.*[^:])?)(::::)(?:" + m.CG_BLANK + "+(.*))?$"), ";;": new RegExp("^(?!\\/\\/)" + m.CG_BLANK + "*(.*)(;;)(?:" + m.CG_BLANK + "+(.*))?$")}));
        d.cdecl(m, "CalloutListRx", new RegExp("^<?(\\d+)>" + m.CG_BLANK + "+(.*)"));
        d.cdecl(m, "CalloutConvertRx",
            new RegExp("(?:(?:\\/\\/|#|;;) ?)?(\\\\)?&lt;!?(--|)(\\d+)\\2&gt;(?=(?: ?\\\\?&lt;!?\\2\\d+\\2&gt;)*" + m.CC_EOL + ")"));
        d.cdecl(m, "CalloutQuickScanRx", new RegExp("\\\\?<!?(--|)(\\d+)\\1>(?=(?: ?\\\\?<!?\\1\\d+\\1>)*" + m.CC_EOL + ")"));
        d.cdecl(m, "CalloutScanRx", new RegExp("(?:(?:\\/\\/|#|;;) ?)?(\\\\)?<!?(--|)(\\d+)\\2>(?=(?: ?\\\\?<!?\\2\\d+\\2>)*" + m.CC_EOL + ")"));
        d.cdecl(m, "ListRxMap", v(["ulist", "olist", "dlist", "colist"], {ulist: m.UnorderedListRx, olist: m.OrderedListRx, dlist: m.DefinitionListRx, colist: m.CalloutListRx}));
        d.cdecl(m, "ColumnSpecRx", /^(?:(\d+)\*)?([<^>](?:\.[<^>]?)?|(?:[<^>]?\.)?[<^>])?(\d+%?)?([a-z])?$/);
        d.cdecl(m, "CellSpecStartRx", new RegExp("^" + m.CG_BLANK + "*(?:(\\d+(?:\\.\\d*)?|(?:\\d*\\.)?\\d+)([*+]))?([<^>](?:\\.[<^>]?)?|(?:[<^>]?\\.)?[<^>])?([a-z])?\\|"));
        d.cdecl(m, "CellSpecEndRx", new RegExp("" + m.CG_BLANK + "+(?:(\\d+(?:\\.\\d*)?|(?:\\d*\\.)?\\d+)([*+]))?([<^>](?:\\.[<^>]?)?|(?:[<^>]?\\.)?[<^>])?([a-z])?$"));
        d.cdecl(m, "GenericBlockMacroRx", new RegExp("^(" + m.CG_WORD + "+)::(\\S*?)\\[((?:\\\\\\]|[^\\]])*?)\\]$"));
        d.cdecl(m, "MediaBlockMacroRx", /^(image|video|audio)::(\S+?)\[((?:\\\]|[^\]])*?)\]$/);
        d.cdecl(m, "TocBlockMacroRx", /^toc::\[(.*?)\]$/);
        d.cdecl(m, "InlineAnchorRx", new RegExp("\\\\?(?:\\[\\[([" + m.CC_ALPHA + ":_][" + m.CC_WORD + ":.-]*)(?:," + m.CG_BLANK + "*(\\S.*?))?\\]\\]|anchor:(\\S+)\\[(.*?[^\\\\])?\\])"));
        d.cdecl(m, "InlineBiblioAnchorRx", new RegExp("\\\\?\\[\\[\\[([" + m.CC_WORD + ":][" + m.CC_WORD + ":.-]*?)\\]\\]\\]"));
        d.cdecl(m, "EmailInlineMacroRx", new RegExp("([\\\\>:\\/])?" + m.CG_WORD + "[" + m.CC_WORD + ".%+-]*@" +
            m.CG_ALNUM + "[" + m.CC_ALNUM + ".-]*\\." + m.CG_ALPHA + "{2,4}\\b"));
        d.cdecl(m, "FootnoteInlineMacroRx", /\\?(footnote(?:ref)?):\[(.*?[^\\])\]/m);
        d.cdecl(m, "ImageInlineMacroRx", /\\?(?:image|icon):([^:\[][^\[]*)\[((?:\\\]|[^\]])*?)\]/);
        d.cdecl(m, "IndextermInlineMacroRx", /\\?(?:(indexterm2?):\[(.*?[^\\])\]|\(\((.+?)\)\)(?!\)))/m);
        d.cdecl(m, "KbdBtnInlineMacroRx", /\\?(?:kbd|btn):\[((?:\\\]|[^\]])+?)\]/);
        d.cdecl(m, "KbdDelimiterRx", new RegExp("(?:\\+|,)(?=" + m.CG_BLANK + "*[^\\1])"));
        d.cdecl(m, "LinkInlineRx", /(^|link:|&lt;|[\s>\(\)\[\];])(\\?(?:https?|file|ftp|irc):\/\/[^\s\[\]<]*[^\s.,\[\]<])(?:\[((?:\\\]|[^\]])*?)\])?/);
        d.cdecl(m, "LinkInlineMacroRx", /\\?(?:link|mailto):([^\s\[]+)(?:\[((?:\\\]|[^\]])*?)\])/);
        d.cdecl(m, "MathInlineMacroRx", /\\?((?:latex|ascii)?math):([a-z,]*)\[(.*?[^\\])\]/m);
        d.cdecl(m, "MenuInlineMacroRx", new RegExp("\\\\?menu:(" + m.CG_WORD + "|" + m.CG_WORD + ".*?\\S)\\[" + m.CG_BLANK + "*(.+?)?\\]"));
        d.cdecl(m, "MenuInlineRx", new RegExp('\\\\?"(' + m.CG_WORD + '[^"]*?' + m.CG_BLANK + "*&gt;" + m.CG_BLANK + '*[^" \\t][^"]*)"'));
        d.cdecl(m, "PassInlineLiteralRx", new RegExp("(^|[^`" + m.CC_WORD + "])(?:\\[([^\\]]+?)\\])?(\\\\?`([^`\\s]|[^`\\s].*?\\S)`)(?![`" +
            m.CC_WORD + "])"));
        d.cdecl(m, "PassInlineMacroRx", /\\?(?:(\+{3}|\${2})(.*?)\1|pass:([a-z,]*)\[(.*?[^\\])\])/m);
        d.cdecl(m, "XrefInlineMacroRx", new RegExp("\\\\?(?:&lt;&lt;([" + m.CC_WORD + '":].*?)&gt;&gt;|xref:([' + m.CC_WORD + '":].*?)\\[(.*?)\\])'));
        d.cdecl(m, "LineBreakRx", m.RUBY_ENGINE["$=="]("opal") ? /^(.*)[ \t]\+$/m : t);
        d.cdecl(m, "LayoutBreakLineRx", /^('|<){3,}$/);
        d.cdecl(m, "LayoutBreakLinePlusRx", /^(?:'|<){3,}$|^ {0,3}([-\*_])( *)\1\2\1$/);
        d.cdecl(m, "BlankLineRx", new RegExp("^" + m.CG_BLANK + "*\\n"));
        d.cdecl(m,
            "DataDelimiterRx", /,|;/);
        d.cdecl(m, "DigitsRx", /^\d+$/);
        d.cdecl(m, "DoubleQuotedRx", /^("|)(.*)\1$/);
        d.cdecl(m, "DoubleQuotedMultiRx", /^("|)(.*)\1$/m);
        d.cdecl(m, "TrailingDigitsRx", /\d+$/);
        d.cdecl(m, "EscapedSpaceRx", new RegExp("\\\\(" + m.CG_BLANK + ")"));
        d.cdecl(m, "SpaceDelimiterRx", new RegExp("([^\\\\])" + m.CG_BLANK + "+"));
        d.cdecl(m, "UnicodeCharScanRx", function () {
            m.RUBY_ENGINE["$=="]("opal");
            return t
        }());
        d.cdecl(m, "UriSniffRx", new RegExp("^" + m.CG_ALPHA + "[" + m.CC_ALNUM + ".+-]*:/{0,2}"));
        d.cdecl(m, "UriTerminator",
            /[);:]$/);
        d.cdecl(m, "XmlSanitizeRx", /<[^>]+>/);
        d.cdecl(m, "INTRINSIC_ATTRIBUTES", v("startsb endsb vbar caret asterisk tilde plus apostrophe backslash backtick empty sp space two-colons two-semicolons nbsp deg zwsp quot apos lsquo rsquo ldquo rdquo wj brvbar amp lt gt".split(" "), {startsb: "[", endsb: "]", vbar: "|", caret: "^", asterisk: "*", tilde: "~", plus: "&#43;", apostrophe: "'", backslash: "\\", backtick: "`", empty: "", sp: " ", space: " ", "two-colons": "::", "two-semicolons": ";;", nbsp: "&#160;", deg: "&#176;", zwsp: "&#8203;",
            quot: "&#34;", apos: "&#39;", lsquo: "&#8216;", rsquo: "&#8217;", ldquo: "&#8220;", rdquo: "&#8221;", wj: "&#8288;", brvbar: "&#166;", amp: "&", lt: "<", gt: ">"}));
        d.cdecl(m, "QUOTE_SUBS", [
            ["strong", "unconstrained", /\\?(?:\[([^\]]+?)\])?\*\*(.+?)\*\*/m],
            ["strong", "constrained", new RegExp("(^|[^" + m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?\\*(\\S|\\S.*?\\S)\\*(?!" + m.CG_WORD + ")")],
            ["double", "constrained", new RegExp("(^|[^" + m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?``(\\S|\\S.*?\\S)''(?!" + m.CG_WORD + ")")],
            ["emphasis", "constrained",
                new RegExp("(^|[^" + m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?'(\\S|\\S.*?\\S)'(?!" + m.CG_WORD + ")")],
            ["single", "constrained", new RegExp("(^|[^" + m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?`(\\S|\\S.*?\\S)'(?!" + m.CG_WORD + ")")],
            ["monospaced", "unconstrained", /\\?(?:\[([^\]]+?)\])?\+\+(.+?)\+\+/m],
            ["monospaced", "constrained", new RegExp("(^|[^" + m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?\\+(\\S|\\S.*?\\S)\\+(?!" + m.CG_WORD + ")")],
            ["emphasis", "unconstrained", /\\?(?:\[([^\]]+?)\])?__(.+?)__/m],
            ["emphasis", "constrained", new RegExp("(^|[^" +
                m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?_(\\S|\\S.*?\\S)_(?!" + m.CG_WORD + ")")],
            ["none", "unconstrained", /\\?(?:\[([^\]]+?)\])?##(.+?)##/m],
            ["none", "constrained", new RegExp("(^|[^" + m.CC_WORD + ";:}])(?:\\[([^\\]]+?)\\])?#(\\S|\\S.*?\\S)#(?!" + m.CG_WORD + ")")],
            ["superscript", "unconstrained", /\\?(?:\[([^\]]+?)\])?\^(.+?)\^/m],
            ["subscript", "unconstrained", /\\?(?:\[([^\]]+?)\])?~(.+?)~/m]
        ]);
        d.cdecl(m, "REPLACEMENTS", [
            [/\\?\(C\)/, "&#169;", "none"],
            [/\\?\(R\)/, "&#174;", "none"],
            [/\\?\(TM\)/, "&#8482;", "none"],
            [/(^|\n| |\\)--( |\n|$)/,
                "&#8201;&#8212;&#8201;", "none"],
            [new RegExp("(" + m.CG_WORD + ")\\\\?--(?=" + m.CG_WORD + ")"), "&#8212;", "leading"],
            [/\\?\.\.\./, "&#8230;", "leading"],
            [new RegExp("(" + m.CG_ALPHA + ")\\\\?'(?!')"), "&#8217;", "leading"],
            [/\\?-&gt;/, "&#8594;", "none"],
            [/\\?=&gt;/, "&#8658;", "none"],
            [/\\?&lt;-/, "&#8592;", "none"],
            [/\\?&lt;=/, "&#8656;", "none"],
            [/\\?(&)amp;((?:[a-zA-Z]+|#\d{2,5}|#x[a-fA-F0-9]{2,4});)/, "", "bounding"]
        ]);
        (function (a) {
            var c = a._scope;
            a._proto.$load = function (a, b) {
                var e, g, h, k, m, n, q, r, u = this, w = t, y = t, x = t, A = t,
                    D = t, B = t, C = t, T = t, y = C = T = t;
                null == b && (b = v([], {}));
                b = b.$dup();
                (e = w = b["$[]"]("timings")) === t || e._isBoolean && !0 != e || w.$start("read");
                y = b["$[]="]("attributes", function () {
                    if ((e = (x = b["$[]"]("attributes"))["$!"]()) === t || e._isBoolean && !0 != e) {
                        if ((e = !1 !== (g = x["$is_a?"](null == (h = d.Object._scope.Hash) ? d.cm("Hash") : h)) && g !== t ? g : (h = null == (k = d.Object._scope.RUBY_ENGINE_JRUBY) ? d.cm("RUBY_ENGINE_JRUBY") : k, !1 !== h && h !== t ? x["$is_a?"]((null == (k = d.Object._scope.Java) ? d.cm("Java") : k)._scope.JavaUtil._scope.Map) : h)) === t || e._isBoolean &&
                            !0 != e) {
                            if ((e = x["$is_a?"](null == (g = d.Object._scope.Array) ? d.cm("Array") : g)) === t || e._isBoolean && !0 != e) {
                                if ((e = x["$is_a?"](null == (h = d.Object._scope.String) ? d.cm("String") : h)) === t || e._isBoolean && !0 != e) {
                                    if ((e = (k = x["$respond_to?"]("keys"), !1 !== k && k !== t ? x["$respond_to?"]("[]") : k)) === t || e._isBoolean && !0 != e)return u.$raise(null == (e = d.Object._scope.ArgumentError) ? d.cm("ArgumentError") : e, "illegal type for attributes option: " + x.$class().$ancestors());
                                    D = x;
                                    x = v([], {});
                                    (e = (k = D.$keys()).$each, e._p = (q = function (a) {
                                        null ==
                                            a && (a = t);
                                        return x["$[]="](a, D["$[]"](a))
                                    }, q._s = u, q), e).call(k);
                                    return x
                                }
                                A = (e = null == (h = d.Object._scope.RUBY_ENGINE_OPAL) ? d.cm("RUBY_ENGINE_OPAL") : h) === t || e._isBoolean && !0 != e ? "\\1" : "$1";
                                x = x.$gsub(c.SpaceDelimiterRx, "" + A + c.NULL).$gsub(c.EscapedSpaceRx, A);
                                return(e = (h = x.$split(c.NULL)).$inject, e._p = (n = function (a, b) {
                                    var c, e = t, f = t;
                                    null == a && (a = t);
                                    null == b && (b = t);
                                    c = d.to_ary(b.$split("=", 2));
                                    e = null == c[0] ? t : c[0];
                                    f = null == c[1] ? t : c[1];
                                    a["$[]="](e, !1 !== (c = f) && c !== t ? c : "");
                                    return a
                                }, n._s = u, n), e).call(h, v([], {}))
                            }
                            return(e =
                                (g = x).$inject, e._p = (m = function (a, b) {
                                var c, e = t, f = t;
                                null == a && (a = t);
                                null == b && (b = t);
                                c = d.to_ary(b.$split("=", 2));
                                e = null == c[0] ? t : c[0];
                                f = null == c[1] ? t : c[1];
                                a["$[]="](e, !1 !== (c = f) && c !== t ? c : "");
                                return a
                            }, m._s = u, m), e).call(g, v([], {}))
                        }
                        return x.$dup()
                    }
                    return v([], {})
                }());
                B = t;
                if ((e = a["$is_a?"](null == (r = d.Object._scope.File) ? d.cm("File") : r)) === t || e._isBoolean && !0 != e)if ((e = a["$respond_to?"]("readlines")) === t || e._isBoolean && !0 != e)(e = a["$is_a?"](null == (r = d.Object._scope.String) ? d.cm("String") : r)) === t || e._isBoolean &&
                    !0 != e ? (e = a["$is_a?"](null == (r = d.Object._scope.Array) ? d.cm("Array") : r)) === t || e._isBoolean && !0 != e ? u.$raise(null == (e = d.Object._scope.ArgumentError) ? d.cm("ArgumentError") : e, "Unsupported input type: " + a.$class()) : B = a.$dup() : B = a.$lines().$entries(); else {
                    try {
                        a.$rewind()
                    } catch (J) {
                        t
                    }
                    B = a.$readlines()
                } else B = a.$readlines(), C = a.$mtime(), a = (null == (e = d.Object._scope.File) ? d.cm("File") : e).$new((null == (e = d.Object._scope.File) ? d.cm("File") : e).$expand_path(a.$path())), T = a.$path(), y["$[]="]("docfile", T), y["$[]="]("docdir",
                    (null == (e = d.Object._scope.File) ? d.cm("File") : e).$dirname(T)), y["$[]="]("docname", (null == (e = d.Object._scope.File) ? d.cm("File") : e).$basename(T, (null == (e = d.Object._scope.File) ? d.cm("File") : e).$extname(T))), y["$[]="]("docdate", T = C.$strftime("%Y-%m-%d")), y["$[]="]("doctime", C = C.$strftime("%H:%M:%S %Z")), y["$[]="]("docdatetime", "" + T + " " + C);
                !1 !== w && w !== t && (w.$record("read"), w.$start("parse"));
                if ((e = b["$key?"]("parse")) === t || e._isBoolean && !0 != e)b["$[]="]("parse", !0);
                y = c.Document.$new(B, b);
                !1 !== w && w !== t &&
                w.$record("parse");
                return y
            };
            a._proto.$load_file = function (a, b) {
                var c;
                null == b && (b = v([], {}));
                return this.$load((null == (c = d.Object._scope.File) ? d.cm("File") : c).$new(!1 !== (c = a) && c !== t ? c : ""), b)
            };
            a._proto.$convert = function (a, b) {
                var e, g, h, k, m, n, q = t, r = t, u = t, w = t, y = t, x = t, A = t, D = t, B = y = t, C = t, T = q = A = w = D = x = r = q = D = x = B = t, J = u = t;
                null == b && (b = v([], {}));
                b = b.$dup();
                q = b.$delete("to_file");
                r = b.$delete("to_dir");
                u = !1 !== (e = b.$delete("mkdirs")) && e !== t ? e : !1;
                w = b["$[]"]("timings");
                y = q;
                (!0)["$==="](y) || t["$==="](y) ? (x = (e = r["$!"](),
                    !1 !== e && e !== t ? a["$is_a?"](null == (g = d.Object._scope.File) ? d.cm("File") : g) : e), A = !1, D = r, q = t) : (!1)["$==="](y) ? (D = A = x = !1, q = t) : (x = !1, A = q["$respond_to?"]("write"), D = !1 !== A && A !== t ? !1 : q);
                if ((e = (g = b["$key?"]("header_footer")["$!"](), !1 !== g && g !== t ? !1 !== (h = x) && h !== t ? h : D : g)) !== t && (!e._isBoolean || !0 == e))b["$[]="]("header_footer", !0);
                y = this.$load(a, b);
                if (q["$=="]("/dev/null"))return y;
                if (!1 !== x && x !== t)B = (null == (e = d.Object._scope.File) ? d.cm("File") : e).$expand_path(a.$path()), C = (null == (e = d.Object._scope.File) ? d.cm("File") :
                    e).$join((null == (e = d.Object._scope.File) ? d.cm("File") : e).$dirname(B), "" + y.$attributes()["$[]"]("docname") + y.$attributes()["$[]"]("outfilesuffix")), C["$=="](B) && this.$raise(null == (e = d.Object._scope.IOError) ? d.cm("IOError") : e, "Input file and output file are the same!"), B = (null == (e = d.Object._scope.File) ? d.cm("File") : e).$dirname(C); else if (!1 !== D && D !== t) {
                    if (x = (e = b["$has_key?"]("base_dir")) === t || e._isBoolean && !0 != e ? (null == (e = d.Object._scope.File) ? d.cm("File") : e).$expand_path((null == (e = d.Object._scope.Dir) ?
                        d.cm("Dir") : e).$pwd()) : (null == (e = d.Object._scope.File) ? d.cm("File") : e).$expand_path(b["$[]"]("base_dir")), D = y.$safe()["$>="](c.SafeMode._scope.SAFE) ? x : t, !1 !== r && r !== t ? (B = y.$normalize_system_path(r, x, D, v(["target_name", "recover"], {target_name: "to_dir", recover: !1})), !1 !== q && q !== t ? (C = y.$normalize_system_path(q, B, t, v(["target_name", "recover"], {target_name: "to_dir", recover: !1})), B = (null == (e = d.Object._scope.File) ? d.cm("File") : e).$dirname(C)) : C = (null == (e = d.Object._scope.File) ? d.cm("File") : e).$join(B, "" +
                        y.$attributes()["$[]"]("docname") + y.$attributes()["$[]"]("outfilesuffix"))) : !1 !== q && q !== t && (C = y.$normalize_system_path(q, x, D, v(["target_name", "recover"], {target_name: "to_dir", recover: !1})), B = (null == (e = d.Object._scope.File) ? d.cm("File") : e).$dirname(C)), (e = (null == (g = d.Object._scope.File) ? d.cm("File") : g)["$directory?"](B)) === t || e._isBoolean && !0 != e)!1 !== u && u !== t ? (null == (e = d.Object._scope.FileUtils) ? d.cm("FileUtils") : e).$mkdir_p(B) : this.$raise(null == (e = d.Object._scope.IOError) ? d.cm("IOError") : e, "target directory does not exist: " +
                        r)
                } else C = q, B = t;
                !1 !== w && w !== t && w.$start("convert");
                q = y.$convert();
                !1 !== w && w !== t && w.$record("convert");
                if (!1 !== C && C !== t) {
                    !1 !== w && w !== t && w.$start("write");
                    if (!1 === A || A === t)y.$attributes()["$[]="]("outfile", C), y.$attributes()["$[]="]("outdir", B);
                    y.$write(q, C);
                    !1 !== w && w !== t && w.$record("write");
                    (e = (g = (h = (k = (m = A["$!"](), !1 !== m && m !== t ? y.$safe()["$<"](c.SafeMode._scope.SECURE) : m), !1 !== k && k !== t ? y["$attr?"]("basebackend-html") : k), !1 !== h && h !== t ? y["$attr?"]("linkcss") : h), !1 !== g && g !== t ? y["$attr?"]("copycss") :
                        g)) === t || e._isBoolean && !0 != e || (r = c.DEFAULT_STYLESHEET_KEYS["$include?"](x = y.$attr("stylesheet")), D = (e = r["$!"](), !1 !== e && e !== t ? x["$nil_or_empty?"]()["$!"]() : e), w = (e = y["$attr?"]("source-highlighter", "coderay"), !1 !== e && e !== t ? y.$attr("coderay-css", "class")["$=="]("class") : e), A = (e = y["$attr?"]("source-highlighter", "pygments"), !1 !== e && e !== t ? y.$attr("pygments-css", "class")["$=="]("class") : e), (e = !1 !== (g = !1 !== (h = !1 !== (k = r) && k !== t ? k : D) && h !== t ? h : w) && g !== t ? g : A) === t || e._isBoolean && !0 != e || (B = y.$attr("outdir"), q =
                        y.$normalize_system_path(y.$attr("stylesdir"), B, y.$safe()["$>="](c.SafeMode._scope.SAFE) ? B : t), !1 !== u && u !== t && c.Helpers.$mkdir_p(q), !1 !== r && r !== t ? c.Stylesheets.$instance().$write_primary_stylesheet(q) : !1 !== D && D !== t && (T = (e = (T = y.$attr("copycss"))["$empty?"]()) === t || e._isBoolean && !0 != e ? y.$normalize_system_path(T) : y.$normalize_system_path(x), u = y.$normalize_system_path(x, q, y.$safe()["$>="](c.SafeMode._scope.SAFE) ? B : t), ((e = !1 !== (g = T["$=="](u)) && g !== t ? g : (J = y.$read_asset(T))["$nil?"]()) === t || e._isBoolean &&
                        !0 != e) && (e = (g = null == (h = d.Object._scope.File) ? d.cm("File") : h).$open, e._p = (n = function (a) {
                        null == a && (a = t);
                        return a.$write(J)
                    }, n._s = this, n), e).call(g, u, "w")), !1 !== w && w !== t ? c.Stylesheets.$instance().$write_coderay_stylesheet(q) : !1 !== A && A !== t && c.Stylesheets.$instance().$write_pygments_stylesheet(q, y.$attr("pygments-style"))));
                    return y
                }
                return q
            };
            a._proto.$render = a._proto.$convert;
            a._proto.$convert_file = function (a, b) {
                var c;
                null == b && (b = v([], {}));
                return this.$convert((null == (c = d.Object._scope.File) ? d.cm("File") :
                    c).$new(!1 !== (c = a) && c !== t ? c : ""), b)
            };
            return a._proto.$render_file = a._proto.$convert_file
        })(a.$singleton_class());
        m.RUBY_ENGINE["$=="]("opal")
    })(u);
    a = a = a = d.RUBY_ENGINE_OPAL;
    return!0
})(Opal);