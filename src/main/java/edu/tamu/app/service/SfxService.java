package edu.tamu.app.service;

import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

import edu.tamu.app.config.AppConfig;

/**
 * The SfxService checks with the linkresolver to find out if the full text is available for the given url
 *
 * @author Jason Savell <jsavell@library.tamu.edu>
 *
 */
@Service
public class SfxService {

    @Autowired
    AppConfig appConfig;

    @Autowired
    CatalogService catalogService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private RestTemplate restTemplate;

    private String xmlTemplate = null;

    public boolean hasFullText(String title, String issn) throws RuntimeException {
        StringBuilder rftXml = new StringBuilder();
        rftXml.append("<rft:title>"+StringEscapeUtils.escapeHtml4(title)+"</rft:title>\r\n");
        rftXml.append("<rft:issn>"+StringEscapeUtils.escapeHtml4(issn)+"</rft:issn>\r\n");

        try {
            String postXml = String.format(getXmlTemplate(),rftXml);
            MultiValueMap<String, String> formValues = new LinkedMultiValueMap<>();
            formValues.add("url_ver", "Z39.88-2004");
            formValues.add("url_ctx_fmt", "info:ofi/fmt:xml:xsd:ctx");
            formValues.add("url_ctx_val", postXml);
            formValues.add("sfx.response_type", "service_exist");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formValues, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(appConfig.getSfx().getResolverUrl(), request, String.class);
            if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody().contains("<services>yes</services>")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("The SFX API returned an error");
        }
        return false;
    }

    private String getXmlTemplate() throws IOException {
        if (this.xmlTemplate == null) {
            this.xmlTemplate =  Files.readString(resourceLoader.getResource("classpath:templates/sfx_rft.xml").getFile().toPath());
        }
        return this.xmlTemplate;
    }
}
