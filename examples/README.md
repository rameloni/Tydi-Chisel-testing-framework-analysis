# A collection of Chisel and Tydi-Chisel examples that support the analysis of the frameworks
This folder contains several examples of Chisel and Tydi-Chisel designs. 
The examples are used to support the analysis that is object of this repository. 
They are used to test the features of the testing frameworks and highlight eventual limitations.

The examples are divided into two folders: [chisel-examples](chisel-examples) and [tydi-chisel-examples](tydi-chisel-examples). The [chisel-examples](chisel-examples) contains sources of Chisel designs. The [tydi-chisel-examples](tydi-chisel-examples) folder contains Tydi-Chisel implementations. 

> **Note:** Each subfolder contains a readme in which the examples are detailed.

| Example                 | Target                                                                                                                |
| :---------------------- | :-------------------------------------------------------------------------------------------------------------------- |
| `Adder`                 | Explore the representation of `Array` and `Vec` in testing frameworks.                                                |
| `FSM` and `Parity`      | Understand how `ChiselEnum` and `Enum` are depicted in testing frameworks.                                            |
| `Functionality`         | Analyze the impact of various `assign values` methods on output representation in testing frameworks.                 |
| `Memory`                | Explore the representation of `Mem` in testing frameworks.                                                            |
| `Router`                | Examine the impact of various strategies for `representing constants` and `circuit components` in testing frameworks. |
| `HelloWorldRgb`         | Explore the representation of `Groups` and `Streams` in testing frameworks.                                           |
| `PixelConverter`        | Understand how `Unions` are depicted in testing frameworks.                                                           |
| `PipelineSimple`        | See how multiple tydi modules are represented in testing frameworks.                                                  |
| `PipelineNestedGroup`   | Analyze the impact of nested tydi groups on testing frameworks.                                                       |
| `PipelineNestedStreams` | Examine the impact of nested streams on testing frameworks.                                                           |