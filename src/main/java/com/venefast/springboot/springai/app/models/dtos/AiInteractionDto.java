package com.venefast.springboot.springai.app.models.dtos;

import jakarta.validation.constraints.NotBlank;

public record AiInteractionDto(
    Long id,
    @NotBlank(message = "Prompt is required")
    String prompt,
    String response,
    String model
) {}
