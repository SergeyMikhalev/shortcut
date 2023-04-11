package ru.job4j.shortcut.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.shortcut.dto.RefStatistic;
import ru.job4j.shortcut.repository.WebRefRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final WebRefRepository refs;

    @Override
    public List<RefStatistic> get() {
        return refs.findAll().stream().map(ref -> new RefStatistic(ref.getUrl(), ref.getUseCount())
        ).collect(Collectors.toList());
    }
}
