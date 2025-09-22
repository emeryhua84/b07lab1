public class Polynomial {
    // i. One field for coefficients
    private double[] coefficients;

    // ii. No-argument constructor (zero polynomial)
    public Polynomial() {
        this.coefficients = new double[]{0};
    }

    // iii. Constructor with an array of double
    public Polynomial(double[] coeffs) {
        if (coeffs == null || coeffs.length == 0) {
            this.coefficients = new double[]{0};
        } else {
            // defensive copy
            this.coefficients = new double[coeffs.length];
            System.arraycopy(coeffs, 0, this.coefficients, 0, coeffs.length);
        }
    }

    // iv. add method
    public Polynomial add(Polynomial other) {
        int n = Math.max(this.coefficients.length, other.coefficients.length);
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            double a = (i < this.coefficients.length) ? this.coefficients[i] : 0;
            double b = (i < other.coefficients.length) ? other.coefficients[i] : 0;
            result[i] = a + b;
        }
        return new Polynomial(result);
    }

    // v. evaluate method
    public double evaluate(double x) {
        double sum = 0;
        double power = 1; // x^0 initially
        for (double c : coefficients) {
            sum += c * power;
            power *= x;
        }
        return sum;
    }

    // vi. hasRoot method
    public boolean hasRoot(double x) {
        return evaluate(x) == 0.0;
    }
}
