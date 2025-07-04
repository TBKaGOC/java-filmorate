package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
