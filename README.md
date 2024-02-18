# An analysis of available testing frameworks for Tydi-Chisel

This repository contains the results of an analysis of available testing frameworks for Tydi and Chisel related
projects.
An analysis of Chisel[^1] and Tydi-Chisel[^2] (through the chisel tydi_lib[^3])
by [Casper Cromjongh](https://github.com/ccromjongh) projects has been performed.

Specifically, we are interested in the following research questions:

- What are the available testing frameworks for Tydi-Chisel?
- What are the advantages and disadvantages of each selected framework?
- How chisel and tydi constructs are represented in the testing frameworks?
    - How can the representation be generated?
    - To what extent do signals in waveform viewers match the original source code?
    - What is the gap between the source code and the waveform representation and testing framework?

All these aspects are explored and discussed in the [results](results) section.

Specifically, the repository is divided into two main sections:

1. A collection of Chisel and Tydi-Chisel examples that support the analysis of the frameworks
2. Tool analysis results

Below the tree repository index is presented with links to each section.

# Tree repository index

## Examples index
Main page: [examples](examples).

| Sections                                                   | Description                                                                                                                  |
| :--------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------- |
| [Chisel code examples](examples/chisel-examples)           | A collection of Chisel code examples. It contains: source code, test benches and examples explanation.                       |
| [Tydi-Chisel code examples](examples/tydi-chisel-examples) | A collection of Tydi-Chisel code examples. It contains: tydi and chisel source codes, test benches and examples explanation. |

## Results index
Main page: [results](results).

| Section                                            | Description                                                                                            |
| :------------------------------------------------- | :----------------------------------------------------------------------------------------------------- |
| [Chiseltest](results/chiseltest)                   | An analysis of the features of chiseltest: a testing framework for Chisel and Chisel-related projects. |
| [HGDB-debugger](results/hgdb)                      | An analysis of hgdb debugger.                                                                          |
| [Waveforms](results/waveforms)                     | An analysis of the waveforms produced by Chisel test benches.                                          |
| [Analysis by example](results/analysis-by-example) | A detailed analysis of Chisel and Tydi-Chisel representations in testing frameworks.                   |

# Run the examples
## Installation requirements
1. Install Chisel following the instructions in the Chisel GitHub repository[^1].
2. Install the Tydi lang compiler locally (in .cargo/bin):
    ```bash
    git clone https://github.com/twoentartian/tydi-lang-2.git
    cd tydi-lang-2
    cargo install --path tydi-lang-complier
    ```
3. Install the Tydi lang transpiler:
    ```bash
    cd examples/tydi-chisel-examples/tydi
    make compile-transpiler
    ```
4. Install the `tydi_chisel` library:
    ```bash
   cd examples/tydi-chisel-examples/chisel
   make all # The makefile will install the tydi_chisel library locally
   ```
   Now the `tydi_chisel` library is installed in the local maven repository and it can be added as a local dependency with:
   ```scala
    libraryDependencies += "nl.tudelft" %% "root" % "0.1.0",
   ```
   and imported in a chisel source code with:
   ```scala
    import nl.tudelft.tydi_chisel._
   ```
   
## Compile and transpile Tydi-lang sources
Now that everything is installed, it is possible to compile the Tydi-lang source projects.
1. Compile **all** the Tydi projects in the `src` folder:
    ```bash
    cd examples/tydi-chisel-examples/tydi/
    make 
    ```
2. (Alternative) Compile a **single** Tydi project:
    ```bash
    cd examples/tydi-chisel-examples/tydi/
    make SRC=<path_to_tydi_source_DIRECTORY> 
    ```
This will create a `build` folder with the compiled Tydi to json IR and Chisel boilerplate code. 

# References

[^1]: Chisel, an open-source hardware description language (Constructing Hardware in a Scala Embedded
Language). [![chisel](https://img.shields.io/badge/Github_Page-chisel-green)](https://github.com/chipsalliance/chisel)

[^2]: Casper Cromjongh et al. **“Tydi-Chisel: Collaborative and Interface-Driven Data-Streaming Accelerators”**. In:
*2023 IEEE Nordic Circuits and Systems Conference (NorCAS*). Aalborg, Denmark: IEEE, Oct. 31, 2023, pp. 1–7. ISBN:
9798350337570. [![10.1109/NorCAS58970.2023.10305451](https://zenodo.org/badge/DOI/10.1109/NorCAS58970.2023.10305451.svg)](https://doi.org/10.1109/NorCAS58970.2023.10305451)

[^3]: A implementation of Tydi interfaces and concepts in
Chisel. [![Tydi-Chisel](https://img.shields.io/badge/Github_Page-Tydi--Chisel-green)](https://github.com/ccromjongh/Tydi-Chisel)
