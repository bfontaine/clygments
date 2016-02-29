# clygments

[![Build Status](https://img.shields.io/travis/bfontaine/clygments.svg)](https://travis-ci.org/bfontaine/clygments)
[![Coverage Status](https://img.shields.io/coveralls/bfontaine/clygments.svg)](https://coveralls.io/r/bfontaine/clygments)

**clygments** is a Clojure wrapper for [Pygments][].

[Pygments]: http://pygments.org/

## Usage

Add the dependency in your `project.clj`:

```clj
[clygments "0.1.1"]
```

Then:

```clj
(ns your-project.core
  (:use 'clygments.core))
```

It exposes only one function, `highlight`. Give it your code as a string, its
language and the desired output and it’ll do that for you :)

It’ll return a string or `nil` if there was an error, like an unsupported
language or output.

### Example

```clj
(highlight "(def x (+ 20 22))" :clojure :html)
;; => <div class=\"highlight\"><pre><span class=\"p\">(</span><span class=\"k\">def </span><span class=\"nv\">x</span> <span class=\"mi\">42</span><span class=\"p\">)</span>\n</pre></div>
```

## Support

### Langages

See [Pygments’ list][lexers] for a list of available languages.

### Output

* `:bbcode`
* `:html`
* `:latex`
* `:null` (no formatting, this leaves the code unchanged)
* `:raw`
* `:rtf`
* `:svg`
* `:terminal256`
* `:terminal`

Images outputs are [not][issue-2] supported. See also
[Pygments docs][formatters] for the full list.

[issue-2]: https://github.com/bfontaine/clygments/issues/2#issuecomment-35169407

### Options

All lexers’ and formatters’ options are supported since version 0.1.1. They are
given as a map to `highlight` and support hyphens for a better readability. See
Pygments’ docs for more info.

#### Example

```clj
;; expand tabs to 4 spaces
(highlight "def foo():\n\tpass" :python :html {:tab-size 4})

;; generate a full standalone HTML document with a custom title
(highlight "int i = 2+2;" :C :html {:full true, :title "This is my code"})
```

[lexers]: http://pygments.org/docs/lexers/
[formatters]: http://pygments.org/docs/formatters/

## License

Copyright © 2014-2016 Baptiste Fontaine

Distributed under the Eclipse Public License either version 1.0 or any later
version.
