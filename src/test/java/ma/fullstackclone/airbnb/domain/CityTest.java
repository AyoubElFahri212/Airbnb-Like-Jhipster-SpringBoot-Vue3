package ma.fullstackclone.airbnb.domain;

import static ma.fullstackclone.airbnb.domain.CityTestSamples.*;
import static ma.fullstackclone.airbnb.domain.CountryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ma.fullstackclone.airbnb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(City.class);
        City city1 = getCitySample1();
        City city2 = new City();
        assertThat(city1).isNotEqualTo(city2);

        city2.setId(city1.getId());
        assertThat(city1).isEqualTo(city2);

        city2 = getCitySample2();
        assertThat(city1).isNotEqualTo(city2);
    }

    @Test
    void countryTest() {
        City city = getCityRandomSampleGenerator();
        Country countryBack = getCountryRandomSampleGenerator();

        city.setCountry(countryBack);
        assertThat(city.getCountry()).isEqualTo(countryBack);

        city.country(null);
        assertThat(city.getCountry()).isNull();
    }
}
