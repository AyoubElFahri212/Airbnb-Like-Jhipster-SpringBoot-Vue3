package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.PromotionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PromotionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Promotion.class);
        Promotion promotion1 = getPromotionSample1();
        Promotion promotion2 = new Promotion();
        assertThat(promotion1).isNotEqualTo(promotion2);

        promotion2.setId(promotion1.getId());
        assertThat(promotion1).isEqualTo(promotion2);

        promotion2 = getPromotionSample2();
        assertThat(promotion1).isNotEqualTo(promotion2);
    }
}
