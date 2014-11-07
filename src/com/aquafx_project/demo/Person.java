package com.aquafx_project.demo;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class Person {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty primaryEmail;
    private final SimpleStringProperty secondaryEmail;
    private final SimpleBooleanProperty vip;
 
    Person(String fName, String lName, String primaryEmail, String secondaryEmail, boolean vip) {
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.primaryEmail = new SimpleStringProperty(primaryEmail);
        this.secondaryEmail = new SimpleStringProperty(secondaryEmail);
        this.vip = new SimpleBooleanProperty(vip);
    }
 
    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String fName) {
        firstName.set(fName);
    }
        
    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String fName) {
        lastName.set(fName);
    }
    
    public String getPrimaryEmail() {
        return primaryEmail.get();
    }
    public void setPrimaryEmail(String fName) {
        primaryEmail.set(fName);
    }
    
    public String getSecondaryEmail() {
        return secondaryEmail.get();
    }
    public void setSecondaryEmail(String fName) {
        secondaryEmail.set(fName);
    }

    public boolean getVip() {
        return vip.get();
    }
    public void setVip(boolean fName) {
        vip.set(fName);
    }
}
