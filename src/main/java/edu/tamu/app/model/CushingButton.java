package edu.tamu.app.model;

public class CushingButton implements GetItForMeButton {

	//button shows for all record types
	@Override
	public boolean checkRecordType(String marcRecord) {
		return true;
	}

	@Override
	public boolean checkLocation(String locationCode) {
		return locationCode.contains("cush");
	}

	//button shows for all item types
	@Override
	public boolean checkItemType(String typeCode) {
		return true;
	}

	//button shows for all item statuses
	@Override
	public boolean checkItemStatus(int itemStatusCode) {
		return true;
	}

	public String getLinkTemplate(String callNumber, String locationName) {
		return "aeon.library.tamu.edu/aeonnew/openurl.asp?sid="+this.getSID()+
				"&callnumber="+callNumber+"&location="+locationName;
	}

	public String getLinkText() {
		return "Request from Cushing";
	}

	@Override
	public String getSID() {
		return "libcat:cushing";
	}

}
