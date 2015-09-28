package nl.dries.telegram.bot

import nl.dries.telegram.bot.TelegramJsonProtocol._
import org.scalatest._
import spray.json._

/**
 * Model/JSON spec
 */
class TelegramJsonProtocolSpec extends FunSuite with ShouldMatchers {

  test("A message should be able to serialize to/from JSON with a user") {
    val user = User(2, "Dries", "Schulten", "Dries_Schulten")
    val message = Message(1, 12356, Some("text"), Left(user), user)

    val json = message.toJson
    assert(json.convertTo[Message] === message)
  }

  test("A message should be able to serialize to/from JSON with a group chat") {
    val user = User(2, "Dries", "Schulten", "Dries_Schulten")
    val group = GroupChat(3, "Group chat")
    val message = Message(1, 12305, Some("message"), Right(group), user)

    val json = message.toJson
    assert(json.convertTo[Message] === message)
  }
}