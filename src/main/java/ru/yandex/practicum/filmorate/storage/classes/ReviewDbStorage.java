package ru.yandex.practicum.filmorate.storage.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.extraExceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.extraExceptions.SQLErrorTransaction;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        try {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("review_id");
            int reviewId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
            review.setReviewId(reviewId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось добавить отзыв");
        }
        review.setUseful(0);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        getReviewById(review.getReviewId());  // Проверка наличия в бд
        String sqlQuery = "UPDATE reviews\n" +
                "SET content = ?,\n" +
                "    is_positive = ?\n" +
                "WHERE review_id = ?";
        try {
            jdbcTemplate.update(sqlQuery,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());
            return getReviewById(review.getReviewId());
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось обновить отзыв");
        }
    }

    @Override
    public void deleteReview(Integer reviewId) {
        try {
            String sqlQuery = "DELETE\n" +
                    "FROM reviews\n" +
                    "WHERE review_id = ?";
            jdbcTemplate.update(sqlQuery, reviewId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось удалить отзыв с id:" + reviewId);
        }
    }

    @Override
    public Review getReviewById(Integer reviewId) {
        String sqlQuery = "SELECT *\n" + // Получение отзыва
                "FROM reviews\n" +
                "WHERE review_id = ?";
        try {
            Review review = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, reviewId);
            review.setUseful(getUsefulByReview(reviewId));
            return review;
        } catch (DataAccessException e) {
            throw new ReviewNotFoundException("Не удалось отправить отзыв с id:" + reviewId);
        }
    }

    @Override
    public List<Review> getTopReviews(Integer count) {
        String sqlQuery = "SELECT\n" +
                "  r.review_id,\n" +
                "  r.content,\n" +
                "  r.is_positive,\n" +
                "  r.user_id,\n" +
                "  r.film_id,\n" +
                "  COUNT(rl.like_id) AS like_count,\n" +
                "  (SELECT COUNT(rl2.like_id) FROM review_likes rl2 WHERE rl2.review_id = r.review_id AND rl2.is_like = FALSE) " +
                "AS dislike_count\n" +
                "FROM\n" +
                "  reviews r\n" +
                "LEFT JOIN\n" +
                "  review_likes rl ON r.review_id = rl.review_id AND rl.is_like = TRUE\n" +
                "GROUP BY\n" +
                "  r.review_id, r.content, r.is_positive, r.user_id, r.film_id\n" +
                "ORDER BY\n" +
                "  like_count DESC, dislike_count ASC\n" +
                "LIMIT ?;";
        try {
            List<Review> topReviewsByFilm = jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
            for (Review review : topReviewsByFilm) {
                review.setUseful(getUsefulByReview(review.getReviewId()));
            }
            return topReviewsByFilm;
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось отправить список самых популярных отзывов");
        }
    }

    @Override
    public List<Review> getTopReviewsByFilm(Integer filmId, Integer count) {
        String sqlQuery = "SELECT *\n" +
                "FROM reviews\n" +
                "WHERE film_id = ?\n" +
                "ORDER BY (\n" +
                "  SELECT COUNT(*) FROM review_likes WHERE review_id = reviews.review_id AND is_like = true\n" +
                ") DESC, (\n" +
                "  SELECT COUNT(*) FROM review_likes WHERE review_id = reviews.review_id AND is_like = false\n" +
                ") ASC\n" +
                "LIMIT ?;";
        try {
            List<Review> topReviewsByFilm = jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
            for (Review review : topReviewsByFilm) {
                review.setUseful(getUsefulByReview(review.getReviewId()));
            }
            return topReviewsByFilm;
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось отправить список самых популярных отзывов");
        }
    }

    @Override
    public void addLikeToReview(Integer reviewId, Integer userId) {
        try {
            String sqlQuery = "INSERT INTO review_likes(review_id, user_id, is_like)\n" +
                    "VALUES (?, ?, true)";
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить лайк на отзыв id:" + reviewId);
        }
    }

    @Override
    public void addDislikeToReview(Integer reviewId, Integer userId) {
        try {
            String sqlQuery = "INSERT INTO review_likes(review_id, user_id, is_like)\n" +
                    "VALUES (?, ?, false)";
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось добавить дизлайк на отзыв id:" + reviewId);
        }
    }

    @Override
    public void deleteLikeFromReview(Integer reviewId, Integer userId) {
        try {
            String sqlQuery = "DELETE\n" +
                    "FROM review_likes\n" +
                    "WHERE review_id = ?\n" +
                    "  AND user_id = ?\n" +
                    "LIMIT 1";
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось удалить лайк на отзыв id:" + reviewId);
        }
    }

    @Override
    public void deleteDislikeFromReview(Integer reviewId, Integer userId) {
        try {
            String sqlQuery = "DELETE\n" +
                    "FROM review_likes\n" +
                    "WHERE review_id = ?\n" +
                    "  AND user_id = ?\n" +
                    "LIMIT 1";
            jdbcTemplate.update(sqlQuery, reviewId, userId);
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new SQLErrorTransaction("Не удалось удалить дизлайк на отзыв id:" + reviewId);
        }
    }

    private Integer getUsefulByReview(Integer reviewId) {
        try {
            String sqlQuery = "SELECT\n" +
                    "  (SELECT COUNT(*)\n" +
                    "   FROM review_likes\n" +
                    "   WHERE review_id = ?\n" +
                    "     AND is_like = TRUE) -\n" +
                    "  (SELECT COUNT(*)\n" +
                    "   FROM review_likes\n" +
                    "   WHERE review_id = ?\n" +
                    "     AND is_like = FALSE) AS like_dislike_difference;";
            Integer useful = jdbcTemplate.queryForObject(sqlQuery, Integer.class, reviewId, reviewId);
            if (useful == null) {
                return 0;
            }
            return useful;
        } catch (DataAccessException e) {
            throw new SQLErrorTransaction("Не удалось получить рейтинг отзыва id:" + reviewId);
        }
    }

    private Review mapRowToReview(ResultSet resultSet, int i) {
        try {
            return new Review(resultSet.getInt("review_id"),
                    resultSet.getString("content"),
                    resultSet.getBoolean("is_positive"),
                    resultSet.getInt("user_id"),
                    resultSet.getInt("film_id"));
        } catch (SQLException e) {
            throw new SQLErrorTransaction("Не удалось создать объект отзыва на основе базы данных");
        }
    }
}
