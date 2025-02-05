package ma.fullstackclone.airbnb.repository;

import ma.fullstackclone.airbnb.domain.Amenity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Amenity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long>, JpaSpecificationExecutor<Amenity> {}
