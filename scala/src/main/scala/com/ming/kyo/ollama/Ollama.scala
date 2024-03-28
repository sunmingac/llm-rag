package com.ming.kyo.ollama

import com.ming.kyo.ollama.completion.*
import kyo.*
import sttp.client3.circe.*

import java.io.Closeable

trait Ollama:
  def generateCompletion(prompt: String): OllamaResponse < Requests

private class OllamaImpl (model: ChatModel) extends Ollama:
  def generateCompletion(prompt: String): OllamaResponse < Requests =
    val data = OllamaRequest(model, prompt, stream = false)
    val request: OllamaResponse < Requests = Requests(_
      .post(ChatUrls.completion)
      .body(data)
      .response(asJson[OllamaResponse])
    )
    request

object Ollama:
  def make(model:  ChatModel): Ollama = new OllamaImpl(model)
  

