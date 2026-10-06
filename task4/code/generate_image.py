import argparse

import numpy as np
from PIL import Image, ImageDraw


def make_image(width, height):
    y, x = np.indices((height, width))
    pixels = np.stack([x * 255 // max(width - 1, 1),
                       y * 255 // max(height - 1, 1),
                       (x + y) * 255 // max(width + height - 2, 1)], axis=-1).astype(np.uint8)
    image = Image.fromarray(pixels, "RGB")
    draw = ImageDraw.Draw(image)
    u = min(width, height) // 8 or 1
    draw.rectangle([u, u, 3 * u, 2 * u], fill=(255, 255, 255))                 # white box, top-left
    draw.ellipse([width - 4 * u, height - 4 * u, width - u, height - u], fill=(0, 0, 0))  # black disc, bottom-right
    draw.line([0, 0, width - 1, height - 1], fill=(255, 0, 0), width=max(u // 4, 1))      # red diagonal
    draw.text((u, height // 2), "SLICE", fill=(255, 255, 0))
    return image


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("width", type=int)
    ap.add_argument("height", type=int)
    ap.add_argument("output")
    args = ap.parse_args()
    if min(args.width, args.height) < 8:
        ap.error("width and height must be at least 8")
    make_image(args.width, args.height).save(args.output)


if __name__ == "__main__":
    main()
