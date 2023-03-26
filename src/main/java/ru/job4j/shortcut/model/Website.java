package ru.job4j.shortcut.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "websites")
public class Website {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String url;
    private String login;
    private String password;

    public Website(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }
}
