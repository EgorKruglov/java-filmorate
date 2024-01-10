package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    public Review addReview(Review review);

    public Review updateReview(Review review);

    public void deleteReview(Integer reviewId);

    public Review getReviewById(Integer reviewId);

    public List<Review> getTopReviews(Integer count);

    public List<Review> getTopReviewsByFilm(Integer filmId, Integer count);

    public void addLikeToReview(Integer reviewId, Integer userId);

    public void addDislikeToReview(Integer reviewId, Integer userId);

    public void deleteLikeFromReview(Integer reviewId, Integer userId);

    public void deleteDislikeFromReview(Integer reviewId, Integer userId);
}
