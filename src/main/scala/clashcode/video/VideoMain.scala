package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._

object VideoMain extends App {

  val v1 = Video("A really bad robot\nShowing only the first steps, the rest is boring\n\nfitness = -10",
    3.second,
    "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143",
    Some(30),
    238476L)

  val v2 = Video("Another robot\n\nfitness = 97840",
    2.second,
    "32211522203053512111122122322133011214240300302120305251330342334444432414443414434442434444444422144242403414212434224453421212",
    None,
    238476L)

  val framesPerSecond = 15
  
  val stages = List(v1, v2).flatMap(v => VideoCreator.create(v, framesPerSecond))
  val device: Device = SwingDeviceFactory(framesPerSecond).device
  device.playOnes(stages)

}

case class SwingDeviceFactory(framesPerSecond: Int) {
  
  def device: Device = _device
  
  private lazy val _device: SwingDevice = new SwingDevice(framesPerSecond, createGraphics)

  private def createGraphics(g: Graphics2D): AwtGraphics = {
    val useKacheln = false
    new ImageAwtGraphics(ImageProvider_V01, useKacheln, 0.6, 0.07) {
      def graphics: Graphics2D = g
      def drawArea: DrawArea = _device.determineCalcArea
    }
  }
}