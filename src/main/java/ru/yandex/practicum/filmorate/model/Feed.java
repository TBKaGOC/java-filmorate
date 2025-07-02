package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = {"eventId"})
public class Feed {
    private Integer eventId;
    @NotNull private Long timestamp;
    @NotNull private Integer userId;
    @NotBlank private String eventType;
    @NotBlank private String operation;
    @NotNull private Integer entityId;
}
