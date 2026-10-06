import argparse

import numpy as np
from PIL import Image


def parse_slice(text):
    parts = text.strip().split(":")
    if not 2 <= len(parts) <= 3:
        raise argparse.ArgumentTypeError(f"'{text}' is not in start:stop[:step] form")
    try:
        s = slice(*(int(p) if p.strip() else None for p in parts))
    except ValueError:
        raise argparse.ArgumentTypeError(f"'{text}' contains a non-integer")
    if s.step == 0:
        raise argparse.ArgumentTypeError("slice step cannot be zero")
    return s


def slice_2d(matrix, rows, cols):
    result = matrix[rows, cols]
    if result.size == 0:
        raise ValueError("the slice selects no elements")
    return result.copy()


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("input")
    ap.add_argument("output")
    ap.add_argument("--rows", type=parse_slice, default=slice(None), help="default: all rows")
    ap.add_argument("--cols", type=parse_slice, default=slice(None), help="default: all columns")
    args = ap.parse_args()

    pixels = np.array(Image.open(args.input).convert("RGBA"))
    try:
        result = slice_2d(pixels, args.rows, args.cols)
    except ValueError as e:
        ap.error(str(e))
    Image.fromarray(result, "RGBA").save(args.output)
    print(f"python: {pixels.shape[0]}x{pixels.shape[1]} -> {result.shape[0]}x{result.shape[1]}, saved {args.output}")


if __name__ == "__main__":
    main()
