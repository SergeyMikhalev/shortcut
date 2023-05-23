package ru.job4j.shortcut.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.shortcut.ShortcutApplication;
import ru.job4j.shortcut.service.RedirectionService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShortcutApplication.class)
@AutoConfigureMockMvc
class RedirectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedirectionService redirectionService;

    @Test
    public void whenWrongCode() throws Exception {
        Mockito.when(redirectionService.getRedirectionURL("100"))
                .thenThrow(new IllegalArgumentException(" Для кода: 100 ссылки переадресации не найдено"));
        this.mockMvc.perform(get("/redirect/100"))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(content().string(" Для кода: 100 ссылки переадресации не найдено"));
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        verify(redirectionService).getRedirectionURL(code.capture());
        assertEquals("100", code.getValue());
    }

    @Test
    public void whenFoundRedirectionUrlForCode() throws Exception {
        Mockito.when(redirectionService.getRedirectionURL("100")).thenReturn("yandex.ru/some/");
        this.mockMvc.perform(get("/redirect/100"))
                .andDo(print())
                .andExpect(status().is(302))
                .andExpect(content().string("yandex.ru/some/"));
        ArgumentCaptor<String> code = ArgumentCaptor.forClass(String.class);
        verify(redirectionService).getRedirectionURL(code.capture());
        assertEquals("100", code.getValue());
    }

}