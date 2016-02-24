package edu.tamu.app.model;

import java.util.List;
import java.util.Map;

public interface GetItForMeButton {
	boolean checkRecordType(String marcRecord);
	boolean checkLocation(String locationCode);
	boolean checkItemType(String typeCode);
	boolean checkItemStatus(String itemStatusCode);
	String getSID();
	String getLinkTemplate(Map<String,String> templateParameters);
	List<String> getTemplateParameterKeys();
	String getLinkText();
}
