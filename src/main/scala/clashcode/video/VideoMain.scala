package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._

object VideoMain extends App {

  val fieldSize = 10
  val framesPerSecond = 20
  val seed = 1L

  val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
  //val strCode = "51301322323332310310015011211525211111100030050100035520005502144444444444444444114441114434454345441441440045000444300140034051"
  //val strCode = "51301351323352311515012111511323201151120550440004411221333414434444304444444444411451544454454344443442040305454535030515343550"
  val stages = SceneCreator.stringCodeToStages(strCode, fieldSize, seed)
  val device = SwingDeviceFactory(20).device
  device.playOnes(stages, fieldSize)

}

case class SwingDeviceFactory(framesPerSecond: Int) {
  
  def device: Device = _device
  
  private lazy val _device: SwingDevice = new SwingDevice(framesPerSecond, createGraphics)

  private def createGraphics(g: Graphics2D): AwtGraphics = {
    new AwtRectGraphicsImages(0.6, 0.05) {
      def graphics: Graphics2D = g
      def drawArea: DrawArea = _device.drawArea
    }
  }
}