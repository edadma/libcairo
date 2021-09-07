import io.github.edadma.libcairo._

object Main extends App {

  val surface = image_surface_create(Format.ARGB32, 120, 120);
  val cr      = surface.create

  cr.scale(120, 120)

  cr.set_source_rgb(0, 0, 0)
  cr.move_to(0, 0)
  cr.line_to(1, 1)
  cr.move_to(1, 0)
  cr.line_to(0, 1)
  cr.set_line_width(0.2)
  cr.stroke()

  cr.rectangle(0, 0, 0.5, 0.5)
  cr.set_source_rgba(1, 0, 0, 0.80)
  cr.fill()

  cr.rectangle(0, 0.5, 0.5, 0.5)
  cr.set_source_rgba(0, 1, 0, 0.60)
  cr.fill()

  cr.rectangle(0.5, 0, 0.5, 0.5)
  cr.set_source_rgba(0, 0, 1, 0.40)
  cr.fill()

  surface.write_to_png("image.png")

  cr.destroy()
  surface.destroy()

}
