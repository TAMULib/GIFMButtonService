package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Map;

public final class RecallItButton extends AbstractGetItForMeButton {
	
	public RecallItButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("callNumber");
		this.templateParameterKeys.add("location");
		setLinkText("Recall It!");
		setSID("libcat:Borrow");
		setCssClasses(this.getCssClasses()+" button-recallit");
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		//String callNumber, String locationName
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&rfe_dat="+templateParameters.get("callNumber")+":"+templateParameters.get("location");
	}
}
