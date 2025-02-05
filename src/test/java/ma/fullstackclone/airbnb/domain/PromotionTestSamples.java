package ma.fullstackclone.airbnb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PromotionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Promotion getPromotionSample1() {
        return new Promotion().id(1L).code("code1").maxUses(1);
    }

    public static Promotion getPromotionSample2() {
        return new Promotion().id(2L).code("code2").maxUses(2);
    }

    public static Promotion getPromotionRandomSampleGenerator() {
        return new Promotion().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).maxUses(intCount.incrementAndGet());
    }
}
