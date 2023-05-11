package ru.job4j.shortcut.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.model.Website;
import ru.job4j.shortcut.repository.WebRefRepository;
import ru.job4j.shortcut.repository.WebsiteRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class ConvertServiceImplTest {
    @TestConfiguration
    static class Config {
        @MockBean
        private WebRefRepository webRefRepository;

        @MockBean
        private WebsiteRepository websiteRepository;

        @MockBean
        private RandomStringService randomStringService;

        @Bean
        private ConvertService convertService() {
            return new ConvertServiceImpl(webRefRepository, websiteRepository, randomStringService);
        }
    }

    @Autowired
    private WebRefRepository webRefRepository;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private RandomStringService randomStringService;

    @Autowired
    private ConvertService convertService;


    @Test
    public void whenConvertNewReferenceUsualWay() {
        Mockito.when(randomStringService.generateString(6)).thenReturn("answer");
        Mockito.when(websiteRepository.findByLogin(any()))
                .thenReturn(Optional.of(Website.of()
                        .url("yandex.ru")
                        .login("login")
                        .password("password")
                        .build()));
        Mockito.when(webRefRepository.findByUrl(any())).thenReturn(Optional.empty());
        Mockito.when(webRefRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }
}