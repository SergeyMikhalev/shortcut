package ru.job4j.shortcut.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.dto.RefStatistic;
import ru.job4j.shortcut.model.WebRef;
import ru.job4j.shortcut.repository.WebRefRepository;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class StatisticServiceImplTest {
    @TestConfiguration
    static class Config {
        @MockBean
        private WebRefRepository webRefRepository;

        @Bean
        public StatisticService statisticService() {
            return new StatisticServiceImpl(webRefRepository);
        }
    }

    @Autowired
    private WebRefRepository webRefRepository;

    @Autowired
    private StatisticService statisticService;

    @Test
    public void whenUsualCase() {
        List<WebRef> dataFromRepository = List.of(
                WebRef.of()
                        .url("yandex.ru/100")
                        .useCount(100)
                        .build()
        );

        Mockito.when(webRefRepository.findAll()).thenReturn(dataFromRepository);

        List<RefStatistic> statistics = statisticService.get();

        assertEquals(1, statistics.size());
        assertEquals("yandex.ru/100", statistics.get(0).getRef());
        assertEquals(100L, (long) statistics.get(0).getUseCount());
    }

}