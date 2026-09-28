package com.venefast.springboot.springai.app.mappers;

import com.venefast.springboot.springai.app.models.dtos.AiInteractionDto;
import com.venefast.springboot.springai.app.models.entities.AiInteraction;
import org.springframework.stereotype.Component;

@Component
public class AiInteractionMapper {

    public AiInteractionDto toDto(AiInteraction entity) {
        if (entity == null) {
            return null;
        }
        return new AiInteractionDto(
            entity.getId(),
            entity.getPrompt(),
            entity.getResponse(),
            entity.getModel()
        );
    }

    public AiInteraction toEntity(AiInteractionDto dto) {
        if (dto == null) {
            return null;
        }
        AiInteraction entity = new AiInteraction();
        entity.setId(dto.id());
        entity.setPrompt(dto.prompt());
        entity.setResponse(dto.response());
        entity.setModel(dto.model());
        return entity;
    }
}
