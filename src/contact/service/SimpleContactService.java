package contact.service;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimpleContactService implements ContactService
{
    private long nextId = 0;
    private Map<Long, Contact> contacts;
    private ListProperty<Contact> listOfContacts;
    private IntegerProperty selectedContactIndex;
    
    public SimpleContactService()
    {
        this.contacts = new HashMap<Long, Contact>();
        getContacts();
        createContact("Cathy", "Freeman");
        createContact("Albert", "Namatjira");
        createContact("Noel", "Pearson");
        createContact("Oodgeroo", "Nooncal");
        createContact("Neville", "Bonner");
        createContact("Pat", "O'Shane");
        createContact("Ernie", "Dingo");
        createContact("Adam", "Goodes");
        createContact("David", "Gulpilil");
        createContact("Yvonne", "Goolagong-Cawley");
    }

    public ListProperty<Contact> getContacts(){
    	if (listOfContacts == null) {
            ObservableList<Contact> innerList = FXCollections.observableArrayList();
            listOfContacts = new SimpleListProperty<>(innerList);
        }        
    	return listOfContacts;
    }
    
    public int getSelectedContactIndex() {
        return selectedContactIndexProperty().get();
    }
 
    public void setSelectedContactIndex(int selectedPersonIndex) {
        this.selectedContactIndex.set(selectedPersonIndex);
    }
 
    public IntegerProperty selectedContactIndexProperty() {
        if (selectedContactIndex == null) {
            selectedContactIndex = new SimpleIntegerProperty();
        }
        return selectedContactIndex;
    }
    
    
    public Contact createContact(String firstName, String lastName)
    {
        Contact contact = new Contact(nextId++, firstName, lastName);
        contacts.put(contact.getId(), contact);
        listOfContacts.add(contact);
        return contact;
    }

    public List<Contact> searchContacts(String[] keywords)
    {
        List<Contact> matches = new ArrayList<Contact>();
        if (keywords != null && keywords.length > 0)
        {
            outer:
            for (Contact contact : contacts.values())
            {
                for (String keyword : keywords)
                {
                    keyword = keyword.toLowerCase();
                    if (!(contact.getFirstName().toLowerCase().contains(keyword)
                            || contact.getLastName().toLowerCase().contains(keyword)))
                    {
                        // keyword not found on contact
                        continue outer;
                    }
                }
                matches.add(contact);
            }
        }
        else
        {
            matches.addAll(contacts.values());
        }
        listOfContacts.clear();
        listOfContacts.addAll(matches);
        return matches;
    }

    public Contact getContact(Long contactId)
    {
        return contacts.get(contactId);
    }

    public Contact updateContact(Contact updatedContact)
    {
        contacts.put(updatedContact.getId(), updatedContact);
        listOfContacts.getValue().remove(selectedContactIndex.getValue());        
        listOfContacts.add(selectedContactIndex.getValue(), updatedContact);
        return updatedContact;
    }
}

