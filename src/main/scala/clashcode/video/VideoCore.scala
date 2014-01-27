package clashcode.video

import clashcode.video.swing.SwingDevice

object VideoCore {

  case class Path(stages: List[Stage])

  def play(device: Device, stages: List[Stage], framesPerSecond: Int): Unit = {
    println("play")
    while (true) {
      for (s <- stages) {
        device.paintStage(s, paintStage)
        Thread.sleep((1000.0 / framesPerSecond).toInt);
      }
    }
    println("finished play")
  }

  def paintStage(g: Graphics, stage: Stage): Unit = {
    g.clear
    val visibleCans = stage.cans - stage.robot.pos
    g.paintField
    for (c <- visibleCans) {
      g.paintCan(c)
    }
    g.paintRobot(stage.robot.pos, stage.robot.dir)
  }

}

case class Pos(posx: Int, posy: Int)
case class Rec(width: Int, height: Int)

case class DrawArea(offset: Pos, area: Rec)

sealed trait Direction

case object N extends Direction
case object NE extends Direction
case object E extends Direction
case object SE extends Direction
case object S extends Direction
case object SW extends Direction
case object W extends Direction
case object NW extends Direction

case class Robot(pos: Pos, dir: Direction)

case class Stage(robot: Robot, cans: Set[Pos])

trait Device {
  type PaintFunc = (Graphics, Stage) => Unit
  def paintStage(stage: Stage, f: PaintFunc)
}

trait Graphics {
  def drawArea: DrawArea
  def clear: Unit
  def paintField
  def paintCan(pos: Pos)
  def paintRobot(pos: Pos, dir: Direction)
}

object Maxval {

  def valid(value: Int): Boolean = {
    def isOdd: Boolean = {
      (value + 1) % 2 == 0
    }
    value >= 3 && isOdd
  }
}
  
