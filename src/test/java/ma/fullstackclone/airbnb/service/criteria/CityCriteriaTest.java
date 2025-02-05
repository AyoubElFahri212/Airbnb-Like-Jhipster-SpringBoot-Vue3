package ma.fullstackclone.airbnb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CityCriteriaTest {

    @Test
    void newCityCriteriaHasAllFiltersNullTest() {
        var cityCriteria = new CityCriteria();
        assertThat(cityCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void cityCriteriaFluentMethodsCreatesFiltersTest() {
        var cityCriteria = new CityCriteria();

        setAllFilters(cityCriteria);

        assertThat(cityCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void cityCriteriaCopyCreatesNullFilterTest() {
        var cityCriteria = new CityCriteria();
        var copy = cityCriteria.copy();

        assertThat(cityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(cityCriteria)
        );
    }

    @Test
    void cityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var cityCriteria = new CityCriteria();
        setAllFilters(cityCriteria);

        var copy = cityCriteria.copy();

        assertThat(cityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(cityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var cityCriteria = new CityCriteria();

        assertThat(cityCriteria).hasToString("CityCriteria{}");
    }

    private static void setAllFilters(CityCriteria cityCriteria) {
        cityCriteria.id();
        cityCriteria.name();
        cityCriteria.postalCode();
        cityCriteria.latitude();
        cityCriteria.longitude();
        cityCriteria.countryId();
        cityCriteria.distinct();
    }

    private static Condition<CityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPostalCode()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getCountryId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CityCriteria> copyFiltersAre(CityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPostalCode(), copy.getPostalCode()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getCountryId(), copy.getCountryId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
