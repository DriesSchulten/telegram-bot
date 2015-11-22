package nl.dries.telegram.bot.listeners

import WeatherJsonProtocol._
import org.scalatest.{FunSuite, Matchers}
import spray.json._

/**
  * Specs for the Weather JSON protocol
  */
class WeatherJsonProtocolSpec extends FunSuite with Matchers {

  test("A message should be deserialized") {
    val fileContent: Option[String] = for {
      resource <- Option(getClass.getResourceAsStream("/json/weather.json"))
      bufferedResource <- Option(io.Source.fromInputStream(resource))
      content <- Option(bufferedResource.mkString)
    } yield content

    fileContent match {
      case Some(jsonString) =>
        val json = jsonString.parseJson
        val weather = json.convertTo[Weather]

        weather.description shouldBe "Clouds"

      case None => fail("Couldn't parse test json file")
    }
  }

}
