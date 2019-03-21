# CHANGELOG

## 2.0.0 (unreleased)

* Fix issues with code containing Unicode special characters
* Make the code more Robust by using the Jython API rather than `eval`uating
  generated Python code
* Guess the source languages if no lexer is given (#3)
* Bump Pygments from 2.1.3 to [2.3.1][pygments-changelog].

## 1.0.0 (2017/05/21)

* [breaking] Main exceptions handler removed: versions 0.1.1 and before wrapped
  the main code in a `try/catch` that would print the catched exception’s
  message and the stacktrace. This is useless; exceptions should be catched by
  the caller.
* Drop support for Java 6
* Bump Pygments from 1.6 to [2.1.3][pygments-changelog].

[pygments-changelog]: http://pygments.org/docs/changelog/

## 0.1.1 (2014/02/15)

* support for lexers & formatters options (#1)
* better handling of multi-lines code
* languages and outputs are case-insensitive (e.g. `:HTML` now works like
  `:html`)
* using Pygments [1.6][] instead of 1.5
* minor startup time improvement

[1.6]: https://bitbucket.org/birkenfeld/pygments-main/src/3e451a3806d9215bae592d9c28321076e5e046ef/CHANGES?at=default#cl-102

## 0.1.0 (2014/02/12)

initial version.
