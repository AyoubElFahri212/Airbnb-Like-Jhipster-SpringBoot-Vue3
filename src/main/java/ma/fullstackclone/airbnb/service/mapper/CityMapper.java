package ma.fullstackclone.airbnb.service.mapper;

import ma.fullstackclone.airbnb.domain.City;
import ma.fullstackclone.airbnb.domain.Country;
import ma.fullstackclone.airbnb.service.dto.CityDTO;
import ma.fullstackclone.airbnb.service.dto.CountryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link City} and its DTO {@link CityDTO}.
 */
@Mapper(componentModel = "spring")
public interface CityMapper extends EntityMapper<CityDTO, City> {
    @Mapping(target = "country", source = "country", qualifiedByName = "countryId")
    CityDTO toDto(City s);

    @Named("countryId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CountryDTO toDtoCountryId(Country country);
}
