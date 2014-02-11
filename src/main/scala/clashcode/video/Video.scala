package clashcode.video
import scala.concurrent.duration._

case class Video(text: String, textDuration: Duration, code: String, gameSteps: Option[Int], seed: Long)

case object VideoCreator {

  def create(video: Video, framesPerSecond: Int): List[Stage] = {
    val txt = video.text.split("\n").toList
    val txtStage = TextStage(Text(txt))
    val dur = video.textDuration.toMillis.toDouble / 1000
    val framesCount = math.max((framesPerSecond * dur).toInt, 1)
    val txtStages = List.fill(framesCount)(txtStage)
    val gameStages = SceneCreator.stringCodeToStages(video.code, video.gameSteps, video.seed)
    txtStages ::: gameStages
  }

}