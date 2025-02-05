package ma.fullstackclone.airbnb.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PropertyCategory} and its DTO {@link PropertyCategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface PropertyCategoryMapper extends EntityMapper<PropertyCategoryDTO, PropertyCategory> {
    @Mapping(target = "properties", source = "properties", qualifiedByName = "propertyIdSet")
    PropertyCategoryDTO toDto(PropertyCategory s);

    @Mapping(target = "properties", ignore = true)
    @Mapping(target = "removeProperty", ignore = true)
    PropertyCategory toEntity(PropertyCategoryDTO propertyCategoryDTO);

    @Named("propertyId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PropertyDTO toDtoPropertyId(Property property);

    @Named("propertyIdSet")
    default Set<PropertyDTO> toDtoPropertyIdSet(Set<Property> property) {
        return property.stream().map(this::toDtoPropertyId).collect(Collectors.toSet());
    }
}
