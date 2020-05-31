import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
	String signature;
	String body;

	Message(String sender, String body) {
		this.body = body;

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime time = LocalDateTime.now();
		signature = "[" + dtf.format(time) + "] " + sender;
	}

}