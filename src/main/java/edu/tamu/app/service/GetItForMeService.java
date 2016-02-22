package edu.tamu.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetItForMeService {
	@Autowired
	private CatalogServiceFactory catalogServiceFactory;
	
	public String getButtonsByBibId(String bibId) {
		System.out.println(catalogServiceFactory.getOrCreateCatalogService("evans").getHoldingsByBibId(bibId));

		return null;
	}
	
	//some logical button generating logic
}
