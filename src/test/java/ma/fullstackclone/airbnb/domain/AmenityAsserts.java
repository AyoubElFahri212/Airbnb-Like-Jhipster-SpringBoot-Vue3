package ma.fullstackclone.airbnb.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class AmenityAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmenityAllPropertiesEquals(Amenity expected, Amenity actual) {
        assertAmenityAutoGeneratedPropertiesEquals(expected, actual);
        assertAmenityAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmenityAllUpdatablePropertiesEquals(Amenity expected, Amenity actual) {
        assertAmenityUpdatableFieldsEquals(expected, actual);
        assertAmenityUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmenityAutoGeneratedPropertiesEquals(Amenity expected, Amenity actual) {
        assertThat(expected)
            .as("Verify Amenity auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmenityUpdatableFieldsEquals(Amenity expected, Amenity actual) {
        assertThat(expected)
            .as("Verify Amenity relevant properties")
            .satisfies(e -> assertThat(e.getName()).as("check name").isEqualTo(actual.getName()))
            .satisfies(e -> assertThat(e.getIconClass()).as("check iconClass").isEqualTo(actual.getIconClass()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertAmenityUpdatableRelationshipsEquals(Amenity expected, Amenity actual) {
        assertThat(expected)
            .as("Verify Amenity relationships")
            .satisfies(e -> assertThat(e.getProperties()).as("check properties").isEqualTo(actual.getProperties()));
    }
}
