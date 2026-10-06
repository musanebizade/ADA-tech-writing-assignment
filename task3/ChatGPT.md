
# ChatGPT session - Task 3

## Prompt 1 - the original task

> Implement matrix multiplication using NumPy arrays and Java. Write unit tests for the Java implementation to make sure the multiplication is correct. Perform an analysis of the code size and execution time.

### Answer (summary)

- **NumPy:** implemented matrix multiplication using `np.matmul()`.
- **Java:** implemented matrix multiplication using nested loops and dimension validation.
- **Tests:** suggested tests for known results, identity matrices, and invalid dimensions.
- **Benchmark:** suggested measuring execution time for different matrix sizes.
- **Complexity:** matrix multiplication requires O(MNP) operations.
- **Loop order:** initially used the standard i-j-k order.

### Missing compared with the assignment rules

- Some examples and matrix sizes were hard-coded.
- Input parameters were not fully user-controlled.
- Too few benchmark runs could produce unreliable measurements.
- The loop order could be improved for better memory access.
- More comprehensive test coverage was needed.

## Prompt 2 - steering

> Improve the solution so it works for arbitrary matrix dimensions without hard-coded sizes. Add input validation, clear compile/run instructions, Java unit tests, and repeated benchmark runs. Review the loop order for better memory access.

### Answer (summary)

- Changed the Java multiplication to **i-k-j** loop order for more sequential memory access.
- Added support for arbitrary matrix dimensions.
- Added validation for incompatible, empty, null, and ragged matrices.
- Added Java unit tests covering normal cases and invalid inputs.
- Added warm-up and repeated benchmark runs.
- Used deterministic matrix generation and checksums to compare results.
- Provided commands for compiling and running the Java and Python implementations.

## Running ChatGPT's final code

The Java tests passed successfully:

```text
10 tests found
10 tests successful
0 tests failed
```

Example benchmark results for 800 × 800 matrices:

| Implementation | Execution time |
|---|---:|
| Java i-k-j | 0.186146 s |
| NumPy | 0.008306 s |
| Python loops | Skipped |

NumPy was significantly faster than the Java implementation in this experiment because it uses optimized compiled numerical libraries. The pure-Python loop implementation was skipped for this matrix size because of its high execution time.

Overall, ChatGPT helped improve the loop ordering, input validation, testing strategy, and benchmarking methodology. The suggested solution was evaluated against the assignment requirements rather than accepted without verification.