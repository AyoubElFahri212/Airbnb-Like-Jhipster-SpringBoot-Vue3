package ma.fullstackclone.airbnb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PropertyTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Property getPropertySample1() {
        return new Property()
            .id(1L)
            .title("title1")
            .address("address1")
            .numberOfRooms(1)
            .numberOfBathrooms(1)
            .maxGuests(1)
            .propertySize(1)
            .minimumStay(1)
            .cancellationPolicy("cancellationPolicy1");
    }

    public static Property getPropertySample2() {
        return new Property()
            .id(2L)
            .title("title2")
            .address("address2")
            .numberOfRooms(2)
            .numberOfBathrooms(2)
            .maxGuests(2)
            .propertySize(2)
            .minimumStay(2)
            .cancellationPolicy("cancellationPolicy2");
    }

    public static Property getPropertyRandomSampleGenerator() {
        return new Property()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .numberOfRooms(intCount.incrementAndGet())
            .numberOfBathrooms(intCount.incrementAndGet())
            .maxGuests(intCount.incrementAndGet())
            .propertySize(intCount.incrementAndGet())
            .minimumStay(intCount.incrementAndGet())
            .cancellationPolicy(UUID.randomUUID().toString());
    }
}
