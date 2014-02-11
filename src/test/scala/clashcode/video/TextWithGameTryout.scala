package clashcode.video

import scala.concurrent.duration._


object TextWithGameTryout extends App {

  val video = Video("A really bad robot\nShowing only the first steps, the rest is boring\n\nfitness = -10",
    3.second,
    "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143",
    Some(50),
    238476L)

  val framesPerSecond = 15

  val stages = VideoCreator.create(video: Video, framesPerSecond)
  val device: Device = SwingDeviceFactory(framesPerSecond).device
  device.playOnes(stages)

}

