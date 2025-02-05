package ma.fullstackclone.airbnb.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link ma.fullstackclone.airbnb.domain.Amenity} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AmenityDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @Size(max = 50)
    private String iconClass;

    private Set<PropertyDTO> properties = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public Set<PropertyDTO> getProperties() {
        return properties;
    }

    public void setProperties(Set<PropertyDTO> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AmenityDTO)) {
            return false;
        }

        AmenityDTO amenityDTO = (AmenityDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, amenityDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AmenityDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", iconClass='" + getIconClass() + "'" +
            ", properties=" + getProperties() +
            "}";
    }
}
