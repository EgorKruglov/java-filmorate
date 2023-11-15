package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.movieReleaseDateValidator.MovieReleaseDate;
import ru.yandex.practicum.filmorate.validators.nonNegativeDurationValidator.PositiveDuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Comparable<Film> {

    private final Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private final String name;

    @Size(max = 200, message = "Описание должно быть меньше 200 символов")
    private final String description;

    @MovieReleaseDate
    private final LocalDate releaseDate;

    @PositiveDuration(message = "Продолжительность фильма должна быть положительным числом")
    private final Duration duration;

    private final Set<Integer> usersIdsWhoLiked = new HashSet<>();

    public void addLike(Integer userId) {
        usersIdsWhoLiked.add(userId);
    }

    public void deleteLike(Integer userId) {
        usersIdsWhoLiked.remove(userId);
    }

    @Override
    public int compareTo(Film f) {
        return this.getUsersIdsWhoLiked().size() - f.getUsersIdsWhoLiked().size();
    }
}
