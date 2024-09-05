package com.shybly.wikillm;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

    @Value("${gitlab.api.token}")
    private String gitlabApiToken;
    @Value("${gitlab.api.url}")
    private String gitlabApiUrl;

    @Bean
    public RestTemplate gitlabRestTemplate() {
        return new RestTemplateBuilder()
                .defaultHeader("PRIVATE-TOKEN", gitlabApiToken)
                .rootUri(gitlabApiUrl)
                .build();
    }

    @Bean
    public PgVectorStore vectorStore(final JdbcTemplate jdbcTemplate,
                                     @Qualifier("postgresMlEmbeddingModel") final EmbeddingModel embeddingModel) {
        return new PgVectorStore(jdbcTemplate, embeddingModel);
    }

}
