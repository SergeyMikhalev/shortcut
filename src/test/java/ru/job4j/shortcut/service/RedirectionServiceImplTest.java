package ru.job4j.shortcut.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.model.WebRef;
import ru.job4j.shortcut.repository.WebRefRepository;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
public class RedirectionServiceImplTest {

    @TestConfiguration
    static class Config {
        @MockBean
        private WebRefRepository webRefRepository;

        @Bean
        public RedirectionService redirectionService() {
            return new RedirectionServiceImpl(webRefRepository);
        }
    }

    @Autowired
    private WebRefRepository webRefRepository;

    @Autowired
    private RedirectionService redirectionService;

    private final String code = "007";
    private final String url = "yandex.ru";

    @Test
    public void whenUsualCase() {

        Mockito.when(webRefRepository.findByCode(code))
                .thenReturn(Optional.of(WebRef
                        .of()
                        .code(code)
                        .url(url)
                        .build())
                );
        String result = redirectionService.getRedirectionURL(code);
        assertEquals(url, result);
    }

    @Test
    public void whenNoSuchRef() {
        Mockito.when(webRefRepository.findByCode(code))
                .thenReturn(Optional.empty());
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    redirectionService.getRedirectionURL(code);
                });
        assertEquals(" Для кода: " + code + " ссылки переадресации не найдено", thrown.getMessage());
    }

}