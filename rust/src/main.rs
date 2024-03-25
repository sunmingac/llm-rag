use ollama_rs::generation::completion::request::GenerationRequest;
use ollama_rs::Ollama;
use text_splitter::TextSplitter;
use serde::Deserialize;
use serde_json::json;
use reqwest::Client;
use std::fs;
use qdrant_client::prelude::*;
use qdrant_client::qdrant::vectors_config::Config;
use qdrant_client::qdrant::{CreateCollection, SearchPoints, VectorParams, VectorsConfig};

type Result<T> = core::result::Result<T, Error>;
type Error = Box<dyn std::error::Error>; // For early dev.

const MODEL: &str = "mixtral";
const OLLAMA_ENDPOINT: &str = "http://localhost:11434";
const QDRANT_ENDPOINT: &str = "http://localhost:6334";
const COLLECTION_NAME: &str = "demo";
const VECTOR_SIZE: u64 = 4096;

#[tokio::main]
async fn main() -> Result<()> {
    let question: &str = "how much was Apple fined by the EU?";

    step1().await?;
    let answer = step2(question).await?;
    println!("Answer: {}", answer);
    Ok(())
}

async fn step1() -> Result<()> {
    let ollama = Ollama::default();
    let document_text = load_from_text_file("../docs/news.txt").await?;

    let chunks = split_text(&document_text, 500);
    
    init_database().await?;
    // embedding to db
    for (index, text) in chunks.iter().enumerate() {
        let embedding: Vec<f32> = embedding_document(&ollama, MODEL, text).await?;
        // embeddings.push(embedding);
        persist_document(index as u64, embedding, &text).await?;
    }

    Ok(())
}

async fn step2(prompt: &str) -> Result<String> {
    let ollama = Ollama::default();

    // loading from db
    let embedding = embedding_document(&ollama, MODEL, prompt).await?;
    let retrieved_documents = retrieve_document(embedding).await?;

    let answer = rag(prompt, retrieved_documents).await?;
    Ok(answer)
}

async fn load_from_text_file(path: &str) -> Result<String> {
    fs::read_to_string(path).map_err(|e| e.into())
}

#[allow(dead_code)]
async fn load_from_webpage(url: &str) -> Result<String> {
    let response = reqwest::get(url).await?.text().await?;
    Ok(response)
}

#[allow(dead_code)]
async fn embedding_document_direct(prompt: &str) -> Result<Vec<f64>> {
    #[derive(Deserialize, Debug)]
    struct Embedding {
        embedding: Vec<f64>,
    }

    let request_url = OLLAMA_ENDPOINT.to_string() + "/api/embeddings"; // Replace with the actual API URL

    let body = json!({
        "model": "mixtral",
        "prompt": prompt
      });
    let response = Client::new()
        .post(request_url)
        .json(&body)
        .send().await?
        .json::<Embedding>()
        .await?;

    Ok(response.embedding)
}

fn split_text(text: &str, max_characters: usize) -> Vec<&str> {
    let splitter = TextSplitter::default()
        .with_trim_chunks(true);
    let chunks = splitter.chunks(text, max_characters);
    chunks.collect()
}

async fn embedding_document(ollama: &Ollama, model: &str, prompt: &str) -> Result<Vec<f32>> {
    let result = ollama.generate_embeddings(model.to_string(), prompt.to_string(), None).await?;
    let embeddings: Vec<f32> = result.embeddings.iter().map(|&x| x as f32).collect();
    Ok(embeddings)
}

async fn init_database() -> Result<()> {
    let client = QdrantClient::from_url(QDRANT_ENDPOINT).build()?;
    // let collections_list = client.list_collections().await?;
    // dbg!(collections_list);
    client.delete_collection(COLLECTION_NAME).await?;
    client.create_collection(&CreateCollection {
            collection_name: COLLECTION_NAME.into(),
            vectors_config: Some(VectorsConfig {
                config: Some(Config::Params(VectorParams {
                    size: VECTOR_SIZE,
                    distance: Distance::Cosine.into(),
                    ..Default::default()
                })),
            }),
            ..Default::default()
    })
    .await?;
    Ok(())
}

async fn persist_document(index: u64, embedding: Vec<f32>, text: &str) -> Result<()> {
    let client = QdrantClient::from_url(QDRANT_ENDPOINT).build()?;

    let payload: Payload = json!(
        {
            "id": index,
            "text": text
        })
        .try_into()
        .unwrap();
    let point = PointStruct::new(index, embedding, payload);
    client
        .upsert_points_blocking(COLLECTION_NAME, None, vec![point], None)
        .await?;
    Ok(())
}

async fn retrieve_document(embedding: Vec<f32>) -> Result<Vec<String>> {
    let client = QdrantClient::from_url(QDRANT_ENDPOINT).build()?;
    let search_result = client
        .search_points(&SearchPoints {
            collection_name: COLLECTION_NAME.into(),
            vector: embedding,
            filter: None,
            limit: 3,
            with_payload: Some(true.into()),
            ..Default::default()
        })
        .await?;

    let result = search_result.result.iter().map(|point| {
        point.payload.get("text").unwrap().to_string()
    }).collect();

    Ok(result)
}

#[allow(dead_code)]
async fn send_query(ollama: &Ollama, model: &str, prompt: &str) -> Result<String> {
    let request = GenerationRequest::new(model.to_string(), prompt.to_string());
    let res = ollama.generate(request).await?;
    Ok(res.response)
}

async fn rag(prompt: &str, retrieved_documents: Vec<String>) -> Result<String> {
    let ollama = Ollama::default();
    let messages = json!([
        {
            "role": "system",
            "content": "You are an assistant. You will be shown the user's question, and the relevant information. Respond according to the provided information."
        },
        {
            "role": "user",
            "content": format!("Question: {}. \n Information: {}", prompt, retrieved_documents.join("\n\n"))
        }
    ]);
    let request = GenerationRequest::new(MODEL.to_string(), messages.to_string());
    let res = ollama.generate(request).await?;
    Ok(res.response)
}




