package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.extraExceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review,  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            throw new ValidationException("Ошибка валидации отзыва: " + errors);
        }
        Review resultReview = reviewService.addReview(review);
        log.info("Отзыв добавлен id:" + review.getReviewId());
        return resultReview;
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.append(error.getDefaultMessage()).append("\n")
            );
            throw new ValidationException("Ошибка валидации отзыва: " + errors);
        }
        Review resultReview = reviewService.updateReview(review);
        log.info("Данные отзыва обновлены id:" + review.getReviewId());
        return resultReview;
    }

    @DeleteMapping("/{reviewId}")
    public Map<String, String> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        log.info("Удалён отзыв id:" + reviewId);
        return Map.of("message", "Удалён отзыв с id:" + reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Integer reviewId) {
        Review resultReview = reviewService.getReviewById(reviewId);
        log.info("Отправлена информация об отзыве id:" + reviewId);
        return resultReview;
    }

    @GetMapping()
    public List<Review> getTopReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        List<Review> topReviews = reviewService.getTopReviews(filmId, count);
        log.info("Отправлено " + topReviews.size() + " самых популярных отзывов");
        return topReviews;
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Map<String, String> addLikeToReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.addLikeToReview(reviewId, userId);
        log.info("На отзыв id:" + reviewId + " поставлен лайк пользователем id:" + userId);
        return Map.of("message", "На отзыв id:" + reviewId + " поставлен лайк пользователем id:" + userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Map<String, String> addDislikeToReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.addDislikeToReview(reviewId, userId);
        log.info("На отзыв id:" + reviewId + " поставлен дизлайк пользователем id:" + userId);
        return Map.of("message", "На отзыв id:" + reviewId + " поставлен дизлайк пользователем id:" + userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public Map<String, String> deleteLikeFromReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteLikeFromReview(reviewId, userId);
        log.info("С отзыва id:" + reviewId + " удалён лайк пользователя id:" + userId);
        return Map.of("message", "С отзыва id:" + reviewId + " удалён лайк пользователя id:" + userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public Map<String, String> deleteDislikeFromReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteDislikeFromReview(reviewId, userId);
        log.info("С отзыва id:" + reviewId + " удалён дизлайк пользователя id:" + userId);
        return Map.of("message", "С отзыва id:" + reviewId + " удалён дизлайк пользователя id:" + userId);
    }
}
