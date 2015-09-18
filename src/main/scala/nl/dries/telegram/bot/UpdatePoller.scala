package nl.dries.telegram.bot

import akka.actor.{Cancellable, Props, Actor}
import nl.dries.telegram.bot.UpdatePoller.{StopPolling, TriggerUpdate, StartPolling}

object UpdatePoller {

  case object StartPolling

  case object StopPolling

  case class TriggerUpdate(offset: Int)

  def props() = Props(classOf[UpdatePoller])
}

class UpdatePoller extends Actor {

  /** Request timeout to use */
  val timeout: Int = 20

  /** Last received counter */
  var lastReceivedUpdate: Option[Int] = None

  /** Cancel current update (if in progress) */
  var updateCancellable: Option[Cancellable] = None

  override def receive: Receive = {
    case StartPolling =>
      self ! TriggerUpdate(lastReceivedUpdate.getOrElse(0))

    case StopPolling =>
      updateCancellable map { _.cancel() }te
      updateCancellable = None

    case TriggerUpdate(offset) =>

  }
}
