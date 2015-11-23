package nl.dries.telegram.bot.listeners

import org.scalatest.{FunSuite, Matchers}

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
  }

}
