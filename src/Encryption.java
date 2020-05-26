import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;

public class Encryption {

	public static int n;
	public static int g;

	public static int clientKey;
	public static int partialKey;
	public static int sharedKey;

	// SERVER ONLY
	public static void generateKeys() {
		n = BigInteger.probablePrime(30, new Random()).intValue();
		g = PrimitiveRoot.primitiveRoot(n);
		System.out.println(n);
		System.out.println(g);
	}

	// CLIENT ONLY
	public static void getKeys(int n1, int g1) {

		// do the socket shit here

		n = n1;
		g = g1;
	}

	// CLIENT ONLY
	public static void createPartialKey() {
		clientKey = new Random().nextInt(n - 2) + 2;
		partialKey = (int) (Math.pow(g, clientKey) % n);
	}

	// CLIENT ONLY
	public static void createSharedKey(int partialKey) {
		// handle getting the other client's partialKey through sockets here
		sharedKey = (int) (Math.pow(partialKey, clientKey) % n);
	}

	public static String encrypt(String msg) {
		return "";
	}

	public static String decrypt(String msg) {
		return "";
	}

}
