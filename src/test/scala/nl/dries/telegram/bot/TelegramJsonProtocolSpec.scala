package nl.dries.telegram.bot

import java.util.Date

import nl.dries.telegram.bot.TelegramJsonProtocol._
import org.scalatest._
import spray.json._

/**
 * Model/JSON spec
 */
class TelegramJsonProtocolSpec extends FunSuite with ShouldMatchers {

  val timeInSeconds = new Date().getTime / 1000

  test("A message should be able to serialize to/from JSON with a user") {
    val user = User(2, "Dries", "Schulten", "Dries_Schulten")
    val message = Message(1, new Date(timeInSeconds * 1000), Some("text"), Left(user), user)

    val json = message.toJson
    assert(json.convertTo[Message] === message)
  }

  test("A message should be able to serialize to/from JSON with a group chat") {
    val user = User(2, "Dries", "Schulten", "Dries_Schulten")
    val group = GroupChat(3, "Group chat")
    val message = Message(1, new Date(timeInSeconds * 1000), Some("message"), Right(group), user)

    val json = message.toJson
    assert(json.convertTo[Message] === message)
  }

  test("A sendable message should convert the user (sender) to a 'chat_id' property") {
    val sender = User(1, "Test", "User", "test")
    val sendableText = SendableText(sender, "The message content")

    val json = sendableText.toJson
    assert(json.asJsObject.fields.get("chat_id").isDefined)
  }

  test("A sendable message should convert the group (sender) to a 'chat_id' property") {
    val sender = GroupChat(5, "Test gruop")
    val sendableText = SendableText(sender, "The message content")

    val json = sendableText.toJson
    assert(json.asJsObject.fields.get("chat_id").isDefined)
  }
}
