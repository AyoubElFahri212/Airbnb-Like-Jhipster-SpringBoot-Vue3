package ma.fullstackclone.airbnb.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PropertyCriteriaTest {

    @Test
    void newPropertyCriteriaHasAllFiltersNullTest() {
        var propertyCriteria = new PropertyCriteria();
        assertThat(propertyCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void propertyCriteriaFluentMethodsCreatesFiltersTest() {
        var propertyCriteria = new PropertyCriteria();

        setAllFilters(propertyCriteria);

        assertThat(propertyCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void propertyCriteriaCopyCreatesNullFilterTest() {
        var propertyCriteria = new PropertyCriteria();
        var copy = propertyCriteria.copy();

        assertThat(propertyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(propertyCriteria)
        );
    }

    @Test
    void propertyCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var propertyCriteria = new PropertyCriteria();
        setAllFilters(propertyCriteria);

        var copy = propertyCriteria.copy();

        assertThat(propertyCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(propertyCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var propertyCriteria = new PropertyCriteria();

        assertThat(propertyCriteria).hasToString("PropertyCriteria{}");
    }

    private static void setAllFilters(PropertyCriteria propertyCriteria) {
        propertyCriteria.id();
        propertyCriteria.title();
        propertyCriteria.pricePerNight();
        propertyCriteria.address();
        propertyCriteria.latitude();
        propertyCriteria.longitude();
        propertyCriteria.numberOfRooms();
        propertyCriteria.numberOfBathrooms();
        propertyCriteria.maxGuests();
        propertyCriteria.propertySize();
        propertyCriteria.availabilityStart();
        propertyCriteria.availabilityEnd();
        propertyCriteria.instantBook();
        propertyCriteria.minimumStay();
        propertyCriteria.cancellationPolicy();
        propertyCriteria.isActive();
        propertyCriteria.hostId();
        propertyCriteria.cityId();
        propertyCriteria.amenitiesId();
        propertyCriteria.categoriesId();
        propertyCriteria.distinct();
    }

    private static Condition<PropertyCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getPricePerNight()) &&
                condition.apply(criteria.getAddress()) &&
                condition.apply(criteria.getLatitude()) &&
                condition.apply(criteria.getLongitude()) &&
                condition.apply(criteria.getNumberOfRooms()) &&
                condition.apply(criteria.getNumberOfBathrooms()) &&
                condition.apply(criteria.getMaxGuests()) &&
                condition.apply(criteria.getPropertySize()) &&
                condition.apply(criteria.getAvailabilityStart()) &&
                condition.apply(criteria.getAvailabilityEnd()) &&
                condition.apply(criteria.getInstantBook()) &&
                condition.apply(criteria.getMinimumStay()) &&
                condition.apply(criteria.getCancellationPolicy()) &&
                condition.apply(criteria.getIsActive()) &&
                condition.apply(criteria.getHostId()) &&
                condition.apply(criteria.getCityId()) &&
                condition.apply(criteria.getAmenitiesId()) &&
                condition.apply(criteria.getCategoriesId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PropertyCriteria> copyFiltersAre(PropertyCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getPricePerNight(), copy.getPricePerNight()) &&
                condition.apply(criteria.getAddress(), copy.getAddress()) &&
                condition.apply(criteria.getLatitude(), copy.getLatitude()) &&
                condition.apply(criteria.getLongitude(), copy.getLongitude()) &&
                condition.apply(criteria.getNumberOfRooms(), copy.getNumberOfRooms()) &&
                condition.apply(criteria.getNumberOfBathrooms(), copy.getNumberOfBathrooms()) &&
                condition.apply(criteria.getMaxGuests(), copy.getMaxGuests()) &&
                condition.apply(criteria.getPropertySize(), copy.getPropertySize()) &&
                condition.apply(criteria.getAvailabilityStart(), copy.getAvailabilityStart()) &&
                condition.apply(criteria.getAvailabilityEnd(), copy.getAvailabilityEnd()) &&
                condition.apply(criteria.getInstantBook(), copy.getInstantBook()) &&
                condition.apply(criteria.getMinimumStay(), copy.getMinimumStay()) &&
                condition.apply(criteria.getCancellationPolicy(), copy.getCancellationPolicy()) &&
                condition.apply(criteria.getIsActive(), copy.getIsActive()) &&
                condition.apply(criteria.getHostId(), copy.getHostId()) &&
                condition.apply(criteria.getCityId(), copy.getCityId()) &&
                condition.apply(criteria.getAmenitiesId(), copy.getAmenitiesId()) &&
                condition.apply(criteria.getCategoriesId(), copy.getCategoriesId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
