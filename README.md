# Retrieval Augmented Generation (RAG) Implementation

## Overview
This repository contains the implementation and resources for a Retrieval Augmented Generation (RAG). 



## Large Language Model
Install [Ollama](https://ollama.com/) and start a large language model
```
ollama run llama3
```

## Vector Database
Run [Qdrant](https://github.com/qdrant/rust-client) with enabled gRPC interface

```
docker run -p 6333:6333 -p 6334:6334 \
    -e QDRANT__SERVICE__GRPC_PORT="6334" \
    qdrant/qdrant
```

## Python Implementation
Python uses LangChain to implement a RAG system by integrating a language model and a knowledge retriever.
For simplicity, Chroma vector database is used to store embedded data.  
Source code is located in the `python` folder in Jupyter Notebook format [rag.ipynb](python/rag.ipynb).

## Rust Implementation
The Rust implementation uses the following libraries:
- `Tokio` is an asynchronous runtime for Rust
- `Serde` is a framework for serializing and deserializing Rust data structures efficiently and generically.
- `reqwest` provides a convenient, higher-level HTTP Client.
- `qdrant-client` is the Rust client for Qdrant vector search engine.

source code is located in in `rust` folder.
```
cd rust
cargo run
```

## Scala Implementation
RAG has been implemented in two different styles:
- Cats effect (Monadic effect)  
`scala/com/ming/kyo/Main.scala`
- Kyo (algebraic effect)  
`scala/com/ming/kyo/Main.scala`
```
cd scala
sbt run
```


## RAG Architecture
RAG combines the power of language models with the vast knowledge stored in documents, databases, or other data sources by retrieving relevant information and using it to augment the generation process. This approach enables the generation of more informed, accurate, and contextually rich responses in tasks such as question answering, text completion, and more. A typical RAG application has two main components:

### Indexing: 
a pipeline for ingesting data from a source and indexing it.
![alt text](/images/rag1.png)
### Retrieval and generation: 
the actual RAG chain, which takes the user query at run time and retrieves the relevant data from the index, then passes that to the model.
![alt text](/images/rag2.png)
