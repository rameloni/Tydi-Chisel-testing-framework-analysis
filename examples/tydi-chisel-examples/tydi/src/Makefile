ifdef SRC
# Useful variables
.PHONY: all clean json

# Each command should start with '> '
.RECIPEPREFIX = >

CC=tydi-lang-complier
BUILD_DIR=build
CONFIG_TOML=project.toml

THIS_DIR=$(dir $(abspath $(lastword $(MAKEFILE_LIST))))

# LIST=$(filter-out $(BUILD_DIR) Makefile, $(shell ls $(SRC)))

# Default target
all: json

clean:
> $(RM) -rf $(BUILD_DIR)

# Compile tydi file to json, output build add the name of this directory
json:
> @mkdir -p $(THIS_DIR)$(BUILD_DIR)
> @cd $(SRC) && $(CC) -c $(CONFIG_TOML) -o $(THIS_DIR)$(BUILD_DIR)/$(basename $(SRC))

else

all clean:
	@for dir in $(shell ls); do \
		if [ $$dir != "build" ] && [ $$dir != "Makefile" ]; then \
			$(MAKE) SRC=$$dir $@; \
		fi \
	done

endif