package ma.fullstackclone.airbnb.repository;

import ma.fullstackclone.airbnb.domain.PropertyImage;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PropertyImage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long>, JpaSpecificationExecutor<PropertyImage> {}
