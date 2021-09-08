package io.github.edadma.libcairo.extern

import scala.scalanative.unsafe._

@link("cairo")
@extern
object LibCairo {

  type cairo_surface_t       = CStruct0
  type cairo_surface_tp      = Ptr[cairo_surface_t]
  type cairo_status_t        = CInt
  type cairo_t               = CStruct0
  type cairo_tp              = Ptr[cairo_t]
  type cairo_format_t        = CInt
  type cairo_font_slant_t    = CInt
  type cairo_font_weight_t   = CInt
  type cairo_text_extents_t  = CStruct6[CDouble, CDouble, CDouble, CDouble, CDouble, CDouble]
  type cairo_text_extents_tp = Ptr[cairo_text_extents_t]
  type cairo_font_extents_t  = CStruct5[CDouble, CDouble, CDouble, CDouble, CDouble]
  type cairo_font_extents_tp = Ptr[cairo_font_extents_t]
  type cairo_pattern_t       = CStruct0
  type cairo_pattern_tp      = Ptr[cairo_pattern_t]
  type cairo_font_options_t  = CStruct0
  type cairo_font_options_tp = Ptr[cairo_font_options_t]

  def cairo_create(target: cairo_surface_tp): cairo_tp = extern //491

  def cairo_reference(cr: cairo_tp): cairo_tp = extern //494

  def cairo_destroy(cr: cairo_tp): cairo_tp = extern //497

  def cairo_set_source(cr: cairo_tp, source: cairo_pattern_tp): Unit = extern //653

  def cairo_set_source_rgb(cr: cairo_tp, red: CDouble, green: CDouble, blue: CDouble): Unit = extern //655

  def cairo_set_source_rgba(cr: cairo_tp, red: CDouble, green: CDouble, blue: CDouble, alpha: CDouble): Unit =
    extern //659

  def cairo_set_line_width(cr: cairo_tp, width: CDouble): Unit = extern //762

  def cairo_set_dash(cr: cairo_tp, dashes: Ptr[CDouble], num_dashes: CInt, offset: CDouble): Unit = extern //810

  def cairo_scale(cr: cairo_tp, sx: CDouble, sy: CDouble): Unit = extern //822

  def cairo_device_to_user(cr: cairo_tp, dx: Ptr[CDouble], dy: Ptr[CDouble]): Unit = extern //845

  def cairo_device_to_user_distance(cr: cairo_tp, dx: Ptr[CDouble], dy: Ptr[CDouble]): Unit = extern //848

  def cairo_move_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit = extern //855

  def cairo_line_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit = extern //861

  def cairo_arc(cr: cairo_tp, x: CDouble, y: CDouble, radius: CDouble, angle1: CDouble, angle2: CDouble): Unit =
    extern //870

  def cairo_rel_line_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit = extern //893

  def cairo_rel_curve_to(cr: cairo_tp,
                         dx1: CDouble,
                         dy1: CDouble,
                         dx2: CDouble,
                         dy2: CDouble,
                         dx3: CDouble,
                         dy3: CDouble): Unit = extern //896

  def cairo_rectangle(cr: cairo_tp, x: CDouble, y: CDouble, width: CDouble, height: CDouble): Unit = extern //902

  def cairo_close_path(cr: cairo_tp): Unit = extern //912

  def cairo_paint(cr: cairo_tp): Unit = extern //921

  def cairo_paint_with_alpha(cr: cairo_tp, alpha: CDouble): Unit = extern //924

  def cairo_mask(cr: cairo_tp, source: cairo_pattern_tp): Unit = extern //928

  def cairo_stroke(cr: cairo_tp): Unit = extern //938

  def cairo_stroke_preserve(cr: cairo_tp): Unit = extern //941

  def cairo_fill(cr: cairo_tp): Unit = extern //944

  def cairo_fill_preserve(cr: cairo_tp): Unit = extern //947

  def cairo_select_font_face(cr: cairo_tp,
                             family: CString,
                             slant: cairo_font_slant_t,
                             weight: cairo_font_weight_t): Unit = extern //1444

  def cairo_set_font_size(cr: cairo_tp, size: CDouble): Unit = extern //1450

  def cairo_set_font_options(cr: cairo_tp, options: cairo_font_options_tp): Unit = extern //1461

  def cairo_get_font_options(cr: cairo_tp, options: cairo_font_options_tp): Unit = extern //1465

  def cairo_show_text(cr: cairo_tp, utf8: CString): Unit = extern //1482

  def cairo_text_path(cr: cairo_tp, utf8: CString): Unit = extern //1498

  def cairo_text_extents(cr: cairo_tp, utf8: CString, extents: cairo_text_extents_tp): Unit = extern //1504

  def cairo_font_extents(cr: cairo_tp, extents: cairo_font_extents_tp): Unit = extern //1515

  def cairo_surface_destroy(surface: cairo_surface_tp): Unit = extern //2334

  def cairo_surface_write_to_png(surface: cairo_surface_tp, filename: CString): cairo_status_t = extern //2433

  def cairo_image_surface_create(format: cairo_format_t, width: CInt, height: CInt): cairo_surface_tp = extern //2544

  def cairo_image_surface_create_from_png(filename: CString): cairo_surface_tp = extern //2577

  def cairo_pattern_create_linear(x0: CDouble, y0: CDouble, x1: CDouble, y1: CDouble): cairo_pattern_tp = extern //2760

  def cairo_pattern_create_radial(cx0: CDouble,
                                  cy0: CDouble,
                                  radius0: CDouble,
                                  cx1: CDouble,
                                  cy1: CDouble,
                                  radius1: CDouble): cairo_pattern_tp = extern //2764

  def cairo_pattern_add_color_stop_rgb(pattern: cairo_pattern_tp,
                                       offset: CDouble,
                                       red: CDouble,
                                       green: CDouble,
                                       blue: CDouble): Unit = extern //2837

  def cairo_pattern_add_color_stop_rgba(pattern: cairo_pattern_tp,
                                        offset: CDouble,
                                        red: CDouble,
                                        green: CDouble,
                                        blue: CDouble,
                                        alpha: CDouble): Unit = extern //2842
}
