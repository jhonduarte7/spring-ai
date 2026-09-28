package com.venefast.springboot.springai.app.controllers;

import com.venefast.springboot.springai.app.models.dtos.AiInteractionDto;
import com.venefast.springboot.springai.app.models.dtos.AiPromptRequestDto;
import com.venefast.springboot.springai.app.services.AiInteractionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiInteractionController {

    private final AiInteractionService service;

    public AiInteractionController(AiInteractionService service) {
        this.service = service;
    }

    @GetMapping({ "/generate" })
    public String generate(
            @RequestParam(value = "message", defaultValue = "is it worth to lear spring AI?") String message) {
        return service.generate(message);
    }

    @GetMapping({ "/greet", "/name" })
    public String generateByName(
            @RequestParam(value = "name", defaultValue = "Developer Jhon") String name) {
        return service.generateByName(name);
    }

    @GetMapping({ "/last-name", "/lastname" })
    public String greetWithLastName(
            @RequestParam(value = "lastName", required = false) String lastName,
            @RequestParam(value = "lastname", required = false) String lastnameFallback) {
        String effectiveLastName = (lastName != null && !lastName.isBlank()) ? lastName : lastnameFallback;
        if (effectiveLastName == null || effectiveLastName.isBlank()) {
            effectiveLastName = "Doe";
        }
        return service.greetWithLastName(effectiveLastName);
    }

    @PostMapping("/generate")
    public ResponseEntity<AiInteractionDto> generatePost(@Valid @RequestBody AiPromptRequestDto requestDto) {
        AiInteractionDto responseDto = service.generateResponse(requestDto);
        URI location = URI.create("/api/v1/ai/" + responseDto.id());
        return ResponseEntity.created(location).body(responseDto);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AiInteractionDto>> getRecentHistory() {
        return ResponseEntity.ok(service.getRecentInteractions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AiInteractionDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInteractionById(id));
    }
}
