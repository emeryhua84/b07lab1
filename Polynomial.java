import java.io.*;
import java.util.*;

/**
 * CSCB07 - Lab 2
 * Sparse Polynomial represented by two parallel arrays:
 *  - coeffs[i]   : non-zero coefficient (double)
 *  - exponents[i]: corresponding exponent (int)
 *
 * Invariant:
 *  - coeffs.length == exponents.length
 *  - exponents are strictly increasing
 *  - all coeffs are non-zero
 */
public class Polynomial {

    private double[] coeffs;
    private int[] exponents;

    /** Empty (zero) polynomial */
    public Polynomial() {
        this.coeffs = new double[0];
        this.exponents = new int[0];
    }

    public Polynomial(double[] coeffs, int[] exponents) {
        if (coeffs == null || exponents == null || coeffs.length != exponents.length)
            throw new IllegalArgumentException("Arrays must be non-null and same length.");
        TreeMap<Integer, Double> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            if (Math.abs(coeffs[i]) < 1e-12) continue; // ignore zeros
            map.put(exponents[i], map.getOrDefault(exponents[i], 0.0) + coeffs[i]);
        }
        setFromMap(map);
    }

    public Polynomial(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line == null) line = "";
            parseFromString(line.trim());
        }
    }

    public double evaluate(double x) {
        double sum = 0.0;
        for (int i = 0; i < coeffs.length; i++) {
            sum += coeffs[i] * Math.pow(x, exponents[i]);
        }
        return sum;
    }

    public Polynomial add(Polynomial other) {
        TreeMap<Integer, Double> map = toMap();
        for (int i = 0; i < other.coeffs.length; i++) {
            int e = other.exponents[i];
            map.put(e, map.getOrDefault(e, 0.0) + other.coeffs[i]);
        }
        return fromMap(map);
    }

    public Polynomial multiply(Polynomial other) {
        TreeMap<Integer, Double> map = new TreeMap<>();
        for (int i = 0; i < this.coeffs.length; i++) {
            for (int j = 0; j < other.coeffs.length; j++) {
                int e = this.exponents[i] + other.exponents[j];
                double c = this.coeffs[i] * other.coeffs[j];
                if (Math.abs(c) < 1e-12) continue;
                map.put(e, map.getOrDefault(e, 0.0) + c);
            }
        }
        return fromMap(map);
    }

    /**
     * Lab 2 (e): Save to file in textual format similar to parsing format,
     * e.g., "5-3x2+7x8". We always output coefficients (including 1/-1)
     * to ensure it parses unambiguously.
     */
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            pw.println(toCompactString());
        }
    }

    /** Human-friendly representation. */
    @Override
    public String toString() {
        if (coeffs.length == 0) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            double c = coeffs[i];
            int e = exponents[i];
            if (i > 0) sb.append(c >= 0 ? " + " : " - ");
            else if (c < 0) sb.append("-");
            sb.append(numToString(Math.abs(c)));
            if (e != 0) {
                sb.append("x");
                if (e != 1) sb.append("^").append(e);
            }
        }
        return sb.toString();
    }


    private TreeMap<Integer, Double> toMap() {
        TreeMap<Integer, Double> map = new TreeMap<>();
        for (int i = 0; i < coeffs.length; i++) {
            map.put(exponents[i], coeffs[i]);
        }
        return map;
    }

    private static Polynomial fromMap(TreeMap<Integer, Double> map) {
        // drop near-zeros
        map.entrySet().removeIf(e -> Math.abs(e.getValue()) < 1e-12);
        int n = map.size();
        double[] c = new double[n];
        int[] e = new int[n];
        int i = 0;
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            e[i] = entry.getKey();
            c[i] = entry.getValue();
            i++;
        }
        return new Polynomial(c, e);
    }

    private void setFromMap(TreeMap<Integer, Double> map) {
        map.entrySet().removeIf(e -> Math.abs(e.getValue()) < 1e-12);
        int n = map.size();
        this.coeffs = new double[n];
        this.exponents = new int[n];
        int i = 0;
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            this.exponents[i] = entry.getKey();
            this.coeffs[i] = entry.getValue();
            i++;
        }
    }

    private void parseFromString(String s) {
        // Normalize: turn "5-3x2+7x8" -> "5+-3x2+7x8", then split by '+'
        String norm = s.replace("-", "+-");
        String[] parts = norm.split("\\+");
        TreeMap<Integer, Double> map = new TreeMap<>();
        for (String raw : parts) {
            String t = raw.trim();
            if (t.isEmpty()) continue;

            double c;
            int e;

            if (t.contains("x")) {
                int xPos = t.indexOf('x');
                String cPart = t.substring(0, xPos);
                String ePart = t.substring(xPos + 1); // may be "" or like "2", "10", etc.

                // coefficient
                if (cPart.equals("") || cPart.equals("+")) c = 1.0;
                else if (cPart.equals("-")) c = -1.0;
                else c = Double.parseDouble(cPart);

                // exponent
                if (ePart.isEmpty()) e = 1;
                else {
                    // If format is like "x2" we expect digits only; if caret existed, handle both.
                    if (ePart.startsWith("^")) ePart = ePart.substring(1);
                    e = Integer.parseInt(ePart);
                }
            } else {
                // constant term
                c = Double.parseDouble(t);
                e = 0;
            }

            if (Math.abs(c) < 1e-12) continue;
            map.put(e, map.getOrDefault(e, 0.0) + c);
        }
        setFromMap(map);
    }

    /** Compact textual form matching parser (e.g., "5-3x2+7x8"). */
    private String toCompactString() {
        if (coeffs.length == 0) return "0";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < coeffs.length; i++) {
            double c = coeffs[i];
            int e = exponents[i];
            String termCoeff = numToString(Math.abs(c));
            String term;
            if (e == 0) {
                term = termCoeff;
            } else if (e == 1) {
                term = termCoeff + "x";
            } else {
                term = termCoeff + "x" + e; // no caret, matches example "7x8"
            }
            if (i == 0) {
                sb.append(c < 0 ? "-" : "").append(term);
            } else {
                sb.append(c < 0 ? "-" : "+").append(term);
            }
        }
        return sb.toString();
    }

    private String numToString(double v) {
        // Print integers without .0
        long asLong = Math.round(v);
        if (Math.abs(v - asLong) < 1e-12) return Long.toString(asLong);
        return Double.toString(v);
    }

    /* Expose arrays for minimal testing / marking if desired */
    public double[] getCoeffs() { return Arrays.copyOf(coeffs, coeffs.length); }
    public int[] getExponents() { return Arrays.copyOf(exponents, exponents.length); }
}
