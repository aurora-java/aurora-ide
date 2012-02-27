var jslint;

/*
    There are preferences which can control jslint. They should be set before
    calling jslint().

        jslint.laxLineEnd = true if line breaks should not be checked
        jslint.plusplus   = true if post increment should not be allowed
        jslint.cap        = true if upper case HTML should be allowed
        jslint.jscript    = true if jscript deviations should be allowed

    jslint is a function. It takes one parameter, either a string or an
    array of strings. If the parameter is a string, it will be split on
    '\n'. If it is an array of strings, it is assumed that each string
    represents one line.

    	var myResult = jslint(mySource);

    If it checks out, it returns true. Otherwise, it returns false.

    If true, you can request a Function Report, which shows all of the functions
    and the parameters and vars that they use. This can be used to find
    implied global variables and other problems. The report is in HTML and
    can be inserted in a <body>.

        var myReport = jslint.report();

    If false, you can inspect jslint.error to find out the problem. jslint.error
    contains these members:

        line:      The line (relative to 0) at which the lint was found
        character: The character (relative to 0) at which the lint was found
        reason:    The problem
        evidence:  The text line in which the problem occurred

*/


String.prototype.isAlpha = function () {
    return (this >= 'a' && this <= 'z\uffff') ||
        (this >= 'A' && this <= 'Z\uffff');
};


String.prototype.isDigit = function () {
    return (this >= '0' && this <= '9');
};

(function () {

    var anonname, builtin, funlab, funstack, functions, inHTML, lex, lookahead,
        member, prevtoken, stack, syntax = {}, token, verb, tag = {
            a:        {end: true},
            abbr:     {end: true},
            acronym:  {end: true},
            address:  {end: true},
            applet:   {end: true},
            area:     {           parent: ' map '},
            b:        {end: true},
            base:     {           parent: ' head '},
            bdo:      {end: true},
            big:      {end: true},
            blockquote: {end: true},
            body:     {end: true, parent: ' html noframes '},
            br:       {},
            button:   {end: true},
            caption:  {end: true, parent: ' table '},
            center:   {end: true},
            cite:     {end: true},
            code:     {end: true},
            col:      {           parent: ' table colgroup '},
            colgroup: {end: true, parent: ' table '},
            dd:       {end: true, parent: ' dl '},
            del:      {end: true},
            dfn:      {end: true},
            div:      {end: true},
            dl:       {end: true},
            dt:       {end: true, parent: ' dl '},
            em:       {end: true},
            embed:    {end: true},
            fieldset: {end: true},
            font:     {end: true},
            form:     {end: true},
            frame:    {           parent: ' frameset '},
            frameset: {end: true, parent: ' html frameset '},
            h1:       {end: true},
            h2:       {end: true},
            h3:       {end: true},
            h4:       {end: true},
            h5:       {end: true},
            h6:       {end: true},
            head:     {end: true, parent: ' html '},
            html:     {end: true},
            hr:       {},
            i:        {end: true},
            iframe:   {end: true},
            img:      {},
            input:    {},
            ins:      {end: true},
            kbd:      {end: true},
            label:    {end: true},
            legend:   {end: true, parent: ' fieldset '},
            li:       {end: true, parent: ' dir menu ol ul '},
            link:     {           parent: ' head '},
            map:      {end: true},
            meta:     {           parent: ' head noscript '},
            noframes: {end: true, parent: ' html body '},
            noscript: {end: true},
            object:   {end: true},
            ol:       {end: true},
            optgroup: {end: true, parent: ' select '},
            option:   {end: true, parent: ' optgroup select '},
            p:        {end: true},
            param:    {           parent: ' applet object '},
            pre:      {end: true},
            q:        {end: true},
            samp:     {end: true},
            script:   {end: true, parent:
' head body p div span abbr acronym address bdo blockquote cite code del dfn em ins kbd pre samp strong th td var '},
            select:   {end: true},
            small:    {end: true},
            span:     {end: true},
            strong:   {end: true},
            style:    {end: true, parent: ' head ', special: true},
            sub:      {end: true},
            sup:      {end: true},
            table:    {end: true},
            tbody:    {end: true, parent: ' table '},
            td:       {end: true, parent: ' tr '},
            textarea: {end: true},
            tfoot:    {end: true, parent: ' table '},
            th:       {end: true, parent: ' tr '},
            thead:    {end: true, parent: ' table '},
            title:    {end: true, parent: ' head '},
            tr:       {end: true, parent: ' table tbody thead tfoot '},
            tt:       {end: true},
            u:        {end: true},
            ul:       {end: true},
            'var':    {end: true}
        },
// token
        tx = /^([(){}[\].,:;'"~]|\?>?|==?=?|\/(\*(global)*|=|)|\*[\/=]?|\+[+=]?|-[-=]?|%[=>]?|&[&=]?|\|[|=]?|>>?>?=?|<([\/=%\?]|\!(--)?|<=?)?|\^=?|\!=?=?|[a-zA-Z_$][a-zA-Z0-9_$]*|[0-9]+([xX][0-9a-fA-F]+|\.[0-9]*)?([eE][+-]?[0-9]+)?)/,
// string ending in single quote
        sx = /^((\\[^\x00-\x1f]|[^\x00-\x1f'\\])*)'/,
// string ending in double quote
        qx = /^((\\[^\x00-\x1f]|[^\x00-\x1f"\\])*)"/,
// regular expression
        rx = /^(\\[^\x00-\x1f]|\[(\\[^\x00-\x1f]|[^\x00-\x1f\\\/])*\]|[^\x00-\x1f\\\/\[])+\/[gim]*/,
// star slash
        lx = /\*\/|\/\*/,
// global identifier
        gx = /^([a-zA-Z_$][a-zA-Z0-9_$]*)/,
// global separators
        hx = /^[\x00-\x20,]*(\*\/)?/,
// whitespace
        wx = /^\s*(\/\/.*\r*$)?/;

    jslint = function (s) {
        functions = [];
        error.free = true;
        inHTML = false;
        stack = null;
        funlab = {};
        member = {};
        funstack = [];
        lookahead = [];
        lex = new Lex(s);
        builtin = {Array: 9, Boolean: 9, Date: 9, decodeURI: 9,
            decodeURIComponent: 9, encodeURI: 9, encodeURIComponent: 9,
            Error: 9, escape: 9, EvalError: 9, Function: 9, isFinite: 9,
            isNaN: 9, Math: 9, Number: 9, Object: 9, parseInt: 9,
            parseFloat: 9, RangeError: 9, ReferenceError: 9, RegExp: 9,
            String: 9, SyntaxError: 9, TypeError: 9, unescape: 9,
            URIError: 9};
        prevtoken = token = syntax['(begin)'];
        advance();
        try {
            if (token.value === '<' || token.value === '<!--' ||
                    token.value === '<%' || token.value === '<?' ||
                    token.value === '<!') {
                html(token.value);
            } else {
                statements();
                advance('(end)');
            }
        } catch (e) {
            jslint.error = error;
            return false;
        }
        jslint.error = null;
        return true;
    };


    function produce(o) {
        var r = function () {};
        r.prototype = o;
        return new r();
    }


// Lexical Analysis Constructor

    function Lex(lines, filename) {
        if (typeof lines === 'string') {
            lines = lines.split('\n');
            if (lines.length == 1) {
                lines = lines[0].split('\r');
            }
        }
        jslint.lines = lines;
        var line = 0,
            character = 0,
            from = 0,
            s = lines[0];

        function nextLine() {
            line += 1;
            if (line >= lines.length) {
                return false;
            }
            character = 0;
            s = lines[line];
            return true;
        }

// Public methods

// token -- this is called by advance to get the next token

        this.token = function () {
            function string(x) {
                r = x.exec(s);
                if (r) {
                    t = r[1];
                    l = r[0].length;
                    s = s.substr(l);
                    character += l;
                    if (stack) {
                        if (t.indexOf('<\/') >= 0) {
                            throw error(
'Expected "...<\\/..." and instead saw "...<\/...".', token);
                        }
                    }
                    return it('(string)', r[1]);
                } else {
                    if (!inHTML) {
                        throw error("Unclosed string: " + s,
                            line, character);
                    }
                    while (error.free) {
                        if (!nextLine()) {
                            throw error("Unclosed string.", token);
                        }
                        i = s.indexOf('"');
                        if (i >= 0) {
                            break;
                        }
                    }
                    s = s.substr(i + 1);
                    return it('(string)');
                }
            }

            var c, i, l, r, t;
            while (error.free) {
                if (!s) {
                    if (nextLine()) {
                        return it('(endline)', '');
                    } else {
                        return it('(end)', '');
                    }
                }
                r = wx.exec(s);
                if (!r || !r[0]) {
                    break;
                }
                l = r[0].length;
                s = s.substr(l);
                character += l;
                if (s) {
                    break;
                }
            }
            from = character;
            r = tx.exec(s);
            if (r) {
                t = r[0];
                l = t.length;
                s = s.substr(l);
                character += l;
                c = t.substr(0, 1);
                if (c.isAlpha() || c === '_' || c === '$') {
                    return it('(identifier)', t);
                }
                if (c.isDigit()) {
                    if (token.id === '.') {
                        throw error(
        "A decimal fraction should have a zero before the decimal point.",
                            token);
                    }
                    if (!isFinite(Number(t))) {
                        throw error("Bad number: '" + t + "'.",
                            line, character);
                    }
                    if (s.substr(0, 1).isAlpha()) {
                        throw error("Space is required after a number: '" +
                            t + "'.", line, character);
                    }
                    if (c === '0' && t.substr(1,1).isDigit()) {
                        throw error("Don't use extra leading zeros: '" +
                            t + "'.", line, character);
                    }
                    if (t.substr(t.length - 1) === '.') {
                        throw error(
"A trailing decimal point can be confused with a dot: '" + t + "'.",
                            line, character);
                    }
                    return it('(number)', t);
                }
                if (t === '"') {
                    return string(qx);
                }
                if (t === "'") {
                    return string(sx);
                }
                if (t === '/*') {
                    while (error.free) {
                        i = s.search(lx);
                        if (i >= 0) {
                            break;
                        }
                        if (!nextLine()) {
                            throw error("Unclosed comment.", token);
                        }
                    }
                    character += i + 2;
                    if (s.substr(i, 1) === '/') {
                        throw error("Nested comment.");
                    }
                    s = s.substr(i + 2);
                    return this.token();
                }
                if (t === '/*global') {
                    while (error.free) {
                        r = hx.exec(s);
                        if (r) {
                            l = r[0].length;
                            s = s.substr(l);
                            character += l;
                            if (r[1] === '*/') {
                                return this.token();
                            }
                        }
                        if (s) {
                            r = gx.exec(s);
                            if (r) {
                                l = r[0].length;
                                s = s.substr(l);
                                character += l;
                                builtin[r[1]] = 9;
                            } else {
                                throw error("Bad global identifier: '" +
                                    s + "'.", line, character);
                            }
                         } else if (!nextLine()) {
                            throw error("Unclosed comment.");
                        }
                    }
                }
                return it('(punctuator)', t);
            }
            throw error("Unexpected token: " + (t || s.substr(0, 1)),
                line, character);
        };


// skip -- skip past the next occurrence of a particular string.
// If the argument is empty, skip to just before the next '<' character.
// This is used to ignore HTML content.
// return false if it isn't found.

        this.skip = function (to) {
            if (token.id) {
                if (!to) {
                    to = '';
                    if (token.id.substr(0, 1) === '<') {
                        lookahead.push(token);
                        return true;
                    }
                } else if (token.id.indexOf(to) >= 0) {
                    return true;
                }
            }
            prevtoken = token;
            token = syntax['(error)'];
            while (error.free) {
                var i = s.indexOf(to || '<');
                if (i >= 0) {
                    character += i + to.length;
                    s = s.substr(i + to.length);
                    return true;
                }
                if (!nextLine()) {
                    break;
                }
            }
            return false;
        };

// regex -- this is called by parse when it sees '/' being used as a prefix.

        this.regex = function () {
            var l, r = rx.exec(s), x;
            if (r) {
                l = r[0].length;
                character += l;
                s = s.substr(l);
                x = r[1];
                return it('(regex)', x);
            }
            throw error("Bad regular expression: " + s);
        };


        function it(type, value) {
            var t;
            if (type === '(punctuator)') {
                t = syntax[value];
            } else if (type === '(identifier)') {
                t = syntax[value];
                if (!t || typeof t != 'object') {
                    t = syntax[type];
                }
            } else {
                t = syntax[type];
            }
            if (!t || typeof t != 'object') {
                throw error("Unrecognized symbol: '" + value + "' " + type);
            }
            t = produce(t);
            if (value || type === '(string)') {
                t.value = value;
            }
            t.line = line;
            t.character = character;
            t.from = from;
            t.filename = filename;
            return t;
        }
    }


    Lex.punctuators = {
        '(': 1, ')': true, '[': 1, ']': true, '{': 1, '}': 1,
        ',': 1, ';': 1, ':': 1, '?': 1, '!': 1, '.': 1,
        '|': 1, '^': 1, '&': 1, '+': 1, '-': 1, '*': 1, '/': 1,
        '%': 1, '=': 1, '<': 1, '>': 1, '+=': 1, '-=': 1,
        '*=': 1, '/=': 1, '%=': 1, '&=': 1, '|=': 1,
        '^=': 1, '||': 1, '&&': 1,
        '>>>': 1, '</': true, '<!--': true, '<<': 1, '>>': 1,
        '==': 1, '>>>=': 1, '<<=': 1, '>>=': 1,
        '!=': 1, '<=': 1, '>=': 1, '*/': true, '%>': true, '?>': true,
        '===': 1, '!==': 1, '~': 1, '(begin)': 1,
        '++': true, '--': true};




    var error = function (m, x, y) {
        var l, c;
        if (typeof x === 'number') {
            l = x;
            c = y || 0;
        } else if (x) {
            l = x.line;
            c = x.from;
        } else {
            l = token.line || 0;
            c = token.from || 0;
        }
        if (error.free) {
            error.free = false;
            error.id = '(error)';
            error.reason = m;
            error.evidence = jslint.lines[l] || '';
            error.line = l;
            error.character = c;
            token = syntax['(error)'];
            token.reason = error.reason;
        }
        return error;
    };


    function addlabel(t, type) {
        if (t) {
            if (t === 'arguments') {
                if (type === 'global' || type === 'var*') {
                    funlab[t] = 'parameter';
                    return;
                } else {
                    throw error("Incorrect use of 'arguments'.", prevtoken);
                }
            }
            if (typeof funlab[t] === 'string') {
                switch (funlab[t]) {
                case 'var':
                case 'var*':
                    if (type === 'global') {
                        funlab[t] = 'var*';
                        return;
                    }
                    break;
                case 'global':
                    if (type === 'var') {
                        throw error('Var ' + t +
                            ' was used before it was declared.', prevtoken);
                    }
                    if (type === 'var*' || type === 'global') {
                        return;
                    }
                    break;
                case 'function':
                case 'parameter':
                    if (type === 'global') {
                        return;
                    }
                    break;
                }
                throw error("Identifier '" + t + "' already declared as " +
                    funlab[t], prevtoken);
            }
            funlab[t] = type;
        }
    }


    function beginfunction(i) {
        var f = {'(name)': i, '(line)': token.line + 1, '(context)': funlab};
        funstack.push(funlab);
        funlab = f;
        functions.push(funlab);
    }


    function endfunction() {
        funlab = funstack.pop();
    }


    function parse(rbp, initial) {
        var l, left, o;
        if (token.id && token.id === '/') {
            if (prevtoken.id != '(' && prevtoken.id != '=' &&
                    prevtoken.id != ':' && prevtoken.id != ',' &&
                    prevtoken.id != '=') {
                throw error(
"Expected to see a '(' or '=' or ':' or ',' preceding a regular expression literal, and instead saw '" +
                    prevtoken.value + "'.", prevtoken);
            }
            advanceregex();
        }
        advance();
        if (initial) {
            anonname = 'anonymous';
            verb = prevtoken.value;
        }
        if (initial && prevtoken.fud) {
            prevtoken.fud();
        } else {
            if (prevtoken.nud) {
                o = prevtoken.exps;
                left = prevtoken.nud();
            } else {
                if (token.type === '(number)' && prevtoken.id === '.') {
                    throw error(
"A leading decimal point can be confused with a dot: ." + token.value,
                        prevtoken);
                }
                throw error("Expected an identifier and instead saw '" +
                    prevtoken.id + "'.", prevtoken);
            }
            while (rbp < token.lbp) {
                o = token.exps;
                advance();
                if (prevtoken.led) {
                    left = prevtoken.led(left);
                } else {
                    throw error("Expected an operator and instead saw '" +
                        prevtoken.id + "'.");
                }
            }
            if (initial && !o) {
                throw error(
"Expected an assignment or function call and instead saw an expression.",
                    prevtoken);
            }
        }
        if (l) {
            funlab[l] = 'label';
        }
    }


    function symbol(s, p) {
        return syntax[s] || (syntax[s] = {id: s, lbp: p, value: s});
    }


    function delim(s) {
        return symbol(s, 0);
    }


    function stmt(s, f) {
        var x = delim(s);
        x.identifier = x.reserved = true;
        x.fud = f;
        return x;
    }

    function blockstmt(s, f) {
        var x = stmt(s, f);
        x.block = true;
        return x;
    }

    function prefix(s, f) {
        var x = symbol(s, 150);
        x.nud = (typeof f === 'function') ? f : function () {
            parse(150);
            return this;
        };
        return x;
    }


    function type(s, f) {
        var x = delim(s);
        x.type = s;
        x.nud = f;
        return x;
    }


    function reserve(s, f) {
        var x = type(s, f);
        x.identifier = x.reserved = true;
        return x;
    }


    function reservevar(s) {
        return reserve(s, function () {
            return this;
        });
    }


    function infix(s, f, p) {
        var x = symbol(s, p);
        x.led = (typeof f === 'function') ? f : function (left) {
            return [f, left, parse(p)];
        };
        return x;
    }


    function assignop(s, f) {
        symbol(s, 20).exps = true;
        return infix(s, function (left) {
            if (left === true || (left.identifier && !left.reserved)) {
                parse(19);
                return left;
            }
            if (left == syntax['function']) {
                if (jslint.jscript) {
                    parse(19);
                    return left;
                } else {
                    throw error(
"Expected an identifier in an assignment, and instead saw a function invocation.",
                        prevtoken);
                }
            }
            throw error("Bad assignment.", this);
        }, 20);
    }


    function suffix(s, f) {
        var x = symbol(s, 150);
        x.led = function (left) {
            if (jslint.plusplus) {
                throw error("This operator is considered harmful: " + this.id,
                    this);
            }
            return [f, left];
        };
        return x;
    }


    function optionalidentifier() {
        if (token.reserved) {
            throw error("Expected an identifier and instead saw '" +
                token.id + "' (a reserved word).");
        }
        if (token.identifier && !token.reserved) {
            advance();
            return prevtoken.value;
        }
    }


    function identifier() {
        var i = optionalidentifier();
        if (i) {
            return i;
        }
        throw error("Expected an identifier and instead saw '" +
                token.value + "'.", token);
    }


    function reachable(s) {
        var i = 0, t;
        if (token.id != ';') {
            return;
        }
        while (error.free) {
            t = peek(i);
            if (t.reach) {
                return;
            }
            if (t.id != '(endline)') {
                if (t.id === 'function') {
                    throw error(
"Inner functions should be listed at the top of the outer function.", t);
                }
                throw error("Unreachable '" + t.value + "' after '" + s +
                    "'.", t);
            }
            i += 1;
        }
    }


    function statement() {
        var t = token;
        var isIdentifier = t.identifier;
        if (isIdentifier && !t.reserved) {
        	try {
	        	if (peek().id === ':') {
		            advance();
		            advance(':');
		            addlabel(t.value, 'live*');
		            if (!token.labelled) {
		                throw error("Label '" + t.value +
		                    "' on unlabelable statement '" + token.value + "'.",
		                    token);
		            }
		            token.label = t.value;
		            t = token;
		        }
            } catch (e) {
               // Ignore
            }
        }
        parse(0, true);
        if (!t.block && error.free) {
            if (token.id != ';') {
                throw error("Expected ';' and instead saw '" + token.value +
                    "'.", prevtoken.line,
                    prevtoken.from + prevtoken.value.length);
            }
            advance(';');
        }
    }


    function statements() {
        while (error.free && !token.reach) {
            statement();
        }
    }


    function block() {
        var t = token;
        advance('{');
        statements();
        advance('}', t);
        verb = null;
    }


    function idValue() {
        return this;
    }


    type('(number)', idValue);
    type('(string)', idValue);
    syntax['(identifier)'] = {
        type: '(identifier)',
        lbp: 0,
        identifier: true,
        nud: function () {
            addlabel(this.value, 'global');
            return this;
        },
        led: function () {
            throw error("Expected an operator and instead saw '" +
                token.value + "'.");
        }
    };

    type('(regex)', function () {
        return [this.id, this.value, this.flags];
    });

    delim('(endline)');
    delim('(begin)');
    delim('(end)').reach = true;
    delim('</').reach = true;
    delim('<%');
    delim('<?');
    delim('<!');
    delim('<!--');
    delim('%>');
    delim('?>');
    delim('(error)').reach = true;
    delim('}').reach = true;
    delim(')');
    delim(']');
    delim(';');
    delim(':').reach = true;
    delim(',');
    reserve('else');
    reserve('case').reach = true;
    reserve('default').reach = true;
    reserve('catch');
    reserve('finally');
    reservevar('this');
    reservevar('true');
    reservevar('false');
    reservevar('Infinity');
    reservevar('NaN');
    reservevar('null');
    reservevar('undefined');
    assignop('=', 'assign', 20);
    assignop('+=', 'assignadd', 20);
    assignop('-=', 'assignsub', 20);
    assignop('*=', 'assignmult', 20);
    assignop('/=', 'assigndiv', 20).nud = function () {
        throw error(
            "A regular expression literal can be confused with '/='.");
    };
    assignop('%=', 'assignmod', 20);
    assignop('&=', 'assignbitand', 20);
    assignop('|=', 'assignbitor', 20);
    assignop('^=', 'assignbitxor', 20);
    assignop('<<=', 'assignshiftleft', 20);
    assignop('>>=', 'assignshiftright', 20);
    assignop('>>>=', 'assignshiftrightunsigned', 20);
    infix('?', function (left) {
        parse(10);
        advance(':');
        parse(10);
    }, 30);

    infix('||', 'or', 40);
    infix('&&', 'and', 50);
    infix('|', 'bitor', 70);
    infix('^', 'bitxor', 80);
    infix('&', 'bitand', 90);
    infix('==', function (left) {
        var t = token;
        if (    (t.type === '(number)' && !+t.value) ||
                (t.type === '(string)' && !t.value) ||
                t.type === 'true' || t.type === 'false' ||
                t.type === 'undefined' || t.type === 'null') {
            // Comment-Out because this error confuses the user,  by Mamoru Emoto
            // throw error("Use '===' to compare with '" + t.value + "'.", t);
        }
        return ['==', left, parse(100)];
    }, 100);
    infix('===', 'equalexact', 100);
    infix('!=', function (left) {
        var t = token;
        if (    (t.type === '(number)' && !+t.value) ||
                (t.type === '(string)' && !t.value) ||
                t.type === 'true' || t.type === 'false' ||
                t.type === 'undefined' || t.type === 'null') {
            //throw error("Use '!==' to compare with '" + t.value + "'.", t);
        }
        return ['!=', left, parse(100)];
    }, 100);
    infix('!==', 'notequalexact', 100);
    infix('<', 'less', 110);
    infix('>', 'greater', 110);
    infix('<=', 'lessequal', 110);
    infix('>=', 'greaterequal', 110);
    infix('<<', 'shiftleft', 120);
    infix('>>', 'shiftright', 120);
    infix('>>>', 'shiftrightunsigned', 120);
    infix('in', 'in', 120);
    infix('instanceof', 'instanceof', 120);
    infix('+', 'addconcat', 130);
    prefix('+', 'num');
    infix('-', 'sub', 130);
    prefix('-', 'neg');
    infix('*', 'mult', 140);
    infix('/', 'div', 140);
    infix('%', 'mod', 140);

    suffix('++', 'postinc');
    prefix('++', 'preinc');
    syntax['++'].exps = true;

    suffix('--', 'postdec');
    prefix('--', 'predec');
    syntax['--'].exps = true;
    prefix('delete', function () {
        parse(0);
    }).exps = true;


    prefix('~', 'bitnot');
    prefix('!', 'not');
    prefix('typeof', 'typeof');
    prefix('new', function () {
        parse(150);
    });
    syntax['new'].exps = true;

    infix('.', function (left) {
        var m = identifier();
        if (typeof m === 'string') {
            if (typeof member[m] === 'number') {
                member[m] += 1;
            } else {
                member[m] = 1;
            }
        }
        return true;
    }, 160);

    infix('(', function (left) {
        if (token.id === ')') {
            advance(')');
        } else {
            while (error.free) {
                parse(10);
                if (token.id === ',') {
                    advance(',');
                } else {
                    advance(')', this);
                    break;
                }
            }
        }
        return syntax['function'];
    }, 160);
    syntax['('].exps = true;

    prefix('(', function () {
        parse(0);
        advance(')', this);
    });

    infix('[', function (left) {
        parse(0);
        advance(']', this);
        return true;
    }, 160);

    prefix('[', function () {
        if (token.id === ']') {
            advance(']');
            return;
        }
        while (error.free) {
            parse(10);
            if (token.id === ',') {
                advance(',');
                if (token.id === ']' || token.id === ',') {
                    throw error('Extra comma.', prevtoken);
                }
            } else {
                advance(']', this);
                return;
            }
        }
    }, 160);

    (function (x) {
        x.nud = function () {
            var i;
            if (token.id === '}') {
                advance('}');
                return;
            }
            while(error.free) {
                i = optionalidentifier(true);
                if (!i && (token.id === '(string)' || token.id === '(number)')) {
                    i = token.id;
                    advance();
                }
                if (!i) {
                    throw error("Expected an identifier and instead saw '" +
                        token.value + "'.");
                }
                advance(':');
                parse(10);
                if (token.id === ',') {
                    advance(',');
                } else {
                    advance('}', this);
                    return;
                }
            }
        };
        x.fud = function () {
            throw error(
                "Expected to see a statement and instead saw a block.");
        };
    })(delim('{'));


    function varstatement() {
        while (error.free) {
            addlabel(identifier(), 'var');
            if (token.id === '=') {
                advance('=');
                parse(20);
            }
            if (token.id === ',') {
                advance(',');
            } else {
                return;
            }
        }
    }


    stmt('var', varstatement);


    function functionparams() {
        var t = token;
        advance('(');
        if (token.id === ')') {
            advance(')');
            return;
        }
        while(error.free) {
            addlabel(identifier(), 'parameter');
            if (token.id === ',') {
                advance(',');
            } else {
                advance(')', t);
                return;
            }
        }
    }


    blockstmt('function', function () {
        var i = identifier();
        addlabel(i, 'var*');
        beginfunction(i);
        addlabel(i, 'function');
        functionparams();
        block();
        endfunction();
    });

    prefix('function', function () {
        var i = optionalidentifier() || ('"' + anonname + '"');
        beginfunction(i);
        addlabel(i, 'function');
        functionparams();
        block();
        endfunction();
    });

    blockstmt('if', function () {
        var t = token;
        advance('(');
        parse(20);
        advance(')', t);
        block();
        if (token.id === 'else') {
            advance('else');
            if (token.id === 'if' || token.id === 'switch') {
                statement();
            } else {
                block();
            }
        }
    });

    blockstmt('try', function () {
        var b;
        block();
        if (token.id === 'catch') {
            advance('catch');
            beginfunction('"catch"');
            functionparams();
            block();
            endfunction();
            b = true;
        }
        if (token.id === 'finally') {
            advance('finally');
            beginfunction('"finally"');
            block();
            endfunction();
            return;
        } else if (!b) {
            throw error("Expected 'catch' or 'finally' and instead saw '" +
                token.value + "'.");
        }
    });

    blockstmt('while', function () {
        var t= token;
        advance('(');
        parse(20);
        advance(')', t);
        block();
    }).labelled = true;

    reserve('with');

    blockstmt('switch', function () {
        var t = token;
        advance('(');
        var g = false;
        parse(20);
        advance(')', t);
        t = token;
        advance('{');
        while (error.free) {
            switch (token.id) {
            case 'case':
                switch (verb) {
                case 'break':
                case 'case':
                case 'continue':
                case 'return':
                case 'switch':
                case 'throw':
                    break;
                default:
                    throw error(
                        "Expected a 'break' statement before 'case'.",
                        prevtoken);
                }
                advance('case');
                parse(20);
                g = true;
                advance(':');
                verb = 'case';
                break;
            case 'default':
                switch (verb) {
                case 'break':
                case 'continue':
                case 'return':
                case 'throw':
                    break;
                default:
                    throw error(
                        "Expected a 'break' statement before 'default'.",
                        prevtoken);
                }
                advance('default');
                g = true;
                advance(':');
                break;
            case '}':
                advance('}', t);
                return;
            default:
                if (g) {
                    statements();
                } else {
                    throw error("Expected to see 'case' and instead saw '" +
                        token.value + "'.");
                }
            }
        }
    }).labelled = true;

    stmt('do', function () {
        block();
        advance('while');
        var t = token;
        advance('(');
        parse(20);
        advance(')', t);
    }).labelled = true;

    blockstmt('for', function () {
        var t = token;
        advance('(');
        if (peek(token.id === 'var' ? 1 : 0).id === 'in') {
            if (token.id === 'var') {
                advance('var');
                addlabel(identifier(), 'var');
            } else {
                advance();
            }
            advance('in');
            parse(20);
            advance(')', t);
            block();
            return;
        } else {
            if (token.id != ';') {
                if (token.id === 'var') {
                    advance('var');
                    varstatement();
                } else {
                    for (;;) {
                        parse(0);
                        if (token.id !== ',') {
                            break;
                        }
                        advance(',');
                    }
                }
            }
            advance(';');
            if (token.id != ';') {
                parse(20);
            }
            advance(';');
            if (token.id === ';') {
                throw error("Expected to see ')' and instead saw ';'");
            }
            if (token.id != ')') {
                for (;;) {
                    parse(0);
                    if (token.id !== ',') {
                        break;
                    }
                    advance(',');
                }
            }
            advance(')', t);
            block();
        }
    }).labelled = true;

    stmt('throw', function () {
        parse(20);
        reachable('throw');
    });

    stmt('return', function () {
        if (token.id != ';' && !token.reach) {
            parse(20);
        }
        reachable('return');
    });

    stmt('break', function () {
        if (funlab[token.value] === 'live*') {
            advance();
        }
        reachable('break');
    });


    stmt('continue', function () {
        if (funlab[token.id] === 'live*') {
            advance();
        }
        reachable('continue');
    });

// Future reserved words

    reserve('abstract');
    reserve('as');
    reserve('boolean');
    reserve('byte');
    reserve('char');
    reserve('class');
    reserve('const');
    reserve('debugger');
    reserve('double');
    reserve('enum');
    reserve('export');
    reserve('extends');
    reserve('final');
    reserve('float');
    reserve('goto');
    reserve('implements');
    reserve('import');
    reserve('int');
    reserve('interface');
    reserve('long');
    reserve('native');
    reserve('package');
    reserve('private');
    reserve('protected');
    reserve('public');
    reserve('short');
    reserve('static');
    reserve('super');
    reserve('synchronized');
    reserve('throws');
    reserve('transient');
    reserve('use');
    reserve('void');
    reserve('volatile');


    function Token(s) {
        this.id = s;
        this.lbp = 0;
        this.identifier = true;
        syntax[s] = this;
    }


    Token.prototype.nud = function () {
        addlabel(this.id, 'global');
        return this.id;
    };


    Token.prototype.led = function () {
        throw error("Expected an operator and instead saw '" +
            token.id + "'.");
    };

    function advance(id, t) {
        var l;
        switch (prevtoken.id) {
        case '(number)':
            if (token.id === '.') {
                throw error(
"A dot following a number can be confused with a decimal point.", prevtoken);
            }
            break;
        case '-':
            if (token.id === '-' || token.id === '--') {
                throw error("Confusing minusses.");
            }
            break;
        case '+':
            if (token.id === '+' || token.id === '++') {
                throw error("Confusing plusses.");
            }
            break;
        }
        if (prevtoken.type === '(string)' || prevtoken.identifier) {
            anonname = prevtoken.value;
        }

        if (id && token.value != id) {
            if (t) {
                if (token.id === '(end)') {
                    throw error("Unmatched '" + t.id + "'.", t);
                } else {
                    throw error("Expected '" + id + "' to match '" +
                        t.id + "' from line " + (t.line + 1) +
                        " and instead saw '" + token.value + "'.");
                }
            } else {
                throw error("Expected '" + id + "' and instead saw '" +
                    token.value + "'.");
            }
        }
        prevtoken = token;
        while (error.free) {
            token = lookahead.shift() || lex.token();
            if (token.id != '(endline)') {
                break;
            }
            l = Lex.punctuators[prevtoken.id] !== 1 && !jslint.laxLineEnd;
        }
        if (l && token.id != '{' && token.id != '}' && token.id != ']') {
            throw error(
                "Strict line ending error: '" +
                prevtoken.value + "'.", prevtoken);
        }
    }
    function advanceregex() {
        token = lex.regex();
    }

// We need a peek function. If it has an argument, it peeks that much farther
// ahead. It is used to distinguish
//      " for ( var i in ... "
// from
//      " for ( var i ; ... "

    function peek(i) {
        var j = 0, t;
        if (token == syntax['(error)']) {
            return token;
        }
        i = i || 0;
        while (j <= i) {
            t = lookahead[j];
            if (!t) {
                t = lookahead[j] = lex.token();
            }
            j += 1;
        }
        return t;
    }


    jslint.report = function () {
        var a = [], c, cc, f, i, k, o = '', s;

        function detail(h) {
            if (s.length) {
                return '<div>' + h + ':&nbsp; ' + s.sort().join(', ') +
                    '</div>';
            }
            return '';
        }

        for (k in member) {
            a.push(k);
        }
        if (a.length) {
            a = a.sort();
            for (i = 0; i < a.length; i += 1) {
                a[i] = '<tr><td><tt>' + a[i] + '</tt></td><td>' +
                    member[a[i]] + '</td></tr>';
            }
            o += '<table><tr><th>Members</th><th>Occurrences</th></tr>' +
                a.join('') + '</table>';
        }
        for (i = 0; i < functions.length; ++i) {
            f = functions[i];
            for (k in f) {
                if (f[k] === 'global') {
                    c = f['(context)'];
                    while (error.free) {
                        cc = c['(context)'];
                        if (!cc) {
                            if ((!funlab[k] || funlab[k] === 'var?') &&
                                    builtin[k] != 9) {
                                funlab[k] = 'var?';
                                f[k] = 'global?';
                            }
                            break;
                        }
                        if (c[k] === 'parameter!' || c[k] === 'var!') {
                            f[k] = 'var.';
                            break;
                        }
                        if (c[k] === 'var' || c[k] === 'var*' ||
                                c[k] === 'var!') {
                            f[k] = 'var.';
                            c[k] = 'var!';
                            break;
                        }
                        if (c[k] === 'parameter') {
                            f[k] = 'var.';
                            c[k] = 'parameter!';
                            break;
                        }
                        c = cc;
                    }
                }
            }
        }
        s = [];
        for (k in funlab) {
            if (funlab[k].substr(0, 3) === 'var') {
                if (funlab[k] === 'var?') {
                    s.push('<tt>' + k + '</tt><small>&nbsp;(?)</small>');
                } else {
                    s.push('<tt>' + k + '</tt>');
                }
            }
        }
        o += detail('Global');
        if (functions.length) {
            o += '<p>Function:</p><ol style="padding-left:0.5in">';
        }
        for (i = 0; i < functions.length; i += 1) {
            f = functions[i];
            o += '<li value=' +
                f['(line)'] + '><tt>' + (f['(name)'] || '') + '</tt>';
            s = [];
            for (k in f) {
                if (k.charAt(0) != '(') {
                    switch (f[k]) {
                    case 'parameter':
                        s.push('<tt>' + k + '</tt>');
                        break;
                    case 'parameter!':
                        s.push('<tt>' + k +
                            '</tt><small>&nbsp;(closure)</small>');
                        break;
                    }
                }
            }
            o += detail('Parameter');
            s = [];
            for (k in f) {
                if (k.charAt(0) != '(') {
                    switch(f[k]) {
                    case 'var':
                        s.push('<tt>' + k +
                            '</tt><small>&nbsp;(unused)</small>');
                        break;
                    case 'var*':
                        s.push('<tt>' + k + '</tt>');
                        break;
                    case 'var!':
                        s.push('<tt>' + k +
                            '</tt><small>&nbsp;(closure)</small>');
                        break;
                    case 'var.':
                        s.push('<tt>' + k +
                            '</tt><small>&nbsp;(outer)</small>');
                        break;
                    }
                }
            }
            o += detail('Var');
            s = [];
            c = f['(context)'];
            for (k in f) {
                if (k.charAt(0) != '(' && f[k].substr(0, 6) === 'global') {
                    if (f[k] === 'global?') {
                        s.push('<tt>' + k + '</tt><small>&nbsp;(?)</small>');
                    } else {
                        s.push('<tt>' + k + '</tt>');
                    }
                }
            }
            o += detail('Global');
            s = [];
            for (k in f) {
                if (k.charAt(0) != '(' && f[k] === 'label') {
                   s.push(k);
                }
            }
            o += detail('Label');
            o += '</li>';
        }
        if (functions.length) {
            o += '</ol>';
        }
        return o;
    };

    function inTag(tag) {
        for (var i = 0; i < stack.length; i += 1) {
            if (stack[i] == tag) {
                return true;
            }
        }
        return false;
    }


    function html(start) {
        var attribute, lax = jslint.laxLineEnd, p, src, h, t;
        inHTML = true;
        stack = [];
        while (error.free) {
            switch (start) {
            case '<':
                jslint.laxLineEnd = true;
                advance('<');
                h = token;
                if (h.type === '(identifier)' || h.identifier) {
                    if (jslint.cap) {
                        h.value = h.value.toLowerCase();
                    }
                    advance();
                    src = false;
                    while (error.free) {
                        if (token.id === '/') {
                            advance('/');
                            advance('>');
                            h.empty = true;
                            break;
                        }
                        if (token.id === '(end)') {
                            throw error('Very Bad HTML.', prevtoken);
                        }
                        if (token.id && token.id.substr(0, 1) === '>') {
                            break;
                        }
                        if (!token.identifier) {
                            throw error('Bad HTML: ' + token.value,
                                token);
                        }
                        attribute = token.value;
                        if (attribute === 'src' || attribute === 'SRC') {
                            src = true;
                        }
                        advance();
                        while (token.id === '-') {
                            advance();
                            if (!token.identifier) {
                                throw error('Bad HTML: ' + token.value,
                                    token);
                            }
                            attribute += '-' + token.value;
                            advance();
                        }
                        if (token.id === ':') {
                            advance(':');
                            if (!token.identifier) {
                                throw error('Bad attribute: ' + token.value,
                                    token);
                            }
                            attribute += ':' + token.value;
                            advance();
                        }
                        if (token.id === '=') {
                            advance('=');
                            if (!token.identifier &&
                                    token.type != '(string)' &&
                                    token.type != '(number)') {
                                throw error('Bad attribute: ' + token.value,
                                    token);
                            }
                            advance();
                        }
                    }
                    t = tag[h.value];
                    if (!t) {
                        throw error('Unrecognized HTML tag: <' + h.value +
                            '>. ' + (h.value != h.value.toLowerCase() ?
                            'Did you mean <' + h.value.toLowerCase() +
                            '>?' : ''),
                            h);
                    }
                    p = t.parent;
                    if (p) {
                        if (!stack.length) {
                            throw error('A <' + h.value +
                                '> must be within <' + p + '>');
                        }
                        if (p.indexOf(' ' +
                                stack[stack.length - 1].value +
                                ' ') < 0) {
                            throw error('A <' + h.value +
                                '> must be within <' + p + '>, not within <' +
                                stack[stack.length - 1].value + '>',
                                h);
                        }
                    } else if (h.value === 'html') {
                        if (stack.length) {
                            throw error('Misplaced <html> tag.', h);
                        }
                    } else if (!stack.length) {
                        throw error('Expected <html> and instead saw <' +
                            h.value + '>.', h);
                    } else if (stack.length < 2 || inTag('body')) {
                        throw error('Expected <' + h.value +
                            '> inside <body>.', h);
                    }
                    if (t.special) {
                        lex.skip('</' + h.value + '>');
                    } else if (t.end && !h.empty) {
                        stack.push(h);
                    }
                    if (h.value === 'script' && !src) {
                        advance();
                        jslint.laxLineEnd = lax;
                        inHTML = false;
                        statements();
                        advance('</');
                        advance('script');
                        if (token.id != '>') {
                            throw error("Expected '>' and instead saw '" +
                                token.id + "'.", token);
                        }
                        stack.pop();
                        jslint.laxLineEnd = lax;
                        inHTML = true;
                    }
                } else {
                    throw error("Expected '&lt;' and instead saw '<'.",
                        prevtoken);
                }
                break;
            case '</':
                advance('</');
                t = stack.pop();
                if (!t) {
                    throw error('Unexpected close tag: </' +
                        token.value + '>', token);
                }
                if (jslint.cap) {
                    token.value = token.value.toLowerCase();
                }
                if (t.value != token.value) {
                    throw error('Expected </' + t.value + '> (' +
                        (t.line + 1) + ') and saw </' + token.value + '>',
                        token);
                }
                advance();
                lex.skip('>');
                break;
            case '<!':
                jslint.laxLineEnd = true;
                while (error.free) {
                    advance();
                    if (token.id === '>') {
                        break;
                    }
                    if (token.id === '<' || token.id === '(end)') {
                        throw error("Missing '>'.", prevtoken);
                    }
                }
                lex.skip('>');
                break;
            case '<!--':
                lex.skip('-->');
                break;
            case '<%':
                lex.skip('%>');
                break;
            case '<?':
                jslint.laxLineEnd = true;
                while (error.free) {
                    advance();
                    if (token.id === '?>') {
                        break;
                    }
                    if (token.id === '<?' || token.id === '<' ||
                            token.id === '>' || token.id === '(end)') {
                        throw error("Missing '?>'.", prevtoken);
                    }
                }
                lex.skip('?>');
                break;
            case '<=':
            case '<<':
            case '<<=':
                throw error("Expected '&lt;'.");
            case '(end)':
                return;
            }
            if (!lex.skip('')) {
                if (stack.length) {
                    t = stack.pop();
                    throw error('Unmatched tag <' + t.value + '>', t);
                }
                return;
            }
            advance();
            start = token.value;
        }
    }
})();

jslint.laxLineEnd = false;
jslint.plusplus   = false;
jslint.cap        = false;
jslint.jscript    = false;
r = jslint(scriptText);
if (r) {
    jslint.report();
} else {
    var e = jslint.error;
    meReport.line = e.line + 1;
    meReport.character = e.character + 1;
    meReport.reason = e.reason;
    meReport.evidence = e.evidence;
	k= '$ME_JS_ERROR';
}