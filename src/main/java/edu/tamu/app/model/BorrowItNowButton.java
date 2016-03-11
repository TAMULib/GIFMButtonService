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
	public boolean fitsRecordType(String marcLeader) {
		//holding must be a single item monograph; we can inspect the marcLeader to find out if we have one
		if (marcLeader.substring(6,8).contentEquals("am")) {
			return true;
		}
		return false;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "gwla.relaisd2d.com/service-proxy/?command=mkauth&LS=TEXASAM&PI=TEXASAM&query=isbn%3D"+templateParameters.get("isbn");
	}
}
