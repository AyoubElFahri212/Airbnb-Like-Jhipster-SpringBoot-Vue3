package ma.fullstackclone.airbnb.service.mapper;

import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.Review;
import ma.fullstackclone.airbnb.domain.User;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.dto.ReviewDTO;
import ma.fullstackclone.airbnb.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Review} and its DTO {@link ReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Mapping(target = "author", source = "author", qualifiedByName = "userId")
    @Mapping(target = "property", source = "property", qualifiedByName = "propertyId")
    ReviewDTO toDto(Review s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("propertyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PropertyDTO toDtoPropertyId(Property property);
}
