package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.CityDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.City}.
 */
public interface CityService {
    /**
     * Save a city.
     *
     * @param cityDTO the entity to save.
     * @return the persisted entity.
     */
    CityDTO save(CityDTO cityDTO);

    /**
     * Updates a city.
     *
     * @param cityDTO the entity to update.
     * @return the persisted entity.
     */
    CityDTO update(CityDTO cityDTO);

    /**
     * Partially updates a city.
     *
     * @param cityDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CityDTO> partialUpdate(CityDTO cityDTO);

    /**
     * Get the "id" city.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CityDTO> findOne(Long id);

    /**
     * Delete the "id" city.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the city corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CityDTO> search(String query, Pageable pageable);
}
