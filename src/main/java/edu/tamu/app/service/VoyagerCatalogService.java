package edu.tamu.app.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoyagerCatalogService extends AbstractCatalogService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//private Map<String,Map<String,String>> catalogs = new HashMap<String,Map<String,String>>();
	
	public String getHoldingsByBibId(String bibId) {
		try {
			logger.debug("Asking for holdings from: "+getAPIBase()+"record/"+bibId+"/holdings");
			String result = this.getHttpUtility().makeHttpRequest(getAPIBase()+"record/"+bibId+"/holdings","GET");
			logger.debug("Got data from catalog: ");
			logger.debug(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
