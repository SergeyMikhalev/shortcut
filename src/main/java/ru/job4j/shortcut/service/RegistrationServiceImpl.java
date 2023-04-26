package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.shortcut.dto.RegistrationRequest;
import ru.job4j.shortcut.dto.RegistrationResponse;
import ru.job4j.shortcut.model.Website;
import ru.job4j.shortcut.repository.WebsiteRepository;

import java.util.Optional;


@Service
@AllArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final static int LOGIN_SIZE = 5;
    private final static int PASSWORD_SIZE = 5;

    private final static int MAX_ATTEMPTS_TO_GENERATE = 10;
    private final WebsiteRepository repository;
    private final RandomStringService randomStringService;

    private final BCryptPasswordEncoder encoder;


    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        int attemts = 0;
        Optional<RegistrationResponse> saveResult;
        do {
            if (repository.existsByUrlIgnoreCase(request.getSite())) {
                return new RegistrationResponse(false, "-", "-");
            }
            checkAttemptsCount(request, attemts);
            attemts++;
            saveResult = tryToRegisterSite(request);
        } while (saveResult.isEmpty());
        return saveResult.get();
    }

    private Optional<RegistrationResponse> tryToRegisterSite(RegistrationRequest request) {
        RegistrationResponse result = null;
        Website site = getWebsite(request.getSite());
        String password = site.getPassword();
        site.setPassword(encoder.encode(site.getPassword()));
        try {
            site = repository.save(site);
            result = new RegistrationResponse(true, site.getLogin(), password);
        } catch (Exception e) {
            handleSaveExceptions(site, e);
        }
        return Optional.ofNullable(result);
    }

    private static void checkAttemptsCount(RegistrationRequest request, int attempts) {
        if (attempts > MAX_ATTEMPTS_TO_GENERATE) {
            throw new IllegalStateException("Не удалось сгенерировать уникальные логин "
                    + "и пароль для сохранения сайта " + request.getSite()
                    + "  в БД за заданное количество итераций: "
                    + MAX_ATTEMPTS_TO_GENERATE);
        }
    }

    private void handleSaveExceptions(Website site, Exception e) {
        String message = e.getMessage();
        boolean uniqueIssuesReason = message.contains("ConstraintViolationException");
        if (!uniqueIssuesReason) {
            throw new IllegalStateException("Не удалось сохранить в БД объект: " + site + " -> " + e.getMessage());
        }
    }

    private Website getWebsite(String url) {
        String login = randomStringService.generateString(LOGIN_SIZE);
        String password = randomStringService.generateString(PASSWORD_SIZE);
        Website site = Website.of()
                .url(url)
                .login(login)
                .password(password)
                .build();
        return site;
    }
}
