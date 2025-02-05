package ma.fullstackclone.airbnb.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import ma.fullstackclone.airbnb.domain.enumeration.DiscountType;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ma.fullstackclone.airbnb.domain.Promotion} entity. This class is used
 * in {@link ma.fullstackclone.airbnb.web.rest.PromotionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /promotions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PromotionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering DiscountType
     */
    public static class DiscountTypeFilter extends Filter<DiscountType> {

        public DiscountTypeFilter() {}

        public DiscountTypeFilter(DiscountTypeFilter filter) {
            super(filter);
        }

        @Override
        public DiscountTypeFilter copy() {
            return new DiscountTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter code;

    private DiscountTypeFilter discountType;

    private BigDecimalFilter discountValue;

    private InstantFilter validFrom;

    private InstantFilter validUntil;

    private IntegerFilter maxUses;

    private BooleanFilter isActive;

    private Boolean distinct;

    public PromotionCriteria() {}

    public PromotionCriteria(PromotionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.code = other.optionalCode().map(StringFilter::copy).orElse(null);
        this.discountType = other.optionalDiscountType().map(DiscountTypeFilter::copy).orElse(null);
        this.discountValue = other.optionalDiscountValue().map(BigDecimalFilter::copy).orElse(null);
        this.validFrom = other.optionalValidFrom().map(InstantFilter::copy).orElse(null);
        this.validUntil = other.optionalValidUntil().map(InstantFilter::copy).orElse(null);
        this.maxUses = other.optionalMaxUses().map(IntegerFilter::copy).orElse(null);
        this.isActive = other.optionalIsActive().map(BooleanFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public PromotionCriteria copy() {
        return new PromotionCriteria(this);
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

    public StringFilter getCode() {
        return code;
    }

    public Optional<StringFilter> optionalCode() {
        return Optional.ofNullable(code);
    }

    public StringFilter code() {
        if (code == null) {
            setCode(new StringFilter());
        }
        return code;
    }

    public void setCode(StringFilter code) {
        this.code = code;
    }

    public DiscountTypeFilter getDiscountType() {
        return discountType;
    }

    public Optional<DiscountTypeFilter> optionalDiscountType() {
        return Optional.ofNullable(discountType);
    }

    public DiscountTypeFilter discountType() {
        if (discountType == null) {
            setDiscountType(new DiscountTypeFilter());
        }
        return discountType;
    }

    public void setDiscountType(DiscountTypeFilter discountType) {
        this.discountType = discountType;
    }

    public BigDecimalFilter getDiscountValue() {
        return discountValue;
    }

    public Optional<BigDecimalFilter> optionalDiscountValue() {
        return Optional.ofNullable(discountValue);
    }

    public BigDecimalFilter discountValue() {
        if (discountValue == null) {
            setDiscountValue(new BigDecimalFilter());
        }
        return discountValue;
    }

    public void setDiscountValue(BigDecimalFilter discountValue) {
        this.discountValue = discountValue;
    }

    public InstantFilter getValidFrom() {
        return validFrom;
    }

    public Optional<InstantFilter> optionalValidFrom() {
        return Optional.ofNullable(validFrom);
    }

    public InstantFilter validFrom() {
        if (validFrom == null) {
            setValidFrom(new InstantFilter());
        }
        return validFrom;
    }

    public void setValidFrom(InstantFilter validFrom) {
        this.validFrom = validFrom;
    }

    public InstantFilter getValidUntil() {
        return validUntil;
    }

    public Optional<InstantFilter> optionalValidUntil() {
        return Optional.ofNullable(validUntil);
    }

    public InstantFilter validUntil() {
        if (validUntil == null) {
            setValidUntil(new InstantFilter());
        }
        return validUntil;
    }

    public void setValidUntil(InstantFilter validUntil) {
        this.validUntil = validUntil;
    }

    public IntegerFilter getMaxUses() {
        return maxUses;
    }

    public Optional<IntegerFilter> optionalMaxUses() {
        return Optional.ofNullable(maxUses);
    }

    public IntegerFilter maxUses() {
        if (maxUses == null) {
            setMaxUses(new IntegerFilter());
        }
        return maxUses;
    }

    public void setMaxUses(IntegerFilter maxUses) {
        this.maxUses = maxUses;
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
        final PromotionCriteria that = (PromotionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(discountType, that.discountType) &&
            Objects.equals(discountValue, that.discountValue) &&
            Objects.equals(validFrom, that.validFrom) &&
            Objects.equals(validUntil, that.validUntil) &&
            Objects.equals(maxUses, that.maxUses) &&
            Objects.equals(isActive, that.isActive) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code, discountType, discountValue, validFrom, validUntil, maxUses, isActive, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PromotionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalCode().map(f -> "code=" + f + ", ").orElse("") +
            optionalDiscountType().map(f -> "discountType=" + f + ", ").orElse("") +
            optionalDiscountValue().map(f -> "discountValue=" + f + ", ").orElse("") +
            optionalValidFrom().map(f -> "validFrom=" + f + ", ").orElse("") +
            optionalValidUntil().map(f -> "validUntil=" + f + ", ").orElse("") +
            optionalMaxUses().map(f -> "maxUses=" + f + ", ").orElse("") +
            optionalIsActive().map(f -> "isActive=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
