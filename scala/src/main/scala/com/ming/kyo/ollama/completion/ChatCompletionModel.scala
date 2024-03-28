package com.ming.kyo.ollama.completion

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.semiauto.*
import sttp.client3.UriContext
import sttp.model.Uri
import java.time.Instant

enum ChatModel(val value: String):
  case Llama3 extends ChatModel("llama3")
  case Llama3_70b extends ChatModel("llama3:70b")
  case Mixtral extends ChatModel("mixtral")

object ChatModel:
  given Encoder[ChatModel] = Encoder.encodeString.contramap[ChatModel](_.value)
  given Decoder[ChatModel] = Decoder.decodeString.emap (str =>
    values.find(_.value == str).toRight("ChatModel not found"))

object ChatUrls:
  val baseUrl: String = "http://localhost:11434/"
  val completion: Uri = uri"${baseUrl}api/generate"

case class OllamaRequest(
                       model: ChatModel,
                       prompt: String,
                       stream: Boolean
                     )

object OllamaRequest:
  given Codec[OllamaRequest] = deriveCodec[OllamaRequest]

case class OllamaResponse(
                        model: String,
                        created_at: Instant,
                        response: String,
                        done: Boolean,
                        context: List[Int],
                        total_duration: Long,
                        load_duration: Long,
                        prompt_eval_duration: Long,
                        eval_count: Int,
                        eval_duration: Long
                      )

object OllamaResponse:
  given Codec[OllamaResponse] = deriveCodec[OllamaResponse]