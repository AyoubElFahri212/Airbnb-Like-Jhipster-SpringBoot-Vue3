package ma.fullstackclone.airbnb.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CountryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Country getCountrySample1() {
        return new Country().id(1L).name("name1").code("code1").phoneCode("phoneCode1");
    }

    public static Country getCountrySample2() {
        return new Country().id(2L).name("name2").code("code2").phoneCode("phoneCode2");
    }

    public static Country getCountryRandomSampleGenerator() {
        return new Country()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .code(UUID.randomUUID().toString())
            .phoneCode(UUID.randomUUID().toString());
    }
}
