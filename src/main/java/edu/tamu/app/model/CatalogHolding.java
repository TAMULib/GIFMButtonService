package edu.tamu.app.model;

import java.util.HashMap;
import java.util.Map;

public class CatalogHolding {
	private String marcRecordLeader;
	private String mfhd;
	
	private Map<String,Map<String,String>> catalogItems = new HashMap<String,Map<String,String>>();
	
	public CatalogHolding(String marcRecordLeader,String mfhd, Map<String,Map<String,String>> catalogItems) {
		this.setMarcRecordLeader(marcRecordLeader);
		this.setMfhd(mfhd);
		this.setCatalogItems(catalogItems);
	}
	
	public String getMarcRecordLeader() {
		return marcRecordLeader;
	}

	public void setMarcRecordLeader(String marcRecordLeader) {
		this.marcRecordLeader = marcRecordLeader;
	}
	
	public String getMfhd() {
		return mfhd;
	}

	public void setMfhd(String mfhd) {
		this.mfhd = mfhd;
	}


	public Map<String, Map<String, String>> getCatalogItems() {
		return catalogItems;
	}

	public void setCatalogItems(Map<String, Map<String, String>> catalogItems) {
		this.catalogItems = catalogItems;
	}
	
	public boolean isMultiVolume() {
		return (this.getCatalogItems().size() > 1);
	}
}
