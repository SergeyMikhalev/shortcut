package ru.job4j.shortcut.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.dto.ConvertRequest;
import ru.job4j.shortcut.dto.ConvertResponse;
import ru.job4j.shortcut.model.WebRef;
import ru.job4j.shortcut.model.Website;
import ru.job4j.shortcut.repository.WebRefRepository;
import ru.job4j.shortcut.repository.WebsiteRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static ru.job4j.shortcut.service.ConvertServiceImpl.MAX_ATTEMPTS_TO_GENERATE_CODE;

@RunWith(SpringRunner.class)
public class ConvertServiceImplTest {

    public static final String DEFAULT_URL = "http://yandex.ru/some";
    public static final ConvertRequest DEFAULT_REQUEST = new ConvertRequest(DEFAULT_URL);
    public static final String DEFAULT_USERNAME = "admin";

    @TestConfiguration
    static class Config {
        @MockBean
        private WebRefRepository webRefRepository;

        @MockBean
        private WebsiteRepository websiteRepository;

        @MockBean
        private RandomStringService randomStringService;

        @Bean
        public ConvertService convertService() {
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


    @Before
    public void setUp() {
        Mockito.when(randomStringService.generateString(6)).thenReturn("answer");
        Mockito.when(websiteRepository.findByLogin("admin"))
                .thenReturn(Optional.of(Website.of()
                        .url("yandex.ru")
                        .login("login")
                        .password("password")
                        .build()));
        Mockito.when(webRefRepository.findByUrl(any())).thenReturn(Optional.empty());
        Mockito.when(webRefRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME)
    public void whenConvertNewReferenceUsualWay() {
        ConvertResponse response = convertService.convert(DEFAULT_REQUEST);

        assertEquals("answer", response.getCode());
    }

    @Test
    @WithMockUser(username = "admin")
    public void whenConvertExistingReferenceUsualWay() {
        Mockito.when(webRefRepository.findByUrl("http://yandex.ru/some"))
                .thenReturn(Optional.of(
                        WebRef.of()
                                .url("http://yandex.ru/some")
                                .code("code_01").
                                build()
                ));

        ConvertResponse response = convertService.convert(DEFAULT_REQUEST);

        assertEquals("code_01", response.getCode());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME)
    public void whenWrongUser() {
        Mockito.when(websiteRepository.findByLogin("admin")).thenReturn(Optional.empty()); /**/
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    convertService.convert(DEFAULT_REQUEST);
                }
        );
        assertEquals(
                "Проблема в Авторизации и Аутентификации. "
                        + "В систему вошёл пользователь, который не зарегистрирован. "
                        + "Имя пользователя: admin",
                thrown.getMessage()
        );
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME)
    public void whenConvertingReferenceDiffersFromUsersSite() {
        Mockito.when(websiteRepository.findByLogin("admin"))
                .thenReturn(Optional.of(Website.of()
                        .url("google.com")
                        .login("login")
                        .password("password")
                        .build()));

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    convertService.convert(DEFAULT_REQUEST);
                }
        );
        assertEquals(
                "Ссылка, для которой запрашивается конвертация не относится к сайту,"
                        + " ассоциированному с текущей учетной записью",
                thrown.getMessage()
        );
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME)
    public void whenConvertingReferenceContainsWrongUrl() {
        ConvertRequest request = new ConvertRequest("httpt://yandex.ru/some");

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> {
                    convertService.convert(request);
                }
        );
        assertTrue(thrown.getMessage().contains("Переданная ссылка не является URL"));
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME)
    public void whenCantSaveRefCauseOfConstrainsViolation() {
        Mockito.when(webRefRepository.save(any())).thenThrow(new RuntimeException("ConstraintViolationException")); /**/

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> {
                    convertService.convert(DEFAULT_REQUEST);
                }
        );
        assertEquals("Не удалось сгенерировать уникальный код "
                + "для сохранения ссылки " + DEFAULT_URL
                + "  в БД за заданное количество итераций: " + MAX_ATTEMPTS_TO_GENERATE_CODE, thrown.getMessage());
    }

    @Test
    @WithMockUser(username = DEFAULT_USERNAME)
    public void whenCantSaveRefCauseOfSomeProblem() {
        Mockito.when(webRefRepository.save(any())).thenThrow(new RuntimeException("Another Postgres Exception")); /**/

        IllegalStateException thrown = assertThrows(
                IllegalStateException.class,
                () -> {
                    convertService.convert(DEFAULT_REQUEST);
                }
        );
        assertTrue(thrown.getMessage().contains("Не удалось сохранить в БД объект"));
        assertTrue(thrown.getMessage().contains("Another Postgres Exception"));
    }

}
