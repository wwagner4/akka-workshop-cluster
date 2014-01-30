package clashcode.video

object EffectiveField {

  def calc(outer: DrawArea, widthHeightRatio: Double, border: Int): DrawArea = {
    require(widthHeightRatio <= 1.0)
    val outerRatio = (outer.area.h).toDouble / (outer.area.w)
    if (outerRatio <= widthHeightRatio) {
      val oborder = ((outer.area.w - 2 * border).toDouble / 2.0).toInt
      val x = outer.offset.x + oborder + border
	  val y = outer.offset.y + border
      val h = outer.area.h - 2 * border
      val w = (h / widthHeightRatio).toInt
      DrawArea(Pos(x, y), Rec(w, h))
    } else {
      val oborder = ((outer.area.w - 2 * border).toDouble / 2.0).toInt
      val x = outer.offset.x + border
      val y = outer.offset.y + oborder + border
      val w = outer.area.w - 2 * border
      val h = (w * widthHeightRatio).toInt
      DrawArea(Pos(x, y), Rec(w, h))
    }

  }

}

object EffectiveOffset {
  def calc (pos: Pos, max: Max, field: DrawArea): Pos = {
    val fw = field.area.w.toDouble / max.x
    val x = field.offset.x + (fw * pos.x).toInt
    val fh = field.area.h.toDouble / max.y
    val y = field.offset.y + (fh * pos.y).toInt
    Pos(x, y)
  }
}