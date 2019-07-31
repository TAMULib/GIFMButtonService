package edu.tamu.app.model;

import java.util.List;
import java.util.Map;

public interface ButtonPresentation {
    public List<String> buildPresentation();
    public List<Map<String, String>> getButtons();
    public String getPresentationType();
}
