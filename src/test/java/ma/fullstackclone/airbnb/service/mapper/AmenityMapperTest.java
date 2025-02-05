package ma.fullstackclone.airbnb.service.mapper;

import static ma.fullstackclone.airbnb.domain.AmenityAsserts.*;
import static ma.fullstackclone.airbnb.domain.AmenityTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AmenityMapperTest {

    private AmenityMapper amenityMapper;

    @BeforeEach
    void setUp() {
        amenityMapper = new AmenityMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAmenitySample1();
        var actual = amenityMapper.toEntity(amenityMapper.toDto(expected));
        assertAmenityAllPropertiesEquals(expected, actual);
    }
}
