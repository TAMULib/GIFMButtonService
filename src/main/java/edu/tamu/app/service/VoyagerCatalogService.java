package edu.tamu.app.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.tamu.framework.util.HttpUtility;

public class VoyagerCatalogService extends AbstractCatalogService {
	@Autowired
	private HttpUtility httpUtility;

	
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//private Map<String,Map<String,String>> catalogs = new HashMap<String,Map<String,String>>();
	
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
}
