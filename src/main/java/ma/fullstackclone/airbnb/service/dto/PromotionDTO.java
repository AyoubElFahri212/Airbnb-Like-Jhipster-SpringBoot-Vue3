package ma.fullstackclone.airbnb.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import ma.fullstackclone.airbnb.domain.enumeration.DiscountType;

/**
 * A DTO for the {@link ma.fullstackclone.airbnb.domain.Promotion} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PromotionDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal discountValue;

    @NotNull
    private Instant validFrom;

    @NotNull
    private Instant validUntil;

    private Integer maxUses;

    @NotNull
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }

    public Integer getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(Integer maxUses) {
        this.maxUses = maxUses;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PromotionDTO)) {
            return false;
        }

        PromotionDTO promotionDTO = (PromotionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, promotionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PromotionDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", discountType='" + getDiscountType() + "'" +
            ", discountValue=" + getDiscountValue() +
            ", validFrom='" + getValidFrom() + "'" +
            ", validUntil='" + getValidUntil() + "'" +
            ", maxUses=" + getMaxUses() +
            ", isActive='" + getIsActive() + "'" +
            "}";
    }
}
