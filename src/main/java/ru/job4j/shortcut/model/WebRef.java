package ru.job4j.shortcut.model;

import lombok.*;

import javax.persistence.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "of")
@Entity
@Table(name = "refs")
public class WebRef {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String url;
    private String code;
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Website website;
    @Column(name = "use_count")
    private int useCount;

}
