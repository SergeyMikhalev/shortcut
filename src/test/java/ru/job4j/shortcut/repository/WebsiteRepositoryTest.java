package ru.job4j.shortcut.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.model.Website;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = LiquibaseAutoConfiguration.class)
public class WebsiteRepositoryTest {

    @Autowired
    private WebsiteRepository websiteRepository;

    @Test
    public void whenAddAndThenFindByLogin() {
        Website website = Website.of()
                .url("ya.ru")
                .login("login")
                .password("password")
                .build();
        websiteRepository.save(website);
        Optional<Website> websiteFromDB = websiteRepository.findByLogin("login");
        assertTrue(websiteFromDB.isPresent());
        assertEquals("password", websiteFromDB.get().getPassword());
    }

    @Test
    public void whenAddAndThenNotFindByAnotherLogin() {
        Website website = Website.of()
                .url("ya.ru")
                .login("login")
                .password("password")
                .build();
        websiteRepository.save(website);
        Optional<Website> websiteFromDB = websiteRepository.findByLogin("nigol");
        assertFalse(websiteFromDB.isPresent());
    }

    @Test
    public void whenAddAndExistsByUrlIgnoreCase() {
        Website website = Website.of()
                .url("ya.ru")
                .login("login")
                .password("password")
                .build();
        websiteRepository.save(website);
        assertTrue(websiteRepository.existsByUrlIgnoreCase("ya.ru"));
        assertTrue(websiteRepository.existsByUrlIgnoreCase("yA.Ru"));
    }

    public void whenAddAndNotExistsAnotherUrl() {
        Website website = Website.of()
                .url("ya.ru")
                .login("login")
                .password("password")
                .build();
        websiteRepository.save(website);
        assertFalse(websiteRepository.existsByUrlIgnoreCase("google.com"));
    }

}