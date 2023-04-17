package ru.job4j.shortcut.service;

import org.postgresql.util.PSQLException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;
import ru.job4j.shortcut.model.Website;
import ru.job4j.shortcut.repository.WebsiteRepository;


@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final static int LOGIN_SIZE = 5;
    private final static int PASSWORD_SIZE = 5;

    private final static int MAX_ATTEMPTS_TO_GENERATE = 10;
    private final WebsiteRepository repository;
    private final RandomStringService randomStringService;

    private final BCryptPasswordEncoder encoder;

    public RegistrationServiceImpl(WebsiteRepository repository, RandomStringService randomStringService, BCryptPasswordEncoder encoder) {
        this.repository = repository;
        this.randomStringService = randomStringService;
        this.encoder = encoder;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RegistrationResponse register(RegistrationRequest request) {
        String login;
        String password;

        if (repository.existsByUrlIgnoreCase(request.getSite())) {
            return new RegistrationResponse(false, "-", "-");
        }

        do {
            login = randomStringService.generateString(LOGIN_SIZE);
        } while (repository.existsByLoginIgnoreCase(login));
        do {
            password = randomStringService.generateString(PASSWORD_SIZE);
        } while (repository.existsByPasswordIgnoreCase(encoder.encode(password)));

        Website site = Website.of()
                .url(request.getSite())
                .login(login)
                .password(encoder.encode(password))
                .build();
        try {
            site = repository.save(site);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сохранить в БД объект: " + site + " -> " + e.getMessage());
        }
        return new RegistrationResponse(true, login, password);
    }

    private Website fastRegister(String url) {
        String login;
        String password;
        Website site = new Website();
        boolean saved = false;
        while ((!saved)) {
            login = randomStringService.generateString(LOGIN_SIZE);
            password = randomStringService.generateString(PASSWORD_SIZE);
            site = Website.of()
                    .url(url)
                    .login(login)
                    .password(encoder.encode(password))
                    .build();
            site = repository.save(site);
            saved = true;
            try {

            } catch (Exception e) {
                String message = e.getMessage();
                boolean uniqueIssuesReason = (message.contains("ограничение уникальности")
                        && ((message.contains("login_key")) | (message.contains("password_key"))
                ));
                if (!uniqueIssuesReason) {
                    throw new IllegalStateException("Не удалось сохранить в БД объект: " + site + " -> " + e.getMessage());
                }
                System.out.println(message);
            }
        }
        return site;
    }

    @Override
    public void some(String url) {
        Website site = Website.of()
                .url(url)
                .login("login")
                .password(encoder.encode("password"))
                .build();
        repository.save(site);
    }
}
