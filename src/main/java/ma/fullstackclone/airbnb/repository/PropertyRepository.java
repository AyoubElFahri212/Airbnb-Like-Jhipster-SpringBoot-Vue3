package ma.fullstackclone.airbnb.repository;

import java.util.List;
import java.util.Optional;
import ma.fullstackclone.airbnb.domain.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Property entity.
 *
 * When extending this class, extend PropertyRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface PropertyRepository
    extends PropertyRepositoryWithBagRelationships, JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    @Query("select property from Property property where property.host.login = ?#{authentication.name}")
    List<Property> findByHostIsCurrentUser();

    default Optional<Property> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Property> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Property> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
