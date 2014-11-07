package contact.service;

import java.util.List;

public interface ContactService
{
	List<Contact> searchContacts(String[] keywords);

    Contact getContact(Long contactId);

    Contact updateContact(Contact updatedContact);
}

