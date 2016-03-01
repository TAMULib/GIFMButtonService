package edu.tamu.app.model;

import java.util.List;
import java.util.Map;

public class AbstractGetItForMeButton implements GetItForMeButton {
	protected List<String> templateParameterKeys;
	
	//button shows for all record types
	@Override
	public boolean fitsRecordType(String marcRecord) {
		return true;
	}

	//button shows for all record locations
	@Override
	public boolean fitsLocation(String locationCode) {
		return true;
	}

	//button shows for all item types
	@Override
	public boolean fitsItemType(String typeCode) {
		return true;
	}

	//button shows for all item statuses
	@Override
	public boolean fitsItemStatus(int itemStatusCode) {
		return true;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "default template";
	}
	
	@Override
	public List<String> getTemplateParameterKeys() {
		return this.templateParameterKeys;
	}

	@Override
	public String getLinkText() {
		return "Default Link Text";
	}

	@Override
	public String getSID() {
		return "libcat:InProcess";
	}

}
