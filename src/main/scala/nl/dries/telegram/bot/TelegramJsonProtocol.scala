package nl.dries.telegram.bot

import java.util.Date

sealed trait MessageSender {
  def id: Int
}

case class Message(messageId: Int, date: Date, text: Option[String], chat: Either[User, GroupChat], from: User)

case class Update(updateId: Int, message: Message)

case class User(id: Int, firstName: String, lastName: Option[String], username: Option[String]) extends MessageSender

case class GroupChat(id: Int, title: String) extends MessageSender

case class OperationResult[T](ok: Boolean, result: T)

sealed trait SendableMessage {
  def chat: MessageSender
}

case class SendableText(chat: MessageSender, text: String) extends SendableMessage

trait TelegramJsonProtocol {

  import org.json4s.JsonDSL._
  import org.json4s._

  class SendableMessageSerializer extends CustomSerializer[SendableMessage](format => ( {
    case _ => throw new MappingException("Cannot read 'SendableMessage' from JSON.")
  }, {
    case msg: SendableText => ("text" -> msg.text) ~ ("chat_id" -> msg.chat.id)
  }))

  class EpochDateSerializer extends CustomSerializer[Date](format => ( {
    case JInt(seconds) => new Date(seconds.toLong * 1000)
  }, {
    case d: Date => JInt(d.getTime / 1000)
  }))

  implicit val formats: Formats = DefaultFormats + new SendableMessageSerializer + new EpochDateSerializer
}
