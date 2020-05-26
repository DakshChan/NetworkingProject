import java.math.BigInteger;
import java.util.*;

public class PrimitiveRoot {

	public static int primitiveRoot(int p) {
		final int ROOT_SIZE = 2000;
		ArrayList<Integer> roots = new ArrayList<>();
		int m = p - 1;
		Map<Integer, Integer> primeFactor = getPrimeFactor(m);
		primeFactor.replaceAll((k, v) -> m / k);
		for (int i = 2; i <= m && roots.size() < ROOT_SIZE; i++) {
			boolean notPrimeRoot = false;
			Set<Integer> reminder = new HashSet<>();
			for (Map.Entry<Integer, Integer> map : primeFactor.entrySet()) {
				if(BigInteger.valueOf(i).modPow(BigInteger.valueOf(map.getValue()), BigInteger.valueOf(p)).equals(BigInteger.ONE))
					notPrimeRoot = true;
			}
			if (!notPrimeRoot) {
				roots.add(i);
			}
		}
		return roots.get(new Random().nextInt(roots.size()));
	}

	private static boolean isPrime(int p) {
		for (int i = 2; i <= Math.sqrt(p); i++) {
			if (p % i == 0) {
				return false;
			}
		}
		return true;
	}

	private static Map<Integer, Integer> getPrimeFactor(int p) {
		Map<Integer, Integer> map = new HashMap<>();
		while (p % 2 == 0) {
			insertToMap(2, map);
			p /= 2;
		}

		for (int i = 3; i <= Math.sqrt(p); i += 2) {
			while (p % i == 0) {
				insertToMap(i, map);
				p /= i;
			}
		}

		if (p > 2)
			insertToMap(p, map);
		return map;
	}

	private static void insertToMap(int i, Map<Integer, Integer> map) {
		map.merge(i, 1, Integer::sum);
	}
}