package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public final class GetIt2DaysDocDelButton extends AbstractGetItForMeButton {
	private Boolean inheritLocationTest = false;
	private Boolean inheritItemTypeTest = false;
	private Boolean inheritItemStatusTest = false;
	
	public GetIt2DaysDocDelButton(String[] configuredLocationCodes,String[] configuredItemTypeCodes, Integer[] configuredItemStatusCodes, String configuredLinkText) {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("callNumber");
		this.templateParameterKeys.add("location");
		this.templateParameterKeys.add("location");
		this.templateParameterKeys.add("title");
		this.templateParameterKeys.add("author");
		this.templateParameterKeys.add("isbn");
		this.templateParameterKeys.add("publisher");
		if (configuredLocationCodes != null) {
			this.setLocationCodes(configuredLocationCodes);
		} else {
			this.inheritLocationTest = true;
		}
		if (configuredItemTypeCodes != null) {
			this.setItemTypes(configuredItemTypeCodes);
		} else {
			this.inheritItemTypeTest = true;
		}
		if (configuredItemStatusCodes != null) {
			this.setItemStatusCodes(configuredItemStatusCodes);
		} else {
			this.inheritItemStatusTest = true;
		}

		setLinkText((configuredLinkText!=null) ? configuredLinkText:"Get It: 2 days");
		setSID("libcat:DocDel");
		setCssClasses(this.getCssClasses()+" button-docdel");
	}

	@Override
	public boolean fitsLocation(String locationCode) {
		if (inheritLocationTest) {
			return super.fitsLocation(locationCode);
		} else {
			return Arrays.asList(this.locationCodes).contains(locationCode);
		}
	}

	@Override
	public boolean fitsItemType(String itemTypeCode) {
		if (inheritItemTypeTest) {
			return super.fitsItemType(itemTypeCode);
		} else {
			return Arrays.asList(this.itemTypeCodes).contains(itemTypeCode);
		}
	}

	@Override
	public boolean fitsItemStatus(int itemStatusCode) {
		if (inheritItemStatusTest) {
			return super.fitsItemStatus(itemStatusCode);
		} else {
			return Arrays.asList(this.itemStatusCodes).contains(itemStatusCode);
		}
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&title="+templateParameters.get("title")+"&author="+templateParameters.get("author")+"&isbn="+templateParameters.get("isbn")+
				"&publisher="+templateParameters.get("publisher")+"&rfe_dat="+templateParameters.get("callNumber")+":"+templateParameters.get("location");
	}
}
