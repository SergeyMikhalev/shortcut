package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.shortcut.model.WebRef;
import ru.job4j.shortcut.repository.WebRefRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RedirectionServiceImpl implements RedirectionService {
    private final WebRefRepository refs;

    @Override
    public String getRedirectionURL(String code) {
        Optional<WebRef> ref = refs.findByCode(code);
        if (ref.isEmpty()) {
            throw new IllegalArgumentException(" Для кода: " + code + " ссылки переадресации не найдено");
        }
        return ref.get().getUrl();
    }

}
