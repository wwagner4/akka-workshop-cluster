package clashcode.video

import java.awt.Graphics2D
import clashcode.video.swing._
import scala.concurrent.duration._
import clashcode.video.lists._

object VideoMain extends App {

  val framesPerSecond = 5

  //val vl = AkkaWorkshopPresentationVideos.videos
  //val vl = AkkaWorkshopWinnerVideos.winner
  //val vl = AkkaWorkshopWinnerVideos.next
  //val vl = AkkaWorkshopWinnerVideos.noPhilip
  val vl = AkkaWorkshopWinnerVideos.stuck
  //val vl = List(AkkaWorkshopResultsVideos.v001)

  val params = StageParams(10, ImageProvider_V01, 0.7, 0.1)
  val stages = VideoCreator.create(vl, framesPerSecond)

  val device: Device = new SwingDevice(framesPerSecond, params)
  //val device: Device = new ImagesDevice

  device.playOnes(stages)
  //device.playEndless(stages)

}

