# Tool analysis results
This folder contains considerations and results of the analysis that has been made. 
It is divided as follows:
- The [chiseltest](chiseltest) folder analyzes the `chiseltest`[^1] tool and the features related to it with a focus on the advantages and disadvantages of the tool.
- [hgdb](hgdb) reports the observations about the `hgdb`[^4] (GitHub[^5]) debugger tool.
- The [waveforms](waveforms) directory, instead, explore first the strengths of signals inspections from waveform viewers, and it finally highlights the generic weaknesses of signal representations of circuits written in modern HDLs, in this case Chisel.
- Finally, all the examples (in the [examples](../examples) folder) have been tested using the three tools (chiseltest to write the test benches, hgdb and waveforms to inspect the signals). 
  [analysis-by-example](analysis-by-example) analyzes each example, highlighting the advantages and disadvantages of each tool for each target aspect of the examples.
  It especially focuses on how the Chisel and Tydi-Chisel code is represented by each tool.

# References
[^1]: The officially supported testing framework for Chisel and Chisel-related projects. [![chiseltest](https://img.shields.io/badge/Github_Page-chiseltest-green)](https://github.com/ucb-bar/chiseltest)

[^2]: Chisel, an open-source hardware description language (Constructing Hardware in a Scala Embedded Language). [![chisel](https://img.shields.io/badge/Github_Page-chisel-green)](https://github.com/chipsalliance/chisel)

[^3]: ScalaTest, a testing framework for the Scala ecosystem. [![scalatest](https://img.shields.io/badge/Web_Page-www.scalatest.org-blue)](https://www.scalatest.org/)

[^4]: Keyi Zhang, Zain Asgar, and Mark Horowitz. **“Bringing source-level debugging frameworks to hard-ware generators”**. In: *Proceedings of the 59th ACM/IEEE Design Automation Conference*. DAC'22: 59th ACM/IEEE Design Automation Conference. San Francisco California: ACM, July 10, 2022, pp. 1171–1176. [![10.1145/3489517.3530603](https://zenodo.org/badge/DOI/10.1145/3489517.3530603.svg)](https://dl.acm.org/doi/10.1145/3489517.3530603)

[^5]: Keyi Zhang. Kuree/hgdb. [![hgdb](https://img.shields.io/badge/Github_Page-hgdb-green)](https://github.com/Kuree/hgdb)