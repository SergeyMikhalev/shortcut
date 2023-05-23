package ru.job4j.shortcut.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.job4j.shortcut.ShortcutApplication;
import ru.job4j.shortcut.dto.ConvertRequest;
import ru.job4j.shortcut.dto.ConvertResponse;
import ru.job4j.shortcut.service.ConvertService;
import ru.job4j.shortcut.service.RegistrationService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShortcutApplication.class)
@AutoConfigureMockMvc
class ConvertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConvertService convertService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser
    public void whenConvertOk() throws Exception {
        ConvertRequest request = new ConvertRequest("http://ya.ru/q");
        String reqAsJson = mapper.writeValueAsString(request);
        ConvertResponse response = new ConvertResponse("abc123");
        String respAsJson = mapper.writeValueAsString(response);
        when(convertService.convert(request)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post("/convert")
                        .contentType(MediaType.APPLICATION_JSON).content(reqAsJson)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(respAsJson));
        verify(convertService, times(1)).convert(request);
    }

    @Test
    @WithMockUser
    public void whenWrongRequestData() throws Exception {
        ConvertRequest request = new ConvertRequest("htp://ya.ru/q");
        String reqAsJson = mapper.writeValueAsString(request);
        when(convertService.convert(request))
                .thenThrow(new IllegalArgumentException("Error message"));
        mockMvc.perform(MockMvcRequestBuilders.post("/convert")
                        .contentType(MediaType.APPLICATION_JSON).content(reqAsJson)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error message"));
        verify(convertService, times(1)).convert(request);
    }

    @Test
    @WithMockUser
    public void whenServerSideProblems() throws Exception {
        ConvertRequest request = new ConvertRequest("http://google.ru/q");
        String reqAsJson = mapper.writeValueAsString(request);
        when(convertService.convert(request))
                .thenThrow(new IllegalStateException("Error message"));
        mockMvc.perform(MockMvcRequestBuilders.post("/convert")
                        .contentType(MediaType.APPLICATION_JSON).content(reqAsJson)
                )
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error message"));
        verify(convertService, times(1)).convert(request);
    }

}