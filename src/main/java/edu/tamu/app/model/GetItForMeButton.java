package edu.tamu.app.model;

import java.util.List;
import java.util.Map;

public interface GetItForMeButton {
	boolean fitsRecordType(String marcRecord);
	boolean fitsLocation(String locationCode);
	boolean fitsItemType(String typeCode);
	boolean fitsItemStatus(int itemStatusCode);
	String getSID();
	String getLinkTemplate(Map<String,String> templateParameters);
	List<String> getTemplateParameterKeys();
	String getLinkText();
}
