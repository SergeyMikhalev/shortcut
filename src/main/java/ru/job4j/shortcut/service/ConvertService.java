package ru.job4j.shortcut.service;

import ru.job4j.shortcut.dto.ConvertRequest;
import ru.job4j.shortcut.dto.ConvertResponse;

public interface ConvertService {
    ConvertResponse convert(ConvertRequest request);
}
