# Chiseltest: how `Chisel` and `Tydi-Chisel` code features are represented in `chiseltest`
This [section](../README.md#chiseltest) provides a summary of general considerations about `chiseltest` and its features.

Here, a more detailed analysis of how `Chisel` and `Tydi-Chisel` code features are represented in `chiseltest` is provided.
The document is organized divided per example where each example tries to address a specific feature provided by `Chisel` and `Tydi-Chisel`.

Most of the times, designers need to visually inspect signal values during simulation to understand the current behavior of the design under test. Hence, this analysis focuses on the representation of elements in testing frameworks w.r.t. their representation in source code.`printf`s, `treadle2.VerboseAnnotation` and `IntelliJ` IDEA breakpoint debugging can be used for this purpose.

## Adder: using `Bundle`, `Vec` and `Array` to group signals and instantiate multiple modules 
Chisel provides the `Bundle` and `Vec` classes to group signals together of the different and same type respectively. Their elements can be accessed as named fields for `Bundle` and as indexed elements for `Vec`, similar to software `struct`/`classes` and `array` respectively.

In the chosen example, the `Adder` module uses such classes to group together the signals needed for the internal `carry`, `sum` and `IO` interface.

```scala
// IO interface
val io = IO(new Bundle {
    val A = Input(UInt(n.W))
    val B = Input(UInt(n.W))
    val Cin = Input(UInt(1.W))
    
    val Sum = Output(UInt(n.W))
    val Cout = Output(UInt(1.W))
})
// ...
// Internal carry and sum signals
val carry = Wire(Vec(n + 1, UInt(1.W)))
val sum = Wire(Vec(n, Bool()))
// ... 
```

A simpler carry propagate `Adder` consists of a concatenation of `FullAdder` modules, which are instantiated using an `Array` of `FullAdder` class modules. This allows to write a single line of code to instantiate n-full adders.

```scala
// ...
// Carry propagate adder
val FAs = Array.fill(n)(Module(new FullAdder()))
// ...
```

### Inspecting values by using `printf` debugging
As mentioned in the previous section, `printf` debugging can be used to output custom messages with special representation of signals during simulation. Specifically, in [`AdderTest.scala`](../../examples/chisel-examples/src/chiseltest/AdderTest.scala) I tried to create simple custom messages to print a truth table of IO signals for the `Adder` module together with signals names and types.
Chisel elements have some attribute functions to retrieve some information. For example `name` and `typeName` fields can be used to dynamically access the name and type of signals, therefore if the signal name and/or type changes, the printed message will be automatically updated. While the `peek` function can be used to retrieve the current value of a signal suring simulation.

The following text contains portion of an example output that can be obtained using `printf` debugging. I wrote my own
method to print this truth table and make it suitable for the adder test. This method allows to customize the output format
basing on what the user wants to see. However, it does not represent a ready to use solution since it requires to be written
and updated for each module and each module changes and it can become difficult to read and generate with complex modules. In addition, its implementation highly depends on the designer's coding style, so it does not lead to a standard signal inspection during simulation.

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

Finally, `chiseltest` does not provide a way to inspect the internal signals of the module under test with `peek`/`poke` interface. This can be overcome by writing a `Wrapper` module that extends the actual `DUT` (design under test) to **manually** expose internal signals as output ports with the `chiseltest.experimental.expose`. Also this solution requires to write additional code and to adapt it any time the internal logic changes. Furthermore, the experimental `expose` method allows to expose directly only `Reg`, `Wire` and `Vec`. Namely, signals inside submodules (such as the `Array` of `FullAdder`s) or subbundles require to more code to be exposed. This leads to even more code to be written than expected.

As a workaround, I wrote an object function ([`ExposeBundle`](../../examples/chisel-examples/src/chiseltest/modulewrappers/ExposeBundle.scala)) to expose dynamically all the signals inside a `Bundle`. This allows to dynamically expose all the signals inside an internal `Bundle` without the need to both write additional code and know what is inside the bundle beforehand.
However, an automatic expose/wrap functionality for an entire `Module` would be a great feature to have.
The [`AdderWrapper`](../../examples/chisel-examples/src/chiseltest/modulewrappers/AdderWrapper.scala) module serves an example of this solution. When instantiated in the test, it allows to inspect the internal signals of the `Adder` as if they were IO ports available only during simulation as shown in [`AdderExposeTest`](../../examples/chisel-examples/src/chiseltest/AdderTest.scala)

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

Sometimes, designers also need and want to inspect only a subpart of a circuit or some signals. With `printf` debugging, this requires custom updates of the testbench.

### Inspecting values by using `treadle2.VerboseAnnotation`