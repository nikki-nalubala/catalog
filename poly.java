import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class poly {
    public static void main(String[] args) throws Exception {
        // Change filename as needed
        String filename = "testcase2.json";
        JSONObject root = (JSONObject) new JSONParser().parse(new FileReader(filename));
        JSONObject keys = (JSONObject) root.get("keys");
        int n = Integer.parseInt(keys.get("n").toString());
        int k = Integer.parseInt(keys.get("k").toString());

        // Store points as (x, y)
        List<BigInteger> xList = new ArrayList<>();
        List<BigInteger> yList = new ArrayList<>();

        for (Object key : root.keySet()) {
            if (key.equals("keys")) continue;
            int x = Integer.parseInt(key.toString());
            JSONObject entry = (JSONObject) root.get(key);
            String baseStr = entry.get("base").toString();
            String valStr = entry.get("value").toString();
            int base = Integer.parseInt(baseStr);
            // Use BigInteger to decode large values
            BigInteger y = new BigInteger(valStr, base);
            xList.add(BigInteger.valueOf(x));
            yList.add(y);
        }

        // Use only the first k points to uniquely solve for a degree k-1 polynomial
        // (Choose any k points, here we use the first k)
        List<BigInteger> xK = xList.subList(0, k);
        List<BigInteger> yK = yList.subList(0, k);

        // Compute the secret using Lagrange interpolation at x=0
        BigInteger c = lagrangeInterpolationAtZero(xK, yK);

        System.out.println("Secret (constant term c) = " + c.toString());
    }

    // Lagrange interpolation for f(0): c = Σ y_i * L_i(0)
    static BigInteger lagrangeInterpolationAtZero(List<BigInteger> xs, List<BigInteger> ys) {
        BigInteger result = BigInteger.ZERO;
        int k = xs.size();
        for (int i = 0; i < k; i++) {
            // Compute L_i(0)
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                numerator = numerator.multiply(xs.get(j).negate());
                denominator = denominator.multiply(xs.get(i).subtract(xs.get(j)));
            }
            // L_i(0) = numerator / denominator (use exact division, no modulus)
            BigInteger li0 = numerator.divide(denominator);
            result = result.add(ys.get(i).multiply(li0));
        }
        return result;
    }
}
