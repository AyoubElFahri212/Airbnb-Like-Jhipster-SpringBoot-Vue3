package ma.fullstackclone.airbnb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Amenity.
 */
@Entity
@Table(name = "amenity")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "amenity")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Amenity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false, unique = true)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String name;

    @Size(max = 50)
    @Column(name = "icon_class", length = 50)
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String iconClass;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "amenities")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "host", "city", "amenities", "categories" }, allowSetters = true)
    private Set<Property> properties = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Amenity id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Amenity name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconClass() {
        return this.iconClass;
    }

    public Amenity iconClass(String iconClass) {
        this.setIconClass(iconClass);
        return this;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public Set<Property> getProperties() {
        return this.properties;
    }

    public void setProperties(Set<Property> properties) {
        if (this.properties != null) {
            this.properties.forEach(i -> i.removeAmenities(this));
        }
        if (properties != null) {
            properties.forEach(i -> i.addAmenities(this));
        }
        this.properties = properties;
    }

    public Amenity properties(Set<Property> properties) {
        this.setProperties(properties);
        return this;
    }

    public Amenity addProperty(Property property) {
        this.properties.add(property);
        property.getAmenities().add(this);
        return this;
    }

    public Amenity removeProperty(Property property) {
        this.properties.remove(property);
        property.getAmenities().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Amenity)) {
            return false;
        }
        return getId() != null && getId().equals(((Amenity) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Amenity{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", iconClass='" + getIconClass() + "'" +
            "}";
    }
}
