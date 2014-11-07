package contact.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Contact
{
    private long id;
    private StringProperty firstName;
    private StringProperty lastName;

    public Contact(long id, String firstName, String lastName)
    {
        this.id = id;
        this.firstName = new SimpleStringProperty();
        this.lastName = new SimpleStringProperty();
        this.firstName.setValue(firstName);
        this.lastName.setValue(lastName);
    }

    public long getId()
    {
        return id;
    }

    public String getFirstName()
    {
        return firstName.getValue();
    }

    public StringProperty getFirstNameProperty()
    {
        return firstName;
    }
    
    
    public String getLastName()
    {
        return lastName.getValue();
    }
    
    public StringProperty getLastNameProperty()
    {
        return lastName;
    }
    
    public String toString(){
    	return firstName.getValue() + " " + lastName.getValue();
    }
}

