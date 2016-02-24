package edu.tamu.app.model;

public final class CushingButton implements GetItForMeButton {

	//button shows for all record types
	public static boolean checkRecordType(String marcRecord) {
		return true;
	}

	public static boolean checkLocation(String locationCode) {
		return locationCode.contains("cush");
	}

	//button shows for all item types
	public static boolean checkItemType(String typeCode) {
		return true;
	}

	//button shows for all item statuses
	public static boolean checkItemStatus(int itemStatusCode) {
		return true;
	}

	public static String getLinkTemplate(String callNumber, String locationName) {
		return "aeon.library.tamu.edu/aeonnew/openurl.asp?sid="+CushingButton.getSID()+
				"&callnumber="+callNumber+"&location="+locationName;
	}

	public static String getLinkText() {
		return "Request from Cushing";
	}

	public static String getSID() {
		return "libcat:cushing";
	}
}
