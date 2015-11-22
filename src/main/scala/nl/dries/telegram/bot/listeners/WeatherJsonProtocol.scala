package nl.dries.telegram.bot.listeners

import spray.json._

case class Weather(description: String, temperature: Double, rain: Double, windSpeed: Double, direction: Int)

object WeatherJsonProtocol extends DefaultJsonProtocol {

  implicit object WeatherJsonFormat extends RootJsonFormat[Weather] {
    override def read(json: JsValue): Weather = {
      json.asJsObject.getFields("weather", "main", "wind", "clouds", "rain") match {
        case Seq(JsArray(Vector(JsObject(weather))), JsObject(main), JsObject(wind), JsObject(clouds), JsObject(rain)) => {
          for {
            description <- weather.get("main") match {
              case Some(JsString(value)) => value
              case _ => throw new DeserializationException("Can't find description")
            }
            temp <- main.get("temp") match {
              case Some(JsNumber(value)) => value.toDouble
              case _ => throw new DeserializationException("Can't find temperature")
            }
          } yield Weather(description.toString, temp, 0, 0, 0)
        }
        case _ => throw new DeserializationException("Expected weather object")
      }
    }

    override def write(obj: Weather): JsValue = throw new IllegalStateException(s"Not possible, can't write '$Weather'")
  }

}
