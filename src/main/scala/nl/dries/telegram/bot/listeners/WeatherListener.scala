package nl.dries.telegram.bot.listeners

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import nl.dries.telegram.bot.ChatHandler.SendMessage
import nl.dries.telegram.bot._
import nl.dries.telegram.bot.listeners.WeatherListener.{WeatherCommandRequest, WeatherCommandResult}
import spray.http.Uri
import spray.httpx.RequestBuilding._

import scala.concurrent.duration._
import scala.util.parsing.combinator._
import scala.util.{Failure, Success}

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

object WeatherListener {

  case class WeatherCommandRequest(command: WeatherCommand, replyTo: MessageSender)

  case class WeatherCommandResult(request: WeatherCommandRequest, result: Weather)

  def props(poller: ActorRef) = Props(classOf[WeatherListener], poller)
}

/**
  * Weather bot listener
  */
class WeatherListener(poller: ActorRef) extends Actor with ActorLogging {

  import WeatherParser._
  import nl.dries.telegram.bot.UpdatePoller._

  val appConfig = AppConfig(context.system)

  val chatActor = context.actorOf(ChatHandler.props(Uri(appConfig.botApi)))

  val commandHandler = context.actorOf(WeatherCommandHandler.props(Uri(appConfig.weatherApi), appConfig.weatherToken))

  poller ! AddListener(self)

  override def receive: Receive = {
    case NewUpdate(update) if update.message.text.isDefined =>
      parseCommand(update.message.text.get) match {
        case WeatherParser.Success(command, _) =>
          commandHandler ! WeatherCommandRequest(command, update.message.chat.fold(u => u, g => g))
        case _ => log.debug("No command parsed, ignoring")
      }

    case WeatherCommandResult(request, result) =>
      val reply = new SendableText(request.replyTo, result.toString)
      chatActor ! SendMessage(reply)
  }
}

object WeatherCommandHandler {
  def props(weatherApi: Uri, weatherToken: String) = Props(classOf[WeatherCommandHandler], weatherApi, weatherToken)
}

class WeatherCommandHandler(weatherApi: Uri, weatherToken: String) extends Actor with ActorLogging with JsonCommunicator with WeatherJsonProtocol {

  import context.dispatcher

  implicit val requestTimeout = Timeout(5.seconds)

  override def receive: Receive = {
    case req: WeatherCommandRequest =>
      val origSender = sender()

      val params = List(("appid", weatherToken), ("units", "metric")) ++ {
        req.command match {
          case GetWeatherInCity(city) => List(("q", city))
          case GetWeatherOnLocation(lat, lng) => List(("lat", lat.toString), ("lng", lng.toString))
        }
      }

      sendRequest[Weather](Get(weatherApi.withQuery(params.toArray: _*))) onComplete {
        case Success(weather) => origSender ! WeatherCommandResult(req, weather)
        case Failure(ex) => log.error("Error retrieving weather", ex)
      }
  }
}
