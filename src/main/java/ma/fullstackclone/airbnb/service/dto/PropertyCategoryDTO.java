package ma.fullstackclone.airbnb.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link ma.fullstackclone.airbnb.domain.PropertyCategory} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PropertyCategoryDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @Lob
    private String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof PropertyCategoryDTO)) {
            return false;
        }

        PropertyCategoryDTO propertyCategoryDTO = (PropertyCategoryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, propertyCategoryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PropertyCategoryDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", properties=" + getProperties() +
            "}";
    }
}
