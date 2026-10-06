package ada.matmul;

/** Matrix multiplication for matrices of any compatible size. */
public final class MatrixMultiplier {

    private MatrixMultiplier() {
    }

    /**
     * Multiplies an (m x n) matrix by an (n x p) matrix.
     * The loops use i-k-j order so that the innermost loop walks through
     * rows of b and c sequentially, which is cache friendly.
     *
     * @throws IllegalArgumentException if a matrix is null, empty or ragged,
     *                                  or if the columns of a differ from the rows of b
     */
    public static double[][] multiply(double[][] a, double[][] b) {
        int n = validate(a, "a");
        int p = validate(b, "b");
        if (n != b.length) {
            throw new IllegalArgumentException(String.format(
                    "incompatible sizes: a has %d columns but b has %d rows", n, b.length));
        }

        int m = a.length;
        double[][] c = new double[m][p];
        for (int i = 0; i < m; i++) {
            double[] ai = a[i];
            double[] ci = c[i];
            for (int k = 0; k < n; k++) {
                double aik = ai[k];
                double[] bk = b[k];
                for (int j = 0; j < p; j++) {
                    ci[j] += aik * bk[j];
                }
            }
        }
        return c;
    }

    /** Checks that the matrix is non-empty and rectangular; returns its column count. */
    private static int validate(double[][] matrix, String name) {
        if (matrix == null || matrix.length == 0 || matrix[0] == null || matrix[0].length == 0) {
            throw new IllegalArgumentException("matrix " + name + " must not be null or empty");
        }
        int cols = matrix[0].length;
        for (double[] row : matrix) {
            if (row == null || row.length != cols) {
                throw new IllegalArgumentException("matrix " + name + " must be rectangular");
            }
        }
        return cols;
    }
}