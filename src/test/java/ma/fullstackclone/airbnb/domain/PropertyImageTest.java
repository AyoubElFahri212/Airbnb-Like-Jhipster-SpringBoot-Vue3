package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.PropertyImageTestSamples.*;
import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PropertyImageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertyImage.class);
        PropertyImage propertyImage1 = getPropertyImageSample1();
        PropertyImage propertyImage2 = new PropertyImage();
        assertThat(propertyImage1).isNotEqualTo(propertyImage2);

        propertyImage2.setId(propertyImage1.getId());
        assertThat(propertyImage1).isEqualTo(propertyImage2);

        propertyImage2 = getPropertyImageSample2();
        assertThat(propertyImage1).isNotEqualTo(propertyImage2);
    }

    @Test
    void propertyTest() {
        PropertyImage propertyImage = getPropertyImageRandomSampleGenerator();
        Property propertyBack = getPropertyRandomSampleGenerator();

        propertyImage.setProperty(propertyBack);
        assertThat(propertyImage.getProperty()).isEqualTo(propertyBack);

        propertyImage.property(null);
        assertThat(propertyImage.getProperty()).isNull();
    }
}
