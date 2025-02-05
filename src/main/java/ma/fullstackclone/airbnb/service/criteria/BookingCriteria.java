package ma.fullstackclone.airbnb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import ma.fullstackclone.airbnb.domain.enumeration.BookingStatus;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ma.fullstackclone.airbnb.domain.Booking} entity. This class is used
 * in {@link ma.fullstackclone.airbnb.web.rest.BookingResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /bookings?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookingCriteria implements Serializable, Criteria {

    /**
     * Class for filtering BookingStatus
     */
    public static class BookingStatusFilter extends Filter<BookingStatus> {

        public BookingStatusFilter() {}

        public BookingStatusFilter(BookingStatusFilter filter) {
            super(filter);
        }

        @Override
        public BookingStatusFilter copy() {
            return new BookingStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter checkInDate;

    private InstantFilter checkOutDate;

    private BigDecimalFilter totalPrice;

    private InstantFilter bookingDate;

    private BookingStatusFilter status;

    private LongFilter guestId;

    private LongFilter propertyId;

    private Boolean distinct;

    public BookingCriteria() {}

    public BookingCriteria(BookingCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.checkInDate = other.optionalCheckInDate().map(InstantFilter::copy).orElse(null);
        this.checkOutDate = other.optionalCheckOutDate().map(InstantFilter::copy).orElse(null);
        this.totalPrice = other.optionalTotalPrice().map(BigDecimalFilter::copy).orElse(null);
        this.bookingDate = other.optionalBookingDate().map(InstantFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(BookingStatusFilter::copy).orElse(null);
        this.guestId = other.optionalGuestId().map(LongFilter::copy).orElse(null);
        this.propertyId = other.optionalPropertyId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public BookingCriteria copy() {
        return new BookingCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getCheckInDate() {
        return checkInDate;
    }

    public Optional<InstantFilter> optionalCheckInDate() {
        return Optional.ofNullable(checkInDate);
    }

    public InstantFilter checkInDate() {
        if (checkInDate == null) {
            setCheckInDate(new InstantFilter());
        }
        return checkInDate;
    }

    public void setCheckInDate(InstantFilter checkInDate) {
        this.checkInDate = checkInDate;
    }

    public InstantFilter getCheckOutDate() {
        return checkOutDate;
    }

    public Optional<InstantFilter> optionalCheckOutDate() {
        return Optional.ofNullable(checkOutDate);
    }

    public InstantFilter checkOutDate() {
        if (checkOutDate == null) {
            setCheckOutDate(new InstantFilter());
        }
        return checkOutDate;
    }

    public void setCheckOutDate(InstantFilter checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BigDecimalFilter getTotalPrice() {
        return totalPrice;
    }

    public Optional<BigDecimalFilter> optionalTotalPrice() {
        return Optional.ofNullable(totalPrice);
    }

    public BigDecimalFilter totalPrice() {
        if (totalPrice == null) {
            setTotalPrice(new BigDecimalFilter());
        }
        return totalPrice;
    }

    public void setTotalPrice(BigDecimalFilter totalPrice) {
        this.totalPrice = totalPrice;
    }

    public InstantFilter getBookingDate() {
        return bookingDate;
    }

    public Optional<InstantFilter> optionalBookingDate() {
        return Optional.ofNullable(bookingDate);
    }

    public InstantFilter bookingDate() {
        if (bookingDate == null) {
            setBookingDate(new InstantFilter());
        }
        return bookingDate;
    }

    public void setBookingDate(InstantFilter bookingDate) {
        this.bookingDate = bookingDate;
    }

    public BookingStatusFilter getStatus() {
        return status;
    }

    public Optional<BookingStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public BookingStatusFilter status() {
        if (status == null) {
            setStatus(new BookingStatusFilter());
        }
        return status;
    }

    public void setStatus(BookingStatusFilter status) {
        this.status = status;
    }

    public LongFilter getGuestId() {
        return guestId;
    }

    public Optional<LongFilter> optionalGuestId() {
        return Optional.ofNullable(guestId);
    }

    public LongFilter guestId() {
        if (guestId == null) {
            setGuestId(new LongFilter());
        }
        return guestId;
    }

    public void setGuestId(LongFilter guestId) {
        this.guestId = guestId;
    }

    public LongFilter getPropertyId() {
        return propertyId;
    }

    public Optional<LongFilter> optionalPropertyId() {
        return Optional.ofNullable(propertyId);
    }

    public LongFilter propertyId() {
        if (propertyId == null) {
            setPropertyId(new LongFilter());
        }
        return propertyId;
    }

    public void setPropertyId(LongFilter propertyId) {
        this.propertyId = propertyId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BookingCriteria that = (BookingCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(checkInDate, that.checkInDate) &&
            Objects.equals(checkOutDate, that.checkOutDate) &&
            Objects.equals(totalPrice, that.totalPrice) &&
            Objects.equals(bookingDate, that.bookingDate) &&
            Objects.equals(status, that.status) &&
            Objects.equals(guestId, that.guestId) &&
            Objects.equals(propertyId, that.propertyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, checkInDate, checkOutDate, totalPrice, bookingDate, status, guestId, propertyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookingCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCheckInDate().map(f -> "checkInDate=" + f + ", ").orElse("") +
            optionalCheckOutDate().map(f -> "checkOutDate=" + f + ", ").orElse("") +
            optionalTotalPrice().map(f -> "totalPrice=" + f + ", ").orElse("") +
            optionalBookingDate().map(f -> "bookingDate=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalGuestId().map(f -> "guestId=" + f + ", ").orElse("") +
            optionalPropertyId().map(f -> "propertyId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
