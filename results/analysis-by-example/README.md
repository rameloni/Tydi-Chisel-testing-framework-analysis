<!-- 
## Adder: using `Bundle`/`Vec` and `Array` to group signals and instantiate multiple modules
Chisel provides the `Bundle` and `Vec` classes to group signals together of different and same type respectively. Their elements can be accessed as named fields for `Bundle` and as indexed elements for `Vec`, similar to software `struct`/`classes` and `array` respectively.

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

A simpler carry propagate `Adder` consists of a concatenation of `FullAdder` modules. Those are instantiated using an `Array` of `FullAdder` chisel modules. This allows to write a single line of code to instantiate n-full adders.

```scala
// ...
// Carry propagate adder
val FAs = Array.fill(n)(Module(new FullAdder()))
// ...
```
 -->



<!-- 
A vector in the waveforms appear as a lot of parrallel signals.
```scala
  val carry = Wire(Vec(n + 1, UInt(1.W)))
```
This produces something more similar to a vector in the waveforms.
```scala
  val carryNormal = Wire(UInt((n + 1).W))
```
However, `UInt` are not intended to be used as vectors. So, it is not possible to access individual bits of `carryNormal` in the same way as `carry`. This is a problem when trying to test the circuit.
```scala
  carryNormal(0) := 0.U
```
![Alt text](image.png)
 -->
