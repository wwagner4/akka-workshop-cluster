package clashcode.video

import org.scalatest.FunSuite


class EffectiveFieldSuite extends FunSuite {
  
  test("Wide offset 1 1") {
    val r = 0.5
    val outer = DrawArea(Pos(1, 1), Rec(13, 8))
    val b = 1
    val bt = 2
    val eff = EffectiveField.calc(outer, r, b, bt)
    assert(eff === DrawArea(Pos(2, 4), Rec(8, 4)))
  }
  
  test("Wide offset 2 1") {
    val r = 0.5
    val outer = DrawArea(Pos(2, 1), Rec(12, 8))
    val b = 1
    val bt = 2
    val eff = EffectiveField.calc(outer, r, b, bt)
    assert(eff === DrawArea(Pos(3, 4), Rec(7, 4)))
  }
  
  test("Wide offset 6 1") {
    val r = 0.5
    val outer = DrawArea(Pos(6, 1), Rec(8, 8))
    val b = 1
    val bt = 2
    val eff = EffectiveField.calc(outer, r, b, bt)
    assert(eff === DrawArea(Pos(7, 4), Rec(6, 3)))
  }
  

}