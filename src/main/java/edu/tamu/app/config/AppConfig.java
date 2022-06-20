package edu.tamu.app.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private final DefaultButton defaultButton = new DefaultButton();

    private final Buttons buttons = new Buttons();

    private final BoundWith boundWith = new BoundWith();

    private final Sfx sfx = new Sfx();

    public static class DefaultButton {

        private String[] templateParameterKeys;

        private Map<String, String> fieldMap;

        private Map<String,String> SID;

        private String action;

        private String volumeField;

        private String text;

        private int threshold = 100;

        public String[] getTemplateParameterKeys() {
            return templateParameterKeys;
        }

        public void setTemplateParameterKeys(String[] templateParameterKeys) {
            this.templateParameterKeys = templateParameterKeys;
        }

        public Map<String, String> getFieldMap() {
            return fieldMap;
        }

        public void setFieldMap(Map<String, String> fieldMap) {
            this.fieldMap = fieldMap;
        }

        public Map<String, String> getSID() {
            return SID;
        }

        public void setSID(Map<String, String> SID) {
            this.SID = SID;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getVolumeField() {
            return volumeField;
        }

        public void setVolumeField(String volumeField) {
            this.volumeField = volumeField;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

    }

    public static class Buttons {

        private String patronGroupOverride = null;

        private String[] locationsOverride = null;

        public String getPatronGroupOverride() {
            return patronGroupOverride;
        }

        public void setPatronGroupOverride(String patronGroupOverride) {
            this.patronGroupOverride = patronGroupOverride;
        }

        public String[] getLocationsOverride() {
            return locationsOverride;
        }

        public void setLocationsOverride(String[] locationsOverride) {
            this.locationsOverride = locationsOverride;
        }

    }

    public static class BoundWith {

        private String locations;

        public String getLocations() {
            return locations;
        }

        public void setLocations(String locations) {
            this.locations = locations;
        }

    }

    public static class Sfx {

        private String resolverUrl;

        public String getResolverUrl() {
            return resolverUrl;
        }

        public void setResolverUrl(String resolverUrl) {
            this.resolverUrl = resolverUrl;
        }

    }

    public DefaultButton getDefaultButton() {
        return defaultButton;
    }

    public Buttons getButtons() {
        return buttons;
    }

    public BoundWith getBoundWith() {
        return boundWith;
    }

    public Sfx getSfx() {
        return sfx;
    }

}
