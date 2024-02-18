# Chisel examples

This directory contains a variety of Chisel[^1] code examples and their respective test benches.
A selection of these examples has been meticulously chosen from the `chisel-tutorial`[^2] GitHub repository and subsequently adapted for an analysis of each testing framework characteristics.
The primary objective of these modifications is to highlight certain characteristics of Chisel representations that might remain obscured while using the available testing frameworks.

Specifically, the list of selected examples is presented below:

- [Adder](./src/main/Adder.scala): A simple N-bit adder circuit, that uses the scala `Array` class to wrap the [FullAdder](./src/main/FullAdder.scala) modules and chisel `Vec`/`Bundle`[^3] to group together signals needed for the internal `carry`, `sum` and `IO` interface.
- [DetectTwoOnes](./src/main/FSM.scala): A simple finite state machine that uses `ChiselEnum`[^4] to represent the states.
- [Parity](./src/main/Parity.scala): A simple circuit that uses an `Enum`[^5] (chisel) to represent the states instead of
  `ChiselEnum`.
- [Functionality](./src/main/Functionality.scala): A simple circuit that uses different ways to assign values to outputs:
    - Direct output assignment from pure boolean expressions
    - Wrapping the boolean expressions into a `def` function
    - Wrapping the boolean expression into a `val`
    - Using a scala `object`.
    - Using a scala `class`.
- [Memory](./src/main/Memory.scala): A simple module that wraps the `Mem`[^6] (chisel) module to implement a simple read/write memory.
- [Router](./src/main/Router.scala): This is the most complex example. 
  It implements a simple router which characteristics are wrapped into objects and classes.
  It is composed by:
    - A top `Router` module class that instantiates submodules and connect wires together.
    - A `RouterIO` class that defines the IO interface of the router.
    - A `Packet` class that defines the structure of a packet that goes through the router.
    - A `WriteCmd` and a `ReadCmd` classes that define the structure of the commands that the router receives.
    - A `Router` object that defines constants that specify the characteristics of the router.
    - A `Mem` instance to define the routing table.

Each example aims to illustrate the representation of a particular aspect supported by Chisel in the testing frameworks.
It also shows how such aspect relates to the Chisel source code. 
Sources and tests are located in the [src/main](./src/main) and [src/chiseltest](./src/chiseltest) subdirectories, respectively.

## Examples summary
| Example            | Target                                                                                                                |
|:-------------------|:----------------------------------------------------------------------------------------------------------------------|
| `Adder`            | Explore the representation of chisel `Bundle`/`Vec` and scala `Array` in testing frameworks.                          |
| `FSM` and `Parity` | Understand how `ChiselEnum` and `Enum` are depicted in testing frameworks.                                            |
| `Functionality`    | Analyze the impact of various `assign values` methods on output representation in testing frameworks.                 |
| `Memory`           | Explore the representation of chisel `Mem` in testing frameworks.                                                     |
| `Router`           | Examine the impact of various strategies for `representing constants` and `circuit components` in testing frameworks. |

> **Note:** The examples are used to perform the analysis of the testing frameworks. Results are reported in the
> [results](/results) directory.

# References

[^1]: Chisel, an open-source hardware description language (Constructing Hardware in a Scala Embedded Language). [![chisel](https://img.shields.io/badge/Github_Page-chisel-green)](https://github.com/chipsalliance/chisel)

[^2]: A collection of Chisel tutorials and examples. [![chisel-tutorial](https://img.shields.io/badge/Github_Page-chisel--tutorial-green)](https://github.com/ucb-bar/chisel-tutorial)

[^3]: *Bundles and Vecs* | *Chisel*. en. [![bundles-vec-chisel](https://img.shields.io/badge/Web_Page-Bundles_and_Vecs_Chisel-blue)](https://www.chisel-lang.org/docs/explanations/bundles-and-vecs)

[^4]: *Enumerations* | *Chisel*. en. [![enumerations-chisel](https://img.shields.io/badge/Web_Page-Enumerations_Chisel-blue)](https://www.chisel-lang.org/docs/explanations/chisel-enum)

[^5]: *chisel3.util.Enum documentation* en. [![chisel3.util.Enum](https://img.shields.io/badge/Web_Page-chisel3.util.Enum-blue)](https://javadoc.io/static/edu.berkeley.cs/chisel3_2.12/3.3.0-RC1/chisel3/util/Enum.html)

[^6]: *Memories* | *Chisel*. en. [![memories-chisel](https://img.shields.io/badge/Web_Page-Memories_Chisel-blue)](https://www.chisel-lang.org/docs/explanations/memories)
