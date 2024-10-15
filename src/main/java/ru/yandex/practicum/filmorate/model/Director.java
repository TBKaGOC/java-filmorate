package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Director {
    private Integer id;
    @NotBlank(message = "Имя режиссера не должно быть пустым")
    private String name;
}
