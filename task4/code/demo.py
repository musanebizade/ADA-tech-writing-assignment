import argparse
import subprocess
import sys
from pathlib import Path

import numpy as np
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "output"


def run(cmd):
    r = subprocess.run(cmd, capture_output=True, text=True)
    if r.returncode:
        sys.exit(f"Command failed: {' '.join(map(str, cmd))}\n{r.stderr}")
    print(r.stdout.strip())


def labelled(image, title, height):
    """Scale to a common height and add a title strip."""
    w = max(1, round(image.width * height / image.height))
    panel = Image.new("RGB", (max(w, 120), height + 20), "white")
    panel.paste(image.convert("RGB").resize((w, height), Image.NEAREST), (0, 20))
    ImageDraw.Draw(panel).text((4, 4), title, fill="black")
    return panel


def figure(original, numpy_img, java_img, diff_img, path):
    h = 300
    panels = [labelled(original, f"original {original.width}x{original.height}", h),
              labelled(numpy_img, f"NumPy {numpy_img.width}x{numpy_img.height}", h),
              labelled(java_img, f"Java {java_img.width}x{java_img.height}", h),
              labelled(diff_img, "difference (black = identical)", h)]
    canvas = Image.new("RGB", (sum(p.width for p in panels) + 10 * (len(panels) + 1), h + 40), "white")
    x = 10
    for p in panels:
        canvas.paste(p, (x, 10))
        x += p.width + 10
    canvas.save(path)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    src = ap.add_mutually_exclusive_group()
    src.add_argument("--image", help="existing input image")
    src.add_argument("--size", default="600x400", help="generate a test image of WxH (default 600x400)")
    ap.add_argument("--rows", default="50:350", help="use --rows=VALUE for negative values")
    ap.add_argument("--cols", default="100:500:2", help="use --cols=VALUE for negative values")
    ap.add_argument("--name", default="case", help="prefix for the output files")
    args = ap.parse_args()

    OUT.mkdir(exist_ok=True)
    if args.image:
        source = Path(args.image)
    else:
        w, h = (int(v) for v in args.size.lower().split("x"))
        source = OUT / f"input_{w}x{h}.png"
        run([sys.executable, ROOT / "generate_image.py", str(w), str(h), source])

    java_out = ROOT / "Java" / "out"
    java_out.mkdir(exist_ok=True)
    run(["javac", "-d", java_out, *map(str, (ROOT / "Java" / "src").rglob("*.java"))])

    np_path, java_path = OUT / f"{args.name}_numpy.png", OUT / f"{args.name}_java.png"
    run([sys.executable, ROOT / "Python" / "slicer.py", source, np_path, f"--rows={args.rows}", f"--cols={args.cols}"])
    run(["java", "-cp", java_out, "ada.slicing.Main", source, args.rows, args.cols, java_path])

    a, b = np.array(Image.open(np_path).convert("RGBA")), np.array(Image.open(java_path).convert("RGBA"))
    same = a.shape == b.shape and np.array_equal(a, b)
    diff = np.abs(a.astype(int) - b.astype(int)).astype(np.uint8) if a.shape == b.shape else np.zeros_like(a)
    figure(Image.open(source), Image.open(np_path), Image.open(java_path),
           Image.fromarray(diff[..., :3]), OUT / f"{args.name}_comparison.png")
    print(f"[{args.name}] rows={args.rows} cols={args.cols}: {'IDENTICAL' if same else 'DIFFERENT'}")
    sys.exit(0 if same else 1)


if __name__ == "__main__":
    main()
