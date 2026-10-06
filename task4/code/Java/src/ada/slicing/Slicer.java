package ada.slicing;

/** 2D matrix slicing, equivalent to NumPy's {@code m[rows, cols]} with slice objects. */
public final class Slicer {

    private Slicer() {
    }

    /** Returns a new matrix holding the selected rows and columns of {@code m}. */
    public static int[][] slice(int[][] m, String rowSpec, String colSpec) {
        if (m == null || m.length == 0 || m[0].length == 0) {
            throw new IllegalArgumentException("matrix must not be null or empty");
        }
        Range rows = Range.parse(rowSpec, m.length);
        Range cols = Range.parse(colSpec, m[0].length);
        if (rows.size() == 0 || cols.size() == 0) {
            throw new IllegalArgumentException("the slice selects no elements");
        }
        int[][] out = new int[rows.size()][cols.size()];
        for (int i = 0, r = rows.start(); i < out.length; i++, r += rows.step()) {
            for (int j = 0, c = cols.start(); j < out[i].length; j++, c += cols.step()) {
                out[i][j] = m[r][c];
            }
        }
        return out;
    }
}
