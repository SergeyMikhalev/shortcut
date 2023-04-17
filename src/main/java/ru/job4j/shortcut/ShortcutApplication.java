package ru.job4j.shortcut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories
public class ShortcutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortcutApplication.class, args);
    }

}
