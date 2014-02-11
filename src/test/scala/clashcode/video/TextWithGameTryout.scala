package clashcode.video

import scala.concurrent.duration._

case class Video(text: String, textDuration: Duration, code: String, gameSteps: Option[Int], seed: Long)

object TextWithGameTryout extends App {

  val video = Video("A really bad robot\nShowing only the first steps, the rest is boring\n\nfitness = -10",
    3.second,
    "03530311022335213110315511111120251141140200400110522540004423424544141444444444142541204404414145445445424454151340002434334143",
    Some(50),
    238476L)

  val framesPerSecond = 15

  val stages = VideoCreator.create(video: Video, framesPerSecond)
  val device: Device = SwingDeviceFactory(framesPerSecond, stages.fieldSize).device
  device.playOnes(stages)

}

case object VideoCreator {

  def create(video: Video, framesPerSecond: Int): Stages = {
    val txt = video.text.split("\n").toList
    val txtStage = TextStage(Text(txt))
    val dur = video.textDuration.toMillis.toDouble / 1000
    val framesCount = math.max((framesPerSecond * dur).toInt, 1)
    val txtStages = List.fill(framesCount)(txtStage)
    val gameStages = SceneCreator.stringCodeToStages(video.code, video.gameSteps, video.seed)
    Stages(txtStages ::: gameStages.stages, gameStages.fieldSize)
  }

}