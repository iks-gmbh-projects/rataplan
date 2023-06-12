package de.iks.rataplan.domain;

import de.iks.rataplan.exceptions.MalformedException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContactData {
	private String subject;
	private String content;
	private String senderMail;

	public void assertValid() {
		if(senderMail == null || senderMail.trim().isEmpty() ||
			subject == null || subject.trim().isEmpty() ||
			content == null || content.trim().isEmpty()
		) throw new MalformedException("Invalid or missing fields");
	}
}
