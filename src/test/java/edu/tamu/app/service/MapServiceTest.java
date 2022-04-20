package edu.tamu.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.app.WebServerInit;
import edu.tamu.app.dto.MapDetail;
import edu.tamu.app.enums.MapType;

@ExtendWith(SpringExtension.class)
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

        assertEquals(stackmapMapDetail.type, MapType.StackMap, "Stackmap Map Detail Type is incorrect");
        assertEquals(stackmapMapDetail.url, STACKMAP_URL, "Stackmap Map Detail URL is incorrect");
    }

    @Test
    public void testGetMapLinkLocal() {
        MapDetail localMapDetail = mapService.getMapLink(LOCAL_CODE);

        assertEquals(localMapDetail.type, MapType.URL, "Local Map Detail Type is incorrect");
        assertEquals(localMapDetail.url, LOCAL_URL, "Local Map Detail URL is incorrect");
    }

    @Test
    public void testGetMapLinkCodeBypass() {
        MapDetail bypassMapDetail = mapService.getMapLink(BYPASS_CODE);

        assertEquals(bypassMapDetail.type, MapType.Unknown, "Bypass Map Detail Type is incorrect");
        assertEquals(bypassMapDetail.url, BYPASS_URL, "Bypass Map Detail URL is incorrect");
    }

    @Test
    public void testGetMapLinkUnknown() throws Exception {
        MapDetail unknownMapDetail = mapService.getMapLink(UNKNOWN_CODE);

        assertEquals(unknownMapDetail.type, MapType.Unknown, "Unknown Map Detail Type is incorrect");
        assertEquals(unknownMapDetail.url, UNKNOWN_URL, "Unknown Map Detail URL is incorrect");
    }
}
