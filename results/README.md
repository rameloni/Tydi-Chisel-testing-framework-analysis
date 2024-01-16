# Tool analysis results
This folder contains considerations and results of the analysis that has been made. It specifically shows advantages and disadvantages of the following testing frameworks:
- [`chiseltest`](chiseltest)
- [`hgdb`](hgdb)
- [`waveform viewers`](waveforms)

> **Note:** Each subfolder contains a README.md file with specific considerations for each tool, related to the examples 
> presented in [examples](../examples/) folder.
> This README.md file contains a general summary of the advantages and disadvantages.

## Summary
### Chiseltest
Chisel test is an officially supported testing framework for Chisel that does not require any additional tools in order to be executed.
> *ChiselTest integrates with the ScalaTest framework, which provides good IDE and continuous integration support for launching unit tests.*
> 
> Reference: [`chiseltest`](https://github.com/ucb-bar/chiseltest#chiseltest)
> 
Therefore, it provides an easy-to-use tool with support for all `Chisel` and related projects. It is thus an excellent choice for `Tydi`-related projects.

One of its main advantages is the possibility to use all the features of the `scala` language to create tests. Among the others, the opportunity to generate golden values from scala software functions in order to assess circuit outputs is a great advantage. Scala features such as loops, if statements, functions, class abstractions, templates represent an additional value in terms of test code reuse and readability. For example, it is possible to create a test template that is reusable with different data as shown [here](https://github.com/ucb-bar/chiseltest/blob/main/src/test/scala/chiseltest/tests/AluTest.scala). 

As it is integrated with the `ScalaTest` framework, `chiseltest` can exploit all exisiting testing features for `scala` testing. Namely, it can make use of `scala` IDE functionalities, such as [IntelliJ IDEA](https://www.jetbrains.com/idea/), to run tests and set breakpoints.
Along with that, it is possible to divide tests in **test classes** and **test cases** to further improve organization and readability. 

However, "scala" IDE breakpoint debugging seems to be limited to the test code itself. Breakpoints placed in the circuit code will be executed only at the hardware initialization of the circuit, not during the simulation. Breakpoints inspection is therefore limited to the circuit IO ports. 
Even though IDEA debugger to inspect a circuit hierarchy, it is not possible to inspect what values the internal logic signals have at a specific simulation time. 
[HGDB](#hgdb) provides a solution to this problem, as it allows to set breakpoints on `Chisel` hardware code and inspect values line by line similar to software debuggers.

`chiseltest` offers `peek`/`poke` functionalities to read and write circuit signals during simulation from the script, allowing to both use `printf` debugging and `assertion-based` debugging (with `expect`). Nevertheless, this is limited to the IO circuit ports since `chiseltest` does not support direct exposure of internal logic signals. A workaround to this problem is to either write a `Wrapper` module that extends the actual `DUT` (design under test) to expose internal signals as output ports or to use `treadle.VerboseAnnotation` to print all circuit signals on the console. However, none of these solutions is ideal, as the first one requires additional code and it requires to be adapted any time the internal logic changes, while the second one allows only an inspection from the console output which is not as convenient as a waveform viewer since not easy to read.

[Waveform viewers](#waveform-viewers) potentially allow to inspect the circuit signals in a more convenient way. `chiseltest` supports the generation of [VCD](https://en.wikipedia.org/wiki/Value_change_dump), [FST](https://gtkwave.sourceforge.net/gtkwave.pdf) and [LXT](https://gtkwave.sourceforge.net/gtkwave.pdf) files that can be used by existing waveform viewers such as [GTKWave](https://gtkwave.sourceforge.net/gtkwave.pdf).

<!-- Even though it does not include a waveform viewer it is possible to output VCD files for waveform viewers by simply adding `WriteVcdAnnotation` to the test. However, this point is discussed in the waveform viewers section. -->



### HGDB
#### HGDB vs IntelliJ debugger + chiseltest

### Waveform viewers
As they provide a graphical visualization and parallel visualization of signal changes over time by definition.

------------------------------------------------------------------------------------------------------------------------

| Feature | `chiseltest` (script) | `chiseltest` (scala IntelliJ breakpoints) | `hgdb` | waveform viewers |
| ------- | --------------------- | ----------------------------------------- | ------ | ---------------- |
|         |                       |                                           |        |                  |


# References
- [Chisel3]
- [chiseltest]
- [HGDB]
- [GTKWaves]