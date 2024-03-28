package com.ming.kyo

import com.ming.kyo.ollama.completion.ChatModel
import com.ming.kyo.ollama.Ollama
import com.ming.kyo.ollama.completion.ChatModel.*
import kyo.*

@main def Main(): Unit =
  val question = "tell me a joke"

  val program: Unit < (Consoles & Envs[Ollama] & Requests) = defer {
    await(Consoles.println(s"Question: $question"))
    val ollama: Ollama = await(Envs.get[Ollama])
    val result = await(ollama.generateCompletion(question))
    await(Consoles.println(s"Answer: \n${result.response}"))
  }

  val programWithDependencies: Unit < (Consoles & Requests) = Envs.run(Ollama.make(ChatModel.Llama3))(program)
  val programWithDependenciesAndConcurrent: Unit < (Consoles & Fibers) = Requests.run(programWithDependencies)
  KyoApp.run(programWithDependenciesAndConcurrent)


