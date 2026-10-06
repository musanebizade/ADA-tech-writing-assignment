package ada.slicing;

/** A resolved slice (start, stop, step) over an axis of known length, with Python/NumPy semantics. */
public record Range(int start, int stop, int step) {

    /** Parses "start:stop:step" (every part optional, negatives allowed) for an axis of the given length. */
    public static Range parse(String text, int length) {
        String[] parts = text.trim().split(":", -1);
        if (parts.length < 2 || parts.length > 3) {
            throw new IllegalArgumentException("'" + text + "' is not in start:stop[:step] form");
        }
        int step = parts.length == 3 && !parts[2].isBlank() ? toInt(parts[2]) : 1;
        if (step == 0) {
            throw new IllegalArgumentException("slice step cannot be zero");
        }
        int lower = step > 0 ? 0 : -1;       // smallest valid bound
        int upper = step > 0 ? length : length - 1;
        int start = parts[0].isBlank() ? (step > 0 ? lower : upper) : clamp(toInt(parts[0]), length, lower, upper);
        int stop = parts[1].isBlank() ? (step > 0 ? upper : lower) : clamp(toInt(parts[1]), length, lower, upper);
        return new Range(start, stop, step);
    }

    /** Number of selected indices. */
    public int size() {
        if (step > 0) {
            return Math.max(0, (stop - start + step - 1) / step);
        }
        return Math.max(0, (start - stop - step - 1) / -step);
    }

    private static int clamp(int index, int length, int lower, int upper) {
        if (index < 0) {
            index += length;
        }
        return Math.min(Math.max(index, lower), upper);
    }

    private static int toInt(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + text + "' is not an integer");
        }
    }
}
