package ru.job4j.shortcut.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;
import ru.job4j.shortcut.repository.WebsiteRepository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
public class RegistrationServiceImplTest {
    @TestConfiguration
    static class Config {

        @MockBean
        private WebsiteRepository websiteRepository;

        @MockBean
        private RandomStringService randomStringService;

        @MockBean
        private BCryptPasswordEncoder encoder;

        @Bean
        public RegistrationService registrationService() {
            return new RegistrationServiceImpl(websiteRepository, randomStringService, encoder);
        }
    }

    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    private RandomStringService randomStringService;
    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private RegistrationService registrationService;

    @Test
    public void whenRegisterNewWebsite() {
        Mockito.when(randomStringService.generateString(5)).thenReturn("code1");
        Mockito.when(websiteRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(websiteRepository.existsByUrlIgnoreCase(any())).thenReturn(false);
        Mockito.when(encoder.encode(any())).thenAnswer(i -> i.getArguments()[0]);

        RegistrationRequest request = new RegistrationRequest("yandex.ru");

        RegistrationResponse response = registrationService.register(request);

        assertTrue(response.isRegistration());
        assertEquals("code1", response.getLogin());
        assertEquals("code1", response.getPassword());
    }

    @Test
    public void whenRegisteringExistingWebsite() {
        Mockito.when(randomStringService.generateString(5)).thenReturn("code1");
        Mockito.when(websiteRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Mockito.when(websiteRepository.existsByUrlIgnoreCase(any())).thenReturn(true);

        RegistrationRequest request = new RegistrationRequest("yandex.ru");

        RegistrationResponse response = registrationService.register(request);

        assertFalse(response.isRegistration());
        assertEquals("-", response.getLogin());
        assertEquals("-", response.getPassword());
    }

    @Test(expected = IllegalStateException.class)
    public void whenCantGenerateUniquePassOrLogin() {
        Mockito.when(randomStringService.generateString(5)).thenReturn("code1");
        Mockito.when(websiteRepository.save(any()))
                .thenThrow(new RuntimeException("ConstraintViolationException : something happened"));
        Mockito.when(websiteRepository.existsByUrlIgnoreCase(any())).thenReturn(false);
        Mockito.when(encoder.encode(any())).thenAnswer(i -> i.getArguments()[0]);

        RegistrationRequest request = new RegistrationRequest("yandex.ru");
        RegistrationResponse response = registrationService.register(request);
    }


    @Test(expected = IllegalStateException.class)
    public void whenGetSomeExceptionWhileSavingSite() {
        Mockito.when(randomStringService.generateString(5)).thenReturn("code1");
        Mockito.when(websiteRepository.save(any()))
                .thenThrow(new RuntimeException("Some other exception"));
        Mockito.when(websiteRepository.existsByUrlIgnoreCase(any())).thenReturn(false);
        Mockito.when(encoder.encode(any())).thenAnswer(i -> i.getArguments()[0]);

        RegistrationRequest request = new RegistrationRequest("yandex.ru");
        RegistrationResponse response = registrationService.register(request);
    }
}