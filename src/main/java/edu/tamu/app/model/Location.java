package edu.tamu.app.model;

import java.util.ArrayList;
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
 */

@Entity
public class Location extends ValidatingBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column
    String name;

    @ElementCollection
    protected List<String> locationCodes = new ArrayList<String>();

    @Column
    private String catalogName;

    @Column(nullable=true)
    private String mapUrl;
}
