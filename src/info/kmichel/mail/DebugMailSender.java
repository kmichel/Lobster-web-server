package info.kmichel.mail;

import java.io.PrintStream;

public class DebugMailSender implements MailSender {

	private final PrintStream stream;

	public DebugMailSender(final PrintStream stream) {
		this.stream = stream;
	}

	public void push(final String to, final String subject, final String content) {
		stream.println("Mail to : "+to);
		stream.println("Subject : "+subject);
		stream.println("Content :\n"+content);
		stream.println("--------------------");
	}

}
