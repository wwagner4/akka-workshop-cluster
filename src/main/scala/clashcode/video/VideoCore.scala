package clashcode.video

case class Pos(x: Int, y: Int)
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

trait ImageProvider {

  def robots: Map[Direction, VideoImage]
  def can: VideoImage

}

case class StageParams(
  fieldSize: Int,
  imgProvider: ImageProvider,
  widthHeightRatio: Double, border: Double)

sealed trait CommonColor
case object Black extends CommonColor
case object White extends CommonColor

case class VideoImage(image: java.net.URL, centerx: Double, centery: Double, shrinkFactor: Int)

trait CommonGraphics {

  def drawImage(vimg: VideoImage, pos: Pos, scale: Double)
  def setColor(c: CommonColor)
  def drawLine(fromx: Int, fromy: Int, tox: Int, toy: Int)
  def drawRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def fillRect(p1x: Int, p1y: Int, p2x: Int, p2y: Int)
  def setFontSize(size: Double)
  def drawString(str: String, x: Int, y: Int)

}

sealed trait Stage {
  
  def paint(g: CommonGraphics, drawArea: () => DrawArea, params: StageParams): Unit

  // Utillity methods to be used in the implementation of paint
  
  def clear(g: CommonGraphics, drawArea: DrawArea): Unit = {
    g.setColor(White)
    val x = drawArea.offset.x
    val y = drawArea.offset.y
    val w = drawArea.area.w
    val h = drawArea.area.h
    g.fillRect(x, y, w, h)
  }

}

case class RobotView(pos: Pos, dir: Direction)


case class GameStage(robot: RobotView, cans: Set[Pos]) extends Stage {

  def paint(g: CommonGraphics, drawArea: () => DrawArea, params: StageParams): Unit = {

    // Calculate the current DrawArea
    val da = drawArea()

    def paintField: Unit = {
      g.setColor(Black)
      val field = EffectiveField.calc(da, params.widthHeightRatio, params.border)
      (0 to (params.fieldSize) - 1).foreach(i => {
        val fw = field.area.w / (params.fieldSize)
        val d = i * fw
        g.drawLine(field.offset.x + d, field.offset.y, field.offset.x + d, field.offset.y + field.area.h)
      })
      (0 to (params.fieldSize) - 1).foreach(i => {
        val fh = field.area.h / (params.fieldSize)
        val d = i * fh
        g.drawLine(field.offset.x, field.offset.y + d, field.offset.x + field.area.w, field.offset.y + d)
      })
      g.drawRect(field.offset.x, field.offset.y, field.area.w, field.area.h)
    }
    def paintVideoImage(vimg: VideoImage, pos: Pos): Unit = {
      val effField = EffectiveField.calc(da, params.widthHeightRatio, params.border)
      val effPos: Pos = EffectiveOffset.calc(pos, params.fieldSize, effField)
      val scale = effField.area.w.toDouble / vimg.shrinkFactor
      g.drawImage(vimg, effPos, scale)
    } 

    def paintRobot(pos: Pos, dir: Direction): Unit = {
      paintVideoImage(params.imgProvider.robots(dir), pos)
    }

    clear(g, da)
    val visibleCans = cans - robot.pos
    paintField
    for (canPos <- visibleCans) {
    	paintVideoImage(params.imgProvider.can, canPos)
    }
    paintVideoImage(params.imgProvider.robots(robot.dir), robot.pos)
  }
}

case class Text(lines: List[String])

case class TextStage(text: Text) extends Stage {
  def paint(g: CommonGraphics, drawArea: () => DrawArea, params: StageParams): Unit = {

    def paintText(text: Text, drawArea: DrawArea) = {
      g.setColor(Black)
      val fontSize = drawArea.area.h.toFloat / 20
      g.setFontSize(fontSize)
      val lines = text.lines
      for (i <- 0 until lines.size) {
        if (i == 1) {
          val fontSize = drawArea.area.h.toFloat / 40
          g.setFontSize(fontSize)
        }
        val y = (10 + fontSize * (i + 1)).toInt
        g.drawString(lines(i), 30, y)
      }
    }

    val da = drawArea()
    clear(g, da)
    paintText(text, da)
  }
}

case class NumberedStage(nr: Int, stage: Stage)

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

object ImageProvider_V02 extends ImageProvider {

  def img(resName: String): java.net.URL = {
    this.getClass().getClassLoader().getResource(resName)
  }

  lazy val robots: Map[Direction, VideoImage] = {
    val imgNames = List(
      (S, "img/robots/r00.png"),
      (SE, "img/robots/r01.png"),
      (E, "img/robots/r02.png"),
      (NE, "img/robots/r03.png"),
      (N, "img/robots/r04.png"),
      (NW, "img/robots/r05.png"),
      (W, "img/robots/r06.png"),
      (SW, "img/robots/r07.png"))
    imgNames.map { case (key, name) => (key, VideoImage(img(name), 0.5, 0.6, 2000)) }.toMap
  }

  lazy val can: VideoImage = {
    VideoImage(img("img/cans/can.png"), 0.5, 0.5, 2000)
  }
}

object ImageProvider_V01 extends ImageProvider {

  def img(resName: String): java.net.URL = {
    this.getClass().getClassLoader().getResource(resName)
  }

  lazy val robots: Map[Direction, VideoImage] = {
    val imgNames = List(
      (S, "img01/robots/r00.png"),
      (SE, "img01/robots/r01.png"),
      (E, "img01/robots/r02.png"),
      (NE, "img01/robots/r03.png"),
      (N, "img01/robots/r04.png"),
      (NW, "img01/robots/r05.png"),
      (W, "img01/robots/r06.png"),
      (SW, "img01/robots/r07.png"))
    imgNames.map { case (key, name) => (key, VideoImage(img(name), 0.47, 0.7, 2300)) }.toMap
  }

  lazy val can: VideoImage = {
    VideoImage(img("img01/cans/can.png"), 0.5, 0.65, 1900)
  }
}



