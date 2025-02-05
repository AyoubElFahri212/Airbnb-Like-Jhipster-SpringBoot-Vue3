package ma.fullstackclone.airbnb.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Amenity} and its DTO {@link AmenityDTO}.
 */
@Mapper(componentModel = "spring")
public interface AmenityMapper extends EntityMapper<AmenityDTO, Amenity> {
    @Mapping(target = "properties", source = "properties", qualifiedByName = "propertyIdSet")
    AmenityDTO toDto(Amenity s);

    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "removeProperty", ignore = true)
    Amenity toEntity(AmenityDTO amenityDTO);

    @Named("propertyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PropertyDTO toDtoPropertyId(Property property);

    @Named("propertyIdSet")
    default Set<PropertyDTO> toDtoPropertyIdSet(Set<Property> property) {
        return property.stream().map(this::toDtoPropertyId).collect(Collectors.toSet());
    }
}
