package ma.fullstackclone.airbnb.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PropertyImageDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertyImageDTO.class);
        PropertyImageDTO propertyImageDTO1 = new PropertyImageDTO();
        propertyImageDTO1.setId(1L);
        PropertyImageDTO propertyImageDTO2 = new PropertyImageDTO();
        assertThat(propertyImageDTO1).isNotEqualTo(propertyImageDTO2);
        propertyImageDTO2.setId(propertyImageDTO1.getId());
        assertThat(propertyImageDTO1).isEqualTo(propertyImageDTO2);
        propertyImageDTO2.setId(2L);
        assertThat(propertyImageDTO1).isNotEqualTo(propertyImageDTO2);
        propertyImageDTO1.setId(null);
        assertThat(propertyImageDTO1).isNotEqualTo(propertyImageDTO2);
    }
}
