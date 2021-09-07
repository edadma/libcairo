package io.github.edadma.libcairo.extern

import scala.scalanative.unsafe._

@link("cairo")
@extern
object LibCairo {

  type cairo_surface_t  = CStruct0
  type cairo_surface_tp = Ptr[cairo_surface_t]
  type cairo_status_t   = CInt

  def cairo_surface_write_to_png(surface: cairo_surface_tp, filename: CString): cairo_status_t = extern //2433
  def cairo_image_surface_create_from_png(filename: CString): cairo_surface_tp                 = extern //2577

}
