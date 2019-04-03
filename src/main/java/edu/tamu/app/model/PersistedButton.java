package edu.tamu.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import edu.tamu.weaver.validation.model.ValidatingBaseEntity;

/**
 * @author Jason Savell <jsavell@library.tamu.edu>
 * @author James Creel <jcreel@library.tamu.edu>
 */

@Entity
public class PersistedButton extends ValidatingBaseEntity implements GetItForMeButton {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column
    String name;

    @ElementCollection
    protected List<String> templateParameterKeys;

    @ElementCollection
    protected List<String> locationCodes = new ArrayList<String>();

    @ElementCollection
    protected List<String> itemTypeCodes = new ArrayList<String>();

    @ElementCollection
    protected List<Integer> itemStatusCodes = new ArrayList<Integer>();

    @Column
    private String linkText = "Default Link Text";

    @Column
    private String SID = "libcat:InProcess";

    @Column
    private String cssClasses;

    @Column
    private String recordType;

    @Column(columnDefinition = "TEXT")
    private String linkTemplate;

    @Column
    private boolean active;

    @Override
    public boolean fitsRecordType(String marcRecordLeader) {
        if (recordType != null && !marcRecordLeader.substring(6, 6 + recordType.length()).contentEquals(recordType)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean fitsLocation(String locationCode) {
        if (locationCodes == null) {
            return true;
        }
        if (locationCodes.size() == 1) {
            return locationCode.startsWith(locationCodes.get(0));
        }
        return locationCodes.contains(locationCode);
    }

    @Override
    public boolean fitsItemType(String itemTypeCode) {
        return (itemTypeCodes.size() > 0) ? itemTypeCodes.contains(itemTypeCode) : true;
    }

    @Override
    public boolean fitsItemStatus(int itemStatusCode) {
        return (itemStatusCodes.size() > 0) ? itemStatusCodes.contains(itemStatusCode) : true;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLinkTemplate() {
        return linkTemplate;
    }

    public void setLinkTemplate(String linkTemplate) {
        this.linkTemplate = linkTemplate;
    }

    @Override
    public List<String> getTemplateParameterKeys() {
        return this.templateParameterKeys;
    }

    public void setTemplateParameterKeys(String[] templateParameterKeys) {
        this.templateParameterKeys = Arrays.asList(templateParameterKeys);
    }

    @Override
    public String getLinkText() {
        return linkText;
    }

    @Override
    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    @Override
    public String getSID() {
        return SID;
    }

    @Override
    public void setSID(String SID) {
        this.SID = SID;
    }

    @Override
    public String getCssClasses() {
        return this.cssClasses;
    }

    @Override
    public void setCssClasses(String cssClasses) {
        this.cssClasses = cssClasses;
    }

    public List<String> getLocationCodes() {
        return locationCodes;
    }

    @Override
    public void setLocationCodes(String[] locationCodes) {
        this.locationCodes = Arrays.asList(locationCodes);
    }

    public List<String> getItemTypeCodes() {
        return itemTypeCodes;
    }

    @Override
    public void setItemTypeCodes(String[] itemTypeCodes) {
        this.itemTypeCodes = Arrays.asList(itemTypeCodes);
    }

    public List<Integer> getItemStatusCodes() {
        return itemStatusCodes;
    }

    @Override
    public void setItemStatusCodes(Integer[] itemStatusCodes) {
        this.itemStatusCodes = Arrays.asList(itemStatusCodes);
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    @Override
    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
