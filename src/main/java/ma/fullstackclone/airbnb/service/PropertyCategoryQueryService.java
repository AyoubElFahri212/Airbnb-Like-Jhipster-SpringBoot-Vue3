package ma.fullstackclone.airbnb.service;

import jakarta.persistence.criteria.JoinType;
import ma.fullstackclone.airbnb.domain.*; // for static metamodels
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.repository.PropertyCategoryRepository;
import ma.fullstackclone.airbnb.repository.search.PropertyCategorySearchRepository;
import ma.fullstackclone.airbnb.service.criteria.PropertyCategoryCriteria;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyCategoryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link PropertyCategory} entities in the database.
 * The main input is a {@link PropertyCategoryCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PropertyCategoryDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PropertyCategoryQueryService extends QueryService<PropertyCategory> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyCategoryQueryService.class);

    private final PropertyCategoryRepository propertyCategoryRepository;

    private final PropertyCategoryMapper propertyCategoryMapper;

    private final PropertyCategorySearchRepository propertyCategorySearchRepository;

    public PropertyCategoryQueryService(
        PropertyCategoryRepository propertyCategoryRepository,
        PropertyCategoryMapper propertyCategoryMapper,
        PropertyCategorySearchRepository propertyCategorySearchRepository
    ) {
        this.propertyCategoryRepository = propertyCategoryRepository;
        this.propertyCategoryMapper = propertyCategoryMapper;
        this.propertyCategorySearchRepository = propertyCategorySearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PropertyCategoryDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PropertyCategoryDTO> findByCriteria(PropertyCategoryCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PropertyCategory> specification = createSpecification(criteria);
        return propertyCategoryRepository.findAll(specification, page).map(propertyCategoryMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PropertyCategoryCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<PropertyCategory> specification = createSpecification(criteria);
        return propertyCategoryRepository.count(specification);
    }

    /**
     * Function to convert {@link PropertyCategoryCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PropertyCategory> createSpecification(PropertyCategoryCriteria criteria) {
        Specification<PropertyCategory> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PropertyCategory_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PropertyCategory_.name));
            }
            if (criteria.getPropertyId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPropertyId(), root ->
                        root.join(PropertyCategory_.properties, JoinType.LEFT).get(Property_.id)
                    )
                );
            }
        }
        return specification;
    }
}
