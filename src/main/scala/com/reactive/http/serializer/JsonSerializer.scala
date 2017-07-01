package com.reactive.http.serializer


import java.lang.reflect.InvocationTargetException

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.ContentTypeRange
import akka.http.scaladsl.model.MediaTypes.`application/json`
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import org.json4s.{DefaultFormats, Formats, JValue, MappingException, Serialization, jackson}
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.JsonMethods._
import org.json4s._

import scala.collection.immutable.Seq


/**
  * Created by guifeng on 2017/6/30.
  */
trait JsonSerializer extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats ++ JodaTimeSerializers.all

  def deserializer[T: Manifest](specStr: String): T = {
    val json: JValue = parse(specStr)
    json.extract[T]
  }

}


/**
  * From: https://github.com/hseeberger/akka-http-json
  * Automatic to and from JSON marshalling/unmarshalling using an in-scope *Json4s* protocol.
  *
  * Pretty printing is enabled if an implicit [[Json4sSupport.ShouldWritePretty.True]] is in scope.
  */
object Json4sSupport extends Json4sSupport {

  sealed abstract class ShouldWritePretty

  final object ShouldWritePretty {
    final object True  extends ShouldWritePretty
    final object False extends ShouldWritePretty
  }
}

trait Json4sSupport {
  import Json4sSupport._

  def unmarshallerContentTypes: Seq[ContentTypeRange] =
    List(`application/json`)

  private val jsonStringUnmarshaller =
    Unmarshaller.byteStringUnmarshaller
        .forContentTypes(unmarshallerContentTypes: _*)
        .mapWithCharset {
          case (ByteString.empty, _) => throw Unmarshaller.NoContentException
          case (data, charset)       => data.decodeString(charset.nioCharset.name)
        }

  private val jsonStringMarshaller = Marshaller.stringMarshaller(`application/json`)

  /**
    * HTTP entity => `A`
    */
  implicit def unmarshaller[A: Manifest](implicit serialization: Serialization,
                                         formats: Formats): FromEntityUnmarshaller[A] =
    jsonStringUnmarshaller
        .map(s => serialization.read(s))
        .recover { _ => _ =>
        { case MappingException(_, ite: InvocationTargetException) => throw ite.getCause }
        }

  /**
    * `A` => HTTP entity
    */
  implicit def marshaller[A <: AnyRef](implicit serialization: Serialization,
                                       formats: Formats,
                                       shouldWritePretty: ShouldWritePretty =
                                       ShouldWritePretty.False): ToEntityMarshaller[A] =
    shouldWritePretty match {
      case ShouldWritePretty.False =>
        jsonStringMarshaller.compose(serialization.write[A])
      case ShouldWritePretty.True =>
        jsonStringMarshaller.compose(serialization.writePretty[A])
    }
}