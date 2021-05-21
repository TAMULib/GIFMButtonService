package edu.tamu.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import edu.tamu.app.model.ButtonFormPresentation;
import edu.tamu.app.model.ButtonLinkPresentation;
import edu.tamu.app.model.ButtonPresentation;
import edu.tamu.app.model.CatalogHolding;
import edu.tamu.app.model.GetItForMeButton;
import edu.tamu.app.model.PersistedButton;
import edu.tamu.app.model.repo.PersistedButtonRepo;
import edu.tamu.app.utilities.sort.VolumeComparator;

/**
 * The GetItForMe engine.
 *
 * Registers eligible buttons from a configuration file.
 *
 * Tests holding items for button eligibility based on button configuration
 * rules
 *
 * Provides access to CatalogHoldings, JSON button data, and HTML buttons
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 * @author James Creel <jcreel@library.tamu.edu>
 * @author Michael Nichols <mnichols@tamu.edu>
 *
 */

@Service
public class GetItForMeService {
    @Value("${activeButtons}")
    private String[] activeButtons;

    @Value("${app.defaultButton.templateParameterKeys}")
    private String[] defaultTemplateParameterKeys;

    @Value("#{${app.defaultButton.fieldMap}}")
    private Map<String, String> defaultFieldMap;

    @Value("#{${app.defaultButton.SID}}")
    private Map<String,String> defaultSIDMap;

    @Value("${app.defaultButton.action}")
    private String defaultAction;

    @Value("${app.defaultButton.volumeField}")
    private String defaultVolumeField;

    @Value("${app.defaultButton.text}")
    private String defaultText;

    @Value("${app.defaultButton.threshold:100}")
    private int defaultThreshold;

    @Value("${app.buttons.patronGroupOverride:#{null}}")
    private String patronGroupOverride;

    @Value("${app.buttons.locationsOverride:#{null}}")
    private String[] locationsOverride;

    @Autowired
    Environment environment;

    @Autowired
    CatalogService catalogService;

    @Autowired
    private PersistedButtonRepo persistedButtonRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Registers and instantiates GetItForMe button implementations on app startup
     * based on the button configuration file
     *
     */
    @PostConstruct
    private void registerButtons() {
        // only register the buttons from configuration if the repo is empty
        long buttonCount = persistedButtonRepo.count();
        if (buttonCount == 0) {
            for (String activeButton : activeButtons) {
                String rawLocationCodes = environment.getProperty(activeButton + ".locationCodes");
                String[] itemTypeCodes = environment.getProperty(activeButton + ".itemTypeCodes", String[].class);
                String[] itemStatusCodes = environment.getProperty(activeButton + ".itemStatusCodes", String[].class);
                String linkText = environment.getProperty(activeButton + ".linkText");
                String SID = environment.getProperty(activeButton + ".SID");
                String[] templateParameterKeys = environment.getProperty(activeButton + ".templateParameterKeys",
                        String[].class);
                String templateUrl = environment.getProperty(activeButton + ".templateUrl");

                String recordType = environment.getProperty(activeButton + ".recordType");

                String cssClasses = environment.getProperty(activeButton + ".cssClasses");

                String catalogName = environment.getProperty(activeButton + ".catalog");

                String buttonName = environment.getProperty(activeButton + ".name");

                if (buttonName == null) {
                    buttonName = activeButton;
                }

                PersistedButton persistedButton = new PersistedButton();

                if (rawLocationCodes != null) {
                    persistedButton.setLocationCodes(rawLocationCodes.split(";"));
                }
                if (itemTypeCodes != null) {
                    persistedButton.setItemTypeCodes(itemTypeCodes);
                }
                if (itemStatusCodes != null) {
                    persistedButton.setItemStatusCodes(itemStatusCodes);
                }
                if (linkText != null) {
                    persistedButton.setLinkText(linkText);
                }
                if (SID != null) {
                    persistedButton.setSID(SID);
                }

                if (templateParameterKeys != null) {
                    persistedButton.setTemplateParameterKeys(templateParameterKeys);
                }

                if (templateUrl != null) {
                    persistedButton.setLinkTemplate(templateUrl);
                }

                if (recordType != null) {
                    persistedButton.setRecordType(recordType);
                }

                if (cssClasses != null) {
                    persistedButton.setCssClasses(cssClasses);
                }

                if (catalogName != null) {
                    persistedButton.setCatalogName(catalogName);
                }

                persistedButton.setName(buttonName);
                persistedButton.setActive(true);

                persistedButtonRepo.save(persistedButton);
            }
        }
    }

    public List<CatalogHolding> getHoldingsByBibId(String catalogName, String bibid) {
        return catalogService.getHoldingsByBibId(catalogName, bibid);
    }

    public CatalogHolding getHolding(String catalogName, String bibId, String holdingId) {
        return catalogService.getHolding(catalogName, bibId, holdingId);
    }

    public Map<String,String> getCatalogConfigurationByName(String catalogName) {
        return catalogService.getCatalogConfigurationByName(catalogName);
    }

    public List<GetItForMeButton> getRegisteredButtons() {
        List<? extends GetItForMeButton> buttons = new ArrayList<PersistedButton>();
        buttons = persistedButtonRepo.findAll();
        return (List<GetItForMeButton>) buttons;
    }

    public List<GetItForMeButton> getRegisteredButtons(String catalogName) {
        List<? extends GetItForMeButton> buttons = new ArrayList<PersistedButton>();
        buttons = persistedButtonRepo.findByCatalogName(catalogName);
        return (List<GetItForMeButton>) buttons;
    }

    /**
     * Gets a list of CatalogHolding by bibId, runs registered button test
     * eligibility for all the items in that holding, and builds and returns the
     * resulting button data, keyed by the holding's MFHD.
     *
     * @param catalogName
     * @param bibId
     * @return Map<String,List<Map<String,String>>>
     */

    public Map<String,ButtonPresentation> getButtonDataByBibId(String catalogName, String bibId) {
        List<CatalogHolding> catalogHoldings;
        catalogHoldings = this.getHoldingsByBibId(catalogName, bibId);
        if (catalogHoldings != null) {
            logger.debug("\n\nCATALOG HOLDINGS FOR " + bibId);

            Map<String, ButtonPresentation> presentableHoldings = new HashMap<String, ButtonPresentation>();

            // check each holding
            catalogHoldings.forEach(holding -> {
                logger.debug("MARC Record Leader: " + holding.getMarcRecordLeader());
                // we'll put the valid buttons here, keyed by their parent holding's MFHD
                // we want the MFHD key, even if we don't have any valid buttons for it, so
                // users can see that the MFHD was tested
                presentableHoldings.put(holding.getMfhd(), null);
                List<Map<String,String>> holdingButtons = new ArrayList<Map<String, String>>();
                //Path 1: if the holding has no items, check for an itemless button
                if (holding.getCatalogItems().size() == 0) {
                    for (GetItForMeButton button : this.getRegisteredButtons(catalogName)) {
                        //the 'purchase' button is the only one that can show up for holdings with no items
                        if (button.getActive() && button.getSID().equalsIgnoreCase("purchase") && button.fitsRecordType(holding.getMarcRecordLeader())
                                && button.fitsLocation(holding.getFallbackLocationCode())) {
                            logger.debug("Generating itemless button: "+button.getLinkText());
                            // used to build the button's link from the template parameter keys it provides

                            List<String> parameterKeys = button.getTemplateParameterKeys();
                            Map<String, String> parameters = new HashMap<String, String>();

                            for (String parameterKey : parameterKeys) {
                                if (parameterKey.equals("sid")) {
                                    parameters.put(parameterKey, getCatalogConfigurationByName(catalogName).get("sidPrefix")
                                            + ":" + button.getSID());
                                } else {
                                    parameters.put(parameterKey, null);
                                }
                            }

                            parameters = buildHoldingParameters(parameters, holding);

                            // generate the button data
                            Map<String, String> buttonContent = ButtonLinkPresentation.buildButtonProperties(parameters, button);

                            logger.debug("We want the itemless button with text: " + button.getLinkText());
                            logger.debug("It looks like: ");
                            logger.debug(buttonContent.get("linkHref"));

                            // add the button to the list for the holding's MFHD
                            holdingButtons.add(buttonContent);

                        }
                        if (holdingButtons.size() > 0) {
                            presentableHoldings.put(holding.getMfhd(), new ButtonLinkPresentation(holdingButtons));
                        }
                    }
                } else {
                    //Path 2: For holdings with lots of items, we generate a single form based button, with selectable items
                    if (holding.isLargeVolume() && holding.getCatalogItems().size() > defaultThreshold ) {
                        logger.debug("Generating the large volume button");
                        Map<String, String> defaultButtonContent = new HashMap<String, String>();

                        Map<String, String> parameters = new HashMap<String,String>();
                        List<String> parameterKeys = Arrays.asList(defaultTemplateParameterKeys);

                        for (String parameterKey : parameterKeys) {
                            parameters.put(parameterKey, null);
                        }

                        parameters = buildHoldingParameters(parameters, holding);

                        parameters.put("sid",getCatalogConfigurationByName(catalogName).get("sidPrefix") + ": "+defaultSIDMap.get(holding.getFallbackLocationCode()));
                        defaultButtonContent.put("form",ButtonFormPresentation.buildForm(holding.getCatalogItems(), defaultAction, defaultFieldMap, defaultVolumeField, defaultText, parameters));
                        holdingButtons.add(defaultButtonContent);

                        presentableHoldings.put(holding.getMfhd(), new ButtonFormPresentation(holdingButtons));
                    } else {
                        // Path 3: The 'normal' approach: Check all the items for each holding and create a button if a given item passes the tests
                        holding.getCatalogItems().forEach((itemIdentifier, itemData) -> {
                            logger.debug("Checking holding URI: " + itemIdentifier);
                            String currentLocation = null;
                            if (itemData.containsKey("tempLocationCode")) {
                                currentLocation = itemData.get("tempLocationCode");
                            } else if (itemData.containsKey("permLocationCode")) {
                                currentLocation = itemData.get("permLocationCode");
                            } else if (itemData.containsKey("locationCode")) {
                                currentLocation = itemData.get("locationCode");
                            } else {
                                currentLocation = holding.getFallbackLocationCode();
                            }

                            logger.debug("Current Location is: " + currentLocation);
                            //check if the global override blocks buttons for this item
                            if (!skipAllButtons(currentLocation, itemData)) {
                                // check all registered button for each item
                                for (GetItForMeButton button : this.getRegisteredButtons(catalogName)) {
                                    logger.debug("Analyzing: " + button.toString());

                                    String itemStatusCode = null;
                                    if (itemData.containsKey("itemStatusCode")) {
                                        itemStatusCode = itemData.get("itemStatusCode");
                                    } else if (itemData.containsKey("status")) {
                                        itemStatusCode = itemData.get("status");
                                    }

                                    logger.debug("Location: " + currentLocation + ": "
                                            + button.fitsLocation(currentLocation));
                                    logger.debug("TypeDesc: " + itemData.get("typeDesc") + ": "
                                            + button.fitsItemType(itemData.get("typeDesc")));
                                    logger.debug("Status: " + itemStatusCode + ": "
                                            + button.fitsItemStatus(itemStatusCode));

                                    // test the current item against the current GetItForMe button's requirements
                                    // for eligibility
                                    if (button.getActive()
                                            && button.fitsRecordType(holding.getMarcRecordLeader())
                                            && button.fitsLocation(currentLocation)
                                            && button.fitsItemType(itemData.get("typeDesc"))
                                            && button.fitsItemStatus(itemStatusCode)) {
                                        // used to build the button's link from the template parameter keys it provides
                                        List<String> parameterKeys = button.getTemplateParameterKeys();
                                        Map<String, String> parameters = new HashMap<String, String>();

                                        for (String parameterKey : parameterKeys) {
                                            if (parameterKey.equals("sid")) {
                                                String fullSid = (getCatalogConfigurationByName(catalogName) != null ? getCatalogConfigurationByName(catalogName).get("sidPrefix")
                                                        + ":":"") + button.getSID();
                                                parameters.put(parameterKey, fullSid);
                                            } else {
                                                parameters.put(parameterKey, itemData.get(parameterKey));
                                            }
                                        }
                                        parameters = buildHoldingParameters(parameters, holding);

                                        // generate the button data
                                        // for multi-volume holdings, enrich the linkText to indicate which volume the
                                        // button represents
                                        Map<String, String> buttonContent = new HashMap<String,String>();
                                        if (holding.isMultiVolume()) {
                                            logger.debug("Generating a multi volume button");
                                            parameters.put("edition", itemData.getOrDefault("enumeration","") + " " + itemData.getOrDefault("chron",""));
                                            buttonContent = ButtonLinkPresentation.buildMultiVolumeButtonProperties(parameters, button);
                                        } else {
                                            logger.debug("Generating a single item button");
                                            buttonContent = ButtonLinkPresentation.buildButtonProperties(parameters, button);
                                        }

                                        // generate unique link for the current button
                                        String linkHref = ButtonLinkPresentation.renderTemplate(parameters, button.getLinkTemplate());
                                        logger.debug("We want the button with text: " + button.getLinkText());
                                        logger.debug("It looks like: ");
                                        logger.debug(linkHref);

                                        buttonContent.put("linkHref", linkHref);
                                        buttonContent.put("cssClasses", "button-gifm" + ((button.getCssClasses() != null) ? " "+button.getCssClasses():""));
                                        //add the item's unique identifier to the corresponding button
                                        buttonContent.put("itemKey", itemIdentifier);

                                        // add the button to the list for the holding's MFHD
                                        //presentableHoldings.get(holding.getMfhd()).add(buttonContent);
                                        holdingButtons.add(buttonContent);
                                    } else {
                                        logger.debug("We should skip the button with text: " + button.getLinkText());
                                    }
                                }
                            }
                        });

                        if (holdingButtons.size() > 0) {
                            // for multi-volumes, get the items somewhat ordered by volume (there's no real
                            // definition of the order to work from)
                            if (holding.isMultiVolume()) {
                                Collections.sort(holdingButtons, new VolumeComparator());
                            }
                            presentableHoldings.put(holding.getMfhd(), new ButtonLinkPresentation(holdingButtons));
                        } else {
                            presentableHoldings.put(holding.getMfhd(), null);
                        }
                    }
                }
            });

            return presentableHoldings;
        }
        return null;
    }

    protected boolean skipAllButtons(String locationCode, Map<String,String> itemData) {
        boolean skipButtons = false;
        if (patronGroupOverride != null
                && (locationsOverride != null && locationsOverride.length > 0)
                && itemData.containsKey("patronGroupCode")
                && itemData.get("patronGroupCode").toString().contentEquals(patronGroupOverride)) {
            logger.debug("Matched patronGroupCode for button override");
            boolean matchesLocation = false;
            for (String locationOverride : locationsOverride) {
                if (locationCode.contains(locationOverride)) {
                    matchesLocation = true;
                    break;
                }
            }
            if (!matchesLocation) {
                logger.debug("Item is not in a valid override location. Skipping buttons.");
                skipButtons = true;
            } else {
                logger.debug("Item is in a valid override location. Proceeding with button tests.");
            }
        } else {
            logger.debug("Override is not active.");
        }
        return skipButtons;
    }

    private Map<String,String> buildHoldingParameters(Map<String,String> parameters, CatalogHolding holding) {
        // these template parameter keys are a special case, and come from the parent
        // holding, rather than the item data itself
        String[] getParameterFromHolding = { "title", "author", "publisher",
                "genre", "place", "year", "edition", "oclc", "mfhd", "recordId" };

        for (String parameterName : getParameterFromHolding) {
            if (parameters.containsKey(parameterName)) {
                try {
                    parameters.put(parameterName, holding.getValueByPropertyName(parameterName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //another special case: populate isxn with isbn data, fall back to issn if available
        if (holding.getIsbn() != null) {
            parameters.put("isxn", holding.getIsbn());
        } else if (holding.getIssn() != null) {
            parameters.put("isxn", holding.getIssn());
        }

        return parameters;
    }

}
