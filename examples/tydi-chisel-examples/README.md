# Tydi-Chisel code examples
This directory contains some Tydi code examples and their corresponding Tydi-Chisel representations.
The [`tydi-lang compiler`](https://github.com/twoentartian/tydi-lang-2) and the 
[`tydi-lang-2 transpiler`](https://github.com/ccromjongh/tydi-lang-2-chisel) are used to generate Tydi-Chisel 
boilerplate code from Tydi source code.
Subsequently, the behavioral code must be manually added to the generated Tydi-Chisel boilerplate code.

The examples serve as a basis for analyzing the representation methods of the testing frameworks in relation to 
Tydi-Chisel code. 
Specifically, there are three main characteristics that are of interest for this analysis:
1. Firstly, how the testing frameworks at state-of-the-art represent tydi types and streams.
2. Secondly, how nested tydi groups results in testing frameworks.
3. Finally, how nested streams are treated and what representation is used for them. 

The `tydi-chisel-examples` is organized as follows:
- [`tydi`](./tydi): Contains the Tydi source code examples.
- [`chisel`](./chisel): Contains the corresponding Tydi-Chisel code example.

All the examples are related each other and they have an increasing complexity.
- [`HelloWorldRgb`](./tydi/src/HelloWorldRgb/): A simple example that instantiates two streams of group `Rgb`. It is one of the simplest examples that can be used. It simply connects inputs to outputs.
- [`PixelConverter`](./tydi/src/PixelConverter/): It extends the `HelloWorldRgb` example by adding more complex data structures that uses `Unions` (similar to `enum`s) and `Groups` (similar to `struct`s). It implements a color scale converter from rgb to grayscale and viceversa, depending on what type is the input stream since can be either rgb or gray. Moreover, it shows how to use tydi-lang templates to define custom and reusable types.
- [`CLikeStaticArray`](./tydi/src/CLikeStaticArray/): It shows how to use advanced tydi-lang features such as the usage of for loops to instantiate fields in groups/unions/streamlets.
- [`PipelineSimple`](./tydi/src/PipelineSimple/): A simple streaming pipeline that implements the following simple spark code, where the input is a stream of integers with timestamps attached (`{time: unsigned Integer, value: Integer}`):
  ```scala
  // Input stream: {timestamp, value}
  // Output stream: {min_value, max_value, sum_value, avg_value}
  df.filter(col("value") >= 0)
      .groupBy("timestamp")
      .agg( 
          min("value").as("min_value"), 
          max("value").as("max_value"), 
          sum("value").as("sum_value"), 
          avg("value").as("avg_value")
          )
      .select("min_value", "max_value", "sum_value", "avg_value")
        
  ```
- [`PipelineNestedGroup`](./tydi/src/PipelineNestedGroup/): It extends the `PipelineSimple` by adding a nested group to the input stream (`{time: unsigned Integer, value: Integer, date: DateTime}`). The `DateTime` can be considered as `{month: unsigned Integer, day: unsigned Integer, year: unsigned Integer, utc: Integer}`. It filters every date that is not in the Amsterdam time zone (UTC+1) and every value that is negative. The corresponding spark code is the following:
  ```scala
  // Input stream: {timestamp, value, date}
  // Output stream: {min_value, max_value, sum_value, avg_value}
  df.filter(col("value") >= 0 && col("date").utc() >= +1)
      .groupBy("timestamp")
      .agg( 
          min("value").as("min_value"), 
          max("value").as("max_value"), 
          sum("value").as("sum_value"), 
          avg("value").as("avg_value")
          )
      .select("min_value", "max_value", "sum_value", "avg_value")
  ```
- [`PipelineNestedStreams`]: It introduces the concept of nested streams to the `PipelineNestedGroup` example. In particular it associates a string (of undefined length) to the input and output streams. A string is a sequence of `char`s and in hardware it can be represented by a stream of `char`s since its length is unknown. The corresponding spark code is the following:
  ```scala
  // Input stream: {timestamp, value, date, string}
  // Output stream: {min_value, max_value, sum_value, avg_value, string}
  df.filter(col("value") >= 0 && col("date").utc() >= +1)
      .groupBy("timestamp")
      .agg( 
          min("value").as("min_value"), 
          max("value").as("max_value"), 
          sum("value").as("sum_value"), 
          avg("value").as("avg_value")
          )
      .select("min_value", "max_value", "sum_value", "avg_value", "string")
  ```

> **Note:** The examples are used to perform the analysis of the testing frameworks. Results are reported in the 
> [results](/results) directory.

# References
- Johan Peltenburg et al. **“Tydi: An Open Specification for Complex Data Structures Over Hardware Streams”**. In: *IEEE Micro 40.4 (July 1, 2020), pp. 120–130. ISSN: 0272-1732, 1937-4143*. DOI: [10.1109/MM.2020.2996373](https://doi.org/10.1109/MM.2020.2996373).
- Yongding Tian et al. **“Tydi-lang: A Language for Typed Streaming Hardware”**. In: *Proceedings of the SC ’23 Workshops of The International Conference on High Performance Computing, Network, Storage, and Analysis*. Denver CO USA: ACM, Nov. 12, 2023, pp. 521–529. ISBN: 9798400707858. DOI: [10.1145/3624062.3624539](https://doi.org/10.1145/3624062.3624539).
- Casper Cromjongh et al. **“Tydi-Chisel: Collaborative and Interface-Driven Data-Streaming Accelerators”**. In: *2023 IEEE Nordic Circuits and Systems Conference (NorCAS*). Aalborg, Denmark: IEEE, Oct. 31, 2023, pp. 1–7. ISBN: 9798350337570. DOI: [10.1109/NorCAS58970.2023.10305451](https://doi.org/10.1109/NorCAS58970.2023.10305451).

# Related repositories
- [`Tydi-lang-2`](https://github.com/twoentartian/tydi-lang-2). The Tydi-lang compiler.
- [`Tydi-Chisel`](https://github.com/ccromjongh/Tydi-Chisel). An implementation of Tydi interfaces and concepts in Chisel.
- [`tydi-lang-2-chisel`](https://github.com/ccromjongh/tydi-lang-2-chisel). The Tydi-lang-2-Chisel transpiler.