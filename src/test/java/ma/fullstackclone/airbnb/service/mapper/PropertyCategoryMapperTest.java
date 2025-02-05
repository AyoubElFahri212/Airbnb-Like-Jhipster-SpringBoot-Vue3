package ma.fullstackclone.airbnb.service.mapper;

import static ma.fullstackclone.airbnb.domain.PropertyCategoryAsserts.*;
import static ma.fullstackclone.airbnb.domain.PropertyCategoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PropertyCategoryMapperTest {

    private PropertyCategoryMapper propertyCategoryMapper;

    @BeforeEach
    void setUp() {
        propertyCategoryMapper = new PropertyCategoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPropertyCategorySample1();
        var actual = propertyCategoryMapper.toEntity(propertyCategoryMapper.toDto(expected));
        assertPropertyCategoryAllPropertiesEquals(expected, actual);
    }
}
