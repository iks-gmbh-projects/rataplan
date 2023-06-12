package de.iks.rataplan.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.ContactData;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.service.MailService;

@Service
@RequiredArgsConstructor
public class GeneralControllerService {
	private final MailService mailService;
	
	public void sendMailToContact(ContactData contactData) {
		if (contactDataIsValid(contactData)) {
			mailService.sendMailForContactRequest(contactData);
			return;
		}

		throw new MalformedException("ContactData is malformed!");
	}
	
	private boolean contactDataIsValid(ContactData contactData) {
		return contactData != null 
				&& contactData.getContent() != null
				&& contactData.getSubject() != null
				&& contactData.getSenderMail() != null;
	}
	
}
