package edu.tamu.app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * The SfxService checks with the linkresolver to find out if the full text is available for the given url
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 *
 */


@Service
public class SfxService {

    @Autowired
    CatalogService catalogService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.sfx.resolverUrl}")
    private String sfxResolverUrl;

    private Map<String,String> fieldMapping = Map.of("title","jtitle");

    public boolean hasFullText(Map<String,String> resolverValues) {
        StringBuilder rftXml = new StringBuilder();
        resolverValues.forEach((k,v) -> {
           String key = fieldMapping.containsKey(k) ? fieldMapping.get(k):k;
           rftXml.append("<"+key+">"+v+"</"+key+">");
        });
        Resource xmlTemplateResource = resourceLoader.getResource("classpath:templates/sfx_rft.xml");
        try {
            String postXml = String.format(Files.readString(xmlTemplateResource.getFile().toPath()),rftXml);
            MultiValueMap<String, String> formValues = new LinkedMultiValueMap<>();
            formValues.add("url_ver", "Z39.88-2004");
            formValues.add("url_ctx_fmt", "info:ofi/fmt:xml:xsd:ctx");
            formValues.add("url_ctx_val", postXml);
            formValues.add("sfx.response_type", "service_exist");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formValues, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(sfxResolverUrl, request, String.class);
            if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody().contains("<services>yes</services>")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
