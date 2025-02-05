package ma.fullstackclone.airbnb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ma.fullstackclone.airbnb.domain.PropertyImage} entity. This class is used
 * in {@link ma.fullstackclone.airbnb.web.rest.PropertyImageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /property-images?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PropertyImageCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter imageUrl;

    private BooleanFilter isMain;

    private StringFilter caption;

    private LongFilter propertyId;

    private Boolean distinct;

    public PropertyImageCriteria() {}

    public PropertyImageCriteria(PropertyImageCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.isMain = other.optionalIsMain().map(BooleanFilter::copy).orElse(null);
        this.caption = other.optionalCaption().map(StringFilter::copy).orElse(null);
        this.propertyId = other.optionalPropertyId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PropertyImageCriteria copy() {
        return new PropertyImageCriteria(this);
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

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public Optional<StringFilter> optionalImageUrl() {
        return Optional.ofNullable(imageUrl);
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            setImageUrl(new StringFilter());
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BooleanFilter getIsMain() {
        return isMain;
    }

    public Optional<BooleanFilter> optionalIsMain() {
        return Optional.ofNullable(isMain);
    }

    public BooleanFilter isMain() {
        if (isMain == null) {
            setIsMain(new BooleanFilter());
        }
        return isMain;
    }

    public void setIsMain(BooleanFilter isMain) {
        this.isMain = isMain;
    }

    public StringFilter getCaption() {
        return caption;
    }

    public Optional<StringFilter> optionalCaption() {
        return Optional.ofNullable(caption);
    }

    public StringFilter caption() {
        if (caption == null) {
            setCaption(new StringFilter());
        }
        return caption;
    }

    public void setCaption(StringFilter caption) {
        this.caption = caption;
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
        final PropertyImageCriteria that = (PropertyImageCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(isMain, that.isMain) &&
            Objects.equals(caption, that.caption) &&
            Objects.equals(propertyId, that.propertyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, imageUrl, isMain, caption, propertyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertyImageCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalIsMain().map(f -> "isMain=" + f + ", ").orElse("") +
            optionalCaption().map(f -> "caption=" + f + ", ").orElse("") +
            optionalPropertyId().map(f -> "propertyId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
