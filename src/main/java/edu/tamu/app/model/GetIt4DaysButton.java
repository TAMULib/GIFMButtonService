package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * The GetIt4Days Button represents a request for an item that is in a Remote Storage Facility
 * 
 * @author Jason Savell <jsavell@library.tamu.edu>
 * @author Michael Nichols <mnichols@library.tamu.edu>
 *
 */

public final class GetIt4DaysButton extends AbstractGetItForMeButton {
	
	public GetIt4DaysButton() {
		this.templateParameterKeys = new ArrayList<String>();
		this.templateParameterKeys.add("title");
		this.templateParameterKeys.add("author");
		this.templateParameterKeys.add("isbn");
		this.templateParameterKeys.add("issn");
		this.templateParameterKeys.add("publisher");
		this.templateParameterKeys.add("genre");
		this.templateParameterKeys.add("place");
		this.templateParameterKeys.add("itemBarcode");
		this.templateParameterKeys.add("year");

		setLinkText("Get it: 4 days");
		setSID("libcat:remotestorage");		
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "getitforme.library.tamu.edu/illiad/EVANSLocal/openurl.asp?Action=10&Form=30&sid="+this.getSID()+
				"&title="+templateParameters.get("title")+
				"&author="+templateParameters.get("author")+
				"&isbn="+templateParameters.get("isbn")+
				"&issn="+templateParameters.get("issn")+
				"&publisher="+templateParameters.get("publisher")+
				"&genre="+templateParameters.get("genre")+
				"&rfe_dat="+templateParameters.get("itemBarcode")+
				"&place="+templateParameters.get("place")+
				"&date="+templateParameters.get("year");

	}
}
