package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedDto {
    private Integer eventId;
    @NotNull private Long timestamp;
    @NotNull private Integer userId;
    @NotNull private FeedEventType eventType;
    @NotNull private FeedOperationType operation;
    @NotNull private Integer entityId;
}
