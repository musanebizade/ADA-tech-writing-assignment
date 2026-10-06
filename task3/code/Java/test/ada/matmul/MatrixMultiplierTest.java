package ada.matmul;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;

class MatrixMultiplierTest {

    private static final double EPS = 1e-9;

    private static void assertMatrixEquals(double[][] expected, double[][] actual) {
        assertEquals(expected.length, actual.length, "row count");
        for (int i = 0; i < expected.length; i++) {
            assertArrayEquals(expected[i], actual[i], EPS, "row " + i);
        }
    }

    /** Straightforward i-j-k reference used to cross-check the optimized loop order. */
    private static double[][] reference(double[][] a, double[][] b) {
        double[][] c = new double[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < b.length; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    @Test
    void multipliesKnownSquareMatrices() {
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{5, 6}, {7, 8}};
        assertMatrixEquals(new double[][]{{19, 22}, {43, 50}}, MatrixMultiplier.multiply(a, b));
    }

    @Test
    void identityKeepsMatrixUnchanged() {
        double[][] a = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        double[][] identity = {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}};
        assertMatrixEquals(a, MatrixMultiplier.multiply(a, identity));
        assertMatrixEquals(a, MatrixMultiplier.multiply(identity, a));
    }

    @Test
    void zeroMatrixGivesZeroResult() {
        double[][] a = {{1, 2}, {3, 4}};
        double[][] zero = new double[2][2];
        assertMatrixEquals(zero, MatrixMultiplier.multiply(a, zero));
    }

    @Test
    void multipliesNonSquareMatrices() {
        double[][] a = {{1, 2, 3}, {4, 5, 6}};            // 2 x 3
        double[][] b = {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}; // 3 x 4
        double[][] expected = {{38, 44, 50, 56}, {83, 98, 113, 128}}; // 2 x 4
        assertMatrixEquals(expected, MatrixMultiplier.multiply(a, b));
    }

    @Test
    void handlesOneByOneMatrices() {
        assertMatrixEquals(new double[][]{{6}},
                MatrixMultiplier.multiply(new double[][]{{2}}, new double[][]{{3}}));
    }

    @Test
    void handlesRowTimesColumnVectors() {
        double[][] row = {{1, 2, 3}};
        double[][] column = {{4}, {5}, {6}};
        assertMatrixEquals(new double[][]{{32}}, MatrixMultiplier.multiply(row, column));
        assertMatrixEquals(new double[][]{{4, 8, 12}, {5, 10, 15}, {6, 12, 18}},
                MatrixMultiplier.multiply(column, row));
    }

    @Test
    void matchesReferenceOnRandomMatrices() {
        Random random = new Random(42);
        int[][] shapes = {{3, 4, 5}, {7, 1, 2}, {10, 10, 10}, {1, 9, 1}};
        for (int[] s : shapes) {
            double[][] a = randomMatrix(random, s[0], s[1]);
            double[][] b = randomMatrix(random, s[1], s[2]);
            assertMatrixEquals(reference(a, b), MatrixMultiplier.multiply(a, b));
        }
    }

    @Test
    void doesNotModifyInputs() {
        double[][] a = {{1, 2}, {3, 4}};
        double[][] b = {{5, 6}, {7, 8}};
        MatrixMultiplier.multiply(a, b);
        assertMatrixEquals(new double[][]{{1, 2}, {3, 4}}, a);
        assertMatrixEquals(new double[][]{{5, 6}, {7, 8}}, b);
    }

    @Test
    void rejectsIncompatibleSizes() {
        double[][] a = new double[2][3];
        double[][] b = new double[2][3];
        assertThrows(IllegalArgumentException.class, () -> MatrixMultiplier.multiply(a, b));
    }

    @Test
    void rejectsNullEmptyAndRaggedMatrices() {
        double[][] valid = {{1, 2}, {3, 4}};
        double[][] ragged = {{1, 2}, {3}};
        assertThrows(IllegalArgumentException.class, () -> MatrixMultiplier.multiply(null, valid));
        assertThrows(IllegalArgumentException.class, () -> MatrixMultiplier.multiply(valid, new double[0][0]));
        assertThrows(IllegalArgumentException.class, () -> MatrixMultiplier.multiply(ragged, valid));
    }

    private static double[][] randomMatrix(Random random, int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        for (double[] row : matrix) {
            for (int j = 0; j < cols; j++) {
                row[j] = random.nextInt(21) - 10;
            }
        }
        return matrix;
    }
}