package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author Jason Savell <jsavell@library.tamu.edu>
 * @author James Creel <jcreel@library.tamu.edu>
 */

@Entity
public class PersistedGetItForMeButton implements GetItForMeButton {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @ElementCollection
	protected List<String> templateParameterKeys;

	@ElementCollection
	protected List<String> locationCodes = new ArrayList<String>();

	@ElementCollection
	protected List<String> itemTypeCodes = new ArrayList<String>();

	@ElementCollection
    protected List<Integer> itemStatusCodes = new ArrayList<Integer>();

	@Column
	private String linkText="Default Link Text";

	@Column
	private String SID="libcat:InProcess";

	@Column
	private String cssClasses = "button-gifm";

	@Override
	public boolean fitsRecordType(String marcRecord) {
		return true;
	}

	@Override
	public boolean fitsLocation(String locationCode) {
		return (locationCodes != null) ? locationCodes.contains(locationCode):true;
	}

	@Override
	public boolean fitsItemType(String itemTypeCode) {
		return (itemTypeCodes != null) ? itemTypeCodes.contains(itemTypeCode):true;
	}

	@Override
	public boolean fitsItemStatus(int itemStatusCode) {
		return (itemStatusCodes != null) ? itemStatusCodes.contains(itemStatusCode):true;
	}

	@Override
	public String getLinkTemplate(Map<String,String> templateParameters) {
		return "default template";
	}

	@Override
	public List<String> getTemplateParameterKeys() {
		return this.templateParameterKeys;
	}

	@Override
	public String getLinkText() {
		return linkText;
	}

	@Override
	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	@Override
	public String getSID() {
		return SID;
	}

	@Override
	public void setSID(String SID) {
		this.SID = SID;
	}

	@Override
	public String getCssClasses() {
		return this.cssClasses;
	}

	@Override
	public void setCssClasses(String cssClasses) {
		this.cssClasses = cssClasses;
	}

	public void setLocationCodes(String[] locationCodes) {
		this.locationCodes = Arrays.asList(locationCodes);
	}

	public void setItemTypeCodes(String[] itemTypeCodes) {
		this.itemTypeCodes = Arrays.asList(itemTypeCodes);
	}

	public void setItemStatusCodes(Integer[] itemStatusCodes) {
		this.itemStatusCodes = Arrays.asList(itemStatusCodes);
	}
}
