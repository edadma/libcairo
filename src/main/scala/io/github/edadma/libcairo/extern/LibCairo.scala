package io.github.edadma.libcairo.extern

import scala.scalanative.unsafe._

import io.github.edadma.freetype_face.FT_Face

@link("cairo")
@extern
object LibCairo:
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
  type cairo_matrix_t        = CStruct6[CDouble, CDouble, CDouble, CDouble, CDouble, CDouble]
  type cairo_matrix_tp       = Ptr[cairo_matrix_t]
  type cairo_content_t       = CInt
  type cairo_line_join_t     = CInt
  type cairo_line_cap_t      = CInt
  type cairo_operator_t      = CInt
  type cairo_antialias_t     = CInt
  type cairo_fill_rule_t     = CInt
  type cairo_font_face_t     = CStruct0
  type cairo_font_face_tp    = Ptr[cairo_font_face_t]
  type cairo_scaled_font_t   = CStruct0
  type cairo_scaled_font_tp  = Ptr[cairo_scaled_font_t]

  def cairo_create(target: cairo_surface_tp): cairo_tp                            = extern // 491
  def cairo_reference(cr: cairo_tp): cairo_tp                                     = extern // 494
  def cairo_destroy(cr: cairo_tp): cairo_tp                                       = extern // 497
  def cairo_get_reference_count(cr: cairo_tp): CUnsignedInt                       = extern // 500
  def cairo_save(cr: cairo_tp): Unit                                              = extern // 513
  def cairo_restore(cr: cairo_tp): Unit                                           = extern // 516
  def cairo_push_group(cr: cairo_tp): Unit                                        = extern // 519
  def cairo_push_group_with_content(cr: cairo_tp, content: cairo_content_t): Unit = extern // 522
  def cairo_pop_group(cr: cairo_tp): cairo_pattern_tp                             = extern // 524
  def cairo_pop_group_to_source(cr: cairo_tp): Unit                               = extern // 528
  def cairo_set_operator(cr: cairo_tp, op: cairo_operator_t): Unit                = extern // 650
  def cairo_set_source_surface(cr: cairo_tp, surface: cairo_surface_tp, x: CDouble, y: CDouble): Unit =
    extern // 664
  def cairo_set_tolerance(cr: cairo_tp, tolerance: CDouble): Unit                           = extern // 670
  def cairo_set_source(cr: cairo_tp, source: cairo_pattern_tp): Unit                        = extern // 653
  def cairo_set_source_rgb(cr: cairo_tp, red: CDouble, green: CDouble, blue: CDouble): Unit = extern // 655
  def cairo_set_source_rgba(cr: cairo_tp, red: CDouble, green: CDouble, blue: CDouble, alpha: CDouble): Unit =
    extern // 659
  def cairo_set_line_width(cr: cairo_tp, width: CDouble): Unit                                    = extern // 762
  def cairo_set_line_join(cr: cairo_tp, line_join: cairo_line_join_t): Unit                       = extern // 807
  def cairo_set_dash(cr: cairo_tp, dashes: Ptr[CDouble], num_dashes: CInt, offset: CDouble): Unit = extern // 810
  def cairo_translate(cr: cairo_tp, tx: CDouble, ty: CDouble): Unit                               = extern // 819
  def cairo_scale(cr: cairo_tp, sx: CDouble, sy: CDouble): Unit                                   = extern // 822
  def cairo_rotate(cr: cairo_tp, angle: CDouble): Unit                                            = extern // 825
  def cairo_identity_matrix(cr: cairo_tp): Unit                                                   = extern
  def cairo_device_to_user(cr: cairo_tp, dx: Ptr[CDouble], dy: Ptr[CDouble]): Unit                = extern // 845
  def cairo_device_to_user_distance(cr: cairo_tp, dx: Ptr[CDouble], dy: Ptr[CDouble]): Unit       = extern // 848
  def cairo_move_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit                                   = extern // 855
  def cairo_rel_move_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit                               = extern
  def cairo_line_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit                                   = extern // 861
  def cairo_arc(cr: cairo_tp, x: CDouble, y: CDouble, radius: CDouble, angle1: CDouble, angle2: CDouble): Unit =
    extern // 870
  def cairo_arc_negative(
      cr: cairo_tp,
      x: CDouble,
      y: CDouble,
      radius: CDouble,
      angle1: CDouble,
      angle2: CDouble,
  ): Unit = extern
  def cairo_rel_line_to(cr: cairo_tp, x: CDouble, y: CDouble): Unit = extern // 893
  def cairo_rel_curve_to(
      cr: cairo_tp,
      dx1: CDouble,
      dy1: CDouble,
      dx2: CDouble,
      dy2: CDouble,
      dx3: CDouble,
      dy3: CDouble,
  ): Unit = extern // 896
  def cairo_rectangle(cr: cairo_tp, x: CDouble, y: CDouble, width: CDouble, height: CDouble): Unit = extern // 902
  def cairo_close_path(cr: cairo_tp): Unit                                                         = extern // 912
  def cairo_paint(cr: cairo_tp): Unit                                                              = extern // 921
  def cairo_paint_with_alpha(cr: cairo_tp, alpha: CDouble): Unit                                   = extern // 924
  def cairo_mask(cr: cairo_tp, source: cairo_pattern_tp): Unit                                     = extern // 928
  def cairo_stroke(cr: cairo_tp): Unit                                                             = extern // 938
  def cairo_stroke_preserve(cr: cairo_tp): Unit                                                    = extern // 941
  def cairo_fill(cr: cairo_tp): Unit                                                               = extern // 944
  def cairo_fill_preserve(cr: cairo_tp): Unit                                                      = extern // 947
  def cairo_stroke_extents(cr: cairo_tp, x1: Ptr[CDouble], y1: Ptr[CDouble], x2: Ptr[CDouble], y2: Ptr[CDouble]): Unit =
    extern // 967
  def cairo_reset_clip(cr: cairo_tp): Unit = extern // 978
  def cairo_clip(cr: cairo_tp): Unit       = extern // 981
  def cairo_select_font_face(
      cr: cairo_tp,
      family: CString,
      slant: cairo_font_slant_t,
      weight: cairo_font_weight_t,
  ): Unit = extern // 1444
  def cairo_set_font_size(cr: cairo_tp, size: CDouble): Unit                                            = extern // 1450
  def cairo_set_font_options(cr: cairo_tp, options: cairo_font_options_tp): Unit                        = extern // 1461
  def cairo_get_font_options(cr: cairo_tp, options: cairo_font_options_tp): Unit                        = extern // 1465
  def cairo_show_text(cr: cairo_tp, utf8: CString): Unit                                                = extern // 1482
  def cairo_text_path(cr: cairo_tp, utf8: CString): Unit                                                = extern // 1498
  def cairo_text_extents(cr: cairo_tp, utf8: CString, extents: cairo_text_extents_tp): Unit             = extern // 1504
  def cairo_font_extents(cr: cairo_tp, extents: cairo_font_extents_tp): Unit                            = extern // 1515
  def cairo_surface_destroy(surface: cairo_surface_tp): Unit                                            = extern // 2334
  def cairo_surface_write_to_png(surface: cairo_surface_tp, filename: CString): cairo_status_t          = extern // 2433
  def cairo_image_surface_create(format: cairo_format_t, width: CInt, height: CInt): cairo_surface_tp   = extern // 2544
  def cairo_image_surface_create_from_png(filename: CString): cairo_surface_tp                          = extern // 2577
  def cairo_pattern_create_linear(x0: CDouble, y0: CDouble, x1: CDouble, y1: CDouble): cairo_pattern_tp = extern // 2760
  def cairo_pattern_create_radial(
      cx0: CDouble,
      cy0: CDouble,
      radius0: CDouble,
      cx1: CDouble,
      cy1: CDouble,
      radius1: CDouble,
  ): cairo_pattern_tp = extern // 2764
  def cairo_pattern_destroy(pattern: cairo_pattern_tp): Unit = extern // 2774
  def cairo_pattern_add_color_stop_rgb(
      pattern: cairo_pattern_tp,
      offset: CDouble,
      red: CDouble,
      green: CDouble,
      blue: CDouble,
  ): Unit = extern // 2837
  def cairo_pattern_add_color_stop_rgba(
      pattern: cairo_pattern_tp,
      offset: CDouble,
      red: CDouble,
      green: CDouble,
      blue: CDouble,
      alpha: CDouble,
  ): Unit = extern // 2842
  def cairo_matrix_init(
      matrix: cairo_matrix_tp,
      xx: CDouble,
      yx: CDouble,
      xy: CDouble,
      yy: CDouble,
      x0: CDouble,
      y0: CDouble,
  ): Unit = extern // 3018
  def cairo_matrix_init_identity(matrix: cairo_matrix_tp): Unit                                    = extern // 3024
  def cairo_matrix_init_translate(matrix: cairo_matrix_tp, tx: CDouble, ty: CDouble): Unit         = extern // 3027
  def cairo_matrix_init_scale(matrix: cairo_matrix_tp, sx: CDouble, sy: CDouble): Unit             = extern // 3031
  def cairo_matrix_init_rotate(matrix: cairo_matrix_tp, radians: CDouble): Unit                    = extern // 3035
  def cairo_matrix_translate(matrix: cairo_matrix_tp, tx: CDouble, ty: CDouble): Unit              = extern // 3039
  def cairo_matrix_scale(matrix: cairo_matrix_tp, sx: CDouble, sy: CDouble): Unit                  = extern // 3042
  def cairo_matrix_rotate(matrix: cairo_matrix_tp, radians: CDouble): Unit                         = extern // 3045
  def cairo_matrix_invert(matrix: cairo_matrix_tp): cairo_status_t                                 = extern // 3048
  def cairo_matrix_multiply(result: cairo_matrix_tp, a: cairo_matrix_tp, b: cairo_matrix_tp): Unit = extern // 3051
  def cairo_matrix_transform_distance(matrix: cairo_matrix_tp, dx: Ptr[CDouble], dy: Ptr[CDouble]): Unit =
    extern // 3056
  def cairo_matrix_transform_point(matrix: cairo_matrix_tp, x: Ptr[CDouble], y: Ptr[CDouble]): Unit = extern // 3060
  def cairo_new_path(cr: cairo_tp): Unit                                                            = extern
  def cairo_set_line_cap(cr: cairo_tp, line_cap: cairo_line_cap_t): Unit                            = extern
  def cairo_pdf_surface_create(
      filename: CString,
      width_in_points: CDouble,
      height_in_points: CDouble,
  ): cairo_surface_tp = extern
  def cairo_surface_show_page(surface: cairo_surface_tp): Unit                                   = extern
  def cairo_show_page(cr: cairo_tp): Unit                                                        = extern
  def cairo_ft_font_face_create_for_ft_face(face: FT_Face, load_flags: CInt): cairo_font_face_tp = extern
  def cairo_set_font_face(cr: cairo_tp, font_face: cairo_font_face_tp): Unit                     = extern
  def cairo_set_scaled_font(cr: cairo_tp, scaled_font: cairo_scaled_font_tp): Unit               = extern
  def cairo_get_scaled_font(cr: cairo_tp): cairo_scaled_font_tp                                  = extern
  def cairo_scaled_font_reference(scaled_font: cairo_scaled_font_tp): cairo_scaled_font_tp       = extern
  def cairo_path_extents(cr: cairo_tp, x1: Ptr[CDouble], y1: Ptr[CDouble], x2: Ptr[CDouble], y2: Ptr[CDouble]): Unit =
    extern
  def cairo_fill_extents(cr: cairo_tp, x1: Ptr[CDouble], y1: Ptr[CDouble], x2: Ptr[CDouble], y2: Ptr[CDouble]): Unit =
    extern
  def cairo_new_sub_path(cr: cairo_tp): Unit                          = extern
  def cairo_image_surface_get_width(surface: cairo_surface_tp): CInt  = extern
  def cairo_image_surface_get_height(surface: cairo_surface_tp): CInt = extern
