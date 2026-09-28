package com.venefast.springboot.springai.app.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.venefast.springboot.springai.app.models.dtos.AiInteractionDto;
import com.venefast.springboot.springai.app.models.entities.AiInteraction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AiInteractionMapperTest {

    private AiInteractionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AiInteractionMapper();
    }

    @Test
    void shouldMapEntityToDto() {
        AiInteraction entity = new AiInteraction("What is Spring Boot?", "Spring Boot is a framework...", "llama3.2");
        entity.setId(1L);

        AiInteractionDto dto = mapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("What is Spring Boot?", dto.prompt());
        assertEquals("Spring Boot is a framework...", dto.response());
        assertEquals("llama3.2", dto.model());
    }

    @Test
    void shouldMapDtoToEntity() {
        AiInteractionDto dto = new AiInteractionDto(2L, "Hello AI", "Hello Human!", "llama3.2");

        AiInteraction entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(2L, entity.getId());
        assertEquals("Hello AI", entity.getPrompt());
        assertEquals("Hello Human!", entity.getResponse());
        assertEquals("llama3.2", entity.getModel());
    }

    @Test
    void shouldHandleNullMappings() {
        assertNull(mapper.toDto(null));
        assertNull(mapper.toEntity(null));
    }
}
