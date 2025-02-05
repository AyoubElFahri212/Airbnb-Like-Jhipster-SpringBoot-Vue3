package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.AmenityTestSamples.*;
import static ma.fullstackclone.airbnb.domain.CityTestSamples.*;
import static ma.fullstackclone.airbnb.domain.PropertyCategoryTestSamples.*;
import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PropertyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Property.class);
        Property property1 = getPropertySample1();
        Property property2 = new Property();
        assertThat(property1).isNotEqualTo(property2);

        property2.setId(property1.getId());
        assertThat(property1).isEqualTo(property2);

        property2 = getPropertySample2();
        assertThat(property1).isNotEqualTo(property2);
    }

    @Test
    void cityTest() {
        Property property = getPropertyRandomSampleGenerator();
        City cityBack = getCityRandomSampleGenerator();

        property.setCity(cityBack);
        assertThat(property.getCity()).isEqualTo(cityBack);

        property.city(null);
        assertThat(property.getCity()).isNull();
    }

    @Test
    void amenitiesTest() {
        Property property = getPropertyRandomSampleGenerator();
        Amenity amenityBack = getAmenityRandomSampleGenerator();

        property.addAmenities(amenityBack);
        assertThat(property.getAmenities()).containsOnly(amenityBack);

        property.removeAmenities(amenityBack);
        assertThat(property.getAmenities()).doesNotContain(amenityBack);

        property.amenities(new HashSet<>(Set.of(amenityBack)));
        assertThat(property.getAmenities()).containsOnly(amenityBack);

        property.setAmenities(new HashSet<>());
        assertThat(property.getAmenities()).doesNotContain(amenityBack);
    }

    @Test
    void categoriesTest() {
        Property property = getPropertyRandomSampleGenerator();
        PropertyCategory propertyCategoryBack = getPropertyCategoryRandomSampleGenerator();

        property.addCategories(propertyCategoryBack);
        assertThat(property.getCategories()).containsOnly(propertyCategoryBack);

        property.removeCategories(propertyCategoryBack);
        assertThat(property.getCategories()).doesNotContain(propertyCategoryBack);

        property.categories(new HashSet<>(Set.of(propertyCategoryBack)));
        assertThat(property.getCategories()).containsOnly(propertyCategoryBack);

        property.setCategories(new HashSet<>());
        assertThat(property.getCategories()).doesNotContain(propertyCategoryBack);
    }
}
