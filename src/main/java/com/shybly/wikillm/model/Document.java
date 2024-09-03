package com.shybly.wikillm.model;

import com.shybly.wikillm.util.FloatArrayConverter;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "vector_store")
public class Document {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "content", columnDefinition = "text", updatable = false, nullable = false)
    private String content;

    @Column(name = "metadata", columnDefinition = "json", updatable = false, nullable = false)
    private String metadata;

    @Convert(converter = FloatArrayConverter.class)
    @Column(name = "embedding", columnDefinition = "float[]")
    private Float[] embedding;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Float[] embedding) {
        this.embedding = embedding;
    }
}


