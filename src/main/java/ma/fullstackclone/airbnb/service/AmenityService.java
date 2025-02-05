package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.Amenity}.
 */
public interface AmenityService {
    /**
     * Save a amenity.
     *
     * @param amenityDTO the entity to save.
     * @return the persisted entity.
     */
    AmenityDTO save(AmenityDTO amenityDTO);

    /**
     * Updates a amenity.
     *
     * @param amenityDTO the entity to update.
     * @return the persisted entity.
     */
    AmenityDTO update(AmenityDTO amenityDTO);

    /**
     * Partially updates a amenity.
     *
     * @param amenityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AmenityDTO> partialUpdate(AmenityDTO amenityDTO);

    /**
     * Get the "id" amenity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AmenityDTO> findOne(Long id);

    /**
     * Delete the "id" amenity.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the amenity corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AmenityDTO> search(String query, Pageable pageable);
}
