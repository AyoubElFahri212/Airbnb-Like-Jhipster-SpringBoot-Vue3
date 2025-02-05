package ma.fullstackclone.airbnb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AmenityCriteriaTest {

    @Test
    void newAmenityCriteriaHasAllFiltersNullTest() {
        var amenityCriteria = new AmenityCriteria();
        assertThat(amenityCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void amenityCriteriaFluentMethodsCreatesFiltersTest() {
        var amenityCriteria = new AmenityCriteria();

        setAllFilters(amenityCriteria);

        assertThat(amenityCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void amenityCriteriaCopyCreatesNullFilterTest() {
        var amenityCriteria = new AmenityCriteria();
        var copy = amenityCriteria.copy();

        assertThat(amenityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(amenityCriteria)
        );
    }

    @Test
    void amenityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var amenityCriteria = new AmenityCriteria();
        setAllFilters(amenityCriteria);

        var copy = amenityCriteria.copy();

        assertThat(amenityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(amenityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var amenityCriteria = new AmenityCriteria();

        assertThat(amenityCriteria).hasToString("AmenityCriteria{}");
    }

    private static void setAllFilters(AmenityCriteria amenityCriteria) {
        amenityCriteria.id();
        amenityCriteria.name();
        amenityCriteria.iconClass();
        amenityCriteria.propertyId();
        amenityCriteria.distinct();
    }

    private static Condition<AmenityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getIconClass()) &&
                condition.apply(criteria.getPropertyId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AmenityCriteria> copyFiltersAre(AmenityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getIconClass(), copy.getIconClass()) &&
                condition.apply(criteria.getPropertyId(), copy.getPropertyId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
