package edu.tamu.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.CatalogHolding;
import edu.tamu.app.model.CushingButton;
import edu.tamu.app.model.GetIt1WeekButton;
import edu.tamu.app.model.GetIt2DaysButton;
import edu.tamu.app.model.GetIt2DaysDocDelButton;
import edu.tamu.app.model.GetIt4DaysButton;
import edu.tamu.app.model.GetItForMeButton;
import edu.tamu.app.model.RecallItButton;

@Service
public class GetItForMeService {
	@Autowired
	private CatalogServiceFactory catalogServiceFactory;
	
	public String getButtonsByBibId(String bibId) {
		List<CatalogHolding> catalogHoldings = catalogServiceFactory.getOrCreateCatalogService("evans").getHoldingsByBibId(bibId);
		System.out.println("\n\nCATALOG HOLDINGS FOR "+bibId);
		
		List<GetItForMeButton> eligibleButtons = new ArrayList<GetItForMeButton>();
		eligibleButtons.add(new CushingButton());
		eligibleButtons.add(new GetIt2DaysButton());
		eligibleButtons.add(new GetIt4DaysButton());
		eligibleButtons.add(new GetIt1WeekButton());
		eligibleButtons.add(new RecallItButton());
		eligibleButtons.add(new GetIt2DaysDocDelButton());

		catalogHoldings.forEach(holding -> {
			System.out.println ("MARC Record Leader: "+holding.getMarcRecordLeader());
			//if configured, check for single item monograph
			//button.checkRecordType(marcRecord)
			holding.getCatalogItems().forEach((uri,items) -> {
				System.out.println("Checking: "+uri);
				for (GetItForMeButton button:eligibleButtons) {
					System.out.println(items.get("permLocationCode"));
					System.out.println(items.get("typeCode"));
					System.out.println(items.get("itemStatusCode"));
					List<String> parameterKeys = button.getTemplateParameterKeys();
					Map<String,String> parameters = new HashMap<String,String>();
					for (String parameterKey:parameterKeys) {
						parameters.put(parameterKey,items.get(parameterKey));
					}
					if (button.checkLocation(items.get("permLocationCode")) && button.checkItemType(items.get("typeCode")) && button.checkItemStatus(items.get("itemStatusCode"))) {
						System.out.println("We want the button with text: "+button.getLinkText());
						System.out.println("It looks like: ");
						System.out.println(button.getLinkTemplate(parameters));
					} else {
						System.out.println ("We should skip the button with text: "+button.getLinkText());
					}
				}
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
