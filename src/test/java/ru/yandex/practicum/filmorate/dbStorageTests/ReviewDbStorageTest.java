package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.classes.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.classes.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private ReviewStorage reviewStorage;
    private UserDbStorage userStorage;
    private FilmDbStorage filmStorage;
    private DirectorStorage directorStorage;
    User user;
    Film film;

    @BeforeEach
    public void updateDb() {
        reviewStorage = new ReviewDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, directorStorage);
        user = userStorage.addUser(new User("user@email.ru", "vanya123", "Ivan Petrov", LocalDate.of(1990,
                1, 1)));
        film = filmStorage.addFilm(new Film("Film One", "description1", LocalDate.of(2010, 5, 10),
                90, new Mpa(1, null), Set.of(new Genre(1, null), new Genre(2, null))));
    }

    @Test
    public void testAddReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        assertThat(addedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review);
    }

    @Test
    public void testUpdateReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        addedReview.setContent("updated content");
        Review updatedReview = reviewStorage.updateReview(addedReview);

        assertThat(updatedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedReview);
    }

    @Test
    public void testDeleteReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        assertThat(reviewStorage.getTopReviews(10).size()).isEqualTo(1);

        reviewStorage.deleteReview(addedReview.getReviewId());

        assertThat(reviewStorage.getTopReviews(10)).isEmpty();
    }

    @Test
    public void testGetReviewById() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        Review retrievedReview = reviewStorage.getReviewById(addedReview.getReviewId());

        assertThat(retrievedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(addedReview);
    }

    @Test
    public void testGetTopReviews() {
        Review review1 = new Review("content one", false, user.getId(), film.getId());
        Review review2 = new Review("content two", true, user.getId(), film.getId());
        Review review3 = new Review("content three", true, user.getId(), film.getId());

        Review addedReview1 = reviewStorage.addReview(review1);
        Review addedReview2 = reviewStorage.addReview(review2);
        Review addedReview3 = reviewStorage.addReview(review3);

        reviewStorage.addLikeToReview(review2.getReviewId(), user.getId());

        List<Review> topReviewList = reviewStorage.getTopReviews(10);
        assertThat(topReviewList.get(0).getReviewId()).isEqualTo(addedReview2.getReviewId());  // Проверка порядка
        assertThat(topReviewList.get(2).getReviewId()).isEqualTo(addedReview3.getReviewId());
    }

    @Test
    public void testGetTopReviewsByFilm() {
        Review review1 = new Review("content one", false, user.getId(), film.getId());
        Review review2 = new Review("content two", true, user.getId(), film.getId());
        Review review3 = new Review("content three", true, user.getId(), film.getId());

        Review addedReview1 = reviewStorage.addReview(review1);
        Review addedReview2 = reviewStorage.addReview(review2);
        Review addedReview3 = reviewStorage.addReview(review3);

        reviewStorage.addLikeToReview(review2.getReviewId(), user.getId());

        List<Review> topReviewList = reviewStorage.getTopReviewsByFilm(film.getId(), 10);
        assertThat(topReviewList.get(0).getReviewId()).isEqualTo(addedReview2.getReviewId());  // Проверка порядка
        assertThat(topReviewList.get(2).getReviewId()).isEqualTo(addedReview3.getReviewId());
    }

    @Test
    public void testAddLikeToReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        reviewStorage.addLikeToReview(addedReview.getReviewId(), user.getId());

        Review likedReview = reviewStorage.getReviewById(addedReview.getReviewId());
        assertThat(likedReview.getUseful()).isEqualTo(1);
    }

    @Test
    public void testAddDislikeToReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        reviewStorage.addDislikeToReview(addedReview.getReviewId(), user.getId());

        Review likedReview = reviewStorage.getReviewById(addedReview.getReviewId());
        assertThat(likedReview.getUseful()).isEqualTo(-1);
    }

    @Test
    public void testDeleteLikeFromReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        reviewStorage.addLikeToReview(addedReview.getReviewId(), user.getId());
        reviewStorage.deleteLikeFromReview(addedReview.getReviewId(), user.getId());

        Review likedReview = reviewStorage.getReviewById(addedReview.getReviewId());
        assertThat(likedReview.getUseful()).isEqualTo(0);
    }

    @Test
    public void testDeleteDislikeFromReview() {
        Review review = new Review("content one", false, user.getId(), film.getId());
        Review addedReview = reviewStorage.addReview(review);

        reviewStorage.addDislikeToReview(addedReview.getReviewId(), user.getId());
        reviewStorage.deleteDislikeFromReview(addedReview.getReviewId(), user.getId());

        Review likedReview = reviewStorage.getReviewById(addedReview.getReviewId());
        assertThat(likedReview.getUseful()).isEqualTo(0);
    }
}
