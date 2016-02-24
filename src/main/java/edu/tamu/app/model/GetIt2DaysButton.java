package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Map;

public final class GetIt2DaysButton extends AbstractGetItForMeButton {
	
	public GetIt2DaysButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("callNumber");
		this.templateParameterKeys.add("location");
		this.templateParameterKeys.add("itemBarcode");
	}

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
	public boolean checkItemStatus(int itemStatusCode) {
		if (itemStatusCode == 22) {
			return true;
		}
		return false;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		//String callNumber, String locationName
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&rfe_dat="+templateParameters.get("callNumber")+":"+templateParameters.get("location")+"&Notes="+templateParameters.get("itemBarcode");
	}
	
	@Override
	public String getLinkText() {
		return "Get it: 2 days";
	}

	@Override
	public String getSID() {
		return "libcat:InProcess";
	}

}
