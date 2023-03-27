package ru.job4j.shortcut.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.job4j.shortcut.model.Website;

@Repository
public interface WebsiteRepository extends CrudRepository<Website, Integer> {
    boolean existsByUrlIgnoreCase(@NonNull String url);

    boolean existsByLoginIgnoreCase(@NonNull String login);

    boolean existsByPasswordIgnoreCase(@NonNull String password);

}
