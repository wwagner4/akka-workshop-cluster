package clashcode.video

import clashcode.robot.FieldEvaluator
import clashcode.robot.FieldFactory
import clashcode.robot.FieldPos
import scala.util.Random
import clashcode.robot.Converter

case object SceneCreator {

  def stringCodeToStages(strCode: String, fieldSize: Int, seed: Long): List[Stage] = {
    val ran = new Random(seed)
    
    def stepsToStages(steps: List[FieldStep], robot: RobotView, fieldSize: Int): List[Stage] = steps match {
      case Nil => Nil
      case s :: r => {
        val stages = PathUtil.stepToStages(s, robot, fieldSize, ran)
        val lastRobot = stages.last.robot
        stages ::: stepsToStages(r, lastRobot, fieldSize)
      }
    }

    val path = PathUtil.strCodeToPath(strCode, ran)
    val steps = PathUtil.pathToSteps(path);
    if (steps.size == 0) Nil
    else {
      val startField = steps(0).from
      val startRobot = RobotView(Pos(startField.x * 2 + 1, startField.y * 2 + 1), S)
      stepsToStages(steps, startRobot, fieldSize)
    }

  }

}

case class FieldStep(from: FieldPos, to: FieldPos)

case object DirectionUtil {
  def turnRight(actualDir: Direction): Direction = actualDir match {
    case N => NE
    case NE => E
    case E => SE
    case SE => S
    case S => SW
    case SW => W
    case W => NW
    case NW => N
  }
  def turnLeft(actualDir: Direction): Direction = actualDir match {
    case N => NW
    case NW => W
    case W => SW
    case SW => S
    case S => SE
    case SE => E
    case E => NE
    case NE => N
  }
  def diff(from: Direction, to: Direction): Int = {
    def toNum(dir: Direction): Int = dir match {
      case N => 0
      case NE => 1
      case E => 2
      case SE => 3
      case S => 4
      case SW => 5
      case W => 6
      case NW => 7
    }
    val d = math.abs(toNum(from) - toNum(to))
    if (d > 4) d - 8 else d
  }

  def turnList(startDirection: Direction, times: Int): List[Direction] = {
    if (times == 0) Nil
    else if (times > 0) {
      val nextDir = turnRight(startDirection)
      nextDir :: turnList(nextDir, times - 1)
    } else {
      val nextDir = turnLeft(startDirection)
      nextDir :: turnList(nextDir, times + 1)
    }
  }
}

case object PathUtil {

  val dummyCans = Set.empty[Pos]

  def pathToSteps(path: List[FieldPos]): List[FieldStep] = {
    path match {
      case Nil => throw new IllegalStateException("path must contain at least two positions")
      case a :: Nil => Nil
      case a :: b :: r => FieldStep(a, b) :: pathToSteps(b :: r)
    }
  }

  def strCodeToPath(strCode: String, ran: Random): List[FieldPos] = {
    val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
    val decisions = Converter.toDecisions(code)
    val f = FieldFactory.createRandomField(ran, 10)
    FieldEvaluator.evaluate(decisions, f, ran).path
  }

  def stepToStages(step: FieldStep, robot: RobotView, fieldSize: Int, ran: Random): List[Stage] = {
    def turn(nextDir: Direction): List[Stage] = {
      val prevDir = robot.dir
      val diff = DirectionUtil.diff(prevDir, nextDir)
      DirectionUtil.turnList(robot.dir, diff).map(d => Stage(RobotView(robot.pos, d), dummyCans))
    }
    def move(nextDir: Direction): List[Stage] = nextDir match {
      case N => List(
        Stage(RobotView(Pos(robot.pos.x, robot.pos.y - 1), nextDir), dummyCans),
        Stage(RobotView(Pos(robot.pos.x, robot.pos.y - 2), nextDir), dummyCans))
      case E => List(
        Stage(RobotView(Pos(robot.pos.x + 1, robot.pos.y), nextDir), dummyCans),
        Stage(RobotView(Pos(robot.pos.x + 2, robot.pos.y), nextDir), dummyCans))
      case S => List(
        Stage(RobotView(Pos(robot.pos.x, robot.pos.y + 1), nextDir), dummyCans),
        Stage(RobotView(Pos(robot.pos.x, robot.pos.y + 2), nextDir), dummyCans))
      case W => List(
        Stage(RobotView(Pos(robot.pos.x - 1, robot.pos.y), nextDir), dummyCans),
        Stage(RobotView(Pos(robot.pos.x - 2, robot.pos.y), nextDir), dummyCans))
      case _ => throw new IllegalArgumentException(s"Robot can only move N, E, S or W. Not $nextDir")
    }
    if (step.from.x == step.to.x && step.from.y == step.to.y) {
      val nextDir = if (ran.nextBoolean) DirectionUtil.turnRight(robot.dir)
      else DirectionUtil.turnLeft(robot.dir)
      val nextRobot = RobotView(robot.pos, nextDir)
      // TODO The cans must come somehow out of the step to be able to check if cans where collected
      List(Stage(robot, dummyCans))
    } else {
      val ndir: Direction = nextDirection(step, fieldSize)
      turn(ndir) ::: move(ndir)
    }
  }

  def nextDirection(step: FieldStep, fieldSize: Int): Direction = {
    def assertInBounds(value: Int): Unit = {
      if (value < 0 || value >= fieldSize) throw new IllegalArgumentException(s"Value $value is out of bound for field size $fieldSize. $step")
    }
    assertInBounds(step.from.x)
    assertInBounds(step.from.y)
    assertInBounds(step.to.x)
    assertInBounds(step.to.y)
    if (step.from.x == step.to.x) {
      if (step.from.y > step.to.y) {
        if (step.from.y - step.to.y > 1) throw new IllegalArgumentException(s"The step in y direction is greater than one. $step")
        else N
      } else if (step.from.y < step.to.y) {
        if (step.to.y - step.from.y > 1) throw new IllegalArgumentException(s"The step in y direction is greater than one. $step")
        else S
      } else throw new IllegalArgumentException(s"No movement. $step")
    } else if (step.from.y == step.to.y) {
      if (step.from.x > step.to.x) {
        if (step.from.x - step.to.x > 1) throw new IllegalArgumentException(s"The step in x direction is greater than one. $step")
        else W
      } else if (step.from.x < step.to.x) {
        if (step.to.x - step.from.x > 1) throw new IllegalArgumentException(s"The step in x direction is greater than one. $step")
        else E
      } else throw new IllegalArgumentException(s"No movement. $step")
    } else throw new IllegalArgumentException(s"Moved along two axes. $step")
  }
}
  
