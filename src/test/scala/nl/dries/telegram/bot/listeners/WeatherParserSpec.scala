package nl.dries.telegram.bot.listeners

import org.scalatest._

/**
  * Parser specification
  */
class WeatherParserSpec extends FunSuite with Matchers {

  test("A command with an argument should be parsed correctly") {
    val arguments = "New York"
    val parsed = WeatherParser.parseCommand(s"/getweather $arguments")

    parsed shouldBe a[WeatherParser.Success[_]]
    parsed.get match {
      case loc: GetWeatherInCity => loc.location shouldBe arguments
      case _ => fail(s"Result not a $GetWeatherInCity")
    }
  }

  test("A incorrect command should yield an error") {
    val wrong = "command argument"
    val parsed = WeatherParser.parseCommand(wrong)

    parsed shouldBe a[WeatherParser.Failure]
  }
}
