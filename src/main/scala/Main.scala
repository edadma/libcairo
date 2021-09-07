//import io.github.edadma.libcairo._
//
//object Main extends App {
//
//  val surface = image_surface_create(Format.ARGB32, 120, 120)
//  val cr      = surface.create
//
//  cr.scale(120, 120)
//
//  cr.set_source_rgb(0, 0, 0)
//  cr.move_to(0, 0)
//  cr.line_to(1, 1)
//  cr.move_to(1, 0)
//  cr.line_to(0, 1)
//  cr.set_line_width(0.2)
//  cr.stroke()
//
//  cr.rectangle(0, 0, 0.5, 0.5)
//  cr.set_source_rgba(1, 0, 0, 0.80)
//  cr.fill()
//
//  cr.rectangle(0, 0.5, 0.5, 0.5)
//  cr.set_source_rgba(0, 1, 0, 0.60)
//  cr.fill()
//
//  cr.rectangle(0.5, 0, 0.5, 0.5)
//  cr.set_source_rgba(0, 0, 1, 0.40)
//  cr.fill()
//
//  surface.write_to_png("image1.png")
//
//  cr.destroy()
//  surface.destroy()
//
//}

//import io.github.edadma.libcairo._
//
//object Main extends App {
//
//  val surface = image_surface_create(Format.ARGB32, 120, 120)
//  val cr      = surface.create
//
//  /* Examples are in 1.0 x 1.0 coordinate space */
//  cr.scale(120, 120)
//
//  val radpat = pattern_create_radial(0.25, 0.25, 0.1, 0.5, 0.5, 0.5)
//
//  radpat.add_color_stop_rgb(0, 1.0, 0.8, 0.8)
//  radpat.add_color_stop_rgb(1, 0.9, 0.0, 0.0)
//
//  for (i <- 1 to 9; j <- 1 to 9)
//    cr.rectangle(i / 10.0 - 0.04, j / 10.0 - 0.04, 0.08, 0.08)
//
//  cr.set_source(radpat)
//  cr.fill()
//
//  val linpat = pattern_create_linear(0.25, 0.35, 0.75, 0.65)
//
//  linpat.add_color_stop_rgba(0.00, 1, 1, 1, 0)
//  linpat.add_color_stop_rgba(0.25, 0, 1, 0, 0.5)
//  linpat.add_color_stop_rgba(0.50, 1, 1, 1, 0)
//  linpat.add_color_stop_rgba(0.75, 0, 0, 1, 0.5)
//  linpat.add_color_stop_rgba(1.00, 1, 1, 1, 0)
//
//  cr.rectangle(0.0, 0.0, 1, 1)
//  cr.set_source(linpat)
//  cr.fill()
//
//  surface.write_to_png("image2.png")
//
//  cr.destroy()
//  surface.destroy()
//
//}
