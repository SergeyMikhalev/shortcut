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

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConvertServiceImpl implements ConvertService {

    public static final int REF_CODE_LENGTH = 6;
    private final WebRefRepository refs;
    private final WebsiteRepository sites;

    private final RandomStringService randomStringService;


    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ConvertResponse convert(ConvertRequest request) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        Website site = getExistingWebsite(userName);
        checkURL(request, site);

        Optional<WebRef> ref = refs.findByUrl(request.getUrl());
        if (ref.isPresent()) {
            return new ConvertResponse(ref.get().getCode());
        }
        String code = getUniqueRefCode();
        tryToSaveWebRef(request, site, code);
        return new ConvertResponse(code);
    }

    private void tryToSaveWebRef(ConvertRequest request, Website site, String code) {
        WebRef webRef = new WebRef(request.getUrl(), code, site.getId());
        try {
            refs.save(webRef);
        } catch (Exception e) {
            throw new IllegalStateException("Не удалось сохранить в БД объект: " + webRef + " -> " + e.getMessage());
        }
    }

    private String getUniqueRefCode() {
        String code;
        do {
            code = randomStringService.generateString(REF_CODE_LENGTH);
        } while (refs.existsByCode(code));
        return code;
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
}
