package ma.fullstackclone.airbnb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Property.
 */
@Entity
@Table(name = "property")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "property")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Property implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "title", length = 100, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String title;

    @Lob
    @Column(name = "description", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price_per_night", precision = 21, scale = 2, nullable = false)
    private BigDecimal pricePerNight;

    @NotNull
    @Size(max = 255)
    @Column(name = "address", length = 255, nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    @Column(name = "latitude", precision = 21, scale = 2)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 21, scale = 2)
    private BigDecimal longitude;

    @NotNull
    @Min(value = 1)
    @Column(name = "number_of_rooms", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer numberOfRooms;

    @Min(value = 1)
    @Column(name = "number_of_bathrooms")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer numberOfBathrooms;

    @Min(value = 1)
    @Column(name = "max_guests")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer maxGuests;

    @Min(value = 0)
    @Column(name = "property_size")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer propertySize;

    @Column(name = "availability_start")
    private ZonedDateTime availabilityStart;

    @Column(name = "availability_end")
    private ZonedDateTime availabilityEnd;

    @NotNull
    @Column(name = "instant_book", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean instantBook;

    @Min(value = 1)
    @Column(name = "minimum_stay")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer minimumStay;

    @NotNull
    @Column(name = "cancellation_policy", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String cancellationPolicy;

    @Lob
    @Column(name = "house_rules")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String houseRules;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Boolean)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY)
    private User host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "country" }, allowSetters = true)
    private City city;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_property__amenities",
        joinColumns = @JoinColumn(name = "property_id"),
        inverseJoinColumns = @JoinColumn(name = "amenities_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "properties" }, allowSetters = true)
    private Set<Amenity> amenities = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_property__categories",
        joinColumns = @JoinColumn(name = "property_id"),
        inverseJoinColumns = @JoinColumn(name = "categories_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "properties" }, allowSetters = true)
    private Set<PropertyCategory> categories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Property id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Property title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Property description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPricePerNight() {
        return this.pricePerNight;
    }

    public Property pricePerNight(BigDecimal pricePerNight) {
        this.setPricePerNight(pricePerNight);
        return this;
    }

    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public String getAddress() {
        return this.address;
    }

    public Property address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return this.latitude;
    }

    public Property latitude(BigDecimal latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return this.longitude;
    }

    public Property longitude(BigDecimal longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Integer getNumberOfRooms() {
        return this.numberOfRooms;
    }

    public Property numberOfRooms(Integer numberOfRooms) {
        this.setNumberOfRooms(numberOfRooms);
        return this;
    }

    public void setNumberOfRooms(Integer numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public Integer getNumberOfBathrooms() {
        return this.numberOfBathrooms;
    }

    public Property numberOfBathrooms(Integer numberOfBathrooms) {
        this.setNumberOfBathrooms(numberOfBathrooms);
        return this;
    }

    public void setNumberOfBathrooms(Integer numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public Integer getMaxGuests() {
        return this.maxGuests;
    }

    public Property maxGuests(Integer maxGuests) {
        this.setMaxGuests(maxGuests);
        return this;
    }

    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }

    public Integer getPropertySize() {
        return this.propertySize;
    }

    public Property propertySize(Integer propertySize) {
        this.setPropertySize(propertySize);
        return this;
    }

    public void setPropertySize(Integer propertySize) {
        this.propertySize = propertySize;
    }

    public ZonedDateTime getAvailabilityStart() {
        return this.availabilityStart;
    }

    public Property availabilityStart(ZonedDateTime availabilityStart) {
        this.setAvailabilityStart(availabilityStart);
        return this;
    }

    public void setAvailabilityStart(ZonedDateTime availabilityStart) {
        this.availabilityStart = availabilityStart;
    }

    public ZonedDateTime getAvailabilityEnd() {
        return this.availabilityEnd;
    }

    public Property availabilityEnd(ZonedDateTime availabilityEnd) {
        this.setAvailabilityEnd(availabilityEnd);
        return this;
    }

    public void setAvailabilityEnd(ZonedDateTime availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }

    public Boolean getInstantBook() {
        return this.instantBook;
    }

    public Property instantBook(Boolean instantBook) {
        this.setInstantBook(instantBook);
        return this;
    }

    public void setInstantBook(Boolean instantBook) {
        this.instantBook = instantBook;
    }

    public Integer getMinimumStay() {
        return this.minimumStay;
    }

    public Property minimumStay(Integer minimumStay) {
        this.setMinimumStay(minimumStay);
        return this;
    }

    public void setMinimumStay(Integer minimumStay) {
        this.minimumStay = minimumStay;
    }

    public String getCancellationPolicy() {
        return this.cancellationPolicy;
    }

    public Property cancellationPolicy(String cancellationPolicy) {
        this.setCancellationPolicy(cancellationPolicy);
        return this;
    }

    public void setCancellationPolicy(String cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getHouseRules() {
        return this.houseRules;
    }

    public Property houseRules(String houseRules) {
        this.setHouseRules(houseRules);
        return this;
    }

    public void setHouseRules(String houseRules) {
        this.houseRules = houseRules;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }

    public Property isActive(Boolean isActive) {
        this.setIsActive(isActive);
        return this;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public User getHost() {
        return this.host;
    }

    public void setHost(User user) {
        this.host = user;
    }

    public Property host(User user) {
        this.setHost(user);
        return this;
    }

    public City getCity() {
        return this.city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Property city(City city) {
        this.setCity(city);
        return this;
    }

    public Set<Amenity> getAmenities() {
        return this.amenities;
    }

    public void setAmenities(Set<Amenity> amenities) {
        this.amenities = amenities;
    }

    public Property amenities(Set<Amenity> amenities) {
        this.setAmenities(amenities);
        return this;
    }

    public Property addAmenities(Amenity amenity) {
        this.amenities.add(amenity);
        return this;
    }

    public Property removeAmenities(Amenity amenity) {
        this.amenities.remove(amenity);
        return this;
    }

    public Set<PropertyCategory> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<PropertyCategory> propertyCategories) {
        this.categories = propertyCategories;
    }

    public Property categories(Set<PropertyCategory> propertyCategories) {
        this.setCategories(propertyCategories);
        return this;
    }

    public Property addCategories(PropertyCategory propertyCategory) {
        this.categories.add(propertyCategory);
        return this;
    }

    public Property removeCategories(PropertyCategory propertyCategory) {
        this.categories.remove(propertyCategory);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Property)) {
            return false;
        }
        return getId() != null && getId().equals(((Property) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Property{" +
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
            "}";
    }
}
