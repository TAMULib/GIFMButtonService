package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Map;

public final class CushingButton extends AbstractGetItForMeButton {
	
	public CushingButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("callNumber");
		this.templateParameterKeys.add("location");
	}

	//button shows for all record types
	@Override
	public boolean checkRecordType(String marcRecord) {
		return true;
	}

	@Override
	public boolean checkLocation(String locationCode) {
		return locationCode.contains("cush");
	}

	//button shows for all item types
	@Override
	public boolean checkItemType(String typeCode) {
		return true;
	}

	//button shows for all item statuses
	@Override
	public boolean checkItemStatus(int itemStatusCode) {
		return true;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		//String callNumber, String locationName
		return "aeon.library.tamu.edu/aeonnew/openurl.asp?sid="+this.getSID()+
				"&callnumber="+templateParameters.get("callNumber")+"&location="+templateParameters.get("location");
	}
	
	@Override
	public String getLinkText() {
		return "Request from Cushing";
	}

	@Override
	public String getSID() {
		return "libcat:cushing";
	}

}
