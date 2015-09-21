package nl.dries.telegram.bot

import akka.actor.ActorSystem

/**
 * Bot entry-point
 */
object TelegramBot extends App {

  val system = ActorSystem("telegram-bot")
  val updatePoller = system.actorOf(UpdatePoller.props())
}
