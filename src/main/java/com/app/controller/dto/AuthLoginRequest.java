package com.app.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequest(@NotBlank String username, String password) {
}
