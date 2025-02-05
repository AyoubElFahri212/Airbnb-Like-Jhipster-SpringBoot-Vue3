package ma.fullstackclone.airbnb.repository;

import ma.fullstackclone.airbnb.domain.PropertyCategory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PropertyCategory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyCategoryRepository extends JpaRepository<PropertyCategory, Long>, JpaSpecificationExecutor<PropertyCategory> {}
