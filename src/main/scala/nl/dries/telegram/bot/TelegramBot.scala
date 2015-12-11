package nl.dries.telegram.bot

import akka.actor.ActorSystem
import nl.dries.telegram.bot.listeners.{WeatherListener, EchoListener}

/**
 * Bot entry-point
 */
object TelegramBot extends App {

  val system = ActorSystem("telegram-bot")
  val updatePoller = system.actorOf(UpdatePoller.props)

  val weatherListener = system.actorOf(WeatherListener.props(updatePoller))
}
