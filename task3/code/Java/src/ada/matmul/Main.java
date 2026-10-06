package ada.matmul;

import java.util.Arrays;

/**
 * Command line benchmark for {@link MatrixMultiplier}.
 *
 * <pre>
 * Usage: Main &lt;repeats&gt; &lt;warmup&gt; &lt;MxNxP&gt; [&lt;MxNxP&gt; ...]
 * Example: Main 5 3 100x100x100 200x50x80
 * </pre>
 *
 * Prints one CSV row per size: language,method,m,n,p,median_seconds,checksum
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: Main <repeats> <warmup> <MxNxP> [<MxNxP> ...]");
            System.exit(1);
        }
        try {
            int repeats = parsePositive(args[0], "repeats");
            int warmup = parseNonNegative(args[1], "warmup");

            System.out.println("language,method,m,n,p,median_seconds,checksum");
            for (int i = 2; i < args.length; i++) {
                int[] d = parseDims(args[i]);
                run(d[0], d[1], d[2], repeats, warmup);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void run(int m, int n, int p, int repeats, int warmup) {
        double[][] a = makeMatrix(m, n, 0);
        double[][] b = makeMatrix(n, p, 1);

        // Warm-up runs let the JIT compiler optimize the code before measuring.
        double[][] c = null;
        for (int i = 0; i < warmup; i++) {
            c = MatrixMultiplier.multiply(a, b);
        }

        double[] seconds = new double[repeats];
        for (int i = 0; i < repeats; i++) {
            long start = System.nanoTime();
            c = MatrixMultiplier.multiply(a, b);
            seconds[i] = (System.nanoTime() - start) / 1e9;
        }
        Arrays.sort(seconds);
        double median = seconds[repeats / 2];
        if (repeats % 2 == 0) {
            median = (seconds[repeats / 2 - 1] + seconds[repeats / 2]) / 2;
        }
        System.out.printf("java,i-k-j,%d,%d,%d,%.6f,%.1f%n", m, n, p, median, checksum(c));
    }

    /** Deterministic test matrix; the same formula is used in the Python program. */
    static double[][] makeMatrix(int rows, int cols, int seed) {
        double[][] matrix = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = (i * 7 + j * 3 + seed) % 10;
            }
        }
        return matrix;
    }

    private static double checksum(double[][] matrix) {
        double sum = 0;
        for (double[] row : matrix) {
            for (double value : row) {
                sum += value;
            }
        }
        return sum;
    }

    private static int[] parseDims(String text) {
        String[] parts = text.toLowerCase().split("x");
        if (parts.length != 3) {
            throw new IllegalArgumentException("'" + text + "' is not in MxNxP form");
        }
        int[] dims = new int[3];
        for (int i = 0; i < 3; i++) {
            dims[i] = parsePositive(parts[i], "dimension");
        }
        return dims;
    }

    private static int parsePositive(String text, String name) {
        int value = parseInt(text, name);
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive: " + text);
        }
        return value;
    }

    private static int parseNonNegative(String text, String name) {
        int value = parseInt(text, name);
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative: " + text);
        }
        return value;
    }

    private static int parseInt(String text, String name) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " is not an integer: " + text);
        }
    }
}