package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.AmenityTestSamples.*;
import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AmenityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Amenity.class);
        Amenity amenity1 = getAmenitySample1();
        Amenity amenity2 = new Amenity();
        assertThat(amenity1).isNotEqualTo(amenity2);

        amenity2.setId(amenity1.getId());
        assertThat(amenity1).isEqualTo(amenity2);

        amenity2 = getAmenitySample2();
        assertThat(amenity1).isNotEqualTo(amenity2);
    }

    @Test
    void propertyTest() {
        Amenity amenity = getAmenityRandomSampleGenerator();
        Property propertyBack = getPropertyRandomSampleGenerator();

        amenity.addProperty(propertyBack);
        assertThat(amenity.getProperties()).containsOnly(propertyBack);
        assertThat(propertyBack.getAmenities()).containsOnly(amenity);

        amenity.removeProperty(propertyBack);
        assertThat(amenity.getProperties()).doesNotContain(propertyBack);
        assertThat(propertyBack.getAmenities()).doesNotContain(amenity);

        amenity.properties(new HashSet<>(Set.of(propertyBack)));
        assertThat(amenity.getProperties()).containsOnly(propertyBack);
        assertThat(propertyBack.getAmenities()).containsOnly(amenity);

        amenity.setProperties(new HashSet<>());
        assertThat(amenity.getProperties()).doesNotContain(propertyBack);
        assertThat(propertyBack.getAmenities()).doesNotContain(amenity);
    }
}
