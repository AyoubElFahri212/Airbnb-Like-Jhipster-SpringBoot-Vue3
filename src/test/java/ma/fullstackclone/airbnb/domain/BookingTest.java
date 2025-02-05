package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.BookingTestSamples.*;
import static ma.fullstackclone.airbnb.domain.PropertyTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BookingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Booking.class);
        Booking booking1 = getBookingSample1();
        Booking booking2 = new Booking();
        assertThat(booking1).isNotEqualTo(booking2);

        booking2.setId(booking1.getId());
        assertThat(booking1).isEqualTo(booking2);

        booking2 = getBookingSample2();
        assertThat(booking1).isNotEqualTo(booking2);
    }

    @Test
    void propertyTest() {
        Booking booking = getBookingRandomSampleGenerator();
        Property propertyBack = getPropertyRandomSampleGenerator();

        booking.setProperty(propertyBack);
        assertThat(booking.getProperty()).isEqualTo(propertyBack);

        booking.property(null);
        assertThat(booking.getProperty()).isNull();
    }
}
