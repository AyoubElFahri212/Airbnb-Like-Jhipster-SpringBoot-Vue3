package ma.fullstackclone.airbnb.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AmenityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AmenityDTO.class);
        AmenityDTO amenityDTO1 = new AmenityDTO();
        amenityDTO1.setId(1L);
        AmenityDTO amenityDTO2 = new AmenityDTO();
        assertThat(amenityDTO1).isNotEqualTo(amenityDTO2);
        amenityDTO2.setId(amenityDTO1.getId());
        assertThat(amenityDTO1).isEqualTo(amenityDTO2);
        amenityDTO2.setId(2L);
        assertThat(amenityDTO1).isNotEqualTo(amenityDTO2);
        amenityDTO1.setId(null);
        assertThat(amenityDTO1).isNotEqualTo(amenityDTO2);
    }
}
