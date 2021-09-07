libcairo
========

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/edadma/libcairo?include_prereleases) ![GitHub (Pre-)Release Date](https://img.shields.io/github/release-date-pre/edadma/libcairo) ![GitHub last commit](https://img.shields.io/github/last-commit/edadma/libcairo) ![GitHub](https://img.shields.io/github/license/edadma/libcairo)

*libcairo* provides Scala Native bindings for the [libcairo](http://www.libcairo.org/) C library for reading and writing
PNGs.

Overview
--------

The goal of this project is to provide an easy-to-use Scala Native facade for the majority *libcairo*, the official PNG
reference library. Currently, many of the functions needed to read and write PNGs are covered. Also, convenience methods
are provided to read into and write from an image buffer. The simplified API offered by the C library has not yet been
covered, but is planned.

The more "programmer friendly" part of this library is found in the `io.github.edadma.libcairo` package. That's the only
package you need to import from, as seen in the example below. The other package in the library
is `io.github.edadma.libcairo.extern` which provides for interaction with the libcairo C library using Scala Native
interoperability elements from the so-call `unsafe` namespace. There are no public declarations in
the `io.github.edadma.libcairo` package that use `unsafe` types in their parameter or return types, making it a pure
Scala facade. Consequently, you never have to worry about memory allocation or type conversions.

Usage
-----

To use this library, `libcairo-dev` needs to be installed:

```shell
sudo apt install libcairo-dev
```

Include the following in your `project/plugins.sbt`:

```sbt
addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.2")

```

Include the following in your `build.sbt`:

```sbt
resolvers += Resolver.githubPackages("edadma")

libraryDependencies += "io.github.edadma" %%% "libcairo" % "0.1.0"

```

Use the following `import` in your code:

```scala
import io.github.edadma.libcairo._

```

Examples
--------

The following examples are direct translations of examples from the Cairo documentation, specifically
their [tutorial](https://www.cairographics.org/tutorial/#L1drawing).

The following examples

```scala
```

Documentation
-------------

API documentation is forthcoming, however documentation for the C library is
found [here](https://www.cairographics.org/documentation/).

License
-------

[ISC](https://github.com/edadma/libcairo/blob/main/LICENSE)
