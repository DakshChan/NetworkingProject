import java.util.ArrayList;

public class Chat {

	public static ArrayList<Chat> chats;

	public String id;
	public String name;
	public ArrayList<Message> messages;
	public ArrayList<User> users;

	Chat() {
		id = randomAlphaNumeric(8);
		if (chats == null)
			chats = new ArrayList<>();
		chats.add(this);
	}

	Chat(String name) {
		id = randomAlphaNumeric(8);
		this.name = name;
		if (chats == null)
			chats = new ArrayList<>();
		chats.add(this);
	}

	Chat(String name, String id) {
		this.id = id;
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		Chat chat = (Chat) o;
		return this.id.equals(chat.id);
	}

	public static String randomAlphaNumeric(int count) {
		final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder builder = new StringBuilder();
		while (count-- != 0) {
			int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
			builder.append(ALPHA_NUMERIC_STRING.charAt(character));
		}
		return builder.toString();
	}

}
