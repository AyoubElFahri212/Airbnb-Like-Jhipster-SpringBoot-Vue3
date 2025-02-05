package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.PropertyImageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.PropertyImage}.
 */
public interface PropertyImageService {
    /**
     * Save a propertyImage.
     *
     * @param propertyImageDTO the entity to save.
     * @return the persisted entity.
     */
    PropertyImageDTO save(PropertyImageDTO propertyImageDTO);

    /**
     * Updates a propertyImage.
     *
     * @param propertyImageDTO the entity to update.
     * @return the persisted entity.
     */
    PropertyImageDTO update(PropertyImageDTO propertyImageDTO);

    /**
     * Partially updates a propertyImage.
     *
     * @param propertyImageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PropertyImageDTO> partialUpdate(PropertyImageDTO propertyImageDTO);

    /**
     * Get the "id" propertyImage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PropertyImageDTO> findOne(Long id);

    /**
     * Delete the "id" propertyImage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the propertyImage corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PropertyImageDTO> search(String query, Pageable pageable);
}
