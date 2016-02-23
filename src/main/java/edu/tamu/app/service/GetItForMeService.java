package edu.tamu.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.CatalogHolding;

@Service
public class GetItForMeService {
	@Autowired
	private CatalogServiceFactory catalogServiceFactory;
	
	public String getButtonsByBibId(String bibId) {
		List<CatalogHolding> catalogHoldings = catalogServiceFactory.getOrCreateCatalogService("evans").getHoldingsByBibId(bibId);
		System.out.println("\n\nCATALOG HOLDINGS FOR "+bibId);
		catalogHoldings.forEach(holding -> {
			System.out.println ("MARC Record Leader: "+holding.getMarcRecordLeader());
			holding.getCatalogItems().forEach((uri,items) -> {
				System.out.println("URI: "+uri);
				items.forEach((k,v) -> {
					System.out.println(k+": "+v);
				});
			});
		});
		
		
		return null;
	}
	
	//some logical button generating logic
}
