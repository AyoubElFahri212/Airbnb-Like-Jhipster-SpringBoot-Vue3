package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;
import static ma.fullstackclone.airbnb.domain.ReviewTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ReviewTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Review.class);
        Review review1 = getReviewSample1();
        Review review2 = new Review();
        assertThat(review1).isNotEqualTo(review2);

        review2.setId(review1.getId());
        assertThat(review1).isEqualTo(review2);

        review2 = getReviewSample2();
        assertThat(review1).isNotEqualTo(review2);
    }

    @Test
    void propertyTest() {
        Review review = getReviewRandomSampleGenerator();
        Property propertyBack = getPropertyRandomSampleGenerator();

        review.setProperty(propertyBack);
        assertThat(review.getProperty()).isEqualTo(propertyBack);

        review.property(null);
        assertThat(review.getProperty()).isNull();
    }
}
