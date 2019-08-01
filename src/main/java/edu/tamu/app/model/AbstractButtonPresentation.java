package edu.tamu.app.model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public abstract class AbstractButtonPresentation implements ButtonPresentation {

    private List<Map<String, String>> buttons;

    @Override
    public List<Map<String, String>> getButtons() {
        return buttons;
    }

    public void setButtons(List<Map<String, String>> buttons) {
        this.buttons = buttons;
    }

    public static String renderTemplate(Map<String,String> parameters, String template) {
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            try {
                template = template.replace("{" + entry.getKey() + "}", URLEncoder.encode((entry.getValue() != null) ? entry.getValue():"", StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return template;
    }
}
