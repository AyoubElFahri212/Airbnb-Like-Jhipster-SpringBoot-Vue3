package ma.fullstackclone.airbnb.service;

import jakarta.persistence.criteria.JoinType;
import ma.fullstackclone.airbnb.domain.*; // for static metamodels
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.repository.PropertyRepository;
import ma.fullstackclone.airbnb.repository.search.PropertySearchRepository;
import ma.fullstackclone.airbnb.service.criteria.PropertyCriteria;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.mapper.PropertyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Property} entities in the database.
 * The main input is a {@link PropertyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link PropertyDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PropertyQueryService extends QueryService<Property> {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyQueryService.class);

    private final PropertyRepository propertyRepository;

    private final PropertyMapper propertyMapper;

    private final PropertySearchRepository propertySearchRepository;

    public PropertyQueryService(
        PropertyRepository propertyRepository,
        PropertyMapper propertyMapper,
        PropertySearchRepository propertySearchRepository
    ) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.propertySearchRepository = propertySearchRepository;
    }

    /**
     * Return a {@link Page} of {@link PropertyDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PropertyDTO> findByCriteria(PropertyCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Property> specification = createSpecification(criteria);
        return propertyRepository.fetchBagRelationships(propertyRepository.findAll(specification, page)).map(propertyMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PropertyCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Property> specification = createSpecification(criteria);
        return propertyRepository.count(specification);
    }

    /**
     * Function to convert {@link PropertyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Property> createSpecification(PropertyCriteria criteria) {
        Specification<Property> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Property_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Property_.title));
            }
            if (criteria.getPricePerNight() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPricePerNight(), Property_.pricePerNight));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Property_.address));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), Property_.latitude));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), Property_.longitude));
            }
            if (criteria.getNumberOfRooms() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumberOfRooms(), Property_.numberOfRooms));
            }
            if (criteria.getNumberOfBathrooms() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumberOfBathrooms(), Property_.numberOfBathrooms));
            }
            if (criteria.getMaxGuests() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMaxGuests(), Property_.maxGuests));
            }
            if (criteria.getPropertySize() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPropertySize(), Property_.propertySize));
            }
            if (criteria.getAvailabilityStart() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAvailabilityStart(), Property_.availabilityStart));
            }
            if (criteria.getAvailabilityEnd() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getAvailabilityEnd(), Property_.availabilityEnd));
            }
            if (criteria.getInstantBook() != null) {
                specification = specification.and(buildSpecification(criteria.getInstantBook(), Property_.instantBook));
            }
            if (criteria.getMinimumStay() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getMinimumStay(), Property_.minimumStay));
            }
            if (criteria.getCancellationPolicy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCancellationPolicy(), Property_.cancellationPolicy));
            }
            if (criteria.getIsActive() != null) {
                specification = specification.and(buildSpecification(criteria.getIsActive(), Property_.isActive));
            }
            if (criteria.getHostId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getHostId(), root -> root.join(Property_.host, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getCityId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCityId(), root -> root.join(Property_.city, JoinType.LEFT).get(City_.id))
                );
            }
            if (criteria.getAmenitiesId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getAmenitiesId(), root -> root.join(Property_.amenities, JoinType.LEFT).get(Amenity_.id))
                );
            }
            if (criteria.getCategoriesId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getCategoriesId(), root ->
                        root.join(Property_.categories, JoinType.LEFT).get(PropertyCategory_.id)
                    )
                );
            }
        }
        return specification;
    }
}
