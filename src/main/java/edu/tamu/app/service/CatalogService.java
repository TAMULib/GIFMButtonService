package edu.tamu.app.service;

import edu.tamu.framework.util.HttpUtility;

public interface CatalogService {

	String getHoldingsByBibId(String bibId);
	
	String getName();
	
	void setName(String name);
	
	String getType();
	
	void  setType(String type);
	
	String getHost();
	
	void setHost(String host);
	
	String getPort();
	
	void setPort(String port);

	String getApp();
	
	void setApp(String app);
	
	String getProtocol();
	
	void setProtocol(String protocol);
	
	HttpUtility getHttpUtility();
	
	void setHttpUtility(HttpUtility httpUtility);

}
