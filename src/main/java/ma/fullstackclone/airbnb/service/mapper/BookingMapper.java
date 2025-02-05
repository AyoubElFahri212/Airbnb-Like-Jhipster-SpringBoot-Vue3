package ma.fullstackclone.airbnb.service.mapper;

import ma.fullstackclone.airbnb.domain.Booking;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.User;
import ma.fullstackclone.airbnb.service.dto.BookingDTO;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Booking} and its DTO {@link BookingDTO}.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {
    @Mapping(target = "guest", source = "guest", qualifiedByName = "userId")
    @Mapping(target = "property", source = "property", qualifiedByName = "propertyId")
    BookingDTO toDto(Booking s);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("propertyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PropertyDTO toDtoPropertyId(Property property);
}
