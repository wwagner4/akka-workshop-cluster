package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._

object VideoMain extends App {

  val cans = Set(
    Pos(5, 5),
    Pos(1, 3),
    Pos(3, 3),
    Pos(6, 6))

  val cans1 = cans - Pos(3, 3)

  val stages = List(
    Stage(RobotView(Pos(1, 1), E), cans),
    Stage(RobotView(Pos(2, 1), E), cans),
    Stage(RobotView(Pos(3, 1), E), cans),
    Stage(RobotView(Pos(3, 1), SE), cans),
    Stage(RobotView(Pos(3, 1), S), cans),
    Stage(RobotView(Pos(3, 2), S), cans),
    Stage(RobotView(Pos(3, 3), S), cans),
    Stage(RobotView(Pos(3, 4), S), cans),
    Stage(RobotView(Pos(3, 5), S), cans),
    Stage(RobotView(Pos(3, 6), S), cans),
    Stage(RobotView(Pos(3, 6), SE), cans),
    Stage(RobotView(Pos(3, 6), E), cans),
    Stage(RobotView(Pos(4, 6), E), cans),
    Stage(RobotView(Pos(5, 6), E), cans),
    Stage(RobotView(Pos(6, 6), E), cans),
    Stage(RobotView(Pos(6, 6), NE), cans),
    Stage(RobotView(Pos(6, 6), N), cans),
    Stage(RobotView(Pos(6, 6), NW), cans),
    Stage(RobotView(Pos(6, 6), W), cans),
    Stage(RobotView(Pos(6, 6), SW), cans),
    Stage(RobotView(Pos(6, 6), S), cans),
    Stage(RobotView(Pos(6, 6), S), cans),
    Stage(RobotView(Pos(6, 6), S), cans),
    Stage(RobotView(Pos(6, 6), S), cans),
    Stage(RobotView(Pos(6, 6), S), cans))

  val device = new SwingDevice(createGraphics)

  def createGraphics(g: Graphics2D): AwtGraphics = new AwtRectGraphicsImages(0.6, 0.05) {

    def graphics: Graphics2D = g
    def drawArea: DrawArea = device.drawArea

  }

  Video.play(device, stages, Max(10, 10), 2)

}