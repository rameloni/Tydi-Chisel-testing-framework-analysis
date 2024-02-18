# Tydi-Chisel code examples

This directory contains some Tydi-lang code[^1] examples and their corresponding Tydi-Chisel[^2] representations.
The `tydi-lang-2` compiler[^3] and `tydi-lang-2-chisel` transpiler[^4] are used to generate Tydi-Chisel boilerplate code[^5] from a Tydi source code.
Subsequently, the behavioral code must be manually added to the generated Tydi-Chisel boilerplate code.

> The examples in [tydi](tydi) directory are written in `tydi-lang-2` code.
> For a full syntax description of `tydi-lang-2` please refer to the [Tydi-lang syntax documentation](https://github.com/twoentartian/tydi-lang-2/blob/main/tydi-lang2-syntax.md).

The examples serve as a basis for analyzing the representation methods of the testing frameworks in relation to Tydi-lang and Tydi-Chisel code.
Specifically, there are three main characteristics that are of interest for this analysis:
1. Firstly, how the testing frameworks at state-of-the-art represent tydi types and streams.
2. Secondly, how nested tydi groups results in testing frameworks.
3. Finally, how nested streams are treated and what representation is used for them.

The `tydi-chisel-examples` is organized as follows:
- The [tydi](./tydi) subfolder contains the Tydi source code examples.
- The [chisel](./chisel) subfolder contains the corresponding Tydi-Chisel code example.

All the examples are related each other, and they have an increasing complexity.
- [HelloWorldRgb](./tydi/src/HelloWorldRgb): A simple example that instantiates two streams of group `Rgb`. 
  It is one of the simplest examples that can be made.
  It simply interconnects inputs to outputs.
- [PixelConverter](./tydi/src/PixelConverter): It extends the `HelloWorldRgb` example by adding more complex data structures.
  It uses `Union`s (similar to C `union`s) and `Groups` (similar to software `struct`s). 
  It implements a color scale converter from rgb to grayscale and vice versa. 
  Depending on what type is the input stream since can be either rgb or gray it converts its input to the other color-scale. 
  Moreover, it also makes use of tydi-lang templates to define custom and reusable types.
- [CLikeStaticArray](./tydi/src/CLikeStaticArray): It shows how to use advanced tydi-lang features such as the usage of for loops to instantiate fields in groups/unions/streamlets.
- [PipelineSimple](./tydi/src/PipelineSimple): A simple streaming pipeline that implements the following simple spark code, in which the input is a stream of integers with timestamps attached (`{time: unsigned Integer, value: Integer}`):
  ```scala
  // Input stream: {timestamp, value}
  // Output stream: {min_value, max_value, sum_value, avg_value}
  df.filter(col("value") >= 0)
      .agg( 
          min("value").as("min_value"), 
          max("value").as("max_value"), 
          sum("value").as("sum_value"), 
          avg("value").as("avg_value")
          )
      .select("min_value", "max_value", "sum_value", "avg_value")
        
  ```
- [PipelineNestedGroup](./tydi/src/PipelineNestedGroup): It extends the `PipelineSimple` by adding a nested group to the input stream (`{time: unsigned Integer, value: Integer, date: DateTime}`).
  The `DateTime` can be encoded as `{month: unsigned Integer, day: unsigned Integer, year: unsigned Integer, utc: Integer}`. 
  It filters every date that is not in the Amsterdam time zone (UTC+1) and every value that is negative. 
  The respective spark code is the following:
  ```scala
  // Input stream: {timestamp, value, date}
  // Output stream: {min_value, max_value, sum_value, avg_value}
  df.filter(col("value") >= 0 && col("date").utc() >= +1)
      .agg( 
          min("value").as("min_value"), 
          max("value").as("max_value"), 
          sum("value").as("sum_value"), 
          avg("value").as("avg_value")
          )
      .select("min_value", "max_value", "sum_value", "avg_value")
  ```
- [PipelineNestedStreams](./tydi/src/PipelineNestedStream): It introduces the concept of nested streams to the `PipelineNestedGroup` example. 
  Specifically, it associates a string (of undefined length) to the input and output streams. 
  A string can be seen as sequence of `char`s and in tydi it can be represented by a stream of `char`s.
  Since its length is unknown, neither static arrays nor groups can represent it. 
  The respective spark code is the following:
  ```scala
  // Input stream: {timestamp, value, date, string}
  // Output stream: {min_value, max_value, sum_value, avg_value, string}
  df.filter(col("value") >= 0 && col("date").utc() >= +1)
      .agg( 
          min("value").as("min_value"), 
          max("value").as("max_value"), 
          sum("value").as("sum_value"), 
          avg("value").as("avg_value")
          )
      .select("min_value", "max_value", "sum_value", "avg_value", "string")
  ```

> **Note:** The examples are used to perform the analysis of the testing frameworks. 
> Results are reported in the [results](/results) directory.

Each example aims to test the representation of a particular Tydi feature:
how it is represented in the testing frameworks by using the Tydi-Chisel library backend.

| Example                 | Target                                                                                                                   |
|:------------------------|:-------------------------------------------------------------------------------------------------------------------------|
| `HelloWorldRgb`         | Explore the representation of `Groups` and `Streams` in testing frameworks.                                              |
| `PixelConverter`        | Understand how `Unions` are depicted in testing frameworks.                                                              |
| `PipelineSimple`        | See how multiple tydi modules are represented and how differences in stream types can be deducted in testing frameworks. |
| `PipelineNestedGroup`   | Analyze the impact of nested tydi groups on testing frameworks.                                                          |
| `PipelineNestedStreams` | Examine the impact of nested streams on testing frameworks.                                                              |

# References

[^1]: Yongding Tian et al. **“Tydi-lang: A Language for Typed Streaming Hardware”**. In: *Proceedings of the SC ’23
Workshops of The International Conference on High Performance Computing, Network, Storage, and Analysis*. Denver CO USA:
ACM, Nov. 12, 2023, pp. 521–529. ISBN:
9798400707858. [![10.1145/3624062.3624539](https://zenodo.org/badge/DOI/10.1145/3624062.3624539.svg)](https://doi.org/10.1145/3624062.3624539)

[^2]: Casper Cromjongh et al. **“Tydi-Chisel: Collaborative and Interface-Driven Data-Streaming Accelerators”**. In:
*2023 IEEE Nordic Circuits and Systems Conference (NorCAS*). Aalborg, Denmark: IEEE, Oct. 31, 2023, pp. 1–7. ISBN:
9798350337570. [![10.1109/NorCAS58970.2023.10305451](https://zenodo.org/badge/DOI/10.1109/NorCAS58970.2023.10305451.svg)](https://doi.org/10.1109/NorCAS58970.2023.10305451)

[^3]: The Tydi-lang-2
compiler. [![Tydi-lang-2](https://img.shields.io/badge/Github_Page-Tydi--lang--2-green)](https://github.com/twoentartian/tydi-lang-2)

[^4]: The Tydi-lang-2-Chisel
transpiler. [![tydi-lang-2-chisel](https://img.shields.io/badge/Github_Page-tydi--lang--2--chisel-green)](https://github.com/ccromjongh/tydi-lang-2-chisel)

[^5]: A implementation of Tydi interfaces and concepts in
Chisel. [![Tydi-Chisel](https://img.shields.io/badge/Github_Page-Tydi--Chisel-green)](https://github.com/ccromjongh/Tydi-Chisel)