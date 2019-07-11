package edu.tamu.app.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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

    @Autowired
    private CatalogServiceFactory catalogServiceFactory;

    @Value("${activeButtons}")
    private String[] activeButtons;

    @Autowired
    Environment environment;

    @Autowired
    private PersistedButtonRepo persistedButtonRepo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Get a list of CatalogHolding as provided by the CatalogServiceFactory
     *
     * @param catalogName
     * @param bibId
     * @return List<CatalogHolding>
     */
    public List<CatalogHolding> getHoldingsByBibId(String catalogName, String bibId) {
        return getCatalogServiceByName(catalogName).getHoldingsByBibId(bibId);
    }

    protected CatalogService getCatalogServiceByName(String catalogName) {
        return catalogServiceFactory.getOrCreateCatalogService(catalogName);
    }

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
                Integer[] itemStatusCodes = environment.getProperty(activeButton + ".itemStatusCodes", Integer[].class);
                String linkText = environment.getProperty(activeButton + ".linkText");
                String SID = environment.getProperty(activeButton + ".SID");
                String[] templateParameterKeys = environment.getProperty(activeButton + ".templateParameterKeys",
                        String[].class);
                String templateUrl = environment.getProperty(activeButton + ".templateUrl");

                String recordType = environment.getProperty(activeButton + ".recordType");

                String cssClasses = environment.getProperty(activeButton + ".cssClasses");

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

                persistedButton.setName(buttonName);
                persistedButton.setActive(true);

                persistedButtonRepo.save(persistedButton);
            }
        }
    }

    public List<GetItForMeButton> getRegisteredButtons() {
        List<? extends GetItForMeButton> buttons = new ArrayList<PersistedButton>();
        buttons = persistedButtonRepo.findAll();
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

    public Map<String, List<Map<String, String>>> getButtonsByBibId(String catalogName, String bibId) {
        List<CatalogHolding> catalogHoldings = this.getHoldingsByBibId(catalogName, bibId);
        if (catalogHoldings != null) {
            logger.debug("\n\nCATALOG HOLDINGS FOR " + bibId);

            Map<String, List<Map<String, String>>> validButtons = new HashMap<String, List<Map<String, String>>>();

            // check each holding
            catalogHoldings.forEach(holding -> {
                logger.debug("MARC Record Leader: " + holding.getMarcRecordLeader());
                // we'll put the valid buttons here, keyed by their parent holding's MFHD
                // we want the MFHD key, even if we don't have any valid buttons for it, so
                // users can see that the MFHD was tested
                validButtons.put(holding.getMfhd(), new ArrayList<Map<String, String>>());

                //if the holding has no items, fallback to a generic button
                if (holding.getCatalogItems().size() >= 0) {
                    Map<String, String> parameters = new HashMap<String,String>();
                    Map<String, String> defaultButtonContent = new HashMap<String, String>();
                    List<String> parameterKeys = Arrays.asList("title", "author", "publisher", "genre", "place", "year", "edition", "isxn");
                    for (String parameterKey : parameterKeys) {
                        parameters.put(parameterKey, null);
                    }
                    parameters = buildHoldingParameters(parameters, holding);
                    defaultButtonContent.put("linkText", "Get It For Me");
                    defaultButtonContent.put("linkHref",generateLinkHref(parameters, "getitforme.library.tamu.edu/illiad/EvansLocal/openurl.asp?Action=10&Form=30&LoanAuthor={author}&LoanPublisher={publisher}&sid="+getCatalogServiceByName(catalogName).getSidPrefix()+"&LoanTitle={title}&isxn={isxn}&genre={genre}&LoanDate={year}&LoanEdition={edition}&LoanPlace={place}"));
                    defaultButtonContent.put("cssClasses", "button-gifm");
                    validButtons.get(holding.getMfhd()).add(defaultButtonContent);
                } else {
                    // check all the items for each holding
                    holding.getCatalogItems().forEach((uri, itemData) -> {
                        logger.debug("Checking holding URI: " + uri);
                        // check all registered button for each item
                        for (GetItForMeButton button : this.getRegisteredButtons()) {
                            logger.debug("Analyzing: " + button.toString());
                            String currentLocation = null;
                            if (itemData.containsKey("tempLocationCode")) {
                                currentLocation = itemData.get("tempLocationCode");
                            } else if (itemData.containsKey("permLocationCode")) {
                                currentLocation = itemData.get("permLocationCode");
                            } else {
                                currentLocation = holding.getFallbackLocationCode();
                            }

                            logger.debug("Location: " + currentLocation + ": "
                                    + button.fitsLocation(itemData.get("permLocationCode")));
                            logger.debug("TypeDesc: " + itemData.get("typeDesc") + ": "
                                    + button.fitsItemType(itemData.get("typeDesc")));
                            logger.debug("Status: " + itemData.get("itemStatusCode") + ": "
                                    + button.fitsItemStatus(Integer.parseInt(itemData.get("itemStatusCode"))));

                            // test the current item against the current GetItForMe button's requirements
                            // for eligibility
                            if (button.getActive()
                                    && button.fitsRecordType(holding.getMarcRecordLeader())
                                    && button.fitsLocation(currentLocation)
                                    && button.fitsItemType(itemData.get("typeDesc"))
                                    && button.fitsItemStatus(Integer.parseInt(itemData.get("itemStatusCode")))) {
                                // used to build the button's link from the template parameter keys it provides
                                List<String> parameterKeys = button.getTemplateParameterKeys();
                                Map<String, String> parameters = new HashMap<String, String>();

                                for (String parameterKey : parameterKeys) {
                                    if (parameterKey.equals("sid")) {
                                        parameters.put(parameterKey, getCatalogServiceByName(catalogName).getSidPrefix()
                                                + ":" + button.getSID());
                                    } else {
                                        parameters.put(parameterKey, itemData.get(parameterKey));
                                    }
                                }
                                parameters = buildHoldingParameters(parameters, holding);

                                // generate the button data
                                Map<String, String> buttonContent = new HashMap<String, String>();
                                // for multi-volume holdings, enrich the linkText to indicate which volume the
                                // button represents
                                if (holding.isMultiVolume()) {
                                    logger.debug("Generating a multi volume button");
                                    parameters.put("edition", itemData.get("enumeration") + " " + itemData.get("chron"));
                                    buttonContent.put("linkText", itemData.get("enumeration") + " " + itemData.get("chron")
                                            + " | " + button.getLinkText());
                                } else {
                                    logger.debug("Generating a single item button");
                                    buttonContent.put("linkText", button.getLinkText());
                                }

                                // generate unique link for the current button
                                String linkHref = generateLinkHref(parameters, button.getLinkTemplate());
                                logger.debug("We want the button with text: " + button.getLinkText());
                                logger.debug("It looks like: ");
                                logger.debug(linkHref);

                                buttonContent.put("linkHref", linkHref);
                                buttonContent.put("cssClasses", "button-gifm " + button.getCssClasses());
                                // add the button to the list for the holding's MFHD
                                validButtons.get(holding.getMfhd()).add(buttonContent);
                            } else {
                                logger.debug("We should skip the button with text: " + button.getLinkText());
                            }
                        }
                        // for multi-volumes, get the items somewhat ordered by volume (there's no real
                        // definition of the order to work from)
                        if (holding.isMultiVolume()) {
                            Collections.sort(validButtons.get(holding.getMfhd()), new VolumeComparator());
                        }
                    });
                }
            });
            return validButtons;
        }
        return null;
    }

    private String generateLinkHref(Map<String,String> parameters, String linkHref) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            try {
                linkHref = linkHref.replace("{" + entry.getKey() + "}", URLEncoder.encode((entry.getValue() != null) ? entry.getValue():"", StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return linkHref;
    }

    private Map<String,String> buildHoldingParameters(Map<String,String> parameters, CatalogHolding holding) {
        // these template parameter keys are a special case, and come from the parent
        // holding, rather than the item data itself
        String[] getParameterFromHolding = { "title", "author", "publisher",
                "genre", "place", "year", "edition" };

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
