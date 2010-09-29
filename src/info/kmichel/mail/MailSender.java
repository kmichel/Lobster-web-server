package info.kmichel.mail;

public interface MailSender {

	void push(final String to, final String subject, final String content);

}
