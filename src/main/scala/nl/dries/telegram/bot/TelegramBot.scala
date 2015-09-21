package nl.dries.telegram.bot

import akka.actor.ActorSystem
import akka.pattern._
import akka.util.Timeout
import nl.dries.telegram.bot.UpdatePoller.{GetUpdates, GetUpdatesResponse}

import scala.concurrent.duration._
import scala.util.Success

/**
 * Bot entry-point
 */
object TelegramBot extends App {

  val system = ActorSystem("telegram-bot")
  val updatePoller = system.actorOf(UpdatePoller.props())

  import system.dispatcher

  system.scheduler.schedule(5 seconds, 10 seconds) {
    implicit val timeout = Timeout(10 seconds)
    val future = updatePoller ? GetUpdates
    future.onComplete {
      case Success(m) =>
        val res = m.asInstanceOf[GetUpdatesResponse]
        println(res.updates)
      case _ =>
        println("Error")
    }
  }
  System.in.read()

  system.shutdown()
}
