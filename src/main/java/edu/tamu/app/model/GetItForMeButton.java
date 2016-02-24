package edu.tamu.app.model;

public interface GetItForMeButton {
	public static boolean checkRecordType(String marcRecord) {
		return false;
	}
	
	public static boolean checkLocation(String locationCode) {
		return false;
	}

	public static boolean checkItemType(String typeCode) {
		return false;
	}

	public static boolean checkItemStatus(int itemStatusCode) {
		return false;
	}

	public static String getSID() {
		return null;
	}
}
