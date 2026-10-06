# CSCI 6221 Advanced Software Paradigms - Assignment 2

Musa Nabizada

Each task has its own folder with a `README.md` report. Programming tasks also have a `code/` folder.

## Contents

| Task | Topic | Report | Code |
|---|---|---|---|
| 1 | Endianness (big vs little endian) | [task1/README.md](task1/README.md) | none |
| 2 | `__sizeof__()` of a tuple and a list in Python | [task2/README.md](task2/README.md) | in the report |
| 3 | Matrix multiplication: NumPy vs Java, unit tests, code size and timing | [task3/README.md](task3/README.md) | [task3/code](task3/code) |
| 4 | 2D matrix slicing on images: NumPy vs Java | [task4/README.md](task4/README.md) | [task4/code](task4/code) |

ChatGPT comparisons for the programming tasks: [task3/ChatGPT.md](task3/ChatGPT.md), [task4/ChatGPT.md](task4/ChatGPT.md).

## Repository structure

```
ADA-tech-writing-assignment/
├── README.md                      # this overview
├── task1/
│   └── README.md                  # endianness report
├── task2/
│   └── README.md                  # tuple vs list report
├── task3/
│   ├── README.md                  # report: tests, code size, timing
│   ├── ChatGPT.md                 # ChatGPT session and comparison
│   └── code/
│       ├── benchmark.py           # runs Java and Python, writes results.csv
│       ├── results.csv
│       ├── Python/
│       │   ├── python_code.py     # NumPy and pure-loop multiplication
│       │   └── requirements.txt
│       └── Java/
│           ├── build.ps1          # compile and run the unit tests
│           ├── run.ps1            # compile and run the benchmark
│           ├── src/ada/matmul/    # MatrixMultiplier.java, Main.java
│           └── test/ada/matmul/   # MatrixMultiplierTest.java (JUnit 5)
└── task4/
    ├── README.md                  # report: slicing and image comparison
    ├── ChatGPT.md                 # ChatGPT session and comparison
    ├── results/                   # comparison figures used in the report
    └── code/
        ├── demo.py                # runs both slicers, compares, draws the figure
        ├── generate_image.py      # creates a test image of any size
        ├── Python/slicer.py       # NumPy slicing
        ├── Java/src/ada/slicing/  # Range.java, Slicer.java, Main.java
        └── output/                # generated images
```

## Requirements

- JDK 17 or newer (tested with 25), Python 3 (tested with 3.14)
- `pip install numpy pillow`
- Maven is not needed.

## Quick start (tested on Windows with Git Bash)

```bash
# Task 3: unit tests, then benchmark
cd task3/code/Java
powershell -ExecutionPolicy Bypass -File ./build.ps1
cd ..
python benchmark.py

# Task 4: slice an image with both implementations and compare
cd ../../task4/code
python demo.py --size 600x400 --rows=50:350 --cols=100:500:2 --name crop
python demo.py --image my_photo.png --rows=::2 --cols=::2 --name half
```