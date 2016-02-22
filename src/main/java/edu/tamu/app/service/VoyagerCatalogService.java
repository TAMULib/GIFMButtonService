package edu.tamu.app.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

class VoyagerCatalogService extends AbstractCatalogService {
	@Autowired
	private ObjectMapper objectMapper;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//private Map<String,Map<String,String>> catalogs = new HashMap<String,Map<String,String>>();
	
	public String getHoldingsByBibId(String bibId) {
		try {
			logger.debug("Asking for holdings from: "+getAPIBase()+"record/"+bibId+"/holdings?view=items");
			String result = this.getHttpUtility().makeHttpRequest(getAPIBase()+"record/"+bibId+"/holdings?view=items","GET");

	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	
	    	Document doc = dBuilder.parse(new InputSource(new StringReader(result)));
	    	
	    	doc.getDocumentElement().normalize();
			
	    	NodeList items = doc.getElementsByTagName("item");
	    	
	    	// Data needed for button logic
	    	
	    	//make new request for each holding uri
	    	/*
	    	 * catalog = libcat
	    	my $borrowSid = "Borrow";
	    	my $holdSid   = "DocDel";
	    	my $recallSid = "recall";
	    	my $archSid   = "cushing"; 
	    	my $remoteSid = "remotestorage";
	    	my $inProcessSid = "InProcess";
	    	
	    	*/
	    	/* (from initial holdings request)
	    	 * marc record (item type)
	    	*<marcRecord><leader>00302cx  a22001094  4500</leader>
	    	*/
	    	
	    	/* Item Location
	    	 * <itemData id="32" name="permLocation" code="stk">Evans Library or Annex</itemData>
	    	 * <itemData id="0" name="tempLocation"/>
	    	 * <itemData id="32" name="location" code="stk">Evans Library or Annex</itemData>
	    	 */
	    	
	    	/* Item Type
	    	 * <itemData name="typeCode">4</itemData>
	    	 * <itemData name="typeDesc">normal</itemData>
	    	 */
	    	/*
	    	*<itemData name="itemStatus">Charged - Due on 2016-03-16</itemData>
	    	*<itemData name="itemStatusCode">2</itemData>
	    	*/

	    	int count = items.getLength();
	    	for (int i=0;i<count;i++) {
		    	Map<String,String> itemData = new HashMap<String,String>();
				String itemsResult = this.getHttpUtility().makeHttpRequest(items.item(i).getAttributes().getNamedItem("href").getTextContent(),"GET");
		    	doc = dBuilder.parse(new InputSource(new StringReader(itemsResult)));
		    	
		    	doc.getDocumentElement().normalize();
				
		    	NodeList itemDataNode = doc.getElementsByTagName("itemData");
		    	int itemDataCount = itemDataNode.getLength();
		    	System.out.println("\n\n\nThe "+itemDataCount+" Items for "+items.item(i).getAttributes().getNamedItem("href").getTextContent()+" are: ");
		    	for (int j=0;j<itemDataCount;j++) {
		    		itemData.put(itemDataNode.item(j).getAttributes().getNamedItem("name").getTextContent(),itemDataNode.item(j).getTextContent());
		    	}
		    	itemData.forEach((k,v) -> {
		    		System.out.println(k+": "+v);
		    	});
	    		
	    	}
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
