package ma.fullstackclone.airbnb.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link ma.fullstackclone.airbnb.domain.Property} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PropertyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String title;

    @Lob
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal pricePerNight;

    @NotNull
    @Size(max = 255)
    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    @NotNull
    @Min(value = 1)
    private Integer numberOfRooms;

    @Min(value = 1)
    private Integer numberOfBathrooms;

    @Min(value = 1)
    private Integer maxGuests;

    @Min(value = 0)
    private Integer propertySize;

    private ZonedDateTime availabilityStart;

    private ZonedDateTime availabilityEnd;

    @NotNull
    private Boolean instantBook;

    @Min(value = 1)
    private Integer minimumStay;

    @NotNull
    private String cancellationPolicy;

    @Lob
    private String houseRules;

    @NotNull
    private Boolean isActive;

    private UserDTO host;

    private CityDTO city;

    private Set<AmenityDTO> amenities = new HashSet<>();

    private Set<PropertyCategoryDTO> categories = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public void setNumberOfBathrooms(Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public Integer getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Integer getPropertySize() {
        return propertySize;
    }

    public void setPropertySize(Integer propertySize) {
        this.propertySize = propertySize;
    }

    public ZonedDateTime getAvailabilityStart() {
        return availabilityStart;
    }

    public void setAvailabilityStart(ZonedDateTime availabilityStart) {
        this.availabilityStart = availabilityStart;
    }

    public ZonedDateTime getAvailabilityEnd() {
        return availabilityEnd;
    }

    public void setAvailabilityEnd(ZonedDateTime availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }

    public Boolean getInstantBook() {
        return instantBook;
    }

    public void setInstantBook(Boolean instantBook) {
        this.instantBook = instantBook;
    }

    public Integer getMinimumStay() {
        return minimumStay;
    }

    public void setMinimumStay(Integer minimumStay) {
        this.minimumStay = minimumStay;
    }

    public String getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getHouseRules() {
        return houseRules;
    }

    public void setHouseRules(String houseRules) {
        this.houseRules = houseRules;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public UserDTO getHost() {
        return host;
    }

    public void setHost(UserDTO host) {
        this.host = host;
    }

    public CityDTO getCity() {
        return city;
    }

    public void setCity(CityDTO city) {
        this.city = city;
    }

    public Set<AmenityDTO> getAmenities() {
        return amenities;
    }

    public void setAmenities(Set<AmenityDTO> amenities) {
        this.amenities = amenities;
    }

    public Set<PropertyCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(Set<PropertyCategoryDTO> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyDTO)) {
            return false;
        }

        PropertyDTO propertyDTO = (PropertyDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, propertyDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertyDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", pricePerNight=" + getPricePerNight() +
            ", address='" + getAddress() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", numberOfRooms=" + getNumberOfRooms() +
            ", numberOfBathrooms=" + getNumberOfBathrooms() +
            ", maxGuests=" + getMaxGuests() +
            ", propertySize=" + getPropertySize() +
            ", availabilityStart='" + getAvailabilityStart() + "'" +
            ", availabilityEnd='" + getAvailabilityEnd() + "'" +
            ", instantBook='" + getInstantBook() + "'" +
            ", minimumStay=" + getMinimumStay() +
            ", cancellationPolicy='" + getCancellationPolicy() + "'" +
            ", houseRules='" + getHouseRules() + "'" +
            ", isActive='" + getIsActive() + "'" +
            ", host=" + getHost() +
            ", city=" + getCity() +
            ", amenities=" + getAmenities() +
            ", categories=" + getCategories() +
            "}";
    }
}
