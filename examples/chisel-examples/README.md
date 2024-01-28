# Chisel examples
This directory contains a variety of Chisel code examples. 
A selection of these examples has been meticulously chosen from the `chisel-tutorial` repository and subsequently 
adapted for an analysis of each testing framework characteristics. 
The primary objective of these modifications is to highlight certain characteristics of Chisel representations that 
might remain obscured while using the available testing frameworks.

Specifically, the following examples have been selected:
- [`Adder`](./src/main/Adder.scala): A simple N-bit adder circuit, that uses `Array` (scala) class to wrap the 
    [`FullAdder`](./src/main/FullAdder.scala)s modules, `Vec` and `Bundle` (chisel) to group together signals needed for the internal `carry`, `sum` and `IO` interface.
- [`DetectTwoOnes`](./src/main/FSM.scala): A simple finite state machine that uses `ChiselEnum` to represent the states.
- [`Parity`](./src/main/Parity.scala): A simple circuit that uses an `Enum` (chisel) to represent the states instead of
  `ChiselEnum`.
- [`Functionality`](./src/main/Functionality.scala): A simple circuit that uses different ways to assign values to outputs:
    - assigning it directly from pure boolean expressions
    - wrapping the boolean expressions into a function
    - wrapping the boolean expression into a `val`
    - using an scala `object`
    - using a scala `class`
- [`Memory`](./src/main/Memory.scala): A simple module that wraps the `Mem` (chisel) module to implement a simple read/write memory.
- [`Router`](./src/main/Router.scala): This is the most complex example. It implements a simple router which 
  characteristics are wrapped into objects and classes. It is composed by:
  - A top `Router` module class that instantiates submodules and connect wires together.
  - A `RouterIO` class that defines the IO interface of the router.
  - A `Packet` class that defines the structure of a packet that goes through the router.
  - A `WriteCmd` and a `ReadCmd` classes that define the structure of the commands that the router receives.
  - A `Router` object that defines constants that specify the characteristics of the router.
  - A `Mem` instance to define the routing table.

Each example aims to illustrate the representation of a particular aspect supported by Chisel in the testing frameworks. 
It also shows how this aspect relates to the Chisel source code. Sources and tests are located in the 
[`src/main`](./src/main) and [`src/chiseltest`](./src/chiseltest) subdirectories, respectively.

| Example            | Target                                                                                                                |
| :----------------- | :-------------------------------------------------------------------------------------------------------------------- |
| `Adder`            | Explore the representation of `Array` and `Vec` in testing frameworks.                                                |
| `FSM` and `Parity` | Understand how `ChiselEnum` and `Enum` are depicted in testing frameworks.                                            |
| `Functionality`    | Analyze the impact of various `assign values` methods on output representation in testing frameworks.                 |
| `Memory`           | Explore the representation of `Mem` in testing frameworks.                                                            |
| `Router`           | Examine the impact of various strategies for `representing constants` and `circuit components` in testing frameworks. |


> **Note:** The examples are used to perform the analysis of the testing frameworks. Results are reported in the 
> [results](/results) directory.

# References
- [Chisel](https://www.chisel-lang.org/). The official Chisel website.
- [Chisel-tutorial](https://github.com/ucb-bar/chisel-tutorial/tree/release/src/main/scala/examples) examples. The official Chisel tutorial examples main page.