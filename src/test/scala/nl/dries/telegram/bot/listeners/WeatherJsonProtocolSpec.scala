package nl.dries.telegram.bot.listeners

import org.scalatest.{FunSuite, Matchers}

/**
  * Specs for the Weather JSON protocol
  */
class WeatherJsonProtocolSpec extends FunSuite with Matchers with WeatherJsonProtocol {

  test("A weather object should be deserialized from a JSON string") {
    val fileContent: Option[String] = for {
      resource <- Option(getClass.getResourceAsStream("/json/weather.json"))
      bufferedResource <- Option(io.Source.fromInputStream(resource))
      content <- Option(bufferedResource.mkString)
    } yield content

    fileContent match {
      case Some(json) => {
        val weather = parseWeather(json)
        weather.description shouldBe "broken clouds"
        weather.temperature shouldBe 293.25
      }
      case None => fail("Could not read test data")
    }
  }

}
