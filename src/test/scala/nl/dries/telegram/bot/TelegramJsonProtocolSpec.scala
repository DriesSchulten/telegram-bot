package nl.dries.telegram.bot

import java.util.Date
import org.json4s.Extraction
import org.json4s.JsonAST.JNothing
import org.scalatest._

/**
  * Model/JSON spec
  */
class TelegramJsonProtocolSpec extends FunSuite with Matchers with Json4sSupport with TelegramJsonProtocol {

  val timeInSeconds = new Date().getTime / 1000

  test("A message should be able to serialize to/from JSON with a user") {
    val user = User(2, "Dries", Some("Schulten"), Some("Dries_Schulten"))
    val message = Message(1, new Date(timeInSeconds * 1000), Some("text"), Left(user), user)

    val json = write(message)
    read[Message](json) shouldBe message
  }

  test("A message should be able to serialize to/from JSON with a group chat") {
    val user = User(2, "Dries", Some("Schulten"), None)
    val group = GroupChat(3, "Group chat")
    val message = Message(1, new Date(timeInSeconds * 1000), Some("message"), Right(group), user)

    val json = write(message)
    read[Message](json) shouldBe message
  }

  test("A sendable message should convert the user (sender) to a 'chat_id' property") {
    val sender = User(1, "Test", None, Some("test"))
    val sendableText = SendableText(sender, "The message content")

    val json = Extraction.decompose(sendableText)
    json \ "chat_id" shouldNot be(JNothing)
  }

  test("A sendable message should convert the group (sender) to a 'chat_id' property") {
    val sender = GroupChat(5, "Test gruop")
    val sendableText = SendableText(sender, "The message content")

    val json = Extraction.decompose(sendableText)
    json \ "chat_id" shouldNot be(JNothing)
  }
}
