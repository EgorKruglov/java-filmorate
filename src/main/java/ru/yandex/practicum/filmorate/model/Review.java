package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Positive
    private Integer reviewId;

    @NotBlank
    private String content;

    @NotNull
    private Boolean isPositive;

    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    private Integer useful;  // рейтинг полезности

    public Review(Integer reviewId, String content, Boolean isPositive, Integer userId, Integer filmId) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }

    public Review(String content, Boolean isPositive, Integer userId, Integer filmId) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
    }

    public Map<String, Object> toMap() {
        Map<String,Object> reviewMap = new HashMap<>();
        reviewMap.put("content", content);
        reviewMap.put("is_positive", isPositive);
        reviewMap.put("user_id", userId);
        reviewMap.put("film_Id", filmId);
        reviewMap.put("useful", useful);
        return reviewMap;
    }
}
