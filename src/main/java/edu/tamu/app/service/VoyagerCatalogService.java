package edu.tamu.app.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.w3c.dom.NamedNodeMap;
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
    private static final List<String> LARGE_VOLUME_LOCATIONS = Arrays.asList("rs,hdr","rs,jlf");
    private static final int LARGE_VOLUME_ITEM_LIMIT = 10;
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

    protected Map<String,String> buildCoreRecord(String bibId) {
        logger.debug("Asking for Record from: " + getAPIBase() + "record/" + bibId + "/?view=full");
        try {
            String recordResult = HttpUtility.makeHttpRequest(getAPIBase() + "record/" + bibId + "/?view=full", "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new InputSource(new StringReader(recordResult)));

            doc.getDocumentElement().normalize();

            NodeList dataFields = doc.getElementsByTagName("datafield");
            int dataFieldCount = dataFields.getLength();

            Map<String,String> recordValues = new HashMap<String,String>();
            Map<String,String> recordBackupValues = new HashMap<String,String>();

            addMapValue(recordValues, "marcRecordLeader", doc.getElementsByTagName("leader").item(0).getTextContent());
            NodeList controlFields = doc.getElementsByTagName("controlfield");
            int controlFieldsCount = controlFields.getLength();

            for (int i = 0; i < controlFieldsCount; i++) {
                Node currentControlNode = controlFields.item(i);
                if (currentControlNode.getAttributes().getNamedItem("tag") != null && currentControlNode.getAttributes().getNamedItem("tag").getTextContent().contentEquals("001")) {
                    addMapValue(recordValues,"recordId",currentControlNode.getChildNodes().item(0).getTextContent());
                }
            }

            for (int i = 0; i < dataFieldCount; i++) {
                Node currentNode = dataFields.item(i);
                NodeList dataNodes = currentNode.getChildNodes();
                switch (currentNode.getAttributes().getNamedItem("tag").getTextContent()) {
                    case "022":
                        if (dataNodes.item(0).getAttributes().getNamedItem("code").getTextContent().equals("a")) {
                            addMapValue(recordValues,"issn",dataNodes.item(0).getTextContent());
                        }
                        addMapValue(recordValues,"genre","journal");
                    break;
                    case "020":
                        if (dataNodes.item(0).getAttributes().getNamedItem("code").getTextContent().equals("a")) {
                            addMapValue(recordValues,"isbn",dataNodes.item(0).getTextContent().split(" ")[0]);
                        }
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
                        int childCount = dataNodes.getLength();
                        for (int x=0;x<childCount;x++) {
                            switch (dataNodes.item(x).getAttributes().getNamedItem("code").getTextContent()) {
                                case "a":
                                    appendMapValue(recordValues, "place",dataNodes.item(x).getTextContent());
                                break;
                                case "b":
                                    appendMapValue(recordValues, "publisher",dataNodes.item(x).getTextContent());
                                break;
                                case "c":
                                    if (!recordValues.containsKey("year") || (recordValues.get("year") == null || recordValues.get("year").length() == 0)) {
                                        addMapValue(recordValues,"year", dataNodes.item(x).getTextContent());
                                    }
                                break;
                            }
                        }
                    break;
                    case "260":
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
                    case "035":
                        if (dataNodes.item(0).getAttributes().getNamedItem("code").getTextContent().equals("a")) {
                            addMapValue(recordValues,"oclc",currentNode.getChildNodes().item(0).getTextContent());
                        }
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
            return recordValues;
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

    Map<String,String> buildCoreHolding(Node holdingNode) {
        Map<String,String> holdingValues = new HashMap<String,String>();

        NodeList childNodes = holdingNode.getChildNodes();
        addMapValue(holdingValues, "mfhd", childNodes.item(0).getChildNodes().item(1).getTextContent());
        String fallbackLocationCode = "";
        String callNumber = "";


        NodeList marcRecordNodes = childNodes.item(0).getChildNodes();
        int marcRecordCount = marcRecordNodes.getLength();

        for (int j = 0; j < marcRecordCount; j++) {
            Node marcRecordNode = marcRecordNodes.item(j);
            NodeList subfieldNodes = marcRecordNode.getChildNodes();
            int subfieldCount = subfieldNodes.getLength();
            if (marcRecordNode.getNodeName().contentEquals("datafield") && marcRecordNode.getAttributes().getNamedItem("tag") != null && marcRecordNode.getAttributes().getNamedItem("tag").getTextContent().equals("852")) {
                for (int k = 0; k < subfieldCount; k++) {
                    Node subfieldNode = subfieldNodes.item(k);
                    if (subfieldNode.getAttributes().getNamedItem("code") != null) {
                        if (subfieldNode.getAttributes().getNamedItem("code").getTextContent().equals("b")) {
                            fallbackLocationCode = subfieldNode.getTextContent();
                        } else if (subfieldNode.getAttributes().getNamedItem("code").getTextContent().equals("h") || subfieldNode.getAttributes().getNamedItem("code").getTextContent().equals("i") ) {
                            callNumber += subfieldNode.getTextContent();
                        }
                    }
                }
            }
        }

        int childCount = childNodes.getLength();
        logger.debug("The Count of Children: " + childCount);

        Boolean validLargeVolume = false;
        if (childCount-1 > LARGE_VOLUME_ITEM_LIMIT) {
            for (String location : LARGE_VOLUME_LOCATIONS) {
                if (holdingValues.get("fallbackLocationCode").equals(location)) {
                    validLargeVolume = true;
                    break;
                }
            }
        }
        addMapValue(holdingValues,"fallbackLocationCode", fallbackLocationCode);
        addMapValue(holdingValues,"callNumber", callNumber);
        addMapValue(holdingValues,"validLargeVolume",validLargeVolume.toString());
        return holdingValues;
    }

    protected Map<String,String> buildCoreItem(Node itemNode) {
        NodeList itemDataNode = itemNode.getChildNodes();

        int itemDataCount = itemDataNode.getLength();
        Map<String, String> itemData = new HashMap<String, String>();
        for (int l = 0; l < itemDataCount; l++) {
            if (itemDataNode.item(l).getAttributes().getNamedItem("code") != null) {
                itemData.put(
                        itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent()
                                + "Code",
                        itemDataNode.item(l).getAttributes().getNamedItem("code").getTextContent());
                logger.debug(itemDataNode.item(l).getAttributes().getNamedItem("code").getTextContent()+"Code",itemDataNode.item(l).getAttributes().getNamedItem("code").getTextContent());
            }
            itemData.put(itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent(),
                    itemDataNode.item(l).getTextContent());
            logger.debug(itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent(),itemDataNode.item(l).getAttributes().getNamedItem("name").getTextContent());
        }
        return itemData;
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
            Map<String,String> recordValues = buildCoreRecord(bibId);

            logger.debug("Asking for holdings from: " + getAPIBase() + "record/" + bibId + "/holdings?view=items");
            String result = HttpUtility.makeHttpRequest(getAPIBase() + "record/" + bibId + "/holdings?view=items",
                    "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);
            logger.debug("Received holdings from: " + getAPIBase() + "record/" + bibId + "/holdings?view=items");

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new InputSource(new StringReader(result)));

            doc.getDocumentElement().normalize();
            NodeList holdings = doc.getElementsByTagName("holding");
            int holdingCount = holdings.getLength();

            List<CatalogHolding> catalogHoldings = new ArrayList<CatalogHolding>();
            logger.debug("\n\nThe Holding Count: " + holdingCount);

            for (int i = 0; i < holdingCount; i++) {
                logger.debug("Current Holding: " + holdings.item(i).getAttributes().getNamedItem("href").getTextContent());
                Map<String,String> holdingValues = buildCoreHolding(holdings.item(i));

                logger.debug("MarcRecordLeader: " + recordValues.get("marcRecordLeader"));
                logger.debug("MFHD: " + holdingValues.get("mfhd"));
                logger.debug("ISBN: " + recordValues.get("isbn"));
                logger.debug("Fallback Location: " + holdingValues.get("fallbackLocationCode"));
                logger.debug("Call Number: " + holdingValues.get("callNumber"));

                Boolean validLargeVolume = Boolean.valueOf(holdingValues.get("validLargeVolume"));

                logger.debug("Valid Large Volume: "+ validLargeVolume);

                Map<String, Map<String, String>> catalogItems = new HashMap<String, Map<String, String>>();

                NodeList childNodes = holdings.item(i).getChildNodes();
                int childCount = childNodes.getLength();

                if (validLargeVolume) {
                    //when we have a lot of items and it's a large volume candidate, just use the item data that came with the holding response, even though it's incomplete data
                    for (int j = 0; j < childCount; j++) {
                        if (childNodes.item(j) != null && childNodes.item(j).getNodeName() == "item") {
                            catalogItems.put(childNodes.item(j).getAttributes().getNamedItem("href").getTextContent(), buildCoreItem(childNodes.item(j)));
                        }
                    }
                } else {
                    if (childNodes.item(1) != null) {
                        logger.debug("Item URL: " + childNodes.item(1).getAttributes().getNamedItem("href").getTextContent());
                    }

                    for (int j = 0; j < childCount; j++) {
                        if (childNodes.item(j) != null && childNodes.item(j).getNodeName() == "item") {
                            String itemResult = HttpUtility.makeHttpRequest(
                                    childNodes.item(j).getAttributes().getNamedItem("href").getTextContent(), "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);

                            logger.debug("Got Item details from: "
                                    + childNodes.item(j).getAttributes().getNamedItem("href").getTextContent());
                            doc = dBuilder.parse(new InputSource(new StringReader(itemResult)));
                            doc.getDocumentElement().normalize();
                            NodeList itemNodes = doc.getElementsByTagName("item");

                            int itemNodesCount = itemNodes.getLength();
                            for (int l=0;l<itemNodesCount;l++) {
                                catalogItems.put(childNodes.item(j).getAttributes().getNamedItem("href").getTextContent(),
                                        buildCoreItem(itemNodes.item(l)));
                            }
                            //sleep for a moment between item requests to avoid triggering a 429 from the Voyager API
                            try {
                                TimeUnit.MILLISECONDS.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                catalogHoldings.add(new CatalogHolding(recordValues.get("marcRecordLeader"), holdingValues.get("mfhd"), recordValues.get("issn"), recordValues.get("isbn"), recordValues.get("title"), recordValues.get("author"), recordValues.get("publisher"),
                        recordValues.get("place"), recordValues.get("year"), recordValues.get("genre"), recordValues.get("edition"), holdingValues.get("fallbackLocationCode"), recordValues.get("oclc"), recordValues.get("recordId"), holdingValues.get("callNumber"), validLargeVolume, new HashMap<String, Map<String, String>>(catalogItems)));
                catalogItems.clear();
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

    @Override
    public CatalogHolding getHolding(String bibId, String holdingId) {
        logger.debug("Asking for holding from: " + getAPIBase() + "record/" + bibId + "/holdings?view=items");
        try {
            Map<String,String> recordValues = buildCoreRecord(bibId);
            String result = HttpUtility.makeHttpRequest(getAPIBase() + "record/" + bibId + "/holdings?view=items",
                    "GET", Optional.empty(), Optional.empty(), REQUEST_TIMEOUT);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new InputSource(new StringReader(result)));

            doc.getDocumentElement().normalize();

            Node holdingNode = null;
            NodeList controlFieldNodes = doc.getElementsByTagName("controlfield");
            for (int i=0; i<controlFieldNodes.getLength();i++) {
                Node controlFieldNode = controlFieldNodes.item(i);
                if (controlFieldNode != null) {
                    NamedNodeMap controlFieldAttributes = controlFieldNode.getAttributes();
                    for (int j=0; j<controlFieldAttributes.getLength();j++) {
                        if (controlFieldAttributes.item(j) != null && controlFieldAttributes.item(j).getNodeName() == "tag" && controlFieldAttributes.item(j).getTextContent().equals("001")) {
                            if (controlFieldNode.getTextContent().equals(holdingId)) {
                                holdingNode = controlFieldNode.getParentNode().getParentNode();
                            }
                        }
                    }
                }
            }

            Map<String,String> holdingValues = buildCoreHolding(holdingNode);

            Map<String, Map<String, String>> catalogItems = new HashMap<String, Map<String, String>>();

            NodeList childNodes = holdingNode.getChildNodes();
            int childCount = childNodes.getLength();

            for (int j = 0; j < childCount; j++) {
                if (childNodes.item(j) != null && childNodes.item(j).getNodeName() == "item") {
                    catalogItems.put(childNodes.item(j).getAttributes().getNamedItem("href").getTextContent(), buildCoreItem(childNodes.item(j)));
                }
            }

            return new CatalogHolding(recordValues.get("marcRecordLeader"), holdingValues.get("mfhd"), recordValues.get("issn"), recordValues.get("isbn"), recordValues.get("title"), recordValues.get("author"), recordValues.get("publisher"),
                    recordValues.get("place"), recordValues.get("year"), recordValues.get("genre"), recordValues.get("edition"), holdingValues.get("fallbackLocationCode"), recordValues.get("oclc"), recordValues.get("recordId"), holdingValues.get("callNumber"), Boolean.valueOf(holdingValues.get("validLargeVolume")), new HashMap<String, Map<String, String>>(catalogItems));

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
