package ru.job4j.shortcut.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refs")
public class WebRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String url;
    private String code;
    @Column(name = "site_id")
    private int siteId;

    @Column(name = "use_count")
    private int useCount;

    public WebRef(String url, String code, int siteId) {
        this.url = url;
        this.code = code;
        this.siteId = siteId;
        this.useCount = 0;
    }
}
