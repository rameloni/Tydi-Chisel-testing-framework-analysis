.PHONY: all download-tydi-lib compile-tydi-lib clean

TYDI_LIB_REPO=https://github.com/abs-tudelft/Tydi-Chisel.git
TYDI_LIB_NAME=Tydi-Chisel

all: download-tydi-lib install clean

download-tydi-lib:
	@mkdir -p tmp/
	@cd tmp/ && git clone $(TYDI_LIB_REPO)

install:
	@cd tmp/$(TYDI_LIB_NAME) && sbt publishLocal

clean:
	@rm -rf tmp/