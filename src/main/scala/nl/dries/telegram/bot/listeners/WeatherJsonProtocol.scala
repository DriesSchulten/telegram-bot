package nl.dries.telegram.bot.listeners

import org.json4s.DefaultFormats
import org.json4s.native.JsonParser
import spray.http.{HttpCharsets, HttpEntity, MediaTypes}
import spray.httpx.unmarshalling.Unmarshaller

case class Weather(description: String, temperature: Double, rain: Double, windSpeed: Double, direction: Int)

trait WeatherJsonProtocol {

  implicit val formats = DefaultFormats

  /**
    * Parse weather out of a JSON string
    * @param content given content in JSON
    * @return Weather object constructed out of JSON data
    */
  def parseWeather(content: String): Weather = {
    val json = JsonParser.parse(content)
    val description = (json \ "weather") (0) \ "description"
    val temperature = json \ "main" \ "temp"
    val rain = json \ "rain" \ "3h"
    val windSpeed = json \ "wind" \ "speed"
    val windDirection = json \ "wind" \ "deg"

    Weather(description.extract[String], temperature.extract[Double], rain.extract[Double], windSpeed.extract[Double], windDirection.extract[Int])
  }

  /**
    * Spray unmarshaller to extract 'Weather' from a JSON HTTP response
    */
  implicit val weatherUnmarshaller = Unmarshaller[Weather](MediaTypes.`application/json`) {
    case x: HttpEntity.NonEmpty => parseWeather(x.asString(HttpCharsets.`UTF-8`))
    case HttpEntity.Empty => throw new IllegalStateException("Not expecting empty result")
  }
}
