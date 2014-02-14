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

case class NumberedStage(nr: Int, stage: Stage)

sealed trait Stage {
  def paint(g: CommonGraphics): Unit
}

case class GameStage(robot: RobotView, cans: Set[Pos], fieldSize: Int) extends Stage {

  val max = Max(fieldSize * 2, fieldSize * 2)

  def paint(g: CommonGraphics): Unit = {
    g.clear
    val visibleCans = cans - robot.pos
    g.paintField(max)
    for (c <- visibleCans) {
      g.paintCan(c, max)
    }
    g.paintRobot(robot.pos, robot.dir, max)
  }

}

case class TextStage(text: Text) extends Stage {
  def paint(g: CommonGraphics): Unit = {
    g.clear
    g.paintText(text)
  }

}

trait Device {

  // Define how to paint a stage on that device
  def paintStage(stage: NumberedStage)

  def postPaintStage: Unit = {
    // Do nothing by default
  }

  def playEndless(stages: List[NumberedStage]): Unit = {
    assert(stages.nonEmpty, "Stages must not be empty")
    while (true) {
      stages.foreach(s => {
        paintStage(s)
        postPaintStage
      })
    }
  }

  def playOnes(stages: List[NumberedStage]): Unit = {
    stages.foreach(s => {
      paintStage(s)
      postPaintStage
    })
  }

}

/**
 * Abstraction level for Graphics
 * Can, but must not be used from Device implementations
 */

case class Text(lines: List[String])

trait CommonGraphics {
  def drawArea: DrawArea
  def clear: Unit
  def paintField(max: Max)
  def paintCan(pos: Pos, max: Max)
  def paintRobot(pos: Pos, dir: Direction, max: Max)
  def paintText(text: Text)
}

