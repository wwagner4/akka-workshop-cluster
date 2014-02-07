package clashcode

import org.scalatest.FunSuite
import clashcode.robot.Evaluator
import clashcode.robot.Converter
import clashcode.robot.FieldPos

class SceneCreatorSuite extends FunSuite {

  val name = "Create path from result"
  test(name) {
    val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
    val code: Array[Byte] = stringToCode(strCode)
    //println(code.toList)
    val decisions = Converter.toDecisions(code)
    val path = Evaluator.evaluate(decisions).path
    println(s"path.length=${path.length}")
    assert(path.length === 40000)
    assert(path.take(10) === List(FieldPos(1,0), FieldPos(2,0), FieldPos(2,0), FieldPos(3,0), FieldPos(3,0), FieldPos(4,0), FieldPos(5,0), FieldPos(5,0), FieldPos(6,0), FieldPos(7,0)))
  }

  def stringToCode(str: String): Array[Byte] = {
    str.map(c => (c - 48).toByte).toArray
  }

}