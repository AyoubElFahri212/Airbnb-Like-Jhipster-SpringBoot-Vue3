package ma.fullstackclone.airbnb.service.mapper;

import static ma.fullstackclone.airbnb.domain.PropertyImageAsserts.*;
import static ma.fullstackclone.airbnb.domain.PropertyImageTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyImageMapperTest {

    private PropertyImageMapper propertyImageMapper;

    @BeforeEach
    void setUp() {
        propertyImageMapper = new PropertyImageMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPropertyImageSample1();
        var actual = propertyImageMapper.toEntity(propertyImageMapper.toDto(expected));
        assertPropertyImageAllPropertiesEquals(expected, actual);
    }
}
