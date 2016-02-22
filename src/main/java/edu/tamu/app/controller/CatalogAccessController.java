package edu.tamu.app.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.service.CatalogServiceFactory;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.SkipAop;
import edu.tamu.framework.model.ApiResponse;
import static edu.tamu.framework.enums.ApiResponseType.SUCCESS;
import static edu.tamu.framework.enums.ApiResponseType.ERROR;

@Controller
@ApiMapping("/catalog-access")
public class CatalogAccessController {
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CatalogServiceFactory catalogServiceFactory;

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
		String catalogName = "evans";
        return catalogServiceFactory.getOrCreateCatalogService(catalogName).getHoldingsByBibId(bibId);
	}

}
