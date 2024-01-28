# An analysis of available testing frameworks for Tydi-Chisel

This repository contains the results of an analysis of available testing frameworks for Tydi related projects. Specifically, an analysis on pure Chisel and [`Tydi-Chisel`](https://github.com/ccromjongh/Tydi-Chisel) by [Casper Cromjongh](https://github.com/ccromjongh) projects has been performed. 

[`Tydi-Chisel`](https://github.com/ccromjongh/Tydi-Chisel) by [Casper Cromjongh](https://github.com/ccromjongh).
 Specifically, simple Chisel and Tydi-Chisel designs have been tested.


Specifically, we are interested in the following questions:
- What are the available testing frameworks for Tydi-Chisel?
- What are the advantages and disadvantages of each framework?.

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

