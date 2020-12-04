package edu.tamu.app.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.CatalogHolding;
import edu.tamu.weaver.utility.HttpUtility;

@Service
public class CatalogService {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.catalogServiceUrl}")
    private String catalogServiceUrl;

    @Value("${app.catalogsConfiguration.file:''}")
    private String catalogsFile;

    private Map<String,Map<String,String>> catalogConfigurations = new HashMap<String,Map<String,String>>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    protected void buildCatalogConfigurations() {

        if (!catalogsFile.equals("")) {
            ClassPathResource catalogsRaw = new ClassPathResource(catalogsFile);
            try {
                final JsonNode catalogsJson = objectMapper.readTree(new FileInputStream(catalogsRaw.getFile()));
                catalogsJson.get("catalogs").fieldNames().forEachRemaining(catalogName -> {
                    try {
                        catalogConfigurations.put(catalogName, objectMapper.readValue(catalogsJson.get("catalogs").get(catalogName).toString(), new TypeReference<HashMap<String, Object>>() {}));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                });
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public List<CatalogHolding> getHoldingsByBibId(String catalogName, String bibId) {
        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("bibId", bibId);
        parameters.put("catalogName", catalogName);
        try {
            return objectMapper.readValue(getData("get-holdings", parameters).get("ArrayList<CatalogHolding>").toString(), new TypeReference<List<CatalogHolding>>() {});
        } catch (IOException e) {
            logger.error("Unable to retrieve Holdings from Catalog Service");
            e.printStackTrace();
            return null;
        }
    }

    public CatalogHolding getHolding(String catalogName, String bibId, String holdingId) {
        Map<String,String> parameters = new HashMap<String,String>();
        parameters.put("bibId", bibId);
        parameters.put("catalogName", catalogName);
        parameters.put("holdingId", holdingId);
        try {
            return objectMapper.readValue(getData("get-holding", parameters).get("CatalogHolding").toString(), new TypeReference<CatalogHolding>() {});
        } catch (IOException e) {
            logger.error("Unable to retrieve Holding from Catalog Service");
            e.printStackTrace();
            return null;
        }
    }

    public Map<String,String> getCatalogConfigurationByName(String catalogName) {
        return catalogConfigurations.get(catalogName);
    }

    private JsonNode getData(String endPoint, Map<String,String> parameters) throws IOException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(catalogServiceUrl + endPoint+"/");
        if (parameters != null && !parameters.isEmpty()) {
            urlBuilder.append("?");
            parameters.forEach((k,v) -> {
                urlBuilder.append(k + "=" + v + "&");
            });
            urlBuilder.setLength(urlBuilder.length()-1);
        }
        String url = urlBuilder.toString();
        String holdingsResult;
        holdingsResult = HttpUtility.makeHttpRequest(url, "GET");
        JsonNode holdingsNode = objectMapper.readTree(holdingsResult);
        return holdingsNode.get("payload");
    }
}
