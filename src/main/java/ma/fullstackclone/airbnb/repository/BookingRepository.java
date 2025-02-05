package ma.fullstackclone.airbnb.repository;

import java.util.List;
import ma.fullstackclone.airbnb.domain.Booking;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Booking entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    @Query("select booking from Booking booking where booking.guest.login = ?#{authentication.name}")
    List<Booking> findByGuestIsCurrentUser();
}
