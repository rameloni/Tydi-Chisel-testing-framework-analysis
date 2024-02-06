# # Install the tydi-lang-2-chisel transpiler
TRANSPILER=tydi-transpiler

TRANSPILER_SRC_LINK="https://github.com/ccromjongh/tydi-lang-2-chisel.git"
TRANSPILER_SRC_NAME="tydi-lang-2-chisel"

ifdef SRC
# Useful variables
.PHONY: all clean json json2chisel

# Each command should start with '> '
.RECIPEPREFIX = >

CC=tydi-lang-complier
BUILD_DIR=build
CONFIG_TOML=project.toml

THIS_DIR=$(dir $(abspath $(lastword $(MAKEFILE_LIST))))
PROJ_NAME=$(shell basename $(SRC))
# LIST=$(filter-out $(BUILD_DIR) Makefile, $(shell ls $(SRC)))

# Default target
all: json json2chisel

clean:
> $(RM) -rf $(BUILD_DIR)

# Compile tydi file to json, output build add the name of this directory
json:
> @mkdir -p $(THIS_DIR)$(BUILD_DIR)
> @cd $(SRC) && $(CC) -c $(CONFIG_TOML) -o $(THIS_DIR)$(BUILD_DIR)/$(PROJ_NAME) > /dev/null

json2chisel: json
> @cp $(THIS_DIR)$(BUILD_DIR)/$(PROJ_NAME)/json_IR.json $(TRANSPILER)/json_output.json
> @cd $(TRANSPILER) && python3.9 $(TRANSPILER).py > /dev/null && rm json_output.json
> @mv $(TRANSPILER)/output.scala $(THIS_DIR)$(BUILD_DIR)/$(PROJ_NAME)/$(PROJ_NAME).scala

else

all clean:
	@for dir in $(shell ls src); do \
		if [ $$dir != "build" ] && [ $$dir != "Makefile" ]; then \
			$(MAKE) SRC="src/$$dir" $@ -s; \
		fi \
	done
	
compile-transpiler:
	@rm -rf $(TRANSPILER)
	@mkdir -p $(TRANSPILER)
	@cd $(TRANSPILER) && git clone $(TRANSPILER_SRC_LINK)
	@cd $(TRANSPILER) && cp -r $(TRANSPILER_SRC_NAME)/templates . && cp $(TRANSPILER_SRC_NAME)/main.py $(TRANSPILER).py
	@rm -rf $(TRANSPILER)/$(TRANSPILER_SRC_NAME)

clean-transpiler:
	@rm -rf $(TRANSPILER)
	
endif