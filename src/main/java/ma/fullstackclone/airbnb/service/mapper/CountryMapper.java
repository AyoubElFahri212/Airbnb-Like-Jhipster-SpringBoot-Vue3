package ma.fullstackclone.airbnb.service.mapper;

import ma.fullstackclone.airbnb.domain.Country;
import ma.fullstackclone.airbnb.service.dto.CountryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Country} and its DTO {@link CountryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CountryMapper extends EntityMapper<CountryDTO, Country> {}
