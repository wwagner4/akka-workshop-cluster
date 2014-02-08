package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._

object VideoMain extends App {

//  val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
  val strCode = "51301322323332310310015011211525211111100030050100035520005502144444444444444444114441114434454345441441440045000444300140034051"
  val stages = SceneCreator.stringCodeToStages(strCode, 249874L)

  val device = new SwingDevice(createGraphics)

  def createGraphics(g: Graphics2D): AwtGraphics = new AwtRectGraphicsImages(0.7, 0.05) {

    def graphics: Graphics2D = g
    def drawArea: DrawArea = device.drawArea

  }

  Video.play(device, stages, Max(20, 20), 15)

}