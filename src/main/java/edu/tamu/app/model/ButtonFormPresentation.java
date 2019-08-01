package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.tamu.app.utilities.sort.AlphanumComparator;

public class ButtonFormPresentation extends AbstractButtonPresentation {
    private static final String PRESENTATION_TYPE = "form";

    public ButtonFormPresentation(List<Map<String, String>> buttons) {
        setButtons(buttons);
    }

    public ButtonFormPresentation(String presentationType) {
        setButtons(new ArrayList<Map<String, String>>());
    }

    @Override
    public String getPresentationType() {
        return PRESENTATION_TYPE;
    }

    @Override
    public List<String> buildPresentation() {
        List<String> renderedButtons = new ArrayList<String>();
        for (Map<String, String> button:getButtons()) {
            renderedButtons.add(button.get("form"));
        }
        return renderedButtons;
    }

    public static String buildForm(Map<String,Map<String,String>> items, String action, Map<String,String> fieldMap, String volumeField, Map<String,String> parameters ) {
        List<String> volumesText = new ArrayList<String>();
        items.forEach((uri, itemData) -> {
            volumesText.add(itemData.get("enumeration")+((itemData.containsKey("chron") && !itemData.get("chron").isEmpty()) ? " "+itemData.get("chron"):""));
        });
        Collections.sort(volumesText, new AlphanumComparator());
        StringBuilder volumeOptions = new StringBuilder();
        volumeOptions.append("<select name=\""+volumeField+"\">");
        volumesText.forEach((text) -> {
            volumeOptions.append("<option value=\""+text+"\">"+text+"</option>");
        });
        volumeOptions.append("</select>");



        StringBuilder formFields = new StringBuilder();
        fieldMap.forEach((k,v) -> {
            formFields.append("<input type=\"hidden\" name=\""+k+"\" value=\""+v+"\" />");
        });

        String formTemplate = renderTemplate(parameters, formFields.toString()) + volumeOptions.toString() + "<input class=\"button-gifm\" type=\"submit\" value=\"Get It For Me\" />";

        return "<form target=\"_blank\" action=\""+action+"\" method=\"GET\">"+formTemplate+"</form>";
    }


}
