package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.BookingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.Booking}.
 */
public interface BookingService {
    /**
     * Save a booking.
     *
     * @param bookingDTO the entity to save.
     * @return the persisted entity.
     */
    BookingDTO save(BookingDTO bookingDTO);

    /**
     * Updates a booking.
     *
     * @param bookingDTO the entity to update.
     * @return the persisted entity.
     */
    BookingDTO update(BookingDTO bookingDTO);

    /**
     * Partially updates a booking.
     *
     * @param bookingDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BookingDTO> partialUpdate(BookingDTO bookingDTO);

    /**
     * Get the "id" booking.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BookingDTO> findOne(Long id);

    /**
     * Delete the "id" booking.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the booking corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BookingDTO> search(String query, Pageable pageable);
}
