package ru.job4j.shortcut.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.job4j.shortcut.model.WebRef;

import java.util.List;
import java.util.Optional;

public interface WebRefRepository extends CrudRepository<WebRef, Integer> {

    List<WebRef> findAll();
    Optional<WebRef> findByUrl(String url);

    boolean existsByCode(String code);

    Optional<WebRef> findByCode(String code);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "update refs set use_count = use_count+1 where id =?1", nativeQuery = true)
    int incrementUseCount(Integer id);
}
