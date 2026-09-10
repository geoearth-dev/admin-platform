package dev.geo.admin.system.service.auth.impl;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LegacyStatusConversionTest {
    @Test
    void preservesExternalEnabledAndDisabledMeaning() {
        assertEquals("1", SysSyncDataServiceImpl.fromLegacyStatus("0"));
        assertEquals("0", SysSyncDataServiceImpl.fromLegacyStatus("1"));
        assertNull(SysSyncDataServiceImpl.fromLegacyStatus(null));
        assertEquals("2", SysSyncDataServiceImpl.fromLegacyStatus("2"));
    }
}
