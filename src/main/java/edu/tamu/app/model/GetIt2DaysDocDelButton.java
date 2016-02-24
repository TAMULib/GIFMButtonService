package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class GetIt2DaysDocDelButton extends AbstractGetItForMeButton {
	
	public GetIt2DaysDocDelButton() {
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
		String[] locationCodes = {"base", "bsc", "curr", "curr,text", "nbs","stk", "stk,mov1", "tdoc", "udoc", "wein", "psel,stk", "west,audio", "west,nbs", "west,stk", "west,udoc"};
		return Arrays.asList(locationCodes).contains(locationCode);
	}

	//button shows for all item types
	@Override
	public boolean checkItemType(String itemTypeCode) {
		String[] itemTypeCodes = {"curr", "normal", "14d", "newbook"};
		return Arrays.asList(itemTypeCodes).contains(itemTypeCode);
	}

	//button shows for all item status
	@Override
	public boolean checkItemStatus(String itemStatusCode) {
		Integer[] itemStatuses = {1,11};
		return Arrays.asList(itemStatuses).contains(itemStatusCode);
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		//String callNumber, String locationName
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&rfe_dat="+templateParameters.get("callNumber")+":"+templateParameters.get("location");
	}
	
	@Override
	public String getLinkText() {
		return "Recall It!";
	}

	@Override
	public String getSID() {
		return "libcat:DocDel";
	}

}
