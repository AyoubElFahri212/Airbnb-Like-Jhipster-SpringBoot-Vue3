package ma.fullstackclone.airbnb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AmenityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Amenity getAmenitySample1() {
        return new Amenity().id(1L).name("name1").iconClass("iconClass1");
    }

    public static Amenity getAmenitySample2() {
        return new Amenity().id(2L).name("name2").iconClass("iconClass2");
    }

    public static Amenity getAmenityRandomSampleGenerator() {
        return new Amenity().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).iconClass(UUID.randomUUID().toString());
    }
}
