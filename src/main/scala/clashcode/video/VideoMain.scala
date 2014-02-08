package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._

object VideoMain extends App {

  val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
  val stages = SceneCreator.stringCodeToStages(strCode, 249874L)

  val device = new SwingDevice(createGraphics)

  def createGraphics(g: Graphics2D): AwtGraphics = new AwtRectGraphicsImages(0.6, 0.05) {

    def graphics: Graphics2D = g
    def drawArea: DrawArea = device.drawArea

  }

  Video.play(device, stages, Max(20, 20), 15)

}