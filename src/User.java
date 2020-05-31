import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

public class User {

	private String name;
	private String password;
	private int senderKey;
	private ArrayList<Chat> chats;

	public User(String name) {
		System.out.println("no pass");
		this.name = name;
	}

	public User(String name, String password) {
		this.name = name;
		this.password = password;
		senderKey = BigInteger.probablePrime(30, new Random()).intValue();
	}

	@Override
	public boolean equals(Object obj) {
		User user = (User) obj;
		System.out.println(user.password);
		if (this.password != null) {
			System.out.println("this runs");
			return user.name.equals(this.name) && user.password.equals(this.password);
		}
		return user.name.equals(this.name);
	}

	public String getName() { return name; }

}
