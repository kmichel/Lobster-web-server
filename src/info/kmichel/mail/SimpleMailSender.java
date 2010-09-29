package info.kmichel.mail;

import java.util.Date;
import java.util.Properties;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

public class SimpleMailSender implements MailSender {

	private final Properties properties;

	public SimpleMailSender(final Properties properties) {
		this.properties = properties;
	}

	public void push(final String to, final String subject, final String content) {
		try {
			// XXX check connection leakage !
			// XXX missing escaping check
			final Session session = Session.getInstance(properties, null);
			final MimeMessage message = new MimeMessage(session);
			message.setFrom();
			message.setRecipients(Message.RecipientType.TO, to);
			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setText(content);
			message.saveChanges();
			final Transport transport = session.getTransport("smtp");
			if (message != null) {
				transport.connect();
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			}
		} catch (final MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
