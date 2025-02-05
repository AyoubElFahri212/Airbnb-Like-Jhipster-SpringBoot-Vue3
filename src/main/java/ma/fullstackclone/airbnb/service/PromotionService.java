package ma.fullstackclone.airbnb.service;

import java.util.Optional;
import ma.fullstackclone.airbnb.service.dto.PromotionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link ma.fullstackclone.airbnb.domain.Promotion}.
 */
public interface PromotionService {
    /**
     * Save a promotion.
     *
     * @param promotionDTO the entity to save.
     * @return the persisted entity.
     */
    PromotionDTO save(PromotionDTO promotionDTO);

    /**
     * Updates a promotion.
     *
     * @param promotionDTO the entity to update.
     * @return the persisted entity.
     */
    PromotionDTO update(PromotionDTO promotionDTO);

    /**
     * Partially updates a promotion.
     *
     * @param promotionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PromotionDTO> partialUpdate(PromotionDTO promotionDTO);

    /**
     * Get the "id" promotion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PromotionDTO> findOne(Long id);

    /**
     * Delete the "id" promotion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the promotion corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PromotionDTO> search(String query, Pageable pageable);
}
