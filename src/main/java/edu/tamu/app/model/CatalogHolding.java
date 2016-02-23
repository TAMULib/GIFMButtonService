package edu.tamu.app.model;

import java.util.HashMap;
import java.util.Map;

public class CatalogHolding {
	private String marcRecordLeader;
	private Map<String,Map<String,String>> catalogItems = new HashMap<String,Map<String,String>>();
	
	public CatalogHolding(String marcRecordLeader,Map<String,Map<String,String>> catalogItems) {
		this.setMarcRecordLeader(marcRecordLeader);
		this.setCatalogItems(catalogItems);
	}
	
	public String getMarcRecordLeader() {
		return marcRecordLeader;
	}

	public void setMarcRecordLeader(String marcRecordLeader) {
		this.marcRecordLeader = marcRecordLeader;
	}

	public Map<String, Map<String, String>> getCatalogItems() {
		return catalogItems;
	}

	public void setCatalogItems(Map<String, Map<String, String>> catalogItems) {
		this.catalogItems = catalogItems;
	}
}
