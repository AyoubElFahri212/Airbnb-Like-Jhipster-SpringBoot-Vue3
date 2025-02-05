package ma.fullstackclone.airbnb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import ma.fullstackclone.airbnb.domain.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PropertyRepositoryWithBagRelationshipsImpl implements PropertyRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PROPERTIES_PARAMETER = "properties";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Property> fetchBagRelationships(Optional<Property> property) {
        return property.map(this::fetchAmenities).map(this::fetchCategories);
    }

    @Override
    public Page<Property> fetchBagRelationships(Page<Property> properties) {
        return new PageImpl<>(fetchBagRelationships(properties.getContent()), properties.getPageable(), properties.getTotalElements());
    }

    @Override
    public List<Property> fetchBagRelationships(List<Property> properties) {
        return Optional.of(properties).map(this::fetchAmenities).map(this::fetchCategories).orElse(Collections.emptyList());
    }

    Property fetchAmenities(Property result) {
        return entityManager
            .createQuery(
                "select property from Property property left join fetch property.amenities where property.id = :id",
                Property.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Property> fetchAmenities(List<Property> properties) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, properties.size()).forEach(index -> order.put(properties.get(index).getId(), index));
        List<Property> result = entityManager
            .createQuery(
                "select property from Property property left join fetch property.amenities where property in :properties",
                Property.class
            )
            .setParameter(PROPERTIES_PARAMETER, properties)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Property fetchCategories(Property result) {
        return entityManager
            .createQuery(
                "select property from Property property left join fetch property.categories where property.id = :id",
                Property.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Property> fetchCategories(List<Property> properties) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, properties.size()).forEach(index -> order.put(properties.get(index).getId(), index));
        List<Property> result = entityManager
            .createQuery(
                "select property from Property property left join fetch property.categories where property in :properties",
                Property.class
            )
            .setParameter(PROPERTIES_PARAMETER, properties)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
