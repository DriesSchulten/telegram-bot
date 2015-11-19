package nl.dries.telegram.bot.listeners

import akka.actor.{Actor, ActorLogging, ActorRef}

import scala.util.parsing.combinator._

sealed trait WeatherCommand

case class GetWeatherInCity(location: String) extends WeatherCommand

case class GetWeatherOnLocation(lat: Double, lng: Double) extends WeatherCommand

/**
  * Parse weather commands
  */
object WeatherParser extends RegexParsers {
  private def getWeatherInCity = literal("/getweather") ~ argument ^^ { case _ ~ city => GetWeatherInCity(city) }

  private def getWeatherOnLocation = literal("/getweather") ~ doubleArgument ~ doubleArgument ^^ { case _ ~ lat ~ lng => GetWeatherOnLocation(lat, lng) }

  private def argument = regex("[a-zA-Z]+[ a-zA-Z]*".r)

  private def doubleArgument = regex("[0-9]+\\.[0-9]+".r) ^^ { value => value.toDouble }

  private def commands = getWeatherInCity | getWeatherOnLocation

  def parseCommand(command: String) = parse(commands, command)
}

/**
  * Weather bot listener
  */
class WeatherListener(poller: ActorRef) extends Actor with ActorLogging {

  import nl.dries.telegram.bot.UpdatePoller._

  override def receive: Receive = {
    case NewUpdate(update) if update.message.text.isDefined =>
      println("Update")
  }
}
