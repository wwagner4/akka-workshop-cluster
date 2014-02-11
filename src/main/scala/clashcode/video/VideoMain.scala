package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._

object VideoMain extends App {

  val v02_01 = Video("The very beginning\nDoes not collect anything\nfitness = 15051\ngeneration = -12026",
    6.second,
    "42145244515403530131540422512315102255411253023101403042025344053035202022001230222445254235425225545025324555014311303323455441",
    Some(20),
    238476L)

  val v02_07 = Video("Collects some cans and gets stuck\nfitness = 15051\ngeneration = 700",
    6.second,
    "12011402000342202311222542432551525201224303511505200001011243514434402402340452424421454434415022245044221411342433534030432422",
    Some(30),
    238476L)

  val v02_09 = Video("Pretty good but gets stuck in the corner\nfitness = 81738\ngeneration = 1220",
    6.second,
    "02311052233004032311432332322141521214240030414300301253531533104444424424443554444430424452453224445245414453252444204124422424",
    Some(120),
    238476L)

  val v02_10 = Video("The best robot of that breeding\nCollects all but one\nfitness = 97300\ngeneration = 2320",
    6.second,
    "32311522203010132211152122322333351230220100202020002030421500534444444424444101414401454442424415441544444415252444434104413251",
    None,
    238476L)

    
    val framesPerSecond = 10
  
  val stages = List(v02_01, v02_07, v02_09, v02_10).flatMap(v => VideoCreator.create(v, framesPerSecond))
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