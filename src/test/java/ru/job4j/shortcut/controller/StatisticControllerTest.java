package ru.job4j.shortcut.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.job4j.shortcut.ShortcutApplication;
import ru.job4j.shortcut.dto.RefStatistic;
import ru.job4j.shortcut.service.StatisticService;

import java.util.List;

@SpringBootTest(classes = ShortcutApplication.class)
@AutoConfigureMockMvc
class StatisticControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticService statisticService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser
    public void whenUsualCase() throws Exception {
        RefStatistic refStatistic1 = new RefStatistic("ya.ru/some1/", 10);
        RefStatistic refStatistic2 = new RefStatistic("ya.ru/some2/", 15);
        when(statisticService.get()).thenReturn(List.of(refStatistic1, refStatistic2));
        String result = mapper.writeValueAsString(List.of(refStatistic1, refStatistic2));

        mockMvc.perform(get("/statistic"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(result));
        verify(statisticService, times(1)).get();
    }

}