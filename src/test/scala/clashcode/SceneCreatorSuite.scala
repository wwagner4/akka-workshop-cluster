package clashcode

import org.scalatest.FunSuite
import clashcode.robot.Evaluator
import clashcode.robot.Converter
import clashcode.robot.FieldPos
import scala.util.Random
import clashcode.robot.FieldFactory
import clashcode.robot.FieldEvaluator

class SceneCreatorSuite extends FunSuite {

  test("Create path from result with original Evaluator") {
    def strCodeToPath(strCode: String): List[FieldPos] = {
      val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
      val decisions = Converter.toDecisions(code)
      Evaluator.evaluate(decisions).path
    }
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = strCodeToPath(strCode)
    assert(path.length === 40000)
    assert(path.take(10) === List(FieldPos(1, 0), FieldPos(2, 0), FieldPos(2, 0), FieldPos(3, 0), FieldPos(3, 0), FieldPos(4, 0), FieldPos(5, 0), FieldPos(5, 0), FieldPos(6, 0), FieldPos(7, 0)))
  }

  test("Create path from result with PathUtil") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, 200)
    assert(path.length === 200)
    assert(path.take(10) === List(FieldPos(4, 0), FieldPos(4, 1), FieldPos(4, 1), FieldPos(5, 1), FieldPos(5, 1), FieldPos(5, 2), FieldPos(5, 3), FieldPos(5, 3), FieldPos(4, 3), FieldPos(4, 4)))
  }

  test("Split path to steps") {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val path = PathUtil.strCodeToPath(strCode, 200).take(4)
    val steps: List[FieldStep] = pathToSteps(path)
    val expectedSteps = List(
      FieldStep(FieldPos(4, 0), FieldPos(4, 1)),
      FieldStep(FieldPos(4, 1), FieldPos(4, 1)),
      FieldStep(FieldPos(4, 1), FieldPos(5, 1)))
    assert(steps === expectedSteps)
  }

  def pathToSteps(path: List[FieldPos]): List[FieldStep] = {
    path match {
      case Nil => throw new IllegalStateException("path must contain at least two positions")
      case a :: Nil => Nil
      case a :: b :: r => FieldStep(a, b) :: pathToSteps(b :: r)
    }
  }
}

case class FieldStep(from: FieldPos, to: FieldPos)

case object PathUtil {

  def strCodeToPath(strCode: String, seed: Long): List[FieldPos] = {
    val code: Array[Byte] = strCode.map(c => (c - 48).toByte).toArray
    val decisions = Converter.toDecisions(code)
    val ran = new Random(seed)
    val f = FieldFactory.createRandomField(ran, 10)
    FieldEvaluator.evaluate(decisions, f, ran).path
  }
  
}
  
