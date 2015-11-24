package nl.dries.telegram.bot

import org.json4s.native.JsonMethods._
import org.json4s.{Extraction, Formats}
import spray.http.{HttpCharsets, HttpEntity, MediaTypes}
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling.Unmarshaller

/**
  * JSON4S serialize/deserialize support with changing the keys from/to camel case.
  */
trait Json4sSupport {

  /** Require set of formats */
  implicit val formats: Formats

  /**
    * Parses JSON to an object of a given type
    * @param json given JSON
    * @tparam T the type of the expected result
    * @return instance of T
    */
  def read[T: Manifest](json: String): T = parse(json).camelizeKeys.extract[T]

  /**
    * Serializes a given object to JSON
    * @param obj the given object
    * @tparam T the type of the obj
    * @return a JSON representation of the obj
    */
  def write[T <: AnyRef](obj: T): String = compact(render(Extraction.decompose(obj).snakizeKeys))
}

/**
  * Spray (un)marshaller for JSON4S
  */
trait SprayJson4sSupport extends Json4sSupport {

  implicit def json4sUnmarshaller[T: Manifest] = {
    Unmarshaller[T](MediaTypes.`application/json`) {
      case x: HttpEntity.NonEmpty => read(x.asString(HttpCharsets.`UTF-8`))
    }
  }

  implicit def json4sMarshaller[T <: AnyRef] = Marshaller.delegate[T, String](MediaTypes.`application/json`)(write(_))
}
