package com.jagha.collabflow.dto.board;

import java.time.Instant;

public class BoardResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerName;
    private Instant createdAt;

    public BoardResponse() {
    }

    public BoardResponse(Long id, String name, String description, Long ownerId, String ownerName, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
