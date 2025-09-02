package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import edu.tamu.app.model.ButtonPresentation;
import edu.tamu.app.service.GetItForMeService;
import edu.tamu.app.service.MapService;
import edu.tamu.app.service.SfxService;
import edu.tamu.app.service.TextCallNumberService;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/catalog-access")
public class GetItForMeController {

    @Value("${app.publicCatalogUrl:}")
    private String publicCatalogUrl;

    @Autowired
    private GetItForMeService getItForMeService;

    @Autowired
    private TextCallNumberService textCallNumberService;

    @Autowired
    private SfxService sfxService;

    @Autowired
    private MapService mapService;

    /**
     * Provides fully formatted HTML buttons, keyed by item MFHD
     * @param String catalogName (optional)
     * @param String bibId
     * @return
     */
    @RequestMapping("/get-html-buttons")
    public ApiResponse getHtmlButtonsByBibId(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId) {
        Map<String, ButtonPresentation> presentableHoldings = getItForMeService.getButtonDataByBibId(catalogName, bibId);
        if (presentableHoldings != null) {
            Map<String,List<String>> buttonContents = new HashMap<>();
            for (Map.Entry<String, ButtonPresentation> entry : presentableHoldings.entrySet()) {
                if (entry.getValue() != null) {
                    buttonContents.put(entry.getKey(), entry.getValue().buildPresentation());
                } else {
                    buttonContents.put(entry.getKey(), new ArrayList<String>());
                }
            }
            return new ApiResponse(SUCCESS,buttonContents);
        } else {
            return new ApiResponse(ERROR,"Error processing Catalog or Holding");
        }
    }

    /**
     * Provides the raw button data in JSON format, keyed by MFHD.
     * @param String catalogName (optional)
     * @param String bibId
     * @return
     */
    @RequestMapping("/get-buttons")
    public ApiResponse getButtonsByBibId(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId, @RequestParam(value="verbose",defaultValue="false") boolean verbose) {
        Map<String,ButtonPresentation> buttonData = getItForMeService.getButtonDataByBibId(catalogName, bibId, verbose);
        if (buttonData != null) {
            return new ApiResponse(SUCCESS, buttonData);
        } else {
            return new ApiResponse(ERROR, "Error processing Catalog or Holding");
        }
    }

    private boolean testIsCushing(Map<String, String> button) {
        boolean cssClassIsCushing = button.get("cssClasses") != null && button.get("cssClasses").contains("btn_cushing");
        boolean linkHrefIsCushing = button.get("linkHref") != null && button.get("linkHref").contains("aeon.library.tamu.edu");
        return cssClassIsCushing && linkHrefIsCushing;
    }

    private String buildRedirectUrl(String baseUrl) {
        return baseUrl.startsWith("http") ? baseUrl : "https://" + baseUrl;
    }

    private String ensureBibId(String id) {
        String bibId = id;
        //if we have the prefix, we need to remove it and convert the uuid to dash format
        String bibIdPrefix = "tamu.";
        if (bibId.startsWith(bibIdPrefix)) {
            String[] uuidPieces = bibId.split(bibIdPrefix, 0);
            bibId = uuidPieces[1].replace(".", "-");
        }
        return bibId;
    }

    /**
     * Provides the raw button data in JSON format, keyed by MFHD.
     * If a specific button with matching criteria - Cushing is found, it performs a redirect.
     * @param String catalogName Optional catalog name. Defaults to "evans" if not provided.
     * @param String bibId  Required bibliographic ID (UUID, hrid, or EBSCO format). Used to retrieve holdings and associated button data.
     * @param String location Optional location parameter used for location-based redirect.
     * @return ApiResponse or RedirectView based on redirectUrl.
     */
    @RequestMapping("/get-buttons-redirect")
    public Object getButtonsRedirectByBibId(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId, @RequestParam(value="location", required = false, defaultValue="") String location) {
        bibId = ensureBibId(bibId);
        Map<String,ButtonPresentation> buttonData = getItForMeService.getButtonDataByBibId(catalogName, bibId);
        String redirectUrl = null;
        if (buttonData != null) {
            //test for the button set having a Cushing button
            boolean hasCushing = false;
            //test for the user requesting a Cushing button
            boolean wantsCushing = location.equalsIgnoreCase("aeon");
            Integer holdingsWithButtonsCount = 0;

            for (Map.Entry<String, ButtonPresentation> entry : buttonData.entrySet()) {
                String holdingId = entry.getKey();
                ButtonPresentation buttonPresentation = entry.getValue();
                if (holdingId != null && buttonPresentation != null && buttonPresentation.getButtons() != null) {
                    holdingsWithButtonsCount++;
                    for( Map<String, String> button: buttonPresentation.getButtons()) {
                        String linkHref = button.get("linkHref");
                        //is the current button Cushing
                        boolean isCushing = testIsCushing(button);
                        //is at least one button in the set Cushing
                        if (isCushing && !hasCushing) {
                            hasCushing = true;
                        }
                        //when the user wants Cushing, grab the url of the first button that is for Cushing
                        if(redirectUrl == null && isCushing && wantsCushing && linkHref != null ) {
                            redirectUrl = buildRedirectUrl(linkHref);
                            break;
                        }
                        //when the user doesn't want Cushing, grab the first button that isn't Cushing
                        if(redirectUrl == null && !isCushing && !wantsCushing && linkHref != null ) {
                            redirectUrl = buildRedirectUrl(linkHref);
                            break;
                        }
                    }
                }
                //complete the whole button set loop even if we have a redirectUrl so we can learn if we have
                //a Cushing button in the set or not and our total count of holdings with buttons
            }
            //if we have a Cushing button but the user hasn't explicitly requested one, we can auto-redirect
            //to the other button if there are only 2 or fewer holdings
            Integer holdingCountThreshold = (hasCushing) ? 3:2;

            //If we have too many holdings options to choose from, redirect to the catalog so the user can choose there
            if ((!hasCushing || !wantsCushing) && holdingsWithButtonsCount >= holdingCountThreshold && publicCatalogUrl.length() > 0) {
                redirectUrl = publicCatalogUrl + bibId;
            }

            if (redirectUrl != null) {
                return new RedirectView(redirectUrl);
            }
            throw new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "No valid redirect URL found.", null);
        } else {
            throw new ResponseStatusException(HttpStatus.SC_NOT_FOUND, "Error processing Catalog or Holding", null);
        }
    }

    /**
     * Provides the current buttons configuration in JSON format
     * @return
     */
    @RequestMapping("/get-button-config")
    public ApiResponse getButtonConfiguration(@RequestParam(value="catalogName",defaultValue="evans") String catalogName) {
        return new ApiResponse(SUCCESS,"Current Button Configuration",getItForMeService.getRegisteredButtons(catalogName));
    }

    /**
     * Provides the text a call number button data in JSON format
     * @param String catalogName (optional)
     * @param String bibId
     * @param String holdingId
     * @return
     */
    @RequestMapping("/text-call-number")
    public ApiResponse textCall(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId, @RequestParam("holdingId") String holdingId) {
        Map<String,String> buttonData = textCallNumberService.getTextCallNumberButton(catalogName, bibId, holdingId);
        if (buttonData != null) {
            return new ApiResponse(SUCCESS, buttonData);
        } else {
            return new ApiResponse(ERROR,"Error processing Catalog or Holding");
        }
    }

    /**
     * Returns the result of a check for full text for a given title and issn
     *
     * @param title String The title to check
     * @param issn the Issn to check
     * @return ApiResponse
     */

    @RequestMapping("/check-full-text")
    public ApiResponse checkFullText(@RequestParam("title") String title, @RequestParam("issn") String issn) {
        try {
            return new ApiResponse(SUCCESS, "Full Text results", sfxService.hasFullText(title, issn));
        } catch (RuntimeException e) {
            return new ApiResponse(ERROR,e.getMessage());
        }
    }

    @RequestMapping("/get-map-link")
    public ApiResponse getMapLink(@RequestParam("location") String location) {
        return new ApiResponse(SUCCESS, "Map Details", mapService.getMapLink(location));
    }
}
