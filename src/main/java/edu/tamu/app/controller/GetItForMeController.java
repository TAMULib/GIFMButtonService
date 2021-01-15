package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.app.model.ButtonPresentation;
import edu.tamu.app.service.GetItForMeService;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@RequestMapping("/catalog-access")
public class GetItForMeController {
    @Autowired
    GetItForMeService getItForMeService;

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
	        Map<String,List<String>> buttonContents = new HashMap<String,List<String>>();
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
	public ApiResponse getButtonsByBibId(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId) {
		Map<String,ButtonPresentation> buttonData = getItForMeService.getButtonDataByBibId(catalogName, bibId);
		if (buttonData != null) {
		    return new ApiResponse(SUCCESS,buttonData);
		} else {
		    return new ApiResponse(ERROR,"Error processing Catalog or Holding");
		}
	}

    /**
     * Provides the current buttons configuration in JSON format
     * @return
     */
	@RequestMapping("/get-button-config")
	public ApiResponse getButtonConfiguration() {
	    return new ApiResponse(SUCCESS,"Current Button Configuration",getItForMeService.getRegisteredButtons());
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
        Map<String,String> buttonData = getItForMeService.getTextCallNumberButton(catalogName, bibId, holdingId);
        if (buttonData != null) {
            return new ApiResponse(SUCCESS, getItForMeService.getTextCallNumberButton(catalogName, bibId, holdingId));
        } else {
            return new ApiResponse(ERROR,"Error processing Catalog or Holding");
        }
    }
}
