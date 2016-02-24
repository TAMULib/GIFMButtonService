package edu.tamu.app.model;

import java.util.List;
import java.util.Map;

public class AbstractGetItForMeButton implements GetItForMeButton {
	protected List<String> templateParameterKeys;
	
	//button shows for all record types
	@Override
	public boolean checkRecordType(String marcRecord) {
		return true;
	}

	//button shows for all record locations
	@Override
	public boolean checkLocation(String locationCode) {
		return true;
	}

	//button shows for all item types
	@Override
	public boolean checkItemType(String typeCode) {
		return true;
	}

	//button shows for item status of 22 only
	@Override
	public boolean checkItemStatus(String itemStatusCode) {
		return true;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		//String callNumber, String locationName
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
