package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.PropertyCategory}.
 */
public interface PropertyCategoryService {
    /**
     * Save a propertyCategory.
     *
     * @param propertyCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    PropertyCategoryDTO save(PropertyCategoryDTO propertyCategoryDTO);

    /**
     * Updates a propertyCategory.
     *
     * @param propertyCategoryDTO the entity to update.
     * @return the persisted entity.
     */
    PropertyCategoryDTO update(PropertyCategoryDTO propertyCategoryDTO);

    /**
     * Partially updates a propertyCategory.
     *
     * @param propertyCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PropertyCategoryDTO> partialUpdate(PropertyCategoryDTO propertyCategoryDTO);

    /**
     * Get the "id" propertyCategory.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PropertyCategoryDTO> findOne(Long id);

    /**
     * Delete the "id" propertyCategory.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the propertyCategory corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PropertyCategoryDTO> search(String query, Pageable pageable);
}
