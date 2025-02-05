package ma.fullstackclone.airbnb.service.mapper;

import ma.fullstackclone.airbnb.domain.Promotion;
import ma.fullstackclone.airbnb.service.dto.PromotionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Promotion} and its DTO {@link PromotionDTO}.
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper extends EntityMapper<PromotionDTO, Promotion> {}
