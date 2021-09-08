//import io.github.edadma.libcairo._
//
//object Main extends App {
//
//  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
//  val cr      = surface.create
//
//  cr.scale(120, 120)
//
//  cr.setSourceRGB(0, 0, 0)
//  cr.moveTo(0, 0)
//  cr.lineTo(1, 1)
//  cr.moveTo(1, 0)
//  cr.lineTo(0, 1)
//  cr.setLineWidth(0.2)
//  cr.stroke()
//
//  cr.rectangle(0, 0, 0.5, 0.5)
//  cr.setSourceRGBA(1, 0, 0, 0.80)
//  cr.fill()
//
//  cr.rectangle(0, 0.5, 0.5, 0.5)
//  cr.setSourceRGBA(0, 1, 0, 0.60)
//  cr.fill()
//
//  cr.rectangle(0.5, 0, 0.5, 0.5)
//  cr.setSourceRGBA(0, 0, 1, 0.40)
//  cr.fill()
//
//  surface.writeToPNG("setsourcergba.png")
//
//  cr.destroy()
//  surface.destroy()
//
//}

//import io.github.edadma.libcairo._
//
//object Main extends App {
//
//  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
//  val cr      = surface.create
//
//  /* Examples are in 1.0 x 1.0 coordinate space */
//  cr.scale(120, 120)
//
//  val radpat = patternCreateRadial(0.25, 0.25, 0.1, 0.5, 0.5, 0.5)
//
//  radpat.addColorStopRGB(0, 1.0, 0.8, 0.8)
//  radpat.addColorStopRGB(1, 0.9, 0.0, 0.0)
//
//  for (i <- 1 to 9; j <- 1 to 9)
//    cr.rectangle(i / 10.0 - 0.04, j / 10.0 - 0.04, 0.08, 0.08)
//
//  cr.setSource(radpat)
//  cr.fill()
//
//  val linpat = patternCreateLinear(0.25, 0.35, 0.75, 0.65)
//
//  linpat.addColorStopRGBA(0.00, 1, 1, 1, 0)
//  linpat.addColorStopRGBA(0.25, 0, 1, 0, 0.5)
//  linpat.addColorStopRGBA(0.50, 1, 1, 1, 0)
//  linpat.addColorStopRGBA(0.75, 0, 0, 1, 0.5)
//  linpat.addColorStopRGBA(1.00, 1, 1, 1, 0)
//
//  cr.rectangle(0.0, 0.0, 1, 1)
//  cr.setSource(linpat)
//  cr.fill()
//
//  surface.writeToPNG("setsourcegradient.png")
//
//  cr.destroy()
//  surface.destroy()
//
//}

//import math._
//
//import io.github.edadma.libcairo._
//
//object Main extends App {
//
//  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
//  val cr      = surface.create
//
//  cr.scale(120, 120)
//
//  cr.setLineWidth(0.1)
//  cr.setSourceRGB(0, 0, 0)
//
//  cr.moveTo(0.25, 0.25)
//  cr.lineTo(0.5, 0.375)
//  cr.relLineTo(0.25, -0.125)
//  cr.arc(0.5, 0.5, 0.25 * sqrt(2), -0.25 * Pi, 0.25 * Pi)
//  cr.relCurveTo(-0.25, -0.125, -0.25, 0.125, -0.5, 0)
//  cr.closePath()
//
//  cr.stroke()
//
//  surface.writeToPNG("path-close.png")
//  cr.destroy()
//  surface.destroy()
//
//}
