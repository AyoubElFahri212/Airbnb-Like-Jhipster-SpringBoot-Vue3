package ma.fullstackclone.airbnb.service.mapper;

import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.PropertyImage;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.dto.PropertyImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PropertyImage} and its DTO {@link PropertyImageDTO}.
 */
@Mapper(componentModel = "spring")
public interface PropertyImageMapper extends EntityMapper<PropertyImageDTO, PropertyImage> {
    @Mapping(target = "property", source = "property", qualifiedByName = "propertyId")
    PropertyImageDTO toDto(PropertyImage s);

    @Named("propertyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PropertyDTO toDtoPropertyId(Property property);
}
