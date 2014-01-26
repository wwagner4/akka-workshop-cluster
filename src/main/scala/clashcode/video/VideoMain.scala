package clashcode.video

import java.awt.Graphics2D
import clashcode.video.VideoMain.Pos
import clashcode.video.VideoMain.Direction

object VideoMain extends App {

  sealed trait Direction

  case object N extends Direction
  case object NE extends Direction
  case object E extends Direction
  case object SE extends Direction
  case object S extends Direction
  case object SW extends Direction
  case object W extends Direction
  case object NW extends Direction

  val maxx = 15
  val maxy = 9

  require(Maxval.valid(maxx))
  require(Maxval.valid(maxy))

  case class Pos(posx: Int, posy: Int)

  case class Robot(pos: Pos, dir: Direction)

  case class Stage(robot: Robot, cans: Set[Pos])

  case class Path(stages: List[Stage])

  def play(stages: List[Stage], g: Graphics): Unit = {
    for (s <- stages) {
      paintStage(g, s)
    }
  }

  def paintStage(g: Graphics, stage: Stage): Unit = {
    g.clear
    val visibleCans = stage.cans - stage.robot.pos
    g.paintField
    for(c <- visibleCans) {
      g.paintCan(c)
    }
    g.paintRobot(stage.robot.pos, stage.robot.dir)
  }
  
  def createGraphics: Graphics = ???
  
  val cans = Set(
    Pos(1, 4),
    Pos(2, 5),
    Pos(3, 14),
    Pos(10, 1))

  val cans1 = cans - Pos(3, 14)

  val stages = List(
    Stage(Robot(Pos(0, 0), W), cans),
    Stage(Robot(Pos(0, 1), S), cans),
    Stage(Robot(Pos(1, 1), SW), cans),
    Stage(Robot(Pos(1, 3), S), cans1))

  val sp = new SwingPlayer()  
    
  play(stages, sp.graphics)
  
}



trait Graphics {
	def clear: Unit
	def paintField
	def paintCan(pos: Pos)
	def paintRobot(pos: Pos, dir: Direction)
}

class AwtGraphics(g: Graphics2D) extends Graphics {
	def clear: Unit = ???
	def paintField = ???
	def paintCan(pos: Pos) = ???
	def paintRobot(pos: Pos, dir: Direction) = ???

}

class SwingPlayer {
 
  def graphics: Graphics = ???
  
}


object Maxval {

  def valid(value: Int): Boolean = {
    def isOdd: Boolean = {
      (value + 1) % 2 == 0
    }
    value >= 3 && isOdd
  }
}
  
