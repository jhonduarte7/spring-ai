package com.venefast.springboot.springai.app.services;

import com.venefast.springboot.springai.app.models.dtos.AiInteractionDto;
import com.venefast.springboot.springai.app.models.dtos.AiPromptRequestDto;
import java.util.List;

public interface AiInteractionService {

    String generate(String message);

    String generateByName(String name);

    String greetWithLastName(String lastName);

    AiInteractionDto generateResponse(AiPromptRequestDto requestDto);

    List<AiInteractionDto> getRecentInteractions();

    AiInteractionDto getInteractionById(Long id);
}
