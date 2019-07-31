package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        return null;
    }

    public static String buildForm(Map<String,Map<String,String>> items, String action, Map<String,String> fieldMap, Map<String,String> parameters ) {

        StringBuilder volumeOptions = new StringBuilder();
        volumeOptions.append("<select name=\"edition\">");
        items.forEach((uri, itemData) -> {
            String volumeOption = itemData.get("enumeration")+((itemData.containsKey("chron") && !itemData.get("chron").isEmpty()) ? " "+itemData.get("chron"):"");
            volumeOptions.append("<option name=\"edition\" value=\""+volumeOption+"\">"+volumeOption+"</option>");
        });
        volumeOptions.append("</select>");



        StringBuilder formFields = new StringBuilder();
        fieldMap.forEach((k,v) -> {
            formFields.append("<input type=\"hidden\" name=\""+k+"\" value=\""+v+"\" />");
        });

        String formTemplate = formFields.toString();

        formTemplate = generateLinkHref(parameters, formTemplate);

        formTemplate += volumeOptions.toString();

        formTemplate += "<input class=\"button-gifm\" type=\"submit\" value=\"Get It For Me\" />";

        return "<form target=\"_blank\" action=\""+action+"\" method=\"GET\">"+formTemplate+"</form>";
    }


}
