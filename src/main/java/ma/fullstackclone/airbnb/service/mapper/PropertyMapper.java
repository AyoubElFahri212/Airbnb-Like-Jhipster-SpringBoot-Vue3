package ma.fullstackclone.airbnb.service.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import ma.fullstackclone.airbnb.domain.Amenity;
import ma.fullstackclone.airbnb.domain.City;
import ma.fullstackclone.airbnb.domain.Property;
import ma.fullstackclone.airbnb.domain.PropertyCategory;
import ma.fullstackclone.airbnb.domain.User;
import ma.fullstackclone.airbnb.service.dto.AmenityDTO;
import ma.fullstackclone.airbnb.service.dto.CityDTO;
import ma.fullstackclone.airbnb.service.dto.PropertyCategoryDTO;
import ma.fullstackclone.airbnb.service.dto.PropertyDTO;
import ma.fullstackclone.airbnb.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Property} and its DTO {@link PropertyDTO}.
 */
@Mapper(componentModel = "spring")
public interface PropertyMapper extends EntityMapper<PropertyDTO, Property> {
    @Mapping(target = "host", source = "host", qualifiedByName = "userId")
    @Mapping(target = "city", source = "city", qualifiedByName = "cityId")
    @Mapping(target = "amenities", source = "amenities", qualifiedByName = "amenityIdSet")
    @Mapping(target = "categories", source = "categories", qualifiedByName = "propertyCategoryIdSet")
    PropertyDTO toDto(Property s);

    @Mapping(target = "removeAmenities", ignore = true)
    @Mapping(target = "removeCategories", ignore = true)
    Property toEntity(PropertyDTO propertyDTO);

    @Named("userId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserDTO toDtoUserId(User user);

    @Named("cityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CityDTO toDtoCityId(City city);

    @Named("amenityId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    AmenityDTO toDtoAmenityId(Amenity amenity);

    @Named("amenityIdSet")
    default Set<AmenityDTO> toDtoAmenityIdSet(Set<Amenity> amenity) {
        return amenity.stream().map(this::toDtoAmenityId).collect(Collectors.toSet());
    }

    @Named("propertyCategoryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PropertyCategoryDTO toDtoPropertyCategoryId(PropertyCategory propertyCategory);

    @Named("propertyCategoryIdSet")
    default Set<PropertyCategoryDTO> toDtoPropertyCategoryIdSet(Set<PropertyCategory> propertyCategory) {
        return propertyCategory.stream().map(this::toDtoPropertyCategoryId).collect(Collectors.toSet());
    }
}
