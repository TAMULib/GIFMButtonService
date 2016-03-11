package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Map;

public final class BorrowItNowButton extends AbstractGetItForMeButton {
	
	public BorrowItNowButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("isbn");
		setLinkText("Borrow It Now");
		setSID("libcat:Borrow");
		setCssClasses(this.getCssClasses()+" button-borrowitnow");
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "gwla.relaisd2d.com/service-proxy/?command=mkauth&LS=TEXASAM&PI=TEXASAM&query=isbn%3D"+templateParameters.get("isbn");
	}
}
