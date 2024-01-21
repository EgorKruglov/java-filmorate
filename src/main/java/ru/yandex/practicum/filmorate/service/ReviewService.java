package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.extraExceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.classes.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    @Autowired
    public ReviewService(ReviewDbStorage reviewStorage, EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.eventStorage = eventStorage;
    }

    public Review addReview(Review review) {
        if (review.getFilmId() < 0) {
            throw new FilmNotFoundException("Id фильма должен быть неотрицательным");
        }
        if (review.getUserId() < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Добавление отзыва на фильм: {}", review);
        Review reviewFromDb = reviewStorage.addReview(review);
        Event event = new Event(reviewFromDb.getUserId(), EventType.REVIEW, EventOperation.ADD, reviewFromDb.getReviewId());
        eventStorage.add(event);
        return reviewFromDb;
    }

    public Review updateReview(Review review) {
        if (review.getFilmId() < 0) {
            throw new FilmNotFoundException("Id фильма должен быть неотрицательным");
        }
        if (review.getUserId() < 0) {
            throw new UserNotFoundException("Id пользователя должен быть неотрицательным");
        }
        log.info("Обновление отзыва на фильм: {}", review);
        Review reviewFromDb = reviewStorage.updateReview(review);
        Event event = new Event(reviewFromDb.getUserId(), EventType.REVIEW, EventOperation.UPDATE, reviewFromDb.getReviewId());
        eventStorage.add(event);
        return reviewFromDb;
    }

    public void deleteReview(Integer reviewId) {
        if (reviewId < 0) {
            throw new ReviewNotFoundException("Id отзыва должен быть неотрицательным");
        }
        log.info("Удаление отзыва на фильм с id:" + reviewId);
        Review reviewFromDb = reviewStorage.getReviewById(reviewId);
        Event event = new Event(reviewFromDb.getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
        eventStorage.add(event);
        reviewStorage.deleteReview(reviewId);
    }

    public Review getReviewById(Integer reviewId) {
        if (reviewId < 0) {
            throw new ReviewNotFoundException("Id отзыва должен быть неотрицательным");
        }
        log.info("Получение отзыва по id: {}", reviewId);
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getTopReviews(Integer filmId, Integer count) {
        if (filmId == null) {
            log.info("Получение " + count + " самых популярных отзывов среди всех фильмов");
            return reviewStorage.getTopReviews(count);
        } else {
            if (filmId < 0) {
                throw new FilmNotFoundException("Id фильма должен быть неотрицательным");
            }
            log.info("Получение " + count + " самых популярных отзывов фильма с id:" + filmId);
            return reviewStorage.getTopReviewsByFilm(filmId, count);
        }
    }

    public void addLikeToReview(Integer reviewId, Integer userId) {
        if (reviewId < 0 || userId < 0) {
            throw new ReviewNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Добавление лайка на отзыв с Id:{} от пользователя с Id:{}", reviewId, userId);
        reviewStorage.addLikeToReview(reviewId, userId);
    }

    public void addDislikeToReview(Integer reviewId, Integer userId) {
        if (reviewId < 0 || userId < 0) {
            throw new ReviewNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Добавление дизлайка на отзыв с Id:{} от пользователя с Id:{}", reviewId, userId);
        reviewStorage.addDislikeToReview(reviewId, userId);
    }

    public void deleteLikeFromReview(Integer reviewId, Integer userId) {
        if (reviewId < 0 || userId < 0) {
            throw new ReviewNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Удаление лайка с отзыва Id:{} пользователем с Id:{}", reviewId, userId);
        reviewStorage.deleteLikeFromReview(reviewId, userId);
    }

    public void deleteDislikeFromReview(Integer reviewId, Integer userId) {
        if (reviewId < 0 || userId < 0) {
            throw new ReviewNotFoundException("Id должен быть неотрицательным");
        }
        log.info("Удаление дизлайка с отзыва Id:{} пользователем с Id:{}", reviewId, userId);
        reviewStorage.deleteDislikeFromReview(reviewId, userId);
    }
}
