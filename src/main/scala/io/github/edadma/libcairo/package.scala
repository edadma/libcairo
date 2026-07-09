package io.github.edadma.libcairo

import io.github.edadma.freetype_face.FT_Face

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*
import scala.scalanative.libc.stdlib.*
import io.github.edadma.libcairo.extern.LibCairo as lib
import io.github.edadma.libcairo.extern.LibCairo.cairo_font_face_tp

implicit class Surface(val surface: lib.cairo_surface_tp):
  def create: Context = lib.cairo_create(surface)
  def destroy(): Unit = lib.cairo_surface_destroy(surface)
  def writeToPNG(filename: String): Status =
    Zone { new Status(lib.cairo_surface_write_to_png(surface, toCString(filename))) }

  /** Encode the surface as a PNG in memory, with no temp file. */
  def writeToPNGBytes: Array[Byte] = PngStream.synchronized {
    PngStream.writeBuffer = new scala.collection.mutable.ArrayBuffer[Byte]
    lib.cairo_surface_write_to_png_stream(surface, PngStream.writeFunc, null)
    val result = PngStream.writeBuffer.toArray
    PngStream.writeBuffer = null
    result
  }
  def showPage(): Unit = lib.cairo_surface_show_page(surface)

  /** Emit the current page like [[showPage]] but retain its content, so the next page starts
    * as a copy of this one. Paginated (PDF/PS) surfaces only. */
  def copyPage(): Unit = lib.cairo_surface_copy_page(surface)

  /** The error state of the surface. Cairo constructors never return null — they return a
    * surface in an error state — so check this after creating a surface from fallible input
    * (a PNG file, for instance). */
  def status: Status = new Status(lib.cairo_surface_status(surface))

  /** Finish the surface: flush all drawing and drop external resources — for a PDF surface,
    * write the trailer and close the file. The surface object survives (until [[destroy]])
    * but no longer accepts drawing. */
  def finish(): Unit = lib.cairo_surface_finish(surface)

  def getType: SurfaceType   = new SurfaceType(lib.cairo_surface_get_type(surface))
  def getContent: Content    = new Content(lib.cairo_surface_get_content(surface))
  def getReferenceCount: Int = lib.cairo_surface_get_reference_count(surface).toInt

  /** Create a surface compatible with this one (same backend) for drawing intermediate
    * content — the right way to make an offscreen buffer that composites efficiently back
    * onto this surface. */
  def createSimilar(content: Content, width: Int, height: Int): Surface =
    lib.cairo_surface_create_similar(surface, content.value, width, height)

  /** Create an image surface optimal for uploading to this surface. */
  def createSimilarImage(format: Format, width: Int, height: Int): Surface =
    lib.cairo_surface_create_similar_image(surface, format.value, width, height)

  def setDeviceOffset(xOffset: Double, yOffset: Double): Unit =
    lib.cairo_surface_set_device_offset(surface, xOffset, yOffset)
  def getDeviceOffset: (Double, Double) =
    val x = stackalloc[CDouble]()
    val y = stackalloc[CDouble]()

    lib.cairo_surface_get_device_offset(surface, x, y)
    (!x, !y)

  /** Scale between user space and backend pixels — the surface-side mechanism for HiDPI
    * rendering: a 2.0 scale draws everything at double resolution while user-space
    * coordinates stay in logical units. */
  def setDeviceScale(xScale: Double, yScale: Double): Unit =
    lib.cairo_surface_set_device_scale(surface, xScale, yScale)
  def getDeviceScale: (Double, Double) =
    val x = stackalloc[CDouble]()
    val y = stackalloc[CDouble]()

    lib.cairo_surface_get_device_scale(surface, x, y)
    (!x, !y)

  def getWidth: Int   = lib.cairo_image_surface_get_width(surface)
  def getHeight: Int  = lib.cairo_image_surface_get_height(surface)
  def getFormat: Format = new Format(lib.cairo_image_surface_get_format(surface))

  /** A pointer to the surface's pixel buffer. For a [[Format.ARGB32]] surface each pixel
    * is a native-endian 32-bit `0xAARRGGBB` with premultiplied alpha, packed in rows of
    * [[getStride]] bytes. Call [[flush]] before reading after drawing through a context,
    * and do not draw to the surface again until done reading. Valid for the surface's
    * lifetime. */
  def getData: Ptr[Byte] = lib.cairo_image_surface_get_data(surface)

  /** The number of bytes between the start of consecutive rows in [[getData]]. Cairo pads
    * rows for alignment, so this can exceed `width * 4` — always stride a row by this, not
    * by the pixel width. */
  def getStride: Int = lib.cairo_image_surface_get_stride(surface)

  /** Flush any pending drawing so the pixel buffer reflects every prior operation. Must be
    * called before reading [[getData]] when the surface has been drawn to through a
    * [[Context]]. */
  def flush(): Unit = lib.cairo_surface_flush(surface)

  /** Tell Cairo the pixel buffer was modified directly (through [[getData]]) outside of any
    * [[Context]] drawing. This invalidates Cairo's internal snapshots so the next operation
    * that uses the surface — as a source via [[Context.setSourceSurface]], for instance —
    * sees the new pixels rather than a stale cached copy. Pair it with [[flush]]: `flush`
    * before reading or writing the buffer, `markDirty` after writing, before drawing with the
    * surface again. */
  def markDirty(): Unit = lib.cairo_surface_mark_dirty(surface)

  /** Like [[markDirty]] but limited to a rectangle (in surface coordinates) — cheaper when
    * only part of the buffer was written. */
  def markDirtyRectangle(x: Int, y: Int, width: Int, height: Int): Unit =
    lib.cairo_surface_mark_dirty_rectangle(surface, x, y, width, height)

  def reference: Surface = lib.cairo_surface_reference(surface)

  // PDF surfaces only — these are silent no-ops (or put the surface in an error state) on
  // other surface types.

  /** Restrict the generated PDF to a specific version of the PDF specification. Must be
    * called before any drawing. */
  def restrictToVersion(version: PdfVersion): Unit =
    lib.cairo_pdf_surface_restrict_to_version(surface, version.value)

  /** Change the page size, in points, for subsequent pages. Call before drawing anything on
    * the new page. */
  def setSize(widthInPoints: Double, heightInPoints: Double): Unit =
    lib.cairo_pdf_surface_set_size(surface, widthInPoints, heightInPoints)

  /** Add an item to the document's outline (the bookmark tree shown in a PDF viewer's
    * sidebar). `parentId` is [[PdfOutline.ROOT]] for a top-level item or the id returned by a
    * previous call for a nested one. `linkAttribs` takes the same attribute syntax as a
    * [[Tags.LINK]] tag — e.g. `"dest='chapter1'"` or `"page=3 pos=[0 792]"` — and names where
    * the item jumps to. Returns the id of the new item. */
  def addOutline(parentId: Int, utf8: String, linkAttribs: String, flags: PdfOutlineFlags): Int = Zone {
    lib.cairo_pdf_surface_add_outline(surface, parentId, toCString(utf8), toCString(linkAttribs), flags.value)
  }

  /** Set a standard document-information field ([[PdfMetadata.TITLE]], AUTHOR, …). Dates use
    * ISO-8601: `2026-06-12T10:30:00Z`. */
  def setMetadata(metadata: PdfMetadata, utf8: String): Unit = Zone {
    lib.cairo_pdf_surface_set_metadata(surface, metadata.value, toCString(utf8))
  }

  /** Set an arbitrary named document-information field. */
  def setCustomMetadata(name: String, value: String): Unit = Zone {
    lib.cairo_pdf_surface_set_custom_metadata(surface, toCString(name), toCString(value))
  }

  /** Set the label the viewer displays for the current page — `"iv"`, `"Appendix A"` — in
    * place of the plain page number. Applies to this page and those after it until set
    * again. */
  def setPageLabel(utf8: String): Unit = Zone {
    lib.cairo_pdf_surface_set_page_label(surface, toCString(utf8))
  }

  /** Set the dimensions of the embedded thumbnail image generated for the current and
    * subsequent pages; 0×0 (the default) emits no thumbnails. */
  def setThumbnailSize(width: Int, height: Int): Unit =
    lib.cairo_pdf_surface_set_thumbnail_size(surface, width, height)

implicit class Context private[libcairo] (val cr: lib.cairo_tp) extends AnyVal:
  def reference: Context                           = lib.cairo_reference(cr)
  def destroy(): Unit                              = lib.cairo_destroy(cr)
  def newPath(): Unit                              = lib.cairo_new_path(cr)
  def getReferenceCount: Int                       = lib.cairo_get_reference_count(cr).toInt
  def save(): Unit                                 = lib.cairo_save(cr)
  def restore(): Unit                              = lib.cairo_restore(cr)
  def pushGroup(): Unit                            = lib.cairo_push_group(cr)
  def pushGroupWithContent(content: Content): Unit = lib.cairo_push_group_with_content(cr, content.value)
  def popGroup: Pattern                            = lib.cairo_pop_group(cr)
  def popGroupToSource(): Unit                     = lib.cairo_pop_group_to_source(cr)
  def setSource(source: Pattern): Unit             = lib.cairo_set_source(cr, source.pattern)
  def setOperator(op: Operator): Unit              = lib.cairo_set_operator(cr, op.value)
  def setSourceRGB(red: Double, green: Double, blue: Double): Unit =
    lib.cairo_set_source_rgb(cr, red, green, blue)
  def setSourceRGBA(red: Double, green: Double, blue: Double, alpha: Double): Unit =
    lib.cairo_set_source_rgba(cr, red, green, blue, alpha)
  def setLineWidth(width: Double): Unit      = lib.cairo_set_line_width(cr, width)
  def setLineJoin(line_join: LineJoin): Unit = lib.cairo_set_line_join(cr, line_join.value)
  def setLineCap(line_cap: LineCap): Unit    = lib.cairo_set_line_cap(cr, line_cap.value)
  def setDash(dashes: collection.Seq[Double], offset: Double): Unit =
    val a = stackalloc[CDouble](dashes.length.toUInt)

    for (d, i) <- dashes.zipWithIndex do a(i) = d

    lib.cairo_set_dash(cr, a, dashes.length, offset)
  def translate(tx: Double, ty: Double): Unit = lib.cairo_translate(cr, tx, ty)
  def scale(sx: Double, sy: Double): Unit     = lib.cairo_scale(cr, sx, sy)
  def rotate(angle: Double): Unit             = lib.cairo_rotate(cr, angle)
  def identityMatrix(): Unit                  = lib.cairo_identity_matrix(cr)
  def deviceToUser(dx: Double, dy: Double): (Double, Double) =
    val dxp = stackalloc[CDouble]()
    val dyp = stackalloc[CDouble]()

    !dxp = dx
    !dyp = dy
    lib.cairo_device_to_user(cr, dxp, dyp)
    (!dxp, !dyp)
  def deviceToUserDistance(dx: Double, dy: Double): (Double, Double) =
    val dxp = stackalloc[CDouble]()
    val dyp = stackalloc[CDouble]()

    !dxp = dx
    !dyp = dy
    lib.cairo_device_to_user_distance(cr, dxp, dyp)
    (!dxp, !dyp)
  def moveTo(x: Double, y: Double): Unit      = lib.cairo_move_to(cr, x, y)
  def relMoveTo(dx: Double, dy: Double): Unit = lib.cairo_rel_move_to(cr, dx, dy)
  def lineTo(x: Double, y: Double): Unit      = lib.cairo_line_to(cr, x, y)
  def arc(x: Double, y: Double, radius: Double, angle1: Double, angle2: Double): Unit =
    lib.cairo_arc(cr, x, y, radius, angle1, angle2)
  def arcNegative(x: Double, y: Double, radius: Double, angle1: Double, angle2: Double): Unit =
    lib.cairo_arc_negative(cr, x, y, radius, angle1, angle2)
  def relLineTo(x: Double, y: Double): Unit = lib.cairo_rel_line_to(cr, x, y)
  def relCurveTo(dx1: Double, dy1: Double, dx2: Double, dy2: Double, dx3: Double, dy3: Double): Unit =
    lib.cairo_rel_curve_to(cr, dx1, dy1, dx2, dy2, dx3, dy3)

  def rectangle(x: Double, y: Double, width: Double, height: Double): Unit =
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
    val x1 = stackalloc[CDouble]()
    val y1 = stackalloc[CDouble]()
    val x2 = stackalloc[CDouble]()
    val y2 = stackalloc[CDouble]()

    lib.cairo_stroke_extents(cr, x1, y1, x2, y2)
    (!x1, !y1, !x2, !y2)
  }

  def resetClip(): Unit = lib.cairo_reset_clip(cr)

  def clip(): Unit = lib.cairo_clip(cr)

  def selectFontFace(family: String, slant: FontSlant, weight: FontWeight): Unit =
    Zone { lib.cairo_select_font_face(cr, toCString(family), slant.value, weight.value) }

  def setFontSize(size: Double): Unit            = lib.cairo_set_font_size(cr, size)
  def setFontOptions(options: FontOptions): Unit = lib.cairo_set_font_options(cr, options.ptr)
  def getFontOptions(options: FontOptions): Unit = lib.cairo_get_font_options(cr, options.ptr)
  def showText(utf8: String): Unit               = Zone { lib.cairo_show_text(cr, toCString(utf8)) }
  def textPath(utf8: String): Unit               = Zone { lib.cairo_text_path(cr, toCString(utf8)) }
  def textExtents(utf8: String): TextExtents = Zone {
    val extents: TextExtentsOps = stackalloc[lib.cairo_text_extents_t]()

    lib.cairo_text_extents(cr, toCString(utf8), extents.ptr)
    TextExtents(extents.xBearing, extents.yBearing, extents.width, extents.height, extents.xAdvance, extents.yAdvance)
  }
  def fontExtents: FontExtents =
    val extents: FontExtentsOps = stackalloc[lib.cairo_font_extents_t]()

    lib.cairo_font_extents(cr, extents.ptr)
    FontExtents(extents.ascent, extents.descent, extents.height, extents.maxXAdvance, extents.maxYAdvance)
  def setSourceSurface(surface: Surface, x: Double, y: Double): Unit =
    lib.cairo_set_source_surface(cr, surface.surface, x, y)
  def setTolerance(tolerance: Double): Unit       = lib.cairo_set_tolerance(cr, tolerance)
  def showPage(): Unit                            = lib.cairo_show_page(cr)
  def setFontFace(fontFace: FontFace): Unit       = lib.cairo_set_font_face(cr, fontFace.fontfaceptr)
  def setScaledFont(scaledFont: ScaledFont): Unit = lib.cairo_set_scaled_font(cr, scaledFont.scaledfontptr)
  def getScaledFont: ScaledFont                   = lib.cairo_get_scaled_font(cr)
  def pathExtents: (Double, Double, Double, Double) =
    val x1 = stackalloc[CDouble]()
    val y1 = stackalloc[CDouble]()
    val x2 = stackalloc[CDouble]()
    val y2 = stackalloc[CDouble]()

    lib.cairo_path_extents(cr, x1, y1, x2, y2)
    (!x1, !y1, !x2, !y2)

  def fillExtents: (Double, Double, Double, Double) =
    val x1 = stackalloc[CDouble]()
    val y1 = stackalloc[CDouble]()
    val x2 = stackalloc[CDouble]()
    val y2 = stackalloc[CDouble]()

    lib.cairo_fill_extents(cr, x1, y1, x2, y2)
    (!x1, !y1, !x2, !y2)
  def newSubPath(): Unit = lib.cairo_new_sub_path(cr)

  /** The error state of the context. Cairo never returns errors from drawing calls — an
    * error makes the context inert and sticks until checked here. */
  def status: Status = new Status(lib.cairo_status(cr))

  def curveTo(x1: Double, y1: Double, x2: Double, y2: Double, x3: Double, y3: Double): Unit =
    lib.cairo_curve_to(cr, x1, y1, x2, y2, x3, y3)
  def hasCurrentPoint: Boolean = lib.cairo_has_current_point(cr) != 0
  def getCurrentPoint: (Double, Double) =
    val x = stackalloc[CDouble]()
    val y = stackalloc[CDouble]()

    lib.cairo_get_current_point(cr, x, y)
    (!x, !y)

  def setFillRule(fillRule: FillRule): Unit   = lib.cairo_set_fill_rule(cr, fillRule.value)
  def getFillRule: FillRule                   = new FillRule(lib.cairo_get_fill_rule(cr))
  def setAntialias(antialias: Antialias): Unit = lib.cairo_set_antialias(cr, antialias.value)
  def getAntialias: Antialias                 = new Antialias(lib.cairo_get_antialias(cr))
  def setMiterLimit(limit: Double): Unit      = lib.cairo_set_miter_limit(cr, limit)
  def getMiterLimit: Double                   = lib.cairo_get_miter_limit(cr)
  def getLineWidth: Double                    = lib.cairo_get_line_width(cr)
  def getLineCap: LineCap                     = new LineCap(lib.cairo_get_line_cap(cr))
  def getLineJoin: LineJoin                   = new LineJoin(lib.cairo_get_line_join(cr))
  def getOperator: Operator                   = new Operator(lib.cairo_get_operator(cr))
  def getTolerance: Double                    = lib.cairo_get_tolerance(cr)
  def getSource: Pattern                      = lib.cairo_get_source(cr)
  def getTarget: Surface                      = lib.cairo_get_target(cr)
  def getGroupTarget: Surface                 = lib.cairo_get_group_target(cr)

  def clipPreserve(): Unit = lib.cairo_clip_preserve(cr)
  def clipExtents: (Double, Double, Double, Double) =
    val x1 = stackalloc[CDouble]()
    val y1 = stackalloc[CDouble]()
    val x2 = stackalloc[CDouble]()
    val y2 = stackalloc[CDouble]()

    lib.cairo_clip_extents(cr, x1, y1, x2, y2)
    (!x1, !y1, !x2, !y2)
  def inClip(x: Double, y: Double): Boolean   = lib.cairo_in_clip(cr, x, y) != 0
  def inFill(x: Double, y: Double): Boolean   = lib.cairo_in_fill(cr, x, y) != 0
  def inStroke(x: Double, y: Double): Boolean = lib.cairo_in_stroke(cr, x, y) != 0

  def transform(matrix: Matrix): Unit = lib.cairo_transform(cr, matrix.matrix)
  def setMatrix(matrix: Matrix): Unit = lib.cairo_set_matrix(cr, matrix.matrix)
  def getMatrix(matrix: Matrix): Unit = lib.cairo_get_matrix(cr, matrix.matrix)
  def userToDevice(x: Double, y: Double): (Double, Double) =
    val xp = stackalloc[CDouble]()
    val yp = stackalloc[CDouble]()

    !xp = x
    !yp = y
    lib.cairo_user_to_device(cr, xp, yp)
    (!xp, !yp)
  def userToDeviceDistance(dx: Double, dy: Double): (Double, Double) =
    val dxp = stackalloc[CDouble]()
    val dyp = stackalloc[CDouble]()

    !dxp = dx
    !dyp = dy
    lib.cairo_user_to_device_distance(cr, dxp, dyp)
    (!dxp, !dyp)

  /** Emit the current page like [[showPage]] but retain its content, so the next page starts
    * as a copy of this one. Paginated (PDF/PS) surfaces only. */
  def copyPage(): Unit = lib.cairo_copy_page(cr)

  /** Draw glyphs by font glyph index at exact positions — the low-level text path a
    * typesetter or shaping engine uses instead of [[showText]], which leaves glyph choice
    * and placement to Cairo. Positions are in user space; on a PDF surface, pair with a
    * surrounding [[tagBegin]] carrying ActualText when the glyphs don't map trivially back
    * to the source text. */
  def showGlyphs(glyphs: collection.Seq[Glyph]): Unit = withGlyphArray(glyphs) { (a, n) =>
    lib.cairo_show_glyphs(cr, a, n)
  }

  /** Append the outlines of the glyphs to the current path, like [[textPath]] but
    * index-addressed. */
  def glyphPath(glyphs: collection.Seq[Glyph]): Unit = withGlyphArray(glyphs) { (a, n) =>
    lib.cairo_glyph_path(cr, a, n)
  }

  def glyphExtents(glyphs: collection.Seq[Glyph]): TextExtents = withGlyphArray(glyphs) { (a, n) =>
    val extents: TextExtentsOps = stackalloc[lib.cairo_text_extents_t]()

    lib.cairo_glyph_extents(cr, a, n, extents.ptr)
    TextExtents(extents.xBearing, extents.yBearing, extents.width, extents.height, extents.xAdvance, extents.yAdvance)
  }

  private def withGlyphArray[A](glyphs: collection.Seq[Glyph])(f: (lib.cairo_glyph_tp, Int) => A): A =
    val a = lib.cairo_glyph_allocate(glyphs.length)

    for (g, i) <- glyphs.zipWithIndex do
      val p = a + i

      p._1 = g.index.toUSize
      p._2 = g.x
      p._3 = g.y
    try f(a, glyphs.length)
    finally lib.cairo_glyph_free(a)

  /** Open a structure element in the document's tag tree. On a PDF surface this produces
    * tagged PDF: standard structure tags (`"Document"`, `"H1"`, `"P"`, …) give the document a
    * logical reading order for accessibility, while [[Tags.LINK]] and [[Tags.DEST]] create
    * hyperlinks and named destinations. `attributes` is a space-separated `key=value` list —
    * strings in single quotes — e.g. `"uri='https://example.org'"` for a link or
    * `"name='chapter1'"` for a destination. Every begin must be matched by a [[tagEnd]] with
    * the same name, properly nested. */
  def tagBegin(tagName: String, attributes: String): Unit = Zone {
    lib.cairo_tag_begin(cr, toCString(tagName), toCString(attributes))
  }

  /** Close the innermost open structure element named `tagName`. */
  def tagEnd(tagName: String): Unit = Zone {
    lib.cairo_tag_end(cr, toCString(tagName))
  }
end Context

implicit class FontOptions private[libcairo] (val ptr: lib.cairo_font_options_tp) extends AnyVal {
  def setHintMetrics(hintMetrics: HintMetrics): Unit = lib.cairo_font_options_set_hint_metrics(ptr, hintMetrics.value)

  def destroy(): Unit = lib.cairo_font_options_destroy(ptr)
}

/** Allocate a fresh, default font-options object. The caller owns it and must [[FontOptions.destroy]] it (or
  * apply it to a context with [[Context.setFontOptions]], which copies the options in). */
def fontOptionsCreate: FontOptions = lib.cairo_font_options_create()

implicit class TextExtentsOps(val ptr: lib.cairo_text_extents_tp) extends AnyVal {
  def xBearing: Double = ptr._1

  def yBearing: Double = ptr._2

  def width: Double = ptr._3

  def height: Double = ptr._4

  def xAdvance: Double = ptr._5

  def yAdvance: Double = ptr._6
}

case class TextExtents(
    xBearing: Double,
    yBearing: Double,
    width: Double,
    height: Double,
    xAdvance: Double,
    yAdvance: Double,
)

implicit class FontExtentsOps(val ptr: lib.cairo_font_extents_tp) extends AnyVal {
  def ascent: Double = ptr._1

  def descent: Double = ptr._2

  def height: Double = ptr._3

  def maxXAdvance: Double = ptr._4

  def maxYAdvance: Double = ptr._5
}

case class FontExtents(ascent: Double, descent: Double, height: Double, maxXAdvance: Double, maxYAdvance: Double)

/** One positioned glyph for [[Context.showGlyphs]]: `index` is the glyph index in the
  * current font face (not a character code), and `(x, y)` places its origin in user space. */
case class Glyph(index: Long, x: Double, y: Double)

implicit class Pattern private[libcairo] (val pattern: lib.cairo_pattern_tp) extends AnyVal {
  def destroy(): Unit = lib.cairo_pattern_destroy(pattern)

  def reference: Pattern = lib.cairo_pattern_reference(pattern)

  def status: Status = new Status(lib.cairo_pattern_status(pattern))

  def addColorStopRGB(offset: Double, red: Double, green: Double, blue: Double): Unit =
    lib.cairo_pattern_add_color_stop_rgb(pattern, offset, red, green, blue)

  def addColorStopRGBA(offset: Double, red: Double, green: Double, blue: Double, alpha: Double): Unit =
    lib.cairo_pattern_add_color_stop_rgba(pattern, offset, red, green, blue, alpha)

  /** How the pattern paints outside its natural area — [[Extend.REPEAT]] tiles a surface
    * pattern, [[Extend.PAD]] extends the edge colors of a gradient. */
  def setExtend(extend: Extend): Unit = lib.cairo_pattern_set_extend(pattern, extend.value)
  def getExtend: Extend               = new Extend(lib.cairo_pattern_get_extend(pattern))

  def setFilter(filter: Filter): Unit = lib.cairo_pattern_set_filter(pattern, filter.value)
  def getFilter: Filter               = new Filter(lib.cairo_pattern_get_filter(pattern))

  /** The pattern's transformation maps user space to pattern space — it is the inverse of
    * how the pattern appears on the page, so to draw a pattern scaled up 2x, set a matrix
    * that scales by 0.5. */
  def setMatrix(matrix: Matrix): Unit = lib.cairo_pattern_set_matrix(pattern, matrix.matrix)
  def getMatrix(matrix: Matrix): Unit = lib.cairo_pattern_get_matrix(pattern, matrix.matrix)
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
    val dxp = stackalloc[CDouble]()
    val dyp = stackalloc[CDouble]()

    !dxp = dx
    !dyp = dy
    lib.cairo_matrix_transform_distance(matrix, dxp, dyp)
    (!dxp, !dyp)
  }

  def transformPoint(x: CDouble, y: CDouble): (Double, Double) = {
    val xp = stackalloc[CDouble]()
    val yp = stackalloc[CDouble]()

    !xp = x
    !yp = y
    lib.cairo_matrix_transform_distance(matrix, xp, yp)
    (!xp, !yp)
  }

}

def imageSurfaceCreate(format: Format, width: Int, height: Int): Surface =
  lib.cairo_image_surface_create(format.value, width, height)

/** Wrap an existing pixel buffer as an image surface — no copy. `data` must point at
  * `height * stride` bytes laid out for `format` (for [[Format.ARGB32]], native-endian
  * premultiplied `0xAARRGGBB`), and must stay alive and unchanged for the surface's whole
  * lifetime — Cairo reads and writes it in place. `stride` is the row pitch in bytes; use
  * [[formatStrideForWidth]] to compute a valid one (Cairo requires a specific alignment). This
  * is the entry point for compositing a buffer produced elsewhere — a decoded image, or another
  * renderer's output — through Cairo. */
def imageSurfaceCreateForData(data: Ptr[Byte], format: Format, width: Int, height: Int, stride: Int): Surface =
  lib.cairo_image_surface_create_for_data(data, format.value, width, height, stride)

/** The row pitch, in bytes, Cairo requires for an image of `width` pixels in `format` — at
  * least `width` times the pixel size, rounded up to Cairo's alignment. Size a buffer for
  * [[imageSurfaceCreateForData]] as `height * formatStrideForWidth(format, width)`. Returns a
  * negative value if the width is too large to be represented. */
def formatStrideForWidth(format: Format, width: Int): Int =
  lib.cairo_format_stride_for_width(format.value, width)

def imageSurfaceCreateFromPNG(filename: String): Surface =
  Zone { lib.cairo_image_surface_create_from_png(toCString(filename)) }

/** Decode a PNG held in memory into a new image surface — the counterpart of
  * [[Surface.writeToPNGBytes]]. Check [[Surface.status]] on the result: malformed data
  * yields a surface in an error state, not a null. */
def imageSurfaceCreateFromPNGBytes(data: Array[Byte]): Surface = PngStream.synchronized {
  PngStream.readData = data
  PngStream.readPos = 0

  val surface = lib.cairo_image_surface_create_from_png_stream(PngStream.readFunc, null)

  PngStream.readData = null
  surface
}

/** Shared state for the PNG stream callbacks. Cairo's stream functions take a C function
  * pointer plus a closure pointer; a Scala function that captures can't become a C function
  * pointer, so the buffers live here and the callbacks are static. Each PNG operation
  * completes within a single (synchronized) call, so the state never outlives it. */
private object PngStream {
  var writeBuffer: scala.collection.mutable.ArrayBuffer[Byte] = null
  var readData: Array[Byte]                                   = null
  var readPos: Int                                            = 0

  val writeFunc: lib.cairo_write_func_t =
    CFuncPtr3.fromScalaFunction { (_: Ptr[Byte], data: Ptr[Byte], length: CUnsignedInt) =>
      val n = length.toInt
      var i = 0

      while i < n do
        writeBuffer += data(i)
        i += 1
      Status.SUCCESS.value
    }

  val readFunc: lib.cairo_read_func_t =
    CFuncPtr3.fromScalaFunction { (_: Ptr[Byte], data: Ptr[Byte], length: CUnsignedInt) =>
      val n = length.toInt

      if readData == null || readPos + n > readData.length then Status.READ_ERROR.value
      else
        var i = 0

        while i < n do
          data(i) = readData(readPos + i)
          i += 1
        readPos += n
        Status.SUCCESS.value
    }
}

def pdfSurfaceCreate(filename: String, width_in_points: Double, height_in_points: Double): Surface =
  Zone { lib.cairo_pdf_surface_create(toCString(filename), width_in_points, height_in_points) }

def patternCreateRGB(red: Double, green: Double, blue: Double): Pattern =
  lib.cairo_pattern_create_rgb(red, green, blue)

def patternCreateRGBA(red: Double, green: Double, blue: Double, alpha: Double): Pattern =
  lib.cairo_pattern_create_rgba(red, green, blue, alpha)

/** Create a pattern that paints with the contents of a surface — combine with
  * [[Pattern.setExtend]] ([[Extend.REPEAT]]) for tiling and [[Pattern.setMatrix]] for
  * placement and scaling. */
def patternCreateForSurface(surface: Surface): Pattern =
  lib.cairo_pattern_create_for_surface(surface.surface)

def patternCreateLinear(x0: Double, y0: Double, x1: Double, y1: Double): Pattern =
  lib.cairo_pattern_create_linear(x0, y0, x1, y1)

def patternCreateRadial(
    cx0: Double,
    cy0: Double,
    radius0: Double,
    cx1: Double,
    cy1: Double,
    radius1: Double,
): Pattern =
  lib.cairo_pattern_create_radial(cx0, cy0, radius0, cx1, cy1, radius1)

// enums

class Status(val value: CInt) extends AnyVal {
  def isSuccess: Boolean = value == 0

  /** Cairo's human-readable description of the status. */
  def message: String = fromCString(lib.cairo_status_to_string(value))
}

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

class Operator(val value: lib.cairo_operator_t) extends AnyVal

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

class Antialias(val value: lib.cairo_antialias_t) extends AnyVal

object Antialias {
  final val DEFAULT  = new Antialias(0)
  final val NONE     = new Antialias(1)
  final val GRAY     = new Antialias(2)
  final val SUBPIXEL = new Antialias(3)
  final val FAST     = new Antialias(4)
  final val GOOD     = new Antialias(5)
  final val BEST     = new Antialias(6)
}

class FillRule(val value: lib.cairo_fill_rule_t) extends AnyVal

object FillRule {
  final val WINDING  = new FillRule(0)
  final val EVEN_ODD = new FillRule(1)
}

class LineCap(val value: lib.cairo_line_cap_t) extends AnyVal

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

/** The standard tag names from cairo.h for use with [[Context.tagBegin]] /
  * [[Context.tagEnd]]. Any other name is treated as a PDF structure tag (`"Document"`,
  * `"H1"`, `"P"`, `"Figure"`, …). (Named `Tags`, not `Tag`, to avoid clashing with
  * `scala.scalanative.unsafe.Tag` when both packages are wildcard-imported.) */
object Tags {

  /** A named destination: `tagBegin(Tags.DEST, "name='chapter1'")`. Content is optional —
    * an empty begin/end pair marks a point. */
  final val DEST = "cairo.dest"

  /** A hyperlink around the enclosed content. Attributes select the target:
    * `"uri='https://...'"` for an external link, `"dest='name'"` for a named destination,
    * or `"page=N pos=[x y]"` for a direct page location. */
  final val LINK = "Link"

  /** Marks content split across graphics-state boundaries as one logical element, with
    * `id` linking the pieces. */
  final val CONTENT = "cairo.content"

  /** References a [[CONTENT]] element by `ref` attribute. */
  final val CONTENT_REF = "cairo.content_ref"
}

class PdfVersion(val value: lib.cairo_pdf_version_t) extends AnyVal {

  /** The version's display name, e.g. `"PDF 1.7"`. */
  def string: String = fromCString(lib.cairo_pdf_version_to_string(value))
}

object PdfVersion {
  final val V1_4 = new PdfVersion(0)
  final val V1_5 = new PdfVersion(1)
  final val V1_6 = new PdfVersion(2)
  final val V1_7 = new PdfVersion(3)
}

class PdfMetadata(val value: lib.cairo_pdf_metadata_t) extends AnyVal

object PdfMetadata {
  final val TITLE       = new PdfMetadata(0)
  final val AUTHOR      = new PdfMetadata(1)
  final val SUBJECT     = new PdfMetadata(2)
  final val KEYWORDS    = new PdfMetadata(3)
  final val CREATOR     = new PdfMetadata(4)
  final val CREATE_DATE = new PdfMetadata(5)
  final val MOD_DATE    = new PdfMetadata(6)
}

class PdfOutlineFlags(val value: lib.cairo_pdf_outline_flags_t) extends AnyVal {
  def |(that: PdfOutlineFlags): PdfOutlineFlags = new PdfOutlineFlags(value | that.value)
}

object PdfOutlineFlags {
  final val NONE   = new PdfOutlineFlags(0)
  final val OPEN   = new PdfOutlineFlags(0x1)
  final val BOLD   = new PdfOutlineFlags(0x2)
  final val ITALIC = new PdfOutlineFlags(0x4)
}

object PdfOutline {

  /** The parent id that makes [[Surface.addOutline]] create a top-level outline item. */
  final val ROOT = 0
}

class RegionOverlap(val value: CInt) extends AnyVal

object RegionOverlap {
  final val IN   = new RegionOverlap(0)
  final val OUT  = new RegionOverlap(1)
  final val PART = new RegionOverlap(2)
}

def fontFaceCreateForFTFace(face: FT_Face, load_flags: Int): FontFace =
  lib.cairo_ft_font_face_create_for_ft_face(face, load_flags)

implicit class FontFace private[libcairo] (val fontfaceptr: lib.cairo_font_face_tp) extends AnyVal

implicit class ScaledFont private[libcairo] (val scaledfontptr: lib.cairo_scaled_font_tp) extends AnyVal:
  def reference: ScaledFont = lib.cairo_scaled_font_reference(scaledfontptr)
