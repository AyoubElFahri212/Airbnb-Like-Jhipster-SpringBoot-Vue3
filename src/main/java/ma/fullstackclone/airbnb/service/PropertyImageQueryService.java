package ma.fullstackclone.airbnb.service;

import jakarta.persistence.criteria.JoinType;
import ma.fullstackclone.airbnb.domain.*; // for static metamodels
import ma.fullstackclone.airbnb.domain.PropertyImage;
import ma.fullstackclone.airbnb.repository.PropertyImageRepository;
import ma.fullstackclone.airbnb.repository.search.PropertyImageSearchRepository;
import ma.fullstackclone.airbnb.service.criteria.PropertyImageCriteria;
import ma.fullstackclone.airbnb.service.dto.PropertyImageDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyImageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PropertyImage} entities in the database.
 * The main input is a {@link PropertyImageCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PropertyImageDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PropertyImageQueryService extends QueryService<PropertyImage> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyImageQueryService.class);

    private final PropertyImageRepository propertyImageRepository;

    private final PropertyImageMapper propertyImageMapper;

    private final PropertyImageSearchRepository propertyImageSearchRepository;

    public PropertyImageQueryService(
        PropertyImageRepository propertyImageRepository,
        PropertyImageMapper propertyImageMapper,
        PropertyImageSearchRepository propertyImageSearchRepository
    ) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyImageMapper = propertyImageMapper;
        this.propertyImageSearchRepository = propertyImageSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PropertyImageDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PropertyImageDTO> findByCriteria(PropertyImageCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PropertyImage> specification = createSpecification(criteria);
        return propertyImageRepository.findAll(specification, page).map(propertyImageMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PropertyImageCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PropertyImage> specification = createSpecification(criteria);
        return propertyImageRepository.count(specification);
    }

    /**
     * Function to convert {@link PropertyImageCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PropertyImage> createSpecification(PropertyImageCriteria criteria) {
        Specification<PropertyImage> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PropertyImage_.id));
            }
            if (criteria.getImageUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getImageUrl(), PropertyImage_.imageUrl));
            }
            if (criteria.getIsMain() != null) {
                specification = specification.and(buildSpecification(criteria.getIsMain(), PropertyImage_.isMain));
            }
            if (criteria.getCaption() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCaption(), PropertyImage_.caption));
            }
            if (criteria.getPropertyId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPropertyId(), root -> root.join(PropertyImage_.property, JoinType.LEFT).get(Property_.id)
                    )
                );
            }
        }
        return specification;
    }
}
