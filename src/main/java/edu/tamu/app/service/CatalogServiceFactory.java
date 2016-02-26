package edu.tamu.app.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.framework.util.HttpUtility;

@Service
public class CatalogServiceFactory {

	private Map<String, CatalogService> catalogServices = new HashMap<String, CatalogService>();
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private HttpUtility httpUtility;

	@Value("${catalogs.file.location:''}")
	private String catalogsFile;
	
	
	public CatalogService getOrCreateCatalogService(String name)
	{
		if (catalogServices.containsKey(name))
		{
			return catalogServices.get(name);
		}
		else
		{			
			//didn't find it?  Then parse the JSON and construct it and save it
			CatalogService catalogService = buildFromName(name);
			catalogServices.put(name, catalogService);
			return catalogService;
		}		
	}
	
	
	private CatalogService buildFromName(String name) {
		CatalogService catalogService = null;
	
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
		
			JsonNode newCatalog = catalogsJson.get("catalogs").get(name);
			if (newCatalog != null) {
				String host = newCatalog.get("host").asText();
				String port = newCatalog.get("port").asText();
				String app = newCatalog.get("app").asText();
				String protocol = newCatalog.get("protocol").asText();
				
				switch(newCatalog.get("type").asText()) {
					case "voyager":
						catalogService = new VoyagerCatalogService();
						catalogService.setHost(host);
						catalogService.setPort(port);
						catalogService.setApp(app);
						catalogService.setProtocol(protocol);
						catalogService.setType("voyager");
						catalogService.setHttpUtility(httpUtility);
					break;
				}
			}

			/*
			Iterator<Entry<String, JsonNode>> catalogsIt = catalogsJson.get("catalogs").fields();
			while (catalogsIt.hasNext()) {
				Entry<String, JsonNode> entry = catalogsIt.next();
				if (entry.getKey().equals(name)) {
					String host =     entry.getValue().get("host"    ).asText();
					String port =     entry.getValue().get("port"    ).asText();
					String app  =     entry.getValue().get("app"     ).asText();
					String protocol = entry.getValue().get("protocol").asText();
	
					
					switch(	entry.getValue().get("type").asText() )
					{
					case "voyager":
						catalogService = new VoyagerCatalogService();
						catalogService.setHost(host);
						catalogService.setPort(port);
						catalogService.setApp(app);
						catalogService.setProtocol(protocol);
						catalogService.setType("voyager");
						catalogService.setHttpUtility(httpUtility);
						return catalogService;
					
					default:
						return null;
					}
				}
			}
			*/
		}
		return catalogService;
	}
}
