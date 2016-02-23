package edu.tamu.app.controller;

import static edu.tamu.framework.enums.ApiResponseType.ERROR;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.service.CatalogServiceFactory;
import edu.tamu.app.service.GetItForMeService;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.SkipAop;
import edu.tamu.framework.model.ApiResponse;

@Controller
@ApiMapping("/catalog-access")
public class CatalogAccessController {
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CatalogServiceFactory catalogServiceFactory;
    
    @Autowired GetItForMeService getItForMeService;

	@ApiMapping("/get-holdings")
	@SkipAop
	public ApiResponse getHoldings(@Data String data) throws JsonProcessingException, IOException {
		String bibId = "1892485";
		getHoldingsByBibId(bibId);
		return new ApiResponse(SUCCESS,"Excellent");
	}
	
	@ApiMapping("/get-buttons")
	@SkipAop
	public ApiResponse getButtonsByBibId() {
		String bibId = "1892485";
		try {
			getHoldingsByBibId(bibId);
			return new ApiResponse(SUCCESS,"<a href=\"#\">Button for "+bibId+"</a>");
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ApiResponse(ERROR);
	}
	
	private String getHoldingsByBibId(String bibId) throws JsonProcessingException, IOException {
		return getItForMeService.getButtonsByBibId(bibId);
	}

}
