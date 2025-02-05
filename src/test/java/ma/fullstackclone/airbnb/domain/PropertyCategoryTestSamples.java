package ma.fullstackclone.airbnb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PropertyCategoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PropertyCategory getPropertyCategorySample1() {
        return new PropertyCategory().id(1L).name("name1");
    }

    public static PropertyCategory getPropertyCategorySample2() {
        return new PropertyCategory().id(2L).name("name2");
    }

    public static PropertyCategory getPropertyCategoryRandomSampleGenerator() {
        return new PropertyCategory().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
