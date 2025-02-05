package ma.fullstackclone.airbnb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PromotionCriteriaTest {

    @Test
    void newPromotionCriteriaHasAllFiltersNullTest() {
        var promotionCriteria = new PromotionCriteria();
        assertThat(promotionCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void promotionCriteriaFluentMethodsCreatesFiltersTest() {
        var promotionCriteria = new PromotionCriteria();

        setAllFilters(promotionCriteria);

        assertThat(promotionCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void promotionCriteriaCopyCreatesNullFilterTest() {
        var promotionCriteria = new PromotionCriteria();
        var copy = promotionCriteria.copy();

        assertThat(promotionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(promotionCriteria)
        );
    }

    @Test
    void promotionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var promotionCriteria = new PromotionCriteria();
        setAllFilters(promotionCriteria);

        var copy = promotionCriteria.copy();

        assertThat(promotionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(promotionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var promotionCriteria = new PromotionCriteria();

        assertThat(promotionCriteria).hasToString("PromotionCriteria{}");
    }

    private static void setAllFilters(PromotionCriteria promotionCriteria) {
        promotionCriteria.id();
        promotionCriteria.code();
        promotionCriteria.discountType();
        promotionCriteria.discountValue();
        promotionCriteria.validFrom();
        promotionCriteria.validUntil();
        promotionCriteria.maxUses();
        promotionCriteria.isActive();
        promotionCriteria.distinct();
    }

    private static Condition<PromotionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getDiscountType()) &&
                condition.apply(criteria.getDiscountValue()) &&
                condition.apply(criteria.getValidFrom()) &&
                condition.apply(criteria.getValidUntil()) &&
                condition.apply(criteria.getMaxUses()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PromotionCriteria> copyFiltersAre(PromotionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getDiscountType(), copy.getDiscountType()) &&
                condition.apply(criteria.getDiscountValue(), copy.getDiscountValue()) &&
                condition.apply(criteria.getValidFrom(), copy.getValidFrom()) &&
                condition.apply(criteria.getValidUntil(), copy.getValidUntil()) &&
                condition.apply(criteria.getMaxUses(), copy.getMaxUses()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
