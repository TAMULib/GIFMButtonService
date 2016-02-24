package edu.tamu.app.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.model.CatalogHolding;

class VoyagerCatalogService extends AbstractCatalogService {
	@Autowired
	private ObjectMapper objectMapper;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	//private Map<String,Map<String,String>> catalogs = new HashMap<String,Map<String,String>>();
	
	public List<CatalogHolding> getHoldingsByBibId(String bibId) {
		try {
			logger.debug("Asking for holdings from: "+getAPIBase()+"record/"+bibId+"/holdings?view=items");
			String result = this.getHttpUtility().makeHttpRequest(getAPIBase()+"record/"+bibId+"/holdings?view=items","GET");

	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	
	    	Document doc = dBuilder.parse(new InputSource(new StringReader(result)));
	    	
	    	doc.getDocumentElement().normalize();
			NodeList holdings = doc.getElementsByTagName("holding");
			int holdingCount = holdings.getLength();
			
			List<CatalogHolding> catalogHoldings = new ArrayList<CatalogHolding>();

			for (int i=0;i<holdingCount;i++) {
				NodeList childNodes = holdings.item(i).getChildNodes();
				int childCount = childNodes.getLength();
				
				String marcRecordLeader = null;
				String mfhd = null;
				Map<String,Map<String,String>> catalogItem = new HashMap<String,Map<String,String>>();
				for (int j=0;j<childCount;j++) {
					Node child = childNodes.item(j);
					if (child.getNodeName().equals("marcRecord")) {
						marcRecordLeader = child.getFirstChild().getTextContent();
						mfhd = child.getChildNodes().item(1).getTextContent();
					} else if (child.getNodeName().equals("item")) {
						NodeList items = child.getChildNodes();
						int count = items.getLength();
						for (int k=0;k<count;k++) {
							Map<String,String> itemData = new HashMap<String,String>();
							String itemsResult = this.getHttpUtility().makeHttpRequest(child.getAttributes().getNamedItem("href").getTextContent(),"GET");
							doc = dBuilder.parse(new InputSource(new StringReader(itemsResult)));
							doc.getDocumentElement().normalize();
							NodeList itemDataNode = doc.getElementsByTagName("itemData");

							int itemDataCount = itemDataNode.getLength();
							for (int l=0;l<itemDataCount;l++) {
								if (itemDataNode.item(l).getAttributes().getNamedItem("code") != null) {
									itemData.put(itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent()+"Code",itemDataNode.item(l).getAttributes().getNamedItem("code").getTextContent());
								}
								itemData.put(itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent(),itemDataNode.item(l).getTextContent());
							}
							catalogItem.put(child.getAttributes().getNamedItem("href").getTextContent(),itemData);
						}
					}
				}
				catalogHoldings.add(new CatalogHolding(marcRecordLeader,mfhd,catalogItem));
				catalogItem = null;
	    		marcRecordLeader = null;
			}
			return catalogHoldings;
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
