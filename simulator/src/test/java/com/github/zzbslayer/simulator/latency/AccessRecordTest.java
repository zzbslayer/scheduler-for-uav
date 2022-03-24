package com.github.zzbslayer.simulator.latency;

import com.github.zzbslayer.simulator.core.latency.record.AccessRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccessRecordTest {
    @Test
    public void copyFromTest() {
        AccessRecord accessRecord = new AccessRecord(5, 5);
        accessRecord.access(0, 0);
        Assertions.assertEquals(1, accessRecord.getNodeServiceAccessMap().get(0).get(0));


        AccessRecord copy = new AccessRecord(5, 5);
        copy.copyFrom(accessRecord);

        Assertions.assertEquals(1, copy.getNodeServiceAccessMap().get(0).get(0));
        Assertions.assertEquals(0, copy.getNodeServiceAccessMap().get(1).get(0));
        Assertions.assertEquals(0, copy.getNodeServiceAccessMap().get(3).get(2));

    }
}
