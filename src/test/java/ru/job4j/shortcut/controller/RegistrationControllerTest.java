package ru.job4j.shortcut.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.job4j.shortcut.ShortcutApplication;
import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;
import ru.job4j.shortcut.service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShortcutApplication.class)
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegistrationService registrationService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void whenRegistrationOk() throws Exception {
        RegistrationRequest request = new RegistrationRequest("google.com");
        String reqAsJson = mapper.writeValueAsString(request);
        RegistrationResponse response = new RegistrationResponse(true, "abc", "123");
        String respAsJson = mapper.writeValueAsString(response);
        Mockito.when(registrationService.register(request))
                .thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqAsJson)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(respAsJson));
        verify(registrationService, times(1)).register(request);
    }

    @Test
    public void whenIllegalStateException() throws Exception {
        RegistrationRequest request = new RegistrationRequest("google.com");
        String reqAsJson = mapper.writeValueAsString(request);
        Mockito.when(registrationService.register(any()))
                .thenThrow(new IllegalStateException("Message"));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reqAsJson)
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Message"));
        verify(registrationService, times(1)).register(request);
    }
}