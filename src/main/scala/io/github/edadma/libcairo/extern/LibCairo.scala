package io.github.edadma.libcairo.extern

import scala.scalanative.unsafe._

@link("cairo")
@extern
object LibCairo {

  type cairo_surface_t  = CStruct0
  type cairo_surface_tp = Ptr[cairo_surface_t]
  type cairo_status_t   = CInt
  type cairo_t          = CStruct0
  type cairo_tp         = Ptr[cairo_t]
  type cairo_format_t   = CInt

  def cairo_create(target: cairo_surface_tp): cairo_tp                                                = extern //491
  def cairo_reference(cr: cairo_tp): cairo_tp                                                         = extern //494
  def cairo_destroy(cr: cairo_tp): cairo_tp                                                           = extern //497
  def cairo_set_source_rgb(cr: cairo_tp, red: CDouble, green: CDouble, blue: CDouble): Unit           = extern //655
  def cairo_set_line_width(cr: cairo_tp, width: CDouble): Unit                                        = extern //762
  def cairo_move_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit                                       = extern //855
  def cairo_rectangle(cr: cairo_tp, x: CDouble, y: CDouble, width: CDouble, height: CDouble): Unit    = extern //902
  def cairo_stroke(cr: cairo_tp): Unit                                                                = extern //938
  def cairo_fill(cr: cairo_tp): Unit                                                                  = extern //944
  def cairo_surface_write_to_png(surface: cairo_surface_tp, filename: CString): cairo_status_t        = extern //2433
  def cairo_image_surface_create(format: cairo_format_t, width: CInt, height: CInt): cairo_surface_tp = extern //2544
  def cairo_image_surface_create_from_png(filename: CString): cairo_surface_tp                        = extern //2577

}
