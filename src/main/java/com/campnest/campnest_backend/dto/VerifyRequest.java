package com.campnest.campnest_backend.dto;


import java.util.UUID;

public class VerifyRequest {
    private UUID uuid;
    private String code;

    // getters & setters
    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
