# Retrieval Augmented Generation (RAG) Implementation

## Overview
This repository contains the implementation and resources for a Retrieval Augmented Generation (RAG). RAG combines the power of language models with the vast knowledge stored in documents, databases, or other data sources by retrieving relevant information and using it to augment the generation process. This approach enables the generation of more informed, accurate, and contextually rich responses in tasks such as question answering, text completion, and more.

## RAG Architecture
A typical RAG application has two main components:

### Indexing: 
a pipeline for ingesting data from a source and indexing it.
![alt text](/images/rag1.png)
### Retrieval and generation: 
the actual RAG chain, which takes the user query at run time and retrieves the relevant data from the index, then passes that to the model.
![alt text](/images/rag2.png)


## Setup
Install [Ollama](https://ollama.com/) and start a language model
```
ollama run mixtral
```