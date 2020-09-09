package de.iks.rataplan.domain;

public class ContactData {
	
	private String subject;
	private String content;
	private String senderMail;
	
	public ContactData(String subject, String content, String senderMail) {
		this.subject = subject;
		this.content = content;
		this.senderMail = senderMail;
	}

	public ContactData() {
		
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSenderMail() {
		return senderMail;
	}

	public void setSenderMail(String senderMail) {
		this.senderMail = senderMail;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContactData [subject=");
		builder.append(subject);
		builder.append(", content=");
		builder.append(content);
		builder.append(", senderMail=");
		builder.append(senderMail);
		builder.append("]");
		return builder.toString();
	}

}
