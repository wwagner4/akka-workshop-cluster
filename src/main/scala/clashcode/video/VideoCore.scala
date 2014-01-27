package clashcode.video

import clashcode.video.swing.SwingDevice

object VideoCore {

  case class Path(stages: List[Stage])

  def play(device: Device, stages: List[Stage], max: Max, framesPerSecond: Int): Unit = {
    println("play")
    while (true) {
      for (s <- stages) {
        device.paintStage(s, paintStage(max))
        Thread.sleep((1000.0 / framesPerSecond).toInt);
      }
    }
    println("finished play")
  }

  def paintStage(max: Max)(g: Graphics, stage: Stage): Unit = {
    g.clear
    val visibleCans = stage.cans - stage.robot.pos
    g.paintField(max)
    for (c <- visibleCans) {
      g.paintCan(c, max)
    }
    g.paintRobot(stage.robot.pos, stage.robot.dir, max)
  }

}

case class Pos(x: Int, y: Int)
case class Max(x: Int, y: Int)
case class Rec(w: Int, h: Int)

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
  def paintField(max: Max)
  def paintCan(pos: Pos, max: Max)
  def paintRobot(pos: Pos, dir: Direction, max: Max)
}

object Maxval {

  def valid(value: Int): Boolean = {
    def isOdd: Boolean = {
      (value + 1) % 2 == 0
    }
    value >= 3 && isOdd
  }
}
  
