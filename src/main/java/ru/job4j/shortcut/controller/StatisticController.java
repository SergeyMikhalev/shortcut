package ru.job4j.shortcut.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.shortcut.dto.RefStatistic;
import ru.job4j.shortcut.service.StatisticService;

import java.util.List;

@Controller
@RequestMapping("/statistic")
@AllArgsConstructor
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping
    public ResponseEntity<List<RefStatistic>> getStatistic() {
        return new ResponseEntity<>(statisticService.get(), HttpStatus.OK);
    }
}
