package ru.job4j.shortcut.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;
import ru.job4j.shortcut.model.Website;
import ru.job4j.shortcut.repository.WebsiteRepository;

import java.sql.SQLException;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final static int LOGIN_SIZE = 5;
    private final static int PASSWORD_SIZE = 5;
    private final WebsiteRepository repository;
    private final RandomStringService randomStringService;

    public RegistrationServiceImpl(WebsiteRepository repository, RandomStringServiceImpl randomStringService) {
        this.repository = repository;
        this.randomStringService = randomStringService;
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
        } while (repository.existsByPasswordIgnoreCase(password));

        Website site = new Website(request.getSite(), login, password);

        try {
            site = repository.save(site);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сохранить в БД объект: " + site + " -> " + e.getMessage());
        }
        return new RegistrationResponse(true, login, password);
    }
}
