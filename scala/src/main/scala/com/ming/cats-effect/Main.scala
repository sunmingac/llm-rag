package com.ming.cats-effect

import cats.effect.*
import cats.effect.std.Console
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.Method.*
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.circe.*
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import io.circe.parser._
import java.time.Instant
//import cats.effect.cps._

case class LLMRequest(
    model: String,
    prompt: String,
    stream: Boolean
)

object LLMRequest:
  implicit val encoder: Encoder[LLMRequest] = deriveEncoder[LLMRequest]
  implicit val decoder: Decoder[LLMRequest] = deriveDecoder[LLMRequest]

case class LLMResponse(
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

object LLMResponse:
  implicit val encoder: Encoder[LLMResponse] = deriveEncoder[LLMResponse]
  implicit val decoder: Decoder[LLMResponse] = deriveDecoder[LLMResponse]

val request = LLMRequest("llama3", "Why is the sky blue?", stream = false)
val uri = uri"http://localhost:11434/api/generate"

object Main extends IOApp.Simple:
  def newValue[F[_]: Console: Sync](client: Client[F]): F[Unit] =
    val req = Request[F](POST, uri)
      .withEntity(request)
      .withHeaders(`Content-Type`(MediaType.application.json))
    client
      .run(req)
      .use: response =>
        for
         _ <- Sync[F].delay(println(response.status))
         js <- response.bodyText.compile.string
         llmResponse <- Sync[F].fromEither(decode[LLMResponse](js))
         _ <- Sync[F].delay(println(llmResponse.response))
        yield ()

  def run: IO[Unit] =
    EmberClientBuilder
      .default[IO]
      .build
      .use[Unit](newValue[IO])
