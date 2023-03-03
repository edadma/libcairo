//import io.github.edadma.libcairo._
//
//@main def run(): Unit =
//  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
//  val cr = surface.create
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

//import io.github.edadma.libcairo._
//
//@main def run(): Unit =
//  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
//  val cr = surface.create
//
//  /* Examples are in 1.0 x 1.0 coordinate space */
//  cr.scale(120, 120)
//
//  val radpat = patternCreateRadial(0.25, 0.25, 0.1, 0.5, 0.5, 0.5)
//
//  radpat.addColorStopRGB(0, 1.0, 0.8, 0.8)
//  radpat.addColorStopRGB(1, 0.9, 0.0, 0.0)
//
//  for i <- 1 to 9; j <- 1 to 9 do cr.rectangle(i / 10.0 - 0.04, j / 10.0 - 0.04, 0.08, 0.08)
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

//import math._
//
//import io.github.edadma.libcairo._
//
//@main def run(): Unit =
//  val surface = imageSurfaceCreate(Format.ARGB32, 120, 120)
//  val cr = surface.create
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

//import math._
//
//import io.github.edadma.libcairo._
//
//@main def run(): Unit =
//  /* Prepare drawing area */
//  val surface = imageSurfaceCreate(Format.ARGB32, 240, 240)
//  val cr = surface.create
//
//  /* Example is in 26.0 x 1.0 coordinate space */
//  cr.scale(240, 240)
//  cr.setFontSize(0.5)
//
//  /* Drawing code goes here */
//  cr.setSourceRGB(0.0, 0.0, 0.0)
//  cr.selectFontFace("Georgia", FontSlant.NORMAL, FontWeight.BOLD)
//
//  val fe = cr.fontExtents
//  val (ux, uy) = cr.deviceToUserDistance(1, 1)
//  val px = max(ux, uy)
//  val text = "joy"
//  val te = cr.textExtents(text)
//  val x = 0.5 - te.xBearing - te.width / 2
//  val y = 0.5 - fe.descent + fe.height / 2
//
//  /* baseline, descent, ascent, height */
//  cr.setLineWidth(4 * px)
//  cr.setDash(Seq(9 * px), 0)
//  cr.setSourceRGBA(0, 0.6, 0, 0.5)
//  cr.moveTo(x + te.xBearing, y)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y + fe.descent)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y - fe.ascent)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y - fe.height)
//  cr.relLineTo(te.width, 0)
//  cr.stroke()
//
//  /* extents: width & height */
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.setLineWidth(px)
//  cr.setDash(Seq(3 * px), 0)
//  cr.rectangle(x + te.xBearing, y + te.yBearing, te.width, te.height)
//  cr.stroke()
//
//  /* text */
//  cr.moveTo(x, y)
//  cr.setSourceRGB(0, 0, 0)
//  cr.showText(text)
//
//  /* bearing */
//  cr.setDash(Nil, 0)
//  cr.setLineWidth(2 * px)
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.moveTo(x, y)
//  cr.relLineTo(te.xBearing, te.yBearing)
//  cr.stroke()
//
//  /* text's advance */
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.arc(x + te.xAdvance, y + te.yAdvance, 5 * px, 0, 2 * Pi)
//  cr.fill()
//
//  /* reference point */
//  cr.arc(x, y, 5 * px, 0, 2 * Pi)
//  cr.setSourceRGBA(0.75, 0, 0, 0.5)
//  cr.fill()
//
//  /* Write output and clean up */
//  surface.writeToPNG("textextents.png")
//  cr.destroy()
//  surface.destroy()

//import math._
//
//import io.github.edadma.libcairo._
//
//@main def run(): Unit =
//  /* Prepare drawing area */
//  val surface = pdfSurfaceCreate("textextents.pdf", 612, 792)
//  val cr = surface.create
//
//  /* Example is in 26.0 x 1.0 coordinate space */
//  cr.scale(240, 240)
//  cr.setFontSize(0.5)
//
//  /* Drawing code goes here */
//  cr.setSourceRGB(0.0, 0.0, 0.0)
//  cr.selectFontFace("Georgia", FontSlant.NORMAL, FontWeight.BOLD)
//
//  val fe = cr.fontExtents
//  val (ux, uy) = cr.deviceToUserDistance(1, 1)
//  val px = max(ux, uy)
//  val text = "joy"
//  val te = cr.textExtents(text)
//  val x = 0.5 - te.xBearing - te.width / 2
//  val y = 0.5 - fe.descent + fe.height / 2
//
//  /* baseline, descent, ascent, height */
//  cr.setLineWidth(4 * px)
//  cr.setDash(Seq(9 * px), 0)
//  cr.setSourceRGBA(0, 0.6, 0, 0.5)
//  cr.moveTo(x + te.xBearing, y)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y + fe.descent)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y - fe.ascent)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y - fe.height)
//  cr.relLineTo(te.width, 0)
//  cr.stroke()
//
//  /* extents: width & height */
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.setLineWidth(px)
//  cr.setDash(Seq(3 * px), 0)
//  cr.rectangle(x + te.xBearing, y + te.yBearing, te.width, te.height)
//  cr.stroke()
//
//  /* text */
//  cr.moveTo(x, y)
//  cr.setSourceRGB(0, 0, 0)
//  cr.showText(text)
//
//  /* bearing */
//  cr.setDash(Nil, 0)
//  cr.setLineWidth(2 * px)
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.moveTo(x, y)
//  cr.relLineTo(te.xBearing, te.yBearing)
//  cr.stroke()
//
//  /* text's advance */
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.arc(x + te.xAdvance, y + te.yAdvance, 5 * px, 0, 2 * Pi)
//  cr.fill()
//
//  /* reference point */
//  cr.arc(x, y, 5 * px, 0, 2 * Pi)
//  cr.setSourceRGBA(0.75, 0, 0, 0.5)
//  cr.fill()
//
//  /* Write output and clean up */
//  cr.destroy()
//  surface.destroy()

//import math._
//
//import io.github.edadma.libcairo._
//import io.github.edadma.freetype._
//
//@main def run(): Unit =
//  /* Load font using FreeType */
//  val library = initFreeType.getOrElse(sys.error("error initializing library"))
//  val face = library
//    .newFace("/home/ed/dev/freetype/KaiseiDecol/KaiseiDecol-Regular.ttf", 0)
//    .getOrElse(sys.error("error loading face"))
//    .faceptr
//
//  /* Prepare drawing area */
//  val surface = pdfSurfaceCreate("test-ttf.pdf", 612, 792)
//  val cr = surface.create
//
//  /* Example is in 26.0 x 1.0 coordinate space */
//  cr.scale(220, 220)
//  cr.setFontSize(0.5)
//
//  /* Drawing code goes here */
//  cr.setSourceRGB(0.0, 0.0, 0.0)
//  cr.setFontFace(fontFaceCreateForFTFace(face, 0))
////  cr.selectFontFace("Georgia", FontSlant.NORMAL, FontWeight.BOLD)
//
//  val fe = cr.fontExtents
//  val (ux, uy) = cr.deviceToUserDistance(1, 1)
//  val px = max(ux, uy)
//  val text = "j[h"
//  val te = cr.textExtents(text)
//  val x = 0.5 - te.xBearing - te.width / 2
//  val y = 0.5 - fe.descent + fe.height / 2
//
//  /* baseline, descent, ascent, height */
//  cr.setLineWidth(4 * px)
//  cr.setDash(Seq(9 * px), 0)
//  cr.setSourceRGBA(0, 0.6, 0, 0.5)
//  cr.moveTo(x + te.xBearing, y)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y + fe.descent)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y - fe.ascent)
//  cr.relLineTo(te.width, 0)
//  cr.moveTo(x + te.xBearing, y - fe.height)
//  cr.relLineTo(te.width, 0)
//  cr.stroke()
//
//  /* extents: width & height */
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.setLineWidth(px)
//  cr.setDash(Seq(3 * px), 0)
//  cr.rectangle(x + te.xBearing, y + te.yBearing, te.width, te.height)
//  cr.stroke()
//
//  /* text */
//  cr.moveTo(x, y)
//  cr.setSourceRGB(0, 0, 0)
//  cr.showText(text)
//
//  /* bearing */
//  cr.setDash(Nil, 0)
//  cr.setLineWidth(2 * px)
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.moveTo(x, y)
//  cr.relLineTo(te.xBearing, te.yBearing)
//  cr.stroke()
//
//  /* text's advance */
//  cr.setSourceRGBA(0, 0, 0.75, 0.5)
//  cr.arc(x + te.xAdvance, y + te.yAdvance, 5 * px, 0, 2 * Pi)
//  cr.fill()
//
//  /* reference point */
//  cr.arc(x, y, 5 * px, 0, 2 * Pi)
//  cr.setSourceRGBA(0.75, 0, 0, 0.5)
//  cr.fill()
//
//  /* Write output and clean up */
//  cr.destroy()
//  surface.destroy()
