# Ecommerce AI Chatbot (Spring Boot + OpenAI + Semantic Search)

A minimal customer-support chatbot for an online store. It answers questions
about your products using OpenAI embeddings for semantic search plus the
chat completions API for the actual reply — no vector database, no
frameworks like LangChain, just plain Java.

## How it works

1. On startup, `ProductSearchService` reads `products.json` and calls the
   OpenAI Embeddings API once per product to turn its text into a vector.
   Everything is kept in memory (a simple `List<Product>`).
2. When a user sends a message, the same embedding call is made for their
   question, and cosine similarity picks the top 3 most relevant products.
3. Those products are inserted into the system prompt as context, and the
   question + context are sent to the Chat Completions API (`gpt-4o-mini`).
4. The model's answer is returned to the user.

## Run it

```bash
export OPENAI_API_KEY=sk-xxxxxxxxxxxxxxxx
mvn spring-boot:run
```

## Try it

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Do you have anything for tracking my sleep?"}'
```

Expected response shape:

```json
{ "reply": "Yes — we have a Smart Fitness Watch that tracks sleep, heart rate, and steps for $79.99." }
```

## Extending this

- Swap `products.json` for a real database table and cache embeddings
  instead of recomputing them on every boot.
- Add conversation history (a `List<Message>` per session) if you want
  multi-turn context instead of one-shot Q&A.
- Add order-lookup or refund-status tools via OpenAI function/tool calling
  once the basic Q&A flow works.
