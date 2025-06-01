package com.example.clienteapi.adapter.in.web;

import lombok.Data;

@Data
public class TokenResponse {
    private String token;
    private String type = "Bearer";

    public TokenResponse(String token) {
        this.token = token;
    }
}
