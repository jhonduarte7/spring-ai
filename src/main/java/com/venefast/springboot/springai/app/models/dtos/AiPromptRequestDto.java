package com.venefast.springboot.springai.app.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiPromptRequestDto(
    @NotBlank(message = "Prompt is required and cannot be blank")
    @Size(max = 4000, message = "Prompt cannot exceed 4000 characters")
    String prompt,

    String model
) {}
