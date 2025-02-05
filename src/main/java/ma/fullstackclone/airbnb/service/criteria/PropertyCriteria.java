package ma.fullstackclone.airbnb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ma.fullstackclone.airbnb.domain.Property} entity. This class is used
 * in {@link ma.fullstackclone.airbnb.web.rest.PropertyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /properties?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PropertyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private BigDecimalFilter pricePerNight;

    private StringFilter address;

    private BigDecimalFilter latitude;

    private BigDecimalFilter longitude;

    private IntegerFilter numberOfRooms;

    private IntegerFilter numberOfBathrooms;

    private IntegerFilter maxGuests;

    private IntegerFilter propertySize;

    private ZonedDateTimeFilter availabilityStart;

    private ZonedDateTimeFilter availabilityEnd;

    private BooleanFilter instantBook;

    private IntegerFilter minimumStay;

    private StringFilter cancellationPolicy;

    private BooleanFilter isActive;

    private LongFilter hostId;

    private LongFilter cityId;

    private LongFilter amenitiesId;

    private LongFilter categoriesId;

    private Boolean distinct;

    public PropertyCriteria() {}

    public PropertyCriteria(PropertyCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.pricePerNight = other.optionalPricePerNight().map(BigDecimalFilter::copy).orElse(null);
        this.address = other.optionalAddress().map(StringFilter::copy).orElse(null);
        this.latitude = other.optionalLatitude().map(BigDecimalFilter::copy).orElse(null);
        this.longitude = other.optionalLongitude().map(BigDecimalFilter::copy).orElse(null);
        this.numberOfRooms = other.optionalNumberOfRooms().map(IntegerFilter::copy).orElse(null);
        this.numberOfBathrooms = other.optionalNumberOfBathrooms().map(IntegerFilter::copy).orElse(null);
        this.maxGuests = other.optionalMaxGuests().map(IntegerFilter::copy).orElse(null);
        this.propertySize = other.optionalPropertySize().map(IntegerFilter::copy).orElse(null);
        this.availabilityStart = other.optionalAvailabilityStart().map(ZonedDateTimeFilter::copy).orElse(null);
        this.availabilityEnd = other.optionalAvailabilityEnd().map(ZonedDateTimeFilter::copy).orElse(null);
        this.instantBook = other.optionalInstantBook().map(BooleanFilter::copy).orElse(null);
        this.minimumStay = other.optionalMinimumStay().map(IntegerFilter::copy).orElse(null);
        this.cancellationPolicy = other.optionalCancellationPolicy().map(StringFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.hostId = other.optionalHostId().map(LongFilter::copy).orElse(null);
        this.cityId = other.optionalCityId().map(LongFilter::copy).orElse(null);
        this.amenitiesId = other.optionalAmenitiesId().map(LongFilter::copy).orElse(null);
        this.categoriesId = other.optionalCategoriesId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PropertyCriteria copy() {
        return new PropertyCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public BigDecimalFilter getPricePerNight() {
        return pricePerNight;
    }

    public Optional<BigDecimalFilter> optionalPricePerNight() {
        return Optional.ofNullable(pricePerNight);
    }

    public BigDecimalFilter pricePerNight() {
        if (pricePerNight == null) {
            setPricePerNight(new BigDecimalFilter());
        }
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimalFilter pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public StringFilter getAddress() {
        return address;
    }

    public Optional<StringFilter> optionalAddress() {
        return Optional.ofNullable(address);
    }

    public StringFilter address() {
        if (address == null) {
            setAddress(new StringFilter());
        }
        return address;
    }

    public void setAddress(StringFilter address) {
        this.address = address;
    }

    public BigDecimalFilter getLatitude() {
        return latitude;
    }

    public Optional<BigDecimalFilter> optionalLatitude() {
        return Optional.ofNullable(latitude);
    }

    public BigDecimalFilter latitude() {
        if (latitude == null) {
            setLatitude(new BigDecimalFilter());
        }
        return latitude;
    }

    public void setLatitude(BigDecimalFilter latitude) {
        this.latitude = latitude;
    }

    public BigDecimalFilter getLongitude() {
        return longitude;
    }

    public Optional<BigDecimalFilter> optionalLongitude() {
        return Optional.ofNullable(longitude);
    }

    public BigDecimalFilter longitude() {
        if (longitude == null) {
            setLongitude(new BigDecimalFilter());
        }
        return longitude;
    }

    public void setLongitude(BigDecimalFilter longitude) {
        this.longitude = longitude;
    }

    public IntegerFilter getNumberOfRooms() {
        return numberOfRooms;
    }

    public Optional<IntegerFilter> optionalNumberOfRooms() {
        return Optional.ofNullable(numberOfRooms);
    }

    public IntegerFilter numberOfRooms() {
        if (numberOfRooms == null) {
            setNumberOfRooms(new IntegerFilter());
        }
        return numberOfRooms;
    }

    public void setNumberOfRooms(IntegerFilter numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public IntegerFilter getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public Optional<IntegerFilter> optionalNumberOfBathrooms() {
        return Optional.ofNullable(numberOfBathrooms);
    }

    public IntegerFilter numberOfBathrooms() {
        if (numberOfBathrooms == null) {
            setNumberOfBathrooms(new IntegerFilter());
        }
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(IntegerFilter numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public IntegerFilter getMaxGuests() {
        return maxGuests;
    }

    public Optional<IntegerFilter> optionalMaxGuests() {
        return Optional.ofNullable(maxGuests);
    }

    public IntegerFilter maxGuests() {
        if (maxGuests == null) {
            setMaxGuests(new IntegerFilter());
        }
        return maxGuests;
    }

    public void setMaxGuests(IntegerFilter maxGuests) {
        this.maxGuests = maxGuests;
    }

    public IntegerFilter getPropertySize() {
        return propertySize;
    }

    public Optional<IntegerFilter> optionalPropertySize() {
        return Optional.ofNullable(propertySize);
    }

    public IntegerFilter propertySize() {
        if (propertySize == null) {
            setPropertySize(new IntegerFilter());
        }
        return propertySize;
    }

    public void setPropertySize(IntegerFilter propertySize) {
        this.propertySize = propertySize;
    }

    public ZonedDateTimeFilter getAvailabilityStart() {
        return availabilityStart;
    }

    public Optional<ZonedDateTimeFilter> optionalAvailabilityStart() {
        return Optional.ofNullable(availabilityStart);
    }

    public ZonedDateTimeFilter availabilityStart() {
        if (availabilityStart == null) {
            setAvailabilityStart(new ZonedDateTimeFilter());
        }
        return availabilityStart;
    }

    public void setAvailabilityStart(ZonedDateTimeFilter availabilityStart) {
        this.availabilityStart = availabilityStart;
    }

    public ZonedDateTimeFilter getAvailabilityEnd() {
        return availabilityEnd;
    }

    public Optional<ZonedDateTimeFilter> optionalAvailabilityEnd() {
        return Optional.ofNullable(availabilityEnd);
    }

    public ZonedDateTimeFilter availabilityEnd() {
        if (availabilityEnd == null) {
            setAvailabilityEnd(new ZonedDateTimeFilter());
        }
        return availabilityEnd;
    }

    public void setAvailabilityEnd(ZonedDateTimeFilter availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }

    public BooleanFilter getInstantBook() {
        return instantBook;
    }

    public Optional<BooleanFilter> optionalInstantBook() {
        return Optional.ofNullable(instantBook);
    }

    public BooleanFilter instantBook() {
        if (instantBook == null) {
            setInstantBook(new BooleanFilter());
        }
        return instantBook;
    }

    public void setInstantBook(BooleanFilter instantBook) {
        this.instantBook = instantBook;
    }

    public IntegerFilter getMinimumStay() {
        return minimumStay;
    }

    public Optional<IntegerFilter> optionalMinimumStay() {
        return Optional.ofNullable(minimumStay);
    }

    public IntegerFilter minimumStay() {
        if (minimumStay == null) {
            setMinimumStay(new IntegerFilter());
        }
        return minimumStay;
    }

    public void setMinimumStay(IntegerFilter minimumStay) {
        this.minimumStay = minimumStay;
    }

    public StringFilter getCancellationPolicy() {
        return cancellationPolicy;
    }

    public Optional<StringFilter> optionalCancellationPolicy() {
        return Optional.ofNullable(cancellationPolicy);
    }

    public StringFilter cancellationPolicy() {
        if (cancellationPolicy == null) {
            setCancellationPolicy(new StringFilter());
        }
        return cancellationPolicy;
    }

    public void setCancellationPolicy(StringFilter cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public BooleanFilter getIsActive() {
        return isActive;
    }

    public Optional<BooleanFilter> optionalIsActive() {
        return Optional.ofNullable(isActive);
    }

    public BooleanFilter isActive() {
        if (isActive == null) {
            setIsActive(new BooleanFilter());
        }
        return isActive;
    }

    public void setIsActive(BooleanFilter isActive) {
        this.isActive = isActive;
    }

    public LongFilter getHostId() {
        return hostId;
    }

    public Optional<LongFilter> optionalHostId() {
        return Optional.ofNullable(hostId);
    }

    public LongFilter hostId() {
        if (hostId == null) {
            setHostId(new LongFilter());
        }
        return hostId;
    }

    public void setHostId(LongFilter hostId) {
        this.hostId = hostId;
    }

    public LongFilter getCityId() {
        return cityId;
    }

    public Optional<LongFilter> optionalCityId() {
        return Optional.ofNullable(cityId);
    }

    public LongFilter cityId() {
        if (cityId == null) {
            setCityId(new LongFilter());
        }
        return cityId;
    }

    public void setCityId(LongFilter cityId) {
        this.cityId = cityId;
    }

    public LongFilter getAmenitiesId() {
        return amenitiesId;
    }

    public Optional<LongFilter> optionalAmenitiesId() {
        return Optional.ofNullable(amenitiesId);
    }

    public LongFilter amenitiesId() {
        if (amenitiesId == null) {
            setAmenitiesId(new LongFilter());
        }
        return amenitiesId;
    }

    public void setAmenitiesId(LongFilter amenitiesId) {
        this.amenitiesId = amenitiesId;
    }

    public LongFilter getCategoriesId() {
        return categoriesId;
    }

    public Optional<LongFilter> optionalCategoriesId() {
        return Optional.ofNullable(categoriesId);
    }

    public LongFilter categoriesId() {
        if (categoriesId == null) {
            setCategoriesId(new LongFilter());
        }
        return categoriesId;
    }

    public void setCategoriesId(LongFilter categoriesId) {
        this.categoriesId = categoriesId;
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
        final PropertyCriteria that = (PropertyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(pricePerNight, that.pricePerNight) &&
            Objects.equals(address, that.address) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(numberOfRooms, that.numberOfRooms) &&
            Objects.equals(numberOfBathrooms, that.numberOfBathrooms) &&
            Objects.equals(maxGuests, that.maxGuests) &&
            Objects.equals(propertySize, that.propertySize) &&
            Objects.equals(availabilityStart, that.availabilityStart) &&
            Objects.equals(availabilityEnd, that.availabilityEnd) &&
            Objects.equals(instantBook, that.instantBook) &&
            Objects.equals(minimumStay, that.minimumStay) &&
            Objects.equals(cancellationPolicy, that.cancellationPolicy) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(hostId, that.hostId) &&
            Objects.equals(cityId, that.cityId) &&
            Objects.equals(amenitiesId, that.amenitiesId) &&
            Objects.equals(categoriesId, that.categoriesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            pricePerNight,
            address,
            latitude,
            longitude,
            numberOfRooms,
            numberOfBathrooms,
            maxGuests,
            propertySize,
            availabilityStart,
            availabilityEnd,
            instantBook,
            minimumStay,
            cancellationPolicy,
            isActive,
            hostId,
            cityId,
            amenitiesId,
            categoriesId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertyCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalPricePerNight().map(f -> "pricePerNight=" + f + ", ").orElse("") +
            optionalAddress().map(f -> "address=" + f + ", ").orElse("") +
            optionalLatitude().map(f -> "latitude=" + f + ", ").orElse("") +
            optionalLongitude().map(f -> "longitude=" + f + ", ").orElse("") +
            optionalNumberOfRooms().map(f -> "numberOfRooms=" + f + ", ").orElse("") +
            optionalNumberOfBathrooms().map(f -> "numberOfBathrooms=" + f + ", ").orElse("") +
            optionalMaxGuests().map(f -> "maxGuests=" + f + ", ").orElse("") +
            optionalPropertySize().map(f -> "propertySize=" + f + ", ").orElse("") +
            optionalAvailabilityStart().map(f -> "availabilityStart=" + f + ", ").orElse("") +
            optionalAvailabilityEnd().map(f -> "availabilityEnd=" + f + ", ").orElse("") +
            optionalInstantBook().map(f -> "instantBook=" + f + ", ").orElse("") +
            optionalMinimumStay().map(f -> "minimumStay=" + f + ", ").orElse("") +
            optionalCancellationPolicy().map(f -> "cancellationPolicy=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalHostId().map(f -> "hostId=" + f + ", ").orElse("") +
            optionalCityId().map(f -> "cityId=" + f + ", ").orElse("") +
            optionalAmenitiesId().map(f -> "amenitiesId=" + f + ", ").orElse("") +
            optionalCategoriesId().map(f -> "categoriesId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
