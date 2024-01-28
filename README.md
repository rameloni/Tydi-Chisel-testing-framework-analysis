# An analysis of available testing frameworks for Tydi-Chisel

This repository contains the results of an analysis of available testing frameworks for Tydi related projects. 
An analysis of pure Chisel[^1] and Tydi-Chisel[^2][^3] by [Casper Cromjongh](https://github.com/ccromjongh) projects has been performed. 

Specifically, we are interested in the following questions:
- What are the available testing frameworks for Tydi-Chisel?
- What are the advantages and disadvantages of each framework?
- How chisel and tydi constructs are represented in the testing frameworks?
  - To what extent do signals in waveform viewers match the original source code?
 
All these aspects are explored and discussed in the [results](results) section.

# Tree repository index
- [A collection of Chisel and Tydi-Chisel examples that support the analysis of the frameworks](examples)
  - [Chisel examples](examples/chisel-examples): a list of examples of Chisel designs
    - [README.md](examples/chisel-examples/README.md): description of the examples
    - [src/main](examples/chisel-examples/src/main): source code of Chisel examples
    - [src/test](examples/chisel-examples/src/chiseltest): testbenches of Chisel examples
  - [Tydi-Chisel code examples](examples/tydi-chisel-examples): a list of examples of Tydi-Chisel designs
    - [README.md](examples/tydi-chisel-examples/README.md): description of the examples
    - [tydi](examples/tydi-chisel-examples/tydi): source tydi code of Tydi-Chisel examples
    - [chisel](examples/tydi-chisel-examples/chisel)  
      - [src/main](examples/tydi-chisel-examples/chisel/src/main): source code of Tydi-Chisel examples
      - [src/test](examples/tydi-chisel-examples/chisel/src/test): testbenches of Tydi-Chisel examples
- [Tool analysis results](results): outcomes of the analysis
  - [An analysis of the features of chiseltest: a testing framework for Chisel and Chisel-related projects](results/chiseltest)
  - [hgdb](results/hgdb): analysis of hgdb debugger
  - [An analysis of the waveforms produced by Chisel testbenches](results/waveforms)
  - [A detailed analysis of Tydi-Chisel representations in testing frameworks](results/analysis-by-example)ù

# References
[^1]: Chisel, an open-source hardware description language (Constructing Hardware in a Scala Embedded Language). [![chisel](https://img.shields.io/badge/Github_Page-chisel-green)](https://github.com/chipsalliance/chisel)

[^2]: Casper Cromjongh et al. **“Tydi-Chisel: Collaborative and Interface-Driven Data-Streaming Accelerators”**. In: *2023 IEEE Nordic Circuits and Systems Conference (NorCAS*). Aalborg, Denmark: IEEE, Oct. 31, 2023, pp. 1–7. ISBN: 9798350337570. [![10.1109/NorCAS58970.2023.10305451](https://zenodo.org/badge/DOI/10.1109/NorCAS58970.2023.10305451.svg)](https://doi.org/10.1109/NorCAS58970.2023.10305451)

[^3]: A implementation of Tydi interfaces and concepts in Chisel. [![Tydi-Chisel](https://img.shields.io/badge/Github_Page-Tydi--Chisel-green)](https://github.com/ccromjongh/Tydi-Chisel)