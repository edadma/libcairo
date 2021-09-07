import io.github.edadma.libcairo._

object Main extends App {

  val surface = image_surface_create_from_png("fish.png")

  surface.write_to_png("new.png")

}
