# Task 3: Matrix Multiplication in NumPy and Java

## 1. Goal

Implement matrix multiplication for matrices of any compatible size (C = A·B, with A of size M×N and B of size N×P) in:

- **Python** with NumPy (plus a pure-Python loop version as a baseline),
- **Java** (the C-like language), with unit tests,

and compare code size and execution time.

## 2. Repository layout

```
task3/code/
├── Java/
│   ├── src/ada/matmul/MatrixMultiplier.java   # implementation
│   ├── src/ada/matmul/Main.java               # benchmark runner
│   ├── test/ada/matmul/MatrixMultiplierTest.java  # JUnit 5 tests
│   ├── build.ps1                              # compile + run tests
│   └── run.ps1                                # compile + run benchmark
├── Python/
│   ├── python_code.py                         # NumPy + loop implementations
│   └── requirements.txt
└── benchmark.py                               # runs both, writes results.csv
```

## 3. Implementation

**Java (`MatrixMultiplier.multiply`).** Plain triple loop in *i-k-j* order. The innermost loop walks along rows of `b` and `c`, which are contiguous in memory for Java's row-major arrays, so it is cache friendly. The method validates input (null, empty, ragged, or incompatible shapes throw `IllegalArgumentException`) and does not modify its inputs.

**Python (`python_code.py`).** Two implementations behind the same interface:

- `matmul_numpy`: `np.matmul`, which calls an optimized BLAS routine.
- `matmul_loops`: the same i-k-j triple loop as Java, written in pure Python.

**No hard-coded sizes.** Every size, repeat count and warm-up count comes from the command line. Both programs generate the same deterministic input matrices (`(i*7 + j*3 + seed) % 10`) and print a checksum (sum of all result entries), so the two languages can be checked against each other.

## 4. How to build and run

Requirements: JDK 17+ (tested with 25.0.1), Python 3 with NumPy (tested with 3.14). Maven is not needed.

```bash
# Unit tests (downloads the JUnit console jar on first run)
cd task3/code/Java
powershell -ExecutionPolicy Bypass -File ./build.ps1

# Java benchmark: <repeats> <warmup> <MxNxP>...
powershell -ExecutionPolicy Bypass -File ./run.ps1 5 2 100x100x100 200x50x80

# Python benchmark
cd ..
pip install -r Python/requirements.txt
python Python/python_code.py --dims 100x100x100 200x50x80 --repeats 5

# Both languages, same sizes, merged into results.csv
python benchmark.py --sizes 100x100x100 200x200x200 --repeats 5 --warmup 2
```

## 5. Unit tests (Java)

10 JUnit 5 tests in `MatrixMultiplierTest`:

| Test | What it checks |
|---|---|
| known square matrices | [[1,2],[3,4]]·[[5,6],[7,8]] = [[19,22],[43,50]] |
| identity | A·I = I·A = A |
| zero matrix | A·0 = 0 |
| non-square | 2×3 times 3×4 gives the expected 2×4 result |
| 1×1 | scalar product |
| row/column vectors | 1×3·3×1 = 1×1 and 3×1·1×3 = 3×3 |
| random vs reference | i-k-j result equals a naive i-j-k reference on 4 random shapes (seed 42) |
| inputs unchanged | `a` and `b` are not modified |
| incompatible sizes | throws `IllegalArgumentException` |
| null / empty / ragged | throws `IllegalArgumentException` |

Result:

```
[        10 tests found           ]
[        10 tests successful      ]
[         0 tests failed          ]
```

## 6. Code size

Counted as non-blank, non-comment lines.

| File | Total lines | Code lines | Bytes |
|---|---:|---:|---:|
| `MatrixMultiplier.java` (implementation) | 53 | 39 | 2008 |
| `Main.java` (benchmark runner) | 119 | 94 | 4008 |
| `MatrixMultiplierTest.java` | 123 | 106 | 4670 |
| `python_code.py` (both versions + benchmark) | 89 | 72 | 3041 |

The core operation is the clearest comparison:

| Version | Lines for the multiplication itself |
|---|---:|
| Java `multiply` method (with validation) | 22 |
| Python pure loops `matmul_loops` | 9 |
| NumPy `np.matmul(a, b)` | **1** |

NumPy delivers the same result in one call. Java needs explicit loops, allocation and validation, and it also needs a separate runner and a test class. The Python file is shorter overall because the benchmark logic fits in far fewer lines, but it contains no unit tests.

## 7. Execution time

**Setup.** Intel Core i5-10300H, 16 GB DDR4, Windows, Java 25.0.1, Python 3.14, NumPy 2.4.4. Square matrices, N×N×N. Each time is the **median of 5 runs** after warm-up (2 runs for Java so the JIT compiler can optimize the code; 1 for Python). Only the multiplication is timed. The pure-Python loop version is skipped when M·N·P exceeds 10 million (`--max-loop-ops`) because it would take minutes.

| N | Java i-k-j (s) | NumPy `matmul` (s) | Python loops (s) |
|---:|---:|---:|---:|
| 50 | 0.000373 | 0.000013 | 0.052001 |
| 100 | 0.001090 | 0.000226 | 0.400627 |
| 200 | 0.002111 | 0.000427 | 3.730673 |
| 400 | 0.016824 | 0.001371 | skipped |
| 800 | 0.186146 | 0.008306 | skipped |

Speed-ups from the table:

| N | NumPy vs Java | Java vs Python loops |
|---:|---:|---:|
| 50 | 29× | 139× |
| 100 | 4.8× | 367× |
| 200 | 4.9× | 1767× |
| 400 | 12× | n/a |
| 800 | 22× | n/a |

**Correctness cross-check.** The checksums of all three methods are identical for every size (for example 162000000.0 for N = 200 and 10368000000.0 for N = 800), so the Java and Python results agree.

## 8. Discussion

- **Pure Python is by far the slowest.** Every loop iteration goes through the interpreter, with type checks and object creation. Java compiles the same loop to machine code with the JIT, making it hundreds to over a thousand times faster at these sizes.
- **NumPy beats the same-algorithm Java code**, and the gap grows with size (about 5× at N = 100–200, 22× at N = 800). NumPy hands the work to a BLAS library, which uses blocking for cache, SIMD instructions and multiple threads. The Java version is a single-threaded scalar loop. For 800×800, Java reaches about 5.5 GFLOPS and NumPy about 123 GFLOPS (2·N³ operations divided by time).
- **Java time grows faster than N³ at larger sizes.** From N = 400 to 800 the work grows 8× but the time grows about 11×, because the matrices stop fitting in cache. Loop blocking would help, but it is outside the scope of this task.
- **Caveats.** Small-size timings (N = 50) are dominated by JIT warm-up and timer resolution and are noisy. The comparison is not "Java vs Python" in general: NumPy is fast because its core is compiled, multithreaded library code, not because of Python itself.



