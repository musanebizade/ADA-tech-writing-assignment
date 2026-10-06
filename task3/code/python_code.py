import argparse
import statistics
import time

import numpy as np


def make_matrix(rows, cols, seed):
    """Deterministic test matrix; the same formula is used in the Java program."""
    i, j = np.indices((rows, cols))
    return ((i * 7 + j * 3 + seed) % 10).astype(np.float64)


def check_shapes(a, b):
    if a.ndim != 2 or b.ndim != 2:
        raise ValueError("both arguments must be 2D arrays")
    if a.shape[1] != b.shape[0]:
        raise ValueError(
            f"incompatible shapes {a.shape} and {b.shape}: "
            "columns of A must equal rows of B"
        )


def matmul_loops(a, b):
    """Triple loop in i-k-j order (row-major friendly)."""
    check_shapes(a, b)
    m, n = a.shape
    p = b.shape[1]
    c = np.zeros((m, p))
    for i in range(m):
        for k in range(n):
            a_ik = a[i, k]
            for j in range(p):
                c[i, j] += a_ik * b[k, j]
    return c


def matmul_numpy(a, b):
    """Matrix product using NumPy's optimized implementation."""
    check_shapes(a, b)
    return np.matmul(a, b)


def parse_dims(text):
    try:
        m, n, p = (int(x) for x in text.lower().split("x"))
    except ValueError:
        raise argparse.ArgumentTypeError(f"'{text}' is not in MxNxP form")
    if min(m, n, p) <= 0:
        raise argparse.ArgumentTypeError("dimensions must be positive")
    return m, n, p


def time_median(func, a, b, repeats, warmup):
    """Return (median seconds, last result); only the multiplication is timed."""
    for _ in range(warmup):
        func(a, b)
    times = []
    result = None
    for _ in range(repeats):
        start = time.perf_counter()
        result = func(a, b)
        times.append(time.perf_counter() - start)
    return statistics.median(times), result


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--dims", nargs="+", type=parse_dims, required=True,
                        metavar="MxNxP", help="matrix sizes to multiply")
    parser.add_argument("--repeats", type=int, default=5, help="timed runs per size")
    parser.add_argument("--warmup", type=int, default=1, help="untimed runs per size")
    parser.add_argument("--max-loop-ops", type=int, default=10_000_000,
                        help="skip the pure-loop version when M*N*P exceeds this")
    args = parser.parse_args()

    print("language,method,m,n,p,median_seconds,checksum")
    for m, n, p in args.dims:
        a, b = make_matrix(m, n, 0), make_matrix(n, p, 1)
        methods = [("np.matmul", matmul_numpy)]
        if m * n * p <= args.max_loop_ops:
            methods.append(("loops", matmul_loops))
        for name, func in methods:
            seconds, c = time_median(func, a, b, args.repeats, args.warmup)
            print(f"python,{name},{m},{n},{p},{seconds:.6f},{c.sum():.1f}")


if __name__ == "__main__":
    main()