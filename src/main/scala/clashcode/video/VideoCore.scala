package clashcode.video

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

case class RobotView(pos: Pos, dir: Direction)

sealed trait Stage {
  def paint(g: CommonGraphics, max: Max): Unit
}

case class GameStage(robot: RobotView, cans: Set[Pos]) extends Stage {
  def paint(g: CommonGraphics, max: Max): Unit = {
    g.clear
    val visibleCans = cans - robot.pos
    g.paintField(max)
    for (c <- visibleCans) {
      g.paintCan(c, max)
    }
    g.paintRobot(robot.pos, robot.dir, max)
  }
  
}

case class Stages(stages: List[Stage], fieldSize: Int)

trait Device {

  // Define how to paint a stage on that device
  def paintStage(stage: Stage)

  def postPaintStage: Unit = {
    // Do nothing by default
  }

  def max: Max
  
  def playEndless(stages: Stages): Unit = {
    val max = Max(2 * stages.fieldSize, 2 * stages.fieldSize)
    while (true) {
      for (s <- stages.stages) {
        paintStage(s)
        postPaintStage
      }
    }
  }

  def playOnes(stages: Stages): Unit = {
    val max = Max(2 * stages.fieldSize, 2 * stages.fieldSize)
    for (s <- stages.stages) {
      paintStage(s)
      postPaintStage
    }
    println("finished play")
  }

}


/**
 * Abstraction level for Graphics
 * Can, but must not be used from Device implementations
 */
trait CommonGraphics {
  def drawArea: DrawArea
  def clear: Unit
  def paintField(max: Max)
  def paintCan(pos: Pos, max: Max)
  def paintRobot(pos: Pos, dir: Direction, max: Max)
}

