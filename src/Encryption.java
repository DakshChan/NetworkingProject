import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;

public class Encryption {
 
	public static int n;
	public static int g;

	public static int clientKey;
	public static int partialKey;
	public static int sharedKey;

	long[] p = new long[18];
	long[][] s = new long[4][256];

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

	long f(long x) {
		long h = s[0][(int) (x >> 24)] + s[1][(int)(x >> 16 & 0xff)];
		return (h ^ s[2][(int) (x >> 8 & 0xff)]) + s[3][(int) (x & 0xff)];
	}

	public static String encrypt(String msg) {

		

		return "";
	}

	public static String decrypt(String msg) {
		return "";
	}

}
