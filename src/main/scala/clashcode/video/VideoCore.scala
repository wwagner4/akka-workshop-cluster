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

case class Stage(robot: RobotView, cans: Set[Pos])

trait Device {

  type PaintFunc = (Graphics, Stage) => Unit

  def paintStage(stage: Stage)

  def postPaintStage: Unit = {
    // Do nothing by default
  }

  def max: Max
  
  def playEndless(stages: List[Stage], fieldSize: Int): Unit = {
    val max = Max(2 * fieldSize, 2 * fieldSize)
    while (true) {
      for (s <- stages) {
        paintStage(s)
        postPaintStage
      }
    }
  }

  def playOnes(stages: List[Stage], fieldSize: Int): Unit = {
    val max = Max(2 * fieldSize, 2 * fieldSize)
    for (s <- stages) {
      paintStage(s)
      postPaintStage
    }
    println("finished play")
  }

  protected def paintStageToGraphics(g: Graphics, stage: Stage): Unit = {
    g.clear
    val visibleCans = stage.cans - stage.robot.pos
    g.paintField(max)
    for (c <- visibleCans) {
      g.paintCan(c, max)
    }
    g.paintRobot(stage.robot.pos, stage.robot.dir, max)
  }

}

trait Graphics {
  def drawArea: DrawArea
  def clear: Unit
  def paintField(max: Max)
  def paintCan(pos: Pos, max: Max)
  def paintRobot(pos: Pos, dir: Direction, max: Max)
}

  
