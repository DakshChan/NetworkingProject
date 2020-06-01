import java.math.BigInteger;
import java.security.spec.ECField;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
 
	public static int n;
	public static int g;

	private static int clientKey;
	//public static int partialKey;
	public static BigInteger partialKey;
	private static int sharedKey;

	long[] p = new long[18];
	long[][] s = new long[4][256];

	// SERVER ONLY
	public static void generateKeys() {
		n = BigInteger.probablePrime(30, new Random()).intValue();
		g = PrimitiveRoot.primitiveRoot(n);
		System.out.println(n);
		System.out.println(g);
		createPartialKey();
	}

	// CLIENT ONLY
	public static void getKeys(int n, int g) {
		Encryption.n = n;
		Encryption.g = g;
		createPartialKey();
	}

	// CLIENT ONLY
	public static void createPartialKey() {
		clientKey = new Random().nextInt(n - 2) + 2;
		System.out.println(clientKey);
		partialKey = BigInteger.valueOf(g).modPow(BigInteger.valueOf(clientKey), BigInteger.valueOf(n));
		System.out.println("Partial Key: " + partialKey);
	}

	// CLIENT ONLY
	public static void createSharedKey(int partialKey) {
		// handle getting the other client's partialKey through sockets here
		sharedKey = (int) (Math.pow(partialKey, clientKey) % n);
		System.out.println("Shared key: " + sharedKey);
	}

	public void serverHandshake() {

	}

	public void clientHandshake() {

	}

	public static String encrypt(String msg) {

		try {
			byte[] keyData = Integer.toString(sharedKey).getBytes();
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			byte[] encryptedBytes = cipher.doFinal(msg.getBytes());

			return new String(Base64.getEncoder().encode(encryptedBytes));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String decrypt(String msg) {

		try {
			byte[] keyData = Integer.toString(sharedKey).getBytes();
			SecretKeySpec secretKeySpec = new SecretKeySpec(keyData, "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(msg));

			return new String(decryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
