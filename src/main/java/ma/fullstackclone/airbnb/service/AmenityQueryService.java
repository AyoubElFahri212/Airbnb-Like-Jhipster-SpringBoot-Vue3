package ma.fullstackclone.airbnb.service;

import jakarta.persistence.criteria.JoinType;
import ma.fullstackclone.airbnb.domain.*; // for static metamodels
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.repository.AmenityRepository;
import ma.fullstackclone.airbnb.repository.search.AmenitySearchRepository;
import ma.fullstackclone.airbnb.service.criteria.AmenityCriteria;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
import ma.fullstackclone.airbnb.service.mapper.AmenityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Amenity} entities in the database.
 * The main input is a {@link AmenityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AmenityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AmenityQueryService extends QueryService<Amenity> {

    private static final Logger LOG = LoggerFactory.getLogger(AmenityQueryService.class);

    private final AmenityRepository amenityRepository;

    private final AmenityMapper amenityMapper;

    private final AmenitySearchRepository amenitySearchRepository;

    public AmenityQueryService(
        AmenityRepository amenityRepository,
        AmenityMapper amenityMapper,
        AmenitySearchRepository amenitySearchRepository
    ) {
        this.amenityRepository = amenityRepository;
        this.amenityMapper = amenityMapper;
        this.amenitySearchRepository = amenitySearchRepository;
    }

    /**
     * Return a {@link Page} of {@link AmenityDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AmenityDTO> findByCriteria(AmenityCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Amenity> specification = createSpecification(criteria);
        return amenityRepository.findAll(specification, page).map(amenityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AmenityCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Amenity> specification = createSpecification(criteria);
        return amenityRepository.count(specification);
    }

    /**
     * Function to convert {@link AmenityCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Amenity> createSpecification(AmenityCriteria criteria) {
        Specification<Amenity> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Amenity_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Amenity_.name));
            }
            if (criteria.getIconClass() != null) {
                specification = specification.and(buildStringSpecification(criteria.getIconClass(), Amenity_.iconClass));
            }
            if (criteria.getPropertyId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPropertyId(), root -> root.join(Amenity_.properties, JoinType.LEFT).get(Property_.id))
                );
            }
        }
        return specification;
    }
}
