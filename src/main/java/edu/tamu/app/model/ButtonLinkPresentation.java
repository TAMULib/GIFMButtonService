package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ButtonLinkPresentation extends AbstractButtonPresentation {
    private static final String PRESENTATION_TYPE = "link";

    public ButtonLinkPresentation(List<Map<String, String>> buttons) {
        setButtons(buttons);
    }

    public ButtonLinkPresentation(String presentationType) {
        setButtons(new ArrayList<Map<String, String>>());
    }

    @Override
    public String getPresentationType() {
        return PRESENTATION_TYPE;
    }

    protected String getButtonHtml() {
        return "<a target=\"_blank\" class=\"{cssClasses}\" href=\"http://{linkHref}\">{linkText}</a>";
    }

    @Override
    public List<String> buildPresentation() {
        List<String> renderedButtons = new ArrayList<String>();
        for (Map<String, String> button:getButtons()) {
            String templateHtml = getButtonHtml();
            for (Entry<String, String> entry: button.entrySet()) {
                templateHtml = templateHtml.replace("{"+entry.getKey()+"}",entry.getValue());
            }
            renderedButtons.add(templateHtml);
        }
        return renderedButtons;
    }

    public static Map<String,String> buildButtonProperties(Map<String,String> parameters, GetItForMeButton button) {
        Map<String,String> buttonContent = new HashMap<String,String>();
        buttonContent.put("linkText", button.getLinkText());
        String linkHref = ButtonLinkPresentation.generateLinkHref(parameters, button.getLinkTemplate());

        buttonContent.put("linkHref", linkHref);
        buttonContent.put("cssClasses", "button-gifm" + ((button.getCssClasses() != null) ? " "+button.getCssClasses():""));

        return buttonContent;
    }

    public static Map<String,String> buildMultiVolumeButtonProperties(Map<String,String> parameters, GetItForMeButton button) {
        Map<String,String> buttonContent = buildButtonProperties(parameters, button);
        buttonContent.put("linkText",parameters.get("edition")+" | "+button.getLinkText());
        return buttonContent;
    }
}
