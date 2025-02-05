package ma.fullstackclone.airbnb.repository;

import java.util.List;
import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Property;
import org.springframework.data.domain.Page;

public interface PropertyRepositoryWithBagRelationships {
    Optional<Property> fetchBagRelationships(Optional<Property> property);

    List<Property> fetchBagRelationships(List<Property> properties);

    Page<Property> fetchBagRelationships(Page<Property> properties);
}
