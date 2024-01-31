# Tydi source code examples
The examples in this directory are written in `tydi-lang-2` code.

For a full syntax description of `tydi-lang-2` please refer to the [Tydi-lang syntax documentation](https://github.com/twoentartian/tydi-lang-2/blob/main/tydi-lang2-syntax.md).

# Compile Tydi sources
1. Install the Tydi compiler
```bash
git clone https://github.com/twoentartian/tydi-lang-2.git
cd tydi-lang-2
cargo install --path tydi-lang-complier
```

2. Compile the Tydi sources
```bash
# From the tydi example folder
cd src/
make 
```
This will create a `build` folder with the compiled Tydi to json sources 