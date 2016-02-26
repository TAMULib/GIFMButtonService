package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.service.GetItForMeService;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.SkipAop;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/catalog-access")
public class CatalogAccessController {
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    GetItForMeService getItForMeService;
    
	@ApiMapping("/get-holdings")
	@SkipAop
	public ApiResponse getHoldings(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId) throws JsonProcessingException, IOException {
		return new ApiResponse(SUCCESS,getItForMeService.getHoldingsByBibId(catalogName,bibId));
	}

	@ApiMapping("/get-buttons")
	@SkipAop
	public ApiResponse getButtonsByBibId(@RequestParam(value="catalogName",defaultValue="evans") String catalogName, @RequestParam("bibId") String bibId) {
		Map<String,List<Map<String,String>>> buttonContents = getItForMeService.getButtonsByBibId(catalogName, bibId);
		if (buttonContents != null) {
			return new ApiResponse(SUCCESS,buttonContents);
		}
		return new ApiResponse(ERROR,"Catalog or Holding not found");
	}
}
