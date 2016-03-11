package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Map;

public final class GetIt1WeekButton extends AbstractGetItForMeButton {
	
	public GetIt1WeekButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("callNumber");
		this.templateParameterKeys.add("location");
		setLinkText("Get it: 1 week");
		setSID("libcat:Borrow");
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&rfe_dat="+templateParameters.get("callNumber")+":"+templateParameters.get("location");
	}
}
