package ma.fullstackclone.airbnb.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link ma.fullstackclone.airbnb.domain.PropertyImage} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PropertyImageDTO implements Serializable {

    private Long id;

    @NotNull
    private String imageUrl;

    @NotNull
    private Boolean isMain;

    @Size(max = 100)
    private String caption;

    private PropertyDTO property;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsMain() {
        return isMain;
    }

    public void setIsMain(Boolean isMain) {
        this.isMain = isMain;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public PropertyDTO getProperty() {
        return property;
    }

    public void setProperty(PropertyDTO property) {
        this.property = property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyImageDTO)) {
            return false;
        }

        PropertyImageDTO propertyImageDTO = (PropertyImageDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, propertyImageDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertyImageDTO{" +
            "id=" + getId() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", isMain='" + getIsMain() + "'" +
            ", caption='" + getCaption() + "'" +
            ", property=" + getProperty() +
            "}";
    }
}
