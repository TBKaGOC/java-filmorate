package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(of = {"eventId"})
public class Feed {
    Integer eventId;
    @NotNull Long timestamp;
    @NotNull Integer userId;
    @NotBlank String eventType;
    @NotBlank String operation;
    @NotNull Integer entityId;
}
