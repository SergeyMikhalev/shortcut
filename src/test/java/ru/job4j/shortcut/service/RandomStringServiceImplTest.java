package ru.job4j.shortcut.service;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

public class RandomStringServiceImplTest {

    @Test
    public void generationTest() {
        RandomStringService service = new RandomStringServiceImpl(new Random());
        String generated = service.generateString(10);
        assertEquals(10, generated.length());
    }

}