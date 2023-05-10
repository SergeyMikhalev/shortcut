package ru.job4j.shortcut.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.job4j.shortcut.model.WebRef;
import ru.job4j.shortcut.model.Website;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = LiquibaseAutoConfiguration.class)
public class WebRefRepositoryTest {

    @Autowired
    private WebRefRepository webRefRepository;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Test
    public void whenAddAndFindByCode() {
        Optional<WebRef> webRefFromDB = webRefRepository.findByCode("aaa");
        assertTrue(webRefFromDB.isPresent());
        assertEquals("ya.ru/some", webRefFromDB.get().getUrl());
        assertEquals("ya.ru", webRefFromDB.get().getWebsite().getUrl());
    }

    @Test
    public void whenAddAndNotFindByWrongCode() {
        Optional<WebRef> webRefFromDB = webRefRepository.findByCode("bbb");
        assertFalse(webRefFromDB.isPresent());
    }

    @Test
    public void whenAddAndFindByUrl() {
        Optional<WebRef> webRefFromDB = webRefRepository.findByUrl("ya.ru/some");
        assertTrue(webRefFromDB.isPresent());
        assertEquals("aaa", webRefFromDB.get().getCode());
        assertEquals("ya.ru", webRefFromDB.get().getWebsite().getUrl());
    }

    @Test
    public void whenAddAndNotFindByWrongUrl() {
        Optional<WebRef> webRefFromDB = webRefRepository.findByUrl("ya.ru/other");
        assertFalse(webRefFromDB.isPresent());
    }

    @Test
    public void whenAddAndExistsByCode() {
        assertTrue(webRefRepository.existsByCode("aaa"));
    }

    @Test
    public void whenAddAndNotExistsByWrongCode() {
        assertFalse(webRefRepository.existsByCode("bbb"));
    }

    @Test
    public void whenAddAndFindAll() {
        List<WebRef> refs = webRefRepository.findAll();
        assertEquals(refs.size(), 2);
        List<String> urls = refs.stream().map(WebRef::getUrl).collect(Collectors.toList());
        List<String> urlsExpected = List.of("ya.ru/some", "ya.ru/someOther");
        assertTrue(urlsExpected.containsAll(urls));
    }

    @Test
    public void whenUpdateRefCountOnExisting() {
        Optional<WebRef> ref = webRefRepository.findByCode("aaa");
        assertTrue(ref.isPresent());
        Integer id = ref.get().getId();
        assertEquals(0, ref.get().getUseCount());
        assertEquals(1, webRefRepository.incrementUseCount(id));
        ref = webRefRepository.findByCode("aaa");
        assertTrue(ref.isPresent());
        assertEquals(1, ref.get().getUseCount());
    }

    @Test
    public void whenUpdateRefCountOnNonExisting() {
        Optional<WebRef> ref = webRefRepository.findByCode("ccc");
        assertTrue(ref.isPresent());
        Integer id = ref.get().getId() + 1;
        assertEquals(0, webRefRepository.incrementUseCount(id));
    }

    @Before
    public void createSimpleWebRef() {
        Website website = Website.of()
                .url("ya.ru")
                .login("login")
                .password("password")
                .build();
        websiteRepository.save(website);
        WebRef webRef = WebRef.of()
                .website(website)
                .code("aaa")
                .useCount(0)
                .url("ya.ru/some")
                .build();
        webRefRepository.save(webRef);
        WebRef webRef2 = WebRef.of()
                .website(website)
                .code("ccc")
                .useCount(0)
                .url("ya.ru/someOther")
                .build();
        webRefRepository.save(webRef2);
    }
}