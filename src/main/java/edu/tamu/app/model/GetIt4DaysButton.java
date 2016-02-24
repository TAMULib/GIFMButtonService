package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class GetIt4DaysButton extends AbstractGetItForMeButton {
	
	public GetIt4DaysButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("itemBarcode");
	}

	//button shows for all record types
	@Override
	public boolean checkRecordType(String marcRecord) {
		return true;
	}

	@Override
	public boolean checkLocation(String locationCode) {
		String[] locationCodes = {"rs,hdr", "rs,jlf"};
		return Arrays.asList(locationCodes).contains(locationCode);
	}

	//button shows for all item types
	@Override
	public boolean checkItemType(String typeCode) {
		return true;
	}

	//button shows for all item status
	@Override
	public boolean checkItemStatus(int itemStatusCode) {
		return true;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		//String callNumber, String locationName
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&rfe_dat="+templateParameters.get("itemBarcode");
	}
	
	@Override
	public String getLinkText() {
		return "Get it: 4 days";
	}

	@Override
	public String getSID() {
		return "libcat:remotestorage";
	}

}
