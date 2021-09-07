libcairo
========

![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/edadma/libcairo?include_prereleases) ![GitHub (Pre-)Release Date](https://img.shields.io/github/release-date-pre/edadma/libcairo) ![GitHub last commit](https://img.shields.io/github/last-commit/edadma/libcairo) ![GitHub](https://img.shields.io/github/license/edadma/libcairo)

*libcairo* provides Scala Native bindings for the [Cairo](https://www.cairographics.org/) 2D graphics C library.

Overview
--------

The goal of this project is to provide an easy-to-use Scala Native facade for the entire Cairo 2D graphics library.
Currently, many of the basic functions needed to do graphcs, and to read and write PNGs are covered. The remainder of
the library will be steadily worked on.

The more "programmer friendly" part of this library is found in the `io.github.edadma.libcairo` package. That's the only
package you need to import from, as seen in the example below. The other package in the library
is `io.github.edadma.libcairo.extern` which provides for interaction with the *libcairo* C library using Scala Native
interoperability elements from the so-call `unsafe` namespace. There are no public declarations in
the `io.github.edadma.libcairo` package that use `unsafe` types in their parameter or return types, making it a pure
Scala facade. Consequently, you never have to worry about memory allocation or type conversions.

Usage
-----

To use this library, `libcairo2-dev` needs to be installed:

```shell
sudo apt install libcairo2-dev
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

### Example 1

This example creates a PNG file called `image1.png` containing the following image:

![image1](https://raw.githubusercontent.com/edadma/libcairo/main/image1.png)

```scala
import io.github.edadma.libcairo._

object Main extends App {

  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
  val cr = surface.create

  cr.scale(120, 120)

  cr.setSourceRGB(0, 0, 0)
  cr.moveTo(0, 0)
  cr.lineTo(1, 1)
  cr.moveTo(1, 0)
  cr.lineTo(0, 1)
  cr.setLineWidth(0.2)
  cr.stroke()

  cr.rectangle(0, 0, 0.5, 0.5)
  cr.setSourceRGBA(1, 0, 0, 0.80)
  cr.fill()

  cr.rectangle(0, 0.5, 0.5, 0.5)
  cr.setSourceRGBA(0, 1, 0, 0.60)
  cr.fill()

  cr.rectangle(0.5, 0, 0.5, 0.5)
  cr.setSourceRGBA(0, 0, 1, 0.40)
  cr.fill()

  surface.writeToPNG("image1.png")

  cr.destroy()
  surface.destroy()

}

```

### Example 2

This example creates a PNG file called `image2.png` containing the following image:

![image2](https://raw.githubusercontent.com/edadma/libcairo/main/image2.png)

```scala
import io.github.edadma.libcairo._

object Main extends App {

  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
  val cr = surface.create

  /* Examples are in 1.0 x 1.0 coordinate space */
  cr.scale(120, 120)

  val radpat = patternCreateRadial(0.25, 0.25, 0.1, 0.5, 0.5, 0.5)

  radpat.addColorStopRGB(0, 1.0, 0.8, 0.8)
  radpat.addColorStopRGB(1, 0.9, 0.0, 0.0)

  for (i <- 1 to 9; j <- 1 to 9)
    cr.rectangle(i / 10.0 - 0.04, j / 10.0 - 0.04, 0.08, 0.08)

  cr.setSource(radpat)
  cr.fill()

  val linpat = patternCreateLinear(0.25, 0.35, 0.75, 0.65)

  linpat.addColorStopRGBA(0.00, 1, 1, 1, 0)
  linpat.addColorStopRGBA(0.25, 0, 1, 0, 0.5)
  linpat.addColorStopRGBA(0.50, 1, 1, 1, 0)
  linpat.addColorStopRGBA(0.75, 0, 0, 1, 0.5)
  linpat.addColorStopRGBA(1.00, 1, 1, 1, 0)

  cr.rectangle(0.0, 0.0, 1, 1)
  cr.setSource(linpat)
  cr.fill()

  surface.writeToPNG("image2.png")

  cr.destroy()
  surface.destroy()

}

```

Documentation
-------------

API documentation is forthcoming, however documentation for the C library is
found [here](https://www.cairographics.org/documentation/).

License
-------

[ISC](https://github.com/edadma/libcairo/blob/main/LICENSE)
