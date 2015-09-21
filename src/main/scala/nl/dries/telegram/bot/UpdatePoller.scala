package nl.dries.telegram.bot

import akka.actor.{Actor, Props}

import scala.concurrent.duration._

object UpdatePoller {

  case object GetUpdates

  case class GetUpdatesResponse(updates: List[Update])

  def props() = Props(classOf[UpdatePoller])
}

class UpdatePoller extends Actor {

  import UpdatePoller._
  import UpdateRunner._
  import context.dispatcher

  /** Request timeout to use */
  val timeout: Int = 20

  /** Last received counter */
  var lastReceivedUpdate: Option[Int] = None

  /** Not yet retrieved updates, but already received via long-poll */
  var pendingUpdates = List.empty[Update]

  /** Runner actor */
  val botConfig = context.system.settings.config.getConfig("bot")
  val runner = context.system.actorOf(UpdateRunner.props(timeout, botConfig.getString("token")))

  /** Start requesting updates */
  context.system.scheduler.scheduleOnce(1 second, runner, TriggerUpdate(lastReceivedUpdate getOrElse 0))

  override def receive: Receive = {
    case GetUpdates =>
      sender() ! GetUpdatesResponse(pendingUpdates)
      pendingUpdates = List.empty[Update]

    case UpdateResult(updates) =>
      if (updates.nonEmpty) {
        lastReceivedUpdate = Some(updates.map(_.id).max)
        pendingUpdates = pendingUpdates ++ updates
      }
      runner ! TriggerUpdate(lastReceivedUpdate getOrElse 0)
  }
}
