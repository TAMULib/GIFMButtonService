package edu.tamu.app.model;

public interface GetItForMeButton {
	boolean checkRecordType(String marcRecord);
	boolean checkLocation(String locationCode);
	boolean checkItemType(String typeCode);
	boolean checkItemStatus(int itemStatusCode);
	String getSID();
}
