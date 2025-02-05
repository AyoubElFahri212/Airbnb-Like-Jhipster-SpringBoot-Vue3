package ma.fullstackclone.airbnb.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PropertyCategoryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertyCategoryDTO.class);
        PropertyCategoryDTO propertyCategoryDTO1 = new PropertyCategoryDTO();
        propertyCategoryDTO1.setId(1L);
        PropertyCategoryDTO propertyCategoryDTO2 = new PropertyCategoryDTO();
        assertThat(propertyCategoryDTO1).isNotEqualTo(propertyCategoryDTO2);
        propertyCategoryDTO2.setId(propertyCategoryDTO1.getId());
        assertThat(propertyCategoryDTO1).isEqualTo(propertyCategoryDTO2);
        propertyCategoryDTO2.setId(2L);
        assertThat(propertyCategoryDTO1).isNotEqualTo(propertyCategoryDTO2);
        propertyCategoryDTO1.setId(null);
        assertThat(propertyCategoryDTO1).isNotEqualTo(propertyCategoryDTO2);
    }
}
