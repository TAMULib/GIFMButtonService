package edu.tamu.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.BorrowItNowButton;
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
	
	public Map<String,List<Map<String,String>>> getButtonsByBibId(String bibId) {
		List<CatalogHolding> catalogHoldings = catalogServiceFactory.getOrCreateCatalogService("evans").getHoldingsByBibId(bibId);
		System.out.println("\n\nCATALOG HOLDINGS FOR "+bibId);
		
		List<GetItForMeButton> eligibleButtons = new ArrayList<GetItForMeButton>();
		Map<String,List<Map<String,String>>> validButtons = new HashMap<String,List<Map<String,String>>>();
		
		eligibleButtons.add(new CushingButton());
		eligibleButtons.add(new GetIt2DaysButton());
		eligibleButtons.add(new GetIt4DaysButton());
		eligibleButtons.add(new GetIt1WeekButton());
		eligibleButtons.add(new RecallItButton());
		eligibleButtons.add(new GetIt2DaysDocDelButton());
		eligibleButtons.add(new BorrowItNowButton());
		
		catalogHoldings.forEach(holding -> {
//			System.out.println ("MARC Record Leader: "+holding.getMarcRecordLeader());
			//if configured, check for single item monograph
			//button.checkRecordType(marcRecord)
			validButtons.put(holding.getMfhd(), new ArrayList<Map<String,String>>());
			holding.getCatalogItems().forEach((uri,items) -> {
				System.out.println("Checking: "+uri);
				for (GetItForMeButton button:eligibleButtons) {
					System.out.println ("Analyzing: "+button.toString());

					System.out.println("Location: "+items.get("permLocationCode")+": "+button.checkLocation(items.get("permLocationCode")));
					System.out.println("TypeDesc: "+items.get("typeDesc")+": "+button.checkItemType(items.get("typeDesc")));
					System.out.println("Status: "+items.get("itemStatusCode")+": "+button.checkItemStatus(Integer.parseInt(items.get("itemStatusCode"))));
					List<String> parameterKeys = button.getTemplateParameterKeys();
					Map<String,String> parameters = new HashMap<String,String>();
					for (String parameterKey:parameterKeys) {
						parameters.put(parameterKey,items.get(parameterKey));
					}
					//ToDo ISBN will need to either be passed in as an argument or found through another API
					if (parameters.containsKey("isbn")) {
						parameters.put("isbn", "placeHolderValue");
					}
					if (button.checkLocation(items.get("permLocationCode")) && button.checkItemType(items.get("typeDesc")) && button.checkItemStatus(Integer.parseInt(items.get("itemStatusCode")))) {
						System.out.println("We want the button with text: "+button.getLinkText());
						System.out.println("It looks like: ");
						System.out.println(button.getLinkTemplate(parameters));
						Map<String,String> buttonContent = new HashMap<String,String>();
						buttonContent.put("linkText",button.getLinkText());
						buttonContent.put("linkHref",button.getLinkTemplate(parameters));
//						validButtons.put(holding.getMfhd(), buttonContent);
						validButtons.get(holding.getMfhd()).add(buttonContent);
					} else {
						System.out.println ("We should skip the button with text: "+button.getLinkText());
					}
					System.out.println("\n\n");
				}
			});
		});
		
		
		return validButtons;
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
