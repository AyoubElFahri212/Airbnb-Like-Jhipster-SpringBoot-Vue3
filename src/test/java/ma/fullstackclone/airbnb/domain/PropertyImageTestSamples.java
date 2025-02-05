package ma.fullstackclone.airbnb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PropertyImageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PropertyImage getPropertyImageSample1() {
        return new PropertyImage().id(1L).imageUrl("imageUrl1").caption("caption1");
    }

    public static PropertyImage getPropertyImageSample2() {
        return new PropertyImage().id(2L).imageUrl("imageUrl2").caption("caption2");
    }

    public static PropertyImage getPropertyImageRandomSampleGenerator() {
        return new PropertyImage()
            .id(longCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .caption(UUID.randomUUID().toString());
    }
}
