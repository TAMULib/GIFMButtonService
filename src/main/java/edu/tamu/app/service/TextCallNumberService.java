package edu.tamu.app.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.CatalogHolding;

/**
 * The TextCallNumberService generates a Text A Call Number Button from holding data
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 *
 */

@Service
public class TextCallNumberService {

    @Autowired
    CatalogService catalogService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Map<String,String> getTextCallNumberButton(String catalogName, String bibId, String holdingId) {
        Map<String,String> textCallNumberButtonContent = new HashMap<String,String>();
        String locationName = "";
        String itemStatus = "";

        CatalogHolding holding = catalogService.getHolding(catalogName, bibId, holdingId);

        Iterator<Map.Entry<String, Map<String,String>>> itemsIterator = holding.getCatalogItems().entrySet().iterator();
        while(itemsIterator.hasNext()) {
            Map.Entry<String,Map<String,String>> entry = itemsIterator.next();
            Map<String,String> itemData = entry.getValue();
            itemStatus = itemData.get("itemStatus");
            if (catalogName.equals("evans") && holding.getFallbackLocationCode().isEmpty() && holding.getFallbackLocationCode().equals("stk") && (holding.getCallNumber().contains("THESIS") || holding.getCallNumber().contains("RECORD") || holding.getCallNumber().contains("DISSERTATION"))) {
                locationName = holding.getFallbackLocationCode();
                break;
            } else if (itemData.containsKey("tempLocationCode")) {
                locationName = itemData.get("tempLocation");
                break;
            } else if (itemData.containsKey("permLocationCode")) {
                locationName = itemData.get("permLocation");
            } else if (itemData.containsKey("locationCode")) {
                locationName = itemData.get("locationCode");
            } else {
                //we don't have access to a friendly location name at the holding level, so reuse location code
                locationName = holding.getFallbackLocationCode();
            }

        }

        textCallNumberButtonContent.put("title", holding.getTitle());
        textCallNumberButtonContent.put("call", holding.getCallNumber());
        textCallNumberButtonContent.put("loc", locationName);
        if (holding.getCatalogItems().size() == 1) {
            textCallNumberButtonContent.put("status", itemStatus);
        }

        logger.debug("*** Text A Call Number Button for holding: "+holdingId+" ***");
        textCallNumberButtonContent.forEach((k,v) -> {
            logger.debug(k+": "+v);
        });
        return textCallNumberButtonContent;
    }
}
