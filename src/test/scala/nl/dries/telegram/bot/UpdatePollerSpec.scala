package nl.dries.telegram.bot

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import nl.dries.telegram.bot.UpdateRunner.TriggerUpdate
import org.scalatest.{FunSuiteLike, Matchers}

import scala.concurrent.duration._

/**
 * Update poller specs
 */
class UpdatePollerSpec extends TestKit(ActorSystem("update-spec")) with FunSuiteLike with Matchers {

  test("Actor should trigger runner when starting up") {
    val runner = TestProbe()
    val poller = system.actorOf(UpdatePoller.props, "poller")

    runner.expectMsg(2.seconds, TriggerUpdate(0))
  }

}
