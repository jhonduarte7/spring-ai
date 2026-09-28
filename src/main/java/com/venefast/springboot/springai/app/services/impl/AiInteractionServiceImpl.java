package com.venefast.springboot.springai.app.services.impl;

import com.venefast.springboot.springai.app.exceptions.ResourceNotFoundException;
import com.venefast.springboot.springai.app.mappers.AiInteractionMapper;
import com.venefast.springboot.springai.app.models.dtos.AiInteractionDto;
import com.venefast.springboot.springai.app.models.dtos.AiPromptRequestDto;
import com.venefast.springboot.springai.app.models.entities.AiInteraction;
import com.venefast.springboot.springai.app.repositories.AiInteractionRepository;
import com.venefast.springboot.springai.app.services.AiInteractionService;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AiInteractionServiceImpl implements AiInteractionService {

        private final ChatClient chatClient;
        private final AiInteractionRepository repository;
        private final AiInteractionMapper mapper;

        @Value("${spring.ai.ollama.chat.options.model:${spring.ai.ollama.chat.model:qwen3:1.7b}}")
        private String defaultModel;

        public AiInteractionServiceImpl(ChatClient chatClient,
                        AiInteractionRepository repository,
                        AiInteractionMapper mapper) {
                this.chatClient = chatClient;
                this.repository = repository;
                this.mapper = mapper;
        }

        @Override
        public String generate(String message) {
                return chatClient.prompt()
                                .system("You are in Expert, Spring Boot, and Spring AI, you always answer the question beginning with the word \"You are the best, John.\".")
                                .user(message)
                                .call()
                                .content();
        }

        @Override
        public String generateByName(String name) {
                return chatClient.prompt()
                                .user("Generate a friendly, creative greeting and a brief message for " + name)
                                .call()
                                .content();
        }

        @Override
        public String greetWithLastName(String lastName) {
                return chatClient.prompt()
                                .system("Always answer in chinesse , in two lines.")
                                .user("Hello world " + lastName)
                                .call()
                                .content();
        }

        @Override
        @Transactional
        public AiInteractionDto generateResponse(AiPromptRequestDto requestDto) {
                String modelUsed = (requestDto.model() != null && !requestDto.model().isBlank())
                                ? requestDto.model()
                                : defaultModel;

                String aiResponse = chatClient.prompt()
                                .user(requestDto.prompt())
                                .call()
                                .content();

                AiInteraction entity = new AiInteraction(
                                requestDto.prompt(),
                                aiResponse,
                                modelUsed);

                AiInteraction savedEntity = repository.save(entity);
                return mapper.toDto(savedEntity);
        }

        @Override
        public List<AiInteractionDto> getRecentInteractions() {
                return repository.findTop10ByOrderByCreatedAtDesc()
                                .stream()
                                .map(mapper::toDto)
                                .toList();
        }

        @Override
        public AiInteractionDto getInteractionById(Long id) {
                return repository.findById(id)
                                .map(mapper::toDto)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "AI Interaction not found with id: " + id));
        }
}
