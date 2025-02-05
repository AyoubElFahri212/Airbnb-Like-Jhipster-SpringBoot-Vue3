package ma.fullstackclone.airbnb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PropertyImageCriteriaTest {

    @Test
    void newPropertyImageCriteriaHasAllFiltersNullTest() {
        var propertyImageCriteria = new PropertyImageCriteria();
        assertThat(propertyImageCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void propertyImageCriteriaFluentMethodsCreatesFiltersTest() {
        var propertyImageCriteria = new PropertyImageCriteria();

        setAllFilters(propertyImageCriteria);

        assertThat(propertyImageCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void propertyImageCriteriaCopyCreatesNullFilterTest() {
        var propertyImageCriteria = new PropertyImageCriteria();
        var copy = propertyImageCriteria.copy();

        assertThat(propertyImageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(propertyImageCriteria)
        );
    }

    @Test
    void propertyImageCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var propertyImageCriteria = new PropertyImageCriteria();
        setAllFilters(propertyImageCriteria);

        var copy = propertyImageCriteria.copy();

        assertThat(propertyImageCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(propertyImageCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var propertyImageCriteria = new PropertyImageCriteria();

        assertThat(propertyImageCriteria).hasToString("PropertyImageCriteria{}");
    }

    private static void setAllFilters(PropertyImageCriteria propertyImageCriteria) {
        propertyImageCriteria.id();
        propertyImageCriteria.imageUrl();
        propertyImageCriteria.isMain();
        propertyImageCriteria.caption();
        propertyImageCriteria.propertyId();
        propertyImageCriteria.distinct();
    }

    private static Condition<PropertyImageCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getImageUrl()) &&
                condition.apply(criteria.getIsMain()) &&
                condition.apply(criteria.getCaption()) &&
                condition.apply(criteria.getPropertyId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PropertyImageCriteria> copyFiltersAre(
        PropertyImageCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getImageUrl(), copy.getImageUrl()) &&
                condition.apply(criteria.getIsMain(), copy.getIsMain()) &&
                condition.apply(criteria.getCaption(), copy.getCaption()) &&
                condition.apply(criteria.getPropertyId(), copy.getPropertyId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
