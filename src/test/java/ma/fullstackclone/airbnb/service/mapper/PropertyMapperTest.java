package ma.fullstackclone.airbnb.service.mapper;

import static ma.fullstackclone.airbnb.domain.PropertyAsserts.*;
import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyMapperTest {

    private PropertyMapper propertyMapper;

    @BeforeEach
    void setUp() {
        propertyMapper = new PropertyMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPropertySample1();
        var actual = propertyMapper.toEntity(propertyMapper.toDto(expected));
        assertPropertyAllPropertiesEquals(expected, actual);
    }
}
