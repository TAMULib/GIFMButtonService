package edu.tamu.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import edu.tamu.app.model.CatalogHolding;

@Service
public class CatalogService {
    public List<CatalogHolding> getHoldingsByBibId(String catalogName, String bibid) {
        //TODO get holdings from the catalog service somehow
        return null;
    }

    public CatalogHolding getHolding(String catalogName, String bibId, String holdingId) {
        //TODO get an individual holding from the catalog service somehow
        return null;
    }

    public Map<String,String> getCatalogConfigurationByName(String catalogName) {
        //TODO there needs to be some sort of representation for GIFM only catalog properties like SID
        //      we can repurpose catalogs.json for this
        return null;
    }
}
