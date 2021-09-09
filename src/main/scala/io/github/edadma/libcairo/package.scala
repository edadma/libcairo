package io.github.edadma

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
import scala.scalanative.libc.stdlib._

import io.github.edadma.libcairo.extern.{LibCairo => lib}

package object libcairo {

  implicit class Surface private[libcairo] (val surface: lib.cairo_surface_tp) {
    def create: Context = lib.cairo_create(surface)

    def destroy(): Unit = lib.cairo_surface_destroy(surface)

    def writeToPNG(filename: String): lib.cairo_status_t =
      Zone(implicit z => lib.cairo_surface_write_to_png(surface, toCString(filename)))
  }

  implicit class Context private[libcairo] (val cr: lib.cairo_tp) {
    def reference: Context = lib.cairo_reference(cr)

    def destroy(): Unit = lib.cairo_destroy(cr)

    def getReferenceCount: Int = lib.cairo_get_reference_count(cr).toInt

    def save(): Unit = lib.cairo_save(cr)

    def restore(): Unit = lib.cairo_restore(cr)

    def pushGroup(): Unit = lib.cairo_push_group(cr)

    def pushGroupWithContent(content: Content): Unit = lib.cairo_push_group_with_content(cr, content.value)

    def popGroup: Pattern = lib.cairo_pop_group(cr)

    def popGroupToSource(): Unit = lib.cairo_pop_group_to_source(cr)

    def setSource(source: Pattern): Unit = lib.cairo_set_source(cr, source.pattern)

    def setSourceRGB(red: Double, green: Double, blue: Double): Unit =
      lib.cairo_set_source_rgb(cr, red, green, blue)

    def setSourceRGBA(red: Double, green: Double, blue: Double, alpha: Double): Unit =
      lib.cairo_set_source_rgba(cr, red, green, blue, alpha)

    def setLineWidth(width: Double): Unit = lib.cairo_set_line_width(cr, width)

    def setLineJoin(line_join: LineJoin): Unit = lib.cairo_set_line_join(cr, line_join.value)

    def setDash(dashes: collection.Seq[Double], offset: Double): Unit = {
      val a = stackalloc[CDouble](dashes.length.toUInt)

      for ((d, i) <- dashes.zipWithIndex)
        a(i) = d

      lib.cairo_set_dash(cr, a, dashes.length, offset)
    }

    def translate(tx: Double, ty: Double): Unit = lib.cairo_scale(cr, tx, ty)

    def scale(sx: Double, sy: Double): Unit = lib.cairo_scale(cr, sx, sy)

    def deviceToUser(dx: Double, dy: Double): (Double, Double) = {
      val dxp = stackalloc[CDouble]
      val dyp = stackalloc[CDouble]

      !dxp = dx
      !dyp = dy
      lib.cairo_device_to_user(cr, dxp, dyp)
      (!dxp, !dyp)
    }

    def deviceToUserDistance(dx: Double, dy: Double): (Double, Double) = {
      val dxp = stackalloc[CDouble]
      val dyp = stackalloc[CDouble]

      !dxp = dx
      !dyp = dy
      lib.cairo_device_to_user_distance(cr, dxp, dyp)
      (!dxp, !dyp)
    }

    def moveTo(x: Double, y: Double): Unit = lib.cairo_move_to(cr, x, y)

    def lineTo(x: Double, y: Double): Unit = lib.cairo_line_to(cr, x, y)

    def arc(x: Double, y: Double, radius: Double, angle1: Double, angle2: Double): Unit =
      lib.cairo_arc(cr, x, y, radius, angle1, angle2)

    def relLineTo(x: Double, y: Double): Unit = lib.cairo_rel_line_to(cr, x, y)

    def relCurveTo(dx1: Double, dy1: Double, dx2: Double, dy2: Double, dx3: Double, dy3: Double): Unit =
      lib.cairo_rel_curve_to(cr, dx1, dy1, dx2, dy2, dx3, dy3)

    def rectangle(x: CDouble, y: CDouble, width: CDouble, height: CDouble): Unit =
      lib.cairo_rectangle(cr, x, y, width, height)

    def closePath(): Unit = lib.cairo_close_path(cr)

    def paint(): Unit = lib.cairo_paint(cr)

    def paintWithAlpha(alpha: Double): Unit = lib.cairo_paint_with_alpha(cr, alpha)

    def mask(source: Pattern): Unit = lib.cairo_mask(cr, source.pattern)

    def stroke(): Unit = lib.cairo_stroke(cr)

    def strokePreserve(): Unit = lib.cairo_stroke_preserve(cr)

    def fill(): Unit = lib.cairo_fill(cr)

    def fillPreserve(): Unit = lib.cairo_fill_preserve(cr)

    def strokeExtents: (Double, Double, Double, Double) = {
      val x1 = stackalloc[CDouble]
      val y1 = stackalloc[CDouble]
      val x2 = stackalloc[CDouble]
      val y2 = stackalloc[CDouble]

      lib.cairo_stroke_extents(cr, x1, y1, x2, y2)
      (!x1, !y1, !x2, !y2)
    }

    def resetClip(): Unit = lib.cairo_reset_clip(cr)

    def clip(): Unit = lib.cairo_clip(cr)

    def selectFontFace(family: String, slant: FontSlant, weight: FontWeight): Unit =
      Zone(implicit z => lib.cairo_select_font_face(cr, toCString(family), slant.value, weight.value))

    def setFontSize(size: Double): Unit = lib.cairo_set_font_size(cr, size)

    def setFontOptions(options: FontOptions): Unit = lib.cairo_set_font_options(cr, options.ptr)

    def getFontOptions(options: FontOptions): Unit = lib.cairo_get_font_options(cr, options.ptr)

    def showText(utf8: String): Unit = Zone(implicit z => lib.cairo_show_text(cr, toCString(utf8)))

    def textPath(utf8: String): Unit = Zone(implicit z => lib.cairo_text_path(cr, toCString(utf8)))

    def textExtents(utf8: String): TextExtents = Zone { implicit z =>
      val extents: TextExtentsOps = stackalloc[lib.cairo_text_extents_t]

      lib.cairo_text_extents(cr, toCString(utf8), extents.ptr)
      TextExtents(extents.xBearing, extents.yBearing, extents.width, extents.height, extents.xAdvance, extents.yAdvance)
    }

    def fontExtents: FontExtents = {
      val extents: FontExtentsOps = stackalloc[lib.cairo_font_extents_t]

      lib.cairo_font_extents(cr, extents.ptr)
      FontExtents(extents.ascent, extents.descent, extents.height, extents.maxXAdvance, extents.maxYAdvance)
    }
  }

  implicit class FontOptions private[libcairo] (val ptr: lib.cairo_font_options_tp) {}

  implicit class TextExtentsOps(val ptr: lib.cairo_text_extents_tp) extends AnyVal {
    def xBearing: Double = ptr._1

    def yBearing: Double = ptr._2

    def width: Double = ptr._3

    def height: Double = ptr._4

    def xAdvance: Double = ptr._5

    def yAdvance: Double = ptr._6
  }

  case class TextExtents(xBearing: Double,
                         yBearing: Double,
                         width: Double,
                         height: Double,
                         xAdvance: Double,
                         yAdvance: Double)

  implicit class FontExtentsOps(val ptr: lib.cairo_font_extents_tp) extends AnyVal {
    def ascent: Double = ptr._1

    def descent: Double = ptr._2

    def height: Double = ptr._3

    def maxXAdvance: Double = ptr._4

    def maxYAdvance: Double = ptr._5
  }

  case class FontExtents(ascent: Double, descent: Double, height: Double, maxXAdvance: Double, maxYAdvance: Double)

  implicit class Pattern private[libcairo] (val pattern: lib.cairo_pattern_tp) extends AnyVal {
    def addColorStopRGB(offset: Double, red: Double, green: Double, blue: Double): Unit =
      lib.cairo_pattern_add_color_stop_rgb(pattern, offset, red, green, blue)

    def addColorStopRGBA(offset: Double, red: Double, green: Double, blue: Double, alpha: Double): Unit =
      lib.cairo_pattern_add_color_stop_rgba(pattern, offset, red, green, blue, alpha)
  }

  implicit class Matrix private[libcairo] (val matrix: lib.cairo_matrix_tp) extends AnyVal {
    def matrixInit(xx: Double, yx: Double, xy: Double, yy: Double, x0: Double, y0: Double): Unit =
      lib.cairo_matrix_init(matrix, xx, yx, xy, yy, x0, y0)

    def initTranslate(tx: Double, ty: Double): Unit = lib.cairo_matrix_init_translate(matrix, tx, ty)

    def initScale(tx: Double, ty: Double): Unit = lib.cairo_matrix_init_scale(matrix, tx, ty)

    def initRotate(radians: Double): Unit = lib.cairo_matrix_init_rotate(matrix, radians)

    def translate(tx: Double, ty: Double): Unit = lib.cairo_matrix_translate(matrix, tx, ty)

    def scale(tx: Double, ty: Double): Unit = lib.cairo_matrix_scale(matrix, tx, ty)

    def rotate(radians: Double): Unit = lib.cairo_matrix_rotate(matrix, radians)

    def multiply(b: Matrix, result: Matrix): Unit = lib.cairo_matrix_multiply(result.matrix, matrix, b.matrix)

    def transformDistance(dx: CDouble, dy: CDouble): (Double, Double) = {
      val dxp = stackalloc[CDouble]
      val dyp = stackalloc[CDouble]

      !dxp = dx
      !dyp = dy
      lib.cairo_matrix_transform_distance(matrix, dxp, dyp)
      (!dxp, !dyp)
    }

    def transformPoint(x: CDouble, y: CDouble): (Double, Double) = {
      val xp = stackalloc[CDouble]
      val yp = stackalloc[CDouble]

      !xp = x
      !yp = y
      lib.cairo_matrix_transform_distance(matrix, xp, yp)
      (!xp, !yp)
    }

  }

  def imageSurfaceCreate(format: Format, width: Int, height: Int): Surface =
    lib.cairo_image_surface_create(format.value, width, height)

  def imageSurfaceCreateFromPNG(filename: String): Surface =
    Zone(implicit z => lib.cairo_image_surface_create_from_png(toCString(filename)))

  def patternCreateLinear(x0: Double, y0: Double, x1: Double, y1: Double): Pattern =
    lib.cairo_pattern_create_linear(x0, y0, x1, y1)

  def patternCreateRadial(cx0: Double,
                          cy0: Double,
                          radius0: Double,
                          cx1: Double,
                          cy1: Double,
                          radius1: Double): Pattern =
    lib.cairo_pattern_create_radial(cx0, cy0, radius0, cx1, cy1, radius1)

  // enums

  class Status(val value: CInt) extends AnyVal

  object Status {
    final val SUCCESS                   = new Status(0)
    final val NO_MEMORY                 = new Status(1)
    final val INVALID_RESTORE           = new Status(2)
    final val INVALID_POP_GROUP         = new Status(3)
    final val NO_CURRENT_POINT          = new Status(4)
    final val INVALID_MATRIX            = new Status(5)
    final val INVALID_STATUS            = new Status(6)
    final val NULL_POINTER              = new Status(7)
    final val INVALID_STRING            = new Status(8)
    final val INVALID_PATH_DATA         = new Status(9)
    final val READ_ERROR                = new Status(10)
    final val WRITE_ERROR               = new Status(11)
    final val SURFACE_FINISHED          = new Status(12)
    final val SURFACE_TYPE_MISMATCH     = new Status(13)
    final val PATTERN_TYPE_MISMATCH     = new Status(14)
    final val INVALID_CONTENT           = new Status(15)
    final val INVALID_FORMAT            = new Status(16)
    final val INVALID_VISUAL            = new Status(17)
    final val FILE_NOT_FOUND            = new Status(18)
    final val INVALID_DASH              = new Status(19)
    final val INVALID_DSC_COMMENT       = new Status(20)
    final val INVALID_INDEX             = new Status(21)
    final val CLIP_NOT_REPRESENTABLE    = new Status(22)
    final val TEMP_FILE_ERROR           = new Status(23)
    final val INVALID_STRIDE            = new Status(24)
    final val FONT_TYPE_MISMATCH        = new Status(25)
    final val USER_FONT_IMMUTABLE       = new Status(26)
    final val USER_FONT_ERROR           = new Status(27)
    final val NEGATIVE_COUNT            = new Status(28)
    final val INVALID_CLUSTERS          = new Status(29)
    final val INVALID_SLANT             = new Status(30)
    final val INVALID_WEIGHT            = new Status(31)
    final val INVALID_SIZE              = new Status(32)
    final val USER_FONT_NOT_IMPLEMENTED = new Status(33)
    final val DEVICE_TYPE_MISMATCH      = new Status(34)
    final val DEVICE_ERROR              = new Status(35)
    final val INVALID_MESH_CONSTRUCTION = new Status(36)
    final val DEVICE_FINISHED           = new Status(37)
    final val JBIG2_GLOBAL_MISSING      = new Status(38)
    final val PNG_ERROR                 = new Status(39)
    final val FREETYPE_ERROR            = new Status(40)
    final val WIN32_GDI_ERROR           = new Status(41)
    final val TAG_ERROR                 = new Status(42)
    final val LAST_STATUS               = new Status(43)
  }

  class Content(val value: lib.cairo_content_t) extends AnyVal

  object Content {
    final val COLOR       = new Content(0x1000)
    final val ALPHA       = new Content(0x2000)
    final val COLOR_ALPHA = new Content(0x3000)
  }

  class Format(val value: CInt) extends AnyVal

  object Format {
    final val INVALID   = new Format(-1)
    final val ARGB32    = new Format(0)
    final val RGB24     = new Format(1)
    final val A8        = new Format(2)
    final val A1        = new Format(3)
    final val RGB16_565 = new Format(4)
    final val RGB30     = new Format(5)
  }

  class Operator(val value: CInt) extends AnyVal

  object Operator {
    final val CLEAR          = new Operator(0)
    final val SOURCE         = new Operator(1)
    final val OVER           = new Operator(2)
    final val IN             = new Operator(3)
    final val OUT            = new Operator(4)
    final val ATOP           = new Operator(5)
    final val DEST           = new Operator(6)
    final val DEST_OVER      = new Operator(7)
    final val DEST_IN        = new Operator(8)
    final val DEST_OUT       = new Operator(9)
    final val DEST_ATOP      = new Operator(10)
    final val XOR            = new Operator(11)
    final val ADD            = new Operator(12)
    final val SATURATE       = new Operator(13)
    final val MULTIPLY       = new Operator(14)
    final val SCREEN         = new Operator(15)
    final val OVERLAY        = new Operator(16)
    final val DARKEN         = new Operator(17)
    final val LIGHTEN        = new Operator(18)
    final val COLOR_DODGE    = new Operator(19)
    final val COLOR_BURN     = new Operator(20)
    final val HARD_LIGHT     = new Operator(21)
    final val SOFT_LIGHT     = new Operator(22)
    final val DIFFERENCE     = new Operator(23)
    final val EXCLUSION      = new Operator(24)
    final val HSL_HUE        = new Operator(25)
    final val HSL_SATURATION = new Operator(26)
    final val HSL_COLOR      = new Operator(27)
    final val HSL_LUMINOSITY = new Operator(28)
  }

  class Antialias(val value: CInt) extends AnyVal

  object Antialias {
    final val DEFAULT  = new Antialias(0)
    final val NONE     = new Antialias(1)
    final val GRAY     = new Antialias(2)
    final val SUBPIXEL = new Antialias(3)
    final val FAST     = new Antialias(4)
    final val GOOD     = new Antialias(5)
    final val BEST     = new Antialias(6)
  }

  class FillRule(val value: CInt) extends AnyVal

  object FillRule {
    final val WINDING  = new FillRule(0)
    final val EVEN_ODD = new FillRule(1)
  }

  class LineCap(val value: CInt) extends AnyVal

  object LineCap {
    final val BUTT   = new LineCap(0)
    final val ROUND  = new LineCap(1)
    final val SQUARE = new LineCap(2)
  }

  class LineJoin(val value: lib.cairo_line_join_t) extends AnyVal

  object LineJoin {
    final val MITER = new LineJoin(0)
    final val ROUND = new LineJoin(1)
    final val BEVEL = new LineJoin(2)
  }

  class TextClusterFlags(val value: CInt) extends AnyVal

  object TextClusterFlags {
    final val BACKWARD = new TextClusterFlags(0x1)
  }

  class FontSlant(val value: CInt) extends AnyVal

  object FontSlant {
    final val NORMAL  = new FontSlant(0)
    final val ITALIC  = new FontSlant(1)
    final val OBLIQUE = new FontSlant(2)
  }

  class FontWeight(val value: CInt) extends AnyVal

  object FontWeight {
    final val NORMAL = new FontWeight(0)
    final val BOLD   = new FontWeight(1)
  }

  class SubpixelOrder(val value: CInt) extends AnyVal

  object SubpixelOrder {
    final val DEFAULT = new SubpixelOrder(0)
    final val RGB     = new SubpixelOrder(1)
    final val BGR     = new SubpixelOrder(2)
    final val VRGB    = new SubpixelOrder(3)
    final val VBGR    = new SubpixelOrder(4)
  }

  class HintStyle(val value: CInt) extends AnyVal

  object HintStyle {
    final val DEFAULT = new HintStyle(0)
    final val NONE    = new HintStyle(1)
    final val SLIGHT  = new HintStyle(2)
    final val MEDIUM  = new HintStyle(3)
    final val FULL    = new HintStyle(4)
  }

  class HintMetrics(val value: CInt) extends AnyVal

  object HintMetrics {
    final val DEFAULT = new HintMetrics(0)
    final val OFF     = new HintMetrics(1)
    final val ON      = new HintMetrics(2)
  }

  class FontType(val value: CInt) extends AnyVal

  object FontType {
    final val TOY    = new FontType(0)
    final val FT     = new FontType(1)
    final val WIN32  = new FontType(2)
    final val QUARTZ = new FontType(3)
    final val USER   = new FontType(4)
  }

  class PathDataType(val value: CInt) extends AnyVal

  object PathDataType {
    final val MOVE_TO    = new PathDataType(0)
    final val LINE_TO    = new PathDataType(1)
    final val CURVE_TO   = new PathDataType(2)
    final val CLOSE_PATH = new PathDataType(3)
  }

  class DeviceType(val value: CInt) extends AnyVal

  object DeviceType {
    final val DRM     = new DeviceType(0)
    final val GL      = new DeviceType(1)
    final val SCRIPT  = new DeviceType(2)
    final val XCB     = new DeviceType(3)
    final val XLIB    = new DeviceType(4)
    final val XML     = new DeviceType(5)
    final val COGL    = new DeviceType(6)
    final val WIN32   = new DeviceType(7)
    final val INVALID = new DeviceType(-1)
  }

  class SurfaceObserverMode(val value: CInt) extends AnyVal

  object SurfaceObserverMode {
    final val NORMAL            = new SurfaceObserverMode(0)
    final val RECORD_OPERATIONS = new SurfaceObserverMode(0x1)
  }

  class SurfaceType(val value: CInt) extends AnyVal

  object SurfaceType {
    final val IMAGE          = new SurfaceType(0)
    final val PDF            = new SurfaceType(1)
    final val PS             = new SurfaceType(2)
    final val XLIB           = new SurfaceType(3)
    final val XCB            = new SurfaceType(4)
    final val GLITZ          = new SurfaceType(5)
    final val QUARTZ         = new SurfaceType(6)
    final val WIN32          = new SurfaceType(7)
    final val BEOS           = new SurfaceType(8)
    final val DIRECTFB       = new SurfaceType(9)
    final val SVG            = new SurfaceType(10)
    final val OS2            = new SurfaceType(11)
    final val WIN32_PRINTING = new SurfaceType(12)
    final val QUARTZ_IMAGE   = new SurfaceType(13)
    final val SCRIPT         = new SurfaceType(14)
    final val QT             = new SurfaceType(15)
    final val RECORDING      = new SurfaceType(16)
    final val VG             = new SurfaceType(17)
    final val GL             = new SurfaceType(18)
    final val DRM            = new SurfaceType(19)
    final val TEE            = new SurfaceType(20)
    final val XML            = new SurfaceType(21)
    final val SKIA           = new SurfaceType(22)
    final val SUBSURFACE     = new SurfaceType(23)
    final val COGL           = new SurfaceType(24)
  }

  class PatternType(val value: CInt) extends AnyVal

  object PatternType {
    final val SOLID         = new PatternType(0)
    final val SURFACE       = new PatternType(1)
    final val LINEAR        = new PatternType(2)
    final val RADIAL        = new PatternType(3)
    final val MESH          = new PatternType(4)
    final val RASTER_SOURCE = new PatternType(5)
  }

  class Extend(val value: CInt) extends AnyVal

  object Extend {
    final val NONE    = new Extend(0)
    final val REPEAT  = new Extend(1)
    final val REFLECT = new Extend(2)
    final val PAD     = new Extend(3)
  }

  class Filter(val value: CInt) extends AnyVal

  object Filter {
    final val FAST     = new Filter(0)
    final val GOOD     = new Filter(1)
    final val BEST     = new Filter(2)
    final val NEAREST  = new Filter(3)
    final val BILINEAR = new Filter(4)
    final val GAUSSIAN = new Filter(5)
  }

  class RegionOverlap(val value: CInt) extends AnyVal

  object RegionOverlap {
    final val IN   = new RegionOverlap(0)
    final val OUT  = new RegionOverlap(1)
    final val PART = new RegionOverlap(2)
  }

}
