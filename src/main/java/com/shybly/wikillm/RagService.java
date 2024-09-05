package com.shybly.wikillm;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final String PROMPT_BLUEPRINT = """
              Answer the query strictly referring the provided context:
              {context}
              Query:
              {query}
              In case you don't have any answer from the context provided, just say:
              I'm sorry I don't have the information you are looking for.
            """;
    private OllamaChatModel ollamaChatModel;
    private VectorStore vectorStore;

    public String chat(String query) {
        final List<Document> similarDocuments = vectorStore.similaritySearch(query);
        final String prompt = createPrompt(query, similarDocuments);

        return ollamaChatModel.call(prompt);
    }

    private String createPrompt(String query, List<Document> context) {
        final PromptTemplate promptTemplate = new PromptTemplate(PROMPT_BLUEPRINT);
        promptTemplate.add("query", query);
        promptTemplate.add("context", context);

        return promptTemplate.render();
    }
}
