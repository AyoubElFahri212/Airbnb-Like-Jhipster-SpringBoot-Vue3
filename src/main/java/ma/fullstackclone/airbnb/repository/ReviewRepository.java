package ma.fullstackclone.airbnb.repository;

import java.util.List;
import ma.fullstackclone.airbnb.domain.Review;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Review entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {
    @Query("select review from Review review where review.author.login = ?#{authentication.name}")
    List<Review> findByAuthorIsCurrentUser();
}
