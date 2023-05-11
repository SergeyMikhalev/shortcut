package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.job4j.shortcut.dto.ConvertRequest;
import ru.job4j.shortcut.dto.ConvertResponse;
import ru.job4j.shortcut.model.WebRef;
import ru.job4j.shortcut.model.Website;
import ru.job4j.shortcut.repository.WebRefRepository;
import ru.job4j.shortcut.repository.WebsiteRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConvertServiceImpl implements ConvertService {

    public static final int REF_CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS_TO_GENERATE_CODE = 10;

    private final WebRefRepository refs;
    private final WebsiteRepository sites;
    private final RandomStringService randomStringService;


    @Override
    public ConvertResponse convert(ConvertRequest request) {
        int attempts = 0;
        Optional<String> retCode;
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Website site = getExistingWebsite(userName);
        checkURL(request, site);

        Optional<WebRef> ref = refs.findByUrl(request.getUrl());
        if (ref.isPresent()) {
            return new ConvertResponse(ref.get().getCode());
        }

        do {
            checkAttemptsCount(request.getUrl(), attempts);
            retCode = tryToSaveWebRef(request, site);
            attempts++;
        } while (retCode.isEmpty());

        return new ConvertResponse(retCode.get());
    }

    private Optional<String> tryToSaveWebRef(ConvertRequest request, Website site) {
        String resultCode = null;
        String code = randomStringService.generateString(REF_CODE_LENGTH);
        WebRef webRef = WebRef.of()
                .url(request.getUrl())
                .code(code)
                .website(site)
                .build();
        try {
            refs.save(webRef);
            resultCode = webRef.getCode();
        } catch (Exception e) {
            handleSaveExceptions(webRef, e);
        }
        return Optional.ofNullable(resultCode);
    }

    private void handleSaveExceptions(WebRef ref, Exception e) {
        String message = e.getMessage();
        boolean uniqueIssuesReason = message.contains("ConstraintViolationException");
        if (!uniqueIssuesReason) {
            throw new IllegalStateException("Не удалось сохранить в БД объект: " + ref + " -> " + e.getMessage());
        }
    }

    private Website getExistingWebsite(String userName) {
        Optional<Website> site = sites.findByLogin(userName);
        if (site.isEmpty()) {
            throw new IllegalArgumentException("Проблема в Авторизации и Аутентификации. "
                    + "В систему вошёл пользователь, который не зарегистрирован. "
                    + "Имя пользователя: " + userName);
        }
        return site.get();
    }

    private static void checkURL(ConvertRequest request, Website site) {
        try {
            URL refURL = new URL(request.getUrl());
            if (!refURL.getHost().equals(site.getUrl())) {
                throw new IllegalArgumentException(
                        "Ссылка, для которой запрашивается конвертация не относится к сайту,"
                                + " ассоциированному с текущей учетной записью"
                );
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Переданная ссылка не является URL " + e);
        }
    }

    private static void checkAttemptsCount(String url, int attempts) {
        if (attempts > MAX_ATTEMPTS_TO_GENERATE_CODE) {
            throw new IllegalStateException("Не удалось сгенерировать уникальный код "
                    + "для сохранения ссылки " + url
                    + "  в БД за заданное количество итераций: "
                    + MAX_ATTEMPTS_TO_GENERATE_CODE);
        }
    }
}
