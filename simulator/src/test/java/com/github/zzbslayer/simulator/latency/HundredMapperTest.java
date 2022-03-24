package com.github.zzbslayer.simulator.latency;

import com.github.zzbslayer.simulator.core.latency.prediction.mapper.HundredMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HundredMapperTest {
    HundredMapper mapper = new HundredMapper();

    @Test
    public void encodeTest() {
        Assertions.assertEquals(1, mapper.accessToInstance(-11));
        Assertions.assertEquals(1, mapper.accessToInstance(100));
        Assertions.assertEquals(1, mapper.accessToInstance(90));
        Assertions.assertEquals(2, mapper.accessToInstance(101));
        Assertions.assertEquals(2, mapper.accessToInstance(200));
    }

    @Test
    public void decodeTest() {
        Assertions.assertThrows(RuntimeException.class, () -> mapper.instanceToAccess(-1));

        Assertions.assertEquals(1, mapper.accessToInstance(mapper.instanceToAccess(1)));

        Assertions.assertEquals(1, mapper.accessToInstance(mapper.instanceToAccess(1)));
        Assertions.assertEquals(2, mapper.accessToInstance(mapper.instanceToAccess(2)));
    }
}
