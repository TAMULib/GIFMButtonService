package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class BorrowItNowButton extends AbstractGetItForMeButton {
	
	public BorrowItNowButton() {
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

	@Override
	public boolean checkItemType(String itemTypeCode) {
		String[] itemTypeCodes = {"curr", "normal", "14d", "newbook"};
		return Arrays.asList(itemTypeCodes).contains(itemTypeCode);
	}

	@Override
	public boolean checkItemStatus(int itemStatusCode) {
		Integer[] itemStatuses = {2,3,4,5,6,7,12,13,14,17,18};
		return Arrays.asList(itemStatuses).contains(itemStatusCode);
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "gwla.relaisd2d.com/service-proxy/?command=mkauth&LS=TEXASAM&PI=TEXASAM&query=isbn%3D"+templateParameters.get("isbn");
	}
	
	@Override
	public String getLinkText() {
		return "Borrow It Now";
	}

	@Override
	public String getSID() {
		return "libcat:Borrow";
	}

}
