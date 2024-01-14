package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Director {
    private Long id;
    @NotBlank(message = "Поле 'Имя директора' не можкт быть пустым")
    private String name;

    public Map<String, Object> toMap() {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", name);
        filmMap.put("director_id", id);
        return filmMap;
    }
}