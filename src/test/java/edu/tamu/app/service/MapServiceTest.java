package edu.tamu.app.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.dto.MapDetail;
import edu.tamu.app.enums.MapType;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { WebServerInit.class }, webEnvironment = WebEnvironment.DEFINED_PORT)
public class MapServiceTest {

    @Autowired
    private MapService mapService;

    private static final String STACKMAP_CODE = "curr,text";
    private static final String STACKMAP_URL = null;

    private static final String LOCAL_CODE = "cush,scif";
    private static final String LOCAL_URL = "https://library.tamu.edu/about/directions/cushing-library.html";

    private static final String BYPASS_CODE = "hdr";
    private static final String BYPASS_URL = "https://library.tamu.edu/404.html";

    private static final String UNKNOWN_CODE = "madeupcode";
    private static final String UNKNOWN_URL = "https://library.tamu.edu/404.html";

    @Test
    public void testGetMapLinkStackMap() {
        MapDetail stackmapMapDetail = mapService.getMapLink(STACKMAP_CODE);

        assertEquals("Stackmap Map Detail Type is incorrect", stackmapMapDetail.type, MapType.StackMap);
        assertEquals("Stackmap Map Detail URL is incorrect", stackmapMapDetail.url, STACKMAP_URL);
    }

    @Test
    public void testGetMapLinkLocal() {
        MapDetail localMapDetail = mapService.getMapLink(LOCAL_CODE);

        assertEquals("Local Map Detail Type is incorrect", localMapDetail.type, MapType.URL);
        assertEquals("Local Map Detail URL is incorrect", localMapDetail.url, LOCAL_URL);
    }

    @Test
    public void testGetMapLinkCodeBypass() {
        MapDetail bypassMapDetail = mapService.getMapLink(BYPASS_CODE);

        assertEquals("Bypass Map Detail Type is incorrect", bypassMapDetail.type, MapType.Unknown);
        assertEquals("Bypass Map Detail URL is incorrect", bypassMapDetail.url, BYPASS_URL);
    }

    @Test
    public void testGetMapLinkUnknown() {
        MapDetail unknownMapDetail = mapService.getMapLink(UNKNOWN_CODE);

        assertEquals("Unknown Map Detail Type is incorrect", unknownMapDetail.type, MapType.Unknown);
        assertEquals("Unknown Map Detail URL is incorrect", unknownMapDetail.url, UNKNOWN_URL);
    }
}
