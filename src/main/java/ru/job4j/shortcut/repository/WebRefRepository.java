package ru.job4j.shortcut.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.shortcut.model.WebRef;

import java.util.Optional;

public interface WebRefRepository extends CrudRepository<WebRef, Integer> {
    Optional<WebRef> findByUrl(String url);

    boolean existsByCode(String code);

    Optional<WebRef> findByCode(String code);
}
