package ma.fullstackclone.airbnb.service;

import jakarta.persistence.criteria.JoinType;
import ma.fullstackclone.airbnb.domain.*; // for static metamodels
import ma.fullstackclone.airbnb.domain.Booking;
import ma.fullstackclone.airbnb.repository.BookingRepository;
import ma.fullstackclone.airbnb.repository.search.BookingSearchRepository;
import ma.fullstackclone.airbnb.service.criteria.BookingCriteria;
import ma.fullstackclone.airbnb.service.dto.BookingDTO;
import ma.fullstackclone.airbnb.service.mapper.BookingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Booking} entities in the database.
 * The main input is a {@link BookingCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link BookingDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class BookingQueryService extends QueryService<Booking> {

    private static final Logger LOG = LoggerFactory.getLogger(BookingQueryService.class);

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final BookingSearchRepository bookingSearchRepository;

    public BookingQueryService(
        BookingRepository bookingRepository,
        BookingMapper bookingMapper,
        BookingSearchRepository bookingSearchRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.bookingSearchRepository = bookingSearchRepository;
    }

    /**
     * Return a {@link Page} of {@link BookingDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<BookingDTO> findByCriteria(BookingCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Booking> specification = createSpecification(criteria);
        return bookingRepository.findAll(specification, page).map(bookingMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(BookingCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Booking> specification = createSpecification(criteria);
        return bookingRepository.count(specification);
    }

    /**
     * Function to convert {@link BookingCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Booking> createSpecification(BookingCriteria criteria) {
        Specification<Booking> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Booking_.id));
            }
            if (criteria.getCheckInDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCheckInDate(), Booking_.checkInDate));
            }
            if (criteria.getCheckOutDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCheckOutDate(), Booking_.checkOutDate));
            }
            if (criteria.getTotalPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTotalPrice(), Booking_.totalPrice));
            }
            if (criteria.getBookingDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getBookingDate(), Booking_.bookingDate));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), Booking_.status));
            }
            if (criteria.getGuestId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getGuestId(), root -> root.join(Booking_.guest, JoinType.LEFT).get(User_.id))
                );
            }
            if (criteria.getPropertyId() != null) {
                specification = specification.and(
                    buildSpecification(criteria.getPropertyId(), root -> root.join(Booking_.property, JoinType.LEFT).get(Property_.id))
                );
            }
        }
        return specification;
    }
}
