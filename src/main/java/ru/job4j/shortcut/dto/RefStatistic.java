package ru.job4j.shortcut.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefStatistic {
    private String ref;
    private Integer useCount;
}
