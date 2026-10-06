# ChatGPT session - Task 4

## Prompt 1 - original task

> Write 2D matrix slicing on NumPy matrices and Java. Show the results of both implementations on a graphical image so that the outputs can be compared.

### Answer summary

ChatGPT created a program where the user enters a matrix and a slice, and the selected part of the matrix is displayed as a grayscale image.

- **NumPy:** used NumPy slicing and Pillow.
- **Java:** used a 2D array, loops for slicing, and a grayscale image.
- The implementation supported basic `start:end` slicing.
- It did not initially support negative indices, steps, or empty slice parts.
- It also did not automatically compare the generated images.

### My test

I tested the generated code using a 5 × 5 matrix containing values from 1 to 25. I selected rows `1:4` and columns `1:4`.

Both implementations produced:

```text
7 8 9
12 13 14
17 18 19
```

However, the generated images were not identical.

```text
NumPy size: (300, 300, 3)
Java size: (300, 300, 3)
Different pixels: 70000 of 90000
```

The matrix slicing itself was correct, but the image representation was different.

## Prompt 2 - improvements

> Improve the solution so that it supports full NumPy slicing using `start:stop:step`, including negative indices, negative steps, empty values, and values outside the matrix. Also explain and fix the difference between the Java and NumPy images. Improve the Java implementation if possible and add an automatic comparison proving that both results are identical.

### Answer summary

ChatGPT identified the image problem as being related to how Java was writing grayscale values using `setRGB` with `TYPE_BYTE_GRAY`.

The improved version:

- supports `start:stop:step` slicing;
- supports negative indices and negative steps;
- uses a flat row-major array in Java;
- writes grayscale values directly to the image raster;
- adds automatic comparison of the sliced matrices;
- adds pixel-by-pixel image comparison.

### My test

For rows `1:4` and columns `1:4`:

```text
PASS: sliced matrices are exactly identical.
Different pixels: 0 of 90000
PASS: images are pixel-for-pixel identical.
```

I also tested more advanced slicing:

```text
Rows:    ::-1
Columns: -4:-1:2
```

The comparison produced:

```text
PASS: sliced matrices are exactly identical.
Different pixels: 0 of 100000
PASS: images are pixel-for-pixel identical.
```

## Conclusion

The first ChatGPT solution worked for basic matrix slicing, but testing revealed that the generated images were not actually identical. After I provided the pixel-comparison result in the second prompt, ChatGPT identified the image-conversion problem and improved the implementation.

The final version supports more complete NumPy-style slicing and automatically verifies both the matrix results and the generated images.