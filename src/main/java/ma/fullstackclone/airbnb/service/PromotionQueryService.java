package ma.fullstackclone.airbnb.service;

import ma.fullstackclone.airbnb.domain.*; // for static metamodels
import ma.fullstackclone.airbnb.domain.Promotion;
import ma.fullstackclone.airbnb.repository.PromotionRepository;
import ma.fullstackclone.airbnb.repository.search.PromotionSearchRepository;
import ma.fullstackclone.airbnb.service.criteria.PromotionCriteria;
import ma.fullstackclone.airbnb.service.dto.PromotionDTO;
import ma.fullstackclone.airbnb.service.mapper.PromotionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Promotion} entities in the database.
 * The main input is a {@link PromotionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PromotionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PromotionQueryService extends QueryService<Promotion> {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionQueryService.class);

    private final PromotionRepository promotionRepository;

    private final PromotionMapper promotionMapper;

    private final PromotionSearchRepository promotionSearchRepository;

    public PromotionQueryService(
        PromotionRepository promotionRepository,
        PromotionMapper promotionMapper,
        PromotionSearchRepository promotionSearchRepository
    ) {
        this.promotionRepository = promotionRepository;
        this.promotionMapper = promotionMapper;
        this.promotionSearchRepository = promotionSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PromotionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PromotionDTO> findByCriteria(PromotionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Promotion> specification = createSpecification(criteria);
        return promotionRepository.findAll(specification, page).map(promotionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PromotionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Promotion> specification = createSpecification(criteria);
        return promotionRepository.count(specification);
    }

    /**
     * Function to convert {@link PromotionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Promotion> createSpecification(PromotionCriteria criteria) {
        Specification<Promotion> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Promotion_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), Promotion_.code));
            }
            if (criteria.getDiscountType() != null) {
                specification = specification.and(buildSpecification(criteria.getDiscountType(), Promotion_.discountType));
            }
            if (criteria.getDiscountValue() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDiscountValue(), Promotion_.discountValue));
            }
            if (criteria.getValidFrom() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValidFrom(), Promotion_.validFrom));
            }
            if (criteria.getValidUntil() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getValidUntil(), Promotion_.validUntil));
            }
            if (criteria.getMaxUses() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMaxUses(), Promotion_.maxUses));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), Promotion_.isActive));
            }
        }
        return specification;
    }
}
