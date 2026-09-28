package com.venefast.springboot.springai.app.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.venefast.springboot.springai.app.services.AiInteractionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AiInteractionController.class)
class AiInteractionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AiInteractionService service;

        @Test
        void shouldPassMessageToServiceOnGetGenerate() throws Exception {
                when(service.generate("¿Qué es Java?"))
                                .thenReturn("Java es un lenguaje de programación.");

                mockMvc.perform(get("/api/v1/ai/generate").param("message", "¿Qué es Java?"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Java es un lenguaje de programación."));

                verify(service).generate("¿Qué es Java?");
        }

        @Test
        void shouldUseDefaultMessageWhenParamOmitted() throws Exception {
                when(service.generate(anyString()))
                                .thenReturn("Why did the programmer quit? Because they didn't get arrays.");

                mockMvc.perform(get("/api/v1/ai/generate"))
                                .andExpect(status().isOk())
                                .andExpect(content().string(
                                                "Why did the programmer quit? Because they didn't get arrays."));

                verify(service).generate("is it worth to lear spring AI?");
        }

        @Test
        void shouldPassNameToServiceOnGetGreet() throws Exception {
                when(service.generateByName("Jhon"))
                                .thenReturn("Hello Jhon! Welcome to Spring AI.");

                mockMvc.perform(get("/api/v1/ai/greet").param("name", "Jhon"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Hello Jhon! Welcome to Spring AI."));

                verify(service).generateByName("Jhon");
        }

        @Test
        void shouldUseDefaultNameWhenParamOmittedOnGetGreet() throws Exception {
                when(service.generateByName("Developer Jhon"))
                                .thenReturn("Hello Developer Jhon! Keep coding.");

                mockMvc.perform(get("/api/v1/ai/greet"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Hello Developer Jhon! Keep coding."));

                verify(service).generateByName("Developer Jhon");
        }

        @Test
        void shouldPassLastNameToServiceOnGetLastName() throws Exception {
                when(service.greetWithLastName("Duarte"))
                                .thenReturn("Bonjour tout le monde Duarte.");

                mockMvc.perform(get("/api/v1/ai/last-name").param("lastName", "Duarte"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Bonjour tout le monde Duarte."));

                verify(service).greetWithLastName("Duarte");
        }

        @Test
        void shouldUseDefaultLastNameWhenParamOmitted() throws Exception {
                when(service.greetWithLastName("Doe"))
                                .thenReturn("Bonjour tout le monde Doe.");

                mockMvc.perform(get("/api/v1/ai/last-name"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Bonjour tout le monde Doe."));

                verify(service).greetWithLastName("Doe");
        }
}
