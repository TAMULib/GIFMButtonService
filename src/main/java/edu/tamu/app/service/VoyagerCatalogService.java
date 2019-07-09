package edu.tamu.app.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.tamu.app.model.CatalogHolding;
import edu.tamu.weaver.utility.HttpUtility;

/**
 * A CatalogService implementation for interfacing with the Voyager REST VXWS
 * api
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 * @author James Creel <jcreel@library.tamu.edu>
 *
 */

class VoyagerCatalogService extends AbstractCatalogService {
    private static final int MAX_ITEMS = 50;
    private static final int REQUEST_TIMEOUT = 120000;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private void appendMapValue(Map<String,String> map, String key, String newValue) {
        if (map.containsKey(key) && map.get(key) != null && !map.get(key).isEmpty()) {
            addMapValue(map, key, map.get(key) + newValue);
        } else {
            addMapValue(map, key, newValue);
        }
    }

    private void addMapValue(Map<String,String> map, String key, String newValue) {
        map.put(key, (newValue != null ? newValue:""));
    }

    /**
     * Fetches holdings from the Voyager API and translates them into
     * catalogHoldings
     *
     * @param bibId String
     *
     * @return List<CatalogHolding>
     *
     */
    @Override
    public List<CatalogHolding> getHoldingsByBibId(String bibId) {
        try {
            logger.debug("Asking for Record from: " + getAPIBase() + "record/" + bibId + "/?view=full");
            String recordResult = HttpUtility.makeHttpRequest(getAPIBase() + "record/" + bibId + "/?view=full", "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new InputSource(new StringReader(recordResult)));

            doc.getDocumentElement().normalize();
            NodeList dataFields = doc.getElementsByTagName("datafield");
            int dataFieldCount = dataFields.getLength();

            Map<String,String> recordValues = new HashMap<String,String>();
            Map<String,String> recordBackupValues = new HashMap<String,String>();

            String marcRecordLeader = doc.getElementsByTagName("leader").item(0).getTextContent();

            for (int i = 0; i < dataFieldCount; i++) {
                Node currentNode = dataFields.item(i);
                switch (currentNode.getAttributes().getNamedItem("tag").getTextContent()) {
                    case "022":
                        addMapValue(recordValues,"issn",currentNode.getChildNodes().item(0).getTextContent());
                        addMapValue(recordValues,"genre","journal");
                    break;
                    case "020":
                        addMapValue(recordValues,"isbn",currentNode.getChildNodes().item(0).getTextContent().split(" ")[0]);
                        addMapValue(recordValues,"genre","book");
                    break;
                    case "245":
                        if (currentNode.getChildNodes().item(1) != null) {
                            addMapValue(recordValues,"title", currentNode.getChildNodes().item(0).getTextContent()+currentNode.getChildNodes().item(1).getTextContent());
                        } else {
                            addMapValue(recordValues,"title", currentNode.getChildNodes().item(0).getTextContent());
                        }
                    break;
                    case "100":
                        addMapValue(recordValues,"author", currentNode.getChildNodes().item(0).getTextContent());
                    break;
                    case "264":
                        NodeList publisherDataNodes = currentNode.getChildNodes();
                        int childCount = publisherDataNodes.getLength();
                        for (int x=0;x<childCount;x++) {
                            switch (publisherDataNodes.item(x).getAttributes().getNamedItem("code").getTextContent()) {
                                case "a":
                                    appendMapValue(recordValues, "place",publisherDataNodes.item(x).getTextContent());
                                break;
                                case "b":
                                    appendMapValue(recordValues, "publisher",publisherDataNodes.item(x).getTextContent());
                                break;
                                case "c":
                                    if (!recordValues.containsKey("year") || (recordValues.get("year") == null || recordValues.get("year").length() == 0)) {
                                        addMapValue(recordValues,"year", publisherDataNodes.item(x).getTextContent());
                                    }
                                break;
                            }
                        }
                    break;
                    case "260":
                        NodeList dataNodes = currentNode.getChildNodes();
                        childCount = dataNodes.getLength();
                        for (int x=0;x<childCount;x++) {
                            switch (dataNodes.item(x).getAttributes().getNamedItem("code").getTextContent()) {
                                case "a":
                                    appendMapValue(recordBackupValues, "place",dataNodes.item(x).getTextContent());
                                break;
                                case "b":
                                    appendMapValue(recordBackupValues, "publisher", dataNodes.item(x).getTextContent());
                                break;
                                case "c":
                                    addMapValue(recordBackupValues,"year", recordBackupValues.get("year") + dataNodes.item(x).getTextContent());
                                break;
                            }
                        }
                    break;
                    case "250":
                        addMapValue(recordValues,"edition", currentNode.getChildNodes().item(0).getTextContent());
                    break;
                }
            }

            //apply backup values if needed and available
            Iterator<String> bpIterator = recordBackupValues.keySet().iterator();
            while (bpIterator.hasNext()) {
                String key = bpIterator.next();
                if (!recordValues.containsKey(key) || (recordValues.get(key) == null || recordValues.get(key).length() == 0)) {
                    addMapValue(recordValues,key,recordBackupValues.get(key));
                }
            }

            logger.debug("Asking for holdings from: " + getAPIBase() + "record/" + bibId + "/holdings?view=items");
            String result = HttpUtility.makeHttpRequest(getAPIBase() + "record/" + bibId + "/holdings?view=items",
                    "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);
            logger.debug("Received holdings from: " + getAPIBase() + "record/" + bibId + "/holdings?view=items");

            doc = dBuilder.parse(new InputSource(new StringReader(result)));

            doc.getDocumentElement().normalize();
            NodeList holdings = doc.getElementsByTagName("holding");
            int holdingCount = holdings.getLength();

            List<CatalogHolding> catalogHoldings = new ArrayList<CatalogHolding>();
            logger.debug("\n\nThe Holding Count: " + holdingCount);

            for (int i = 0; i < holdingCount; i++) {
                logger.debug(
                        "Current Holding: " + holdings.item(i).getAttributes().getNamedItem("href").getTextContent());
                NodeList childNodes = holdings.item(i).getChildNodes();
                int childCount = childNodes.getLength();
                logger.debug("The Count of Children: " + childCount);
                Map<String, Map<String, String>> catalogItems = new HashMap<String, Map<String, String>>();

                if (childNodes.item(1) == null) {
                    logger.debug("Skipping holding with no items");
                } else {
                    String mfhd = childNodes.item(0).getChildNodes().item(1).getTextContent();
                    String fallBackLocationCode = childNodes.item(1).getChildNodes().item(0).getTextContent();
                    logger.debug("MarcRecordLeader: " + marcRecordLeader);
                    logger.debug("MFHD: " + mfhd);
                    logger.debug("ISBN: " + recordValues.get("isbn"));
                    logger.debug("Item URL: " + childNodes.item(1).getAttributes().getNamedItem("href").getTextContent());
                    logger.debug("Fallback Location: " + fallBackLocationCode);
                    int itemCount = childCount <= 50 ? childCount:MAX_ITEMS;
                    for (int j = 0; j < itemCount; j++) {
                        if (childNodes.item(j).getNodeName() == "item") {
                            String itemResult = HttpUtility.makeHttpRequest(
                                    childNodes.item(j).getAttributes().getNamedItem("href").getTextContent(), "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);

                            logger.debug("Got Item details from: "
                                    + childNodes.item(j).getAttributes().getNamedItem("href").getTextContent());
                            doc = dBuilder.parse(new InputSource(new StringReader(itemResult)));
                            doc.getDocumentElement().normalize();
                            NodeList itemDataNode = doc.getElementsByTagName("itemData");

                            int itemDataCount = itemDataNode.getLength();
                            Map<String, String> itemData = new HashMap<String, String>();
                            for (int l = 0; l < itemDataCount; l++) {
                                if (itemDataNode.item(l).getAttributes().getNamedItem("code") != null) {
                                    itemData.put(
                                            itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent()
                                                    + "Code",
                                            itemDataNode.item(l).getAttributes().getNamedItem("code").getTextContent());
                                }
                                itemData.put(itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent(),
                                        itemDataNode.item(l).getTextContent());
                            }
                            catalogItems.put(childNodes.item(j).getAttributes().getNamedItem("href").getTextContent(),
                                    itemData);
                            //sleep for a moment between item requests to avoid triggering a 429 from the Voyager API
                            try {
                                TimeUnit.MILLISECONDS.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    catalogHoldings.add(new CatalogHolding(marcRecordLeader, mfhd, recordValues.get("issn"), recordValues.get("isbn"), recordValues.get("title"), recordValues.get("author"), recordValues.get("publisher"),
                            recordValues.get("place"), recordValues.get("year"), recordValues.get("genre"), recordValues.get("edition"), fallBackLocationCode, new HashMap<String, Map<String, String>>(catalogItems)));
                    catalogItems.clear();
                }
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
