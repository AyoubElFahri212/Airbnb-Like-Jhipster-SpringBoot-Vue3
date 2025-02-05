package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.Property}.
 */
public interface PropertyService {
    /**
     * Save a property.
     *
     * @param propertyDTO the entity to save.
     * @return the persisted entity.
     */
    PropertyDTO save(PropertyDTO propertyDTO);

    /**
     * Updates a property.
     *
     * @param propertyDTO the entity to update.
     * @return the persisted entity.
     */
    PropertyDTO update(PropertyDTO propertyDTO);

    /**
     * Partially updates a property.
     *
     * @param propertyDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PropertyDTO> partialUpdate(PropertyDTO propertyDTO);

    /**
     * Get all the properties with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PropertyDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" property.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PropertyDTO> findOne(Long id);

    /**
     * Delete the "id" property.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the property corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PropertyDTO> search(String query, Pageable pageable);
}
