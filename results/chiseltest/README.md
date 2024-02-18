# An analysis of the features of chiseltest: a testing framework for Chisel and Chisel-related projects

Chisel test[^1] is the officially supported testing framework for Chisel[^2].
It does not require any additional tool to be executed.

> *ChiselTest integrates with the ScalaTest framework, which provides good IDE and continuous integration support for launching unit tests.*
> 
> Reference: `chiseltest`[^1]

Therefore, it provides an easy-to-use tool with support for all Chisel and related projects.
Thus, it presents itself as an excellent choice for Tydi-related projects as well.

The rest of this document is organized as follows.
First, a general overview of what is possible to do with chiseltest is provided in section [1](#1-general-overview).
Later sections focuses on different ways to test circuits and inspect their signals during simulation through chiseltest (sections [2](#2-inspecting-values-by-using-printf-debugging-with-explicit-print-statements), [3](#3-inspecting-values-by-using-treadle2verboseannotation), and [4](#4-can-intellij-idea-breakpoint-debugging-be-useful-for-chisel-related-codes)).

- [An analysis of the features of chiseltest: a testing framework for Chisel and Chisel-related projects](#an-analysis-of-the-features-of-chiseltest-a-testing-framework-for-chisel-and-chisel-related-projects)
  - [1. General overview](#1-general-overview)
  - [2. Use `printf` debugging with `peekpoke` and explicit print statements to inspect values](#2-use-printf-debugging-with-peekpoke-and-explicit-print-statements-to-inspect-values)
  - [3. Inspecting values by using `treadle2.VerboseAnnotation`](#3-inspecting-values-by-using-treadle2verboseannotation)
  - [4. Can IntelliJ IDEA breakpoint debugging be useful for Chisel related codes?](#4-can-intellij-idea-breakpoint-debugging-be-useful-for-chisel-related-codes)
  - [5. Conclusions](#5-conclusions)
- [References](#references)
  
## 1. General overview
As it is integrated with the ScalaTest framework, `chiseltest` can exploit all the existing features for scala testing[^4]. 
Among the others, the opportunity to generate golden values from scala software constructs in order to assess circuit outputs is a great advantage.
Scala features such as loops, if statements, functions, class abstractions, templates represent an additional value in terms of test code reuse and readability.
For example, it is also possible to create test templates that can be reused with different data as shown in a chisel example [here](https://github.com/ucb-bar/chiseltest/blob/main/src/test/scala/chiseltest/tests/AluTest.scala). 


Additionally, it can make use of `scala` IDE functionalities, such as the ones provided by [IntelliJ IDEA](https://www.jetbrains.com/idea/), to run specific tests just clicking a button form the user interface.
Along with that, ScalaTest integration offers the opportunity to divide tests in **test classes** and **test cases** to further improve organization and readability. 
However, despite IntelliJ allows to set breakpoints in a scala code, breakpoint debugging is highly limited as stated in section [4](#4-can-intellij-idea-breakpoint-debugging-be-useful-for-chisel-related-codes), since tests are actually not executed by scala debugger but by one of the available backends[^1].
HGDB[^3] provides a solution to this problem, as it allows to set breakpoints on Chisel hardware code and inspect values line by line similarly to software debuggers.

Upon closer examination of chiseltest functionalities, as documented on the main page [^1], it becomes evident that the framework offers peek/poke methods.
These methods enable reading and writing of circuit signals during simulation, providing direct access to their values in the testbench code.
Consequently, these functions can be used for printf and assertion-based debugging as explored in sections [2](#2-inspecting-values-by-using-printf-debugging-with-peekpoke-and-explicit-print-statements) and [3](#3-inspecting-values-by-using-treadle2verboseannotation). 
Nevertheless, peek/poke testing is limited to the IO ports of the circuit as the internal logic is not exposed "out-of-the-box".
As argued in next sections, certain actions can be employed to perform peek/poke testing on internal logics, but this requires additional code effort from the developer.
ChiselTest offers an annotation to print verbose information about the entire circuit on the output console, potentially resulting in an overly complex output and a non-trivial signal inspection.

Finally, ChiselTest supports the generation of simulation trace files, producing [VCD](https://en.wikipedia.org/wiki/Value_change_dump), [FST](https://gtkwave.sourceforge.net/gtkwave.pdf), and [LXT](https://gtkwave.sourceforge.net/gtkwave.pdf) files for the current simulation.
These trace files can subsequently be utilized in existing waveform viewers, such as [GTKWave](https://gtkwave.sourceforge.net/gtkwave.pdf), enabling the selection and observation of signals across the circuit hierarchy in a more convenient manner.

The following sections explore how `printf`, `VerboseAnnotation` and `IntelliJ IDEA` breakpoint debugging can be used in chiseltest to inspect circuit values.
The analysis will include a discussion of the advantages and drawbacks of each.

> **Note:** The following sections are based on the [AdderTest.scala](../../examples/chisel-examples/src/chiseltest/AdderTest.scala) testbench to test an `Adder` module composed of cascaded `FullAdder`s. 
> Both modules are available in the [chisel-examples](../../examples/chisel-examples/src/main/scala/chiseltest/examples) folder. 

## 2. Use `printf` debugging with `peekpoke` and explicit print statements to inspect values
As mentioned in the previous section, `printf` debugging can be used to output custom messages with special representation of signals during simulation.
Chisel elements have some attribute functions to retrieve information, such as the element name and type, that can be used in conjunction with peek/poke to print well formatted outputs.
Precisely, `name` and `typeName` fields can be used to dynamically access those values.
Therefore, if the signal name and/or type changes, the printed message will be automatically updated.
Similarly, the `peek` function can be used to retrieve the current value of a signal during simulation.

The `AdderTester` in [AdderTest.scala](../../examples/chisel-examples/src/chiseltest/AdderTest.scala) provides an example of `printf` debugging and its output is reported in [printfDebuggingExample.txt](./sample-outputs/printfDebuggingExample.txt).
Below, a portion of the output is presented.
An explicit writing of printf statements allows to customize the output format to align it with developers' preferences. 
Thus, this method allows to create the best and comprehensive format for each specific debugging case such as the truth table below.
However, this approach does not represent a ready-to-use solution, as it necessitates manual writing and updates any time a module is changed. 
Explicit printf debugging fails to establish a standardized signal inspection and may contribute to a less readable testbench code.
In addition, its implementation highly depends on the designer's coding style, so it may not be suitable for all developers.

```text
=================================================================
Module: Adder
     clock          A          B        Cin        Sum       Cout : Ports
     Input      Input      Input      Input     Output     Output : Directions
     Clock    UInt<4>    UInt<4>    UInt<1>    UInt<4>       Bool : Types
=================================================================
. . . . . . . . . . . . . . . 
. . . . . . . . . . . . . . . 
        12          1         11          1         13          0
        13          1         13          0         14          0
        14          1         13          1         15          0
        15          1         15          0          0          1
        16          1         15          1          1          1
        17          3          1          0          4          0
        18          3          1          1          5          0
        19          3          3          0          6          0
        20          3          3          1          7          0
        21          3          5          0          8          0
. . . . . . . . . . . . . . . 
. . . . . . . . . . . . . . . 
```

The utilization of printf debugging in `chiseltest` uncovered another limitation.
When attempting to inspect the internal signals of a module, either wires or IO signals of a submodule, the `peek` function raises an exception due to the inaccessibility of these values from outside the `DUT` (Device Under Test).
Nonetheless, this can be overcome by writing a `Wrapper` module that extends the actual `DUT` to **manually** expose internal signals as output ports with the `chiseltest.experimental.expose`.
Hence, any testing technique that can be used for IO ports can be now also used for internal exposed signals, such as `printf` debugging and `assertion-based` debugging (with `expect`).

However, there are some drawbacks to this `expose` function. 
Likewise, printf statements, also this method requires to write additional code and to adapt it any time the internal logic changes.
Furthermore, only `Reg`, `Wire` and `Vec` can be exposed directly by this experimental chiseltest function.
Namely, signals of submodules (such as the `Array` of `FullAdder`s) or sub-bundles require even more code to be written than expected.

As a workaround, I created a utility function ([`ExposeBundle`](../../examples/chisel-examples/src/chiseltest/modulewrappers/ExposeBundle.scala)) to expose dynamically all the signals inside a `Bundle`.
This enables the dynamic exposure of all signals within an internal bundle without the necessity of writing additional code or knowing what content the bundle has beforehand.
A dedicated function to expose or encapsulate an entire module would indeed constitute a valuable and beneficial feature.
The [`AdderWrapper`](../../examples/chisel-examples/src/chiseltest/modulewrappers/AdderWrapper.scala) module serves as an example of this solution.
When instantiated in the test, such a module wrapper allows to inspect the internal signals of the `Adder` as if they were IO ports available only during simulation as shown in [`AdderExposeTest`](../../examples/chisel-examples/src/chiseltest/AdderTest.scala).

```scala
  it should "adder with exposed ports by parameter" in {
  test(new AdderWrapperByParameter(new Adder(4, print = true)))
    .withChiselAnnotations(Seq()) { c =>
      c.exposed.io.A.poke(4)
      c.exposed.io.B.poke(1)
      c.exposed.io.Cin.poke(0)

      c.clock.step(1)
      System.out.println(s"Sum: ${c.exposed.io.Sum.peek().asBools.map(x => x.asUInt)}")
      System.out.println(s"Cout: ${c.exposed.io.Cout.peek()}")

      // FAs
      System.out.println(s"    a b i s c")
      for (fas <- c.exposed.FAs) {
        System.out.println(s"FA: ${fas.a.peekInt} ${fas.b.peekInt} ${fas.cin.peekInt} ${fas.sum.peekInt} ${fas.cout.peekInt}")
      }
      System.out.println("-------------------------------")
      for (fas <- c.exposed.FAsMethod) {
        System.out.println(s"FA: ${fas.a.peekInt} ${fas.b.peekInt} ${fas.cin.peekInt} ${fas.sum.peekInt} ${fas.cout.peekInt}")
      }
    }
}

```

Finally, designers may need and want to inspect only a subpart of a circuit or some signals.
With printf debugging, achieving this goal necessitates once again the implementation of custom updates to the testbench.

## 3. Inspecting values by using `treadle2.VerboseAnnotation`
In contrast to explicit `printf` debugging, a verbose output can be easily achieved by simply adding one line of code through `VerboseAnnotation`, as reported in the following code snippet.
Information about the whole circuit (IO and internal logic) will be outputted in the terminal at each simulation step.
```scala
it should "4 bit adder with verbose annotation" in {
	test(new Adder(4, print = true))
	  .withAnnotations(Seq(treadle2.VerboseAnnotation))
	  .runPeekPoke(new AdderTester(_, stepsFor = 8, printDebug = false))
}  
```
This method has the main advantage to be fast and easy to integrate, compared to explicit `printf`, as it requires only one line of code and entails no need for future updates.
Moreover, testbench functions can be implemented regardless of the VerboseAnnotation and kept readable and concise.
However, `assertion-based` debugging cannot be applied to internal logic without the presence of an expose wrapper yet.
[verboseAnnotationOutputExample.txt](./sample-outputs/verboseAnnotationOutputExample.txt) contains an example of the output of the test above.

Although adding `VerboseAnnotation` allows to visually inspect all the signals without any significant effort compared to `printf` debugging, it might lead to very long and articulated outputs, especially for complex circuits.
This can make reading and understanding the circuit behavior difficult even with small circuits such as the `Adder`. 
Secondly, this annotation does not make the designer able to choose which sub-parts to inspect from the output, because it prints all the signals of the circuit, independently from the designer's needs.
Finally, the `VerboseAnnotation` prints signals names according to the FIRRTL representation of the circuit which does not match to what the designer actually wrote, a chisel typed representation.
It may become really difficult to associate the verbose output to the corresponding chisel signal/module.

## 4. Can IntelliJ IDEA breakpoint debugging be useful for Chisel related codes?
Since Chisel is written on top of the scala language, IntelliJ IDEA candidates itself as a good IDE to write and debug Chisel code. 

IntelliJ provides a scala debugger that allows to set breakpoints for a scala source code. 
Therefore, the IDE lets the designer set breakpoints on test code and chisel circuit code, as shown in the following figures (fig. 1 and 2), since both are written in scala.

| ![IDEA breakpoints on circuit signal assingments](./images/idea-breakpoints-on-testfunction.png) | ![IDEA breakpoints on circuit signal assingments](./images/idea-breakpoints-on-circuitassignments.png) |
| ------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------ |
| Fig. 1 - *Set breakpoints in simulation chisel function from IntelliJ*                           | Fig. 2 - *Set breakpoints in signal circuit assignments from IntelliJ*                                 |

This section tries to understand whether this tool can be useful for Chisel or not.

Once I ran a test in debug mode with the breakpoints, the debugger stopped at the breakpoints in fig. 2 first, during the circuit evaluation and before the actual backend simulator execution. 
As stated in the chiseltest page[^1], chiseltest exploits other backend simulators to emulate the circuit behaviour and `peek`/`poke` functions are used to interact with that. 
So, they are the only valid interface to access signal values. 
Fig. 3 shows the breakpoints during initialization of the `Adder` module and, as it can be seen, only the circuit structure can be inspected **without any possibility to associate values to signals**. 
This might be due to the fact that the circuit is not simulated yet.
However, even once the simulation stops at the breakpoints of fig. 1, no signal values of the `dut` module are accessible from the debugger directly. 
In order to do that, explicit new variables must be declared and assigned by `peek` to see values as shown in fig. 4 and 5. 
This will add additional code overhead to the tester functions without still providing a way to inspect the circuit structure. 
Functionalities like `step over` and `step into` are also still not suitable for the circuit evaluation breakpoints since they works only for scala code. 

This issue is addressed and solved by `hgdb`[^3] which allows to perform breakpoint debugging on an HDL circuit. 
This [section](../hgdb/README.md) provides more details about this topic.

| ![idea-breakpoints-in-the-adder](./images/idea-breakpoint-inspection-on-circuitsignals.png) |
| ------------------------------------------------------------------------------------------- |
| Fig. 3 - *Breakpoint during initialization of the `Adder` module*                           |

| ![idea-breakpoints](./images/idea-debugger-access-with-peek-and-assignment-tovar.png)         | ![idea](./images/idea-debugger-no-access-to-values-without-peek.png)                              |
| --------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| Fig. 4 - *Breakpoint at `peek` of signals with explicit assignment to a variable in the test* | Fig. 5 - *Even the backend does not allow to access values of the signals from the idea debugger* |

## 5. Conclusions
Chiseltest is a powerful testing framework that exploits ScalaTest to organize, write and run tests for chisel modules.
Its direct integration with chisel simplifies and speedups the testing process. 
Moreover, the scala language constructs allow to keep test bench code compact and readable.

Although assertion-based debugging is optimal to assess specific conditions, it does not provide an output for signal inspection.
As we have seen in this document, printf debugging and VerboseAnnotation are possible options for that.
However, the former requires additional effort to the user for showing a well formatted output and exposing internals, while the latter overcomplicates the output and does not allow to choose the components to show.
Since, it can generate simulation trace files, it can also be used in conjuction with existing waveform viewers such as GTKWave.
This third solution is analyzed in deep [here](../waveforms/).

Finally, scala IDEs cannot be used to debug chisel code since it is not executed as scala code.
Indeed, the simulation is performed by external backend engines.

# References
[^1]: The officially supported testing framework for Chisel and Chisel-related projects. [![chiseltest](https://img.shields.io/badge/Github_Page-chiseltest-green)](https://github.com/ucb-bar/chiseltest)

[^2]: Chisel. Home | Chisel. URL: https://www.chisel-lang.org/ (visited on 01/09/2024).

[^3]: Keyi Zhang, Zain Asgar, and Mark Horowitz. **“Bringing source-level debugging frameworks to hard-ware generators”**. In: *Proceedings of the 59th ACM/IEEE Design Automation Conference*. DAC’22: 59th ACM/IEEE Design Automation Conference. San Francisco California: ACM, July 10, 2022, pp. 1171–1176. [![10.1145/3489517.3530603](https://zenodo.org/badge/DOI/10.1145/3489517.3530603.svg)](https://dl.acm.org/doi/10.1145/3489517.3530603)

[^4]: ScalaTest, a testing framework for the Scala ecosystem. [![scalatest](https://img.shields.io/badge/Web_Page-www.scalatest.org-blue)](https://www.scalatest.org/)

