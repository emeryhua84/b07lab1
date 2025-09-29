import java.io.*;

/** Simple driver to demonstrate Lab 2 functionality. */
public class Driver {
    public static void main(String[] args) throws Exception {
        // Construct from arrays
        double[] c1 = {6, -2, 5};
        int[]    e1 = {0, 1, 3};     // 6 - 2x + 5x^3
        Polynomial p = new Polynomial(c1, e1);

        // Construct from file (expects one line e.g., 5-3x2+7x8)
        // If no command-line arg given, we create a temp file for demo.
        Polynomial q;
        if (args.length >= 1) {
            q = new Polynomial(new File(args[0]));
        } else {
            File tmp = new File("poly.txt");
            try (PrintWriter pw = new PrintWriter(new FileWriter(tmp))) {
                pw.println("5-3x2+7x8");
            }
            q = new Polynomial(tmp);
        }

        System.out.println("P(x) = " + p);
        System.out.println("Q(x) = " + q);

        Polynomial sum = p.add(q);
        System.out.println("P(x) + Q(x) = " + sum);

        Polynomial prod = p.multiply(q);
        System.out.println("P(x) * Q(x) = " + prod);

        // Evaluate at a few points
        double x = 2.0;
        System.out.println("P(" + x + ") = " + p.evaluate(x));
        System.out.println("Q(" + x + ") = " + q.evaluate(x));
        System.out.println("Prod(" + x + ") = " + prod.evaluate(x));

        // Save multiplied polynomial in textual format (parseable back)
        String out = "product.txt";
        prod.saveToFile(out);
        System.out.println("Saved product to " + out);

        // Re-read to verify parsing round-trip
        Polynomial prod2 = new Polynomial(new File(out));
        System.out.println("Re-loaded product: " + prod2);
    }
}
