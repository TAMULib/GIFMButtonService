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
			//if configured, check for single item monograph
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
	
	
	// Data needed for button logic
	
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

}
