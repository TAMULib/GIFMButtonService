package edu.tamu.app.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.service.CatalogService;
import edu.tamu.framework.aspect.annotation.ApiMapping;
import edu.tamu.framework.aspect.annotation.Data;
import edu.tamu.framework.aspect.annotation.SkipAop;

@Controller
@ApiMapping("/catalog-access")
public class CatalogAccessController {
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CatalogService catalogService;

	@ApiMapping("get-holdings")
	@SkipAop
	public String getHoldingsByBibId(@Data String data) throws JsonProcessingException, IOException {
		String bibId = "1892485";
        catalogService.getHoldingsByBibId(bibId);
		return null;
	}
}
