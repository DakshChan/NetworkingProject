import java.math.BigInteger;
import java.util.Random;

public class User {

	private String name;
	private String password;
	private int senderKey;

	public User(String name, String password) {
		this.name = name;
		this.password = password;
		senderKey = BigInteger.probablePrime(30, new Random()).intValue();
	}

	@Override
	public boolean equals(Object obj) {
		User user = (User) obj;
		return user.name.equals(this.name) && user.password.equals(this.password);
	}

}
