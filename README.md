# clygments

[![Build Status](https://travis-ci.org/bfontaine/clygments.png)](https://travis-ci.org/bfontaine/clygments)

**clygments** is a Clojure bridge for [Pygments][].

This is currently under development.

[Pygments]: http://pygments.org/

## Usage

Add the dependency in your `project.clj`:

```clj
[clygments "0.1.0-SNAPSHOT"]
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

Only text output is supported for now, this includes:

* `:bbcode`
* `:html`
* `:latex`
* `:null` (no formatting, this leaves the code unchanged)
* `:raw`
* `:rtf`
* `:svg`
* `:terminal256`
* `:terminal`

See also [Pygments’ list][formatters].

[lexers]: http://pygments.org/docs/lexers/
[formatters]: http://pygments.org/docs/formatters/

## License

Copyright © 2014 Baptiste Fontaine

Distributed under the Eclipse Public License either version 1.0 or any later
version.
