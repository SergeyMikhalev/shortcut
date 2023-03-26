package ru.job4j.shortcut.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomStringServiceImpl implements RandomStringService {
    public RandomStringServiceImpl(Random rng) {
        this.rng = rng;
    }

    private final String characters = "0123456789abcdefghijklmnopqrstuvwxyz";
    private final Random rng;

    @Override
    public String generateString(int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }
}
