package com.ming.kyo.ollama.completion
import io.circe.{Json, ParsingFailure}
import io.circe.parser.*
import io.circe.syntax.*
import cats.implicits.*
import com.ming.kyo.ollama.completion.OllamaRequest.*

/**
 * Unit tests for [[OllamaRequest]]
 */
class OllamaRequestTest extends munit.FunSuite:

  test("Decode OllamaRequest from valid JSON structure"):
    val jsonString = """{"model":"llama3:70b", "prompt":"123", "stream":false}"""
    val request = parse(jsonString).flatMap(_.as[OllamaRequest])
    val expected= OllamaRequest(ChatModel.Llama3_70b, "123", stream = false)
    assertEquals(request, Right(expected))

  test("Encode OllamaRequest to valid JSON structure"):
    val jsonString = """{"model":"llama3:70b", "prompt":"123", "stream":false}"""
    val json = parse(jsonString)
    val request = OllamaRequest(ChatModel.Llama3_70b, "123", stream = false).asJson
    assertEquals(request.asRight[ParsingFailure], json)

  test("OllamaRequest serialization with invalid JSON structure"):
    val invalidJson = """{"model":"invalid", "prompt":123, "stream":false}"""
    val decodedRequest = decode[OllamaRequest](invalidJson)
    assert(decodedRequest.isLeft)

  test("OllamaRequest prompt boundary cases"):
    val models = ChatModel.values
    val edgePrompts = List("", " ", "\n", "a" * 10000) // Empty, whitespace, newline, very long string

    models.foreach { model =>
      edgePrompts.foreach { prompt =>
        val request = OllamaRequest(model, prompt, stream = true)
        val json = request.asJson.noSpaces
        val decodedRequest = decode[OllamaRequest](json)

        assert(decodedRequest.isRight)
        assert(decodedRequest.getOrElse(fail("Decoding failed")) == request)
      }
    }

  test("OllamaRequest stream edge cases"):
    val requestTrue = OllamaRequest(ChatModel.Llama3, "Test prompt", true)
    val requestFalse = OllamaRequest(ChatModel.Llama3, "Test prompt", false)

    val jsonTrue = requestTrue.asJson.noSpaces
    val jsonFalse = requestFalse.asJson.noSpaces
    val decodedTrue = decode[OllamaRequest](jsonTrue)
    val decodedFalse = decode[OllamaRequest](jsonFalse)

    assert(decodedTrue.isRight && decodedTrue.getOrElse(fail("Decoding failed")).stream)
    assert(decodedFalse.isRight && !decodedFalse.getOrElse(fail("Decoding failed")).stream)
