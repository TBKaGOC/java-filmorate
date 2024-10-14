package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FeedDto {
    Integer eventId;
    @NotNull Long timestamp;
    @NotNull Integer userId;
    @NotNull FeedEventType eventType;
    @NotNull FeedOperationType operation;
    @NotNull Integer entityId;
}
