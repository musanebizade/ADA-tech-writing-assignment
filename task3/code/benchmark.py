import argparse
import csv
import io
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent
JAVA = ROOT / "Java"


def run(cmd):
    r = subprocess.run(cmd, capture_output=True, text=True)
    if r.returncode:
        sys.exit(f"Command failed: {' '.join(cmd)}\n{r.stderr}")
    return r.stdout


def main():
    ap = argparse.ArgumentParser(description="Run Java and Python matmul benchmarks.")
    ap.add_argument("--sizes", nargs="+", default=["50x50x50", "100x100x100", "200x200x200",
                                                     "400x400x400", "800x800x800"])
    ap.add_argument("--repeats", type=int, default=5)
    ap.add_argument("--warmup", type=int, default=2)
    ap.add_argument("--output", default="results.csv")
    args = ap.parse_args()

    out = JAVA / "out"
    out.mkdir(exist_ok=True)
    sources = [str(p) for p in (JAVA / "src").rglob("*.java")]
    subprocess.run(["javac", "-d", str(out), *sources], check=True)

    java_csv = run(["java", "-cp", str(out), "ada.matmul.Main",
                    str(args.repeats), str(args.warmup), *args.sizes])
    py_csv = run([sys.executable, str(ROOT / "Python" / "python_code.py"),
                  "--dims", *args.sizes,
                  "--repeats", str(args.repeats), "--warmup", str(min(args.warmup, 1))])

    rows = list(csv.reader(io.StringIO(java_csv))) + list(csv.reader(io.StringIO(py_csv)))[1:]
    with open(ROOT / args.output, "w", newline="") as f:
        csv.writer(f).writerows(rows)
    for row in rows:
        print(",".join(row))


if __name__ == "__main__":
    main()