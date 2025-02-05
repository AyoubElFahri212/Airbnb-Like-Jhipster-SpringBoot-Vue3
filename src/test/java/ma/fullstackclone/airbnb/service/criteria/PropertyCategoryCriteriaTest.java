package ma.fullstackclone.airbnb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PropertyCategoryCriteriaTest {

    @Test
    void newPropertyCategoryCriteriaHasAllFiltersNullTest() {
        var propertyCategoryCriteria = new PropertyCategoryCriteria();
        assertThat(propertyCategoryCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void propertyCategoryCriteriaFluentMethodsCreatesFiltersTest() {
        var propertyCategoryCriteria = new PropertyCategoryCriteria();

        setAllFilters(propertyCategoryCriteria);

        assertThat(propertyCategoryCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void propertyCategoryCriteriaCopyCreatesNullFilterTest() {
        var propertyCategoryCriteria = new PropertyCategoryCriteria();
        var copy = propertyCategoryCriteria.copy();

        assertThat(propertyCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(propertyCategoryCriteria)
        );
    }

    @Test
    void propertyCategoryCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var propertyCategoryCriteria = new PropertyCategoryCriteria();
        setAllFilters(propertyCategoryCriteria);

        var copy = propertyCategoryCriteria.copy();

        assertThat(propertyCategoryCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(propertyCategoryCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var propertyCategoryCriteria = new PropertyCategoryCriteria();

        assertThat(propertyCategoryCriteria).hasToString("PropertyCategoryCriteria{}");
    }

    private static void setAllFilters(PropertyCategoryCriteria propertyCategoryCriteria) {
        propertyCategoryCriteria.id();
        propertyCategoryCriteria.name();
        propertyCategoryCriteria.propertyId();
        propertyCategoryCriteria.distinct();
    }

    private static Condition<PropertyCategoryCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getPropertyId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PropertyCategoryCriteria> copyFiltersAre(
        PropertyCategoryCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getPropertyId(), copy.getPropertyId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
