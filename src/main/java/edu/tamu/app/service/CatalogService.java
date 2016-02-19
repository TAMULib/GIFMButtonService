package edu.tamu.app.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.util.HttpUtility;

@Component
@Service
public class CatalogService {
	@Autowired
	private HttpUtility httpUtility;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Value("${catalogs.file.location:''}")
	private String catalogsFile;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private Map<String,Map<String,String>> catalogs = new HashMap<String,Map<String,String>>();
	
	public String getHoldingsByBibId(String bibId) {
		try {
			logger.debug("Asking for holdings from: "+getAPIBase()+"record/"+bibId+"/holdings");
			String result = httpUtility.makeHttpRequest(getAPIBase()+"record/"+bibId+"/holdings","GET");
			logger.debug("Got data from catalog: ");
			logger.debug(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getAPIBase() {
		return catalogs.get("evans").get("protocol")+"://"+catalogs.get("evans").get("host")+":"+catalogs.get("evans").get("port")+"/"+catalogs.get("evans").get("app")+"/";
	}
	
	@PostConstruct
	public void buildCatalogs() {
		if (!catalogsFile.equals("")) {
			ClassPathResource catalogsRaw = new ClassPathResource(catalogsFile); 
			JsonNode catalogsJson = null;
			try {
				catalogsJson = objectMapper.readTree(new FileInputStream(catalogsRaw.getFile()));
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
			Iterator<Entry<String, JsonNode>> catalogsIt = catalogsJson.get("catalogs").fields();
			while (catalogsIt.hasNext()) {
				Entry<String, JsonNode> entry = catalogsIt.next();
				Map<String,String> catalogDetails = new HashMap<String,String>();
				Iterator<Entry<String,JsonNode>> catalogPropsIt = entry.getValue().fields();
				while (catalogPropsIt.hasNext()) {
					Entry<String, JsonNode> property = catalogPropsIt.next();
					catalogDetails.put(property.getKey(),property.getValue().asText());
				}
				catalogs.put(entry.getKey(),catalogDetails);
			}
		}
	}
}
