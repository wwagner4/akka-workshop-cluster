package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._

object VideoMain extends App {

  val framesPerSecond = 50
  val seed = 1L

  //val strCode = "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143"
  //val strCode = "51301322323332310310015011211525211111100030050100035520005502144444444444444444114441114434454345441441440045000444300140034051"
  val strCode = "51301351323352311515012111511323201151120550440004411221333414434444304444444444411451544454454344443442040305454535030515343550"
  //val strCode = "32211522203053512111122122322133011214240300302120305251330342334444432414443414434442434444444422144242403414212434224453421212"
  val stages = SceneCreator.stringCodeToStages(strCode, seed)
  val device: Device = SwingDeviceFactory(framesPerSecond, stages.fieldSize).device
  device.playEndless(stages)

}

case class SwingDeviceFactory(framesPerSecond: Int, fieldSize: Int) {
  
  def device: Device = _device
  
  private lazy val _device: SwingDevice = new SwingDevice(framesPerSecond, fieldSize, createGraphics)

  private def createGraphics(g: Graphics2D): AwtGraphics = {
    new ImageAwtGraphics(ImageProvider_V02, 0.6, 0.05) {
      def graphics: Graphics2D = g
      def drawArea: DrawArea = _device.determineCalcArea
    }
  }
}