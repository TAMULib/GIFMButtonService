package edu.tamu.app.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

/**
 * The MapService returns record location details
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 *
 */

@Service
@ConfigurationProperties("mapDetails")
@PropertySource("classpath:mapDetails.properties")
public class MapService {

    public final Map<String, String> mapLinks = new HashMap<String,String>();

    public Map<String, String> getMapLinks() {
        return mapLinks;
    }

    public String getMapLink(String location) {
        if (getMapLinks().containsKey(location.toLowerCase())) {
            return getMapLinks().get(location);
        }
        return getMapLinks().get("default");

    }
}
