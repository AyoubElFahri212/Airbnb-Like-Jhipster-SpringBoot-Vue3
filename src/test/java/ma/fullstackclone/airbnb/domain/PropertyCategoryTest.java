package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.PropertyCategoryTestSamples.*;
import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PropertyCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertyCategory.class);
        PropertyCategory propertyCategory1 = getPropertyCategorySample1();
        PropertyCategory propertyCategory2 = new PropertyCategory();
        assertThat(propertyCategory1).isNotEqualTo(propertyCategory2);

        propertyCategory2.setId(propertyCategory1.getId());
        assertThat(propertyCategory1).isEqualTo(propertyCategory2);

        propertyCategory2 = getPropertyCategorySample2();
        assertThat(propertyCategory1).isNotEqualTo(propertyCategory2);
    }

    @Test
    void propertyTest() {
        PropertyCategory propertyCategory = getPropertyCategoryRandomSampleGenerator();
        Property propertyBack = getPropertyRandomSampleGenerator();

        propertyCategory.addProperty(propertyBack);
        assertThat(propertyCategory.getProperties()).containsOnly(propertyBack);
        assertThat(propertyBack.getCategories()).containsOnly(propertyCategory);

        propertyCategory.removeProperty(propertyBack);
        assertThat(propertyCategory.getProperties()).doesNotContain(propertyBack);
        assertThat(propertyBack.getCategories()).doesNotContain(propertyCategory);

        propertyCategory.properties(new HashSet<>(Set.of(propertyBack)));
        assertThat(propertyCategory.getProperties()).containsOnly(propertyBack);
        assertThat(propertyBack.getCategories()).containsOnly(propertyCategory);

        propertyCategory.setProperties(new HashSet<>());
        assertThat(propertyCategory.getProperties()).doesNotContain(propertyBack);
        assertThat(propertyBack.getCategories()).doesNotContain(propertyCategory);
    }
}
